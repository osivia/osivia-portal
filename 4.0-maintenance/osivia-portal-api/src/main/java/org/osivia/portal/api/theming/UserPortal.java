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

import java.util.List;

/**
 * User portal java-bean.
 */
public class UserPortal {

    /** Portal name. */
    private String name;
    /** User pages. */
    private List<UserPage> userPages;
    /** Default page. */
    private UserPage defaultPage;


    /**
     * Constructor.
     */
    public UserPortal() {
        super();
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
     * Getter for userPages.
     *
     * @return the userPages
     */
    public List<UserPage> getUserPages() {
        return this.userPages;
    }

    /**
     * Setter for userPages.
     *
     * @param userPages the userPages to set
     */
    public void setUserPages(List<UserPage> userPages) {
        this.userPages = userPages;
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

}
