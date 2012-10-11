/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/utils/RequestResponseUtils.java 
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

import java.util.Enumeration;
import java.util.Map;


import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.filter.ActionResponseWrapper;
import javax.portlet.filter.EventResponseWrapper;
import javax.portlet.filter.RenderResponseWrapper;
import javax.portlet.filter.ResourceResponseWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;

public class RequestResponseUtils

{
	static Logger logger = LoggerFactory.getLogger("RequestResponseUtils");
	public static void printHttpServletRequestRelatedInfo(
			HttpServletRequest httpServletRequest) {
		logger
				.debug("------- Begin Information stored at HttpServlet level -------");

		printHttpServletRequestParameter(httpServletRequest);
		printHttpServletRequestAttribute(httpServletRequest);
		// printHttpSessionAttribute(httpServletRequest);
		logger
				.debug("------- End Information stored at HttpServlet level -------");
	}

	public static void printHttpServletRequestRelatedInfo(
			PortletRequest portletRequest) {
		logger
				.debug("------- Begin Information stored at HttpServlet level -------");
		HttpServletRequest httpServletRequest = getHttpServletRequest(portletRequest);
		printHttpServletRequestParameter(httpServletRequest);
		printHttpServletRequestAttribute(httpServletRequest);
		printHttpSessionAttribute(httpServletRequest);
		logger
				.debug("------- End Information stored at HttpServlet level -------");
	}

	public static void printPortletRelatedInfo(PortletRequest portletRequest) {
		logger
				.debug("------- Begin Information stored at portlet level -------");
		logger.debug("Portlet Mode  " + portletRequest.getPortletMode());
		logger.debug("Window State  " + portletRequest.getWindowState());
		logger.debug("Window ID  " + portletRequest.getWindowID());

		printPortletRequestParameter(portletRequest);

		printPortletRequestAttribute(portletRequest);

		printPortletPreferences(portletRequest);

		printPortletSessionInfo(portletRequest);

		printPortletProperties(portletRequest);
		logger.debug("------- End Information stored at portlet level -------");
	}

	public static void printPortletProperties(PortletRequest portletRequest) {
		logger.debug("------- Printing Portlet Parameters -------");
		Enumeration paramEnum = portletRequest.getPropertyNames();
		while (paramEnum.hasMoreElements()) {
			String paramName = (String) paramEnum.nextElement();
			String paramValue = portletRequest.getProperty(paramName);
			logger.debug(paramName + " -> " + paramValue);
		}
	}

	public static void printPortletRequestParameter(
			PortletRequest portletRequest) {
		logger.debug(" ------- Printing Portlet Parameters -------");
		Enumeration paramEnum = portletRequest.getParameterNames();
		while (paramEnum.hasMoreElements()) {
			String paramName = (String) paramEnum.nextElement();
			String paramValue = portletRequest.getParameter(paramName);
			logger.debug(paramName + " -> " + paramValue);
		}
	}

	public static void printPortletRequestAttribute(
			PortletRequest portletRequest) {
		logger.debug("------- Printing Portlet Attributes -------");
		Enumeration attributeEnum = portletRequest.getAttributeNames();
		while (attributeEnum.hasMoreElements()) {
			String attrName = (String) attributeEnum.nextElement();
			Object attrValue = portletRequest.getAttribute(attrName);
			logger.debug(attrName + " -> " + attrValue);
		}
	}

	public static void printPortletPreferences(PortletRequest portletRequest) {
		logger.debug(" ------- Printing PortletPreferences -------");
		PortletPreferences pref = portletRequest.getPreferences();
		Enumeration prefEnum = pref.getNames();
		while (prefEnum.hasMoreElements()) {
			String prefName = (String) prefEnum.nextElement();
			String prefValue = pref.getValue(prefName, "");
			logger.debug(prefName + " -> " + prefValue);
		}
	}

	public static void printPortletSessionInfo(PortletRequest portletRequest) {
		logger.debug("------- Printing PortletSession attributes -------");
		PortletSession session = portletRequest.getPortletSession();
		Enumeration attribEnum = session.getAttributeNames();
		while (attribEnum.hasMoreElements()) {
			String attrName = (String) attribEnum.nextElement();
			Object attrValue = session.getAttribute(attrName);
		}
	}

	public static HttpServletRequest getHttpServletRequest(
			PortletRequest request) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		while (httpServletRequest instanceof HttpServletRequestWrapper) {
			HttpServletRequestWrapper httpServletRequestWrapper = (HttpServletRequestWrapper) httpServletRequest;
			httpServletRequest = (HttpServletRequest) httpServletRequestWrapper
					.getRequest();
		}
		return httpServletRequest;
	}

	public static void printHttpServletRequestParameter(
			HttpServletRequest httpServletRequest) {
		logger.debug("------- Printing HttpServletRequest parameter -------");
		Enumeration paramEnum = httpServletRequest.getParameterNames();
		while (paramEnum.hasMoreElements()) {
			String paramName = (String) paramEnum.nextElement();
			String paramValue = httpServletRequest.getParameter(paramName);
			logger.debug(paramName + " -> " + paramValue);
		}
	}

	public static void printHttpServletRequestAttribute(
			HttpServletRequest httpServletRequest) {
		logger.debug("------- Printing HttpServletRequest attributes -------");
		Enumeration attrEnum = httpServletRequest.getAttributeNames();
		while (attrEnum.hasMoreElements()) {
			String attrName = (String) attrEnum.nextElement();
			Object attrValue = httpServletRequest.getAttribute(attrName);
			logger.debug(attrName + " -> " + attrValue);
		}
	}

	public static void printHttpSessionAttribute(
			HttpServletRequest httpServletRequest) {
		logger.debug("------- Printing HttpSession attributes -------");
		HttpSession httpSession = httpServletRequest.getSession();
		Enumeration attrEnum = httpSession.getAttributeNames();
		while (attrEnum.hasMoreElements()) {
			String attrName = (String) attrEnum.nextElement();
			Object attrValue = httpSession.getAttribute(attrName);
			logger.debug(attrName + " -> " + attrValue);
		}
	}

	private static ServletResponse getServletResponse(PortletResponse response) {

		if (response instanceof ServletResponse)
			return (ServletResponse) response;
		else if (response instanceof ServletResponseWrapper)
			return ((ServletResponseWrapper) response).getResponse();
		else if (response instanceof ActionResponseWrapper)
			return (ServletResponse) ((ActionResponseWrapper) response)
					.getResponse();
		else if (response instanceof RenderResponseWrapper)
			return (ServletResponse) ((RenderResponseWrapper) response)
					.getResponse();
		else if (response instanceof ResourceResponseWrapper)
			return (ServletResponse) ((ResourceResponseWrapper) response)
					.getResponse();
		else if (response instanceof EventResponseWrapper)
			return (ServletResponse) ((EventResponseWrapper) response)
					.getResponse();
		else
			return null;
	}

	public static void printDebugInformation(PortletRequest request,
			PortletResponse response, Map<String, String> portletInfoMap) {

		Enumeration<String> requestParam = request.getParameterNames();
		while (requestParam.hasMoreElements()) {
			String paramName = requestParam.nextElement();
			String paramValue = request.getParameter(paramName);
			logger.debug("Request Param " + paramName + " -> " + paramValue);
		}
	}

}
