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

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.jboss.portal.theme.Orientation;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;

/**
 * Region renderer context implementation.
 *
 * @author Cédric Krommenhoek
 * @see IRegionRendererContext
 */
public class RegionRendererContextImpl implements IRegionRendererContext {

    /** Current region. */
    private final RegionRendererContext region;
    /** Region name. */
    private final String name;
    /** Region CSS identifier. */
    private final String cssId;
    /** Region CMS indicator. */
    private final Boolean cms;
    /** Region orientation. */
    private final Orientation orientation;


    /**
     * Constructor.
     *
     * @param region current region
     * @param name region name
     * @param cssId region CSS identifier
     * @param cms region CMS indicator
     * @param orientation region orientation
     */
    public RegionRendererContextImpl(RegionRendererContext region, String name, String cssId, Boolean cms, Orientation orientation) {
        super();
        this.region = region;
        this.name = name;
        this.cssId = cssId;
        this.cms = cms;
        this.orientation = orientation;
    }


    /**
     * {@inheritDoc}
     */
    public String getId() {
        if (this.region == null) {
            return this.name;
        } else {
            return this.region.getId();
        }
    }


    /**
     * {@inheritDoc}
     */
    public Collection<?> getWindows() {
        if (this.region == null) {
            return CollectionUtils.EMPTY_COLLECTION;
        } else {
            for (Object window : this.region.getWindows()) {
                if (window instanceof WindowContext) {
                    WindowContext w = (WindowContext) window;
                    w.setRegionCms(this.cms);
                }
            }

            return this.region.getWindows();
        }
    }


    /**
     * {@inheritDoc}
     */
    public Orientation getOrientation() {
        if (this.orientation == null) {
            return Orientation.DEFAULT;
        } else {
            return this.orientation;
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getCSSId() {
        return this.cssId;
    }


    /**
     * {@inheritDoc}
     */
    public String getProperty(String name) {
        if (this.region == null) {
            return null;
        } else {
            return this.region.getProperty(name);
        }
    }


    /**
     * {@inheritDoc}
     */
    public Map<?, ?> getProperties() {
        if (this.region == null) {
            return MapUtils.EMPTY_MAP;
        } else {
            return this.region.getProperties();
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isCMS() {
        return BooleanUtils.isTrue(this.cms);
    }

}
