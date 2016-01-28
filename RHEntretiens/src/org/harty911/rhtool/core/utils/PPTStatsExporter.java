package org.harty911.rhtool.core.utils;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.harty911.framework.utils.Chrono;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.RHEMotif;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.User;
import org.harty911.rhtool.ui.utils.ControlUtils;

public class PPTStatsExporter {

	private final RHModel model;
	private File template;
	
	private Date periodStart = null;
	private Date periodEnd = null;
	
	private static final Color COL_BGND_DARK  = new Color(116,165,16);
	private static final Color COL_FORE_DARK  = Color.white;

	private static final Color COL_BGND_LIGHT = new Color(242,242,242);
	private static final Color COL_FORE_LIGHT = Color.black;
	
	public PPTStatsExporter( RHModel rhModel, File template) throws InvalidFormatException, IOException {
		this.model = rhModel;
		this.template = template;
	}

	public void setPeriod(Date start, Date end) {
		periodStart = start;
		periodEnd = end;
	}

	public void export(File file) throws IOException {
		Chrono chrono = new Chrono();
		
		_computeStats();

		String tcomp = chrono.toString();

		// Open
		FileInputStream is = new FileInputStream(template);
		XMLSlideShow ppt = new XMLSlideShow(is);
		is.close();

		XSLFSlideMaster master = ppt.getSlideMasters().get(0);
		XSLFSlideLayout layout = master.getLayout(SlideLayout.TITLE_ONLY);
		
		// Title slide
		XSLFSlide slide = ppt.getSlides().get(0);
		XSLFTextShape txt = slide.getPlaceholder(1);
		XSLFTextParagraph par = txt.addNewTextParagraph();
		par.setTextAlign(TextAlign.RIGHT);
		par.addNewTextRun().setText("Période du "+ControlUtils.printDate(periodStart));
		par = txt.addNewTextParagraph();
		par.setTextAlign(TextAlign.RIGHT);
		par.addNewTextRun().setText("au "+ControlUtils.printDate(periodEnd));
		
		// Global slide
		slide = ppt.createSlide(layout);
		_addTitle(slide, "Collaborateurs rencontrés par motif\n(Global)");
	    
	    _addMotifCount( slide, globalMotifCount);	    
	    		
	    for( User u : userMotifCount.keySet()) {
	    	// create User slide
		    slide = ppt.createSlide(layout);
			_addTitle(slide, "Collaborateurs rencontrés par motif :\n"+u.getNomUsuel());
	    	_addMotifCount( slide, userMotifCount.get(u));	        		
	    }

	    // Save
		FileOutputStream out = new FileOutputStream(file);
		ppt.write(out);
		out.close();  
		ppt.close();

		RHModel.LOGGER.log(Level.INFO, "PPT Stats exported in "+chrono+" ("+tcomp+" computation)");
	}

	protected void _addMotifCount(XSLFSlide slide, HashMap<RHEMotif, Integer> motifCount) {
		Set<RHEMotif> motifs = motifCount.keySet();

		XSLFTable table = slide.createTable();
		table.setAnchor(new Rectangle(100, 150, 1000, 1000));

		XSLFTableRow row = table.addRow();
		_addCell(row, true,"Motif");
		_addCell(row, true,"Nbre");

        for( RHEMotif m : motifs) {
        	row = table.addRow(); 
    		_addCell(row, false, m.getText());
    		_addCell(row, false, motifCount.get(m).toString());
 	    }

	    table.setColumnWidth(0, 450);
	    table.setColumnWidth(1,  80);
	}

	protected XSLFTableCell _addCell(XSLFTableRow row, boolean header, String text) {
		XSLFTableCell cell = row.addCell();
		cell.setFillColor(header ? COL_BGND_DARK : COL_BGND_LIGHT);	
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setBorderBottom(2);
        cell.setBorderBottomColor(Color.white);
        cell.setBorderTop(2);
        cell.setBorderTopColor(Color.white);
        cell.setBorderRight(2);
        cell.setBorderRightColor(Color.white);
        cell.setBorderLeft(2);
        cell.setBorderLeftColor(Color.white);
      
        XSLFTextParagraph par = cell.addNewTextParagraph();
	    
		XSLFTextRun run = par.addNewTextRun();
        run.setFontSize(12.);
      	run.setBold(header);
      	run.setFontColor( header ? COL_FORE_DARK : COL_FORE_LIGHT);
        run.setText(text);
		
        return cell;
	}

	protected void _addTitle(XSLFSlide slide, String text) {
		XSLFTextShape shape = slide.getPlaceholder(0);
		shape.clearText();
        shape.setVerticalAlignment(VerticalAlignment.TOP);
		XSLFTextParagraph par = shape.addNewTextParagraph();
	    XSLFTextRun run = par.addNewTextRun();
        run.setFontSize(18.);
    	run.setBold(true);
	    run.setText(text);
	}


	HashMap<RHEMotif,Integer> globalMotifCount;
	HashMap<User,HashMap<RHEMotif,Integer>> userMotifCount;
	
	private void _computeStats() {
		
		globalMotifCount = new HashMap<>();
		userMotifCount = new HashMap<>();
		
		Set<String> globalGroup = new HashSet<>();
		Set<String> userGroup = new HashSet<>();
		
		for( Talk talk : model.getTalks()) {
			// manage period
			if( periodStart!=null && periodStart.compareTo(talk.getDate()) > 0)
				continue;
			if( periodEnd!=null && periodEnd.compareTo(talk.getDate()) < 0)
				continue;

			User user = talk.getUser1();
			RHEMotif motif = talk.getMotif1();
			
			// count employee+motif1 (unic)
			String key1 = talk.getEmployee()+"#"+motif;
			if( globalGroup.add(key1)) {
				Integer in = globalMotifCount.get(motif);
				int n = (in==null)?0:in.intValue();
				globalMotifCount.put(motif, Integer.valueOf( n + 1));
			}

			// count employee+motif1+user (unic)
			String key2 = talk.getEmployee()+"#"+motif+"#"+user;
			if( userGroup.add(key2)) {
				HashMap<RHEMotif,Integer> motifCount = userMotifCount.get(user);
				if( motifCount==null) {
					motifCount = new HashMap<>();
					userMotifCount.put(user,motifCount);
				}
				Integer in = motifCount.get(motif);
				int n = (in==null)?0:in.intValue();
				motifCount.put(motif, Integer.valueOf( n + 1));
			}
		}		
	}

}

