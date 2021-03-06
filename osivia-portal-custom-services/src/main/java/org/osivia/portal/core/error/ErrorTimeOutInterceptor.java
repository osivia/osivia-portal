/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.osivia.portal.core.error;

import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.aspects.portlet.cache.ContentRef;
import org.jboss.portal.portlet.aspects.portlet.cache.StrongContentRef;
import org.jboss.portal.portlet.invocation.response.ErrorResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.RevalidateMarkupResponse;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.spi.UserContext;
import org.jboss.portal.portlet.cache.CacheControl;
import org.jboss.portal.common.util.ParameterMap;

import org.jboss.portal.WindowState;
import org.jboss.portal.Mode;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.tracker.TrackerBean;


import java.io.Serializable;
import java.util.Map;
import java.util.Stack;

/**
 * Permet d'éviter que pour les threads les erreurs en timeout ne tombent dans le mécanisme standard
 * (en général, elle n'a pas de sens et de plus elles ne sont pas signalées à l'utilisateur)
 * 
 * NON UTILISE POUR L'INSTANT !!!!
 */
public class ErrorTimeOutInterceptor extends PortletInvokerInterceptor
{
	
	private ITracker tracker;

	public ITracker getTracker() {
		return tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}


   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
	   try	{

         // Invoke
         return super.invoke(invocation);
         
	   } catch( Exception e)	{
		   // On regarde si parent (ie il s'agit d'un thread)
		   TrackerBean parent = (TrackerBean) getTracker().getParentBean();
		   if( parent != null)	{
			   Stack stack =  parent.getStack();
			   
			   if( stack != null || stack.size() == 0)	{
				   // Si aucune commande dans la stack, la requête est termminée
				   return new ErrorResponse("Timeout");
			   }
				   
		   }
		   
		   try {
			throw( e);
		   } catch (Exception e1) {
		   }
	   }
	   
	   return null;

   }

  
}
