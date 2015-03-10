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
package org.osivia.portal.core.portlets.interceptors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.portlet.WindowContextImpl;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.EncodedParams;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;

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
	          
          
              /* Menubar */            
             if(cr.getAttributes().get(Constants.PORTLET_ATTR_MENU_BAR) != null) {
                 List<MenubarItem> menubarItems = (List<MenubarItem>) cr.getAttributes().get(Constants.PORTLET_ATTR_MENU_BAR);
                 if( menubarItems.size() > 0)   {
                     boolean windowMaximized = WindowState.MAXIMIZED.equals(invocation.getWindowState());                     
                     boolean PageMaximized = BooleanUtils.isTrue((Boolean) ctx.getAttribute(Scope.REQUEST_SCOPE,
                             "osivia.portal.maximized"));
                     
                     boolean portletMenubar = false;
                     
                     
                     if( !windowMaximized){
                         // It can also be a menu bar
                         
                         PortalObjectId poid = PortalObjectId.parse(invocation.getWindowContext().getId(), PortalObjectPath.CANONICAL_FORMAT);
                     
                         Window window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);
                         portletMenubar = StringUtils.equals(window.getName(), InternalConstants.PORTAL_MENUBAR_WINDOW_NAME); 
                     }
                     
                     // Test has been duplicated because of cache of portletmenuBar
                     // that is already present in case of maximisation
                     
                     if (windowMaximized || (!PageMaximized && portletMenubar)) {

                            // Update menubar urls

                            String pageMarker = PageMarkerUtils.getCurrentPageMarker(ctx);
                            List<MenubarItem> newMenuBar = new ArrayList<MenubarItem>();
                            for (MenubarItem item : menubarItems) {
                                MenubarItem newItem = item.clone();
                                if (StringUtils.contains(item.getUrl(), "/pagemarker/")) {

                                    newItem.setUrl(item.getUrl().replaceAll("/pagemarker/([0-9]*)/", "/pagemarker/" + pageMarker + "/"));
                                }
                                if (StringUtils.contains(item.getOnClickEvent(), "/pagemarker/")) {
                                    newItem.setOnClickEvent(item.getOnClickEvent().replaceAll("/pagemarker/([0-9]*)/", "/pagemarker/" + pageMarker + "/"));
                                }
                                newMenuBar.add(newItem);
                            }
 
                         
                          ctx.setAttribute(Scope.REQUEST_SCOPE, Constants.PORTLET_ATTR_MENU_BAR, newMenuBar);
                     }
                 }
              }


	    }
	    
	    
	    /* Dynamic properties */
	    
	    
		if ((response instanceof FragmentResponse) && (invocation instanceof RenderInvocation)) {
			
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
