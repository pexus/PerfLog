/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/struts1/Struts1PerfLogContextFilter.java 
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
package org.perf.log.filter.struts1;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.perf.log.context.PerfLogContext;


public interface Struts1PerfLogContextFilter {

	public void afterPerfLogContextCreation(HttpServletRequest request,
			HttpServletResponse response, Action action, ActionForm actionForm,
			ActionMapping actionMapping, PerfLogContext perfLogContext);

	public void beforePerfLogContextDeletion(HttpServletRequest request,
			HttpServletResponse response, Action action, ActionForm actionForm,
			ActionMapping actionMapping, PerfLogContext perfLogContext, Throwable t);
	
}
