/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/app/logger/DefaultSampleAppLoggerImpl.java 
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
package org.perf.log.app.logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.*;

import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.logger.FileWriter;
import org.perf.log.logger.PerfLogMessageFormatter;

public class AppLoggerImpl implements org.perf.log.app.logger.Logger {

	// Create a java.util.logggin.Logger
	// This can be used to leverage J2EE container log level control e.g. in
	// WebSphere
	private static java.util.logging.Logger JDKLogger = null;
	private static boolean propertiesInited = false;
	private static String logDestination = "console";
	private static String logFileRootDir = null;
	private static int logFileNumToKeep = 2;
	private static String logFileName = "appLogFile";
	private static int logFileMaxSize = 2097152; // 2MB
	private static String logFileInitialLevel = "info";
	private static final String PROP_LOG_DESTINATION = "logDestination";
	private static final String PROP_LOGFILE_ROOT_DIR = "logFileRootDir";
	private static final String PROP_LOGFILE_MAX_SIZE = "logFileMaxSize";
	private static final String PROP_LOGFILE_NUM_TO_KEEP = "logFileNumToKeep";
	private static final String PROP_LOGFILE_NAME = "logFileName";
	private static final String PROP_LOGFILE_INITIAL_LEVEL = "logFileInitialLevel";
	private static boolean jdkLoggerInited = false;

	private Level getInitialJDKLoggerLevel() {
		// valid initial level values : warn trace info error debug off
		String levelStr = getLogFileInitialLevel();
		if (levelStr.equalsIgnoreCase("info"))
			return Level.INFO;
		else if (levelStr.equalsIgnoreCase("warn"))
			return Level.WARNING;
		else if (levelStr.equalsIgnoreCase("debug"))
			return Level.FINEST;
		else if (levelStr.equalsIgnoreCase("trace"))
			return Level.FINER;
		else if (levelStr.equalsIgnoreCase("error"))
			return Level.SEVERE;
		else if (levelStr.equalsIgnoreCase("off"))
			return Level.OFF;
		else
			return Level.INFO;

	}
	
   	

	private ConsoleHandler getConsoleHandler() {
		ConsoleHandler consoleHandler = null;

		consoleHandler = new java.util.logging.ConsoleHandler();
		consoleHandler.setFormatter(new AppLogMessageFormatter(
				new MessageFormat("{0}\n")));
		return consoleHandler;

	}

	private FileHandler getFileHandler() {
		// file handler...//PerfLogDB Properties
		FileHandler fileHandler = null;
		String logFileNname = getLogFileName();

		File f = new File(getLogFileRootDir());
		if (!f.exists()) {
			f.mkdirs();
		}
		if (getLogFileName() != null) {
			logFileNname = logFileNname.trim();
			if (!logFileNname.endsWith(".log")) {
				logFileNname = logFileNname.concat("%g%u.log");
			} else {
				logFileNname = logFileNname.substring(0,
						logFileNname.indexOf(".log"));
				logFileNname = logFileNname.concat("%g%u.log");
			}
		}
		logFileNname = getLogFileRootDir() + "/" + logFileNname;
		try {
			fileHandler = new FileHandler(logFileNname, getLogFileMaxSize(),
					getLogFileNumToKeep(), true);
			fileHandler.setFormatter(new PerfLogMessageFormatter(
					new MessageFormat("{0}\n")));
		} catch (IOException ioException) {
			System.out
					.println("IO Exception during initializing app log file, will use console handler"
							+ ioException.getMessage());
			return null;
		}
		return fileHandler;

	}

	private synchronized void initJDKLogger() {
		if (!jdkLoggerInited) {
			initProperties();
			if (JDKLogger == null) {
				JDKLogger = java.util.logging.Logger.getLogger(this.getClass()
						.getName());
				// This is a sample implementation. Get the initial level from
				// values initialized from properties file
				// If running in WebSphere Environment, the levels can also be
				// changed
				// from WAS console if required
				// Navigate to Troubleshooting -> Logs and Traces -> <server>
				// and
				// Change Log Level Details
				// and look for org.perf.log.logger.LoggerImpl

				JDKLogger.setLevel(getInitialJDKLoggerLevel());

				if (getLogDestination().equalsIgnoreCase("console")) {

					ConsoleHandler consoleHandler = getConsoleHandler();
					JDKLogger.addHandler(consoleHandler);
				} else {
					
					FileHandler fileHandler = getFileHandler();
					if (fileHandler == null) {
						ConsoleHandler consoleHandler = getConsoleHandler();
						JDKLogger.addHandler(consoleHandler);
					} else {
						JDKLogger.addHandler(fileHandler);
					}
				}
			}
			JDKLogger.setUseParentHandlers(false);
			jdkLoggerInited = true;
		}

	}

	public AppLoggerImpl() {
		super();
		initJDKLogger();
	}

	public AppLoggerImpl(String loggerName) {
		super();
		initJDKLogger();
		this.loggerName = loggerName;
	}

	String loggerName;

	@Override
	public void debug(String msg) {
		log(Level.FINEST, "DEBUG", msg, null);

	}

	@Override
	public void debug(String msg, Throwable t) {

		log(Level.FINEST, "DEBUG", msg, t);

	}

	@Override
	public void error(String msg) {

		log(Level.SEVERE, "ERROR", msg, null);
	}

	@Override
	public void error(String msg, Throwable t) {

		log(Level.SEVERE, "ERROR", msg, t);

	}

