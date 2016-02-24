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
package org.osivia.portal.api.player;

import java.util.Map;

/**
 * Player (open a portlet in a window maximized with a set of parameters)
 *
 */
public class Player {
	
	/** external url */
	private String externalUrl;
	
	/** portlet instance to run */
	private String portletInstance;
	
	/** set of properties to configure the portlet */
	Map<String, String> windowProperties;

	/**
	 * @return the externalUrl
	 */
	public String getExternalUrl() {
		return externalUrl;
	}

	/**
	 * @param externalUrl the externalUrl to set
	 */
	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	/**
	 * @return the portletInstance
	 */
	public String getPortletInstance() {
		return portletInstance;
	}

	/**
	 * @param portletInstance the portletInstance to set
	 */
	public void setPortletInstance(String portletInstance) {
		this.portletInstance = portletInstance;
	}

	/**
	 * @return the windowProperties
	 */
	public Map<String, String> getWindowProperties() {
		return windowProperties;
	}

	/**
	 * @param windowProperties the windowProperties to set
	 */
	public void setWindowProperties(Map<String, String> windowProperties) {
		this.windowProperties = windowProperties;
	}	
	


}
