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


/**
 * The Class BreadcrumbItem.
 */
public class BreadcrumbItem {
	
	/** The name. */
	private String name;
	
	/** The url. */
	private String url;
	
	/** The id. */
	private Object id;
	
	/** The user maximized. */
	private boolean userMaximized = false;
	
	/** The navigation player. */
	private boolean navigationPlayer = false;
	
	/**
	 * Checks if is navigation player.
	 *
	 * @return true, if is navigation player
	 */
	public boolean isNavigationPlayer() {
		return navigationPlayer;
	}
	
	/**
	 * Sets the navigation player.
	 *
	 * @param navigationPlayer the new navigation player
	 */
	public void setNavigationPlayer(boolean navigationPlayer) {
		this.navigationPlayer = navigationPlayer;
	}
	
	/** The portlet path. */
	private List<PortletPathItem> portletPath; 
	
	/**
	 * Gets the portlet path.
	 *
	 * @return the portlet path
	 */
	public List<PortletPathItem> getPortletPath() {
		return portletPath;
	}
	
	/**
	 * Sets the portlet path.
	 *
	 * @param portletPath the new portlet path
	 */
	public void setPortletPath(List<PortletPathItem> portletPath) {
		this.portletPath = portletPath;
	}
	
	/**
	 * Checks if is user maximized.
	 *
	 * @return true, if is user maximized
	 */
	public boolean isUserMaximized() {
		return userMaximized;
	}
	
	/**
	 * Sets the user maximized.
	 *
	 * @param userMaximized the new user maximized
	 */
	public void setUserMaximized(boolean userMaximized) {
		this.userMaximized = userMaximized;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Object getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Object id) {
		this.id = id;
	}
	
	
	/**
	 * Instantiates a new breadcrumb item.
	 *
	 * @param name the name
	 * @param url the url
	 * @param id the id
	 * @param userMaximized the user maximized
	 */
	public BreadcrumbItem(String name, String url, Object id, boolean userMaximized) {
		super();
		this.name = name;
		this.url = url;
		this.id = id;
		this.userMaximized = userMaximized;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	
}
