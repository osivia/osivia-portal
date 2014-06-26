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

import java.util.HashMap;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.theming.AbstractRegionBean;
import org.osivia.portal.api.theming.IRegionsThemingService;
import org.osivia.portal.api.theming.IRenderedRegions;
import org.osivia.portal.api.theming.PortletsRegionBean;
import org.osivia.portal.api.theming.RenderedRegionBean;
import org.osivia.portal.core.customization.ICustomizationService;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Regions customizer interception.
 *
 * @author Cédric Krommenhoek
 * @see ControllerInterceptor
 */
public class RegionsCustomizerInterceptor extends ControllerInterceptor {

    /** Regions theming service. */
    private IRegionsThemingService regionsThemingService;
    /** Customization service. */
    private ICustomizationService customizationService;

    /**
     * Default constructor.
     */
    public RegionsCustomizerInterceptor() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse invoke(ControllerCommand command) throws Exception {
        ControllerResponse response = (ControllerResponse) command.invokeNext();

        if ((command instanceof RenderPageCommand) && (response instanceof PageRendition)) {
            // Render page command
            RenderPageCommand renderPageCommand = (RenderPageCommand) command;

            if (!PortalObjectUtils.isJBossPortalAdministration(renderPageCommand.getPortal()) && (renderPageCommand.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
            "osivia.popupModeWindowID") == null) ) {
                // Page rendition
                PageRendition pageRendition = (PageRendition) response;

                String layoutContextPath = this.regionsThemingService.getLayoutContextPath(renderPageCommand);
                String themeContextPath = this.regionsThemingService.getThemeContextPath(renderPageCommand);
                Boolean administrator = PageCustomizerInterceptor.isAdministrator(renderPageCommand.getControllerContext());

                // Rendered regions
                RenderedRegions renderedRegions = new RenderedRegions(renderPageCommand.getPage());

                Map<String, Object> customizerAttributes = new HashMap<String, Object>();
                customizerAttributes.put(IRenderedRegions.CUSTOMIZER_ATTRIBUTE_LAYOUT_CONTEXT_PATH, layoutContextPath);
                customizerAttributes.put(IRenderedRegions.CUSTOMIZER_ATTRIBUTE_THEME_CONTEXT_PATH, themeContextPath);
                customizerAttributes.put(IRenderedRegions.CUSTOMIZER_ATTRIBUTE_ADMINISTATOR, administrator);
                customizerAttributes.put(IRenderedRegions.CUSTOMIZER_ATTRIBUTE_RENDERED_REGIONS, renderedRegions);
                CustomizationContext context = new CustomizationContext(customizerAttributes);
                this.customizationService.customize(IRenderedRegions.CUSTOMIZER_ID, context);

                // Add regions
                for (AbstractRegionBean region : renderedRegions.getRenderedRegions()) {
                    if (region instanceof RenderedRegionBean) {
                        // Rendered region
                        RenderedRegionBean renderedRegion = (RenderedRegionBean) region;
                        this.regionsThemingService.addRegion(renderPageCommand, pageRendition, renderedRegion);
                    } else if (region instanceof PortletsRegionBean) {
                        // Portlets region
                        PortletsRegionBean portletsRegion = (PortletsRegionBean) region;
                        this.regionsThemingService.decorateRegion(renderPageCommand, portletsRegion);
                    }
                }
            }
        }

        return response;
    }


    /**
     * Setter for regionsThemingService.
     *
     * @param regionsThemingService the regionsThemingService to set
     */
    public void setRegionsThemingService(IRegionsThemingService regionsThemingService) {
        this.regionsThemingService = regionsThemingService;
    }

    /**
     * Setter for customizationService.
     *
     * @param customizationService the customizationService to set
     */
    public void setCustomizationService(ICustomizationService customizationService) {
        this.customizationService = customizationService;
    }

}
