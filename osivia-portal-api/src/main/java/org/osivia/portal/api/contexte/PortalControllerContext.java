package org.osivia.portal.api.contexte;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.jboss.portal.core.controller.ControllerContext;

public class PortalControllerContext {
	
	ControllerContext controllerCtx;
	PortletRequest request;
	PortletResponse response;
	PortletContext portletCtx;
	

	public PortalControllerContext(PortletContext portletCtx, PortletRequest request, PortletResponse response) {
		super();
		this.controllerCtx = (ControllerContext) request.getAttribute("pia.controller");
		this.request = request;
		this.response = response;
		this.portletCtx = portletCtx;

	}

	public PortalControllerContext(ControllerContext ctx) {
		this.controllerCtx = ctx;	
	}


	public ControllerContext getControllerCtx() {
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
