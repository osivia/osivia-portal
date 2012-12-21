/**
 * 
 */
package org.osivia.portal.core.cache.services;

import org.jboss.system.ServiceMBean;
import org.osivia.portal.api.cache.services.ICacheService;




/**
 * @author jss
 *
 */
public interface CacheServiceMBean extends ServiceMBean,ICacheService {

	public void startService()throws Exception;
	
	public void stopService()throws Exception;
}
