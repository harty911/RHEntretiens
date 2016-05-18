package org.harty911.rhtool.ui.printer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.harty911.framework.report.TemplatePrinter;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHModel;
import org.harty911.rhtool.core.model.objects.Talk;
import org.harty911.rhtool.core.model.objects.User;
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
	
	private boolean isActionOver;

	public String URI_SEL;
	public String URI_MOD;
	public String ACTION_USER;
	public String ACTION_STATUS;
	public String ACTION_STATUS_CLASS;
	public String ACTION_DATE;
	public String ACTION_DATE_CLASS;
	public String ACTION_TITLE;
	public String ACTION_TEXT;
	
	///////////////////////////////////////////////////////////
	// Methodes du template
	///////////////////////////////////////////////////////////

	public void FOREACH_ACTION() {
		RHModel model = RHToolApp.getModel();
		
		// Build action list
		List<Talk> actions = new LinkedList<>();
		for( Talk talk : RHToolApp.getModel().getTalks()) {
			if( talk.getActionStatus().isOpen()) {
				// Talk with action defined
				actions.add(talk);
			}
		}
		
		// Sort
		final User me = model.getUserContext();
		Collections.sort( actions, new Comparator<Talk>() {
			@Override
			public int compare(Talk t1, Talk t2) {
				// User
				boolean me1 = ( me.equals(t1.getUser1()) || me.equals(t1.getUser2()));
				boolean me2 = ( me.equals(t2.getUser1()) || me.equals(t2.getUser2()));
				if( !me1 && me2)
					return 1;
				if( me1 && !me2)
					return -1;

				if( t1.getActionDate()==null)
					return 1;
				if( t2.getActionDate()==null)
					return -1;
	
				return t1.getActionDate().compareTo(t2.getActionDate());
			}
			
		});

		// Print actions
		for( Talk talk : actions) {
			
			isActionOver = ( talk.getActionStatus().isOpen() && talk.getActionDate()!=null && 
						talk.getActionDate().compareTo(new Date()) < 0 ); 
			
			ACTION_STATUS_CLASS = "open";
			ACTION_STATUS = talk.getActionStatus().toString();
			User u1 = talk.getUser1();
			model.refresh(u1);
			
			ACTION_USER = u1.getPrenom();
			User u2 = talk.getUser2();
			if(u2!=null) {
				model.refresh(u2);
				ACTION_USER += "<br>" + u2.getPrenom();
			}
			
			ACTION_STATUS_CLASS = ( me.equals(talk.getUser1()) || me.equals(talk.getUser2())) ? "me" : "other";

			ACTION_DATE_CLASS = isActionOver ? "late" : "open";
			ACTION_DATE = ControlUtils.printDate(talk.getActionDate());

			ACTION_TITLE = talk.getEmployee().getNomUsuel() + " - " + ControlUtils.printMonth( talk.getDate());		
			ACTION_TEXT = TextFormater.toRichHTML(talk.getActionDetail());		

			URI_SEL = HyperlinkHandler.toURI(HyperlinkHandler.SCHEME_SELECT, talk).toString();
			URI_MOD = HyperlinkHandler.toURI(HyperlinkHandler.SCHEME_EDIT, talk).toString();
			
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
