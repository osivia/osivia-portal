/******************************************************************************
 * JBoss, a division of Red Hat *
 * Copyright 2009, Red Hat Middleware, LLC, and individual *
 * contributors as indicated by the @authors tag. See the *
 * copyright.txt in the distribution for a full listing of *
 * individual contributors. *
 * *
 * This is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as *
 * published by the Free Software Foundation; either version 2.1 of *
 * the License, or (at your option) any later version. *
 * *
 * This software is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU *
 * Lesser General Public License for more details. *
 * *
 * You should have received a copy of the GNU Lesser General Public *
 * License along with this software; if not, write to the Free *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. *
 ******************************************************************************/


package org.jboss.portal.theme.impl.render.dynamic;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.jboss.portal.theme.render.*;
import org.jboss.portal.theme.render.renderer.RegionRenderer;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;

import java.io.PrintWriter;
import java.util.Collection;

/**
 * Implementation of a drag and drop Region renderer.
 *
 * @author <a href="mailto:tomasz.szymanski@jboss.com">Tomasz Szymanski</a>
 * @author <a href="mailto:roy@jboss.org">Roy Russo</a>
 * @see org.jboss.portal.theme.render.renderer.RegionRenderer
 */
public class DynaRegionRenderer extends AbstractObjectRenderer implements RegionRenderer {

    /**
     * .
     */
    private static final PropertyFetch RENDER_OPTIONS_FETCH = new PropertyFetch(PropertyFetch.ANCESTORS_SCOPE);

    /**
     * .
     */
    private final RegionRenderer delegate;

