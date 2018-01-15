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
 */
package org.osivia.portal.api.theming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User portal java-bean.
 */
public class UserPortal {

    /** Portal name. */
    private String name;
    /** User default page. */
    private UserPage defaultPage;
    /** Portal default page. */
    private UserPage portalDefaultPage;
    /** Displayed pages count. */
    private int displayedPagesCount;

    /** User pages. */
    private final List<UserPage> userPages;
    /** User pages groups. */
    private final Map<String, UserPagesGroup> groups;


    /**
     * Constructor.
     */
    public UserPortal() {
        super();
        this.userPages = new ArrayList<UserPage>();
        this.groups = new LinkedHashMap<String, UserPagesGroup>();
    }


    /**
     * Get user pages group.
     *
     * @param name group name
     * @return group
     */
    public UserPagesGroup getGroup(String name) {
        return this.groups.get(name);
    }


    /**
     * Add user pages group.
     * 
     * @param group group
     */
    public void addGroup(UserPagesGroup group) {
        this.groups.put(group.getName(), group);
    }


    /**
     * Get user pages groups.
     *
     * @return groups
     */
    public Collection<UserPagesGroup> getGroups() {
        return this.groups.values();
    }


    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for defaultPage.
     *
     * @return the defaultPage
     */
    public UserPage getDefaultPage() {
        return this.defaultPage;
    }

    /**
     * Setter for defaultPage.
     *
     * @param defaultPage the defaultPage to set
     */
    public void setDefaultPage(UserPage defaultPage) {
        this.defaultPage = defaultPage;
    }
    
    /**
     * Getter for portalDefaultPage.
     * 
     * @return the portalDefaultPage
     */
    public UserPage getPortalDefaultPage() {
        return portalDefaultPage;
    }
    
    /**
     * Setter for portalDefaultPage.
     * 
     * @param portalDefaultPage the portalDefaultPage to set
     */
    public void setPortalDefaultPage(UserPage portalDefaultPage) {
        this.portalDefaultPage = portalDefaultPage;
    }

    /**
     * Getter for displayedPagesCount.
     *
     * @return the displayedPagesCount
     */
    public int getDisplayedPagesCount() {
        return this.displayedPagesCount;
    }

    /**
     * Setter for displayedPagesCount.
     *
     * @param displayedPagesCount the displayedPagesCount to set
     */
    public void setDisplayedPagesCount(int displayedPagesCount) {
        this.displayedPagesCount = displayedPagesCount;
    }

    /**
     * Getter for userPages.
     *
     * @return the userPages
     */
    public List<UserPage> getUserPages() {
        return this.userPages;
    }

}
