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


public class PortalControllerContext {
	
    Object controllerCtx;
	PortletRequest request;
	PortletResponse response;
	PortletContext portletCtx;
	

	public PortalControllerContext(PortletContext portletCtx, PortletRequest request, PortletResponse response) {
		super();
    	this.controllerCtx = request.getAttribute("osivia.controller");
		this.request = request;
		this.response = response;
		this.portletCtx = portletCtx;

	}

	// Non portlet context (portal, servlet,...)
	// Will be adapted at portal level
	public PortalControllerContext(Object controllerCtx) {
		this.controllerCtx = controllerCtx;	
	}


	public Object getControllerCtx() {
		return controllerCtx;
	}


	public PortletRequest getRequest() {
		return request;
	}


	public PortletResponse getResponse() {
		return response;
	}


	public PortletContext getPortletCtx() {
		return portletCtx;
	}
	
}
