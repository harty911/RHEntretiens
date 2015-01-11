package org.harty911.rhtool.ui.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;
import org.harty911.rhtool.RHToolApp;
import org.harty911.rhtool.core.model.RHEnum;

public class ControlUtils {
	
	private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

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
		control.setText( dateFormat.format(date));
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

	public static String getEnumText( RHEnum e) {
		if( e==null)
			return "";
		RHToolApp.getModel().refresh(e);
		return e.getText();
			
	}
}
