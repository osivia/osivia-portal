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
