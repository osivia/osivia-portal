/**
 *
 */
package org.osivia.portal.core.error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.web.WebCommand;

/**
 * Command error interceptor.
 *
 * @see ControllerInterceptor
 */
public class CommandErrorInterceptor extends ControllerInterceptor {

    /** Dynamic error indicator request attribute name. */
    private static final String DYNAMIC_ERROR_ATTRIBUTE = "osivia.dynamicError";


    /** Internationalization service. */
    private IInternationalizationService internationalizationService;


    /**
     * Default constructor.
     */
    public CommandErrorInterceptor() {
        super();
    }


    /**
     * For sites, try to display a templated error page.
     *
     * @param command command
     * @param response response
     * @param errorCode error id incremented
     * @return response
     * @throws Exception
     */
    public ControllerResponse displayError(ControllerCommand command, ControllerResponse response, long errorCode) throws Exception {
        // Controller context
        ControllerContext controllerContext = command.getControllerContext();
        // Portal
        Portal portal = PortalObjectUtils.getPortal(controllerContext);

        // Error response
        ControllerResponse errorResponse;
        if (PortalObjectUtils.isSpaceSite(portal)) {
            // Client request
            HttpServletRequest clientRequest = controllerContext.getServerInvocation().getServerContext().getClientRequest();
            // Bundle
            IBundleFactory bundleFactory = this.internationalizationService.getBundleFactory(this.getClass().getClassLoader());
            Bundle bundle = bundleFactory.getBundle(clientRequest.getLocale());

            // Indicator used to prevent error loop
            clientRequest.setAttribute(DYNAMIC_ERROR_ATTRIBUTE, true);

            // Command
            WebCommand webCommand = new WebCommand("error");
            errorResponse = controllerContext.execute(webCommand);

            // Error label
            String errorLabel;
            if (response instanceof UnavailableResourceResponse) {
                errorLabel = bundle.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_NOT_FOUND);
            } else if (response instanceof SecurityErrorResponse) {
                errorLabel = bundle.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_FORBIDDEN);
            } else {
                errorLabel = bundle.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_ERROR_HAS_OCCURED);
            }

            // Add notification
            NotificationsUtils.getNotificationsService().addSimpleNotification(new PortalControllerContext(command.getControllerContext()), errorLabel,
                    NotificationsType.ERROR, errorCode);
        } else {
            errorResponse = response;
        }

        return errorResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse invoke(ControllerCommand command) throws Exception {
        // Response
        ControllerResponse response;
        // Controller context
        ControllerContext controllerContext = command.getControllerContext();
        // Client request
        HttpServletRequest clientRequest = controllerContext.getServerInvocation().getServerContext().getClientRequest();

        try {
            response = (ControllerResponse) command.invokeNext();

            // Functionnal errors, see displayError
            if (response instanceof ErrorResponse || response instanceof UnavailableResourceResponse) {
                if (BooleanUtils.isNotTrue((Boolean) clientRequest.getAttribute(DYNAMIC_ERROR_ATTRIBUTE))) {
                    response = this.displayError(command, response, -1);
                } else {
                    throw new PortalException("Missing error page");
                }
            }
        } catch (UserNotificationsException e) {
            // Known technical errors, notification is added in the current page

            // Business exception are displayed in current page
            PortalObjectId portalObjectId = (PortalObjectId) command.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                    "osivia.currentPageId");
            if (portalObjectId != null) {
                Notifications notifications = e.getNotifications();
                PortalControllerContext portalControllerContext = new PortalControllerContext(command.getControllerContext());
                NotificationsUtils.getNotificationsService().addNotifications(portalControllerContext, notifications);
                response = new UpdatePageResponse(portalObjectId);
            } else {
                response = this.injectIntoValve(command, e);
            }
        } catch (Exception e) {
            // Unknown technical errors, try to display it in the 'error' template, see displayError

            try {
                // User identifier
                String userId = clientRequest.getRemoteUser();
                // Error descriptor
                ErrorDescriptor errorDescriptor = new ErrorDescriptor(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, e.getMessage(), userId, null);

                // Print stack in server.log and portal_user_error.log
                long errId = GlobalErrorHandler.getInstance().logError(errorDescriptor);
                if (BooleanUtils.isNotTrue((Boolean) clientRequest.getAttribute(DYNAMIC_ERROR_ATTRIBUTE))) {
                    // Error response
                    response = this.displayError(command, null, errId);
                } else {
                    response = this.injectIntoValve(command, e);
                }
            } catch (Exception errorExc) {
                // Unknown technical errors and template 'error' can not display it, return the default error page
                response = this.injectIntoValve(command, e);
            }
        }

        return response;
    }


    /**
     * Inject exception into valve.
     *
     * @param command controller command
     * @param exception exception to inject
     * @return controller response
     */
    private ControllerResponse injectIntoValve(ControllerCommand command, Exception exception) {
        command.getControllerContext().getServerInvocation().setAttribute(Scope.REQUEST_SCOPE, "osivia.error_exception", exception);
        return new ErrorResponse("Portal exception", false);
    }


    /**
     * Getter for internationalizationService.
     *
     * @return the internationalizationService
     */
    public IInternationalizationService getInternationalizationService() {
        return this.internationalizationService;
    }

    /**
     * Setter for internationalizationService.
     *
     * @param internationalizationService the internationalizationService to set
     */
    public void setInternationalizationService(IInternationalizationService internationalizationService) {
        this.internationalizationService = internationalizationService;
    }

}
