/**
 * 
 */
package org.osivia.portal.core.status;

import org.jboss.system.ServiceMBean;
import org.osivia.portal.api.status.IStatusService;





/**
 * @author jss
 *
 */
public interface StatusServiceMBean extends ServiceMBean,IStatusService {

	public void startService()throws Exception;
	
	public void stopService()throws Exception;
}
