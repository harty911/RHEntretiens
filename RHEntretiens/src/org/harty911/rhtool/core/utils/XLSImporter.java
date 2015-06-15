package org.harty911.rhtool.core.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.harty911.framework.utils.Chrono;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.RHModelObject;
import org.harty911.rhtool.core.model.objects.RHEContrat;

public class XLSImporter <T extends RHModelObject> {

	private final RHModel model;
	private final Class<T> clazz;

	List<Field> colFields = new ArrayList<>();
	private Chrono chrono;
	private Iterator<Row> rows;
	private Workbook workbook;
	private int nbImported;
	
	public XLSImporter( RHModel rhModel, Class<T> clazz) {
		this.model = rhModel;
		this.clazz = clazz;
	}

	public void importXLS( File xlsFile, String sheetName) throws Exception {
		
		try {
			prepareImportXLS( xlsFile, sheetName);
			
			while( performImportXLS());
		}
		catch( Exception e) {
			throw e;
		}
		finally {
			finishImportXLS();
		}
	}


	public void finishImportXLS() {
		RHModel.LOGGER.log(Level.INFO, nbImported +" objects imported in "+chrono);
		if( workbook != null)
			try{ workbook.close(); } catch (IOException e) {}
	}


	/**
	 * STEP1 : load XLS file and prepare column mapping
	 * @param xlsFile the XLS file to open
	 * @param sheetName the sheet to take as source
	 * @return an estimation of number of elements to import
	 * @throws Exception
	 */
	public int prepareImportXLS( File xlsFile, String sheetName) throws Exception {
		
		RHModel.LOGGER.log(Level.INFO,"Prepare to import '"+clazz.getSimpleName()+"' from "+ xlsFile.getAbsolutePath()+":"+sheetName);
		chrono = new Chrono();
		
		workbook = WorkbookFactory.create(xlsFile);
		
		Sheet sheet = workbook.getSheet(sheetName);
		if( sheet==null)
			throw new Exception("Feuille/onglet '"+sheetName+"' introuvable");
		
		int nbElements = sheet.getPhysicalNumberOfRows();
		
		rows = sheet.rowIterator();
		if( !rows.hasNext())
			throw new Exception("L'onglet '"+sheetName+"' est vide");
		
		// prepare field list
		List<Field> fields = model.getDBFields( clazz);
		colFields.clear();

		boolean first = true;
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		// for each column header, attach a Field
		Row header = rows.next();
		for( Cell cell : header) {
			// default :  null (no Field)
			Field field = null;
			String colTitle = cell.toString().trim();
			for( Field each : fields) {
				if( colFields.contains(each))
					break;
				if( each.getName().equalsIgnoreCase(colTitle)) {
					field = each;
					field.setAccessible(true);
					break;
				}
			}

			colFields.add(field);
			
			// Manage reporting
			if( !first)
				sb.append(", ");
			first = false;
			
			sb.append( colTitle);
			sb.append( " => ");
			if( field!=null)
				sb.append( field.getName());
			else
				sb.append("?");
		}
		
		// report mapping to log
		sb.append( " ]");
		RHModel.LOGGER.log(Level.INFO,"Field mapping = "+sb.toString());
		
		return nbElements;
	}

	

	/**
	 * STEP2 : perform one row import
	 * @return false when the last row is reached
	 * @throws Exception
	 */
	public boolean performImportXLS() throws Exception {
		if( !rows.hasNext())
			return false;
		Row row = rows.next();

		T obj = clazz.newInstance();
		
		Iterator<Cell> cells = row.cellIterator();
		Iterator<Field> fields = colFields.iterator();
		while( cells.hasNext() && fields.hasNext()) {
			Cell cell = cells.next();
			Field field = fields.next();
			// skip unmapped columns
			if( field==null) continue;
			
			// String
			if( field.getType()==String.class) {
				field.set(obj, cell.toString());
			}
			// Date
			else if( field.getType() == Date.class ) {
				if( !DateUtil.isCellDateFormatted(cell))
					throw createCellException( cell, "doit être au format DATE");
				field.set(obj, cell.getDateCellValue());
			}
			// base types
			else if (field.getType() == boolean.class) {
				if( cell.getCellType() != Cell.CELL_TYPE_BOOLEAN)
					throw createCellException( cell, "doit être au format Booleen");
				field.set(obj, cell.getBooleanCellValue());
			} else if (field.getType() == int.class) {
				if( cell.getCellType() != Cell.CELL_TYPE_NUMERIC)
					throw createCellException( cell, "doit être au format Numérique");
				field.setInt( obj, (int)cell.getNumericCellValue());
			} else if (field.getType() == long.class) {
				if( cell.getCellType() != Cell.CELL_TYPE_NUMERIC)
					throw createCellException( cell, "doit être au format Numérique");
				field.setLong( obj, (long)cell.getNumericCellValue());
			} else if (field.getType() == float.class) {
				if( cell.getCellType() != Cell.CELL_TYPE_NUMERIC)
					throw createCellException( cell, "doit être au format Numérique");
				field.setFloat( obj, (float)cell.getNumericCellValue());
			} else if (field.getType() == double.class) {
				if( cell.getCellType() != Cell.CELL_TYPE_NUMERIC)
					throw createCellException( cell, "doit être au format Numérique");
				field.setDouble( obj, cell.getNumericCellValue());			
			} 
			// RHEnum
			else if( field.getType().isAssignableFrom(RHEContrat.class)) {
				RHEContrat e = RHToolApp.getModel().getEnumValue( RHEContrat.class, cell.toString());
				field.set(obj, e);
			}
			// alert for unmanaged types
			else 
				throw new Exception( "Le champ '"+field.getName()+"' ("+field.getType().getSimpleName()+") n'est pas géré par l'import");
		}

		model.save( obj);

		nbImported++;
		
		return rows.hasNext();
	}
	
	

	private Exception createCellException(Cell cell, String msg) {
		CellReference ref = new CellReference(cell);
		return new Exception( "Cellule "+ref.formatAsString()+" : " + msg);
	}
}

