/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/utils/TomcatJvmCloneGetterImpl.java 
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

import java.lang.management.ManagementFactory;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

public class TomcatJvmCloneGetterImpl implements JvmCloneGetter {
	private static final String objectName = "Catalina:type=Engine";
	private static final String attributeNames = "name,jvmRoute";

	@Override
	public String getName() {
		MBeanServerConnection conn = ManagementFactory.getPlatformMBeanServer();
		String cloneNameStr = "";
		ObjectName on;
		try {

			if (objectName != null) {
				on = new ObjectName(objectName);
				// Get the attribute names

				if (attributeNames != null) {
					String names[] = attributeNames.split(",");

					for (String name : names) {
						String value = (String) conn.getAttribute(on, name);
						;
						if (value != null) {
							if (!cloneNameStr.equals(""))
								cloneNameStr += "_";
							cloneNameStr += value;
						}
					}

				}
			}
		} catch (Exception e) {
			System.out.println("org.perf.log.RuntimeEnvHelper: Exception : "
					+ e.getMessage());
		}

		if (cloneNameStr == null
				|| (cloneNameStr != null && cloneNameStr.equals("")))
			cloneNameStr = ManagementFactory.getRuntimeMXBean().getName();
		return cloneNameStr;
	}

}
