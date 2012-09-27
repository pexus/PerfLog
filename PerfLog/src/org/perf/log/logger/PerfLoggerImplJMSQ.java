/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/PerfLoggerImplJMSQ.java 
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.perf.log.properties.LoggerProperties;
import org.perf.log.properties.TunableProperties;

// This implementation will work with JMS including MQ environment

public class PerfLoggerImplJMSQ implements PerfLogger 
{
	// Properties for this class
	private static final String STATIC_PERF_LOGGER_IMPL_JMSQ_QUEUE = "static.perfLoggerImplJMSQ.queue";
	private static final String STATIC_PERF_LOGGER_IMPL_JMSQ_QUEUE_CONNECTION_FACTORY = "static.perfLoggerImplJMSQ.queueConnectionFactory";
	private static final String DYNAMIC_PERF_LOGGER_IMPL_JMSQ_MESSAGE_EXPIRATION_TIME_IN_MILLIS = "dynamic.perfLoggerImplJMSQ.messageExpirationTimeInMillis"; // Dynamic
	private static TunableProperties tunableProperties = null;
	protected boolean logEnabled; // initialized from property file
	
	//Default the JMS Message expire time to 1 hours = 3600000 
	private static long msgExpiryTimeInMillis = 3600000L;
	
	//Defult perfLoggerImpl if JMS resource initialization fails
	protected static PerfLoggerImplStdOut defaultPerfLogger = new PerfLoggerImplStdOut();
	

	private final static Logger logger = LoggerFactory.getLogger(PerfLoggerImplJMSQ.class.getName());
	
	// cached variables

	protected static ConnectionFactory cachedConnectionFactory = null;
	protected static Destination cachedQDestination = null;
	protected static long messageDeliveryExpirationTime ;
	
	protected static String queueName;
	protected static String queueConnFactoryName;

	// cached variables
	protected static Context cachedContext = null;
	
	public long getMesageDeliveryExpirationInMillis()
	{
		
		 if(tunableProperties == null) 
			 	tunableProperties = LoggerProperties.getInstance().getTunableProperties();
		// Check if this property is defined in Name Space binding and refresh the value
		// if defined. This enables dynamic control to enable or disable this property
		String propertyValue = tunableProperties.getDynamicProperty(DYNAMIC_PERF_LOGGER_IMPL_JMSQ_MESSAGE_EXPIRATION_TIME_IN_MILLIS);
		if(propertyValue != null) 
		{
			return Long.valueOf(propertyValue);
		}
		else 
			// return value initialized in this class
			return msgExpiryTimeInMillis;
	}
	

	public boolean getLogEnabled(long txnTimeInMillis) 
	{
		return (LoggerProperties.getInstance().isPerfLoggerImplLogEnabled() 
				&& 
				(txnTimeInMillis >= 
					LoggerProperties.getInstance().getPerfLoggerImplLogThreshold()));
	}

	
	public void setLogEnabled(boolean inLogEnabled) 
	{
		logEnabled = inLogEnabled;
	}
	

	protected void loadProps() 
	{
		
		Properties props = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("perfLog.properties");	
        if(in == null) {
        	System.out.println("PerfLoggerImplJMSQ.loadProps(): Error loading perfLog.properties, attempting to load perfLogDefault.properties now.");
        	in = PerfLoggerImplJMSQ.class.getClassLoader().getResourceAsStream ("perfLogDefault.properties");
        }
		if (in != null) 
		{
			try 
			{
				props.load(in);
			} catch (IOException ioException) 
			{
				System.out.println("PerfLoggerImplJMSQ.loadProps():"+ioException.getMessage());
			}
			queueConnFactoryName = props.getProperty(STATIC_PERF_LOGGER_IMPL_JMSQ_QUEUE_CONNECTION_FACTORY);
			if (queueConnFactoryName == null)
				queueConnFactoryName = "jms/perfQCF";
			queueName = props.getProperty(STATIC_PERF_LOGGER_IMPL_JMSQ_QUEUE);
			if (queueName == null)
				queueName = "jms/qPerfLog";
			
			String propVal  = props.getProperty(DYNAMIC_PERF_LOGGER_IMPL_JMSQ_MESSAGE_EXPIRATION_TIME_IN_MILLIS);
			if(propVal!=null)
				msgExpiryTimeInMillis = Long.valueOf(propVal);

		} else
		{
			System.out.println("PerfLoggerImplJMSQ.loadProps():"+"Error in loading perfLog.properties, using default properties");
		}

		System.out.println("PerfLoggerImplJMSQ.loadProps():queueConnFactoryName=" + queueConnFactoryName);
		System.out.println("PerfLoggerImplJMSQ.loadProps():queueName=" + queueName);
		System.out.println("PerfLoggerImplJMSQ.loadProps():msgExpiryTimeInMillis=" + msgExpiryTimeInMillis);
	}

