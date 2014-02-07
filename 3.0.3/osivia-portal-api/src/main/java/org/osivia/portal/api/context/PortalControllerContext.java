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
