/**
 * Copyright 2007-2012 Arthur Blake
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*******************************************************************************
 * Modified from original for org.perf.log.* project
 * 
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/sql/SQLInterceptorSpyLogDelegator.java 
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
package org.perf.log.filter.sql;

import java.util.Date;

import org.perf.log.context.PerfLogContext;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;
import org.perf.log.logger.PerfLogData;
import org.perf.log.logger.PerfLogger;
import org.perf.log.logger.PerfLoggerFactory;
import org.perf.log.properties.LoggerProperties;
import org.perf.log.txn.types.PerfLogTxnType;
import org.perf.log.utils.StringUtils;

import net.sf.log4jdbc.Spy;
import net.sf.log4jdbc.SpyLogDelegator;



/**
 * Delegates JDBC spy logging events to the the Simple Logging Facade for Java (slf4j).
 *
 * @author Arthur Blake
 * 
 * Modified for custom class org.perf.log.SQLInterceptorSpyLogDelegator for capturing performance
 * metrics and sending it to perf logger implementation
 * @author Pradeep Nambiar, Pexus LLC
 */
public class SQLInterceptorSpyLogDelegator implements SpyLogDelegator
{
	private final static Logger logger = LoggerFactory.getLogger("SQLInterceptorSpyLogDelegator");
	private final static PerfLogger plogger = PerfLoggerFactory.getLogger();
	
  /**
   * Create a SpyLogDelegator specific to the Simple Logging Facade for Java (slf4j).
   */
  public SQLInterceptorSpyLogDelegator()
  {
  }

  // logs for sql and jdbc

  /**
   * Determine if any of the 5 log4jdbc spy loggers are turned on (jdbc.audit | jdbc.resultset |
   * jdbc.sqlonly | jdbc.sqltiming | jdbc.connection)
   *
   * @return true if any of the 5 spy jdbc/sql loggers are enabled at debug info or error level.
   */
  @Override
public boolean isJdbcLoggingEnabled()
  {
    return true;
  }

  /**
   * Called when a jdbc method throws an Exception.
   *
   * @param spy        the Spy wrapping the class that threw an Exception.
   * @param methodCall a description of the name and call parameters of the method generated the Exception.
   * @param e          the Exception that was thrown.
   * @param sql        optional sql that occured just before the exception occured.
   * @param execTime   optional amount of time that passed before an exception was thrown when sql was being executed.
   *                   caller should pass -1 if not used
   */
  @Override
  public void exceptionOccured(Spy spy, String methodCall, Exception e,
			String sql, long execTime) {
	  	Date transactionStart = new Date(System.currentTimeMillis() - execTime);
		String txnName = "execute";

		// extract the methodCall i.e. execute, executeQuery, executeUpdate
		if (methodCall != null) {
			if (methodCall.startsWith("executeQuery"))
				txnName = "executeQuery";
			else if (methodCall.startsWith("executeUpdate"))
				txnName = "executeUpdate";
			else if (methodCall.startsWith("executeBatch"))
				txnName = "executeBatch";
			// else use default execute

		}

		String classType = spy.getClassType();
		Integer spyNo = spy.getConnectionNumber();

		String errMsg = "";
		if (e != null)
			errMsg = e.getMessage();
		errMsg = errMsg + " : " + spyNo + ". " + classType + "." + methodCall;

		PerfLogContext perfLogContext = PerfLogContextHelper
				.getCurrentThreadPerfLogContextObject();
		if (sql == null) {
			logger.debug(errMsg);

		} else {
			sql = processSql(sql);
			errMsg = errMsg + " FAILED! " + sql + " {FAILED after " + execTime
					+ " msec}";
			logger.debug(errMsg);

		}
		// send a SQL Exception perf log data..

		PerfLogData pData = new PerfLogData(perfLogContext);
		pData.setTransactionDate(transactionStart);

		if (perfLogContext != null) {
			pData.setGuid(perfLogContext.getGuid());
			pData.setSessionId(perfLogContext.getRequestSessionId());
			pData.setThreadName(perfLogContext.getThreadName());
			pData.setCloneName(perfLogContext.getJvmCloneId());
			pData.setServerName(perfLogContext.getHostId());
			pData.setServerIp(perfLogContext.getHostIp());
			pData.setUserId(perfLogContext.getUserId());
			pData.setPerfLogContext(perfLogContext);
			// remove the previous insert of sql string from request data
			// context
			// to conserve space..
			if (sql != null ) {
				 if(LoggerProperties.getInstance().isPerfLogSqlCacheSQLInContext())
					 PerfLogContextHelper.removeLastInsertedFromRequestDataContext();
			}
			// we add this any way to indicate if there was an error... 
			PerfLogContextHelper.addToRequestDataContext("sqltiming", errMsg);
		} else {
			// This cases can occur when the request is not monitored by
			// Guid Filters
			// e.g. in case remote EJB calls or MDB calls etc. which are not
			// monitored
			// currently
			// Create a temporary perflog context to get a guid
			PerfLogContextHelper.startPerfLogTxnMonitor();
			PerfLogContextHelper.pushInfoContext("sqlInfo",
					"sql from non-instrumented up stream request");
			perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			pData.setGuid(perfLogContext.getGuid());
			pData.setSessionId(perfLogContext.getRequestSessionId());
			pData.setThreadName(perfLogContext.getThreadName());
			pData.setCloneName(perfLogContext.getJvmCloneId());
			pData.setServerName(perfLogContext.getHostId());
			pData.setServerIp(perfLogContext.getHostIp());
			pData.setUserId(perfLogContext.getUserId());
			pData.setPerfLogContext(null);// don't set it since we will be
			// deleting this
			PerfLogContextHelper.endPerfLogTxnMonitor();

		}

		pData.setTransactionType(PerfLogTxnType.SQL_QUERY);
		pData.setTransactionName(txnName);
		pData.setTransactionTime(execTime);
		pData.setMessage(errMsg);
		pData.setThrowable(e);

		// Set the sub transaction name for SQL to be : hashcode # max 80
		// chars of SQL string
		// hashcode for two exact sql with same argument values will be same
		// 

		if (sql != null)
			pData.setSubTransactionName(Integer.toHexString(sql.hashCode())
					+ " # " + StringUtils.truncateString(sql, 80));

		if (LoggerProperties.getInstance().isPerfLogSqlEnabled()
				&& (execTime >= LoggerProperties.getInstance()
						.getPerfLogSqlThreshold()) && (plogger != null)) {
			plogger.log(pData);
		} // if log enabled and exec time exceeds threshold
	}
  /**
   * Called when a JDBC method from a Connection, Statement, PreparedStatement,
   * CallableStatement or ResultSet returns.
   *
   * @param spy        the Spy wrapping the class that called the method that
   *                   returned.
   * @param methodCall a description of the name and call parameters of the
   *                   method that returned.
   * @param returnMsg  return value converted to a String for integral types, or
   *                   String representation for Object.  Return types this will
   *                   be null for void return types.
   */
  @Override
public void methodReturned(Spy spy, String methodCall, String returnMsg) 
  {
	// THIS IS TOO EXPENSIVE DO NOT ENABLE....
	/**
	  	String classType = spy.getClassType();

		String header = spy.getConnectionNumber() + ". " + classType + "."
				+ methodCall + " returned " + returnMsg;

		logger.debug(header);
	**/

	}

