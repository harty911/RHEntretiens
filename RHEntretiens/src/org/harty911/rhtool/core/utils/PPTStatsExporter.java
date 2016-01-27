package org.harty911.rhtool.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.harty911.framework.utils.Chrono;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Talk;

public class PPTStatsExporter {

	private final RHModel model;
	private File template;
	
	private Date periodStart = null;
	private Date periodEnd = null;	
	
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
		
		// Open
		FileInputStream is = new FileInputStream(template);
		XMLSlideShow ppt = new XMLSlideShow(is);
		is.close();

		XSLFSlideMaster master = ppt.getSlideMasters().get(0);
		XSLFSlideLayout layout = master.getLayout(SlideLayout.TITLE_AND_CONTENT);
		
		// create slide
	    XSLFSlide slide1 = ppt.createSlide(layout);
	    XSLFTextShape title1 = slide1.getPlaceholder(0); 
	    title1.setText("Slide 1");
	
		// create slide
	    XSLFSlide slide2 = ppt.createSlide(layout);
	    XSLFTextShape title2 = slide2.getPlaceholder(0); 
	    title2.setText("Slide 2");

	    // Save
		FileOutputStream out = new FileOutputStream(file);
		ppt.write(out);
		out.close();  

		RHModel.LOGGER.log(Level.INFO, "PPT Stats exported in "+chrono);
	}

	
	
	private void _computeStats() {
		
		Set<String> groupKeys = new HashSet<>();
		
		for( Talk talk : model.getTalks()) {
			// manage period
			if( periodStart!=null && periodStart.compareTo(talk.getDate()) > 0)
				continue;
			if( periodEnd!=null && periodEnd.compareTo(talk.getDate()) < 0)
				continue;
				
			// skip similar lines
			String key = talk.getEmployee()+"#"+talk.getMotif1();
			if( ! groupKeys.add(key))
				continue;

			//TODO count what ??
			
			
		}		
	}

}

