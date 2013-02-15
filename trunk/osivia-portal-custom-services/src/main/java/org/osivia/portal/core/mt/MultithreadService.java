/**
 * 
 */
package org.osivia.portal.core.mt;

import java.io.Serializable;
import java.util.Collection;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.portlet.ControllerPageNavigationalState;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.system.ServiceMBeanSupport;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.core.tracker.ITracker;



public class MultithreadService extends ServiceMBeanSupport  implements
		MultithreadServiceMBean, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private transient ITracker tracker;
	private transient IProfilerService profiler;
	
	
	public IProfilerService getProfiler() {
		return profiler;
	}

	public void setProfiler(IProfilerService profiler) {
		this.profiler = profiler;
	}

	public ITracker getTracker() {
		return tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}
	
	
	private static final Log logger = LogFactory.getLog(MultithreadService.class);
	
	
   public ControllerResponse execute(Page page, ControllerContext context, Collection windows,  PortalLayout layout,PortalTheme theme,PageService pageService, ControllerPageNavigationalState pageNavigationalState) throws Exception	{
	   ServicesInvoker invoker = new ServicesInvoker( page,  context,  windows,   layout, theme, pageService, pageNavigationalState, getTracker(), getProfiler());
	   return invoker.render();
	   
   }


	public void startService()throws Exception{
		logger.info("Multithreading service starting !");
		InitialContext init = new InitialContext();
		init.bind("java:multithread", this);
	}
	
	public void stopService()throws Exception{
		InitialContext init = new InitialContext();
		init.unbind("java:multithread");
		
	}
}
