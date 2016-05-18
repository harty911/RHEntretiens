package org.harty911.rhtool.ui.printer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.harty911.framework.report.TemplatePrinter;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.utils.TextFormater;
import org.harty911.rhtool.ui.MainWindow;
import org.harty911.rhtool.ui.actions.HyperlinkHandler;
import org.harty911.rhtool.ui.utils.ControlUtils;

public class ActionsPrinter extends TemplatePrinter {

	public ActionsPrinter(Writer writer) {
		super(writer);
	}
	
	///////////////////////////////////////////////////////////
	// Variables du template
	///////////////////////////////////////////////////////////
	
	private boolean isActionOpen;
	private boolean isActionOver;

	public String URI;
	public String ACTION_STATUS;
	public String ACTION_STATUS_CLASS;
	public String ACTION_DATE;
	public String ACTION_DATE_CLASS;
	public String ACTION_TEXT;
	
	///////////////////////////////////////////////////////////
	// Methodes du template
	///////////////////////////////////////////////////////////

	public void FOREACH_ACTION() {
		// Build action list
		List<Talk> actions = new LinkedList<>();
		for( Talk talk : RHToolApp.getModel().getTalks()) {
			if( !talk.getActionStatus().isNothing()) {
				// Talk with action defined
				actions.add(talk);
			}
		}
		
		// Print actions
		for( Talk talk : actions) {
			
			isActionOpen = talk.getActionStatus().isOpen();
			isActionOver = ( isActionOpen && talk.getActionDate()!=null && 
						talk.getActionDate().compareTo(new Date()) < 0 ); 
			
			ACTION_STATUS_CLASS = isActionOpen ? "open" : "close";
			ACTION_STATUS = talk.getActionStatus().toString();
			
			ACTION_DATE_CLASS = isActionOpen ? ( isActionOver ? "late" : "open" ) : "close";
			ACTION_DATE = ControlUtils.printDate(talk.getActionDate());

			ACTION_TEXT = TextFormater.toRichHTML(talk.getActionDetail());		

			URI = HyperlinkHandler.toURI(HyperlinkHandler.SCHEME_SELECT, talk).toString();
			
			printTemplate();
		}
	}

	
	public static String toHTML() {
		StringWriter strWriter = new StringWriter();
		
		try {
			ActionsPrinter printer = new ActionsPrinter( strWriter);
			printer.readTemplate( new FileInputStream("templates/Actions.html"));
			printer.printTemplate();
			printer.close();
		} catch (IOException e) {
			MainWindow.LOGGER.log(Level.SEVERE, "Unable to render Actions list", e);
		}		
		
		return strWriter.toString();
	}

}
