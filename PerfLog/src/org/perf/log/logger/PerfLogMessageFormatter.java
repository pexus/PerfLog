/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/PerfLogMessageFormatter.java 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
