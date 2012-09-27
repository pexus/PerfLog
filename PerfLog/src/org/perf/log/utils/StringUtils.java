/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/utils/StringUtils.java 
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

public class StringUtils {

	public static String truncateString(String inStr, int maxValue) {
		if(inStr == null) return null;
		int endIndex = inStr.length();
		if(inStr.length()>maxValue) { 
			endIndex = maxValue - 3;
		}
		String outStr = inStr.substring(0,endIndex);
		if(inStr.length()>maxValue)
			outStr += "..."; // indicate the value was chopped - add 3 to length of the out string
		return outStr;
	}

}
