/**
 *
 */
package org.osivia.portal.core.error;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageProperties;

/**
 * Command error interceptor.
 * 
 * @see ControllerInterceptor
 */
public class CommandErrorInterceptor extends ControllerInterceptor {

    
    
    
    /** Internationalization service. */
    private IInternationalizationService internationalizationService;
    

    private static final Log logger = LogFactory.getLog(CommandErrorInterceptor.class);


    /**
     * For sites, try to display a templated error page.
     * 
     * @param cmd command
     * @param resp response
     * @param errorCode error id incremented
     * @return response
     * @throws Exception
     */
    public ControllerResponse displayError(ControllerCommand cmd, ControllerResponse resp, long errorCode) throws Exception {
    
        String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);

        if (portalName != null) {


            PortalObjectId poid = PortalObjectId.parse("/" + portalName, PortalObjectPath.CANONICAL_FORMAT);
            PortalObject portal = cmd.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

            // For sites, display templated error page
            if (InternalConstants.PORTAL_TYPE_SPACE.equals(portal.getDeclaredProperty("osivia.portal.portalType"))) {
                // TODO : internationaliser
                Map<String, String> props = new HashMap<String, String>();

                cmd.getControllerContext().getServerInvocation().getServerContext().getClientRequest().setAttribute("osivia.dynamicerrorpage", "1");


                // get a template named "error"
                StartDynamicPageCommand dynaPageCmd = new StartDynamicPageCommand(portal.getId().toString(PortalObjectPath.SAFEST_FORMAT), "error", null,
                        PortalObjectId.parse("/default/templates/error", PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT), props,
                        new HashMap<String, String>());

                try {
                    UpdatePageResponse errorResp = ((UpdatePageResponse) cmd.getControllerContext().execute(dynaPageCmd));

                    Locale locale = cmd.getControllerContext().getServerInvocation().getRequest().getLocale();

                    String errorLabel;

                    // response is an error, functionnal error cases are managed with a notification.
                    if (resp instanceof UnavailableResourceResponse) {
                        errorLabel = this.internationalizationService.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_NOT_FOUND, locale);
                    } else if (resp instanceof SecurityErrorResponse) {
                        errorLabel = this.internationalizationService.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_FORBIDDEN, locale);
                    } else {
                        errorLabel = this.internationalizationService.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_ERROR_HAS_OCCURED, locale);
                    }


                    NotificationsUtils.getNotificationsService().addSimpleNotification(new PortalControllerContext(cmd.getControllerContext()), errorLabel,
                            NotificationsType.ERROR, errorCode);

                    return errorResp;


                } catch (Exception e2) {

                    // response throws an exception, technical error cases are managed above in invoke method
                    throw e2;
                }
            }
        }

        return resp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
        ControllerResponse resp = null;

        try {
            resp = (ControllerResponse) cmd.invokeNext();

            // * functionnal errors, see displayError
            if (resp instanceof ErrorResponse || resp instanceof UnavailableResourceResponse) {
                return this.displayError(cmd, resp, -1);
            }
            

        } catch (UserNotificationsException e) {
            // * known technical errors, notification is added in the current page

            // Business exception are displayed in current page
            PortalObjectId portalObjectId = (PortalObjectId) cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId");
            if (portalObjectId != null) {
                Notifications notifications = e.getNotifications();
                PortalControllerContext portalControllerContext = new PortalControllerContext(cmd.getControllerContext());
                NotificationsUtils.getNotificationsService().addNotifications(portalControllerContext, notifications);
                return new UpdatePageResponse(portalObjectId);
            } else {
                this.injectIntoValve(cmd, e);
            }
        } catch (Exception e) {
            // * unknown technical errors, try to display it in the 'error' template, see displayError

            try {

                String userId = cmd.getControllerContext().getServerInvocation().getServerContext().getClientRequest().getRemoteUser();
                ErrorDescriptor errDescriptor = new ErrorDescriptor(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, e.getMessage(), userId, null);

                // print stack in server.log and portal_user_error.log
                long errId = GlobalErrorHandler.getInstance().logError(errDescriptor);

                if (cmd.getControllerContext().getServerInvocation().getServerContext().getClientRequest().getAttribute("osivia.dynamicerrorpage") == null) {
                    ControllerResponse errResp = this.displayError(cmd, resp, errId);
                    if (errResp != null) {
                        return errResp;
                    }
                }

                // * unknown technical errors and template 'error' can not display it, return the default error page
            } catch (Exception errorExc) {

            }

            return this.injectIntoValve(cmd, e);
        }


        return resp;
    }


    /**
     * Inject exception into valve.
     * 
     * @param cmd controller command
     * @param e exception to inject
     * @return controller response
     */
    private ControllerResponse injectIntoValve(ControllerCommand cmd, Exception e) {
        cmd.getControllerContext().getServerInvocation().setAttribute(Scope.REQUEST_SCOPE, "osivia.error_exception", e);
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
