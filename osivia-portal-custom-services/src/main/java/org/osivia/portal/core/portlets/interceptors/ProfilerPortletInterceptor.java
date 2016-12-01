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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.ActionInvocation;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.osivia.portal.api.log.LoggerMessage;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.core.error.IPortalLogger;




/**
 * Infos de profiling pour les portlets
 */


public class ProfilerPortletInterceptor extends PortletInvokerInterceptor{
	
	private static Log logger = LogFactory.getLog(ProfilerPortletInterceptor.class);
	
	private transient IProfilerService profiler;
	
	public IProfilerService getProfiler() {
		return this.profiler;
	}

	public void setProfiler(IProfilerService profiler) {
		this.profiler = profiler;
	}


	public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException	{
		
		long begin = System.currentTimeMillis();
		boolean error = false;


		boolean debug = false;
		
        if( invocation instanceof RenderInvocation || invocation instanceof ActionInvocation) 
            debug = true;

		
        if( debug && IPortalLogger.logger.isDebugEnabled()){
            IPortalLogger.logger.debug(new LoggerMessage("portlet enter "));
        }
		
		try	{

		PortletInvocationResponse response =  super.invoke(invocation);
		
		
		
		return response;
		
		} catch( PortletInvokerException e){
			error = true;
			throw e;
		} finally	{
		
            long end = System.currentTimeMillis();
            long elapsedTime = end - begin;

		    
			if( debug)	{
				
		
				this.profiler.logEvent("PORTLET",invocation.getWindowContext().getId() , elapsedTime, error);	
				
	            if( IPortalLogger.logger.isDebugEnabled()){
	                if( error == false)
	                    IPortalLogger.logger.debug(new LoggerMessage("portlet exit " + elapsedTime));
	                else
                        IPortalLogger.logger.debug(new LoggerMessage("portlet exit " + elapsedTime+ " \"an error as occured\""));
	            }				
			}
		}
	}
	

}
