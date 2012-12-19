package fr.toutatice.portail.api.cache.services;

import java.util.Map;


public class CacheFlux {

	private long tsEnregistrement;
	private Object contenuCache;

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

}
