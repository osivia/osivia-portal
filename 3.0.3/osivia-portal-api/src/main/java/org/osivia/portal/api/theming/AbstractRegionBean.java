package org.osivia.portal.api.theming;

/**
 * Region java bean abstract super-class.
 *
 * @author Cédric Krommenhoek
 */
public abstract class AbstractRegionBean {

    /** Region name. */
    private final String name;


    /**
     * Constructor.
     *
     * @param name region name
     */
    public AbstractRegionBean(String name) {
        super();
        this.name = name;
    }

    
    /**
     * Check if current region is customizable.
     * @return true if current region is customizable
     */
    public abstract boolean isCustomizable();
    

    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

}
