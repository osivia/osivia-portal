package org.osivia.portal.api.theming;

/**
 * Rendered region java bean.
 *
 * @author Cédric Krommenhoek
 */
public class RenderedRegionBean {

    /** Rendered region name. */
    private final String name;
    /** Rendered region path. */
    private final String path;
    /** Default region indicator (default value is false). */
    private boolean defaultRegion;
    /** Customizable region indicator (default value is true). */
    private boolean customizable;


    /**
     * Constructor.
     *
     * @param name rendered region name
     * @param path rendered region path
     */
    public RenderedRegionBean(String name, String path) {
        super();
        this.name = name;
        this.path = path;
        this.defaultRegion = false;
        this.customizable = true;
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
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Getter for defaultRegion.
     *
     * @return the defaultRegion
     */
    public boolean isDefaultRegion() {
        return this.defaultRegion;
    }

    /**
     * Setter for defaultRegion.
     *
     * @param defaultRegion the defaultRegion to set
     */
    public void setDefaultRegion(boolean defaultRegion) {
        this.defaultRegion = defaultRegion;
    }

    /**
     * Getter for customizable.
     *
     * @return the customizable
     */
    public boolean isCustomizable() {
        return this.customizable;
    }

    /**
     * Setter for customizable.
     *
     * @param customizable the customizable to set
     */
    public void setCustomizable(boolean customizable) {
        this.customizable = customizable;
    }

}
