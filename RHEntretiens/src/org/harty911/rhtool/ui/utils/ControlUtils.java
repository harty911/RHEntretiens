package org.harty911.rhtool.ui.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHEnum;

public class ControlUtils {
	
	private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private final static Map<String,Integer> durationMap;
	static{
		durationMap = new LinkedHashMap<String, Integer>();
		durationMap.put( "15'", 15);
		durationMap.put( "30'", 30);
		durationMap.put( "45'", 45);
		durationMap.put( "1h00", 60);
		durationMap.put( "1h30", 90);
		durationMap.put( "2h00", 120);
		durationMap.put( "2h30", 150);
		durationMap.put( "3h00", 180);
	}
	
	//public final VerifyListener LONG_VERIFIER = new LongVerifier(Long.MIN_VALUE,Long.MAX_VALUE);
	
	public static final class LongVerifier implements VerifyListener {
		private final long min;
		private final long max;
		
		public LongVerifier( long min, long max) {
			this.min = min;
			this.max = max;
		}
		
		@Override
		public void verifyText(VerifyEvent e) {
		    String cur = ((Text)e.widget).getText();
		    String txt =  cur.substring(0, e.start) + e.text + cur.substring(e.end);
		    try{  
		        long num = Long.parseLong(txt);  
		        if( num < min || num > max)
		        	e.doit = false;
		    }  
		    catch(NumberFormatException ex){  
		        if(!txt.equals(""))
		            e.doit = false;  
		    }  
		}
	}

	
	public static void setControlDate( Text control, Date date) {
		if( date==null)
			date = new Date();
		control.setText( printDate(date));
	}


	public static void setControlDate( DateTime control, Date date) {
		if( date==null)
			date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		control.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}


	public static Date getControlDate( DateTime control) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, control.getDay());
		cal.set(Calendar.MONTH, control.getMonth());
		cal.set(Calendar.YEAR, control.getYear());
		return cal.getTime();
	}

	
	public static String printDate(Date date) {
		if( date==null) 
			return "";
		return dateFormat.format(date);
	}

	
	public static String printEnum( RHEnum e) {
		if( e==null)
			return "";
		RHToolApp.getModel().refresh(e);
		return e.getText();
			
	}


	public static void addTextAssist(Text text, String[] list) {
		// add decoration 
		ControlDecoration deco = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
		deco.setDescriptionText("Utilisez CTRL + ESPACE pour une assistance à la saisie.");
		deco.setImage( FieldDecorationRegistry.getDefault()
				.getFieldDecoration( FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
		deco.setShowOnlyOnFocus(true);
		
		// create content proposal adapter
		KeyStroke keyStroke = null;
		try {
			keyStroke = KeyStroke.getInstance("Ctrl+Space");
		} catch (ParseException e) {}
		SimpleContentProposalProvider provider = new SimpleContentProposalProvider( list); 
	    provider.setFiltering(true);
	    ContentProposalAdapter adapter = new ContentProposalAdapter( text, 
			    new TextContentAdapter(), provider, keyStroke, new char[]{' '});
	    adapter.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE); 
	}

	

	public static String[] getDurationTextList() {
		return durationMap.keySet().toArray(new String[0]);
	}

	public static Integer parseDuration( String duration) {
		return durationMap.get(duration);
	}
	
	public static String printDuration( int minutes) {
		for( Entry<String, Integer> each : durationMap.entrySet()) {
			if( each.getValue().intValue() >= minutes) {
				return each.getKey();
			}
		}
		return null;
	}

}
