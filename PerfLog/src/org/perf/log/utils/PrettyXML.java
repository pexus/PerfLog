/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/utils/PrettyXML.java 
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
package org.perf.log.utils;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;

public class PrettyXML {
	private final static Logger logger = LoggerFactory.getLogger(PrettyXML.class.getName());

	public static String format(String xmlStr) {// Instantiate transformer input
		Source xmlInput = new StreamSource(new StringReader(xmlStr));
		StreamResult xmlOutput = new StreamResult(new StringWriter());

		// Configure transformer
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
			logger.error(e.getMessage(),e);
			return xmlStr;
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			return xmlStr;
		} // An identity transformer
		
		try {
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			transformer.transform(xmlInput, xmlOutput);
		} catch (TransformerException e) {
			logger.error(e.getMessage(),e);
			return xmlStr;
		}
		
		return xmlOutput.getWriter().toString();
		
	}

}
