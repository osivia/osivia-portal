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
package org.osivia.portal.core.tag;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.Orientation;
import org.jboss.portal.theme.impl.JSPRendererContext;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.renderer.PageRendererContext;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.theming.IRegionRendererContext;
import org.osivia.portal.core.theming.RegionDecorator;
import org.osivia.portal.core.theming.RegionRendererContextImpl;

/**
 * Region tag handler.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class RegionTagHandler extends SimpleTagSupport {

    /** Logger. */
    private static final Log log = LogFactory.getLog(RegionTagHandler.class);

    /** Region name. */
    private String regionName;
    /** Region CSS identifier. */
    private String regionID;
    /** Region CMS indicator. */
    private Boolean cms;
    /** Region orientation. */
    private Orientation orientation;


    /**
     * Default constructor.
     */
    public RegionTagHandler() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) this.getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        Boolean layoutParsing = (Boolean) request.getAttribute(InternalConstants.ATTR_LAYOUT_PARSING);
        if (BooleanUtils.isTrue(layoutParsing)) {
            this.parseRegionAttributes(request);
        } else {
            this.renderRegion(request);
        }
    }


    /**
     * Utility method used to parse region attributes.
     *
     * @param request current HTTP servlet request
     */
    private void parseRegionAttributes(HttpServletRequest request) {
        
        Set<String> visibleRegions = (Set<String>) request.getAttribute(InternalConstants.ATTR_LAYOUT_VISIBLE_REGIONS);
        visibleRegions.add(regionName);
        
        // Check if layout contains CMS
        Boolean cms = (Boolean) request.getAttribute(InternalConstants.ATTR_LAYOUT_CMS_INDICATOR);
        if (BooleanUtils.isNotTrue(cms)) {
            request.setAttribute(InternalConstants.ATTR_LAYOUT_CMS_INDICATOR, BooleanUtils.isTrue(this.cms));
        }
    }


    /**
     * Utility method used to render current region.
     *
     * @param request current HTTP servlet request
     * @throws JspException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void renderRegion(HttpServletRequest request) throws JspException, IOException {
        if (this.regionID == null) {
            this.regionID = this.regionName;
        }
        log.debug("rendering " + this.regionName + " [" + this.orientation + "]  cssId[" + this.regionID + "]");

        // JSP writer
        JspWriter out = this.getJspContext().getOut();

        // Current page
        PageRendererContext page = (PageRendererContext) request.getAttribute(LayoutConstants.ATTR_PAGE);
        if (page == null) {
            out.write("<p bgcolor='red'>No page to render!</p>");
            out.write("<p bgcolor='red'>The page to render (PageResult) must be set in the request attribute '" + LayoutConstants.ATTR_PAGE + "'</p>");
            out.flush();
            return;
        }

        // Current region, may be null
        RegionRendererContext currentRegion = page.getRegion(this.regionName);

        // Region renderer context
        IRegionRendererContext regionRendererContext = new RegionRendererContextImpl(currentRegion, this.regionName, this.regionID, this.cms, this.orientation);
        // JSP renderer context
        JSPRendererContext renderContext = (JSPRendererContext) request.getAttribute(LayoutConstants.ATTR_RENDERCONTEXT);

        // Decorator
        RegionDecorator decorator = null;
        Map<String, RegionDecorator> decorators = (Map<String, RegionDecorator>) request.getAttribute(InternalConstants.ATTR_REGIONS_DECORATORS);
        if (decorators != null) {
            decorator = decorators.get(this.regionName);
        }
        renderContext.setAttribute(InternalConstants.ATTR_REGIONS_DECORATORS, decorator);


        try {
            PrintWriter writer = new PrintWriter(out);
            renderContext.setWriter(writer);
            renderContext.render(regionRendererContext);
            writer.flush();
        } catch (RenderException e) {
            throw new JspException(e);
        }
    }


    /**
     * Setter for regionName.
     *
     * @param regionName the regionName to set
     */
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    /**
     * Setter for regionID.
     *
     * @param regionID the regionID to set
     */
    public void setRegionID(String regionID) {
        this.regionID = regionID;
    }

    /**
     * Setter for cms.
     *
     * @param cms the cms to set
     */
    public void setCms(Boolean cms) {
        this.cms = cms;
    }

    /**
     * Setter for orientation.
     *
     * @param orientation the orientation to set
     */
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

}
