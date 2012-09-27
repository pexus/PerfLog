/**
 ** 
 */
package org.perf.log.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This class is used to format the entries in the perf log that can be retrieved using splunk 
 * or log scrapper tools  
 * All the log entries will be prefixed with the formatted TimeStamp as follows: [MM/dd/yy HH:mm:ss:SSS z] 
 * For e.g; [06/04/12 12:49:29:613 CDT] PERFLOG(SUCCESS) ......
 */
public class PerfLogMessageFormatter extends Formatter
{
	private MessageFormat messageFormat = new MessageFormat("{0}\n");

	@Override
	public String format(LogRecord record) 
	{
		Object[] arguments = new Object[1];
		
		if(record.getThrown() != null) 
		{
			arguments[0] = record.getMessage()+"\n"+getStackTrace(record.getThrown());
		} else 
		{
			arguments[0] = record.getMessage();
		}
		return messageFormat.format(arguments);
	}

	public PerfLogMessageFormatter(MessageFormat mf) 
	{
		super();
		messageFormat = mf;
	}
	public static String getStackTrace(Throwable t) 
	{
		StringWriter stringWritter = new StringWriter();
		PrintWriter printWritter = new PrintWriter(stringWritter, true);
		t.printStackTrace(printWritter);
		printWritter.flush();
		stringWritter.flush();
		return stringWritter.toString();
	}

}
