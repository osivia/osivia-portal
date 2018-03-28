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

import java.util.ArrayList;
import java.util.List;

/**
 * Navigation item.
 */
public class NavigationItem {

    /** Main document. */
    private Object mainDoc;
    /** Adapted CMS item. */
    private CMSItem adaptedCMSItem;
    /** Unfetched children indicator. */
    private boolean unfetchedChildren;
    /** Path. */
    private String path;

    /** Children . */
    private final List<NavigationItem> children;


    /**
     * Constructor.
     */
    public NavigationItem() {
        super();
        this.children = new ArrayList<>();
    }


    /**
     * Getter for mainDoc.
     * 
     * @return the mainDoc
     */
    public Object getMainDoc() {
        return mainDoc;
    }

    /**
     * Setter for mainDoc.
     * 
     * @param mainDoc the mainDoc to set
     */
    public void setMainDoc(Object mainDoc) {
        this.mainDoc = mainDoc;
    }

    /**
     * Getter for adaptedCMSItem.
     * 
     * @return the adaptedCMSItem
     */
    public CMSItem getAdaptedCMSItem() {
        return adaptedCMSItem;
    }

    /**
     * Setter for adaptedCMSItem.
     * 
     * @param adaptedCMSItem the adaptedCMSItem to set
     */
    public void setAdaptedCMSItem(CMSItem adaptedCMSItem) {
        this.adaptedCMSItem = adaptedCMSItem;
    }

    /**
     * Getter for unfetchedChildren.
     * 
     * @return the unfetchedChildren
     */
    public boolean isUnfetchedChildren() {
        return unfetchedChildren;
    }

    /**
     * Setter for unfetchedChildren.
     * 
     * @param unfetchedChildren the unfetchedChildren to set
     */
    public void setUnfetchedChildren(boolean unfetchedChildren) {
        this.unfetchedChildren = unfetchedChildren;
    }

    /**
     * Getter for path.
     * 
     * @return the path
     */
    public String getPath() {
        return path;
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
     * Getter for children.
     * 
     * @return the children
     */
    public List<NavigationItem> getChildren() {
        return children;
    }

}
