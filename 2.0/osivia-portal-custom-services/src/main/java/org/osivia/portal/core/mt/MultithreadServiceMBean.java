/**
 * 
 */
package org.osivia.portal.core.mt;

import org.jboss.system.ServiceMBean;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.core.mt.IMultithreadService;
import org.osivia.portal.core.tracker.ITracker;



public interface MultithreadServiceMBean extends ServiceMBean, IMultithreadService {

	public void startService()throws Exception;
	public void stopService()throws Exception;	
	
	public ITracker getTracker();
	public void setTracker(ITracker tracker) ;
	
	public IProfilerService getProfiler() ;
	public void setProfiler(IProfilerService profiler) ;
	

}
