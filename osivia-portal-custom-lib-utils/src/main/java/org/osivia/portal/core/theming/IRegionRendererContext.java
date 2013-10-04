package org.osivia.portal.core.theming;

import org.jboss.portal.theme.render.renderer.RegionRendererContext;

/**
 * Region renderer context interface.
 * 
 * @author Cédric Krommenhoek
 * @see RegionRendererContext
 */
public interface IRegionRendererContext extends RegionRendererContext {

    /**
     * Check if current region is a CMS region.
     * 
     * @return true if current region is a CMS region
     */
    boolean isCMS();

}
