package org.osivia.portal.core.cms;

import org.apache.commons.lang.StringUtils;

/**
 * Region inheritance status enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum RegionInheritance {

    /** Default inheritance. */
    DEFAULT(null, "CMS_REGION_INHERITANCE_DEFAULT"),
    /** No inheritance. */
    NO_INHERITANCE("no-inheritance", "CMS_REGION_INHERITANCE_NO_INHERITANCE"),
    /** Propagated. */
    PROPAGATED("propagated", "CMS_REGION_INHERITANCE_PROPAGATED");


    /** Inheritance property value. */
    private final String value;
    /** Internationalization key. */
    private final String internationalizationKey;


    /**
     * Constructor.
     *
     * @param value inheritance property value
     * @param internationalizationKey internationalization key
     */
    private RegionInheritance(String value, String internationalizationKey) {
        this.value = value;
        this.internationalizationKey = internationalizationKey;
    }


    /**
     * Get region inheritance from his property value.
     *
     * @param value region inheritance property value
     * @return region inheritance
     */
    public static final RegionInheritance fromValue(String value) {
        RegionInheritance result = null;

        for (RegionInheritance regionInheritance : RegionInheritance.values()) {
            if (StringUtils.equals(regionInheritance.value, value)) {
                result = regionInheritance;
                break;
            }
        }

        if (result == null) {
            result = DEFAULT;
        }
        return result;
    }


    /**
     * Getter for value.
     *
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Getter for internationalizationKey.
     *
     * @return the internationalizationKey
     */
    public String getInternationalizationKey() {
        return this.internationalizationKey;
    }

}
