/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/AbsAsyncLoggerTask.java 
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
 * Abstract Asynchronous Worker Thread run() implementation
 * The thread is created in PerfLoggerImplXXXX  class
 * Multiple threads can be created that listens to independent queues
 * The PerfLoggerImplXXX class uses a simple round robin
 * to queue to multiple queues
 * This abstract class implements methods that can be used
 * in either WebSphere Work Manager or CommonJ Work Manager APIs
 *  
 * @author Pradeep Nambiar 2/10/2012
 */


import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.perf.log.properties.LoggerProperties;
import org.perf.log.properties.TunableProperties;
import org.perf.log.utils.PropertyFileLoader;

public abstract class AsyncLoggerWorkerThreadAbstract {
	private final static Logger logger = LoggerFactory.getLogger(AsyncLoggerWorkerThreadAbstract.class.getName());
	String asyncLogTaskName;
	ConcurrentLinkedQueue<PerfLogData> logQueue;
	LogQueueMetricTracker logQueueMetricTracker;
	// Property Names 
	// following can be set dynamically
	public static final String LOGGER_ASYNC_THREAD_LOGGER_MIN_FLUSH_THRESHOLD = "dynamic.logger.asyncThreadLogger.minFlushThreshold"; // dynamic
	public static final String LOGGER_ASYNC_THREAD_LOGGER_THREAD_SLEEP_TIME_IN_MILLIS = "dynamic.logger.asyncThreadLogger.threadSleepTimeInMillis"; // dynamic
	public static final String LOGGER_ASYNC_THREAD_LOGGER_PRINT_STAT_TIME_INTERVAL_IN_MILLIS = "dynamic.logger.asyncThreadLogger.printStatTimeInMillis";// dynamic
	
	static int minFlushThreshold = 500;
	static long threadSleepTimeInMillis = 10000;
	static long printStatTimeIntervalInMillis = 600000;
	
	static boolean terminateThread = false;
	private static TunableProperties tunableProperties = LoggerProperties.getInstance().getTunableProperties();
	
	private static boolean propertiesInited = false;
	
	private synchronized void initProperties() {
		if (!propertiesInited) {
			try {
				String propVal;
				ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
				Properties props = PropertyFileLoader.load(
						"perfLog.properties", 
						"perfLogDefault.properties",
						ctxClassLoader,
						this.getClass().getClassLoader(),
						AsyncLoggerWorkerThreadAbstract.class.getName());
				
				if (props != null) {
					
					propVal = props
							.getProperty(LOGGER_ASYNC_THREAD_LOGGER_MIN_FLUSH_THRESHOLD);
					if (propVal != null)
						setMinFlushThreshold(new Integer(propVal).intValue());
					
					propVal = props
							.getProperty(LOGGER_ASYNC_THREAD_LOGGER_PRINT_STAT_TIME_INTERVAL_IN_MILLIS);					
					if (propVal != null)
						setPrintStatTimeIntervalInMillis(new Long(propVal)
								.longValue());
					
					propVal = props
							.getProperty(LOGGER_ASYNC_THREAD_LOGGER_THREAD_SLEEP_TIME_IN_MILLIS);					
					if (propVal != null)
						setThreadSleepTimeInMillis(new Long(propVal)
								.longValue());
					
				} else {
					System.out.println(AsyncLoggerWorkerThreadAbstract.class.getName()+
		        				":Error in reading perfLog.properties or perfLogDefault.properties");
				}

			} catch (Exception exception) {
				System.out.println(AsyncLoggerWorkerThreadAbstract.class.getName()+
        				":Error Loading perfLog.properties or perfLogDefault.properties"
								+ exception.getMessage());
			}

			propertiesInited = true;
		}

	}

	public AsyncLoggerWorkerThreadAbstract(String asyncLogTaskName,
			ConcurrentLinkedQueue<PerfLogData> logQueue,
			LogQueueMetricTracker logQueueMetricTracker) {
		super();
		this.asyncLogTaskName = asyncLogTaskName;
		this.logQueue = logQueue;
		this.logQueueMetricTracker = logQueueMetricTracker;
		
		initProperties();

	}

	
	public void release() {
		// stop the thread
		terminateThread = true;

	}

	
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		// add thread name to the task name
		this.asyncLogTaskName = Thread.currentThread().getName() + ":" + this.asyncLogTaskName; 
		logQueueMetricTracker.setThreadManagingThisQueue(Thread.currentThread());
		// Configuration params
		System.out.println(asyncLogTaskName + ": Worker thread started.");
		long lastPrintStatTimeInMillis = System.currentTimeMillis();
		System.out.println(asyncLogTaskName + ": " + logQueueMetricTracker.getStats());	
		
