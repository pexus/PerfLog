/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/context/PerfLogContextHelper.java 
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
package org.perf.log.context;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.perf.log.logger.*;
import org.perf.log.properties.PerfLogContextProperties;
import org.perf.log.properties.LoggerProperties;
import org.perf.log.properties.TxnThresholdProps;
import org.perf.log.txn.types.PerfLogTxnType;
import org.perf.log.utils.RuntimeEnvHelper;


/**
 * This class enables creating and managing log context data that can be accessed
 * by any logger. Log context data is created and deleted from filters such as Portlet Filter, Servlet Filter
 * Struts Interceptors, Web Service handlers and SQL handlers etc. They are used to track requests with 
 * a JVM and across JVMs. The contextual data can be used by tools such as Splunk to correlate log outputs
 * for diagnosis
 * 
 * @author Pradeep Nambiar 2/10/2012
 */
public class PerfLogContextHelper {
	
	private final static Logger logger = LoggerFactory.getLogger("PerfLogContextHelper");
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	//private static final String LINE_SEPARATOR = "\n";
	
	// GUID Hash map.. This hash contains a unique ID for each request thread
	// key consists of - ThreadID
	// value consists of a uuid
	// Portlet Filter, Servlet Filter, JAX-RPC Handlers, JAX-WS Handlers
	// is used to create (add) an entry on request entry into the J2EE container
	// and also to remove from this hash map

	private static ConcurrentHashMap<String, PerfLogContext> 
		guidHashMap = new ConcurrentHashMap<String, PerfLogContext>();
	
	
	// create with a given GUID, jvmDepth and filterDepth
	// and initialized the common context e.g. host, clone id, thread id.
	// this is called when request crosses JVM boundaries and we need to keep
	// GUID same to track log data using tools such as Splunk
	//
	// Note: Both the arguments to this method could be null
       
	private static void _startPerfLogTxnMonitor(PerfLogContextTrackingData perfLogContextTrackingData, TxnData txnData) {

		String currentThreadID = new Long(Thread.currentThread().getId())
				.toString();
		boolean logStartPerfLogTxn = false;

		PerfLogContext thisThreadPerfLogContext = new PerfLogContext();
		
		// Create the first TxnFilter
		PerfLogContext.incrementTxnFilterDepth(thisThreadPerfLogContext, txnData);
		
		// Check if we have to create a new Perf Log Context or use data from a previous context
		if (perfLogContextTrackingData == null || 
				(perfLogContextTrackingData != null && perfLogContextTrackingData.getGuid()==null)) {
			// create a guid for this new context
			thisThreadPerfLogContext.setGuid(RuntimeEnvHelper.getInstance().getCloneName()
					+ "-" + UUID.randomUUID().toString());
			thisThreadPerfLogContext.setJvmDepth(1);
			// When creating a new context, set the context creation time 
			// and filter creation time for the first filter to be the same
			thisThreadPerfLogContext.setContextCreationTime(System.currentTimeMillis());
			thisThreadPerfLogContext.setTxnFilterCreationTime(1, thisThreadPerfLogContext.getContextCreationTime());
			// We only log the startPerfLogTxn when creating the guid context
			// If the guid is passed from another JVM then the startPerfLogTxn has already been created
			if(LoggerProperties.getInstance().isLogStartEndPerfLogTxnEnabled())
				logStartPerfLogTxn=true;
			
			
		} else {
			// use supplied guid and jvmdepth and createTime for Perf Log context
			thisThreadPerfLogContext.setGuid(perfLogContextTrackingData.getGuid());
			// increement the calling JVM depth by 1
			thisThreadPerfLogContext.setJvmDepth(perfLogContextTrackingData.getCallingJvmDepth()+1);
			// Context creation time is the time of the original perf log context creation time
			thisThreadPerfLogContext.setContextCreationTime(perfLogContextTrackingData.getCreateTimeInMillis());
			// filter creation time for the first filter in this JVM is set to the current time
			thisThreadPerfLogContext.setTxnFilterCreationTime(1, System.currentTimeMillis());
		}
		//initialize filter depth push count for stacks
		thisThreadPerfLogContext.getInfoContextPushCountForTxnFilterDepth().add(0, new Integer(0));
		thisThreadPerfLogContext.setThreadId(currentThreadID);
		thisThreadPerfLogContext.setJvmStatsAtContextCreation(getJVMStats());
		thisThreadPerfLogContext.setHostId(RuntimeEnvHelper.getInstance().getHostName());
		thisThreadPerfLogContext.setHostIp(RuntimeEnvHelper.getInstance().getIpAddress());
		thisThreadPerfLogContext.setJvmCloneId(RuntimeEnvHelper.getInstance()
				.getCloneName());
		
		thisThreadPerfLogContext.setThreadName("["+Thread.currentThread().getName()+"]");
		// Before putting the new one, see if there is an existing one that
		// was not cleaned out.. due to some hung thread or
		if (guidHashMap.get(currentThreadID) != null) {
			logger.warn("_startPerfLogTxnMonitor: Found zombie PerfLog Context data that will be deleted for thread ID:"
							+ currentThreadID
							+ ":"
							+ "context data is: "
							+ getBaseAndInfoContextString());
			_endPerfLogTxnMonitor(true);
		}
		// put the new one.
		guidHashMap.put(currentThreadID, thisThreadPerfLogContext);
		
		if (logStartPerfLogTxn) {
			// -----------------------------------------------
			// Log start of transaction data.. in the first JVM if enabled
			// This transaction indicates the creation of the GUID context 
			// and the start of the performance log sequence tied to a single
			// request GUID
			PerfLogData perfLogData = new PerfLogData(thisThreadPerfLogContext);
			// set transaction type, other fields will be defaulted based on
			// current thread context.
			perfLogData.setTransactionType(PerfLogTxnType.START_PERF_LOG_TRANSACTION);
			perfLogData.setTransactionName("startPerfLogTxnMonitor");
			perfLogData.setSubTransactionName("perfLogTxnMarker");
			perfLogData.setTransactionTime(0);
			//Set the Transaction date to the same time as the context creation date time
			perfLogData.setTransactionDate(new Date(thisThreadPerfLogContext.getContextCreationTime()));
			logPerfLogData(perfLogData);
			// -------------------------------------------------
		}
	}

