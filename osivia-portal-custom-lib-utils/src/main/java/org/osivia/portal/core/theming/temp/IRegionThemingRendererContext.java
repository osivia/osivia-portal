package org.osivia.portal.core.theming.temp;

import org.jboss.portal.theme.render.renderer.RegionRendererContext;

/**
 * Region theming renderer context interface.
 *
 * @author Cédric Krommenhoek
 * @see RegionRendererContext
 */
public interface IRegionThemingRendererContext extends RegionRendererContext {

    /**
     * Access to region CMS indicator.
     *
     * @return true if current region is a region with CMS
     */
    Boolean getRegionCms();

}
