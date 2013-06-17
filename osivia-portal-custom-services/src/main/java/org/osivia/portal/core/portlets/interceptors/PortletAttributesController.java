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
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.UpdateNavigationalStateResponse;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.core.page.EncodedParams;

import bsh.Interpreter;




/**
 * Permet de traiter les attributs renvoyés par les portlets
 */


public class PortletAttributesController extends PortletInvokerInterceptor{
	
	private static Log logger = LogFactory.getLog(PortletAttributesController.class);
	

	public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException	{
		
		
		PortletInvocationResponse response =  super.invoke(invocation);
		
		/* Empty response */
		
    	// Avoid JSF class cast
	    if( invocation.getWindowContext() instanceof WindowContextImpl)	{
		
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
	    	
            /* Empty portlet */	    	
	          if(cr.getAttributes().get("osivia.popupCallbackUrl") != null) {
	              
	                if( invocation.getWindowContext() instanceof WindowContextImpl)  {
	                    String windowId = invocation.getWindowContext().getId();
	                    windowId = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT);
	                    ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupCallbackUrl"+windowId, cr.getAttributes().get("osivia.popupCallbackUrl"));
	                }
	          }

	    }
	    
	    
	    /* Dynamic properties */
	    
	    
		if (response instanceof FragmentResponse && invocation instanceof RenderInvocation) {
			
			   ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");
	    	
	    	   
 	    	   
 	    	   String windowId = invocation.getWindowContext().getId();
	    	   PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);

			   Window window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);
	    	   
	    	   
			   String safestWindowId = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT);
			   
			   
 	    	   Map<String,String> windowProperties = new HashMap<String, String>();
 	    	   ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.windowProperties."+safestWindowId, windowProperties);	
 	    	   
 				/* Styles dynamiques */

					RenderInvocation renderInvocation = (RenderInvocation) invocation;

					// Dynamic styles
					
					if ("1".equals(window.getDeclaredProperty("osivia.bshActivation"))) {
						
						FragmentResponse fr = (FragmentResponse) response;
						
						String updatedFragment = fr.getChars();

						try {
							String script = window.getDeclaredProperty("osivia.bshScript");

							// Evaluation beanshell
							Interpreter i = new Interpreter();

							Map<String, String[]> publicNavigationalState = renderInvocation.getPublicNavigationalState();

							i.set("pageParamsEncoder", new EncodedParams(publicNavigationalState));
							i.set("windowProperties", windowProperties);

							i.eval(script);

						} catch (bsh.EvalError e) {

							updatedFragment = e.getMessage();


						}
						
						
						return new FragmentResponse(fr.getProperties(), fr.getAttributes(), fr.getContentType(), fr.getBytes(), updatedFragment,
								fr.getTitle(), fr.getCacheControl(), fr.getNextModes());


					}
			    } // End of dynamic properties
		
	    }
		
		
				
				
				 	    	   
 	    	   
 	    	  
 	       

			
			
			
	    	
	    
	    
	    return response;
	}
	

}