	public static boolean startPerfLogTxnMonitor(PerfLogContextTrackingData perfLogContextTrackingData) {
		if (PerfLogContextHelper.getCurrentThreadPerfLogContextObject()!= null) {
			_endPerfLogTxnMonitor(false);
		}
		_startPerfLogTxnMonitor(perfLogContextTrackingData, null);
		return true;
	}
	
	public static boolean startPerfLogTxnMonitor(PerfLogContextTrackingData perfLogContextTrackingData, TxnData txnData) {
		if (PerfLogContextHelper.getCurrentThreadPerfLogContextObject()!= null) {
			_endPerfLogTxnMonitor(false);
		}
		_startPerfLogTxnMonitor(perfLogContextTrackingData, txnData);
		return true;
	}
	
	public static boolean startPerfLogTxnMonitor() {
		boolean contextCreated = false;
		PerfLogContext perfLogContext = getCurrentThreadPerfLogContextObject();
		if (perfLogContext == null) {
			contextCreated = true;
			_startPerfLogTxnMonitor(null, null);
		} else {
			// increment filter depth
			PerfLogContext.incrementTxnFilterDepth(perfLogContext,null);
		}
		return contextCreated;
	}
	public static boolean startPerfLogTxnMonitor(TxnData txnData) {
		boolean contextCreated = false;
		PerfLogContext perfLogContext = getCurrentThreadPerfLogContextObject();
		if (perfLogContext == null) {
			contextCreated = true;
			_startPerfLogTxnMonitor(null, txnData);
		} else {
			// increment filter depth
			PerfLogContext.incrementTxnFilterDepth(perfLogContext,txnData);
		}
		return contextCreated;
	}
	
	public static void dumpPerfLogContext(java.util.logging.Logger inLogger) {
		// use provided logger to dump the PerfLog context data
		_dumpPerfLogContext(inLogger);
		
	}
	public static void dumpPerfLogContext() {
		// use provided default project logger to dump PerfLog context data
		_dumpPerfLogContext(null);
		
	}
	
