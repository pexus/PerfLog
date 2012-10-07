/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/FileWriter.java 
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


import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.perf.log.properties.LoggerProperties;
import org.perf.log.properties.TunableProperties;
import org.perf.log.utils.JvmCloneGetterFactory;
import org.perf.log.utils.PropertyFileLoader;

/**
 * This class is used to write the perf log messages to a file
 */
public class FileWriter 
{
	private final static org.perf.log.logger.Logger logger = LoggerFactory.getLogger(FileWriter.class.getName());
	private static Logger perfFileLogger ;
	private static TunableProperties tunableProperties = LoggerProperties.getInstance().getTunableProperties();
	private static FileWriter instance = null;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static String logRootDir=null;
	
	// Properties  for this class
	static boolean propertiesInited = false;
	private static final String LOGGER_FILEWRITER_LOGFILE_ROOT_DIR = "static.logger.FileWriter.logFileRootDir";
	private static final String LOGGER_FILEWRITER_LOGFILE_MAX_SIZE = "static.logger.FileWriter.logFileMaxSize";
	private static final String LOGGER_FILEWRITER_LOGFILE_NUM_TO_KEEP = "static.logger.FileWriter.logFileNumToKeep";
	private static final String LOGGER_FILEWRITER_FILE_WRITE_ENABLED = "dynamic.logger.FileWriter.fileWriteEnabled";// dynamic
	
	static boolean fileWriterEnabled = true;
	static String logFileRootDirFromProperty = null;
	static int logFileMaxSize = 4194304; // 4MB
	static int logFileNumToKeep = 10;
	
	
	/**
	 * 	This method will construct a formatted perf log data string suitable for writing to perf  log file 
	 * @param PerfLogData
	 * @return str - formatted string 
	 */
	private static String getFormatedPerfDataStr(PerfLogData perfLogData) 
	{
		StringBuffer buf = new StringBuffer();
		// This format is consistent with Splunk format
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss:SSS z");
		java.util.Date txnDate = null;
		String formattedTransactionDate = null;
				
		txnDate = new java.util.Date(perfLogData.getTransactionDate().getTime());
		formattedTransactionDate = "["+sdf.format(txnDate)+"]";
		
		
		buf.append(formattedTransactionDate);
		buf.append(" PERFLOG");
		if(perfLogData.getThrowable()!=null) 
		{
			buf.append("(FAILURE=");
			buf.append(perfLogData.getThrowableClassName() + ": " + perfLogData.getThrowableMessage());
			buf.append(")");
		}
		else
			buf.append("(SUCCESS)");
		buf.append(" :txnType="+perfLogData.getTransactionType());
		buf.append(" :txnDate="+txnDate);
		buf.append(" :txnTime="+perfLogData.getTransactionTime());
		buf.append(" :userId="+perfLogData.getUserId());
		buf.append(" :guid="+perfLogData.getGuid());
		buf.append(" :sessionId="+perfLogData.getSessionId());
		buf.append(" :threadName="+perfLogData.getThreadName());
		buf.append(" :threadId="+perfLogData.getThreadId());
		buf.append(" :serverName="+ perfLogData.getServerName());
		buf.append(" :serverIp="+ perfLogData.getServerIp());
		buf.append(" :cloneName="+perfLogData.getCloneName());
		buf.append(" :jvmDepth="+perfLogData.getJvmDepth());
		buf.append(" :txnFilterDepth="+perfLogData.getTxnFilterDepth());
		buf.append(" :txnName="+perfLogData.getTransactionName());
		buf.append(" :subTxnName="+perfLogData.getSubTransactionName());
		buf.append(" :txnClass="+perfLogData.getTransactionClass());
		buf.append(LINE_SEPARATOR);
		buf.append(" :infoCtxStr="+perfLogData.getInfoContextString());
		buf.append(LINE_SEPARATOR);
		buf.append(" :message="+perfLogData.getMessage());
		return buf.toString();
	}


	


	/**
	 * This Private constructor creates a JDK Logger Instance using File Handler  
	 * that rotates for every 4 MB to a new file up to a max of 100 files
	 */
	private FileWriter() throws Exception
	{
		//PerfLogDB Properties
		FileHandler fileHandler = null;
		
		String logFileName="perf-log";
		
		if(perfFileLogger == null) 
		{
			perfFileLogger = Logger.getLogger(FileWriter.class.getName());
			File f  = new File(getLogRootDir());
			if(!f.exists())
			{
				f.mkdirs();
			}
			if(logFileName != null) 
			{
				logFileName = logFileName.trim();
				if(!logFileName.endsWith(".log")) 
				{
					logFileName = logFileName.concat("%g%u.log");
				} 
				else 
				{
					logFileName = logFileName.substring(0,logFileName.indexOf(".log"));
					logFileName = logFileName.concat("%g%u.log");
				}
			}
			logFileName = getLogRootDir() + "/" + logFileName;
			try 
			{
				fileHandler = new FileHandler(logFileName,getLogFileMaxSize(),getLogFileNumToKeep(),true);
				fileHandler.setFormatter(new PerfLogMessageFormatter(new MessageFormat("{0}\n")));
			} catch (IOException ioException)
			{
				logger.error("IO Exception during initializing perf log file:"+ioException.getMessage());
				throw ioException;
			}
			System.out.println(FileWriter.class.getName()+":PerfLog file opened for logging:"+logFileName);
			perfFileLogger.addHandler(fileHandler);
			perfFileLogger.setUseParentHandlers(false);
			perfFileLogger.setLevel(Level.INFO);
		}
	}

