/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/portal/RequestResponseUtils.java 
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
/*
 * Acknowledgement: 
 * Part of code in this file is derived from example code from Sunil Pant's 
 * WebSphere Portal blog at:
 * http://wpcertification.blogspot.com/2010/05/finding-out-unique-name-of-portlet-that.html
 * 
 */
package org.perf.log.filter.portal;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.portlet.ActionRequest;
import javax.portlet.EventRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.filter.ActionRequestWrapper;
import javax.portlet.filter.ActionResponseWrapper;
import javax.portlet.filter.EventRequestWrapper;
import javax.portlet.filter.EventResponseWrapper;
import javax.portlet.filter.RenderRequestWrapper;
import javax.portlet.filter.RenderResponseWrapper;
import javax.portlet.filter.ResourceRequestWrapper;
import javax.portlet.filter.ResourceResponseWrapper;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;


import com.ibm.portal.ModelException;
import com.ibm.portal.ObjectID;
import com.ibm.portal.content.ContentModel;
import com.ibm.portal.content.ContentNode;
import com.ibm.portal.content.ContentPage;
import com.ibm.portal.content.LayoutContainer;
import com.ibm.portal.content.LayoutControl;
import com.ibm.portal.content.LayoutModel;
import com.ibm.portal.content.LayoutNode;
import com.ibm.portal.model.ContentModelHome;
import com.ibm.portal.model.NavigationSelectionModelHome;
import com.ibm.portal.model.NavigationSelectionModelProvider;
import com.ibm.portal.model.PortletModelHome;
import com.ibm.portal.navigation.NavigationNode;
import com.ibm.portal.navigation.NavigationSelectionModel;
import com.ibm.portal.portletmodel.PortletDefinition;
import com.ibm.portal.portletmodel.PortletModel;
import com.ibm.portal.portletmodel.PortletWindow;

import java.util.*;

public class RequestResponseUtils

{
	static Logger logger = LoggerFactory.getLogger("RequestResponseUtils");
	private static NavigationSelectionModelHome navigationSelectionModelHome = null;
	private static ContentModelHome contentModelHome = null;
	private static PortletModelHome portletModelHome = null;

