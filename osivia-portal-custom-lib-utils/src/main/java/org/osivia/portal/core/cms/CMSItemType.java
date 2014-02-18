package org.osivia.portal.core.cms;

/**
 * CMS item type.
 * 
 * @author Cédric Krommenhoek
 */
public enum CMSItemType {

    /** CMS space type. */
    SPACE("Space", true),
    /** CMS page type. */
    PAGE("Page", true),
    /** CMS workspace type. */
    WORKSPACE("Workspace", true),
    /** CMS folder type. */
    FOLDER("Folder", true),
    /** CMS generic document type. */
    DOCUMENT("Document", false);

    /** CMS item type name. */
    private final String name;
    /** CMS item type container indicator. */
    private final boolean container;


    /**
     * Constructor.
     * 
     * @param name CMS item type name
     * @param container CMS item type container indicator
     */
    private CMSItemType(String name, boolean container) {
        this.name = name;
        this.container = container;
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
     * Getter for container.
     * 
     * @return the container
     */
    public boolean isContainer() {
        return this.container;
    }

}
