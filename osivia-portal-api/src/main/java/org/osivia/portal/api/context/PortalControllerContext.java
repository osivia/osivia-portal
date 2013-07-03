package org.osivia.portal.api.context;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.jboss.portal.core.controller.ControllerContext;

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

	// Non portlet context (portal request)
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
