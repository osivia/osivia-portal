/**
 * 
 */
package org.osivia.portal.core.cache.services;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.InitialContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.system.ServiceMBeanSupport;
import org.osivia.portal.api.cache.services.CacheFlux;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.cache.services.ICacheDataListener;
import org.osivia.portal.api.cache.services.IGlobalParameters;
import org.osivia.portal.core.page.PageProperties;



/**
 * Gestion mutualisée des caches d'information
 * 
 * @author jss
 * 
 */
public class CacheService extends ServiceMBeanSupport implements CacheServiceMBean, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, CacheFlux> mCaches = new Hashtable<String, CacheFlux>();

	protected static final Log logger = LogFactory.getLog(CacheService.class);
	
	private long lastInitialisationTs = 0L;

	private long portalParameterslastInitialisationTs = 0L;
	
	public void startService() throws Exception {
		logger.info("Cache service starting");

		InitialContext init = new InitialContext();
		init.bind("java:cache", this);
	}

	/**
	 * 
	 * Détermine le cache
	 * 
	 * @param infosFlux
	 * @param afficheur
	 * @param request
	 * @return
	 */

	@SuppressWarnings("unchecked")
	private Map<String, CacheFlux> getMapCache(CacheInfo infosCache) throws Exception {

		Map<String, CacheFlux> caches = mCaches;

		// Cache dans le contexte du portlet

		if (infosCache.getScope() == CacheInfo.CACHE_SCOPE_PORTLET_CONTEXT) {

			PortletContext ctx = ((PortletContext) infosCache.getContext());
			caches = (Map<String, CacheFlux>) ctx.getAttribute("caches");
			if (caches == null) {
				caches = new Hashtable<String, CacheFlux>();
				ctx.setAttribute("caches", caches);
			}
		}
		
		
		// Cache dans la session
		
		if (infosCache.getScope() == CacheInfo.CACHE_SCOPE_PORTLET_SESSION) {
			
			if(  infosCache.getRequest() instanceof PortletRequest)	{
				PortletRequest req = ((PortletRequest) infosCache.getRequest());
				PortletSession session = req.getPortletSession();
				String userName = req.getRemoteUser();
				if(userName == null)
					userName = "";
				// when user is logged cache must be refreshed
				caches = (Map<String, CacheFlux>) session.getAttribute("caches."+userName);
				//caches = (Map<String, CacheFlux>) session.getAttribute("caches");				
				if (caches == null) {
					caches = new Hashtable<String, CacheFlux>();
					session.setAttribute("caches."+userName, caches);
				}
			}
			
			if(  infosCache.getRequest() instanceof HttpServletRequest)	{
				HttpServletRequest req = ((HttpServletRequest) infosCache.getRequest());
				HttpSession session = req.getSession();
				String userName = req.getRemoteUser();
				if(userName == null)
					userName = "";
				// when user is logged cache must be refreshed
				caches = (Map<String, CacheFlux>) session.getAttribute("caches."+userName);				
				//caches = (Map<String, CacheFlux>) session.getAttribute("caches");
				if (caches == null) {
					caches = new Hashtable<String, CacheFlux>();
					session.setAttribute("caches."+userName, caches);
				}
			}

		}
		

		return caches;
	}

	public Object getCache(CacheInfo infos) throws Exception {

		CacheFlux cacheFlux = null;

		if (infos.getScope() == CacheInfo.CACHE_SCOPE_NONE)
			return infos.getInvoker().invoke();

		Map<String, CacheFlux> caches = getMapCache(infos);
		
		// On renvoie le cache tel quel (sans controle de validité)
		if (infos.isForceNOTReload()) {
			cacheFlux =  caches.get(infos.getCleItem());
			if( cacheFlux != null)
				return cacheFlux.getContenuCache();
			else 
				return null;
		}

		

		// On récupère le cache
		if (!infos.isForceReload()) {
			cacheFlux = caches.get(infos.getCleItem());
		}
		
		// 1.0.27 : initialisation des parametres globaux
		if (cacheFlux != null)	{
			if( cacheFlux.getContenuCache() instanceof IGlobalParameters)	{
				if( cacheFlux.getTsEnregistrement() < portalParameterslastInitialisationTs)	{
					// Le cache est obsolete, on le conserve quand meme
					// car en cas d'erreur il sera reutilise (isForceNOTReload)
					cacheFlux.setTsEnregistrement(0L);
				}
			}
		}
		
		// Cache inexistant
		if (cacheFlux == null) {

			if (infos.getInvoker() != null) {
				if (infos.getScope() == CacheInfo.CACHE_SCOPE_PORTLET_SESSION) {
					// Pas de synchronisation en mode session
					// Appel
					refreshCache(infos, caches);
				}else{
					rafraichirCacheSynchronise(infos, caches);
				}
				return caches.get(infos.getCleItem()).getContenuCache();
			} else
				return null;
		} else {

			// Cache existant et expiré (20s)
			if (System.currentTimeMillis() - cacheFlux.getTsEnregistrement() > infos.getDelaiExpiration()
					|| cacheFlux.getTsEnregistrement() < getCacheInitialisationTs() || PageProperties.getProperties().isRefreshingPage() ) {
				
				if (infos.getInvoker() != null) {				
					if (infos.getScope() == CacheInfo.CACHE_SCOPE_PORTLET_SESSION) {
						refreshCache(infos, caches);
					}else{
						rafraichirCacheSynchronise(infos, caches);
					}
					return caches.get(infos.getCleItem()).getContenuCache();
				} else
					return null;
			}
		}

		return caches.get(infos.getCleItem()).getContenuCache();

	}

	private void asyncRefreshCache(CacheFlux cacheFlux, CacheInfo infos, Map<String, CacheFlux> caches) throws Exception {
		
		AsyncRefreshCacheThread asyncThread = new  AsyncRefreshCacheThread(this, infos, caches);	
		CacheThreadsPool.getInstance().execute(asyncThread);
		
	}
	
	void refreshCache(CacheInfo infos, Map<String, CacheFlux> caches) throws Exception {
		
		Object response = infos.getInvoker().invoke();
		storeCache(infos, caches, response);
		
	}

	public void stopService() throws Exception {
		InitialContext init = new InitialContext();
		init.unbind("java:cache");

	}

	private void rafraichirCacheSynchronise(CacheInfo infos, Map<String, CacheFlux> caches) throws Exception {

		Object synchronizer = null;

		// synchronizer global
		synchronizer = CacheSynchronizer.getSynchronizer(infos.getCleItem());

		synchronized (synchronizer) {

			CacheFlux cacheFlux = null;

			if (!infos.isForceReload()) {
				cacheFlux = caches.get(infos.getCleItem());
			}

			// reinitialisation des caches
			if (   (cacheFlux != null && (cacheFlux.getTsEnregistrement() < getCacheInitialisationTs())) || PageProperties.getProperties().isRefreshingPage())
				cacheFlux = null;
			

			if (cacheFlux == null) {
				
				refreshCache(infos, caches);
				
			} else {

				// Le test est dupliqué pour éviter n rechargements
				if (infos.isAsyncCacheRefreshing()) {

					boolean isReloading = isAsyncThreadRefreshingCache(cacheFlux);

					if ((System.currentTimeMillis() - cacheFlux.getTsEnregistrement() > infos.getDelaiExpiration())
							&& (!isReloading)) {

						cacheFlux.setTsAskForReloading(System.currentTimeMillis());
						asyncRefreshCache(cacheFlux, infos, caches);

					}
				} else {
					
					if ((System.currentTimeMillis() - cacheFlux.getTsEnregistrement() > infos.getDelaiExpiration())) {
						
						refreshCache(infos, caches);
						
					}

				}
			}
		}
	}
	
	/**
	 * Méthode permattant de savoir si un thread asynchrone est en cours de mise à jour
	 * du cache avec la donnée cacheFlux.
	 * @param cacheFlux donnée à mettre à jour
	 * @return vrai si l ethread est en cours d'exécution
	 */
	private boolean isAsyncThreadRefreshingCache(CacheFlux cacheFlux) {
		boolean isReloading = false;
		// Indique si une "demande d'exécution du thread a été effectuée.
		if (cacheFlux.getTsAskForReloading() != 0L) {
			long elapsedTime = System.currentTimeMillis() - cacheFlux.getTsAskForReloading();
			// Si le thread a été lancé depuis plus de 20 secondes,
			// on le considère en échec (il n'a pas été lancé)
			if (elapsedTime < 20000) {
				isReloading = true;
			}
		}
		return isReloading;
	}

	private synchronized void storeCache(CacheInfo infos, Map<String, CacheFlux> caches, Object response)
			throws Exception {
		CacheFlux old = caches.get(infos.getCleItem());
		
		// v1.0.23 : suppression fichiers temporaires
		
		if( old != null){
			
			Object contenu = old.getContenuCache();
			
			if( contenu instanceof ICacheDataListener)	{
				((ICacheDataListener) contenu).remove();
			}
		}
		
		caches.put(infos.getCleItem(), new CacheFlux(infos, response));
	}

	public long getCacheInitialisationTs() {
		return lastInitialisationTs;
	}

	public void initCache() throws Exception {
		lastInitialisationTs = System.currentTimeMillis();
	}
	public void initPortalParameters() {

		portalParameterslastInitialisationTs = System.currentTimeMillis();;
	}

}
