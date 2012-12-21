package org.osivia.portal.api.cache.services;

import java.io.Serializable;

public interface ICacheService extends Serializable{

	public Object getCache( CacheInfo infos) throws Exception;
	
	public long getCacheInitialisationTs() ;
	
	public void initCache() throws Exception;
	
	public void initPortalParameters();


	
}
