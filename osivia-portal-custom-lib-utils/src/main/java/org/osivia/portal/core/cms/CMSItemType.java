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

import java.util.List;

/**
 * CMS item type java-bean.
 *
 * @author Cédric Krommenhoek
 */
public class CMSItemType {

    /** CMS item type name. */
    private final String name;
    /** CMS item type folderish indicator. */
    private final boolean folderish;
    /** CMS item navigable indicator. */
    private final boolean navigable;
    /** CMS item browsable indicator. */
    private final boolean browsable;
    /** CMS item type ordered indicator. */
    private final boolean ordered;
    /** CMS item force portal contextualization indicator. */
    private final boolean forcePortalContextualization;
    /** CMS item type supports portal forms indicator. */
    private final boolean supportsPortalForms;
    /** CMS item type portal from sub types. */
    private final List<String> portalFormSubTypes;
    /** CMS item default template path, may be null for global default template path. */
    private final String defaultTemplate;
    /** CMS item glyph, may be null for default glyph. */
    private final String glyph;
    /** CMS item is root type */
    private final boolean isRootType;


    /**
     * Constructor.
     * 
     * @param name CMS item type name
     * @param folderish CMS item type is folderish indicator
     * @param navigable CMS item navigable indicator
     * @param browsable CMS item browsable indicator
     * @param ordered CMS item type is ordered indicator
     * @param forcePortalContextualization CMS item force portal contextualization indicator
     * @param supportsPortalForms CMS item type supports portal forms indicator
     * @param portalFormSubTypes CMS item type portal from sub types
     * @param defaultTemplate CMS item default template path, may be null for global default template path
     */
    public CMSItemType(String name, boolean folderish, boolean navigable, boolean browsable, boolean ordered, boolean forcePortalContextualization,
            boolean supportsPortalForms, List<String> portalFormSubTypes, String defaultTemplate) {
        this(name, folderish, navigable, browsable, ordered, forcePortalContextualization, supportsPortalForms, portalFormSubTypes, defaultTemplate, null);
    }


    /**
     * Constructor.
     *
     * @param name CMS item type name
     * @param folderish CMS item type is folderish indicator
     * @param navigable CMS item navigable indicator
     * @param browsable CMS item browsable indicator
     * @param ordered CMS item type is ordered indicator
     * @param forcePortalContextualization CMS item force portal contextualization indicator
     * @param supportsPortalForms CMS item type supports portal forms indicator
     * @param portalFormSubTypes CMS item type portal from sub types
     * @param defaultTemplate CMS item default template path, may be null for global default template path
     * @param glyph CMS item customized glyph, may be null for default glyph
     */
    public CMSItemType(String name, boolean folderish, boolean navigable, boolean browsable, boolean ordered, boolean forcePortalContextualization,
            boolean supportsPortalForms, List<String> portalFormSubTypes, String defaultTemplate, String glyph) {
    	this(name, folderish, navigable, browsable, ordered, forcePortalContextualization, supportsPortalForms, portalFormSubTypes, defaultTemplate, glyph, false);
    }

    /**
     * Constructor.
     *
     * @param name CMS item type name
     * @param folderish CMS item type is folderish indicator
     * @param navigable CMS item navigable indicator
     * @param browsable CMS item browsable indicator
     * @param ordered CMS item type is ordered indicator
     * @param forcePortalContextualization CMS item force portal contextualization indicator
     * @param supportsPortalForms CMS item type supports portal forms indicator
     * @param portalFormSubTypes CMS item type portal from sub types
     * @param defaultTemplate CMS item default template path, may be null for global default template path
     * @param glyph CMS item customized glyph, may be null for default glyph
     */
    public CMSItemType(String name, boolean folderish, boolean navigable, boolean browsable, boolean ordered, boolean forcePortalContextualization,
            boolean supportsPortalForms, List<String> portalFormSubTypes, String defaultTemplate, String glyph, boolean isRootType) {
        super();
        this.name = name;
        this.folderish = folderish;
        this.navigable = navigable;
        this.browsable = browsable;
        this.ordered = ordered;
        this.forcePortalContextualization = forcePortalContextualization;
        this.supportsPortalForms = supportsPortalForms;
        this.portalFormSubTypes = portalFormSubTypes;
        this.defaultTemplate = defaultTemplate;
        this.glyph = glyph;
        this.isRootType = isRootType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
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
        CMSItemType other = (CMSItemType) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CMSItemType [name=" + this.name + "]";
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
     * Getter for folderish.
     *
     * @return the folderish
     */
    public boolean isFolderish() {
        return this.folderish;
    }

    /**
     * Getter for navigable.
     *
     * @return the navigable
     */
    public boolean isNavigable() {
        return this.navigable;
    }

    /**
     * Getter for browsable.
     *
     * @return the browsable
     */
    public boolean isBrowsable() {
        return this.browsable;
    }

    /**
     * Getter for ordered.
     *
     * @return the ordered
     */
    public boolean isOrdered() {
        return this.ordered;
    }

    /**
     * Getter for forcePortalContextualization.
     *
     * @return the forcePortalContextualization
     */
    public boolean isForcePortalContextualization() {
        return this.forcePortalContextualization;
    }

    /**
     * Getter for supportsPortalForms.
     *
     * @return the supportsPortalForms
     */
    public boolean isSupportsPortalForms() {
        return this.supportsPortalForms;
    }

    /**
     * Getter for portalFormSubTypes.
     *
     * @return the portalFormSubTypes
     */
    public List<String> getPortalFormSubTypes() {
        return this.portalFormSubTypes;
    }

    /**
     * Getter for defaultTemplate.
     *
     * @return the defaultTemplate
     */
    public String getDefaultTemplate() {
        return this.defaultTemplate;
    }

    /**
     * Getter for glyph.
     *
     * @return the glyph
     */
    public String getGlyph() {
        return this.glyph;
    }

    /**
     * is root type
     * @return is root type ?
     */
	public boolean isRootType() {
		return isRootType;
	}
    
    

}
