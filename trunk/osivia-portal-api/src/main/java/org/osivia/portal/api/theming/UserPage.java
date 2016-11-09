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
import java.util.List;

import org.jboss.portal.core.model.portal.PortalObjectId;


/**
 * User page java-bean.
 */
public class UserPage {

    /** Page name. */
    private String name;
    /** Page URL. */
    private String url;
    /** Close page URL. */
    private String closePageUrl;
    /** Default page indicator. */
    private boolean defaultPage;
    /** Tab group name. */
    private String group;
    /** Maintains visible indicator. */
    private boolean maintains;

    /** Page identifier. */
    private final String id;
    /** Page portal object identifier, may be null. */
    private final PortalObjectId portalObjectId;
    /** Page children. */
    private final List<UserPage> children;


    /**
     * Constructor.
     *
     * @param id page identifier
     */
    public UserPage(String id) {
        super();
        this.id = id;
        this.portalObjectId = null;
        this.children = new ArrayList<UserPage>();
    }


    /**
     * Constructor.
     * 
     * @param portalObjectId portal object identifier
     */
    public UserPage(PortalObjectId portalObjectId) {
        super();
        this.id = portalObjectId.toString();
        this.portalObjectId = portalObjectId;
        this.children = new ArrayList<UserPage>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        UserPage other = (UserPage) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
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
     * Getter for group.
     *
     * @return the group
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * Setter for group.
     *
     * @param group the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Getter for maintains.
     *
     * @return the maintains
     */
    public boolean isMaintains() {
        return this.maintains;
    }

    /**
     * Setter for maintains.
     *
     * @param maintains the maintains to set
     */
    public void setMaintains(boolean maintains) {
        this.maintains = maintains;
    }

    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter for portalObjectId.
     * 
     * @return the portalObjectId
     */
    public PortalObjectId getPortalObjectId() {
        return portalObjectId;
    }

    /**
     * Getter for children.
     *
     * @return the children
     */
    public List<UserPage> getChildren() {
        return this.children;
    }

}
