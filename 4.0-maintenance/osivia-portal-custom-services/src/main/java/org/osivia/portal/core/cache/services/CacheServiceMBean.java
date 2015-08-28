/**
 * 
 */
package org.osivia.portal.core.cache.services;

import org.jboss.system.ServiceMBean;
import org.osivia.portal.core.cache.global.ICacheService;





/**
 * @author jss
 *
 */
public interface CacheServiceMBean extends ServiceMBean,org.osivia.portal.api.cache.services.ICacheService {

	public void startService()throws Exception;
	
	public void stopService()throws Exception;
	
    public ICacheService getCacheService() ;

    public void setCacheService(ICacheService cacheService) ;
}
