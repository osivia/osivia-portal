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
import java.util.TreeMap;

/**
 * CMS item.
 */
public class CMSItem {

    /** CMS native item. */
    private Object nativeItem;
    /** CMS item path. */
    private String path;
    /** CMS item properties. */
    private Map<String, String> properties;
    /** CMS item meta-properties, used for SEO. */
    private Map<String, String> metaProperties = new TreeMap<String, String>();
    /** Published CMS item indicator. */
    private Boolean published;
    /** Indicates if working version is different from published version. */
    private Boolean beingModified;
    /** CMS item type. */
    private CMSItemType type;
    /** Webid */
    private String webId;


    /**
     * @return the webId
     */
    public String getWebId() {
        return webId;
    }


    /**
     * @param webId the webId to set
     */
    public void setWebId(String webId) {
        this.webId = webId;
    }


    /**
     * Constructor.
     * 
     * @param path CMS item path
     * @param webId webID
     * @param properties CMS item properties
     * @param nativeItem CMS native item
     */
    public CMSItem(String path, String webId, Map<String, String> properties, Object nativeItem) {
        super();

        this.path = path;
        this.properties = properties;
        this.nativeItem = nativeItem;
        this.webId = webId;
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
     * Setter for nativeItem.
     * 
     * @param nativeItem the nativeItem to set
     */
    public void setNativeItem(Object nativeItem) {
        this.nativeItem = nativeItem;
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
     * Setter for path.
     * 
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
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
     * Setter for properties.
     * 
     * @param properties the properties to set
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
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
     * Setter for metaProperties.
     * 
     * @param metaProperties the metaProperties to set
     */
    public void setMetaProperties(Map<String, String> metaProperties) {
        this.metaProperties = metaProperties;
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
        return beingModified;
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
    public CMSItemType getType() {
        return this.type;
    }

    /**
     * Setter for type.
     * 
     * @param type the type to set
     */
    public void setType(CMSItemType type) {
        this.type = type;
    }

}
