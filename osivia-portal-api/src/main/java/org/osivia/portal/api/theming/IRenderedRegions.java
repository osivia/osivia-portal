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
package org.osivia.portal.api.theming;


/**
 * Rendered regions interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IRenderedRegions {

    /** Regions theming customizer identifier. */
    static final String CUSTOMIZER_ID = "osivia.customizer.regions.id";
    /** Regions theming customizer layout context path attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_LAYOUT_CONTEXT_PATH = "osivia.customizer.regions.layoutContextPath";
    /** Regions theming customizer theme context path attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_THEME_CONTEXT_PATH = "osivia.customizer.regions.themeContextPath";
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
     * Customize rendered region.
     * 
     * @param regionName region name
     * @param regionPath region path
     * @return true if the region was successfully customized
     */
    boolean customizeRenderedRegion(String regionName, String regionPath);


    /**
     * Remove rendered region.
     *
     * @param regionName region name
     * @return true if the region was successfully removed
     */
    boolean removeRenderedRegion(String regionName);


    /**
     * Decorate portlets region with header and/or footer.
     *
     * @param regionName portlets region name
     * @param headerPath header path, may be null
     * @param footerPath footer path, may be null
     * @return true if the region was successfully decorated
     */
    boolean decoratePortletsRegion(String regionName, String headerPath, String footerPath);


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
