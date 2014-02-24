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


    /**
     * Constructor.
     * 
     * @param name CMS item type name
     * @param folderish CMS item type is folderish indicator
     * @param navigable CMS item navigable indicator
     * @param ordered CMS item type is ordered indicator
     * @param forcePortalContextualization CMS item force portal contextualization indicator
     * @param supportsPortalForms CMS item type supports portal forms indicator
     * @param portalFormSubTypes CMS item type portal from sub types
     * @param defaultTemplate CMS item default template path, may be null for global default template path
     */
    public CMSItemType(String name, boolean folderish, boolean navigable, boolean ordered, boolean forcePortalContextualization, boolean supportsPortalForms,
            List<String> portalFormSubTypes, String defaultTemplate) {
        super();
        this.name = name;
        this.folderish = folderish;
        this.navigable = navigable;
        this.ordered = ordered;
        this.forcePortalContextualization = forcePortalContextualization;
        this.supportsPortalForms = supportsPortalForms;
        this.portalFormSubTypes = portalFormSubTypes;
        this.defaultTemplate = defaultTemplate;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
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
     * Getter for ordered.
     * 
     * @return the ordered
     */
    public boolean isOrdered() {
        return this.ordered;
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
     * Getter for navigable.
     * 
     * @return the navigable
     */
    public boolean isNavigable() {
        return navigable;
    }

    /**
     * Getter for forcePortalContextualization.
     * 
     * @return the forcePortalContextualization
     */
    public boolean isForcePortalContextualization() {
        return forcePortalContextualization;
    }

    /**
     * Getter for defaultTemplate.
     * 
     * @return the defaultTemplate
     */
    public String getDefaultTemplate() {
        return defaultTemplate;
    }

}
