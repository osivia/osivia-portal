package org.osivia.portal.api.cache.services;



public class CacheFlux {

	private long tsSaving;
	private Object cacheContent;
	private long tsAskForReloading;

	public long getTsEnregistrement() {
		return tsSaving;
	}

	public void setTsSaving(long tsEnregistrement) {
		this.tsSaving = tsEnregistrement;
	}

	public CacheFlux(CacheInfo infos, Object contenuCache) {
		super();
		this.tsSaving = System.currentTimeMillis();
		this.cacheContent = contenuCache;
	}

	public Object getContent() {
		return cacheContent;
	}

	public void setContent(Object contenuCache) {
		this.cacheContent = contenuCache;
	}

	public long getTsAskForReloading() {
		return tsAskForReloading;
	}

	public void setTsAskForReloading(long tsAskForReloading) {
		this.tsAskForReloading = tsAskForReloading;
	}
	
}
