package org.osivia.portal.api.panels;

/**
 * Panels enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum Panel {

    /** Navigation panel. */
    NAVIGATION_PANEL("navigation-panel");


    /** Region name. */
    private final String regionName;
    /** Window name. */
    private final String windowName;
    /** Closed panel indicator request attribute name. */
    private final String closedAttribute;


    /**
     * Constructor.
     *
     * @param regionName region name
     */
    private Panel(String regionName) {
        this.regionName = regionName;
        this.windowName = regionName + "-window";
        this.closedAttribute = "osivia.panels." + regionName + ".closed";
    }


    /**
     * Getter for regionName.
     *
     * @return the regionName
     */
    public String getRegionName() {
        return this.regionName;
    }

    /**
     * Getter for windowName.
     *
     * @return the windowName
     */
    public String getWindowName() {
        return this.windowName;
    }

    /**
     * Getter for closedAttribute.
     * 
     * @return the closedAttribute
     */
    public String getClosedAttribute() {
        return this.closedAttribute;
    }

}
