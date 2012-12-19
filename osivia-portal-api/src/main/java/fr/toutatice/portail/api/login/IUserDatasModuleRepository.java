package fr.toutatice.portail.api.login;

public interface IUserDatasModuleRepository {
	
	public void register (UserDatasModuleMetadatas moduleMetadatas);
	public void unregister (UserDatasModuleMetadatas moduleMetadatas);
	
}