    public DynaRegionRenderer(RegionRenderer regionRenderer) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        super();
        this.delegate = regionRenderer;
    }

    @Override
    public void startContext(RendererContext rendererContext, ObjectRendererContext objectRenderContext) {
        RegionRendererContext rrc = (RegionRendererContext) objectRenderContext;

        //
        if ("AJAXScripts".equals(rrc.getId()) || "AJAXFooter".equals(rrc.getId())) {
            DynaRenderStatus.set(rendererContext, false);
        } else {
            // Get ancestors options
            String ancestorsDndValue = rendererContext.getProperty(DynaRenderOptions.DND_ENABLED, RENDER_OPTIONS_FETCH);
            String ancestorsPartialRefreshValue = rendererContext.getProperty(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, RENDER_OPTIONS_FETCH);
            DynaRenderOptions ancestorsOptions = DynaRenderOptions.getOptions(ancestorsDndValue, ancestorsPartialRefreshValue);

            // Get regions options
            String regionDndValue = rendererContext.getProperty(DynaRenderOptions.DND_ENABLED);
            String regionPartialRefreshValue = rendererContext.getProperty(DynaRenderOptions.PARTIAL_REFRESH_ENABLED);
            DynaRenderOptions regionOptions = DynaRenderOptions.getOptions(regionDndValue, regionPartialRefreshValue);

            // Merge options
            DynaRenderOptions options = DynaMergeBehavior.mergeForRegion(ancestorsOptions, regionOptions);

            //
            rendererContext.setAttribute(DynaConstants.RENDER_OPTIONS, options);
            DynaRenderStatus.set(rendererContext, true);
        }
    }


    @Override
    public void endContext(RendererContext rendererContext, ObjectRendererContext objectRenderContext) {
        if (DynaRenderStatus.isActive(rendererContext)) {
            rendererContext.setAttribute(DynaConstants.RENDER_OPTIONS, null);
        }
    }

    public void renderHeader(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {
        PrintWriter markup = rendererContext.getWriter();
        String serverBaseURL = rendererContext.getProperty(DynaConstants.SERVER_BASE_URL);
        String viewState = rendererContext.getProperty(DynaConstants.VIEW_STATE);
        String popStateURL = rendererContext.getProperty("osivia.popStateUrl");


        // Handle special ajax region here
        if ("AJAXScripts".equals(rrc.getId())) {
            markup.print("<script type='text/javascript'>\n");

            // Async server URL needed for callbacks
            markup.print("server_base_url=\"");
            markup.print(serverBaseURL);
            markup.print("\";\n");

            // View state if not null
            if (viewState != null) {
                markup.print("view_state = \"");
                markup.print(viewState);
                markup.print("\";\n");
            } else {
                markup.print("view_state = null;");
            }

            // reload in ajax mode
            markup.print("popStateUrl=\"");
            markup.print(popStateURL);
            markup.print("\";\n");

            //
            markup.print("</script>\n");
        } else if ("AJAXFooter".equals(rrc.getId())) {
            markup.print("<script type='text/javascript'>footer()</script>\n");
        }

        //
        if (DynaRenderStatus.isActive(rendererContext) && this.isDisplayed(rrc)) {
            //
            DynaRenderOptions options = (DynaRenderOptions) rendererContext.getAttribute(DynaConstants.RENDER_OPTIONS);

            //
            if (!DynaRenderOptions.NO_AJAX.equals(options)) {
                //
                markup.print("<div class=\"dyna-region\">");

                //
                this.delegate.renderHeader(rendererContext, rrc);

                //
                if (options.isDnDEnabled()) {
                    markup.print("<div class=\"dnd-region\" id=\"");
                    markup.print(rrc.getId());
                    markup.print("\">");
                }
            } else {
                this.delegate.renderHeader(rendererContext, rrc);
            }
        }
    }

    /**
     * @see org.jboss.portal.theme.render.renderer.RegionRenderer#renderBody
     */
    public void renderBody(RendererContext rendererContext, final RegionRendererContext rrc) throws RenderException {
        if (DynaRenderStatus.isActive(rendererContext) && this.isDisplayed(rrc)) {
            this.delegate.renderBody(rendererContext, rrc);
        }
    }

    public void renderFooter(RendererContext rendererContext, RegionRendererContext rrc) throws RenderException {
        if (DynaRenderStatus.isActive(rendererContext) && this.isDisplayed(rrc)) {
            DynaRenderOptions options = (DynaRenderOptions) rendererContext.getAttribute(DynaConstants.RENDER_OPTIONS);

            //
            if (!DynaRenderOptions.NO_AJAX.equals(options)) {
                //
                PrintWriter markup = rendererContext.getWriter();

                // Close dnd-region
                if (options.isDnDEnabled()) {
                    markup.print("</div>");
                }

                // Close dyna-region
                markup.print("</div>");
            }

            //
            this.delegate.renderFooter(rendererContext, rrc);
        }
    }


    /**
     * Check if related region is displayed.
     *
     * @param regionRendererContext region renderer context
     * @return true if related region is displayed
     */
    private boolean isDisplayed(RegionRendererContext regionRendererContext) {
        boolean displayed;

        // Wizard mode indicator
        boolean wizard = StringUtils.equals("pageTemplate", regionRendererContext.getProperty("osivia.wizzardMode"));
        // Show CMS tools indicator
        boolean showCmsTools = BooleanUtils.toBoolean(regionRendererContext.getProperty("osivia.cms.showTools"));

        if (wizard || showCmsTools) {
            displayed = true;
        } else {
            // Empty region indicator
            boolean empty;

            Collection<?> windows = regionRendererContext.getWindows();
            if (CollectionUtils.isEmpty(windows)) {
                empty = true;
            } else if (CollectionUtils.size(windows) == 1) {
                Object object = CollectionUtils.get(windows, 0);

                if (object instanceof WindowContext) {
                    WindowContext windowContext = (WindowContext) object;
                    WindowResult windowResult = windowContext.getResult();
                    empty = (windowResult != null) && "PIA_EMPTY".equals(windowResult.getTitle()) && StringUtils.isEmpty(windowResult.getContent());
                } else {
                    empty = false;
                }
            } else {
                empty = false;
            }

            displayed = !empty;
        }

        return displayed;
    }

}
