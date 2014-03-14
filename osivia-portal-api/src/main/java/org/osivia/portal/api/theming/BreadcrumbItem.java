/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.api.theming;

import java.util.List;

import org.osivia.portal.api.path.PortletPathItem;


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
