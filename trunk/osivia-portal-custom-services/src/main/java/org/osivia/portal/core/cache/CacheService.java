package org.osivia.portal.core.cache;

import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.cache.CacheException;
import org.jboss.cache.Fqn;

import org.jboss.cache.TreeCacheListener;
import org.jboss.cache.TreeCacheMBean;
import org.jboss.portal.common.http.HttpRequest;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.system.ServiceMBeanSupport;
import org.osivia.portal.core.cache.global.CacheDatas;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.tracker.ITracker;


/**
 * 
 * Gestionnaire de cache TreeCache pour synchroniser les noeuds
 * 
 * 
 * @author cap2j
 *
 */
@SuppressWarnings("unchecked")

public class CacheService extends ServiceMBeanSupport implements  ICacheService, Serializable {
	

	private static final long serialVersionUID = 3361892990187034534L;


	private Log log = LogFactory.getLog(CacheService.class);
	
	private TreeCacheMBean piaTreeCache;
	private TreeCacheMBean hibernateTreeCache;
	

	public TreeCacheMBean getPiaTreeCache() {
		return piaTreeCache;
	}

	public void setPiaTreeCache(TreeCacheMBean piaTreeCache) {
		this.piaTreeCache = piaTreeCache;
	}

	public TreeCacheMBean getHibernateTreeCache() {
		return hibernateTreeCache;
	}

	public void setHibernateTreeCache(TreeCacheMBean hibernateTreeCache) {
		this.hibernateTreeCache = hibernateTreeCache;
	}
		
	private CacheDatas getCacheDatas() throws RuntimeException	{
		
		Fqn fqn = new Fqn("pia");
		CacheDatas cache = null;

		 try {
			cache = (CacheDatas) getPiaTreeCache().get(fqn, "main");
		} catch (CacheException e) {
			throw new RuntimeException( e);
		}

		if( cache == null)
			cache = new CacheDatas();
		return cache;
	
	}
	
	private void setCacheDatas(CacheDatas cache) throws RuntimeException	{
		
		Fqn fqn = new Fqn("pia");
	
		 try {
			 getPiaTreeCache().put(fqn, "main", cache);
		} catch (CacheException e) {
			throw new RuntimeException( e);
		}
	
	}
	
	
	public long getHeaderCount() {
		return getCacheDatas().getHeaderCount();
		
	}
	
	public void incrementHeaderCount( )	{
		CacheDatas cache = getCacheDatas();
		cache.setHeaderCount(cache.getHeaderCount()+1);
		setCacheDatas(cache);
		
	}
	
	public long getProfilsCount() {
		return getCacheDatas().getProfilsCount();
		
	}
	
	public void incrementProfilsCount( )	{
		CacheDatas cache = getCacheDatas();
		cache.setProfilsCount(cache.getProfilsCount()+1);
		setCacheDatas(cache);
		
	}


	public void startService() throws Exception {
		log.info("start service CacheService");
	}

	public void stopService() throws Exception {
		log.info("stop service CacheService");
	}

	public long getProfilsTimestamp() {
		return 0;
	}

	public void setProfilsTimestamp(long timestamp) {
	
	}

	
	
}
