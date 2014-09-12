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
package org.osivia.portal.core.cms;

import java.util.Map;

public class CMSHandlerProperties {
	

	private String externalUrl;
	private String portletInstance;
	Map<String, String> windowProperties;	
	
	public String getPortletInstance() {
		return portletInstance;
	}

	public void setPortletInstance(String portletInstance) {
		this.portletInstance = portletInstance;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	
	public Map<String, String> getWindowProperties() {
		return windowProperties;
	}

	public void setWindowProperties(Map<String, String> windowProperties) {
		this.windowProperties = windowProperties;
	}

	public String getUrl() {
		return externalUrl;
	}

	public void setUrl(String url) {
		this.externalUrl = url;
	}




}
