/**
 *
 */
package org.osivia.portal.core.error;

import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.core.notifications.NotificationsUtils;

/**
 * Command error interceptor.
 *
 * @see ControllerInterceptor
 */
public class CommandErrorInterceptor extends ControllerInterceptor {

    /**
     * {@inheritDoc}
     */
    public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
        ControllerResponse resp = null;

        try {
            resp = (ControllerResponse) cmd.invokeNext();
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
            // Exceptions are handled in the valve
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

}
