/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.api.context;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * The Class PortalControllerContext.
 * 
 * describes the calling context to portal services 
 */
public class PortalControllerContext {
	
    /** The controller ctx. */
    Object controllerCtx;
	
	/** The request. */
	PortletRequest request;
	
	/** The response. */
	PortletResponse response;
	
	/** The portlet ctx. */
	PortletContext portletCtx;
	

	/**
	 * Instantiates a new portal controller context.
	 *
	 * @param portletCtx the portlet ctx
	 * @param request the request
	 * @param response the response
	 */
	public PortalControllerContext(PortletContext portletCtx, PortletRequest request, PortletResponse response) {
		super();
    	this.controllerCtx = request.getAttribute("osivia.controller");
		this.request = request;
		this.response = response;
		this.portletCtx = portletCtx;

	}

	/**
	 * Instantiates a new portal controller context.
	 * Non portlet context (portal, servlet,...)
	 * Will be adapted at portal level
	 *
	 * @param controllerCtx the controller ctx
	 */
	public PortalControllerContext(Object controllerCtx) {
		this.controllerCtx = controllerCtx;	
	}


	/**
	 * Gets the controller ctx.
	 *
	 * @return the controller ctx
	 */
	public Object getControllerCtx() {
		return controllerCtx;
	}


	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public PortletRequest getRequest() {
		return request;
	}


	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public PortletResponse getResponse() {
		return response;
	}


	/**
	 * Gets the portlet ctx.
	 *
	 * @return the portlet ctx
	 */
	public PortletContext getPortletCtx() {
		return portletCtx;
	}
	
}
