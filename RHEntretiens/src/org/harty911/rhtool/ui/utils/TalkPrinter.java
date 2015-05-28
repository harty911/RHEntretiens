package org.harty911.rhtool.ui.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.harty911.framework.report.TemplatePrinter;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Employee;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.User;

public class TalkPrinter extends TemplatePrinter {
	
	/**
	 * Generate HTML page from a Talk object
	 * @param talk
	 * @throws IOException
	 */
	public static String toHTML( Talk talk) throws IOException {
		StringWriter strWriter = new StringWriter();
		TalkPrinter printer = new TalkPrinter( talk, strWriter);
		printer.readTemplate( new FileInputStream("templates/Talk.html"));
		printer.printTemplate();
		printer.close();
		return strWriter.toString();
	}
	
	
	public Talk talk;
	
	public TalkPrinter( Talk talk, Writer writer) throws FileNotFoundException {
		super(writer);
		this.talk = talk;
		
		initVariables();
	}
	
	///////////////////////////////////////////////////////////
	// Variables du template
	///////////////////////////////////////////////////////////
	
	public String TITLE;
	public String COLLAB;
	public String RHS;
	public String DATE;
	public String DUREE;
	
	private void initVariables() {
		RHModel model = RHToolApp.getModel();
		
		model.refresh(talk);
		TITLE = "Entretien de Carrière";
		DATE = ControlUtils.printDate( talk.getDate());
		DUREE = ControlUtils.printDuration( talk.getDuration());
		
		Employee emp = talk.getEmployee();
		model.refresh(emp);
		COLLAB = emp.getNomUsuel() + " ("+emp.getMatricule()+")";
		
		User u1 = talk.getUser1();
		model.refresh(u1);
		RHS = u1.getNomUsuel();
		User u2 = talk.getUser2();
		if(u2!=null) {
			model.refresh(u2);
			RHS += ", " + u2.getNomUsuel();
		}
	}


	

}
