package org.osivia.portal.core.cache;

import org.jboss.system.ServiceMBean;

public interface CacheServiceMBean  extends ServiceMBean{
	
	public org.jboss.cache.TreeCacheMBean getPiaTreeCache() ;
	public void setPiaTreeCache(org.jboss.cache.TreeCacheMBean treeCache) ;
	public org.jboss.cache.TreeCacheMBean getHibernateTreeCache() ;
	public void setHibernateTreeCache(org.jboss.cache.TreeCacheMBean treeCache) ;

	
	

}
