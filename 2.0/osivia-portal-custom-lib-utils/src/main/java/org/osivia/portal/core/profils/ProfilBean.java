package org.osivia.portal.core.profils;

import java.io.Serializable;

public class ProfilBean implements Serializable {

	private static final long serialVersionUID = -5878083215376307378L;
	private String roleName = "";
	private String name = "";
	private String nuxeoVirtualUser = "";
	private String defaultPageName = "";	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ProfilBean(String name, String roleName, String defaultPageName, String nuxeoVirtualUser) {
		super();
		this.name = name;
		this.roleName = roleName;
		this.defaultPageName = defaultPageName;
		this.nuxeoVirtualUser = nuxeoVirtualUser;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	public String getDefaultPageName() {
		return defaultPageName;
	}
	public void setDefaultPageName(String defaultPageName) {
		this.defaultPageName = defaultPageName;
	}
	
	public String getNuxeoVirtualUser() {
		return nuxeoVirtualUser;
	}
	public void setNuxeoVirtualUser(String nuxeoVirtualUser) {
		this.nuxeoVirtualUser = nuxeoVirtualUser;
	}


}
