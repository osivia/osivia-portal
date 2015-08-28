package org.osivia.portal.core.customization;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.customization.IProjectCustomizationConfiguration;

/**
 * Project customizer interceptor.
 *
 * @author Cédric Krommenhoek
 * @see ControllerInterceptor
 */
public class ProjectCustomizerInterceptor extends ControllerInterceptor {

    /** Customization service. */
    private ICustomizationService customizationService;


    /**
     * Constructor.
     */
    public ProjectCustomizerInterceptor() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse invoke(ControllerCommand controllerCommand) throws Exception, InvocationException {
        // Response
        ControllerResponse response = null;

        if (controllerCommand instanceof RenderPageCommand) {
            // Render page command
            RenderPageCommand renderPageCommand = (RenderPageCommand) controllerCommand;
            // Controller context
            ControllerContext controllerContext = renderPageCommand.getControllerContext();
            // Portal controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

            // Project customization configuration
            ProjectCustomizationConfiguration configuration = new ProjectCustomizationConfiguration(portalControllerContext, renderPageCommand);
            configuration.setBeforeInvocation(true);

            // Customizer attributes
            Map<String, Object> customizerAttributes = new HashMap<String, Object>();
            customizerAttributes.put(IProjectCustomizationConfiguration.CUSTOMIZER_ATTRIBUTE_CONFIGURATION, configuration);
            // Customizer context
            CustomizationContext context = new CustomizationContext(customizerAttributes, portalControllerContext);


            // Customization call #1
            this.customizationService.customize(IProjectCustomizationConfiguration.CUSTOMIZER_ID, context);


            // Redirection
            String redirectionURL = configuration.getRedirectionURL();
            if (StringUtils.isNotEmpty(redirectionURL)) {
                response = new RedirectionResponse(redirectionURL);
            } else {
                // Invoke next
                response = (ControllerResponse) controllerCommand.invokeNext();


                // Update project customization configuration
                configuration.setBeforeInvocation(false);

                // Customization call #2
                this.customizationService.customize(IProjectCustomizationConfiguration.CUSTOMIZER_ID, context);
            }
        } else {
            // Invoke next
            response = (ControllerResponse) controllerCommand.invokeNext();
        }

        return response;
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