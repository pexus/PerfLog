/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/properties/URLResourceTunablePropertiesImpl.java 
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
package org.perf.log.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class URLResourceTunablePropertiesImpl extends
		CachedTunablePropertiesAbstract {
	public URLResourceTunablePropertiesImpl() {
		super();
		
	}	
	private static Properties props = new Properties();
	private static long lastPropertyFileLoadTime = 0;
	
	@Override
	public String getStaticProperty(String name) {
		// Abstract implementation method manages the property via
		// readTunableProperty implementation
		return getCachedTunableProperty(name, false);

	}

	@Override
	public String getDynamicProperty(String name) {
		// Abstract implementation method manages the property via
		// readTunableProperty implementation
		return getCachedTunableProperty(name, true);
	}

	// The following method only loads the properties
	// periodically to avoid too much overhead
	// We will use the same refresh interval defined for tunable properties
	private String getPropertyFromUrlResource(String name) {
		try {
			if (props == null
					|| (lastPropertyFileLoadTime == 0)
					|| ((System.currentTimeMillis() - lastPropertyFileLoadTime) >= LoggerProperties
							.getInstance().getTunablePropertyRefreshInterval())) {
				String urlStr  = LoggerProperties.getInstance().getTunablePropertiesImplUrlResource();
				URL url = new URL(urlStr);
				InputStream is = url.openStream();
				if (is != null) {
					props.load(is);
					lastPropertyFileLoadTime = System.currentTimeMillis();
					return props.getProperty(name);

				} else
					return null;
			} else
				// return from previous loaded property
				// until next refresh time
				return props.getProperty(name);
		} catch (MalformedURLException e) {
			System.out.println(e+ ": " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e+ ": " + e.getMessage());
			return null;
		}
	}

	@Override
	protected TunablePropertyValue readTunableProperty(String name) {

		if (LoggerProperties.getInstance().getTunablePropertiesImplUrlResource() == null)
			return null;

		String propVal = getPropertyFromUrlResource(name);

		if (propVal != null) {
			TunablePropertyValue tunablePropertyValue = new TunablePropertyValue();
			tunablePropertyValue.setValue(propVal);
			tunablePropertyValue.setDefined(true);
			tunablePropertyValue.setLastCheckTime(System.currentTimeMillis());
			return tunablePropertyValue;
		} else
			return null;

	}

}
