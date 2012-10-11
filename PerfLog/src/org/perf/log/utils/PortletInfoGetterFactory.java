/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/utils/PortletInfoGetterFactory.java 
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

import org.perf.log.properties.RuntimeEnvProperties;

public class PortletInfoGetterFactory {
	static PortletInfoGetter portletInfoGetterImplInstance = null;
	static Object syncObject = new Object();

	public static PortletInfoGetter getPortletInfoGetterImpl() {
		if (portletInfoGetterImplInstance == null) {
			synchronized (syncObject) {
				if (portletInfoGetterImplInstance == null) {
					try {
						String portletInfoGetterClassStr = RuntimeEnvProperties.getPortletInfoGetterImplClass();								;
						portletInfoGetterImplInstance = (PortletInfoGetter) Class
								.forName(portletInfoGetterClassStr)
								.newInstance();

					} catch (IllegalAccessException e) {
						System.out.println("IllegalAccessException:"
								+ e.getMessage());
						portletInfoGetterImplInstance = new DefaultPortletInfoGetterImpl();

					} catch (InstantiationException e) {
						System.out.println("InstantiationException:"
								+ e.getMessage());
						portletInfoGetterImplInstance = new DefaultPortletInfoGetterImpl();
					} catch (ClassNotFoundException e) {
						System.out.println("ClassNotFoundException:"
								+ e.getMessage());
						portletInfoGetterImplInstance = new DefaultPortletInfoGetterImpl();
					}
				}
			}
		}
		return portletInfoGetterImplInstance;

	}
}
