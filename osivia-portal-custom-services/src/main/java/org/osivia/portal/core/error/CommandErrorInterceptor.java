/**
 *
 */
package org.osivia.portal.core.error;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
    
    /**
     * {@inheritDoc}
     */
    
    
    
    public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
        ControllerResponse resp = null;

        try {
            resp = (ControllerResponse) cmd.invokeNext();
            
            if( resp instanceof ErrorResponse || resp instanceof UnavailableResourceResponse){


                String portalName = PageProperties.getProperties().getPagePropertiesMap().get("portalName");

                if (portalName != null) {


                    PortalObjectId poid = PortalObjectId.parse("/" + portalName, PortalObjectPath.CANONICAL_FORMAT);
                    PortalObject portal = (PortalObject) cmd.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

                    if (InternalConstants.PORTAL_TYPE_SPACE.equals(portal.getDeclaredProperty("osivia.portal.portalType"))) {

                        
                        /* Display templated error page
                         */

                        // TODO : internationaliser
                        Map<String, String> props = new HashMap<String, String>();



                        StartDynamicPageCommand dynaPageCmd = new StartDynamicPageCommand(portal.getId().toString(PortalObjectPath.SAFEST_FORMAT), "error",
                                null, PortalObjectId.parse("/default/templates/error", PortalObjectPath.CANONICAL_FORMAT).toString(
                                        PortalObjectPath.SAFEST_FORMAT), props, new HashMap<String, String>());

                        try {
                            UpdatePageResponse errorResp =  ((UpdatePageResponse) cmd.getControllerContext().execute(dynaPageCmd));
                            
                            Locale locale = cmd.getControllerContext().getServerInvocation().getRequest().getLocale();

                            String errorLabel ;
                            
                            if(resp instanceof UnavailableResourceResponse)
                                errorLabel = internationalizationService.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_NOT_FOUND, locale);
                            else if(resp instanceof SecurityErrorResponse)
                                errorLabel = internationalizationService.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_FORBIDDEN, locale);
                            else 
                                errorLabel = internationalizationService.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_ERROR_HAS_OCCURED, locale);
                           
                            NotificationsUtils.getNotificationsService().addSimpleNotification(new PortalControllerContext(cmd.getControllerContext()), errorLabel, NotificationsType.ERROR);
                            
                            return errorResp;

                        } catch (Exception e2) {
                            // NO error page
                            //TODO : logger
                            throw e2;
                        }
                    }
                }

                
                
            }
            
        } catch (UserNotificationsException e) {
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
