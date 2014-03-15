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
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cache.services.CacheDatas;
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

	private Map<String, CacheDatas> mCaches = new Hashtable<String, CacheDatas>();

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
	private Map<String, CacheDatas> getMapCache(CacheInfo infosCache) throws PortalException {

		Map<String, CacheDatas> caches = mCaches;

		// Cache dans le contexte du portlet

		if (infosCache.getScope() == CacheInfo.CACHE_SCOPE_PORTLET_CONTEXT) {

			PortletContext ctx = ((PortletContext) infosCache.getContext());
			caches = (Map<String, CacheDatas>) ctx.getAttribute("caches");
			if (caches == null) {
				caches = new Hashtable<String, CacheDatas>();
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
				caches = (Map<String, CacheDatas>) session.getAttribute("caches."+userName);
				//caches = (Map<String, CacheFlux>) session.getAttribute("caches");				
				if (caches == null) {
					caches = new Hashtable<String, CacheDatas>();
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
				caches = (Map<String, CacheDatas>) session.getAttribute("caches."+userName);				
				//caches = (Map<String, CacheFlux>) session.getAttribute("caches");
				if (caches == null) {
					caches = new Hashtable<String, CacheDatas>();
					session.setAttribute("caches."+userName, caches);
				}
			}

		}
		

		return caches;
	}

	public Object getCache(CacheInfo infos) throws PortalException {

		CacheDatas cacheFlux = null;

		if (infos.getScope() == CacheInfo.CACHE_SCOPE_NONE)
			return infos.getInvoker().invoke();

		Map<String, CacheDatas> caches = getMapCache(infos);
		
		// On renvoie le cache tel quel (sans controle de validité)
		if (infos.isForceNOTReload()) {
			cacheFlux =  caches.get(infos.getItemKey());
			if( cacheFlux != null)
				return cacheFlux.getContent();
			else 
				return null;
		}

		

		// On récupère le cache
		if (!infos.isForceReload()) {
			cacheFlux = caches.get(infos.getItemKey());
		}
		
		// 1.0.27 : initialisation des parametres globaux
		if (cacheFlux != null)	{
			if( cacheFlux.getContent() instanceof IGlobalParameters)	{
				if( cacheFlux.getTsEnregistrement() < portalParameterslastInitialisationTs)	{
					// Le cache est obsolete, on le conserve quand meme
					// car en cas d'erreur il sera reutilise (isForceNOTReload)
					cacheFlux.setTsSaving(0L);
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
				return caches.get(infos.getItemKey()).getContent();
			} else
				return null;
		} else {

			boolean expired = false;
			
			// Cache existant et expiré (20s)
			if (System.currentTimeMillis() - cacheFlux.getTsEnregistrement() > infos.getExpirationDelay())
				expired = true;
			
			// Réinitialisation par l'adminstrateur
			if( cacheFlux.getTsEnregistrement() < getCacheInitialisationTs())
				expired = true;
			
			// Réinitialisation par l'utilisateur : tous sauf parametres
			
			if(  PageProperties.getProperties().isRefreshingPage())  {
			    // On controle que la page n'a pas déja été rechargée dans la requete courante
			    if( !PageProperties.getProperties().getPagePropertiesMap().containsKey( "reloaded_"+ infos.getItemKey()))
			        expired = true;
			    PageProperties.getProperties().getPagePropertiesMap().put( "reloaded_"+ infos.getItemKey(), "1");
			}
			
			
			if(  (cacheFlux.getContent() instanceof IGlobalParameters))
				expired = true;
		
			
			if (expired ) {
				
				if (infos.getInvoker() != null) {				
					if (infos.getScope() == CacheInfo.CACHE_SCOPE_PORTLET_SESSION) {
						refreshCache(infos, caches);
					}else{
						rafraichirCacheSynchronise(infos, caches);
					}
					return caches.get(infos.getItemKey()).getContent();
				} else
					return null;
			}
		}

		return caches.get(infos.getItemKey()).getContent();

	}

	private void asyncRefreshCache(CacheDatas cacheFlux, CacheInfo infos, Map<String, CacheDatas> caches) throws PortalException {
		
		AsyncRefreshCacheThread asyncThread = new  AsyncRefreshCacheThread(this, infos, caches);	
		try {
            CacheThreadsPool.getInstance().execute(asyncThread);
        } catch (Exception e) {
           throw new PortalException(e);
        }
		
	}
	
	void refreshCache(CacheInfo infos, Map<String, CacheDatas> caches) throws PortalException {
		
		Object response = infos.getInvoker().invoke();
		storeCache(infos, caches, response);
		
	}

	public void stopService() throws Exception {
		InitialContext init = new InitialContext();
		init.unbind("java:cache");

	}

	private void rafraichirCacheSynchronise(CacheInfo infos, Map<String, CacheDatas> caches) throws PortalException {

		Object synchronizer = null;

		// synchronizer global
		synchronizer = CacheSynchronizer.getSynchronizer(infos.getItemKey());

		synchronized (synchronizer) {

			CacheDatas cacheFlux = null;

			if (!infos.isForceReload()) {
				cacheFlux = caches.get(infos.getItemKey());
			}

			// reinitialisation des caches
			if (   cacheFlux != null)
			if (   (cacheFlux.getTsEnregistrement() < getCacheInitialisationTs()) || (PageProperties.getProperties().isRefreshingPage() && ! (cacheFlux.getContent() instanceof IGlobalParameters)))
				cacheFlux = null;
			

			if (cacheFlux == null) {
				
				refreshCache(infos, caches);
				
			} else {

				// Le test est dupliqué pour éviter n rechargements
				if (infos.isAsyncCacheRefreshing()) {

					boolean isReloading = isAsyncThreadRefreshingCache(cacheFlux);

					if ((System.currentTimeMillis() - cacheFlux.getTsEnregistrement() > infos.getExpirationDelay())
							&& (!isReloading)) {

						cacheFlux.setTsAskForReloading(System.currentTimeMillis());
						asyncRefreshCache(cacheFlux, infos, caches);

					}
				} else {
					
					if ((System.currentTimeMillis() - cacheFlux.getTsEnregistrement() > infos.getExpirationDelay())) {
						
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
	private boolean isAsyncThreadRefreshingCache(CacheDatas cacheFlux) {
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

	private synchronized void storeCache(CacheInfo infos, Map<String, CacheDatas> caches, Object response)
			throws PortalException {
		CacheDatas old = caches.get(infos.getItemKey());
		
		// v1.0.23 : suppression fichiers temporaires
		
		if( old != null){
			
			Object contenu = old.getContent();
			
			if( contenu instanceof ICacheDataListener)	{
				((ICacheDataListener) contenu).remove();
			}
		}
		
		caches.put(infos.getItemKey(), new CacheDatas(infos, response));
	}

	public long getCacheInitialisationTs() {
		return lastInitialisationTs;
	}

	public void initCache() throws PortalException {
		lastInitialisationTs = System.currentTimeMillis();
	}
	public void initPortalParameters() {

		portalParameterslastInitialisationTs = System.currentTimeMillis();;
	}

}
