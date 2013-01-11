package org.osivia.portal.core.portlets.interceptors;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.aspects.server.UserInterceptor;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.PortalObjectPath.CanonicalFormat;
import org.jboss.portal.core.model.portal.portlet.WindowContextImpl;
import org.jboss.portal.identity.User;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.UpdateNavigationalStateResponse;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.osivia.portal.api.profiler.IProfilerService;




/**
 * Permet de traiter les attributs renvoyés par les portlets
 */


public class PortletAttributesController extends PortletInvokerInterceptor{
	
	private static Log logger = LogFactory.getLog(PortletAttributesController.class);
	

	public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException	{
		
		
		PortletInvocationResponse response =  super.invoke(invocation);
		
	    if (response instanceof ContentResponse){
	    	ContentResponse cr = (ContentResponse) response;
	    	
	    	ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");

			/* Empty portlet */
	    	
	    	if( "1".equals(cr.getAttributes().get("osivia.emptyResponse")))	{

	    		
	    		// Avoid JSF class cast
	    	       if( invocation.getWindowContext() instanceof WindowContextImpl)	{

	    	    	   String windowId = invocation.getWindowContext().getId();
	    	    	   windowId = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT);
	    	    	   ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.emptyResponse."+windowId, "1");
	    	       }

	    	}
	    }
	    
	    return response;
	}
	

}
