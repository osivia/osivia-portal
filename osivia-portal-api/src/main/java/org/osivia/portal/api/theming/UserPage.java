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

import java.io.Serializable;
import java.util.List;


/**
 * The Class UserPage.
 */
public class UserPage implements Serializable {


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2616620851302460065L;

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
        if (name != null) {
            builder.append("name=");
            builder.append(name);
            builder.append(", ");
        }
        if (url != null) {
            builder.append("url=");
            builder.append(url);
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
        return name;
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
        return url;
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
        return id;
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
        return closePageUrl;
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
        return defaultPage;
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
        return children;
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