	static {
		try {
			InitialContext context = new InitialContext();
			navigationSelectionModelHome = (NavigationSelectionModelHome) context
					.lookup(NavigationSelectionModelHome.JNDI_NAME);
			contentModelHome = (ContentModelHome) context
					.lookup(ContentModelHome.JNDI_NAME);
			portletModelHome = (PortletModelHome) context
					.lookup(PortletModelHome.JNDI_NAME);
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static String getObjectIDStr(ObjectID objectID) {
		String temp = objectID.toString();
		int firstInd = temp.indexOf("'");
		String result = temp.substring(firstInd + 1, temp.indexOf("'",
				firstInd + 1));
		return result;
	}

	public static List getAllChildPortlets(LayoutNode subtreeRoot,
			LayoutModel lm) throws ModelException {
		List aList = new LinkedList();
		if (subtreeRoot instanceof LayoutContainer) {
			Iterator itr = lm.getChildren(subtreeRoot);
			while (itr.hasNext()) {
				Object o = itr.next();
				Iterator itr2 = getAllChildPortlets((LayoutNode) o, lm)
						.iterator();
				while (itr2.hasNext()) {
					Object o1 = itr2.next();
					aList.add(o1);
				}
			}
		} else {
			aList.add(subtreeRoot);
		}
		return aList;
	}

	private static List getAllChildLayoutContainer(LayoutNode subtreeRoot,
			LayoutModel lm) throws ModelException {
		List aList = new LinkedList();
		if (subtreeRoot instanceof LayoutContainer) {
			aList.add(subtreeRoot);
			Iterator itr = lm.getChildren(subtreeRoot);
			while (itr.hasNext()) {
				Object o = itr.next();
				Iterator itr2 = getAllChildLayoutContainer((LayoutNode) o, lm)
						.iterator();
				while (itr2.hasNext()) {
					Object o1 = itr2.next();
					aList.add(o1);
				}
			}
		}
		return aList;
	}

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

	private static ServletRequest getServletRequest(PortletRequest request) {

		if (request instanceof ServletRequest)
			return (ServletRequest) request;
		else if (request instanceof ServletRequestWrapper)
			return ((ServletRequestWrapper) request).getRequest();
		else if (request instanceof ActionRequestWrapper)
			return (ServletRequest) ((ActionRequestWrapper) request)
					.getRequest();
		else if (request instanceof RenderRequestWrapper)
			return (ServletRequest) ((RenderRequestWrapper) request)
					.getRequest();
		else if (request instanceof ResourceRequestWrapper)
			return (ServletRequest) ((ResourceRequestWrapper) request)
					.getRequest();
		else if (request instanceof EventRequestWrapper)
			return (ServletRequest) ((EventRequestWrapper) request)
					.getRequest();
		else
			return null;
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

	public static String getPageTitle(PortletRequest request,
			PortletResponse response) {

		ServletRequest servletRequest;
		ServletResponse servletResponse;
		if ((servletRequest = getServletRequest(request)) == null)
			return null;
		if ((servletResponse = getServletResponse(response)) == null)
			return null;

		try {

			NavigationSelectionModelProvider provider = navigationSelectionModelHome
					.getNavigationSelectionModelProvider();
			NavigationSelectionModel model = provider
					.getNavigationSelectionModel(servletRequest,
							servletResponse);
			NavigationNode navigationNode = (NavigationNode) model
					.getSelectedNode();
			ContentNode contentNode = navigationNode.getContentNode();
			/*************
			 * if (contentNode.getObjectID().getUniqueName() != null) {
			 * logger.debug(
			 * "###getPageTitle: The portlet is getting rendered on " +
			 * contentNode.getObjectID().getUniqueName()); } else {
			 * logger.debug(
			 * "###getPageTitle: The portlet is getting rendered on " +
			 * contentNode.getObjectID()); }
			 *****************/
			String pageTitle = contentNode.getTitle(request.getLocale());
			if (pageTitle == null) {
				pageTitle = contentNode.getTitle(new Locale("en"));
				if (pageTitle == null) {
					pageTitle = contentNode.getObjectID().getUniqueName();
					if (pageTitle == null)
						pageTitle = contentNode.getObjectID().toString();
				}
			}
			return pageTitle;

		} catch (ModelException e) {
			logger
					.debug("###getPageTitle: Error in PageNameFilter.getPageTitle() "
							+ e.getMessage());
		}

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

	private static String getPortletName(ServletRequest request,
			ServletResponse response, ContentPage contentPage,
			LayoutControl layoutControl) {
		
		try {
			PortletModel portletModel = portletModelHome
					.getPortletModelProvider().getPortletModel(contentPage,
							request, response);
			PortletWindow portletWindow = portletModel
					.getPortletWindow(layoutControl);
			// logger.debug("portletWindow = " + portletWindow);
			PortletDefinition portletDefinition = portletModel
					.getPortletDefinition(portletWindow);
			// logger.debug("portletDefinition = " + portletDefinition);
			// logger.debug("portletName (getObjectID) = " +
			// portletDefinition.getObjectID().toString());
			// logger.debug("portletName (uniqueName) = " +
			// portletDefinition.getObjectID().getUniqueName());
			Locale enLocale = new Locale("en");
			// logger.debug("portletName (getDescription) = " +
			// portletDefinition.getDescription(enLocale));
			// logger.debug("portletName (getTitle) = " +
			// portletDefinition.getTitle(enLocale));

			

			if (portletDefinition.getObjectID().getUniqueName() != null)
				return portletDefinition.getObjectID().getUniqueName();
			else if (portletDefinition.getTitle(enLocale) != null)
				return portletDefinition.getTitle(enLocale);
			else
				return portletDefinition.getObjectID().toString();
		} catch (ModelException e) {
			logger.error("Error in getPortletName() " + e.getMessage(),
					e);
		}
		return null;
	}

	public static String getPortletName(PortletRequest request,
			PortletResponse response) {
		ServletRequest servletRequest;
		ServletResponse servletResponse;
		if ((servletRequest = getServletRequest(request)) == null)
			return null;
		if ((servletResponse = getServletResponse(response)) == null)
			return null;

		try {

			if (navigationSelectionModelHome != null) {
				NavigationSelectionModelProvider provider = navigationSelectionModelHome
						.getNavigationSelectionModelProvider();
				NavigationSelectionModel model = provider
						.getNavigationSelectionModel(servletRequest,
								servletResponse);
				ContentModel contentModel = contentModelHome
						.getContentModelProvider().getContentModel(
								servletRequest, servletResponse);

				NavigationNode navigationNode = (NavigationNode) model
						.getSelectedNode();

				ContentNode contentNode = navigationNode.getContentNode();
				ContentPage contentPage = (ContentPage) contentModel
						.getLocator().findByID(contentNode.getObjectID());

				LayoutModel layoutModel = contentModel
						.getLayoutModel(contentPage);
				List portletList = getAllChildPortlets((LayoutNode) layoutModel
						.getRoot(), layoutModel);
				// logger.debug("####getPortletName:portletList.size=" +
				// portletList.size());

				String nameSpaceStr = response.getNamespace();
				// logger.debug("####getPortletName:nameSpaceStr=" +
				// nameSpaceStr);
				String nameSpacePortletId = nameSpaceStr.substring(3);
				// logger.debug("####getPortletName:nameSpacePorteletId=" +
				// nameSpacePortletId);

				nameSpacePortletId = nameSpacePortletId.substring(0,
						nameSpacePortletId.length() - 1);
				// logger.debug("####getPortletName:nameSpacePorteletId=" +
				// nameSpacePortletId);

				for (int i = 0; i < portletList.size(); i++) {
					LayoutControl container = (LayoutControl) portletList
							.get(i);
					String currentWindowObjectId = getObjectIDStr(container
							.getObjectID());
					// logger.debug("####getPortletName:currentWindowObjectId="
					// + currentWindowObjectId);
					if (nameSpacePortletId.equals(currentWindowObjectId)) {
						String portletName = getPortletName(servletRequest,
								servletResponse, contentPage, container);
						// logger.debug("####getPortletName:portletName=" +
						// portletName);
						return portletName;
					}
				}
			}
		} catch (ModelException e) {
			logger.error("Error in RequestResponseUtils.getPortletName() "
					+ e.getMessage(), e);

		}
		// logger.debug("####getPortletName:returning null");
		return null;
	}

	// util function to push portlet specific context information
	public static void pushPortletLogContext(PortletRequest request,
			PortletResponse response, String phase) {

		PerfLogContextHelper.pushInfoContext(PortletConstants.PORTAL_PAGE_NAME,
				RequestResponseUtils.getPageTitle(request, response));
		PerfLogContextHelper.pushInfoContext(PortletConstants.PORTAL_PORTLET_NAME,
				RequestResponseUtils.getPortletName(request, response));
		PerfLogContextHelper.pushInfoContext(PortletConstants.PORTAL_PHASE, phase);
		
		// Push request type data if available
		if(request instanceof ActionRequest) {
			//ActionRequest actionRequest = (ActionRequest)request;
			//PerfLogContextHelper.pushInfoContext("portletActionName", actionRequest.ACTION_NAME);			
		}
		else if(request instanceof EventRequest) {
			EventRequest eventRequest = (EventRequest)request;
			PerfLogContextHelper.pushInfoContext("portletEventName", eventRequest.getEvent().getName());
			PerfLogContextHelper.addToRequestDataContext("portletEventQName", eventRequest.getEvent().getQName().getNamespaceURI());
			
		}
		else if(request instanceof RenderRequest) {
			// RenderRequest renderRequest = (RenderRequest)request;
			// no useful data worth logging yet
		}
		else if(request instanceof ResourceRequest) {
			ResourceRequest resourceRequest = (ResourceRequest)request;
			PerfLogContextHelper.pushInfoContext("portletResourceID", resourceRequest.getResourceID());
		}

	}

}
