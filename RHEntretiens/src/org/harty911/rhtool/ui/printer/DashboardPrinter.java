package org.harty911.rhtool.ui.printer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;

import org.harty911.framework.report.TemplatePrinter;
import org.harty911.rhtool.ui.MainWindow;

public class DashboardPrinter extends TemplatePrinter {

	public DashboardPrinter(Writer writer) {
		super(writer);
	}
	
	public static String toHTML() {
		StringWriter strWriter = new StringWriter();
		
		try {
			DashboardPrinter printer = new DashboardPrinter( strWriter);
			printer.readTemplate( new FileInputStream("templates/Dashboard.html"));
			printer.printTemplate();
			printer.close();
		} catch (IOException e) {
			MainWindow.LOGGER.log(Level.SEVERE, "Unable to render Dashboard", e);
		}		
		
		return strWriter.toString();
	}

}