		// iterate over the queue and write to file and DB
		while (!terminateThread) {
			long qSize = logQueueMetricTracker.getQSize();
			if (qSize > 0 ) {
				while (!logQueue.isEmpty()) {
					PerfLogData logData = logQueue.remove();
					// Write to performance log file if enabled
					if(FileWriter.isFileWriterEnabled())
						try {
							FileWriter.write(logData);
						} catch (Exception e) {
							// An exception occurred, increment error count and continue
							logQueueMetricTracker.incrementNumErrored();													
						}
					// Write to performance database if enabled
					if(DBWriter.isDbWriterEnabled())
						try {
							DBWriter.write(logData);
						} catch (Exception e) {
							// An Exception occurred while writing to DB, increment error count and continue
							logQueueMetricTracker.incrementNumErrored();							
						}
					logQueueMetricTracker.decrementSize();
				}
				
			}

			try {
				logQueueMetricTracker.setThreadManagingThisQueueIsSleeping(true);
				Thread.sleep(getThreadSleepTimeInMillis(), 0);
				logQueueMetricTracker.setThreadManagingThisQueueIsSleeping(false);
							
			} catch (InterruptedException e) {
				// could get interrupted by the thread that adds to this Q
				// if interrupted continue..
				logQueueMetricTracker.setThreadManagingThisQueueIsSleeping(false);
			
			}
			if ((System.currentTimeMillis() - lastPrintStatTimeInMillis) > printStatTimeIntervalInMillis) {
				System.out.println(asyncLogTaskName + ": " + logQueueMetricTracker.getStats());
				lastPrintStatTimeInMillis = System.currentTimeMillis();
			}
		}
		System.out.println(asyncLogTaskName + ": " + logQueueMetricTracker.getStats());
		System.out.println(asyncLogTaskName + ": Worker thread terminated.");

	}

	/**
	 * @return the minFlushThreshold
	 */
	public static int getMinFlushThreshold() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_ASYNC_THREAD_LOGGER_MIN_FLUSH_THRESHOLD);
		if(propertyValue != null) {
			return new Integer(propertyValue).intValue();
		}
		else // return value initialized in this class
			return minFlushThreshold;
	}

	/**
	 * @return the threadSleepTimeInMillis
	 */
	public static long getThreadSleepTimeInMillis() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_ASYNC_THREAD_LOGGER_THREAD_SLEEP_TIME_IN_MILLIS);
		if(propertyValue != null) {
			return new Long(propertyValue).longValue();
		}
		else 
			return threadSleepTimeInMillis;
	}

	/**
	 * @return the printStatTimeIntervalInMillis
	 */
	public static long getPrintStatTimeIntervalInMillis() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_ASYNC_THREAD_LOGGER_PRINT_STAT_TIME_INTERVAL_IN_MILLIS);
		if(propertyValue != null) {
			return new Integer(propertyValue).intValue();
		}
		else 
			return printStatTimeIntervalInMillis;
	}

	

	


	/**
	 * @param minFlushThreshold the minFlushThreshold to set
	 */
	public static void setMinFlushThreshold(int minFlushThreshold) {
		AsyncLoggerWorkerThreadAbstract.minFlushThreshold = minFlushThreshold;
	}

	
	/**
	 * @param threadSleepTimeInMillis the threadSleepTimeInMillis to set
	 */
	public static void setThreadSleepTimeInMillis(long threadSleepTimeInMillis) {
		AsyncLoggerWorkerThreadAbstract.threadSleepTimeInMillis = threadSleepTimeInMillis;
	}

	/**
	 * @param printStatTimeIntervalInMillis the printStatTimeIntervalInMillis to set
	 */
	public static void setPrintStatTimeIntervalInMillis(
			long printStatTimeIntervalInMillis) {
		AsyncLoggerWorkerThreadAbstract.printStatTimeIntervalInMillis = printStatTimeIntervalInMillis;
	}

	
	
	public boolean isDaemon() {
		
		return true;
	}

	
}
