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
package org.osivia.portal.api.theming;

import java.util.List;

import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.api.path.PortletPathItem;


/**
 * Breadcrumb item.
 */
public class BreadcrumbItem {

    /** Name. */
    private String name;
    /** URL. */
    private String url;
    /** Portal object identifier. */
    private Object id;
    /** User maximized indicator. */
    private boolean userMaximized;
    /** Navigation player indicator. */
    private boolean navigationPlayer;
    /** Portlet path. */
	private List<PortletPathItem> portletPath;
    /** Task identifier. */
    private String taskId;
    /** Document type. */
    private DocumentType documentType;


    /**
     * Constructor.
     *
     * @param name name
     * @param url URL
     * @param id portal object identifier
     * @param userMaximized user maximized indicator
     */
	public BreadcrumbItem(String name, String url, Object id, boolean userMaximized) {
		super();
		this.name = name;
		this.url = url;
		this.id = id;
		this.userMaximized = userMaximized;
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
     * Getter for userMaximized.
     * 
     * @return the userMaximized
     */
    public boolean isUserMaximized() {
        return this.userMaximized;
    }

    /**
     * Setter for userMaximized.
     * 
     * @param userMaximized the userMaximized to set
     */
    public void setUserMaximized(boolean userMaximized) {
        this.userMaximized = userMaximized;
    }

    /**
     * Getter for navigationPlayer.
     * 
     * @return the navigationPlayer
     */
    public boolean isNavigationPlayer() {
        return this.navigationPlayer;
    }

    /**
     * Setter for navigationPlayer.
     * 
     * @param navigationPlayer the navigationPlayer to set
     */
    public void setNavigationPlayer(boolean navigationPlayer) {
        this.navigationPlayer = navigationPlayer;
    }

    /**
     * Getter for portletPath.
     * 
     * @return the portletPath
     */
    public List<PortletPathItem> getPortletPath() {
        return this.portletPath;
    }

    /**
     * Setter for portletPath.
     * 
     * @param portletPath the portletPath to set
     */
    public void setPortletPath(List<PortletPathItem> portletPath) {
        this.portletPath = portletPath;
    }

    /**
     * Getter for taskId.
     * 
     * @return the taskId
     */
    public String getTaskId() {
        return this.taskId;
    }

    /**
     * Setter for taskId.
     * 
     * @param taskId the taskId to set
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