	private static ConnectionFactory getConnectionFactory(Context context) 
	{
		ConnectionFactory connectionFactory = null;
		try 
		{
			connectionFactory = (ConnectionFactory) context.lookup(queueConnFactoryName);
		} catch (NamingException e) 
		{
			logger.error(e.getMessage(), e);
			return null;
		}
		return connectionFactory;
	}

	private static Destination getQueueDestination(Context context) 
	{
		Destination queueDestination = null;
		try 
		{
			queueDestination = (Queue) context.lookup(queueName);

		} catch (NamingException e) 
		{
			logger.error(e.getMessage(), e);
			return null;
		}
		return queueDestination;
	}

	PerfLoggerImplJMSQ() 
	{
		super();
		loadProps();
		// cache initial context 

		try {
			cachedContext = new InitialContext();
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
		}

		if (cachedContext != null) 
		{
			cachedConnectionFactory = getConnectionFactory(cachedContext);
			cachedQDestination = getQueueDestination(cachedContext);
		}
	}

	@Override
	public void log(PerfLogData perfLogData)
	{
		
		if (getLogEnabled(perfLogData.getTransactionTime()) 
				&& cachedContext != null 
				&& cachedConnectionFactory != null	
				&& cachedQDestination != null) 
		{
			Connection connection = null;
			Session session = null;
			long startTime;
			long endTime;
			long elapsedDuration;
			try
			{
				// Start Time
				startTime = System.currentTimeMillis();
				logger.debug("Start Time: " + startTime + "(ms)");
				
				connection = cachedConnectionFactory.createConnection();
				session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(cachedQDestination);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				// messages sent by this producer will be retained for the value specified in the properties file.
				messageDeliveryExpirationTime = getMesageDeliveryExpirationInMillis();
				
				logger.debug("messageDeliveryExpirationTime="+messageDeliveryExpirationTime);
				
				producer.setTimeToLive(messageDeliveryExpirationTime);
				TextMessage msg = session.createTextMessage();
				String perfLogDataJSONStr = perfLogData.toJSON();
				logger.debug(perfLogDataJSONStr);
				msg.setText(perfLogDataJSONStr);
				producer.send(msg);
				
				// End Time
				endTime = System.currentTimeMillis();
				logger.debug("PerfLoggerImplJMSQ End Time: " + endTime + "(ms)");
				// Elapsed Duration
				elapsedDuration = endTime-startTime;
				logger.debug("Elapsed time: " + elapsedDuration +"(ms)");
				
				logger.debug("Succesfully sent JMS Message with ID : " + msg.getJMSMessageID() + " to the Queue : " + cachedQDestination + " : " + perfLogData.toString());
				 
			} catch (Exception jmsException) 
			{
				// write a short version of the perf log data to log along with exception
				// this can happen when Q is full or other messaging error.
				logger.error("Error writing JMS Q:"	+ jmsException.getMessage() + ": " + perfLogData.toString());
			} finally 
			{
				try {
					if (session != null) 
					{
						session.close();
					}
				} catch (JMSException e)
				{
					logger.error(e.getMessage(), e);
				}
				try 
				{
					if (connection != null)
						connection.close();
				} catch (JMSException e) 
				{
					logger.error(e.getMessage(), e);
				}

			}

		} else if (getLogEnabled(perfLogData.getTransactionTime())) 
		{
			// log to stdout using default perf logger implementation
			// if there is an issue with getting JMS Resources
			defaultPerfLogger.log(perfLogData);
		}
	}

}
