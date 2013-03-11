package org.osivia.portal.core.cache.global;



public interface ICacheService {
	
	/* Cache du bandeau */
	
	public long getHeaderCount();
	public void incrementHeaderCount( )	;
	
	/* Cache des profils */
	
	public long getProfilsCount();
	public void incrementProfilsCount( )	;


}
