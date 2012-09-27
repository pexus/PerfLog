/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/DBWriter.java 
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

/**
 * 
 * @author Pradeep Nambiar 2/10/2012
 */

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.perf.log.properties.LoggerProperties;
import org.perf.log.properties.TunableProperties;
import org.perf.log.utils.StringUtils;


public class DBWriter {
	static String delims = "[-]";
	
	private static Logger logger  = LoggerFactory.getLogger(DBWriter.class.getName());
	
	// Properties used by this class
	private static final String LOGGER_DBWRITER_DB_WRITE_ENABLED = "dynamic.logger.DBWriter.dbWriteEnabled";// dynamic
	static boolean dbWriterEnabled = true;
	static boolean propertiesInited = false;
	private static TunableProperties tunableProperties = LoggerProperties.getInstance().getTunableProperties();
	
	private static String perfDBDS = "jdbc/perfDB";
	private static Context ctx = null;
	private static DataSource ds = null;
	static {
		initProperties();
		try {
			ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(perfDBDS);
		} catch (NamingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private static synchronized void initProperties() {
		if (!propertiesInited) {
			try {
				String propVal;
				Properties props = new Properties();
				
				InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("perfLog.properties");	
		        if(in == null) {
		        	System.out.println(DBWriter.class.getName()+":Error loading perfLog.properties, attempting to load perfLogDefault.properties now.");
		        	in = DBWriter.class.getClassLoader().getResourceAsStream ("perfLogDefault.properties");
		        }				
				if (in != null) {
					props.load(in);
					
					propVal = props
							.getProperty(LOGGER_DBWRITER_DB_WRITE_ENABLED);
					if (propVal != null)
						setDbWriterEnabled(new Boolean(propVal).booleanValue());				
					
				} else {
					System.out.println(DBWriter.class.getName()+":Error in reading perfLog.properties or perfLogDefault.properties");
				}

			} catch (Exception exception) {
				System.out.println(DBWriter.class.getName()+":Error Loading perfLog.properties or perfLogDefault.properties"
								+ exception.getMessage());
			}

			propertiesInited = true;
		}

	}
	
	private static String getTransactionStatus(PerfLogData perfLogData) {
		// convert txnType for storing into DB code
		if (perfLogData.getThrowable() == null) {
			// success code
			return "Success";

		} else {
			// failure code

			return 	"Failure:" + perfLogData.getThrowableClassName() + ":"
							+ perfLogData.getThrowableMessage();
		}

	}
	
	private static String getSourceIdForDB(PerfLogData perfLogData) {
		
		// sourceId (Tokenize the requestId - GUID)
		String guid = perfLogData.getGuid();
		if (guid != null && guid.length()>37) {
			return guid.substring(0,guid.length()-37);
		}
		else
			return null;
	}

	/**
	 * @param perfLogDataJSONObj
	 * @return int value indicating whether the database insert is SUCCESS/FAILURE
	 */
	public static int write(PerfLogData perfLogData) throws Exception
	{
		long inserPerfDBRecordStartTime = System.currentTimeMillis();
		
		Connection connection = null;
		String query = null;
		PreparedStatement preparedStatement = null;
		int statusIndicator = 0 ;
		connection = getConnection(perfDBDS);
		if(connection!=null)
		{
			query = "INSERT INTO PERF_LOG(TXN_DT,TXN_START,TXN_START_MS, HOST_NAME,INSTANCE_NAME,TXN_TYPE,USER_ID,TXN_NAME,SUB_TXN_NAME," +
			"TXN_CLASS,TXN_TIME_MS,INFO_CONTEXT,REQUEST_GUID,REQUEST_SESSION_ID,REQUEST_GUID_SOURCE,HOST_IP,THREAD_NAME,THREAD_ID,TXN_STATUS,JVM_DEPTH,TXN_FILTER_DEPTH)" +
			"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try 
			{
				connection.setAutoCommit(true);
				preparedStatement = connection.prepareStatement(query);
				java.util.Calendar cal = Calendar.getInstance();
				preparedStatement.setDate(1,new java.sql.Date(cal.getTime().getTime()));
				preparedStatement.setTimestamp(2,new java.sql.Timestamp(perfLogData.getTransactionDate().getTime()));
				preparedStatement.setLong(3,perfLogData.getTransactionDate().getTime());
				preparedStatement.setString(4,StringUtils.truncateString(perfLogData.getServerName(),255));
				preparedStatement.setString(5,StringUtils.truncateString(perfLogData.getCloneName(),255));
				preparedStatement.setString(6,StringUtils.truncateString(perfLogData.getTransactionType(),255));
				preparedStatement.setString(7,StringUtils.truncateString(perfLogData.getUserId(),255));
				preparedStatement.setString(8,StringUtils.truncateString(perfLogData.getTransactionName(),255));
				preparedStatement.setString(9,StringUtils.truncateString(perfLogData.getSubTransactionName(),255));
				preparedStatement.setString(10,StringUtils.truncateString(perfLogData.getTransactionClass(),255));
				preparedStatement.setLong(11,perfLogData.getTransactionTime());
				preparedStatement.setString(12,StringUtils.truncateString(perfLogData.getInfoContextString(), 2048));
				preparedStatement.setString(13,StringUtils.truncateString(perfLogData.getGuid(),255));
				preparedStatement.setString(14,StringUtils.truncateString(perfLogData.getSessionId(),255));
				preparedStatement.setString(15,StringUtils.truncateString(getSourceIdForDB(perfLogData),255));
				preparedStatement.setString(16,StringUtils.truncateString(perfLogData.getServerIp(),64));
				preparedStatement.setString(17,StringUtils.truncateString(perfLogData.getThreadName(),255));
				preparedStatement.setString(18,StringUtils.truncateString(perfLogData.getThreadId(),255));
				preparedStatement.setString(19,StringUtils.truncateString(getTransactionStatus(perfLogData),255));
				preparedStatement.setInt(20,perfLogData.getJvmDepth());
				preparedStatement.setInt(21,perfLogData.getTxnFilterDepth());
				
							
				statusIndicator = preparedStatement.executeUpdate();
				
			} catch (SQLException sqlException) 
			{
				logger.error("SQL Exception occured: " + sqlException.getMessage());
				throw sqlException;
				
			} finally {
				try {
					if (preparedStatement != null)
						preparedStatement.close();
					// Close the SQL connection
					if (connection != null) 
						connection.close();		
					} 
				catch (SQLException sqlException) {
					logger.error("SQL Exception occured: "
							+ sqlException.getMessage());
				}
			}
			if (statusIndicator == 1) {
				logger.debug("Succesfully inserted DB record in to the PERF_LOG table");
			} else {
				logger.error("Failed to insert DB record in to the PERF_LOG table");
			}
			logger.debug("insertPerfDBRecord ElapsedTime :"
					+ (System.currentTimeMillis() - inserPerfDBRecordStartTime)
					+ "(ms)");
	
		}
		else {
			logger.error("DB connection == null");
		}
		return statusIndicator;
	}

	

	/**
	 * This method will return the JDBC connection as specified in the JNDI context
	 * @param jndi
	 * @return a instance of java.sql.Connection
	 */
	private static Connection getConnection(String jndi) throws Exception
	{
		Connection con = null;
		if(ctx!=null && ds!=null) {
			try {
				con = ds.getConnection();
			}
			catch ( SQLException sqlException )
			{
				logger.error("SQL Exception occured: "+sqlException.getMessage());
				throw sqlException;
			}
		}
		return con;
	}

	
	public static void setDbWriterEnabled(boolean dbWriterEnabled) {
		DBWriter.dbWriterEnabled = dbWriterEnabled;
	}
	
	/**
	 * @return the dbWriterEnabled
	 */
	public static boolean isDbWriterEnabled() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DBWRITER_DB_WRITE_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else 
			return dbWriterEnabled;
	}
	
	

	

}