  /**
   * Called when a spied upon object is constructed.
   *
   * @param spy              the Spy wrapping the class that called the method that returned.
   * @param constructionInfo information about the object construction
   */
  @Override
public void constructorReturned(Spy spy, String constructionInfo)
  {
    // not used in this implementation -- yet
  }

  /**
   * Special call that is called only for JDBC method calls that contain SQL.
   *
   * @param spy        the Spy wrapping the class where the SQL occured.
   * @param methodCall a description of the name and call parameters of the method that generated the SQL.
   * @param sql        sql that occured.
   */
  @Override
public void sqlOccured(Spy spy, String methodCall, String sql)
  {
	    String sqlString = processSql(sql);
        logger.debug(sqlString);
        if(LoggerProperties.getInstance().isPerfLogSqlCacheSQLInContext())
        	PerfLogContextHelper.addToRequestDataContext("sql", sqlString);
   
  }

  /**
   * Break an SQL statement up into multiple lines in an attempt to make it
   * more readable
   *
   * @param sql SQL to break up.
   * @return SQL broken up into multiple lines
   */
  private String processSql(String sql) {
		if (sql == null) {
			return null;
		} else
			return sql.trim();

	}

  /**
   * Special call that is called only for JDBC method calls that contain SQL.
   *
   * @param spy        the Spy wrapping the class where the SQL occurred.
   *
   * @param execTime   how long it took the SQL to run, in milliseconds.
   *
   * @param methodCall a description of the name and call parameters of the
   *                   method that generated the SQL.
   *
   * @param sql        SQL that occurred.
   */
  @Override
  public void sqlTimingOccured(Spy spy, long execTime, String methodCall,
			String sql) {

			Date transactionStart = new Date(System.currentTimeMillis() - execTime); ; 
			
			String sqlTiming = buildSqlTimingDump(spy, execTime, methodCall, sql, false);
			logger.debug(sqlTiming);
			
			String txnName = "execute";

			// extract the methodCall i.e. execute, executeQuery, executeUpdate
			if (methodCall != null) {
				if (methodCall.startsWith("executeQuery"))
					txnName = "executeQuery";
				else if (methodCall.startsWith("executeUpdate"))
					txnName = "executeUpdate";
				else if (methodCall.startsWith("executeBatch"))
					txnName = "executeBatch";
				// else use default execute
			}

			PerfLogContext perfLogContext = PerfLogContextHelper
					.getCurrentThreadPerfLogContextObject();
			PerfLogData pData = new PerfLogData(perfLogContext);
			pData.setTransactionDate(transactionStart);
			if (perfLogContext != null) {
				pData.setGuid(perfLogContext.getGuid());
				pData.setSessionId(perfLogContext.getRequestSessionId());
				pData.setThreadName(perfLogContext.getThreadName());
				pData.setCloneName(perfLogContext.getJvmCloneId());
				pData.setServerName(perfLogContext.getHostId());
				pData.setServerIp(perfLogContext.getHostIp());
				pData.setUserId(perfLogContext.getUserId());
				pData.setPerfLogContext(perfLogContext);
				// remove the previous insert of sql string from request data
				// context to conserve space.. and add this SQL with timing
				// if the appropriate flags are set
				 if(LoggerProperties.getInstance().isPerfLogSqlCacheSQLInContext()) {
					 PerfLogContextHelper.removeLastInsertedFromRequestDataContext();
					 if(LoggerProperties.getInstance().getPerfLogSqlCacheSQLInContextThreshold() >= execTime)
						 PerfLogContextHelper.addToRequestDataContext("sqltiming", sqlTiming);
				 }
			} else {
				// This cases can occur when the request is not monitored by
				// Guid Filters
				// e.g. in case remote EJB calls or MDB calls etc. which are not
				// monitored
				// currently
				// We will create a temporary perflog context to track the query via guid
				PerfLogContextHelper.startPerfLogTxnMonitor();
				PerfLogContextHelper.pushInfoContext("sqlInfo",
						"sql from non-instrumented up stream request");
				perfLogContext = PerfLogContextHelper
						.getCurrentThreadPerfLogContextObject();
				pData.setGuid(perfLogContext.getGuid());
				pData.setSessionId(null);
				pData.setThreadName(perfLogContext.getThreadName());
				pData.setCloneName(perfLogContext.getJvmCloneId());
				pData.setServerName(perfLogContext.getHostId());
				pData.setServerIp(perfLogContext.getHostIp());
				pData.setUserId(null);
				pData.setPerfLogContext(null);
				PerfLogContextHelper.endPerfLogTxnMonitor();

			}

			pData.setMessage(sqlTiming);
			pData.setTransactionName(txnName);
			pData.setTransactionType(PerfLogTxnType.SQL_QUERY);
			pData.setTransactionTime(execTime);

			// Set the sub transaction name for SQL to be : hashcode # max 80
			// chars of SQL string
			// hashcode for two exact sql with same argument values will be same
			// 

			if (sql != null)
				pData.setSubTransactionName(Integer.toHexString(sql.hashCode())
						+ " # " + StringUtils.truncateString(sql, 80));
			pData.setThrowable(null);

			if (LoggerProperties.getInstance().isPerfLogSqlEnabled()
					&& (execTime >= LoggerProperties.getInstance()
							.getPerfLogSqlThreshold())
					&& (plogger!=null)
				) {
				plogger.log(pData);
			}

	}

