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
import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;

/**
 * The Class PortalControllerContext.
 * 
 * describes the calling context to portal services 
 */
public class PortalControllerContext {
	
    /** The controller ctx. */
    private ControllerContext controllerCtx;
	
	/** The request. */
    private PortletRequest request;
	
	/** The response. */
    private PortletResponse response;
	
	/** The portlet ctx. */
    private PortletContext portletCtx;
	

	/**
	 * Instantiates a new portal controller context.
	 *
	 * @param portletCtx the portlet ctx
	 * @param request the request
	 * @param response the response
	 */
	public PortalControllerContext(PortletContext portletCtx, PortletRequest request, PortletResponse response) {
		super();
		this.request = request;
		this.response = response;
		this.portletCtx = portletCtx;

        // Controller context
		if(request != null) {
	        Object controller = request.getAttribute("osivia.controller");
	        if (controller != null && controller instanceof ControllerContext) {
	            this.controllerCtx = (ControllerContext) controller;
	        }
		}
	}


	/**
	 * Instantiates a new portal controller context.
	 * Non portlet context (portal, servlet,...)
	 * Will be adapted at portal level
	 *
	 * @param controllerCtx the controller ctx
	 */
	public PortalControllerContext(Object controllerCtx) {
        if (controllerCtx instanceof ControllerContext) {
            this.controllerCtx = (ControllerContext) controllerCtx;
        }
	}


    /**
     * Get HTTP servlet request.
     * 
     * @return HTTP servlet request
     */
    public HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = null;
        if (this.controllerCtx != null) {
            ServerInvocation serverInvocation = this.controllerCtx.getServerInvocation();
            if (serverInvocation != null) {
                ServerInvocationContext serverContext = serverInvocation.getServerContext();
                if (serverContext != null) {
                    request = serverContext.getClientRequest();
                }
            }
        }
        return request;
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
