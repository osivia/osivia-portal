package fr.toutatice.portail.api.charte;

import java.util.List;

import fr.toutatice.portail.api.path.PortletPathItem;

public class BreadcrumbItem {
	
	private String name;
	private String url;
	private Object id;
	private boolean userMaximized = false;
	
	private boolean navigationPlayer = false;
	
	public boolean isNavigationPlayer() {
		return navigationPlayer;
	}
	public void setNavigationPlayer(boolean navigationPlayer) {
		this.navigationPlayer = navigationPlayer;
	}
	private List<PortletPathItem> portletPath; 
	
	public List<PortletPathItem> getPortletPath() {
		return portletPath;
	}
	public void setPortletPath(List<PortletPathItem> portletPath) {
		this.portletPath = portletPath;
	}
	public boolean isUserMaximized() {
		return userMaximized;
	}
	public void setUserMaximized(boolean userMaximized) {
		this.userMaximized = userMaximized;
	}
	public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = id;
	}
	
	
	public BreadcrumbItem(String name, String url, Object id, boolean userMaximized) {
		super();
		this.name = name;
		this.url = url;
		this.id = id;
		this.userMaximized = userMaximized;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	
}
