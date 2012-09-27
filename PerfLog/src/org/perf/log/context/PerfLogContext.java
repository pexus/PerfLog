/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/context/PerfLogContext.java 
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

/**
 * 
 * @author Pradeep Nambiar 2/10/2012
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;
import org.perf.log.properties.PerfLogContextProperties;
import org.perf.log.utils.RuntimeEnvHelper;
import org.perf.log.utils.StringUtils;


public class PerfLogContext {
	// constructor to be called from PerfLogContextHelper only
	// hence make it protected
	protected PerfLogContext() {
		super();
		
	}
	private final static Logger logger = LoggerFactory.getLogger("PerfLogContext");
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private String guid=null;
	private String threadName=null;
	private String requestSessionId=null;
	private String threadId=null;
	private String hostId=null;
	private String hostIp=null;
	private String jvmCloneId=null;
	private String userId=null;
	private int jvmDepth;
	private int txnFilterDepth=0;
	private long contextCreationTime;
	private String jvmStatsAtContextCreation;
	private Vector<TxnFilter> txnFilters = new Vector<TxnFilter>(5,5);
	
	private long currentDebugDataContextSize = 0;
	private long currentRequestDataContextSize = 0;
	
	private long maxDebugContexDataSize = PerfLogContextProperties.instance().getMaxDebugContextSizeInBytes(); 
	private long maxRequestDataContextSize = PerfLogContextProperties.instance().getMaxRequestDataContextSizeInBytes();
	// response time threshold for this context
	// when deleting the context the full context is printed 
	// if the current time and contextStartTime is exceeded
	// this can be overridden after creating the context
	private long responseTimeThreshold = PerfLogContextProperties.instance().getResponseTimeThresholdInMillis(); 
	// vector to keep track of the number of context pushes that needs to be 
	// popped when decrementing the filter depth
	private Vector<Integer> infoContextPushCountForTxnFilterDepth = new Vector<Integer>(5,5);
	private Stack<ContextElement> infoContextStack = new Stack<ContextElement>();
	
	private static final String DEBUG_DATA_CONTEXT_QUEUE = "debugContextQ";
	private static final String REQUEST_DATA_CONTEXT_QUEUE = "requestContextQ";
	// Debug data that includes all logged strings are maintained in this queue
	// data is only cached to a maximum size, after which old elements are removed to 
	// make way for recent data.
	private Queue<ContextElement> debugContextQueue = new LinkedList<ContextElement>();
	// This Queue consists of any request specific data that filters and handlers can add
	// Data cache is limited to a maximum size to conserve memory
	// Oldest data is dropped in favor for most recent data added
	private ContextElement lastInsertedElementIntoDebugDataContext = null;
	
	private Queue<ContextElement> requestDataContext = new LinkedList<ContextElement>();
	// this is used to track the last inserted element into requestDataContext Queue
	// this is used to remove if required without disturbing the queue
	// in case of adding SQL strings, we add the SQL string before executing it
	// and also after execution with the execution time
	// If we return after a  successful execution, adding the same string
	// occupies too much space in request data context, therefore we remove the 
	// last inserted element. The reason we add twice is to know the SQL string
	// in case if the SQL execution times out and never returns.
	private ContextElement lastInsertedElementIntoRequestDataContext = null;
	
	// maintain list of txn or subTxn types generated from this context
	// this information is used to override certain actions such is 
	// selectively having a higher response time threshold for certain types of transactions
	// this list is populated when creating a filter
	private List<String> txnList = new ArrayList<String>();
	
	
	public String getGuid() {
		return guid;
	}
	protected void setGuid(String guid) {
		this.guid = guid;
	}
	public Stack<ContextElement> getInfoContextStack() {
		return infoContextStack;
	}
	public int getJvmDepth() {
		return jvmDepth;
	}
	protected void setJvmDepth(int callLevel) {
		this.jvmDepth = callLevel;
	}
	protected void setInfoContextStack(Stack<ContextElement> contextStack) {
		this.infoContextStack = contextStack;
	}
	public Queue<ContextElement> getDebugContextQueue() {
		return debugContextQueue;
	}
	protected void setDebugContextQueue(
			Queue<ContextElement> debugDetailContextQueue) {
		this.debugContextQueue = debugDetailContextQueue;
	}
	public int getTxnFilterDepth() {
		return txnFilterDepth;
	}
	protected void setTxnFilterDepth(int txnFilterDepth) {
		this.txnFilterDepth = txnFilterDepth;
	}
	public String getThreadId() {
		return threadId;
	}
	protected void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	public String getHostId() {
		return hostId;
	}
	protected void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public String getJvmCloneId() {
		return jvmCloneId;
	}
	protected void setJvmCloneId(String jvmCloneId) {
		this.jvmCloneId = jvmCloneId;
	}
	public Vector<Integer> getInfoContextPushCountForTxnFilterDepth() {
		return infoContextPushCountForTxnFilterDepth;
	}
	protected void setInfoContextPushCountForTxnFilterDepth(
			Vector<Integer> infoContextPushCountForTxnFilterDepth) {
		this.infoContextPushCountForTxnFilterDepth = infoContextPushCountForTxnFilterDepth;
	}
	public long getContextCreationTime() {
		return contextCreationTime;
	}
	public long getTxnFilterCreationTime(int fdepth) {
		// sanity check.. 
		if(fdepth <1 || fdepth >getTxnFilterDepth()) {
			logger.warn("getFilterCreationTime:Invalid filter depth:"+fdepth);
			return 0;
		}
		
		return txnFilters.get(fdepth-1).getCreationTime();
	}
	protected void setTxnFilterCreationTime(int fdepth, long creationTime) {
		txnFilters.get(fdepth-1).setCreationTime(creationTime);
	}
	protected void setContextCreationTime(long contextStartTime) {
		this.contextCreationTime = contextStartTime;
	}
	public long getResponseTimeThreshold() {
		return responseTimeThreshold;
	}
	protected void setResponseTimeThreshold(long responseTimeThreshold) {
		this.responseTimeThreshold = responseTimeThreshold;
	}
	protected static void decrementTxnFilterDepth(PerfLogContext thisPerfLogContext) {
		
			int currentDepth = thisPerfLogContext.getTxnFilterDepth();
			if(currentDepth >= 1) {
				thisPerfLogContext.txnFilters.remove(currentDepth-1);
				--currentDepth;
			}
			
			// pop all context from InfoContext for current filter depth
			// index to vector - currentDepth already adjusted for 0 based
			int numContextToPop = thisPerfLogContext.getInfoContextPushCountForTxnFilterDepth().get(currentDepth).intValue();
			for(int i = 0;i<numContextToPop;i++) thisPerfLogContext.getInfoContextStack().pop();
			
			/** we don't pop the degbug detail context.. 
			 * This is saved to be dumped when the context is deleted.. if DEBUG is enabled or
			 * if the response time threshold is exceeded.
			
			numContextToPop = thisThreadPerfLogContext.getDebugDetailContextPushCountForFilterDepth().get(currentDepth).intValue();
			for(int i =0;i<numContextToPop;i++) thisThreadPerfLogContext.getDebugDetailContextStack().pop();
			
			**/
			
			thisPerfLogContext.setTxnFilterDepth( currentDepth);
			if(currentDepth <0) 
				logger.warn("decrementTxnFilterDepth(): Filter depth less than 0, check filter log call sequence.");
	
		
	
	}
	protected static  void decrementInfoContextPushCount(PerfLogContext thisThreadPerfLogContext) {
		int currentPushCount = (thisThreadPerfLogContext
				.getInfoContextPushCountForTxnFilterDepth()
				.get(thisThreadPerfLogContext.getTxnFilterDepth() - 1))
				.intValue();
		--currentPushCount;
		if(currentPushCount<0)
			logger.warn( "decrementInfoContextPushCount:Negative push count:"+currentPushCount);
		thisThreadPerfLogContext
				.getInfoContextPushCountForTxnFilterDepth().set(
						thisThreadPerfLogContext.getTxnFilterDepth() - 1,
						new Integer(currentPushCount));
	}
	public String getBaseAndInfoContextString() {
		return getBaseContextString(this) + getInfoContextString(this);
	}
	public  String getInfoContextString() {
		return getInfoContextString(this);
	}
	public  String getBaseContextString() {
		return getBaseContextString(this);
	}
	
	protected static String getBaseContextString(PerfLogContext thisThreadPerfLogContext) {
		if(thisThreadPerfLogContext==null)
			return "["+
			PerfLogContextConstants.GUID+"=null|"+
			PerfLogContextConstants.SID+"=null"+"|"+
			PerfLogContextConstants.USER_ID+"=null"+"|"+
			PerfLogContextConstants.THREAD_ID+"="+Thread.currentThread().getId()+"|"+
			PerfLogContextConstants.HOST_ID+"="+RuntimeEnvHelper.getInstance().getHostName()+"|"+
			PerfLogContextConstants.JVM_CLONE_ID+"="+RuntimeEnvHelper.getInstance().getCloneName()+"|"+
			PerfLogContextConstants.JVM_DEPTH+"=1|"+
			PerfLogContextConstants.FILTER_DEPTH+"=0"+
			"]";
		else
			return
			"[ET="+thisThreadPerfLogContext.getElapsedTimeFromContextCreation()+"]"+
			"["+
			PerfLogContextConstants.GUID+"="+thisThreadPerfLogContext.getGuid()+"|"+
			PerfLogContextConstants.SID+"="+thisThreadPerfLogContext.getRequestSessionId()+"|"+
			PerfLogContextConstants.USER_ID+"="+thisThreadPerfLogContext.getUserId()+"|"+
			PerfLogContextConstants.THREAD_ID+"="+thisThreadPerfLogContext.getThreadId()+"|"+
			PerfLogContextConstants.HOST_ID+"="+thisThreadPerfLogContext.getHostId()+"|"+
			PerfLogContextConstants.HOST_IP+"="+thisThreadPerfLogContext.getHostIp()+"|"+
			PerfLogContextConstants.JVM_CLONE_ID+"="+thisThreadPerfLogContext.getJvmCloneId()+"|"+
			PerfLogContextConstants.JVM_DEPTH+"="+thisThreadPerfLogContext.getJvmDepth()+"|"+
			PerfLogContextConstants.FILTER_DEPTH+"="+thisThreadPerfLogContext.getTxnFilterDepth()+
			"]";
		
	}
	public String getDebugContextString() {
		
		return getDebugContextString(this);
	}
	private static String _getDataQueueContextString(Queue<ContextElement>dataQueue) {
		
		if (dataQueue != null) {
			// loop through all the context strings, concatenate with |
			// character and return
			Iterator<ContextElement> contextListIterator = dataQueue.iterator();
			String contextListString = null;
			while (contextListIterator.hasNext()) {
				if (contextListString == null)
					contextListString = "";
				else
					contextListString += LINE_SEPARATOR;
	
				ContextElement thisContextElement = contextListIterator
						.next();
				if (thisContextElement.getName() == null
						|| thisContextElement.getName() == "")
					contextListString += thisContextElement.getValue();
				else
					contextListString += thisContextElement.getName() + "="
							+ thisContextElement.getValue();
	
			}
			return "["+contextListString +"]";
	
		} else
			return "[NullDataContext]";
	}
	
	
	// debug context is only printed when response time threshold is exceeded
	// or explicitly printed by the code
	// use new line as the context element separator instead of the pipe character
	// used for info context string
	protected static String getDebugContextString(PerfLogContext thisThreadPerfLogContext) {
		
		if (thisThreadPerfLogContext != null) {
			
			 	return  _getDataQueueContextString(thisThreadPerfLogContext.getDebugContextQueue());
		} else
			return "[NullDataContext]";
	}
	protected static String getRequestDataContextString(PerfLogContext thisThreadPerfLogContext) {
		
		if (thisThreadPerfLogContext != null) {
			
			 	return  _getDataQueueContextString(thisThreadPerfLogContext.getRequestDataContext());
		} else
			return "[NullDataContext]";
	}
	
	
	
	protected static String getValueForContextName(String name, Stack<ContextElement> contextStack) {
		if (contextStack == null)
			return null;
		String returnVal = "";
		ListIterator<ContextElement> contextListIterator = contextStack.listIterator();
		// there could be multiple name value pairs with the same name..concat all values for the same name
		while (contextListIterator.hasNext()) {
			ContextElement thisContextElement = contextListIterator.next();
			if (thisContextElement.getName().equals(name)) {
				
				if(returnVal.equals(""))
					returnVal = returnVal +  thisContextElement.getValue();
				else
					returnVal = returnVal + "," + thisContextElement.getValue();
			}
			
		}
		if(returnVal.equals(""))
			return null;
			else 
				return returnVal; // if not found..
	}
	public  String getValueForInfoContextName(String name) {
		
			return getValueForContextName(name, this.getInfoContextStack());
		
	}
	protected static void incrementInfoContextPushCount(PerfLogContext thisThreadPerfLogContext) {
		int currentPushCount = (thisThreadPerfLogContext
				.getInfoContextPushCountForTxnFilterDepth()
				.get(thisThreadPerfLogContext.getTxnFilterDepth() - 1))
				.intValue();
		currentPushCount++;
		thisThreadPerfLogContext
				.getInfoContextPushCountForTxnFilterDepth().set(
						thisThreadPerfLogContext.getTxnFilterDepth() - 1,
						new Integer(currentPushCount));
	}
	protected static void incrementTxnFilterDepth(PerfLogContext thisPerfLogContext, TxnData txnData) {
			int currentFilterDepth = thisPerfLogContext.getTxnFilterDepth();
			currentFilterDepth++;
			// Create a new instance of TxnFilter object that keeps track of this Txn Filter
			thisPerfLogContext.setTxnFilterDepth(currentFilterDepth);
			// TODO add TxnFilter properties here.
			TxnFilter txnFilter = new TxnFilter(txnData);
			thisPerfLogContext.txnFilters.add(currentFilterDepth-1,txnFilter);
			thisPerfLogContext.setTxnFilterCreationTime(currentFilterDepth, System.currentTimeMillis());
			thisPerfLogContext.getInfoContextPushCountForTxnFilterDepth().add(currentFilterDepth-1, new Integer(0));
				
			
	}
	public  void popInfoContext() {
			this.getInfoContextStack().pop();
			decrementInfoContextPushCount(this);
	}
	
	
	
	// returns current datasize for the queue
	// this method is reused for managing the request data and debug data context queues
	// the last inserted element for each of the queue is also tracked
	private long _addToContextQueue(String dataQueueName,
			Queue<ContextElement> dataQueue, long maxDataSize,
			long currentDataSize, String name, String value) {
		if (name == null) {
			return currentDataSize;
		}
		long dataSizeToAdd = name.length();
		if (value == null) {
			dataSizeToAdd += 4;
		} else
			dataSizeToAdd += value.length();

		if ((dataSizeToAdd + currentDataSize) < maxDataSize) {
			currentDataSize += dataSizeToAdd;
			if(dataQueueName.equals(REQUEST_DATA_CONTEXT_QUEUE)) {
				lastInsertedElementIntoRequestDataContext = new ContextElement(name, value);
				dataQueue.add(lastInsertedElementIntoRequestDataContext);
			}
			else {
				lastInsertedElementIntoDebugDataContext = new ContextElement(name, value);
				dataQueue.add(lastInsertedElementIntoDebugDataContext);
			}
		} else {
			// Remove data from the head of the queue to make space for this
			// string...
			logger.debug("_addToContextQueue:" + dataQueueName
					+ ":Removing data from data queue to make room");

			while ((dataSizeToAdd + currentDataSize) > maxDataSize) {
				try {
					if (dataQueue.size() == 0) {
						currentDataSize = 0;
						break;
					}
					ContextElement removedElement = dataQueue.remove();
					if (removedElement != null) {
						String str = removedElement.getName();
						if (str == null)
							currentDataSize -= 4;
						else
							currentDataSize -= str.length();
						str = removedElement.getValue();
						if (str == null)
							currentDataSize -= 4;
						else
							currentDataSize -= str.length();
						
						// safe check..
						if(currentDataSize < 0 || currentDataSize == 0) {
							currentDataSize = 0;
							break;
						}

					}
				} catch (NoSuchElementException e) {
					// Queue empty..
					break;
				}
			} // while

			// check size again.. to see if we can add to the queue
			if ((dataSizeToAdd + currentDataSize) > maxDataSize) {
				// truncate...
				dataSizeToAdd = (maxDataSize - currentDataSize); // 3 is the trailing dots
				value = StringUtils.truncateString(value, (int)dataSizeToAdd - 3);
			}
			
			currentDataSize += dataSizeToAdd;
			if(dataQueueName.equals(REQUEST_DATA_CONTEXT_QUEUE)) {
				lastInsertedElementIntoRequestDataContext = new ContextElement(name, value);
				dataQueue.add(lastInsertedElementIntoRequestDataContext);
			}
			else {
				lastInsertedElementIntoDebugDataContext = new ContextElement(name, value);
				dataQueue.add(lastInsertedElementIntoDebugDataContext);
			}
			

		}
		return currentDataSize;

	}
	
	protected void removeLastInsertedFromRequestDataContext() {
		logger.debug("removeLastInsertedFromRequestDataContext");
		if(lastInsertedElementIntoRequestDataContext != null) {
			String name = lastInsertedElementIntoRequestDataContext.getName();
			String value = lastInsertedElementIntoRequestDataContext.getValue();
			boolean removed = 
				requestDataContext.remove(lastInsertedElementIntoRequestDataContext);
			if(removed) {
				logger.debug("removed element name = " + name);
				if (name == null)
					currentRequestDataContextSize -= 4;
				else
					currentRequestDataContextSize -= name.length();
				if (value == null)
					currentRequestDataContextSize -= 4;
				else
					currentRequestDataContextSize -= value.length();
				if(currentRequestDataContextSize < 0)
					currentRequestDataContextSize = 0;
			}
			lastInsertedElementIntoRequestDataContext = null;
		}
	}
	
	protected void removeLastInsertedFromDebugDataContext() {
		logger.debug("removeLastInsertedFromDebugDataContext");
		if(lastInsertedElementIntoDebugDataContext != null) {
			String name = lastInsertedElementIntoDebugDataContext.getName();
			String value = lastInsertedElementIntoDebugDataContext.getValue();
			boolean removed = 
				debugContextQueue.remove(lastInsertedElementIntoDebugDataContext);
			if(removed) {
				logger.debug("removed element name = " + name);
				if (name == null)
					currentDebugDataContextSize -= 4;
				else
					currentDebugDataContextSize -= name.length();
				if (value == null)
					currentDebugDataContextSize -= 4;
				else
					currentDebugDataContextSize -= value.length();
				if(currentDebugDataContextSize < 0)
					currentDebugDataContextSize = 0;
			}
			lastInsertedElementIntoDebugDataContext = null;
		}
	}
	
	protected  void addToDebugContext(ContextElement logContextElement) {
		addToDebugContext(logContextElement.getName(), logContextElement
				.getValue());
	}
	protected void addToDebugContext(String context) {
		addToDebugContext("", context);
	}
	protected  void addToDebugContext(String name, String value) {
		currentDebugDataContextSize = 
			_addToContextQueue(DEBUG_DATA_CONTEXT_QUEUE,
				debugContextQueue, 
				maxDebugContexDataSize, 
				currentDebugDataContextSize, 
				name, "[ET="+getElapsedTimeFromContextCreation()+"]"+value);
		
		
	}
	
	protected  void addToRequestDataContext(ContextElement logContextElement) {
		addToRequestDataContext(logContextElement.getName(), logContextElement
				.getValue());
	}
	protected void addToRequestDataContext(String context) {
		addToRequestDataContext("", context);
	}
	
	private void _addToRequestDataContext(String name, String value, int maxValueSize) {
		String addValue = StringUtils.truncateString(value, maxValueSize);
		currentRequestDataContextSize = 
			_addToContextQueue(REQUEST_DATA_CONTEXT_QUEUE,
				requestDataContext, 
				maxRequestDataContextSize, 
				currentRequestDataContextSize, 
				name, "[ET="+getElapsedTimeFromContextCreation()+"]"+addValue);
		
		
	}
	
	protected  void addToRequestDataContext(String name, String value, int maxValueSize) {
		_addToRequestDataContext(name, value, maxValueSize);
	}
	
	
	protected  void addToRequestDataContext(String name, String value) {
		int valueSize;
		 if(value == null)
			 valueSize = 0;
		 else if(value.equals(""))
			 valueSize  = 0;
		 else 
			 valueSize = value.length();
		_addToRequestDataContext(name, value, valueSize);
			
	}
	
	private void _pushInfoContext(String name, String value, int maxValueSize) {
		String pushValue = StringUtils.truncateString(value, maxValueSize);
		// Avoid duplicate entries if name and value pair already exists
		String checkValue = getValueForInfoContextName(name);
		if (checkValue != null && checkValue.equals(pushValue))
			return;
		// else push the value into the context..

		this.getInfoContextStack().push(new ContextElement(name, pushValue));
		incrementInfoContextPushCount(this);
	}
	
	public void pushInfoContext(ContextElement logContextElement) {
		pushInfoContext(logContextElement.getName(), logContextElement
				.getValue());
	}
	public  void pushInfoContext(String context) {
		pushInfoContext("", context);
	}
	
	public void pushInfoContext(String name, String value, int maxValueSize)
	{
		_pushInfoContext(name, value, maxValueSize);
	}
	// context string any string that provides a unique business context for
	// logging
	// e.g. task id, user id, etc.
	// all context are logged as a list separated by | character
	// multiple contexts are allowed
	public void pushInfoContext(String name, String value) {
		 int valueSize;
		 if(value == null)
			 valueSize = 0;
		 else if(value.equals(""))
			 valueSize  = 0;
		 else 
			 valueSize = value.length();
		_pushInfoContext(name, value, valueSize);
		
	}
	protected static String getInfoContextString(PerfLogContext thisThreadPerfLogContext) {
		
		if (thisThreadPerfLogContext != null) {
			// loop through all the context strings, concatenate with |
			// character and return
			ListIterator<ContextElement> contextListIterator = thisThreadPerfLogContext
					.getInfoContextStack().listIterator();
			String contextListString = null;
			while (contextListIterator.hasNext()) {
				if (contextListString == null)
					contextListString = "";
				else
					contextListString += "|";
	
				ContextElement thisContextElement = contextListIterator
						.next();
				if (thisContextElement.getName() == null
						|| thisContextElement.getName() == "")
					contextListString += thisContextElement.getValue();
				else
					contextListString += thisContextElement.getName() + "="
							+ thisContextElement.getValue();
	
			}
			return "["+contextListString +"] ";
	
		} else
			return "[NullInfoContext]";
	}
	protected void setCurrentDebugDataContextSize(long currentDebugContextDataSize) {
		this.currentDebugDataContextSize = currentDebugContextDataSize;
	}
	public long getMaxDebugContexDataSize() {
		return maxDebugContexDataSize;
	}
	protected void setMaxDebugContexDataSize(long maxDebugContexDataSize) {
		this.maxDebugContexDataSize = maxDebugContexDataSize;
	}
	public long getElapsedTimeFromContextCreation() {
	    return System.currentTimeMillis() - this.getContextCreationTime();
	}
	public long getMaxRequestDataContextSize() {
		return maxRequestDataContextSize;
	}
	protected void setMaxRequestDataContextSize(long maxRequestDataContextSize) {
		this.maxRequestDataContextSize = maxRequestDataContextSize;
	}
	public Queue<ContextElement> getRequestDataContext() {
		return requestDataContext;
	}
	protected void setRequestDataContext(Queue<ContextElement> requestDataContext) {
		this.requestDataContext = requestDataContext;
	}
	public long getElapsedTimeFromTxnFilterCreation() {
		// get current filter depth
		int currentTxnFilterDepth = getTxnFilterDepth();
		long txnFilterCreationTime = this.txnFilters.get(currentTxnFilterDepth-1).getCreationTime();
		
	    return System.currentTimeMillis() - txnFilterCreationTime;
	}
	public long getCurrentDebugDataContextSize() {
		return currentDebugDataContextSize;
	}
	public long getCurrentRequestDataContextSize() {
		return currentRequestDataContextSize;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRequestSessionId() {
		return requestSessionId;
	}
	public void setRequestSessionId(String requestSessionId) {
		this.requestSessionId = requestSessionId;
	}

	/**
	 * @return the jvmStatsAtContextCreation
	 */
	public String getJvmStatsAtContextCreation() {
		return jvmStatsAtContextCreation;
	}

	/**
	 * @param jvmStatsAtContextCreation the jvmStatsAtContextCreation to set
	 */
	public void setJvmStatsAtContextCreation(String jvmStatsAtContextCreation) {
		this.jvmStatsAtContextCreation = jvmStatsAtContextCreation;
	}

	/**
	 * @return the txnList
	 */
	public List<String> getTxnList() {
		return txnList;
	}
	
	protected void addToTxnList(String txnName) {
		txnList.add(txnName);
	}

	/**
	 * @return the hostIp
	 */
	public String getHostIp() {
		return hostIp;
	}

	/**
	 * @param hostIp the hostIp to set
	 */
	protected void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getThreadName() {
		return threadName;
	}

	protected void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public Vector<TxnFilter> getTxnFilters() {
		return txnFilters;
	}
	
	// filterDepth uses 1 based index
	// Vector is 0 based index
	public TxnFilter getTxnFilter(int filterDepth) {
		
		Vector<TxnFilter> txnFilters = getTxnFilters();
		if(txnFilters!=null && filterDepth > 0 && filterDepth <= txnFilters.size())
			return txnFilters.get(filterDepth-1);
		else 
			return null;
	}
	
	

}
