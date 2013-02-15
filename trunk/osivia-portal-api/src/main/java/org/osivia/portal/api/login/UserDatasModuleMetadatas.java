package org.osivia.portal.api.login;

public class UserDatasModuleMetadatas {
	
	private String name;
	private int order=0;
	public IUserDatasModule module;
	
	public IUserDatasModule getModule() {
		return module;
	}
	public void setModule(IUserDatasModule module) {
		this.module = module;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	

}
