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
package org.osivia.portal.api.cms;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.portlet.PortalGenericPortlet;

/**
 * CMS item type java-bean.
 *
 * @author Cédric Krommenhoek
 * @see Cloneable
 */
public class DocumentType implements Cloneable {

    /** CMS item type name. */
    private final String name;
    /** CMS item type folderish indicator. */
    private final boolean folderish;
    /** CMS item navigable indicator. */
    private final boolean navigable;
    /** CMS item browsable indicator. */
    private final boolean browsable;
    /** CMS item movable indicator. */
    private final boolean movable;
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
    /** CMS item is root type */
    private final boolean isRootType;
    /** CMS item live editable indicator. */
    private final boolean liveEditable;

    /** CMS item customized class loader. */
    private final ClassLoader customizedClassLoader;
    /** Log. */
    private final Log log;


    /** CMS item glyph, may be null for default glyph. */
    private String glyph;
    /** CMS item editorial content indicator, may be null. */
    private Boolean editorialContent;
    /** CMS item type is a file type indicator. */
    private boolean isFile;
    /** Prevented creation indicator. */
    private boolean preventedCreation;


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
    public DocumentType(String name, boolean folderish, boolean navigable, boolean browsable, boolean ordered, boolean forcePortalContextualization,
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
    public DocumentType(String name, boolean folderish, boolean navigable, boolean browsable, boolean ordered, boolean forcePortalContextualization,
            boolean supportsPortalForms, List<String> portalFormSubTypes, String defaultTemplate, String glyph) {
        this(name, folderish, navigable, browsable, ordered, forcePortalContextualization, supportsPortalForms, portalFormSubTypes, defaultTemplate, glyph,
                false, false);
    }


    /**
     * Constructor.
     *
     * @param name the name
     * @param folderish the folderish
     * @param navigable the navigable
     * @param browsable the browsable
     * @param ordered the ordered
     * @param forcePortalContextualization the force portal contextualization
     * @param supportsPortalForms the supports portal forms
     * @param portalFormSubTypes the portal form sub types
     * @param defaultTemplate the default template
     * @param glyph the glyph
     * @param isRootType the is root type
     */
    public DocumentType(String name, boolean folderish, boolean navigable, boolean browsable, boolean ordered, boolean forcePortalContextualization,
            boolean supportsPortalForms, List<String> portalFormSubTypes, String defaultTemplate, String glyph, boolean isRootType) {
        this(name, folderish, navigable, browsable, ordered, forcePortalContextualization, supportsPortalForms, portalFormSubTypes, defaultTemplate, glyph,
                isRootType, false);
    }


    /**
     * @param name
     * @param folderish
     * @param navigable
     * @param browsable
     * @param ordered
     * @param forcePortalContextualization
     * @param supportsPortalForms
     * @param portalFormSubTypes
     * @param defaultTemplate
     * @param glyph
     * @param isRootType
     * @param movable
     */
    public DocumentType(String name, boolean folderish, boolean navigable, boolean browsable, boolean ordered, boolean forcePortalContextualization,
            boolean supportsPortalForms, List<String> portalFormSubTypes, String defaultTemplate, String glyph, boolean isRootType, boolean movable) {
        this(name, folderish, navigable, browsable, ordered, forcePortalContextualization, supportsPortalForms, portalFormSubTypes, defaultTemplate, glyph,
                isRootType, movable, false);
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
    public DocumentType(String name, boolean folderish, boolean navigable, boolean browsable, boolean ordered, boolean forcePortalContextualization,
            boolean supportsPortalForms, List<String> portalFormSubTypes, String defaultTemplate, String glyph, boolean isRootType, boolean movable,
            boolean liveEditable) {
        super();
        this.name = name;
        this.folderish = folderish;
        this.navigable = navigable;
        this.browsable = browsable;
        this.ordered = ordered;
        this.forcePortalContextualization = forcePortalContextualization;
        this.supportsPortalForms = supportsPortalForms;
        this.portalFormSubTypes = new ArrayList<>(portalFormSubTypes);
        this.defaultTemplate = defaultTemplate;
        this.glyph = glyph;
        this.isRootType = isRootType;
        this.movable = movable;
        this.liveEditable = liveEditable;

        // Customized class loader
        this.customizedClassLoader = PortalGenericPortlet.CLASS_LOADER_CONTEXT.get();
        // Log
        this.log = LogFactory.getLog(this.getClass());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentType clone() {
        // Sub types
        List<String> subTypes = new ArrayList<>(this.portalFormSubTypes);
        // Clone
        DocumentType clone = new DocumentType(this.name, this.folderish, this.navigable, this.browsable, this.ordered, this.forcePortalContextualization,
                this.supportsPortalForms, subTypes, this.defaultTemplate, this.glyph, this.isRootType, this.movable, this.liveEditable);
        // Copy properties
        try {
            BeanUtils.copyProperties(clone, this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            this.log.error(e);
        }

        return clone;
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
        DocumentType other = (DocumentType) obj;
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
        StringBuilder builder = new StringBuilder();
        builder.append("DocumentType [name=");
        builder.append(this.name);
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
     * is root type
     *
     * @return is root type ?
     */
    public boolean isRootType() {
        return this.isRootType;
    }

    /**
     * Checks if is movable.
     *
     * @return true, if is movable
     */
    public boolean isMovable() {
        return this.movable;
    }

    /**
     * Checks if is live editable.
     *
     * @return true, if is live editable
     */
    public boolean isLiveEditable() {
        return this.liveEditable;
    }

    /**
     * Getter for customizedClassLoader.
     *
     * @return the customizedClassLoader
     */
    public ClassLoader getCustomizedClassLoader() {
        return this.customizedClassLoader;
    }

    /**
     * Getter for glyph.
     * 
     * @return the glyph
     */
    public String getGlyph() {
        return glyph;
    }

    /**
     * Setter for glyph.
     * 
     * @param glyph the glyph to set
     */
    public void setGlyph(String glyph) {
        this.glyph = glyph;
    }

    /**
     * Getter for editorialContent.
     *
     * @return the editorialContent
     */
    public Boolean getEditorialContent() {
        return this.editorialContent;
    }

    /**
     * Setter for editorialContent.
     *
     * @param editorialContent the editorialContent to set
     */
    public void setEditorialContent(Boolean editorialContent) {
        this.editorialContent = editorialContent;
    }

    /**
     * Getter for isFile.
     *
     * @return the isFile
     */
    public boolean isFile() {
        return this.isFile;
    }

    /**
     * Setter for isFile.
     *
     * @param isFile the isFile to set
     */
    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    /**
     * Getter for preventedCreation.
     * 
     * @return the preventedCreation
     */
    public boolean isPreventedCreation() {
        return preventedCreation;
    }

    /**
     * Setter for preventedCreation.
     * 
     * @param preventedCreation the preventedCreation to set
     */
    public void setPreventedCreation(boolean preventedCreation) {
        this.preventedCreation = preventedCreation;
    }

}
