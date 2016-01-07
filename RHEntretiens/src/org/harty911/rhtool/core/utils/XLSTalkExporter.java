package org.harty911.rhtool.core.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.harty911.framework.utils.Chrono;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Talk;

public class XLSTalkExporter {

	private final RHModel model;

	private Chrono chrono;
	private Workbook workbook;
	private int nbExported;
	private Iterator<Talk> talks;
	private Sheet sheet;

	private Map<String,Boolean> columns = new LinkedHashMap<>();

	private CellStyle styleDate;
	
	public XLSTalkExporter( RHModel rhModel) {
		this.model = rhModel;
		
		columns.put("Date", true);
		columns.put("Collaborateur", true);
	}

	public void exportXLS( File xlsFile, String sheetName) throws IOException {
		try {
			prepareExportXLS( sheetName);
			while( performExportXLS());
		}
		finally {
			finishExportXLS( xlsFile);
		}
	}


	/**
	 * STEP1 : prepare XLS
	 * @param sheetName the sheet to take as source
	 * @return an estimation of number of elements to export
	 */
	public int prepareExportXLS( String sheetName) {
		sheetName = WorkbookUtil.createSafeSheetName(sheetName);
		   
		RHModel.LOGGER.log(Level.INFO,"Prepare to export 'Talk' to sheet :"+sheetName);
		chrono = new Chrono();
				
		workbook = new HSSFWorkbook();
		sheet = workbook.createSheet(sheetName);
		
		Row row = sheet.createRow(0);
		int x=0;
		for( Entry<String, Boolean> col : columns.entrySet()) {
			if( col.getValue())
				row.createCell(x++).setCellValue(col.getKey());
		}
		
		List<Talk> lst = model.getTalks();
		talks = lst.iterator();
 
		CreationHelper createHelper = workbook.getCreationHelper();		    
		styleDate = workbook.createCellStyle();
	    styleDate.setDataFormat( createHelper.createDataFormat().getFormat("d/m/yy"));
	    
		return lst.size();
	}

	

	/**
	 * STEP2 : perform one row export
	 * @return false when the last row is reached
	 */
	public boolean performExportXLS() {
		if( !talks.hasNext())
			return false;
		Talk obj = talks.next();
		nbExported++;
		
		Row row = sheet.createRow(nbExported);
		int x=0;
		
		// Date
		Cell cell = row.createCell(x++);
		cell.setCellStyle(styleDate);
		cell.setCellValue( obj.getDate());

		// Collab
		cell = row.createCell(x++);
		cell.setCellValue( obj.getEmployee().getNomUsuel());
		
		
		return talks.hasNext();
	}
	

	/**
	 * STEP3 : save and close
	 * @param file
	 * @throws IOException
	 */
	public void finishExportXLS( File file) throws IOException {
		RHModel.LOGGER.log(Level.INFO, nbExported +" objects exported in "+chrono);
		if( workbook == null) 
			return;

    	FileOutputStream fileOut = new FileOutputStream(file);
    	workbook.write(fileOut);
    	workbook.close(); 
    	fileOut.close();
	}


}

