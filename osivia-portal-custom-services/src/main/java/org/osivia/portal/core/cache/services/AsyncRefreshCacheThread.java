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
package org.osivia.portal.core.cache.services;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.cache.services.CacheDatas;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.error.Debug;

public class AsyncRefreshCacheThread implements Runnable {
	
	private static Log logger = LogFactory.getLog(AsyncRefreshCacheThread.class);

	private CacheInfo infos;
	private Map<String, CacheDatas> caches;
	private CacheService cacheService;
	private ExecutorService execService;

	public AsyncRefreshCacheThread(CacheService cacheService, CacheInfo infos, Map<String, CacheDatas> caches) {
		super();
		this.infos = infos;
		this.caches = caches;
		this.cacheService = cacheService;
		this.execService = Executors.newSingleThreadExecutor();
	}

	public ExecutorService getExecService() {
		return execService;
	}

	public void run() {
		
		try {

			cacheService.refreshCache(infos, caches);
		}

		catch (Exception e) {
			logger.error(Debug.throwableToString(e));
			execService.shutdown();
		}

	}

}
