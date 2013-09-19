package org.osivia.portal.core.theming;

import java.util.HashMap;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.theming.IRegionsThemingService;
import org.osivia.portal.api.theming.IRenderedRegions;
import org.osivia.portal.api.theming.RenderedRegionBean;
import org.osivia.portal.core.customization.ICustomizationService;
import org.osivia.portal.core.page.PageCustomizerInterceptor;

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
            RenderPageCommand renderPageCommand = (RenderPageCommand) command;
            PageRendition pageRendition = (PageRendition) response;
            String contextPath = this.regionsThemingService.getContextPath(renderPageCommand);
            Boolean administrator = PageCustomizerInterceptor.isAdministrator(renderPageCommand.getControllerContext());

            // Rendered regions
            RenderedRegions renderedRegions = new RenderedRegions(renderPageCommand.getPage());

            Map<String, Object> customizerAttributes = new HashMap<String, Object>();
            customizerAttributes.put(IRenderedRegions.CUSTOMIZER_ATTRIBUTE_CONTEXT_PATH, contextPath);
            customizerAttributes.put(IRenderedRegions.CUSTOMIZER_ATTRIBUTE_ADMINISTATOR, administrator);
            customizerAttributes.put(IRenderedRegions.CUSTOMIZER_ATTRIBUTE_RENDERED_REGIONS, renderedRegions);
            CustomizationContext context = new CustomizationContext(customizerAttributes);
            this.customizationService.customize(IRenderedRegions.CUSTOMIZER_ID, context);

            // Add regions
            for (RenderedRegionBean renderedRegion : renderedRegions.getRenderedRegions()) {
                this.regionsThemingService.addRegion(renderPageCommand, pageRendition, renderedRegion);
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
