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
 * User page java-bean.
 */
public class UserPage {

    /** Page name. */
    private String name;
    /** Page URL. */
    private String url;
    /** Page identifier. */
    private Object id;
    /** Close page URL. */
    private String closePageUrl;
    /** Default page indicator. */
    private boolean defaultPage;
    /** Page children. */
    private List<UserPage> children;


    /**
     * Constructor.
     */
    public UserPage() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserPage [");
        if (this.name != null) {
            builder.append("name=");
            builder.append(this.name);
            builder.append(", ");
        }
        if (this.url != null) {
            builder.append("url=");
            builder.append(this.url);
        }
        builder.append("]");
        return builder.toString();
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
     * Getter for url.
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Setter for url.
     *
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter for id.
     *
     * @return the id
     */
    public Object getId() {
        return this.id;
    }

    /**
     * Setter for id.
     *
     * @param id the id to set
     */
    public void setId(Object id) {
        this.id = id;
    }

    /**
     * Getter for closePageUrl.
     *
     * @return the closePageUrl
     */
    public String getClosePageUrl() {
        return this.closePageUrl;
    }

    /**
     * Setter for closePageUrl.
     *
     * @param closePageUrl the closePageUrl to set
     */
    public void setClosePageUrl(String closePageUrl) {
        this.closePageUrl = closePageUrl;
    }

    /**
     * Getter for defaultPage.
     *
     * @return the defaultPage
     */
    public boolean isDefaultPage() {
        return this.defaultPage;
    }

    /**
     * Setter for defaultPage.
     *
     * @param defaultPage the defaultPage to set
     */
    public void setDefaultPage(boolean defaultPage) {
        this.defaultPage = defaultPage;
    }

    /**
     * Getter for children.
     *
     * @return the children
     */
    public List<UserPage> getChildren() {
        return this.children;
    }

    /**
     * Setter for children.
     *
     * @param children the children to set
     */
    public void setChildren(List<UserPage> children) {
        this.children = children;
    }

}
