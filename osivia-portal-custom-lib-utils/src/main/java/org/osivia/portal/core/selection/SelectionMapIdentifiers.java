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
package org.osivia.portal.core.selection;

import org.jboss.portal.core.model.portal.PortalObjectId;

/**
 * Selection map identifiers bean.
 * 
 * @author Cédric Krommenhoek
 */
public class SelectionMapIdentifiers {

    /** Identifier. */
    private String id;
    /** Scope. */
    private SelectionScope scope = SelectionScope.SCOPE_NAVIGATION;
    /** Page id. */
    private PortalObjectId pageId;

    /**
     * Default constructor.
     */
    public SelectionMapIdentifiers() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param id
     * @param scope
     * @param pageId
     */
    public SelectionMapIdentifiers(String id, SelectionScope scope, PortalObjectId pageId) {
        super();
        this.id = id;
        this.scope = scope;
        this.pageId = pageId;
    }


    /**
     * Getter for id.
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }


    /**
     * Setter for id.
     * 
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Getter for scope.
     * 
     * @return the scope
     */
    public SelectionScope getScope() {
        return scope;
    }


    /**
     * Setter for scope.
     * 
     * @param scope the scope to set
     */
    public void setScope(SelectionScope scope) {
        this.scope = scope;
    }


    /**
     * Getter for pageId.
     * 
     * @return the pageId
     */
    public PortalObjectId getPageId() {
        return pageId;
    }


    /**
     * Setter for pageId.
     * 
     * @param pageId the pageId to set
     */
    public void setPageId(PortalObjectId pageId) {
        this.pageId = pageId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((pageId == null) ? 0 : pageId.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SelectionMapIdentifiers other = (SelectionMapIdentifiers) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (pageId == null) {
            if (other.pageId != null)
                return false;
        } else if (!pageId.equals(other.pageId))
            return false;
        if (scope != other.scope)
            return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SelectionMapIdentifiers [id=" + id + ", scope=" + scope + ", pageId=" + pageId + "]";
    }


}
