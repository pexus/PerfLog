/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/portal/PerfPortletFilters.java 
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
package org.perf.log.filter.portal;

import java.io.IOException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;

import org.perf.log.context.PerfLogContext;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;

public class PerfPortletFilters implements ActionFilter, RenderFilter,
		ResourceFilter, EventFilter {
	
	private final static Logger logger = LoggerFactory.getLogger("PerfPortletFilters");
	String filterName;
	// initialized via init
	// use a default one if none provided...
	PortletPerfLogContextFilter portletPerfLogContextFilterImplClass;
	String portletPerfLogContextFilterImplClassString;
	
	
	private boolean createPerfLogContext(PortletRequest request) {
		boolean logGUIDAndContextCreated = PerfLogContextHelper
				.startPerfLogTxnMonitor();

		if(request.getUserPrincipal() != null) 
			PerfLogContextHelper.setUserId(request.getUserPrincipal().getName());

		return logGUIDAndContextCreated;
	}
	
	private void deletePerfLogContext() {

		PerfLogContextHelper.endPerfLogTxnMonitor();

	}
	
	
	@Override
	public void doFilter(ActionRequest request, ActionResponse response,
			FilterChain filterChain) throws IOException, PortletException {
		logger.debug("doFilter(Action):enter");
		
		Throwable t=null;
		PerfLogContext perfLogContext=null;		
		String phase = PortletConstants.PORTLET_PHASE_ACTION;
		
		try {
			createPerfLogContext(request);
			perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();			
			portletPerfLogContextFilterImplClass.afterPerfLogContextCreation(request, response, phase, perfLogContext);
			filterChain.doFilter(request, response);
		}
		catch (IOException e) {
			t = e;
			logger.error(e.getMessage(),e);
			//TODO - print log context debug details..
			throw e;
		}
		catch (PortletException e) {
			t = e;
			logger.error(e.getMessage(),e);			
			throw e;
		}
		finally {
			
			portletPerfLogContextFilterImplClass.beforePerfLogContextDeletion(request, response, phase, perfLogContext, t);
			deletePerfLogContext();			
		}

	}

	@Override
	public void destroy() {
		logger.debug("destroy:filterName="+filterName);
	}

	@Override
	public void init(FilterConfig filterConfig) throws PortletException {

		filterName = filterConfig.getFilterName();
		portletPerfLogContextFilterImplClassString = filterConfig
				.getInitParameter("portletPerfLogContextImplClass");
		if (portletPerfLogContextFilterImplClassString == null) {
			portletPerfLogContextFilterImplClass = new PortletPerfLogContextFilterDefaultImpl();
		} else {

			try {
				portletPerfLogContextFilterImplClass = (PortletPerfLogContextFilter) Thread
					.currentThread().getContextClassLoader()
						.loadClass(portletPerfLogContextFilterImplClassString)
							.newInstance();
			} catch (Exception e) {
				logger.error(e.getMessage()+":using default implementation - PortletPerfLogContextFilterDefaultImpl");
				portletPerfLogContextFilterImplClass = new PortletPerfLogContextFilterDefaultImpl();
			}
		}
		logger.debug("init:filterName=" + filterName);
	}

	@Override
	public void doFilter(RenderRequest request, RenderResponse response,
			FilterChain filterChain) throws IOException, PortletException {
		logger.debug("doFilter(Render):enter");	
		Throwable t=null;
		PerfLogContext perfLogContext=null;		
		String phase = PortletConstants.PORTLET_PHASE_RENDER;
		try {
			createPerfLogContext(request);
			perfLogContext=PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			portletPerfLogContextFilterImplClass.afterPerfLogContextCreation(request, response, phase, perfLogContext);
			
			filterChain.doFilter(request, response);
		}
		catch (IOException e) {
			t=e;
			logger.error(e.getMessage(),e);
			throw e;
		}
		catch (PortletException e) {
			t=e;
			logger.error(e.getMessage(),e);
			throw e;
		}
		finally {
			portletPerfLogContextFilterImplClass.beforePerfLogContextDeletion(request, response, phase, perfLogContext, t);
			deletePerfLogContext();
		}

	}

	@Override
	public void doFilter(ResourceRequest request, ResourceResponse response,
			FilterChain filterChain) throws IOException, PortletException {
		
		logger.debug("doFilter(Resource):enter");
		Throwable t=null;
		PerfLogContext perfLogContext=null;		
		String phase = PortletConstants.PORTLET_PHASE_RESOURCE;
		try {
			createPerfLogContext(request);
			perfLogContext=PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			portletPerfLogContextFilterImplClass.afterPerfLogContextCreation(request, response, phase, perfLogContext);
			filterChain.doFilter(request, response);
		}
		catch (IOException e) {
			t=e;
			logger.error(e.getMessage(),e);
			throw e;
		}
		catch (PortletException e) {
			t=e;
			logger.error(e.getMessage(),e);
			throw e;
		}
		finally {
			portletPerfLogContextFilterImplClass.beforePerfLogContextDeletion(request, response, phase, perfLogContext, t);
			deletePerfLogContext();
		}

	}

	@Override
	public void doFilter(EventRequest request, EventResponse response, FilterChain filterChain)
			throws IOException, PortletException {
		logger.debug("doFilter(Event):enter:");
		Throwable t=null;
		PerfLogContext perfLogContext=null;		
		String phase = PortletConstants.PORTLET_PHASE_EVENT;
		try {
			createPerfLogContext(request);
			perfLogContext=PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			portletPerfLogContextFilterImplClass.afterPerfLogContextCreation(request, response, phase, perfLogContext);
			
			filterChain.doFilter(request, response);
		} 
		catch (IOException e) {
			t = e;
			logger.error(e.getMessage(),e);
			throw e;
		}
		catch (PortletException e) {
			t = e;
			logger.error(e.getMessage(),e);
			throw e;
		}
		finally {
			portletPerfLogContextFilterImplClass.beforePerfLogContextDeletion(request, response, phase, perfLogContext, t);
			deletePerfLogContext();
		}

		
	}

	public PortletPerfLogContextFilter getPortletPerfLogContextFilterImplClass() {
		return portletPerfLogContextFilterImplClass;
	}

	public void setPortletPerfLogContextFilterImplClass(
			PortletPerfLogContextFilter portletPerfLogContextFilterImplClass) {
		this.portletPerfLogContextFilterImplClass = portletPerfLogContextFilterImplClass;
	}

}
