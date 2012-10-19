/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/PerfLoggerImplAsyncThreadAbstract.java 
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
 * This is an abstract Perf Logger implementation that uses
 * work manager to implement an asynchronous logger 
 * to log performance data to a file and database
 * @author Pradeep Nambiar 2/10/2012
 */

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import org.perf.log.properties.LoggerProperties;
import org.perf.log.properties.TunableProperties;
import org.perf.log.utils.PropertyFileLoader;

public abstract class PerfLoggerImplAsyncThreadAbstract implements PerfLogger {
	private final static Logger logger = LoggerFactory.getLogger(PerfLoggerImplAsyncThreadAbstract.class.getName());

	// Async Logger Thread Implementation properties
	public static final String LOGGER_ASYNC_THREAD_LOGGER_NUM_ASYNC_LOGGER_TASK_THREADS = "static.logger.asyncThreadLogger.numAsyncLoggerTaskThreads"; // static property
	public static final String LOGGER_ASYNC_THREAD_LOGGER_MAX_Q_SIZE = "dynamic.logger.asyncThreadLogger.maxQSize"; // dynamic property
	public static final String LOGGER_ASYNC_THREAD_LOGGER_WORK_MANAGER_RESOURCE_NAME = "static.logger.asyncThreadLogger.workManagerResourceName"; // static property
	

	// Max records in a single Q
	static long maxQSize = 2000;
	// num instances of worker thread
	static int numAsyncLoggerTaskThreads = 2;
	// JNDI Resource for default work manager
	static String workManagerResourceName = "wm/default";

	static ArrayList<LinkedBlockingQueue<PerfLogData>> perfLogQueueArrayList = new ArrayList<LinkedBlockingQueue<PerfLogData>>();
	
	LogQueueMetricTracker logQueueMetricTrackerArray[];
	
	int roundRobinCount = 0;
	static TunableProperties tunableProperties = LoggerProperties.getInstance().getTunableProperties();
	
	static boolean propertiesInited = false;
	
	private synchronized void initProperties() {
		if(!propertiesInited) {
			try {
				
				String propVal;
				ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
				Properties props = PropertyFileLoader.load(
						"perfLog.properties", 
						"perfLogDefault.properties", 
						ctxClassLoader,
						this.getClass().getClassLoader(),
						PerfLoggerImplAsyncThreadAbstract.class.getName());
				if (props != null) {
				
		        	propVal = props.getProperty(LOGGER_ASYNC_THREAD_LOGGER_MAX_Q_SIZE);
		        	if(propVal!=null)
		        		setMaxQSize(new Long(propVal).longValue());
		        	propVal = props.getProperty(LOGGER_ASYNC_THREAD_LOGGER_NUM_ASYNC_LOGGER_TASK_THREADS);
		        	if(propVal!=null)
		        		setNumAsyncLoggerTaskThreads(new Integer(propVal).intValue());
		        	propVal = props.getProperty(LOGGER_ASYNC_THREAD_LOGGER_WORK_MANAGER_RESOURCE_NAME);
		        	if(propVal!=null)
		        		setWorkManagerResourceName(propVal);
				} else {
					System.out.println(PerfLoggerImplAsyncThreadAbstract.class.getName()
								+":Error in reading perfLog.properties or perfLogDefault.properties");
				}
			} catch (Exception exception) {
				System.out.println(PerfLoggerImplAsyncThreadAbstract.class.getName()+
						":Error Loading perfLog.properties or perfLogDefault.proeprties" 
							+ exception.getMessage());
			}			
			propertiesInited = true;
		}
	}

	public PerfLoggerImplAsyncThreadAbstract() {
		super();
		// implementing class must look up work manager, initialize 
		// start the asynchronous work on thread to dequeue PerfLogData from the queues
		// See sample implementations for WebSphere and CommonJ work managers
		initProperties();

	}

	private synchronized int getQueueIndexToLog() {
		// do a round robin between the number of Queues available..
		int index = roundRobinCount;
		roundRobinCount++;
		if (roundRobinCount == getNumAsyncLoggerTaskThreads())
			roundRobinCount = 0;
		return index;
	}

	@Override
	public void log(PerfLogData perfLogData) {
		if (perfLogData != null
				&& getLogEnabled(perfLogData.getTransactionTime())) {
			//--- Begin Log - if log enabled and exceeds log threshold
			int index = getQueueIndexToLog();
			if (logQueueMetricTrackerArray[index].getQSize() <= getMaxQSize()) {
				perfLogQueueArrayList.get(index).add(perfLogData);
				logQueueMetricTrackerArray[index].incrementSize();

			} else {
				// remove the oldest in the Q and add this one to the end..
				// This will ensure the memory will not grow unbounded.
				// and at the same time keep the most recent perf log records
				
				// and increment the num dropped count..
				perfLogQueueArrayList.get(index).remove();
				perfLogQueueArrayList.get(index).add(perfLogData);
				logQueueMetricTrackerArray[index].incrementNumDropped();
			}
			
			//--- End log
		}

	}

	@Override
	public void setLogEnabled(boolean logEnabled) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the maxQSize
	 */
	public static long getMaxQSize() {
		String propertyValue = tunableProperties
				.getStaticProperty(LOGGER_ASYNC_THREAD_LOGGER_MAX_Q_SIZE);
		if (propertyValue != null) {
			maxQSize = new Long(propertyValue).longValue();
		}
		return maxQSize;

	}

	/**
	 * @return the numAsyncLoggerTaskThreads
	 */
	public static int getNumAsyncLoggerTaskThreads() {
		String propertyValue = tunableProperties
				.getStaticProperty(LOGGER_ASYNC_THREAD_LOGGER_NUM_ASYNC_LOGGER_TASK_THREADS);
		if (propertyValue != null) {
			numAsyncLoggerTaskThreads = new Integer(propertyValue).intValue();
		}
		return numAsyncLoggerTaskThreads;
	}

	/**
	 * @param maxQSize
	 *            the maxQSize to set
	 */
	public static void setMaxQSize(long maxQSize) {
		PerfLoggerImplAsyncThreadAbstract.maxQSize = maxQSize;
	}

	/**
	 * @param numAsyncLoggerTaskThreads
	 *            the numAsyncLoggerTaskThreads to set
	 */
	public static void setNumAsyncLoggerTaskThreads(int numAsyncLoggerTasks) {
		PerfLoggerImplAsyncThreadAbstract.numAsyncLoggerTaskThreads = numAsyncLoggerTasks;
	}

	@Override
	public boolean getLogEnabled(long txnTimeInMillis) {
		return (LoggerProperties.getInstance().isPerfLoggerImplLogEnabled() && (txnTimeInMillis >= LoggerProperties
				.getInstance().getPerfLoggerImplLogThreshold()));
	}

	public static String getWorkManagerResourceName() {
		return workManagerResourceName;
	}

	public static void setWorkManagerResourceName(String workManagerResourceName) {
		PerfLoggerImplAsyncThreadAbstract.workManagerResourceName = workManagerResourceName;
	}

}
