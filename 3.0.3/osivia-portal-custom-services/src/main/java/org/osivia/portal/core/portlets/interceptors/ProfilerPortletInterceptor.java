package org.osivia.portal.core.portlets.interceptors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.osivia.portal.api.profiler.IProfilerService;




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


		try	{

		PortletInvocationResponse response =  super.invoke(invocation);
		
			
		
		return response;
		
		} catch( PortletInvokerException e){
			error = true;
			throw e;
		} finally	{
			
			if( invocation instanceof RenderInvocation)	{
				
				long end = System.currentTimeMillis();
				long elapsedTime = end - begin;
		
				this.profiler.logEvent("PORTLET",invocation.getWindowContext().getId() , elapsedTime, error);	
			}
			
		}
	}
	

}
