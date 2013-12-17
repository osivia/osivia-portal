package org.osivia.portal.core.theming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.Page;
import org.osivia.portal.api.theming.AbstractRegionBean;
import org.osivia.portal.api.theming.IRenderedRegions;
import org.osivia.portal.api.theming.PortletsRegionBean;
import org.osivia.portal.api.theming.RenderedRegionBean;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Rendered regions implementation.
 *
 * @author Cédric Krommenhoek
 * @see IRenderedRegions
 */
public class RenderedRegions implements IRenderedRegions {

    /** Current page. */
    private final Page page;
    /** Regions map. */
    private final Map<String, AbstractRegionBean> renderedRegions;


    /**
     * Constructor.
     *
     * @param page current page
     */
    public RenderedRegions(Page page) {
        super();
        this.page = page;
        this.renderedRegions = new HashMap<String, AbstractRegionBean>();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSpaceSite() {
        return PortalObjectUtils.isSpaceSite(this.page);
    }


    /**
     * {@inheritDoc}
     */
    public boolean customizeRenderedRegion(String regionName, String regionPath) {
        boolean customizable = true;
        if (this.renderedRegions.containsKey(regionName)) {
            customizable = this.renderedRegions.get(regionName).isCustomizable();
        }

        if (customizable) {
            this.renderedRegions.put(regionName, new RenderedRegionBean(regionName, regionPath));
        }
        return customizable;
    }


    /**
     * {@inheritDoc}
     */
    public boolean removeRenderedRegion(String regionName) {
        boolean customizable = true;
        if (this.renderedRegions.containsKey(regionName)) {
            customizable = this.renderedRegions.get(regionName).isCustomizable();
        }

        AbstractRegionBean region = null;
        if (customizable) {
            region = this.renderedRegions.remove(regionName);
        }
        return (region != null);
    }


    /**
     * {@inheritDoc}
     */
    public boolean decoratePortletsRegion(String regionName, String headerPath, String footerPath) {
        boolean decoratable = true;
        if (this.renderedRegions.containsKey(regionName)) {
            AbstractRegionBean region = this.renderedRegions.get(regionName);
            if (!(region instanceof PortletsRegionBean)) {
                decoratable = false;
            }
        }

        if (decoratable) {
            this.renderedRegions.put(regionName, new PortletsRegionBean(regionName, headerPath, footerPath));
        }
        return decoratable;
    }


    /**
     * {@inheritDoc}
     */
    public void defineDefaultRenderedRegion(String regionName, String regionPath) {
        RenderedRegionBean renderedRegion = new RenderedRegionBean(regionName, regionPath);
        renderedRegion.setDefaultRegion(true);
        this.renderedRegions.put(regionName, renderedRegion);
    }


    /**
     * {@inheritDoc}
     */
    public void defineFixedRenderedRegion(String regionName, String regionPath) {
        RenderedRegionBean renderedRegion = new RenderedRegionBean(regionName, regionPath);
        renderedRegion.setDefaultRegion(true);
        renderedRegion.setCustomizable(false);
        this.renderedRegions.put(regionName, renderedRegion);
    }


    /**
     * Get rendered regions.
     *
     * @return rendered regions list
     */
    public List<AbstractRegionBean> getRenderedRegions() {
        return new ArrayList<AbstractRegionBean>(this.renderedRegions.values());
    }

}
