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
import java.util.List;
import java.util.Map;

/**
 * Preferences POJO persisted in ECM.
 *
 * @author Loïc Billon
 * @author Cédric Krommenhoek
 */
public class UserPreferences {

    /**
     * ECM UserProfile UUID
     */
    private final String docId;

    /**
     * Check if the preferences should be updated at the end of the session.
     */
    private boolean update;
    /**
     * Map with folder ids and style preference.
     */
    private Map<String, String> folderDisplays;
    /**
     * User saved searches.
     */
    private List<UserSavedSearch> savedSearches;

    /**
     * Constructor.
     *
     * @param docId ECM UserProfile UUID
     */
    public UserPreferences(String docId) {
        super();
        this.docId = docId;
        this.folderDisplays = new HashMap<>();
    }


    /**
     * Get folder display.
     *
     * @param webId folder webId
     * @return folder display
     */
    public String getFolderDisplayMode(String webId) {
        return this.folderDisplays.get(webId);
    }


    /**
     * Update folder display.
     *
     * @param webId   folder webId
     * @param display folder display
     */
    public void updateFolderDisplayMode(String webId, String display) {
        this.update = true;
        this.folderDisplays.put(webId, display);
    }


    public String getDocId() {
        return docId;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public Map<String, String> getFolderDisplays() {
        return folderDisplays;
    }

    public void setFolderDisplays(Map<String, String> folderDisplays) {
        this.folderDisplays = folderDisplays;
    }

    public List<UserSavedSearch> getSavedSearches() {
        return savedSearches;
    }

    public void setSavedSearches(List<UserSavedSearch> savedSearches) {
        this.savedSearches = savedSearches;
    }
}
