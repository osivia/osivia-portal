package org.osivia.portal.core.theming.temp;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.theme.Orientation;
import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;

/**
 * Region theming renderer context implementation.
 *
 * @author Cédric Krommenhoek
 * @see RegionRendererContext
 */
public class RegionThemingRendererContext implements IRegionThemingRendererContext {

    /** Region renderer context. */
    private final RegionRendererContext region;
    /** Region identifier. */
    private final String id;
    /** Region HTML identifier for CSS styles. */
    private final String cssId;
    /** Region CMS indicator. */
    private final boolean regionCms;


    /**
     * Constructor.
     *
     * @param page page renderer context
     * @param name region name
     * @param id region HTML identifier for CSS styles, may be null
     */
    public RegionThemingRendererContext(PageRendererContext page, String name, String id, Boolean regionCms) {
        this.region = page.getRegion(name);

        if (this.region == null) {
            this.id = name;
        } else {
            this.id = this.region.getId();
        }

        if (StringUtils.isBlank(id)) {
            this.cssId = name;
        } else {
            this.cssId = id;
        }

        this.regionCms = BooleanUtils.isTrue(regionCms);
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
    public String getId() {
        return this.id;
    }


    /**
     * {@inheritDoc}
     */
    public Collection<?> getWindows() {
        if (this.region == null) {
            return CollectionUtils.EMPTY_COLLECTION;
        } else {
            return this.region.getWindows();
        }
    }


    /**
     * {@inheritDoc}
     */
    public Orientation getOrientation() {
        return Orientation.DEFAULT;
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
    public Boolean getRegionCms() {
        return this.regionCms;
    }

}
