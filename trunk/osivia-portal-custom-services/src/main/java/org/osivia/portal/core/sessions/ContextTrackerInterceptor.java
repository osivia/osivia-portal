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
package org.osivia.portal.core.sessions;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.osivia.portal.core.tracker.ITracker;


/**
 * Modificaion liée au multithreads : on ne passe plus par un threadlocal mais on stocker les contexte directement en session
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11077 $
 */


public class ContextTrackerInterceptor extends PortletInvokerInterceptor
{
	
   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
	   String contextPath = (String)invocation.getDispatchedRequest().getAttribute("javax.servlet.include.context_path");
	   
 	  ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");
 	  
 	  if( ctx != null){
 	  
 	  HttpSession session = ctx.getServerInvocation().getServerContext().getClientRequest().getSession();
 	  
 	  Set contexts = (Set)session.getAttribute("org.jboss.portal.session.contexts");
 	  
      if (contexts == null)
      {
         contexts = new HashSet();
         session.setAttribute("org.jboss.portal.session.contexts", contexts);
      }
 	  
 	  
 	  
 	  
 	  if( !contexts.contains(contextPath))
 		  contexts.add(contextPath);
 	  }
 	 
 	  
      // Add it to the request context path set
 	  else
 		  org.jboss.portal.server.aspects.server.SignOutInterceptor.getSet().add(contextPath);

      // Invoke next command
      return super.invoke(invocation);
   }
}
