package org.harty911.rhtool.core.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.harty911.framework.utils.Chrono;
import org.harty911.rhtool.core.model.RHEnum;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.RHModelObject;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.User;

public class XLSExporter <T extends RHModelObject> {

	private final RHModel model;
	private final Class<T> clazz;
	private File xlsTemplate;
	
	private Chrono chrono;
	private Workbook workbook;
	private Sheet sheet;
	private Iterator<Map<String,Object>> rowDataItr = null;
	private List<String> colNames = null;
	
	private int nbExported;
	
	public static interface ExportFilter {
		public static final String OBJECTCOLUMN = "RowObject";
		
		public abstract Collection<Map<String, Object>> modifyBeforeExport(List<String> colNames, List<Map<String, Object>> listData);
	}
	private ExportFilter filter;
	
	
	public XLSExporter( RHModel rhModel, Class<T> clazz, File xlsTemplate) throws InvalidFormatException, IOException {
		this.model = rhModel;
		this.clazz = clazz;
		this.xlsTemplate = xlsTemplate;
	}

	public void exportXLS( File xlsFile) throws Exception {
		try {
			prepareExportXLS();
			while( performImportXLS());
		}
		catch( Exception e) {
			throw e;
		}
		finally {
			finishImportXLS( xlsFile);
		}
	}


	/**
	 * STEP1 : load XLS file and prepare column mapping
	 * @param xlsFile the XLS file to open
	 * @param sheetName the sheet to take as source
	 * @return an estimation of number of elements to import
	 * @throws Exception
	 */
	public int prepareExportXLS() throws Exception {
		
		RHModel.LOGGER.log(Level.INFO,"Prepare to import '"+clazz.getSimpleName()+"'");
		chrono = new Chrono();
		
		sheet = null;
		workbook = WorkbookFactory.create(xlsTemplate);

		sheet = workbook.getSheetAt(0);
		if( sheet==null)
			throw new InvalidFormatException("No Sheet found");
		
		Row rTitle = sheet.getRow(0);
		if( rTitle==null)
			throw new InvalidFormatException("No Title row (1) found");
		Row rFormat = sheet.getRow(1);
		if( rFormat==null)
			throw new InvalidFormatException("No Title row (2) found");
		Row rField = sheet.getRow(2);
		if( rField==null)
			throw new InvalidFormatException("No Field row (3) found");
		
		List<Field> fields = model.getDBFields( clazz);
		List<Field> colFields = new ArrayList<>();
		colNames = new ArrayList<>();

		Cell cTitle, cFormat, cField;
		int n = 0;
		for( n=0; true; n++) {
			cTitle = rTitle.getCell(n);
			cField = rField.getCell(n);
			cFormat = rField.getCell(n);
			if( cTitle==null || cFormat==null || cField==null)
				break;
			
			// search field
			String nField = cField.getStringCellValue();
			Field field = null;
			for( Field each : fields) {
				if( each.getName().equalsIgnoreCase(nField)) {
					field  = each;
					field.setAccessible(true);
					break;
				}
			}
			colFields.add(field);
			colNames.add(nField);
		}
		
		// remove field row
		sheet.removeRow(rField);
		
		// Get objects and fill Data
		List<Map<String,Object>> listData = new LinkedList<>();
		
		List<T> objects = model.getObjects(clazz);
		for( T obj : objects) {
			Map<String,Object> rowData = new LinkedHashMap<>();
			
			rowData.put(ExportFilter.OBJECTCOLUMN, obj);
			
			for( int col=0; col<colNames.size(); col++) {
				Field field = colFields.get(col);
				Object value = (field==null) ? null : field.get(obj);
				rowData.put( colNames.get(col), value);
			}
			listData.add(rowData);
		}
		
		// HOOK: Filter/Modifiy data before export
		Collection<Map<String,Object>> list = listData;
		if( filter!=null) {
			list = filter.modifyBeforeExport( colNames, listData);
		}
	
		rowDataItr = list.iterator();
		nbExported = 0;
		return listData.size();
	}

	

	/**
	 * STEP2 : perform one row import
	 * @return false when the last row is reached
	 * @throws Exception
	 */
	public boolean performImportXLS() throws Exception {
		if( rowDataItr==null || !rowDataItr.hasNext() || sheet == null)
			return false;
		Map<String,Object> rowData = rowDataItr.next();

		// take the current row
		Row row = sheet.getRow(nbExported+1);
		if( rowDataItr.hasNext()) {
			// insert a row after
			copyRowAfter(row);
		}
				
		// fill data
		for( int col=0; col<colNames.size(); col++) {
			setValue( row.getCell(col), rowData.get( colNames.get(col)));
		}

		nbExported++;		
		return rowDataItr.hasNext();
	}

	
	/**
	 * STEP3 : save file
	 * @throws IOException 
	 */
	public void finishImportXLS(File xlsFile) throws IOException {
		RHModel.LOGGER.log(Level.INFO, nbExported +" objects exported in "+chrono);
		rowDataItr = null;
		colNames = null;
		if( sheet != null) {
			try {
				FileOutputStream fileOut = new FileOutputStream(xlsFile);
				workbook.write(fileOut);
				fileOut.close();
			}
			finally { 
				workbook.close();
			}
		}
	}

	private void setValue(Cell cell, Object val) {
		if( cell==null)
			return;
		
		if( val==null)
			cell.setCellValue("");
		else if( val instanceof Date)
			cell.setCellValue((Date)val);
		else if( val instanceof RHEnum) {
			RHEnum e = (RHEnum)val;
			model.refresh(e);
			cell.setCellValue(e.getText());
		}
		else if( val instanceof Employee) {
			Employee e = (Employee)val;
			model.refresh(e);
			cell.setCellValue(e.getNomUsuel());
		}
		else if( val instanceof User) {
			User e = (User)val;
			model.refresh(e);
			cell.setCellValue(e.getNomUsuel());
		}
		else 
			cell.setCellValue(val.toString());
	}

	protected void copyRowAfter(Row row) {
		Row newRow = sheet.createRow( row.getRowNum()+1);
		// Loop through source columns to add to new row
		for (int i = 0; i < row.getLastCellNum(); i++) {
		    // Copy cells
		    Cell oldCell = row.getCell(i);
		    if (oldCell == null)
		        continue;
		    Cell newCell = newRow.createCell(i);

		    // Copy style from old cell and apply to new cell
		    CellStyle newCellStyle = workbook.createCellStyle();
		    newCellStyle.cloneStyleFrom( oldCell.getCellStyle());
		    newCell.setCellStyle(newCellStyle);

		    // Set the cell data type
		    newCell.setCellType(oldCell.getCellType());

		    // Set the cell data value
		    switch (oldCell.getCellType()) {
		        case Cell.CELL_TYPE_BLANK:
		            newCell.setCellValue(oldCell.getStringCellValue());
		            break;
		        case Cell.CELL_TYPE_BOOLEAN:
		            newCell.setCellValue(oldCell.getBooleanCellValue());
		            break;
		        case Cell.CELL_TYPE_ERROR:
		            newCell.setCellErrorValue(oldCell.getErrorCellValue());
		            break;
		        case Cell.CELL_TYPE_FORMULA:
		            newCell.setCellFormula(oldCell.getCellFormula());
		            break;
		        case Cell.CELL_TYPE_NUMERIC:
		            newCell.setCellValue(oldCell.getNumericCellValue());
		            break;
		        case Cell.CELL_TYPE_STRING:
		            newCell.setCellValue(oldCell.getRichStringCellValue());
		            break;
		    }
		}
	}

	public void setFilter( ExportFilter filter) {
		this.filter = filter;		
	}
}

