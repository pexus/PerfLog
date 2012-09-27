/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: ejbModule/org/perf/log/mdb/PerfLogMDBProperties.java 
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

/**
 * perfLogMDB properties file
 *  
 * @author Pradeep Nambiar 2/10/2012
 */

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class PerfLogMDBProperties {
	private static Logger logger  = Logger.getLogger(PerfLogMDBProperties.class.getName());
	
	// Property Names
	public static final String PROPERTY_FILE_WRITER_ENABLED = "perfLogMDB.fileWriteEnabled";
	public static final String PROPERTY_DB_WRITER_ENABLED = "perfLogMDB.dbWriterEnabled";
	
	static boolean fileWriterEnabled = true;
	static boolean dbWriterEnabled = true;
		
	private static boolean propertiesInited = false;
	
	static {
		initProperties();
	}
	
	private synchronized static void initProperties() {
		if (!propertiesInited) {
			try {
				InputStream in = PerfLogMDBProperties.class.getClassLoader()
						.getResourceAsStream("perfLogMDB.properties");
				String propVal;
				Properties props = new Properties();
				if (in != null) {
					props.load(in);
					 
					propVal = props
							.getProperty(PROPERTY_DB_WRITER_ENABLED);
					if (propVal != null)
						setDbWriterEnabled(new Boolean(propVal).booleanValue());
					
					propVal = props
							.getProperty(PROPERTY_FILE_WRITER_ENABLED);
					if (propVal != null)
						setFileWriterEnabled(new Boolean(propVal)
								.booleanValue());					
					
				} else {
					logger.warning("Error in reading perfLogMDB.properties");
				}

			} catch (Exception exception) {
				logger.severe(
						":Error Loading perfLogMDB.properties"
								+ exception.getMessage());
			}

			propertiesInited = true;
		}

	}	
	
	/**
	 * @return the fileWriterEnabled
	 */
	public static boolean isFileWriterEnabled() {
		return fileWriterEnabled;
	}

	/**
	 * @return the dbWriterEnabled
	 */
	public static boolean isDbWriterEnabled() {
		return dbWriterEnabled;
	}

	
	/**
	 * @param fileWriterEnabled the fileWriterEnabled to set
	 */
	public static void setFileWriterEnabled(boolean fileWriterEnabled) {
		PerfLogMDBProperties.fileWriterEnabled = fileWriterEnabled;
	}

	/**
	 * @param dbWriterEnabled the dbWriterEnabled to set
	 */
	public static void setDbWriterEnabled(boolean dbWriterEnabled) {
		PerfLogMDBProperties.dbWriterEnabled = dbWriterEnabled;
	}

	

}
