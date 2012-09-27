/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/servlet/PerfServletFilter.java 
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

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.perf.log.context.PerfLogContext;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;


/**
 * @author Pradeep Nambiar
 *
 */
public class PerfServletFilter implements Filter {

	private final static Logger logger = LoggerFactory.getLogger("PerfServletFilter");
	private String filterName;
	private String servletPerfLogContextFilterImplClassString;
	private ServletPerfLogContextFilter servletPerfLogContextFilterImplClass;
	
	private boolean createPerfLogContext(ServletRequest request) {
		boolean perfLogContextCreated = PerfLogContextHelper
				.startPerfLogTxnMonitor();
		if (request instanceof HttpServletRequest  &&
				((HttpServletRequest) request).getUserPrincipal()!=null)
			PerfLogContextHelper.setUserId(((HttpServletRequest) request).getUserPrincipal().getName());

		return perfLogContextCreated;
	}

	private void deletePerfLogContext() {

		PerfLogContextHelper.endPerfLogTxnMonitor();

	}

	@Override
	public void destroy() {

		logger.debug("destroy()");

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		

		Throwable t = null;
		PerfLogContext perfLogContext = null;
		try {

			logger.debug("doFilter()");
			createPerfLogContext(request);
			perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			servletPerfLogContextFilterImplClass.afterPerfLogContextCreation(request, response, perfLogContext);
			
		

			chain.doFilter(request, response);
		} catch (IOException e) {
			t = e;
			throw e;
		} catch (ServletException e) {
			t = e;
			throw e;
		} finally {
			servletPerfLogContextFilterImplClass.beforePerfLogContextDeletion(request, response, perfLogContext, t);
			deletePerfLogContext();
		}

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		logger.debug("init()");
		filterName = filterConfig.getFilterName();
		servletPerfLogContextFilterImplClassString = filterConfig
				.getInitParameter("servletPerfLogContextImplClass");
		if (servletPerfLogContextFilterImplClassString == null) {
			servletPerfLogContextFilterImplClass = new ServletPerfLogContextFilterDefaultImpl();
		} else {
			try {
				servletPerfLogContextFilterImplClass = (ServletPerfLogContextFilter) Thread
						.currentThread().getContextClassLoader()
						.loadClass(servletPerfLogContextFilterImplClassString)
						.newInstance();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				servletPerfLogContextFilterImplClass = new ServletPerfLogContextFilterDefaultImpl();
			}
		}
		logger.debug("init:filterName=" + filterName);

	}

	public ServletPerfLogContextFilter getServletPerfLogContextFilterImplClass() {
		return servletPerfLogContextFilterImplClass;
	}

	public void setServletPerfLogContextFilterImplClass(
			ServletPerfLogContextFilter servletPerfLogContextFilterImplClass) {
		this.servletPerfLogContextFilterImplClass = servletPerfLogContextFilterImplClass;
	}

}