	@Override
	public void info(String msg) {

		log(Level.INFO, "INFO", msg, null);

	}

	@Override
	public void info(String msg, Throwable t) {

		log(Level.INFO, "INFO", msg, t);

	}

	@Override
	public void trace(String msg) {

		log(Level.FINE, "TRACE", msg, null);

	}

	@Override
	public void trace(String msg, Throwable t) {

		log(Level.FINE, "TRACE", msg, t);

	}

	@Override
	public void warn(String msg) {

		log(Level.WARNING, "WARNING", msg, null);

	}

	@Override
	public void warn(String msg, Throwable t) {

		log(Level.WARNING, "WARNING", msg, t);

	}

	/**
	 * This is our internal implementation for logging regular
	 * (non-parameterized) log messages.
	 * 
	 * @param level
	 * @param message
	 * @param t
	 */
	private void log(Level jdkLoggerLevel, String appLoggerLevelStr, String message, Throwable t) {

		StringBuffer buf = new StringBuffer();

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss:SSS z");

		String formattedTransactionDate = null;
		formattedTransactionDate = "[" + sdf.format(new java.util.Date()) + "]";

		buf.append(formattedTransactionDate);
		buf.append(" ");
		buf.append(appLoggerLevelStr);
		buf.append(" ");
		buf.append(loggerName);
		buf.append(" - ");

		buf.append(PerfLogContextHelper.getBaseAndInfoContextString());
		buf.append(message);

		if (JDKLogger.isLoggable(jdkLoggerLevel)) {
			if (t != null) {
				JDKLogger.log(jdkLoggerLevel, buf.toString(), t);
			} else
				JDKLogger.log(jdkLoggerLevel, buf.toString());
		} else {
			// Cache the trace data in the debug context
			// This would be useful for diagnosis when the context data
			// is dumped when there is error or response time exceeds a defined
			// threshold
			PerfLogContextHelper.addToDebugContext("trace", buf.toString());
		}
		// Dump the context data when there is an error
		if (JDKLogger.isLoggable(jdkLoggerLevel) && jdkLoggerLevel == Level.SEVERE) {
			PerfLogContextHelper.dumpPerfLogContext(JDKLogger);
		}

	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {

		this.loggerName = loggerName;
	}

	public void setLevel(Level level) {

		if (JDKLogger != null)
			JDKLogger.setLevel(level);
	}

	private static void initProperties() {
		if (!propertiesInited) {
			try {
				InputStream in = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("perfLogAppLogger.properties");
					
		        if(in == null) {
		        	System.out.println(AppLoggerImpl.class.getClass().getCanonicalName()+":Error loading perfLogAppLogger.properties using context class loader, attempting to load default perfLogAppLogger.properties now.");
		        	in = AppLoggerImpl.class.getClass().getClassLoader().getResourceAsStream("perfLogAppLogger.properties");
		        }
				String propVal;
				Properties props = new Properties();
				if (in != null) {
					props.load(in);

					propVal = props.getProperty(PROP_LOG_DESTINATION);
					if (propVal != null)
						setLogDestination(propVal);

					propVal = props.getProperty(PROP_LOGFILE_ROOT_DIR);
					if (propVal != null)
						setLogFileRootDir(propVal);

					propVal = props.getProperty(PROP_LOGFILE_NAME);
					if (propVal != null)
						setLogFileName(propVal);

					propVal = props.getProperty(PROP_LOGFILE_NUM_TO_KEEP);
					if (propVal != null)
						setLogFileNumToKeep(new Integer(propVal).intValue());

					propVal = props.getProperty(PROP_LOGFILE_MAX_SIZE);
					if (propVal != null)
						setLogFileMaxSize(new Integer(propVal).intValue());

					propVal = props.getProperty(PROP_LOGFILE_INITIAL_LEVEL);
					if (propVal != null)
						setLogFileInitialLevel(propVal);

				} else {
					System.out.println(FileWriter.class.getName()
							+ ":Error in reading perfLogAppLogger.properties");
				}

			} catch (Exception exception) {
				System.out.println(FileWriter.class.getName()
						+ ":Error Loading perfLogAppLogger.properties"
						+ exception.getMessage());
			}

			propertiesInited = true;
		}

	}

	public static String getLogFileRootDir() {
		return logFileRootDir;
	}

	public static void setLogFileRootDir(String inLogFileRootDir) {
		logFileRootDir = inLogFileRootDir;
	}

	public static int getLogFileNumToKeep() {
		return logFileNumToKeep;
	}

	public static void setLogFileNumToKeep(int inLogFileNumToKeep) {
		logFileNumToKeep = inLogFileNumToKeep;
	}

	public static String getLogFileName() {
		return logFileName;
	}

	public static void setLogFileName(String inLogFileName) {
		logFileName = inLogFileName;
	}

	public static int getLogFileMaxSize() {
		return logFileMaxSize;
	}

	public static void setLogFileMaxSize(int inLogFileMaxSize) {
		logFileMaxSize = inLogFileMaxSize;
	}

	public static String getLogFileInitialLevel() {
		return logFileInitialLevel;
	}

	public static void setLogFileInitialLevel(String logFileInitialLevel) {
		AppLoggerImpl.logFileInitialLevel = logFileInitialLevel;
	}

	public static String getLogDestination() {
		return logDestination;
	}

	public static void setLogDestination(String logDestination) {
		AppLoggerImpl.logDestination = logDestination;
	}

}
