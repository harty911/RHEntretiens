package org.harty911.framework.logging;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {

	public static void setup() {
		// get the global logger to configure it
	    Logger logger = Logger.getLogger(""); //Logger.GLOBAL_LOGGER_NAME);

	    // suppress the logging output to the console
	    /*
	    Logger rootLogger = Logger.getLogger("");
	    Handler[] handlers = rootLogger.getHandlers();
	    if (handlers[0] instanceof ConsoleHandler) {
	      rootLogger.removeHandler(handlers[0]);
	    }
	    */
	    
	    logger.setLevel(Level.INFO);
	    
		
	    try {
	    	// create a custom formatter
		    Formatter formatter = new LogFormatter();
		    
		    // replace Console formatter
		    Handler[] handlers = logger.getHandlers(); 
		    if( handlers.length > 0 && handlers[0] instanceof ConsoleHandler ) {
		    	handlers[0].setFormatter(formatter);
		    }

		    // add log file
		    FileHandler fileTxt = new FileHandler("RHtool_%g.log",40960,5);
		    fileTxt.setFormatter(formatter);
		    logger.addHandler(fileTxt);
		    
	    } catch (IOException e) {
			e.printStackTrace();
		}

	}

}
