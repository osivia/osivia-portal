/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.api.cache.services;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;


/**
 * The Class CacheInfo.
 * 
 * Structure the way the cache will be handled (timeout, scope, reloading policy)
 * 
 */
public class CacheInfo {
	
	/** The expiration delay. */
	private long expirationDelay = 60000L;
	
	/** The scope. */
	private int scope;
	
	/** The request. */
	private Object request;
	
	/** The context. */
	private PortletContext context;
	
	/** The cache scope none. */
	public static int CACHE_SCOPE_NONE = 0;
	
	/** The cache scope portlet session. */
	public static int CACHE_SCOPE_PORTLET_SESSION = 1;	
	
	/** The cache scope portlet context. */
	public static int CACHE_SCOPE_PORTLET_CONTEXT = 2; 
	
	/** The cache scope global. */
	public static int CACHE_SCOPE_GLOBAL = 3; 
	

	/** The force reload. */
	private boolean forceReload = false;
	

	/** The force not reload. */
	private boolean forceNOTReload = false;
	
	/**
	 * Variable indiquant si le résultat de la commande 
	 * effectuée avec ce contexte doit être mise à jour
	 * en cache de façon asynchrone.
	 */
	private boolean isAsyncCacheRefreshing = false;
		
	/**
	 * Checks if is force not reload.
	 *
	 * @return true, if is force not reload
	 */
	public boolean isForceNOTReload() {
		return forceNOTReload;
	}

	/**
	 * Sets the force not reload.
	 *
	 * @param forceNOTReload the new force not reload
	 */
	public void setForceNOTReload(boolean forceNOTReload) {
		this.forceNOTReload = forceNOTReload;
	}

	/**
	 * Checks if is force reload.
	 *
	 * @return true, if is force reload
	 */
	public boolean isForceReload() {
		return forceReload;
	}

	/**
	 * Sets the force reload.
	 *
	 * @param forceReload the new force reload
	 */
	public void setForceReload(boolean forceReload) {
		this.forceReload = forceReload;
	}

	/**
	 * Checks if is async cache refreshing.
	 *
	 * @return true, if is async cache refreshing
	 */
	public boolean isAsyncCacheRefreshing() {
		return isAsyncCacheRefreshing;
	}

	/**
	 * Sets the async cache refreshing.
	 *
	 * @param isAsyncCacheRefreshing the new async cache refreshing
	 */
	public void setAsyncCacheRefreshing(boolean isAsyncCacheRefreshing) {
		this.isAsyncCacheRefreshing = isAsyncCacheRefreshing;
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	public Object getContext() {
		return context;
	}

	/**
	 * Sets the context.
	 *
	 * @param context the new context
	 */
	public void setContext(PortletContext context) {
		this.context = context;
	}
	
	/** The item key. */
	private String itemKey;
	
	/** The invoker. */
	private IServiceInvoker invoker;
	
	
	
	/**
	 * Instantiates a new cache info.
	 *
	 * @param itemKey the item Key of the cache element
	 * @param scope the scope
	 * @param invoker the applicative invoker that will compute the cache content
	 * @param request the request
	 * @param context the context
	 * @param isAsyncCacheRefreshing the is async cache refreshing
	 */
	public CacheInfo(String itemKey, int scope, IServiceInvoker invoker, Object request, PortletContext context, boolean isAsyncCacheRefreshing) {
		super();
		this.scope = scope;
		this.request = request;
		this.itemKey = itemKey;
		this.invoker = invoker;
		this.context = context;
		this.isAsyncCacheRefreshing = isAsyncCacheRefreshing;
	}
	
	
	/**
	 * Gets the expiration delay.
	 *
	 * @return the expiration delay
	 */
	public long getExpirationDelay() {
		return expirationDelay;
	}
	
	/**
	 * Sets the expiration delay.
	 *
	 * @param expirationDelay the new expiration delay
	 */
	public void setExpirationDelay(long expirationDelay) {
		this.expirationDelay = expirationDelay;
	}
	
	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public int getScope() {
		return scope;
	}
	
	/**
	 * Sets the scope.
	 *
	 * @param scope the new scope
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}
	
	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public Object getRequest() {
		return request;
	}
	
	/**
	 * Sets the request.
	 *
	 * @param request the new request
	 */
	public void setRequest(Object request) {
		this.request = request;
	}
	
	/**
	 * Gets the item key.
	 *
	 * @return the item key
	 */
	public String getItemKey() {
		return itemKey;
	}
	
	/**
	 * Sets the item key.
	 *
	 * @param itemKey the new item key
	 */
	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}
	
	/**
	 * Gets the invoker.
	 *
	 * @return the invoker
	 */
	public IServiceInvoker getInvoker() {
		return invoker;
	}
	
	/**
	 * Sets the invoker.
	 *
	 * @param invoker the new invoker
	 */
	public void setInvoker(IServiceInvoker invoker) {
		this.invoker = invoker;
	}
	
	


}
