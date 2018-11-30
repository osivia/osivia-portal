/*
 * (C) Copyright 2018 OSIVIA (http://www.osivia.com)
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
package org.osivia.portal.api.user;

import java.util.HashMap;
import java.util.Map;


/**
 * Preferences POJO persisted in ECM.
 * @author Loïc Billon
 *
 */
public class UserPreferences {

	/** Map with folder ids and style preference */
	Map<String, String> folderDisplays = new HashMap<>();
	
	/** ECM UserProfile UUID */
	private final String docId;
	
	/** check if the preferences should be updated at the end of the session */
	private boolean update = false;

	/**
	 * 
	 */
	public UserPreferences(String docId) {
		this.docId = docId;
	}
	
	/**
	 * 
	 */
	public String getFolderDisplayMode(String webid) {
		return folderDisplays.get(webid);
	}

	/**
	 * 
	 */
	public void updateFolderDisplayMode(String webid, String value) {
		
		update = true;
		
		this.folderDisplays.put(webid, value);
	}

	/**
	 * 
	 */
	public boolean isUpdate() {
		return update;
	}
	
	
	/**
	 * 
	 */
	public String getDocId() {
		return docId;
	}
	
	
	/**
	 * 
	 */
	public Map<String, String> getFolderDisplays() {
		return folderDisplays;
	}

	/**
	 * 
	 */
	public void setFolderDisplays(Map<String, String> folderDisplays) {
		this.folderDisplays = folderDisplays;
	}
	
	
}
