/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/servlet/ServletPerfLogContextFilter.java 
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
package org.perf.log.filter.servlet;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.perf.log.context.PerfLogContext;


public interface ServletPerfLogContextFilter {

	public void beforePerfLogContextDeletion(ServletRequest request,
			ServletResponse response, PerfLogContext perfLogContext, Throwable t);

	public void afterPerfLogContextCreation(ServletRequest request,
			ServletResponse response, PerfLogContext perfLogContext);

}
