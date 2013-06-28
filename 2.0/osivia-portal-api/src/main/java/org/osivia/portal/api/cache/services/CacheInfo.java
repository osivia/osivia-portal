package org.osivia.portal.api.cache.services;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

public class CacheInfo {
	
	private long delaiExpiration = 60000L;
	
	private int scope;
	private Object request;
	private PortletContext context;
	
	public static int CACHE_SCOPE_NONE = 0;
	public static int CACHE_SCOPE_PORTLET_SESSION = 1;	
	public static int CACHE_SCOPE_PORTLET_CONTEXT = 2; 
	public static int CACHE_SCOPE_GLOBAL = 3; 
	
	// cache must be reloaded
	private boolean forceReload = false;
	
	// cache must NOT be reloaded
	private boolean forceNOTReload = false;
	
	/**
	 * Variable indiquant si le résultat de la commande 
	 * effectuée avec ce contexte doit être mise à jour
	 * en cache de façon asynchrone.
	 */
	private boolean isAsyncCacheRefreshing = false;
		
	public boolean isForceNOTReload() {
		return forceNOTReload;
	}

	public void setForceNOTReload(boolean forceNOTReload) {
		this.forceNOTReload = forceNOTReload;
	}

	public boolean isForceReload() {
		return forceReload;
	}

	public void setForceReload(boolean forceReload) {
		this.forceReload = forceReload;
	}

	public boolean isAsyncCacheRefreshing() {
		return isAsyncCacheRefreshing;
	}

	public void setAsyncCacheRefreshing(boolean isAsyncCacheRefreshing) {
		this.isAsyncCacheRefreshing = isAsyncCacheRefreshing;
	}

	public Object getContext() {
		return context;
	}

	public void setContext(PortletContext context) {
		this.context = context;
	}
	private String cleItem;
	private IServiceInvoker invoker;
	
	
	
	public CacheInfo(String cleItem, int scope, IServiceInvoker invoker, Object request, PortletContext context, boolean isAsyncCacheRefreshing) {
		super();
		this.scope = scope;
		this.request = request;
		this.cleItem = cleItem;
		this.invoker = invoker;
		this.context = context;
		this.isAsyncCacheRefreshing = isAsyncCacheRefreshing;
	}
	
	
	public long getDelaiExpiration() {
		return delaiExpiration;
	}
	public void setDelaiExpiration(long delaiExpiration) {
		this.delaiExpiration = delaiExpiration;
	}
	public int getScope() {
		return scope;
	}
	public void setScope(int scope) {
		this.scope = scope;
	}
	public Object getRequest() {
		return request;
	}
	public void setRequest(Object request) {
		this.request = request;
	}
	public String getCleItem() {
		return cleItem;
	}
	public void setCleItem(String cleItem) {
		this.cleItem = cleItem;
	}
	public IServiceInvoker getInvoker() {
		return invoker;
	}
	public void setInvoker(IServiceInvoker invoker) {
		this.invoker = invoker;
	}
	
	


}
