/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/PerfLogData.java 
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.perf.log.context.PerfLogContext;
import org.perf.log.context.PerfLogContextConstants;
import org.perf.log.txn.types.PerfLogTxnClass;



public class PerfLogData implements Externalizable
{
	public PerfLogData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PerfLogData(PerfLogContext perfLogContext) {
		super();
		
		// fill in perfLogData with common elements from perfLogContext
		if(perfLogContext!=null) {
			setUserId(perfLogContext.getUserId());
			setCloneName(perfLogContext.getJvmCloneId());
			setGuid(perfLogContext.getGuid());
			setSessionId(perfLogContext.getRequestSessionId());
			setThreadName(perfLogContext.getThreadName());
			setPerfLogContext(perfLogContext);
			setServerIp(perfLogContext.getHostIp());
			setServerName(perfLogContext.getHostId());	
			setJvmDepth(perfLogContext.getJvmDepth());
			setTxnFilterDepth(perfLogContext.getTxnFilterDepth());
			setThreadId(perfLogContext.getThreadId());
			setInfoContextString(perfLogContext.getInfoContextString());
		}
		else		
			setPerfLogContext(null);
		
		
	}
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private final static Logger logger = LoggerFactory.getLogger("PerfLogData");
	String guid = null;
	String sessionId = null;
	String threadName = null;
	String threadId = null;
	Date transactionDate = null;
	String serverName = null;
	String serverIp = null;
	String cloneName = null;
	int jvmDepth = 0;
	int txnFilterDepth = 0;	
	String transactionType = null;
	String userId = null;
	String transactionName = null;
	String subTransactionName = null;
	String transactionClass= null;
	long transactionTime = 0;
	String message = null;
	String infoContextString = null;
	String throwableClassName = null;
	String throwableMessage = null;
	PerfLogContext perfLogContext = null;
	Throwable throwable=null; // set if there was an exception otherwise null.
	public String getGuid() 
	{
		return guid;
	}
	public void setGuid(String guid) 
	{
		this.guid = guid;
	}
	public Date getTransactionDate() 
	{
		return transactionDate;
	}
	
	public void setTransactionDate(Date transactionDate) 
	{
		this.transactionDate = transactionDate;
	}
	
