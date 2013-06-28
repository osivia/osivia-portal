package org.osivia.portal.core.cms;

/**
 * registry for CMS Service
 *
 */
public class CMSServiceLocator implements ICMSServiceLocator{
	
	ICMSService service;

	public void register(ICMSService service) {
		this.service = service;
		
	}

	public ICMSService getCMSService() {
		return service;
	}

}
