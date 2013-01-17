package org.osivia.portal.api.cache.services;



public class CacheFlux {

	private long tsEnregistrement;
	private Object contenuCache;
	private long tsAskForReloading;

	public long getTsEnregistrement() {
		return tsEnregistrement;
	}

	public void setTsEnregistrement(long tsEnregistrement) {
		this.tsEnregistrement = tsEnregistrement;
	}

	public CacheFlux(CacheInfo infos, Object contenuCache) {
		super();
		this.tsEnregistrement = System.currentTimeMillis();
		this.contenuCache = contenuCache;
	}

	public Object getContenuCache() {
		return contenuCache;
	}

	public void setContenuCache(Object contenuCache) {
		this.contenuCache = contenuCache;
	}

	public long getTsAskForReloading() {
		return tsAskForReloading;
	}

	public void setTsAskForReloading(long tsAskForReloading) {
		this.tsAskForReloading = tsAskForReloading;
	}
	
}