	public static String getJVMStats() {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		List <GarbageCollectorMXBean> garbageCollectorMXBeansList = ManagementFactory.getGarbageCollectorMXBeans();
		
		StringBuffer buf = new StringBuffer();
		buf.append(LINE_SEPARATOR + " threadCount = " + threadMXBean.getThreadCount());
		buf.append(LINE_SEPARATOR + " heapMemUsage = " + memoryMXBean.getHeapMemoryUsage());
		buf.append(LINE_SEPARATOR + " nonHeapMemUsage = " + memoryMXBean.getNonHeapMemoryUsage());
		for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeansList) {
			buf.append(LINE_SEPARATOR + " GCName = " + garbageCollectorMXBean.getName() 
					+ " GCCount = " + garbageCollectorMXBean.getCollectionCount()
					+ " GCTime (ms) = " + garbageCollectorMXBean.getCollectionTime()); 
			
		}
		return buf.toString();
	}
	
	private static void _dumpPerfLogContext(java.util.logging.Logger inLogger) {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		
		
		if (thisThreadPerfLogContext != null) {
			String debugContextString = getDebugContextString();
			String requestDataContextString = getRequestDataContextString();
			long currentTime = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss:SSS z");								
			Date contextCreationDateWithMilliSecondPrecision = new Date(thisThreadPerfLogContext.getContextCreationTime());
			String formattedContextCreationDate = sdf.format(contextCreationDateWithMilliSecondPrecision);
			Date thisJVMEntryDateWithMilliSecondPrecision = new Date(thisThreadPerfLogContext.getTxnFilterCreationTime(1));
			String formattedThisJVMEntryDateWithMilliSecondPrecision = sdf.format(thisJVMEntryDateWithMilliSecondPrecision);
			
			long elapsedTimeSinceContextCreation = currentTime	- thisThreadPerfLogContext.getContextCreationTime();
			long elapsedTimeSinceJMVEntry = currentTime - thisThreadPerfLogContext.getTxnFilterCreationTime(1);
			StringBuffer buf = new StringBuffer();
			
			buf.append(": Full Request PerfLog Context Data" + LINE_SEPARATOR);
			buf.append("======= Request PerfLog Context Data Begin ===================");
			buf.append(LINE_SEPARATOR + "context creation time = " + formattedContextCreationDate);
			buf.append(LINE_SEPARATOR + "elapsedtime since context creation (ms) = " + elapsedTimeSinceContextCreation);
			buf.append(LINE_SEPARATOR + "this JVM entry time = " + formattedThisJVMEntryDateWithMilliSecondPrecision);
			buf.append(LINE_SEPARATOR + "elapsedtime since this JVM entry = " + elapsedTimeSinceJMVEntry );
			buf.append(LINE_SEPARATOR + "responseTimeThreshold (ms) = ");
				buf.append(TxnThresholdProps.getMaxResponseTimeThresholdForTxnList(
							thisThreadPerfLogContext.getTxnList()));
			buf.append(LINE_SEPARATOR + "txnList = "+thisThreadPerfLogContext.getTxnList().toString());
			buf.append(LINE_SEPARATOR + "currentDebugContextDataSize / Max Size = ");
			buf.append(thisThreadPerfLogContext.getCurrentDebugDataContextSize());
			buf.append(" / " + thisThreadPerfLogContext.getMaxDebugContexDataSize());
			buf.append(LINE_SEPARATOR + "currentRequestDataSize / Max Size = ");
			buf.append(thisThreadPerfLogContext.getCurrentRequestDataContextSize());
			buf.append(" / " + thisThreadPerfLogContext.getMaxRequestDataContextSize());
			buf.append(LINE_SEPARATOR + "JVM Stats - At context creation:");
			buf.append(thisThreadPerfLogContext.getJvmStatsAtContextCreation());
			buf.append(LINE_SEPARATOR + "JVM Stats - Current:");
			buf.append(getJVMStats());
			if(requestDataContextString != null) {
				if(!requestDataContextString.equals("[null]") && !requestDataContextString.equals("[NullDataContext]")) {
					buf.append(LINE_SEPARATOR + "----- Request Data --------");
					buf.append(LINE_SEPARATOR + requestDataContextString);
				}
			}
			if(debugContextString != null ) {
				if(!debugContextString.equals("[null]") && !debugContextString.equals("[NullDataContext]")) {
					buf.append(LINE_SEPARATOR + "----- Debug Context Data --------" );
					buf.append(LINE_SEPARATOR + debugContextString );
				}
			}
			buf.append(LINE_SEPARATOR + "======== Request PerfLog Context Data End  ==================="+ LINE_SEPARATOR);
			
			if(inLogger != null) {
				inLogger.log(Level.SEVERE,buf.toString());
			}
			else  {
				// use built-in default logger 
				logger.info(buf.toString());
			}

		}
	}

	private static void _endPerfLogTxnMonitor(boolean forceDumpDebugContext) {
		String currentThreadID = new Long(Thread.currentThread().getId()).toString();
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext == null) {
			
			logger.warn("_endPerfLogTxnMonitor(): Unexpected delete for PerfLogContext object  requested for thread ID:"
							+ currentThreadID);

		}
		else {
			// Log context details if the response time threshold exceeds for this context...
			long elapsedTime = System.currentTimeMillis() - thisThreadPerfLogContext.getContextCreationTime();
			long responseTimeThreshold = 
				TxnThresholdProps.
					getMaxResponseTimeThresholdForTxnList(
							thisThreadPerfLogContext.getTxnList());
			
			if (elapsedTime > responseTimeThreshold) {
				logger.info("Elapsed time exceeded response time threshold: "+ responseTimeThreshold);
				dumpPerfLogContext();
			}
			else if (forceDumpDebugContext ) {
				logger.info("forceDumpDebugContext = true");
				dumpPerfLogContext();
			}
			else if(PerfLogContextProperties.instance().isForceDumpOfDebugContextOnDelete()) {
				logger.info("forceDumpOfDebugContextOnDelete = true");				
				dumpPerfLogContext();
			}
			if (thisThreadPerfLogContext.getJvmDepth() == 1
					&& LoggerProperties.getInstance()
							.isLogStartEndPerfLogTxnEnabled()) {
				// ----------------------------------------------------------
				// Before deleting this context log the end perf log txn
				long t1 = System.currentTimeMillis();
				PerfLogData perfLogData = new PerfLogData(thisThreadPerfLogContext);				
				perfLogData.setTransactionDate(new Date(t1));
				// set transaction type, other fields will be defaulted based on
				// current thread context.
				perfLogData.setTransactionType(PerfLogTxnType.END_PERF_LOG_TRANSACTION);
				perfLogData.setTransactionName("endPerfLogTxnMonitor");
				perfLogData.setSubTransactionName("perfLogTxnMarker");
				perfLogData.setTransactionTime(System.currentTimeMillis()-t1);
				logPerfLogData(perfLogData);
				// --------------------------------------------------------------
			}
			// remove the TxnFilter
			PerfLogContext.decrementTxnFilterDepth(thisThreadPerfLogContext);
			
			if(thisThreadPerfLogContext.getInfoContextStack()!=null)
				thisThreadPerfLogContext.getInfoContextStack().clear();
			if(thisThreadPerfLogContext.getDebugContextQueue()!=null)
				thisThreadPerfLogContext.getDebugContextQueue().clear();
			if(thisThreadPerfLogContext.getRequestDataContext()!=null)
				thisThreadPerfLogContext.getRequestDataContext().clear();
			if(thisThreadPerfLogContext.getTxnList()!=null)
				thisThreadPerfLogContext.getTxnList().clear();
			
			thisThreadPerfLogContext.getInfoContextPushCountForTxnFilterDepth().clear();
			guidHashMap.remove(currentThreadID);
			
			
		}
	}
	
	public static boolean endPerfLogTxnMonitor() {
		String currentThreadID = new Long(Thread.currentThread().getId()).toString();
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if(thisThreadPerfLogContext == null) {
			logger.warn("endPerfLogTxnMonitor(): Unexpected delete PerfLogContext object  requested for thread ID:"
					+ currentThreadID);
			return true;// assume it is deleted
		}
		if (thisThreadPerfLogContext.getTxnFilterDepth() > 1) {
			PerfLogContext.decrementTxnFilterDepth(thisThreadPerfLogContext);
			return false;
		} else {
			_endPerfLogTxnMonitor(false);
			return true;
		}
	}
	
	// argument indicates if the performance data collected for current txn filter needs to be logged
	// Caller can always log outside this call if required.
	// If no logging is desired then either use the overloaded method without this argument
	// or set this argument to false
	public static boolean endPerfLogTxnMonitor(boolean logPerfLogData) {
		if(logPerfLogData) {
			PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
			// create a PerfLogData object from current context information and log to the default
			// perfLogger
			PerfLogData perfLogData = new PerfLogData(thisThreadPerfLogContext);
			// Get Current TxnFilter info
			TxnData txnData=null;
			int currentTxnFilterDepth = thisThreadPerfLogContext.getTxnFilterDepth();
			TxnFilter txnFilter = thisThreadPerfLogContext.getTxnFilter(currentTxnFilterDepth);
			if(txnFilter != null && (txnData = txnFilter.getTxnData()) != null) {
				perfLogData.setTransactionDate(new Date(txnFilter.getCreationTime()));
				perfLogData.setTransactionName(txnData.getTxnName());
				perfLogData.setTransactionType(txnData.getTxnType());
				perfLogData.setSubTransactionName(txnData.getSubTxnName());
				perfLogData.setTransactionClass(txnData.getTxnClass());
				perfLogData.setTransactionTime(txnFilter.getTxnTime());
			}
			// other contextual details including guid, IP, host etc
			// will be filled in by this method
			PerfLogContextHelper.logPerfLogData(perfLogData);
		}
		
		// now end this PerfLogTxnMonitor
		return endPerfLogTxnMonitor();
	}
	
	// End the performance log monitor for the current txn filter
	// and log the provided perf log data
	public static boolean endPerfLogTxnMonitor(PerfLogData perfLogData) {
		if(perfLogData != null) {
			PerfLogContextHelper.logPerfLogData(perfLogData);
		}
		// now end this PerfLogTxnMonitor, this will remove the current
		// transaction filter and delete the perf log context if this is the
		// last txn filter
		return endPerfLogTxnMonitor();
	}
	
	public static void pushInfoContext(String context) {
		pushInfoContext("", context);
	}

	public static void pushInfoContext(ContextElement logContextElement) {
		pushInfoContext(logContextElement.getName(), logContextElement
				.getValue());
	}

	// context string any string that provides a unique business context for
	// logging
	// e.g. task id, user id, etc.
	// all context are logged as a list separated by | character
	// multiple contexts are allowed
	public static void pushInfoContext(String name, String value) {

		// first
		
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			thisThreadPerfLogContext.pushInfoContext(name, value);
		}
	}
	
	public static void pushInfoContext(String name, String value, int maxValueSize) {

		// first
		
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			thisThreadPerfLogContext.pushInfoContext(name, value, maxValueSize);
		}
	}
	
	public static void addToDebugContext(String context) {
		addToDebugContext("", context);
	}

	public static void addToDebugContext(ContextElement logContextElement) {
		addToDebugContext(logContextElement.getName(), logContextElement
				.getValue());
	}
	
	public static void addToDebugContext(String name, String value) {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext!= null) {
			thisThreadPerfLogContext.addToDebugContext(name, value);
		}
	}
	public static void addToRequestDataContext(String context) {
		addToDebugContext("", context);
	}

	public static void addToRequestDataContext(ContextElement logContextElement) {
		addToRequestDataContext(logContextElement.getName(), logContextElement
				.getValue());
	}
	
	public static void addToRequestDataContext(String name, String value) {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext !=null) {
			thisThreadPerfLogContext.addToRequestDataContext(name, value);

		}
	}
	
	public static void addToRequestDataContext(String name, String value, int maxValueSize) {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext !=null) {
			thisThreadPerfLogContext.addToRequestDataContext(name, value, maxValueSize);

		}
	}
	
	public static void removeLastInsertedFromRequestDataContext() {
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext !=null) {
			thisThreadPerfLogContext.removeLastInsertedFromRequestDataContext();
		}
	}
	
	public static void removeLastInsertedFromDebugDataContext() {
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext !=null) {
			thisThreadPerfLogContext.removeLastInsertedFromDebugDataContext();
		}
	}

	public static void popInfoContext() {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			thisThreadPerfLogContext.popInfoContext();
		}

	}
	
	public static void setUserId(String userId) {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			thisThreadPerfLogContext.setUserId(userId);
		}

	}
	
	public static String getUserId() {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			return thisThreadPerfLogContext.getUserId();
		}
		else 
			return null;

	}
	
	public static void setRequestSessionId(String sid) {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			thisThreadPerfLogContext.setRequestSessionId(sid);
		}
		
	}
	
	public static String getRequestSessionId() {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			return thisThreadPerfLogContext.getRequestSessionId();
		}
		else 
			return null;
		
	}
	
	public static String getThreadId() {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			return thisThreadPerfLogContext.getThreadId();
		}
		else 
			return null;
		
	}
	
	public static String getHostId() {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			return thisThreadPerfLogContext.getHostId();
		}
		else 
			return null;
		
	}
	
	public static String getHostIp() {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			return thisThreadPerfLogContext.getHostIp();
		}
		else 
			return null;
		
	}
	
	public static String getJvmCloneId() {

		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			return thisThreadPerfLogContext.getJvmCloneId();
		}
		else 
			return null;
		
	}
	
	public static PerfLogContext getCurrentThreadPerfLogContextObject() {
		String currentThreadID = new Long(Thread.currentThread().getId())
				.toString();
		return guidHashMap.get(currentThreadID);
	}
	
	public static String getBaseContextString() {
		return PerfLogContext.getBaseContextString(getCurrentThreadPerfLogContextObject());
	}
	
	
	public static String getBaseAndInfoContextString() {
		
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		
		return PerfLogContext.getBaseContextString(thisThreadPerfLogContext) + 
				PerfLogContext.getInfoContextString(thisThreadPerfLogContext);
	}

	public static String getDebugContextString() {
		
		return PerfLogContext.getDebugContextString(getCurrentThreadPerfLogContextObject());
	}
	
	public static String getValueForInfoContextName(String name) {
		
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			return PerfLogContext.getValueForContextName(name, thisThreadPerfLogContext.getInfoContextStack());
		}
		return null;
	}
	public static String getGuid() {
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null)
			return thisThreadPerfLogContext.getGuid();
		else
			return null;
	}

	
	
	
	public static long getElapsedTime() {
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			return thisThreadPerfLogContext.getElapsedTimeFromContextCreation();
		} else {
			return 0;
		}
	}

	public static String getRequestDataContextString() {
		
		return PerfLogContext.getRequestDataContextString(getCurrentThreadPerfLogContextObject());
	}

	public static long getElapsedTimeForCurrentFilter() {
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext != null) {
			return thisThreadPerfLogContext.getElapsedTimeFromTxnFilterCreation();
		} else {
			return 0;
		}
	}
	
	public static List<String> getTxnList() {
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext !=null) {
			return thisThreadPerfLogContext.getTxnList();

		}
		else 
			return null;
	}
	
	public static void addToTxnList(String txnName) {
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if (thisThreadPerfLogContext !=null) {
			thisThreadPerfLogContext.addToTxnList(txnName);

		}
	}
	
	// 
	// Helper function to aid logging performance metrics from custom application code
	// using the  PerfLogger implementation
	// Ideally the perfLogData is initialized by the calling application code
	// However if the common elements are not initialized this method will
	// extract information from the current perflog context
	// 
	public static void logPerfLogData(PerfLogData perfLogData) {
		PerfLogContext thisThreadPerfLogContext = getCurrentThreadPerfLogContextObject();
		if(perfLogData != null) {
			// fill in perfLogData with common elements from thread guid context
			// if not already initialized
			if(thisThreadPerfLogContext != null) {
				if(perfLogData.getUserId()==null)
					perfLogData.setUserId(thisThreadPerfLogContext.getUserId());
				if(perfLogData.getCloneName() == null)
					perfLogData.setCloneName(thisThreadPerfLogContext.getJvmCloneId());
				if(perfLogData.getGuid()==null)
					perfLogData.setGuid(thisThreadPerfLogContext.getGuid());
				if(perfLogData.getSessionId()==null)
					perfLogData.setSessionId(thisThreadPerfLogContext.getRequestSessionId());
				if(perfLogData.getThreadName()==null)
					perfLogData.setThreadName(thisThreadPerfLogContext.getThreadName());
				if(perfLogData.getServerIp()==null)
					perfLogData.setServerIp(thisThreadPerfLogContext.getHostIp());
				if(perfLogData.getServerName()==null)
					perfLogData.setServerName(thisThreadPerfLogContext.getHostId());
				if(perfLogData.getTransactionDate()==null)
					perfLogData.setTransactionDate(new Date());
				if(perfLogData.getTransactionName()==null)
					perfLogData.setTransactionName("UnknownTransactionName");
				if(perfLogData.getSubTransactionName()==null)
					perfLogData.setSubTransactionName("UnknownSubTransactionName");
				if(perfLogData.getTransactionType()==null)
					perfLogData.setTransactionType("UnknownTransactionType");
				// Set the perflog context of this thread to this perfLogData
				// This will also set the InfoContext String
				perfLogData.setPerfLogContext(thisThreadPerfLogContext);
				
			}
			PerfLogger plogger = PerfLoggerFactory.getLogger();
			plogger.log(perfLogData);
		}
	}
}