  /**
   * Log a Setup and/or administrative log message for log4jdbc.
   *
   * @param msg message to log.
   */
  @Override
public void debug(String msg)
  {
   logger.debug( msg);
  }

  /**
   * Called whenever a new connection spy is created.
   *
   * @param spy ConnectionSpy that was created.
   */
  @Override
public void connectionOpened(Spy spy)
  {
      logger.debug(spy.getConnectionNumber() + ". Connection opened");
    
  }

  /**
   * Called whenever a connection spy is closed.
   *
   * @param spy ConnectionSpy that was closed.
   */
  @Override
public void connectionClosed(Spy spy)
  {
    
      logger.debug(spy.getConnectionNumber() + ". Connection closed");
    
  }

/**
   * Helper method to quickly build a SQL timing dump output String for
   * logging.
   *
   * @param spy        the Spy wrapping the class where the SQL occurred.
   *
   * @param execTime   how long it took the SQL to run, in milliseconds.
   *
   * @param methodCall a description of the name and call parameters of the
   *                   method that generated the SQL.
   *
   * @param sql        SQL that occurred.
   *
   * @param debugInfo  if true, include debug info at the front of the output.
   *
   * @return a SQL timing dump String for logging.
   */
  private String buildSqlTimingDump(Spy spy, long execTime, String methodCall,
    String sql, boolean debugInfo)
  {
    StringBuffer out = new StringBuffer();

		out.append("[dbcon=");
    	out.append(spy.getConnectionNumber());
		out.append("] ");

		// NOTE: if both sql dump and sql timing dump are on, the processSql
		// algorithm will run TWICE once at the beginning and once at the end
		// this is not very efficient but usually
		// only one or the other dump should be on and not both.

		sql = processSql(sql);

		out.append(sql);
		out.append(" {sqlExecutionTime=");
		out.append(execTime);
		out.append(" msec}");

		return out.toString();
  }
}
