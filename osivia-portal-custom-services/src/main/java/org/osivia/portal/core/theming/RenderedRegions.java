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
        return this.customizeRenderedRegion(regionName, regionPath, null);
    }


    /**
     * {@inheritDoc}
     */
    public boolean customizeRenderedRegion(String regionName, String regionPath, String contextPath) {
        boolean customizable = true;
        if (this.renderedRegions.containsKey(regionName)) {
            customizable = this.renderedRegions.get(regionName).isCustomizable();
        }

        if (customizable) {
            RenderedRegionBean region = new RenderedRegionBean(regionName, regionPath);
            region.setContextPath(contextPath);
            this.renderedRegions.put(regionName, region);
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
        return this.decoratePortletsRegion(regionName, headerPath, footerPath, null);
    }


    /**
     * {@inheritDoc}
     */
    public boolean decoratePortletsRegion(String regionName, String headerPath, String footerPath, String contextPath) {
        boolean decoratable = true;
        if (this.renderedRegions.containsKey(regionName)) {
            AbstractRegionBean region = this.renderedRegions.get(regionName);
            if (!(region instanceof PortletsRegionBean)) {
                decoratable = false;
            }
        }

        if (decoratable) {
            PortletsRegionBean region = new PortletsRegionBean(regionName, headerPath, footerPath);
            region.setContextPath(contextPath);
            this.renderedRegions.put(regionName, region);
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
