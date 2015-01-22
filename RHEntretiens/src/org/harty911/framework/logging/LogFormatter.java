package org.harty911.framework.logging;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

        Date dt = new Date(record.getMillis());
        sb.append(DateFormat.getInstance().format(dt))
            .append(" [")
            .append(record.getLevel().getName())
            .append("] ");
        sb.append(record.getSourceClassName().replaceFirst(".+\\.", ""))
	            .append("#")
	            .append(record.getSourceMethodName())
	            .append(": ");
    	sb.append(formatMessage(record))
            .append(LINE_SEPARATOR);

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                // ignore
            }
        }

        return sb.toString();
    }
}