	public String getServerName() 
	{
		return serverName;
	}
	public void setServerName(String serverName) 
	{
		this.serverName = serverName;
	}
	public String getCloneName()
	{
		return cloneName;
	}
	public void setCloneName(String cloneName) 
	{
		this.cloneName = cloneName;
	}
	public String getTransactionType() 
	{
		return transactionType;
	}
	public void setTransactionType(String transactionType) 
	{
		this.transactionType = transactionType;
	}
	public String getUserId() 
	{
		return userId;
	}
	public void setUserId(String userId) 
	{
		this.userId = userId;
	}
	public String getTransactionName() 
	{
		return transactionName;
	}
	public void setTransactionName(String transactionName)
	{
		this.transactionName = transactionName;
	}
	public String getSubTransactionName() 
	{
		return subTransactionName;
	}
	public void setSubTransactionName(String subTransactionName) 
	{
		this.subTransactionName = subTransactionName;
	}
	public long getTransactionTime() 
	{
		return transactionTime;
	}
	public void setTransactionTime(long transactionTime) 
	{
		this.transactionTime = transactionTime;
	}
	public String getMessage() 
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public void setPerfLogContext(PerfLogContext perfLogContext) 
	{
		this.perfLogContext = perfLogContext;
		if(perfLogContext != null) {
			// extract the infoContext String plus additional details not available
			// in the log data that would be useful
			setInfoContextString(perfLogContext.getInfoContextString());
		}
	}
	public Throwable getThrowable() 
	{
		return throwable;
	}
	public void setThrowable(Throwable throwable) 
	{
		this.throwable = throwable;
		if(throwable != null) {
			setThrowableClassName(throwable.getClass().getCanonicalName());
			setThrowableMessage(throwable.getMessage());
		}
	}
	public String getSessionId() 
	{
		return sessionId;
	}
	public void setSessionId(String sessionId) 
	{
		this.sessionId = sessionId;
	}
	private String readStr(ObjectInput in) 
	{
		// read length
		try 
		{
			int len = in.readInt();
			if(len == 0)
				return null;
			else
				return (String)in.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void writeStr(String outStr, ObjectOutput out ) 
	{
		try 
		{
			if(outStr!=null) 
			{
				out.writeInt(outStr.length());
				out.writeObject(outStr);
			}
			else 
			{
				out.writeInt(0);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException 
	{
		setTransactionTime(in.readLong());
		setGuid(readStr(in));
		setSessionId(readStr(in));	
		setThreadName(readStr(in));
		setThreadId(readStr(in));
		setTransactionDate(new Date(in.readLong()));		
		setServerName(readStr(in));
		setServerIp(readStr(in));
		setCloneName(readStr(in));
		setJvmDepth(in.readInt());
		setTxnFilterDepth(in.readInt());
		setTransactionType(readStr(in));
		setUserId(readStr(in));
		setTransactionName(readStr(in));
		setSubTransactionName(readStr(in));
		setTransactionClass(readStr(in));
		setInfoContextString(readStr(in));
		setMessage(readStr(in));
		setThrowableClassName(readStr(in));
		setThrowableMessage(readStr(in));		
		// add any required PerfLogContext data

	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException 
	{
		// write out the desired fields
		out.writeLong(getTransactionTime());		
		writeStr(getGuid(),out);		
		writeStr(getSessionId(),out);
		writeStr(getThreadName(),out);
		writeStr(getThreadId(),out);
		if(getTransactionDate() != null)
			out.writeLong(getTransactionDate().getTime());
		else
			out.writeLong(new Date().getTime());
		
		writeStr(getServerName(),out);
		writeStr(getServerIp(),out);
		writeStr(getCloneName(),out);
		out.writeInt(getJvmDepth());
		out.writeInt(getTxnFilterDepth());
		writeStr(getTransactionType(),out);
		writeStr(getUserId(),out);
		writeStr(getTransactionName(),out);
		writeStr(getSubTransactionName(),out);
		writeStr(getTransactionClass(),out);
		writeStr(getInfoContextString(),out);
		writeStr(getMessage(),out);
		writeStr(getThrowableClassName(),out);
		writeStr(getThrowableMessage(),out);
	}
	
	@SuppressWarnings("unchecked")
	public String toJSON() {
			
			JSONObject obj = new JSONObject();
			obj.put("txnDate", new Long(getTransactionDate().getTime()));
			obj.put("txnTime", new Long(getTransactionTime()));			
			obj.put("guid", getGuid());
			obj.put("sid", getSessionId());
			obj.put("threadName", getThreadName());
			obj.put("threadId",getThreadId());
			obj.put("cloneName", getCloneName());
			obj.put("jvmDepth", new Integer(getJvmDepth()));
			obj.put("txnFilterDepth",new Integer(getTxnFilterDepth()));
			obj.put("serverName",getServerName());
			obj.put("serverIp", getServerIp());
			obj.put("userid",getUserId());
			obj.put("txnName",getTransactionName());
			obj.put("subTxnName", getSubTransactionName());
			obj.put("txnClass",getTransactionClass());
			obj.put("txnType",getTransactionType());
			obj.put("infoCtxStr",getInfoContextString());
			obj.put("message",getMessage());
			obj.put("throwableClass",getThrowableClassName());
			obj.put("throwableMessage",getThrowableMessage());
			return obj.toJSONString();
				
		}
	/**
	 * @return the infoContextString
	 */
	public String getInfoContextString() 
	{
		return infoContextString;
	}

	/**
	 * @param infoContextString the infoContextString to set
	 */
	protected void setInfoContextString(String infoContextString) 
	{
		this.infoContextString = infoContextString;
	}

	/**
	 * @return the throwableClassName
	 */
	public String getThrowableClassName() 
	{
		return throwableClassName;
	}

	/**
	 * @param throwableClassName the throwableClassName to set
	 */
	public void setThrowableClassName(String throwableClassName) 
	{
		this.throwableClassName = throwableClassName;
	}

	/**
	 * @return the throwableMessage
	 */
	public String getThrowableMessage() 
	{
		return throwableMessage;
	}

	/**
	 * @param throwableMessage the throwableMessage to set
	 */
	public void setThrowableMessage(String throwableMessage) 
	{
		this.throwableMessage = throwableMessage;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// A short version of perf data suitable for writing to 
		// log file when exceptions happen
		StringBuffer buf = new StringBuffer();
	    
	    buf.append("PERFLOG");
	    if(getThrowable()!=null) {
	    	buf.append("(FAILURE=");
	    	buf.append(getThrowable().getMessage());
	    	buf.append(")");
	    }
	    else
	    	buf.append("(SUCCESS)");
	   
	    buf.append(" :txnType=");
	    buf.append(getTransactionType());
	    buf.append(" :txnTime=");
	    buf.append(getTransactionTime());
	    buf.append(" msec");
	    buf.append(LINE_SEPARATOR);
	   	return buf.toString();
	}
	
	// convert perf log data suitable for writing to console or file
	public String getFullFormatedPerfDataStr() {
		
		StringBuffer buf = new StringBuffer();
		 buf.append("PERFLOG");
		 if(getThrowable()!=null) {
		    	buf.append("(FAILURE=");
		    	buf.append(getThrowableClassName() + ": " + getThrowableMessage());
		    	buf.append(")");
		    }
		    else
		    	buf.append("(SUCCESS)");
		 buf.append(" :txnType="+getTransactionType());
		 buf.append(" :txnDate="+getTransactionDate());
		 buf.append(" :txnTime="+getTransactionTime());
		 buf.append(" :userid="+getUserId());
		 buf.append(" :guid="+getGuid());
		 buf.append(" :sessionid="+getSessionId());
		 buf.append(" :threadName="+getThreadName());
		 buf.append(" :threadId="+getThreadId());
		 buf.append(" :serverName="+getServerName());
		 buf.append(" :serverIp="+getServerIp());
		 buf.append(" :cloneName="+getCloneName());
		 buf.append(" :jvmDepth="+getJvmDepth());
		 buf.append(" :txnFilterDepth="+getTxnFilterDepth());
		 buf.append(" :txnName="+getTransactionName());
		 buf.append(" :subTxnName="+getSubTransactionName());
		 buf.append(" :txnClass="+getTransactionClass());
		 buf.append(LINE_SEPARATOR);
		 buf.append(" :infoCtxStr="+getInfoContextString());
		 buf.append(LINE_SEPARATOR);
		 buf.append(" :message="+getMessage());

		return buf.toString();
		
	}
	/**
	 * @return the serverIp
	 */
	public  String getServerIp() {
		return serverIp;
	}
	/**
	 * @param serverIp the serverIp to set
	 */
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	
	// This method takes a JSON message string creates a PerfLogData object
	public static PerfLogData fromJSON(String messageContent) {
		PerfLogData perfLogData = new PerfLogData();
		JSONObject perfLogDataJSONObj = null;
		// deserialize the JSON Object
		JSONParser parser = new JSONParser();

		try {
			if (messageContent != null) {
				perfLogDataJSONObj = (JSONObject) parser.parse(messageContent);

				// transactionStart
				if (perfLogDataJSONObj.get("txnDate") != null) {
					java.util.Date timeStamp = new java.util.Date(
							((Long) perfLogDataJSONObj.get("txnDate"))
									.longValue());
					perfLogData.setTransactionDate(new java.sql.Timestamp(
							timeStamp.getTime()));
				} else
					perfLogData.setTransactionDate(null);

				// GUID
				if (perfLogDataJSONObj.get("guid") != null) {
					perfLogData.setGuid(perfLogDataJSONObj.get("guid")
							.toString());
				} else
					perfLogData.setGuid(null);

				// SID (Session ID)
				if (perfLogDataJSONObj.get("sid") != null) {
					perfLogData.setSessionId(perfLogDataJSONObj.get("sid")
							.toString());
				} else
					perfLogData.setSessionId(null);
				
				// THREAD_NAME
				if (perfLogDataJSONObj.get("threadName") != null) {
					perfLogData.setThreadName(perfLogDataJSONObj.get("threadName")
							.toString());
				} else
					perfLogData.setThreadName(null);
				
				// THREAD_ID
				if (perfLogDataJSONObj.get("threadId") != null) {
					perfLogData.setThreadId(perfLogDataJSONObj.get("threadId")
							.toString());
				} else
					perfLogData.setThreadId(null);
				

				// cloneName (InstanceName)
				if (perfLogDataJSONObj.get("cloneName") != null) {
					perfLogData.setCloneName(perfLogDataJSONObj
							.get("cloneName").toString());
				} else
					perfLogData.setCloneName(null);
				
				// jvmDepth
				if (perfLogDataJSONObj.get("jvmDepth") != null) {
					perfLogData.setJvmDepth(((Integer)(perfLogDataJSONObj
							.get("jvmDepth"))).intValue());
				} else
					perfLogData.setJvmDepth(0);
				
				// txnFilterDepth
				if (perfLogDataJSONObj.get("txnFilterDepth") != null) {
					perfLogData.setTxnFilterDepth(((Integer)(perfLogDataJSONObj
							.get("txnFilterDepth"))).intValue());
				} else
					perfLogData.setTxnFilterDepth(0);
				
				
				

				// serverName
				if (perfLogDataJSONObj.get("serverName") != null) {
					perfLogData.setServerName(perfLogDataJSONObj.get(
							"serverName").toString());
				} else
					perfLogData.setServerName(null);

				// serverIp
				if (perfLogDataJSONObj.get("serverIp") != null) {
					perfLogData.setServerIp(perfLogDataJSONObj.get("serverIp")
							.toString());
				} else
					perfLogData.setServerIp(null);

				// userId
				if (perfLogDataJSONObj.get("userid") != null) {
					perfLogData.setUserId(perfLogDataJSONObj.get("userid")
							.toString());
				} else
					perfLogData.setUserId(null);

				// pageId
				if (perfLogDataJSONObj.get("txnName") != null) {
					perfLogData.setTransactionName(perfLogDataJSONObj.get(
							"txnName").toString());
				} else
					perfLogData.setTransactionName(null);

				// action
				if (perfLogDataJSONObj.get("subTxnName") != null) {
					perfLogData.setSubTransactionName(perfLogDataJSONObj.get(
							"subTxnName").toString());
				} else
					perfLogData.setSubTransactionName(null);
				
				// txnClass
				if (perfLogDataJSONObj.get("txnClass") != null) {
					perfLogData.setTransactionClass(perfLogDataJSONObj.get(
							"txnClass").toString());
				} else
					perfLogData.setTransactionClass(null);

				// transactionTimeInMilliSeconds
				if (perfLogDataJSONObj.get("txnTime") != null) {
					perfLogData.setTransactionTime(((Long) perfLogDataJSONObj
							.get("txnTime")).longValue());
				} else
					perfLogData.setTransactionTime(0);

				// get throwableMessage and throwableClass
				String throwableMessage = (String) perfLogDataJSONObj
						.get("throwableMessage");
				perfLogData.setThrowableMessage(throwableMessage);
				perfLogData.setThrowableClassName((String) perfLogDataJSONObj
						.get("throwableClass"));
				// Transaction Type
				perfLogData.setTransactionType((String) perfLogDataJSONObj
						.get("txnType"));
				// InfoContext String
				perfLogData.setInfoContextString((String) perfLogDataJSONObj
						.get("infoCtxStr"));

				// Message String
				perfLogData.setMessage((String) perfLogDataJSONObj.get(
						"message"));

			}
		} catch (ParseException parseException) {
			logger.error("JSON Parser Exception" + parseException.getMessage());
			// parseException.printStackTrace();
		
		}
		return perfLogData;
	}
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public String getTransactionClass() {
		if(transactionClass != null)
			return transactionClass;
		else 
			return PerfLogTxnClass.getTxnClassFromTransactionType(this);
	}
	public void setTransactionClass(String transactionClass) {
		this.transactionClass = transactionClass;
	}
	public String getThreadId() {
		return threadId;
	}
	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public int getJvmDepth() {
		return jvmDepth;
	}
	public void setJvmDepth(int jvmDepth) {
		this.jvmDepth = jvmDepth;
	}
	public int getTxnFilterDepth() {
		return txnFilterDepth;
	}
	public void setTxnFilterDepth(int txnFilterDepth) {
		this.txnFilterDepth = txnFilterDepth;
	}
		
}
