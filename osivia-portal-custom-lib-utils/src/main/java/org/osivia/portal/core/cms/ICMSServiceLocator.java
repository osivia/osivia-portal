package org.osivia.portal.core.cms;

public interface ICMSServiceLocator {
	
	public void register(ICMSService service);
	
	public ICMSService getCMSService();

}
