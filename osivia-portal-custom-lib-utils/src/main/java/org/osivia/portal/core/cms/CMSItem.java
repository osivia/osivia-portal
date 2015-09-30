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
package org.osivia.portal.core.cms;

import java.util.HashMap;
import java.util.Map;

import org.osivia.portal.api.cms.DocumentType;

/**
 * CMS item.
 */
public class CMSItem {

    /** CMS native item. */
    private final Object nativeItem;
    /** CMS item path. */
    private final String path;
    /** CMS item properties. */
    private final Map<String, String> properties;
    /** CMS item meta-properties, used for SEO. */
    private final Map<String, String> metaProperties;
    /** Domain identifier. */
    private final String domainId;
    /** Web identifier */
    private final String webId;

    /** Published CMS item indicator. */
    private Boolean published;
    /** Indicates if working version is different from published version. */
    private Boolean beingModified;
    /** CMS item type. */
    private DocumentType type;


    /**
     * Constructor.
     * 
     * @param path CMS item path
     * @param webId web identifier
     * @param domainId domain identifier
     * @param properties CMS item properties, may be null if empty
     * @param nativeItem CMS native item
     */
    public CMSItem(String path, String domainId, String webId, Map<String, String> properties, Object nativeItem) {
        super();

        this.nativeItem = nativeItem;
        this.path = path;
        if (properties == null) {
            this.properties = new HashMap<String, String>();
        } else {
            this.properties = properties;
        }
        this.domainId = domainId;
        this.webId = webId;
        this.metaProperties = new HashMap<String, String>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CMSItem [path=" + this.path + ", domainId=" + this.domainId + ", webId=" + this.webId + "]";
    }


    /**
     * Getter for published.
     *
     * @return the published
     */
    public Boolean getPublished() {
        return this.published;
    }

    /**
     * Setter for published.
     *
     * @param published the published to set
     */
    public void setPublished(Boolean published) {
        this.published = published;
    }

    /**
     * Getter for beingModified.
     *
     * @return the beingModified
     */
    public Boolean getBeingModified() {
        return this.beingModified;
    }

    /**
     * Setter for beingModified.
     *
     * @param beingModified the beingModified to set
     */
    public void setBeingModified(Boolean beingModified) {
        this.beingModified = beingModified;
    }

    /**
     * Getter for type.
     *
     * @return the type
     */
    public DocumentType getType() {
        return this.type;
    }

    /**
     * Setter for type.
     *
     * @param type the type to set
     */
    public void setType(DocumentType type) {
        this.type = type;
    }

    /**
     * Getter for nativeItem.
     *
     * @return the nativeItem
     */
    public Object getNativeItem() {
        return this.nativeItem;
    }

    /**
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Getter for properties.
     *
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * Getter for metaProperties.
     *
     * @return the metaProperties
     */
    public Map<String, String> getMetaProperties() {
        return this.metaProperties;
    }

    /**
     * Getter for domainId.
     *
     * @return the domainId
     */
    public String getDomainId() {
        return this.domainId;
    }

    /**
     * Getter for webId.
     *
     * @return the webId
     */
    public String getWebId() {
        return this.webId;
    }

}
