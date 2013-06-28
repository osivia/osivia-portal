package org.osivia.portal.core.cache.global;

import java.io.Serializable;

public class CacheDatas implements Serializable{

	private static final long serialVersionUID = -4356842856191357397L;
	
	private long headerCount = 1;
	private long profilsCount = 1;
	
	public long getHeaderCount() {
		return headerCount;
	}
	public void setHeaderCount(long headerCount) {
		this.headerCount = headerCount;
	}
	public long getProfilsCount() {
		return profilsCount;
	}
	public void setProfilsCount(long profilsCount) {
		this.profilsCount = profilsCount;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}


	
}
