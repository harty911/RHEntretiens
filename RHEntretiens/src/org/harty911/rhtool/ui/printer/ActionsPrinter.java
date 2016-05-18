package org.harty911.rhtool.ui.printer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;

import org.harty911.framework.report.TemplatePrinter;
import org.harty911.rhtool.ui.MainWindow;

public class ActionsPrinter extends TemplatePrinter {

	public ActionsPrinter(Writer writer) {
		super(writer);
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
