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
import org.jboss.portal.identity.User;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.UpdateNavigationalStateResponse;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.osivia.portal.api.profiler.IProfilerService;




/**
 * Infos de profiling pour les portlets
 */


public class ProfilerPortletInterceptor extends PortletInvokerInterceptor{
	
	private static Log logger = LogFactory.getLog(ProfilerPortletInterceptor.class);
	
	private transient IProfilerService profiler;
	
	public IProfilerService getProfiler() {
		return profiler;
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
		
				profiler.logEvent("PORTLET",invocation.getWindowContext().getId() , elapsedTime, error);	
			}
			
		}
	}
	

}
