/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/

package org.osivia.portal.core.renderers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.jboss.portal.theme.render.AbstractObjectRenderer;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.renderer.ActionRendererContext;
import org.jboss.portal.theme.render.renderer.DecorationRenderer;
import org.jboss.portal.theme.render.renderer.DecorationRendererContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.PageProperties;

/**
 * Implementation of a decoration renderer, based on div tags.
 *
 * @author <a href="mailto:mholzner@novell.com>Martin Holzner</a>
 * @version $LastChangedRevision: 12912 $, $LastChangedDate: 2009-02-28 11:37:59 -0500 (Sat, 28 Feb 2009) $
 * @see AbstractObjectRenderer
 * @see DecorationRenderer
 */
public class DivDecorationRenderer extends AbstractObjectRenderer implements DecorationRenderer {

    /** Internationalization service. */
    private final IInternationalizationService internationalizationService;


    /**
     * Default constructor.
     */
    public DivDecorationRenderer() {
        super();

        this.internationalizationService = Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    public void render(RendererContext rendererContext, DecorationRendererContext drc) throws RenderException {
        PrintWriter markup = rendererContext.getWriter();

        PageProperties properties = PageProperties.getProperties();

        this.renderTitle(properties, markup, drc);
    }


    /**
     * Render portlet title.
     *
     * @param properties page properties
     * @param markup markup
     * @param drc decoration renderer context
     */
    private void renderTitle(PageProperties properties, PrintWriter markup, DecorationRendererContext drc) {
        // Current window identifier
        String currentWindowId = properties.getCurrentWindowId();
        // Current locale
        Locale locale = LocaleUtils.toLocale(properties.getWindowProperty(currentWindowId, InternalConstants.LOCALE_PROPERTY));
        // Title value
        String title = properties.getWindowProperty(currentWindowId, "osivia.title");
        if (title == null) {
            title = drc.getTitle();
        }

        // Bootstrap panel style indicator
        boolean bootstrapPanelStyle = BooleanUtils.toBoolean(properties.getWindowProperty(currentWindowId, "osivia.bootstrapPanelStyle"));
        // Mobile collapse indicator
        boolean mobileCollapse = BooleanUtils.toBoolean(properties.getWindowProperty(currentWindowId, "osivia.mobileCollapse"));
        // Display title indicator
        boolean displayTitle = "1".equals(properties.getWindowProperty(currentWindowId, "osivia.displayTitle"));
        // Display decorators indicator
        boolean displayDecorators = displayTitle && "1".equals(properties.getWindowProperty(currentWindowId, "osivia.displayDecorators"));

        // Maximized URL
        String maximizedURL = null;
        if (displayDecorators) {
            maximizedURL = this.getMaximizedURL(properties, drc);
        }


        // Title container
        Element titleContainer = DOM4JUtils.generateElement(HTMLConstants.H2, null, StringUtils.EMPTY);
        StringBuilder builder = new StringBuilder();
        builder.append("portlet-titlebar-title h5");
        if (bootstrapPanelStyle) {
            builder.append(" card-title");
        }
        if (maximizedURL != null) {
            builder.append(" maximizable");
        }
        DOM4JUtils.addAttribute(titleContainer, HTMLConstants.CLASS, builder.toString());

        // Title
        if (mobileCollapse) {
            Element titleLink = DOM4JUtils.generateLinkElement("#body_" + currentWindowId, null, null, "no-ajax-link collapsed", title);
            DOM4JUtils.addAttribute(titleLink, HTMLConstants.DATA_TOGGLE, "collapse");
            titleContainer.add(titleLink);
        } else {
            Element titleText = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, title);
            titleContainer.add(titleText);
        }

        // Decorators
        if (displayDecorators) {
            Element decorators = DOM4JUtils.generateElement(HTMLConstants.SPAN, "portlet-mode-container no-ajax-link", StringUtils.EMPTY);
            titleContainer.add(decorators);

            // Maximized
            if (maximizedURL != null) {
                Element maximizedLink = DOM4JUtils.generateLinkElement(maximizedURL, null, null, null, null, "halflings halflings-menu-right");

                String tooltip = this.internationalizationService.getString("MAXIMIZED", locale);

                // Screen-reader only
                Element srOnly = DOM4JUtils.generateElement(HTMLConstants.SPAN, "sr-only", tooltip);
                maximizedLink.add(srOnly);

                DOM4JUtils.addTooltip(maximizedLink, tooltip);
                decorators.add(maximizedLink);
            }
        }


        // Write HTML
        HTMLWriter htmlWriter = new HTMLWriter(markup);
        htmlWriter.setEscapeText(false);
        try {
            htmlWriter.write(titleContainer);
        } catch (IOException e) {
            // Do nothing
        }
    }


    /**
     * Get maximized URL.
     *
     * @param properties page properties
     * @param decorationRendererContext decoration renderer context
     * @return maximized URL, or null if current window can't be maximized
     */
    private String getMaximizedURL(PageProperties properties, DecorationRendererContext decorationRendererContext) {
        // Maximized URL
        String url = null;

        // Maximized CMS URL window property
        url = properties.getWindowProperty(properties.getCurrentWindowId(), Constants.WINDOW_PROP_MAXIMIZED_CMS_URL);

        if (url == null) {
            Collection<?> windowStates = decorationRendererContext.getTriggerableActions(ActionRendererContext.WINDOWSTATES_KEY);
            if (CollectionUtils.isNotEmpty(windowStates)) {
                for (Object windowState : windowStates) {
                    ActionRendererContext action = (ActionRendererContext) windowState;
                    String actionName = action.getName();
                    if ("maximized".equals(actionName)) {
                        if (action.isEnabled()) {
                            url = action.getURL();
                        }
                        break;
                    }
                }
            }
        }

        return url;
    }

}
