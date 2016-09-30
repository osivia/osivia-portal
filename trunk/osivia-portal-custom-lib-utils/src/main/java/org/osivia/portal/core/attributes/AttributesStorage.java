package org.osivia.portal.core.attributes;

/**
 * Attributes storage enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum AttributesStorage {

    /** Selection. */
    SELECTION("selection"),
    /** Portlet sequencing. */
    PORTLET_SEQUENCING("sequencing");


    /** Storage attribute name prefix. */
    private static final String STORAGE_PREFIX = "osivia.attributes.storage.";
    /** Timestamp attribute name prefix. */
    private static final String TIMESTAMP_PREFIX = "osivia.attributes.ts.";


    /** Storage attribute name. */
    private final String attributeName;
    /** Timestamp attribute name. */
    private final String timestampAttributeName;


    /**
     * Constructor.
     *
     * @param name storage name
     */
    private AttributesStorage(String name) {
        this.attributeName = STORAGE_PREFIX + name;
        this.timestampAttributeName = TIMESTAMP_PREFIX + name;
    }


    /**
     * Getter for attributeName.
     * 
     * @return the attributeName
     */
    public String getAttributeName() {
        return this.attributeName;
    }

    /**
     * Getter for timestampAttributeName.
     * 
     * @return the timestampAttributeName
     */
    public String getTimestampAttributeName() {
        return this.timestampAttributeName;
    }

}
