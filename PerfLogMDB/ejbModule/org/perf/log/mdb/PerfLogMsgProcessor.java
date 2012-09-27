/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: ejbModule/org/perf/log/mdb/PerfLogMsgProcessor.java 
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
package org.perf.log.mdb;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.perf.log.logger.PerfLogData;
import org.perf.log.logger.DBWriter;
import org.perf.log.logger.FileWriter;;



/**
 * Message-Driven Bean implementation class for: PerfLogMsgProcessor
 * The sample implementation uses JNDI name 
 * jms/asPerfLog for activation spec and  jms/qPerfLog for Queue 
 * Note: The actual activation spec name and queue JNDI name is deployment specific
 * For WebSphere it is defined in ibm-ejb-jar-bnd.xml. Other implementation
 * may have vendor specific deployment files and assembly tools
 * 
 *
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"
		) }, 
		mappedName = "jms/qPerfLog")
public class PerfLogMsgProcessor implements MessageListener {

	private static Logger aLogger = Logger.getLogger(PerfLogMsgProcessor.class
			.getName());

	/**
	 * Default Empty constructor.
	 */
	public PerfLogMsgProcessor() {

	}

	/**
	 * Message Driven Bean(MDB) - onMessage() Implementation
	 * 
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message inMessage) {
		TextMessage textMessage = null;
		
		String messageContent = null;

		// Time Elapsed - MDB processing
		long mdbStartTime;

		mdbStartTime = System.currentTimeMillis();

		try {
			if (inMessage instanceof TextMessage) {
				textMessage = (TextMessage) inMessage;
				messageContent = textMessage.getText();
			} 
			else {
				aLogger.warning("JMS Message of wrong type, ignoring message: "
						+ inMessage.getClass().getName());
				return;
			}

			aLogger.finest("onMessage(): messageContent = " + messageContent);

			
			PerfLogData perfLogData = PerfLogData.fromJSON(messageContent);

			// Insert a PerfDB record in to Database
			if(PerfLogMDBProperties.isDbWriterEnabled())
				try {
					DBWriter.write(perfLogData);
				} catch (Exception e) {
					aLogger.severe("DBWriter Error writing to performance database:" + e.getMessage());
				}

			// Write the PerfDB entry in to a Log File
			if(PerfLogMDBProperties.isFileWriterEnabled())
				try {
					FileWriter.write(perfLogData);
				} catch (Exception e) {
					aLogger.severe("FileWriter Error writing to performance log file:" + e.getMessage());
				}

		} catch (JMSException jmsException) {
			aLogger.severe("JMS Exception occured : "
					+ jmsException.getMessage());

		}

		aLogger.fine("PerfLogProcessor - MDB Consumer Total elapsedtime :"
				+ (System.currentTimeMillis() - mdbStartTime) + "(ms)");
	}

}
