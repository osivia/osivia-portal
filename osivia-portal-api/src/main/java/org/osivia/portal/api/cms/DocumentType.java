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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.portlet.PortalGenericPortlet;

/**
 * Document type type java-bean.
 *
 * @author Cédric Krommenhoek
 * @see Cloneable
 */
public class DocumentType implements Cloneable {

    /** Folderish indicator. */
    private boolean folderish;
    /** Browsable indicator. */
    private boolean browsable;
    /** Navigable indicator. */
    private boolean navigable;
    /** Editable indicator. */
    private boolean ordered;
    /** Force portal contextualization indicator. */
    private boolean forceContextualization;
    /** Root type indicator. */
    private boolean root;
    /** File type indicator. */
    private boolean file;

    /** Icon, may be null for default glyph. */
    private String icon;
    /** Editable indicator. */
    private boolean editable;
    /** Movable indicator. */
    private boolean movable;
    /** Prevented creation indicator. */
    private boolean preventedCreation;
    /** Template path, may be null for global default template path. */
    private String template;


    /** Name. */
    private final String name;
    /** Subtypes. */
    private final List<String> subtypes;

    /** Customized class loader. */
    private final ClassLoader customizedClassLoader;
    /** Log. */
    private final Log log;


    /**
     * Constructor.
     *
     * @param name document type name
     */
    public DocumentType(String name) {
        super();
        this.name = name;
        this.subtypes = new ArrayList<>();

        // Customized class loader
        this.customizedClassLoader = PortalGenericPortlet.CLASS_LOADER_CONTEXT.get();
        // Log
        this.log = LogFactory.getLog(this.getClass());
    }


    /**
     * Constructor.
     * 
     * @param name document type name
     * @param category document type category
     */
    private DocumentType(String name, Category category) {
        this(name);

        if (category != null) {
            this.folderish = category.folderish;
            this.browsable = category.folderish;
            this.navigable = category.folderish && !Category.ROOT.equals(category);
            this.ordered = Category.ROOT.equals(category);
            this.forceContextualization = Category.ROOT.equals(category);
            this.root = Category.ROOT.equals(category);
            this.file = Category.FILE.equals(category);
        }
    }


    /**
     * Create root document type.
     * 
     * @param name document type name
     * @return document type
     */
    public static DocumentType createRoot(String name) {
        return new DocumentType(name, Category.ROOT);
    }


    /**
     * Create node document type.
     * 
     * @param name document type name
     * @return document type
     */
    public static DocumentType createNode(String name) {
        return new DocumentType(name, Category.NODE);
    }


    /**
     * Create leaf document type.
     * 
     * @param name document type name
     * @return document type
     */
    public static DocumentType createLeaf(String name) {
        return new DocumentType(name, Category.LEAF);
    }


    /**
     * Create file document type.
     * 
     * @param name document type name
     * @return document type
     */
    public static DocumentType createFile(String name) {
        return new DocumentType(name, Category.FILE);
    }


    /**
     * Add subtypes.
     * 
     * @param names subtype names
     */
    public void addSubtypes(String... names) {
        this.subtypes.addAll(Arrays.asList(names));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentType clone() {
        // Clone
        DocumentType clone = new DocumentType(this.name);

        // Subtypes
        clone.subtypes.addAll(this.subtypes);

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
     * Getter for folderish.
     * 
     * @return the folderish
     */
    public boolean isFolderish() {
        return folderish;
    }

    /**
     * Setter for folderish.
     * 
     * @param folderish the folderish to set
     */
    public void setFolderish(boolean folderish) {
        this.folderish = folderish;
    }

    /**
     * Getter for browsable.
     * 
     * @return the browsable
     */
    public boolean isBrowsable() {
        return browsable;
    }

    /**
     * Setter for browsable.
     * 
     * @param browsable the browsable to set
     */
    public void setBrowsable(boolean browsable) {
        this.browsable = browsable;
    }

    /**
     * Getter for navigable.
     * 
     * @return the navigable
     */
    public boolean isNavigable() {
        return navigable;
    }

    /**
     * Setter for navigable.
     * 
     * @param navigable the navigable to set
     */
    public void setNavigable(boolean navigable) {
        this.navigable = navigable;
    }

    /**
     * Getter for ordered.
     * 
     * @return the ordered
     */
    public boolean isOrdered() {
        return ordered;
    }

    /**
     * Setter for ordered.
     * 
     * @param ordered the ordered to set
     */
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    /**
     * Getter for forceContextualization.
     * 
     * @return the forceContextualization
     */
    public boolean isForceContextualization() {
        return forceContextualization;
    }

    /**
     * Setter for forceContextualization.
     * 
     * @param forceContextualization the forceContextualization to set
     */
    public void setForceContextualization(boolean forceContextualization) {
        this.forceContextualization = forceContextualization;
    }

    /**
     * Getter for root.
     * 
     * @return the root
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * Setter for root.
     * 
     * @param root the root to set
     */
    public void setRoot(boolean root) {
        this.root = root;
    }

    /**
     * Getter for file.
     * 
     * @return the file
     */
    public boolean isFile() {
        return file;
    }

    /**
     * Setter for file.
     * 
     * @param file the file to set
     */
    public void setFile(boolean file) {
        this.file = file;
    }

    /**
     * Getter for icon.
     * 
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Setter for icon.
     * 
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Getter for editable.
     * 
     * @return the editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Setter for editable.
     * 
     * @param editable the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Getter for movable.
     * 
     * @return the movable
     */
    public boolean isMovable() {
        return movable;
    }

    /**
     * Setter for movable.
     * 
     * @param movable the movable to set
     */
    public void setMovable(boolean movable) {
        this.movable = movable;
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

    /**
     * Getter for template.
     * 
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Setter for template.
     * 
     * @param template the template to set
     */
    public void setTemplate(String template) {
        this.template = template;
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
     * Getter for subtypes.
     * 
     * @return the subtypes
     */
    public List<String> getSubtypes() {
        return subtypes;
    }

    /**
     * Getter for customizedClassLoader.
     * 
     * @return the customizedClassLoader
     */
    public ClassLoader getCustomizedClassLoader() {
        return customizedClassLoader;
    }


    /**
     * Document type category enumeration.
     * 
     * @author Cédric Krommenhoek
     */
    private enum Category {

        /** Root type. */
        ROOT(true),
        /** Node type. */
        NODE(true),
        /** Leaf type. */
        LEAF(false),
        /** File type. */
        FILE(false);


        /** Folderish indicator. */
        private final boolean folderish;


        /**
         * Constructor.
         * 
         * @param folderish folderish indicator
         */
        private Category(boolean folderish) {
            this.folderish = folderish;
        }
    }

}
