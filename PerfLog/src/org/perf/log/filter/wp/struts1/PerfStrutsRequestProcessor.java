/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/struts1/wp/PerfStrutsRequestProcessor.java 
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
package org.perf.log.filter.wp.struts1;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.perf.log.context.PerfLogContext;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;


import com.ibm.portal.struts.portlet.WpRequestProcessor;

public class PerfStrutsRequestProcessor extends WpRequestProcessor {

	private final static Logger logger = LoggerFactory.getLogger("PerfStrutsRequestProcessor");

	
	private Struts1PerfLogContextFilter struts1PerfLogContextFilterImplClass;

	private boolean createPerfLogContext(HttpServletRequest request) {
		boolean logGUIDAndContextCreated = PerfLogContextHelper
				.startPerfLogTxnMonitor();

		if(request.getUserPrincipal()!=null)
			PerfLogContextHelper.setUserId(request.getUserPrincipal().getName());

		return logGUIDAndContextCreated;
	}

	private void deletePerfLogContext() {

		PerfLogContextHelper.endPerfLogTxnMonitor();

	}

	@Override
	protected ActionForward processActionPerform(HttpServletRequest request,
			HttpServletResponse response, Action action, ActionForm actionForm,
			ActionMapping actionMapping) throws IOException, ServletException {

		ActionForward forward = null;
		Throwable t = null;
		PerfLogContext perfLogContext = null;
		try {

			createPerfLogContext(request);
			perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			struts1PerfLogContextFilterImplClass.afterPerfLogContextCreation(request,
					response, action, actionForm, actionMapping, perfLogContext);

			forward = super.processActionPerform(request, response, action,
					actionForm, actionMapping);

		} catch (IOException e) {
			t = e;
			logger.error(e.getMessage());
			throw e;
		} catch (ServletException e) {
			t = e;
			logger.error(e.getMessage());
			throw e;
		} finally {

			struts1PerfLogContextFilterImplClass.beforePerfLogContextDeletion(
					request, response, action, actionForm, actionMapping,
					perfLogContext, t);
			deletePerfLogContext();
		}

		return forward;
	}

	@Override
	protected ActionMapping processMapping(HttpServletRequest request,
			HttpServletResponse response, String path) throws IOException {
		// TODO Auto-generated method stub
		return super.processMapping(request, response, path);
	}

	@Override
	public void init(ActionServlet servlet, ModuleConfig moduleConfig)
			throws ServletException {

		super.init(servlet, moduleConfig);
		struts1PerfLogContextFilterImplClass = new Struts1PerfLogContextFilterDefaultImpl();

	}

	public Struts1PerfLogContextFilter getStruts1PerfLogContextFilterImplClass() {
		return struts1PerfLogContextFilterImplClass;
	}

	public void setStruts1PerfLogContextFilterImplClass(
			Struts1PerfLogContextFilter struts1PerfLogContextFilterImplClass) {
		this.struts1PerfLogContextFilterImplClass = struts1PerfLogContextFilterImplClass;
	}

}
