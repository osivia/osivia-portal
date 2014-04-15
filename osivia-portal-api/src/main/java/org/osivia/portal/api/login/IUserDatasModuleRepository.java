package org.osivia.portal.api.login;


import javax.portlet.PortletRequest;


public interface IUserDatasModuleRepository {
	
	public void register (UserDatasModuleMetadatas moduleMetadatas);
	public void unregister (UserDatasModuleMetadatas moduleMetadatas);
	public void reload(PortletRequest request);
	
}