	/**
	 * Returns the singleton instance of PerfLogger class
	 * @return
	 */
	public synchronized static FileWriter getInstance() throws Exception
	{
		if(instance == null)
		{
			initProperties();
			try {
				instance = new FileWriter();
			} catch (Exception e) {
				throw e;
			}
		}
		return instance;
	}

	/**
	 * This method internally calls the PerfLogger Instance to put a PERF LOG entry in to the PERFDB-LOG File.
	 * @param messageContent
	 */
	public static void write(PerfLogData perfLogData) throws Exception
	{
		if(perfFileLogger==null)
		{
			getInstance();
		}
		perfFileLogger.log(Level.INFO,getFormatedPerfDataStr(perfLogData));
	}
	
	public static String getLogFileRootDirFromProperty() {
		String propertyValue = tunableProperties.getStaticProperty(LOGGER_FILEWRITER_LOGFILE_ROOT_DIR);
		if(propertyValue != null) {
			logFileRootDirFromProperty = propertyValue;
		}
		return logFileRootDirFromProperty;
	}


	public static String getLogRootDir() {
		// Get the File Root Dir 
		String propValue = getLogFileRootDirFromProperty();
		if(propValue != null) {
			FileWriter.logRootDir = propValue + "/perfLogs";
		}
		else 
			FileWriter.logRootDir = "perfLogs";
		
		// append the JVM Clone name to the directory to split individually 
		// create and log to each JVM clone's instance log file
		String envInstanceName = JvmCloneGetterFactory.getJvmCloneGetterImpl().getName();
		if(envInstanceName!=null)
		{
			FileWriter.logRootDir = FileWriter.logRootDir+"/"+envInstanceName;
		}
		
		return FileWriter.logRootDir;
	}

	public static void setFileWriterEnabled(boolean fileWriterEnabled) {
		FileWriter.fileWriterEnabled = fileWriterEnabled;
	}

	
	public static void setLogFileRootDirFromProperty(String logFileRootDir) {
		FileWriter.logFileRootDirFromProperty = logFileRootDir;
	}


	
	public static void setLogFileMaxSize(int logFileMaxSize) {
		FileWriter.logFileMaxSize = logFileMaxSize;
	}

	
	public static void setLogFileNumToKeep(int logFileNumToKeep) {
		FileWriter.logFileNumToKeep = logFileNumToKeep;
	}


	public static void setInstance(FileWriter instance) {
		FileWriter.instance = instance;
	}
	
	/**
	 * @return the fileWriterEnabled
	 */
	public static boolean isFileWriterEnabled() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_FILEWRITER_FILE_WRITE_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else 
			return fileWriterEnabled;
	}
	
	public static int getLogFileMaxSize() {
		String propertyValue = tunableProperties.getStaticProperty(LOGGER_FILEWRITER_LOGFILE_MAX_SIZE);
		if(propertyValue != null) {
			logFileMaxSize = new Integer(propertyValue).intValue();
		}
		return logFileMaxSize;
	}

	
	public static int getLogFileNumToKeep() {
		String propertyValue = tunableProperties.getStaticProperty(LOGGER_FILEWRITER_LOGFILE_NUM_TO_KEEP);
		if(propertyValue != null) {
			logFileNumToKeep = new Integer(propertyValue).intValue();
		}
		return logFileNumToKeep;
	}


	private static synchronized void initProperties() {
		if (!propertiesInited) {
			try {
				
				String propVal;
				ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
				Properties props = PropertyFileLoader.load(
						"perfLog.properties", 
						"perfLogDefault.properties",
						ctxClassLoader,
						FileWriter.class.getClass().getClassLoader(),
						FileWriter.class.getName());
				if (props != null) {
					propVal = props
							.getProperty(LOGGER_FILEWRITER_FILE_WRITE_ENABLED);
					if (propVal != null)
						setFileWriterEnabled(new Boolean(propVal)
								.booleanValue());				
					propVal = props
						.getProperty(LOGGER_FILEWRITER_LOGFILE_ROOT_DIR);					
					if (propVal != null)
						setLogFileRootDirFromProperty(propVal);
					
					propVal = props
						.getProperty(LOGGER_FILEWRITER_LOGFILE_NUM_TO_KEEP);					
					if (propVal != null)
						setLogFileNumToKeep(new Integer(propVal).intValue());
					
					propVal = props
						.getProperty(LOGGER_FILEWRITER_LOGFILE_MAX_SIZE);					
					if (propVal != null)
						setLogFileMaxSize(new Integer(propVal).intValue());
					
					
				} else {
					System.out.println(FileWriter.class.getName()+":Error in reading perfLog.properties or perfLogDefault.properties");
				}
	
			} catch (Exception exception) {
				System.out.println(FileWriter.class.getName()+":Error Loading perfLog.properties or perfLogDefault.properties"
								+ exception.getMessage());
			}
	
			propertiesInited = true;
		}
	
	}


}
