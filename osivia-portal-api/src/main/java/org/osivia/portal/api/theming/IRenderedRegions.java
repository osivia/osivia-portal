package org.osivia.portal.api.theming;


/**
 * Rendered regions interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IRenderedRegions {

    /** Regions theming customizer identifier. */
    static final String CUSTOMIZER_ID = "osivia.customizer.regions.id";
    /** Regions theming customizer context path attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_CONTEXT_PATH = "osivia.customizer.regions.contextPath";
    /** Regions theming customizer administrator indicator attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_ADMINISTATOR = "osivia.customizer.regions.administrator";
    /** Regions theming customizer context path attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_RENDERED_REGIONS = "osivia.customizer.regions.renderedRegions";


    /**
     * Check if current site is a space site.
     *
     * @return true if current site is a space site
     */
    boolean isSpaceSite();


    /**
     * Define rendered region.
     * 
     * @param regionName region name
     * @param regionPath region path
     * @return true if the region was successfully added
     */
    boolean defineRenderedRegion(String regionName, String regionPath);


    /**
     * Remove rendered region.
     * 
     * @param regionName region name
     * @return true if the region was successfully removed
     */
    boolean removeRenderedRegion(String regionName);


    /**
     * Define default rendered region.
     * Don't use this method for customizers !
     *
     * @param regionName region name
     * @param regionPath region path
     */
    void defineDefaultRenderedRegion(String regionName, String regionPath);


    /**
     * Define fixed rendered region.
     * Don't use this method for customizers !
     *
     * @param regionName region name
     * @param regionPath region path
     */
    void defineFixedRenderedRegion(String regionName, String regionPath);

}
