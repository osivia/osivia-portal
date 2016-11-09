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

public class CMSEditableWindow {

	String name;
	String applicationID;
	Map<String,String> applicationProperties;
	
	public CMSEditableWindow(String name, String applicationID, Map<String, String> applicationProperties) {
		super();
		this.name = name;
		this.applicationID = applicationID;
		this.applicationProperties = applicationProperties;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getApplicationID() {
		return applicationID;
	}
	public void setApplicationID(String applicationID) {
		this.applicationID = applicationID;
	}
	public Map<String, String> getApplicationProperties() {
		return applicationProperties;
	}
	public void setApplicationProperties(Map<String, String> applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
	
	

}
