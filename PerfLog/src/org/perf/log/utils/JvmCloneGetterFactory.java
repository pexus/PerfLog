/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/utils/JvmCloneGetterFactory.java 
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

public class JvmCloneGetterFactory {
	static JvmCloneGetter jvmCloneGetterImplInstance = null;
	static Object syncObject = new Object();

	public static JvmCloneGetter getJvmCloneGetterImpl() {
		if (jvmCloneGetterImplInstance == null) {
			synchronized (syncObject) {
				if (jvmCloneGetterImplInstance == null) {
					try {
						String jvmCloneImplGetterClassStr = RuntimeEnvProperties
								.getJvmCloneGetterImplClass();
						jvmCloneGetterImplInstance = (JvmCloneGetter) Class
								.forName(jvmCloneImplGetterClassStr)
								.newInstance();

					} catch (IllegalAccessException e) {
						System.out.println("IllegalAccessException:"
								+ e.getMessage());
						jvmCloneGetterImplInstance = new DefaultJvmCloneGetterImpl();

					} catch (InstantiationException e) {
						System.out.println("InstantiationException:"
								+ e.getMessage());
						jvmCloneGetterImplInstance = new DefaultJvmCloneGetterImpl();
					} catch (ClassNotFoundException e) {
						System.out.println("ClassNotFoundException:"
								+ e.getMessage());
						jvmCloneGetterImplInstance = new DefaultJvmCloneGetterImpl();
					}
				}
			}
		}
		return jvmCloneGetterImplInstance;

	}
}
