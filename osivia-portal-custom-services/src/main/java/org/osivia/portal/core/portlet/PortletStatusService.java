package org.osivia.portal.core.portlet;

import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.portlet.IPortletStatusService;
import org.osivia.portal.api.portlet.PortletStatus;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Portlet status service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IPortletStatusService
 */
public class PortletStatusService implements IPortletStatusService {

    /**
     * Constructor.
     */
    public PortletStatusService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public <T extends PortletStatus> T getStatus(PortalControllerContext portalControllerContext, String portletName, Class<T> type) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // Page identifier
        PortalObjectId pageId = PortalObjectUtils.getPageId(controllerContext);

        // Portlet status
        T status = null;

        // Portlet status container
        PortletStatusContainer statusContainer = (PortletStatusContainer) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, STATUS_CONTAINER_ATTRIBUTE);
        if (statusContainer != null) {
            try {
                status = type.cast(statusContainer.getPortletStatus(pageId, portletName));
            } catch (ClassCastException e) {
                // Class loader has changed, old status object must be destroyed
                statusContainer.setPortletStatus(pageId, portletName, null);
            }
        }

        return status;
    }


    /**
     * {@inheritDoc}
     */
    public void setStatus(PortalControllerContext portalControllerContext, String portletName, PortletStatus status) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // Page identifier
        PortalObjectId pageId = PortalObjectUtils.getPageId(controllerContext);

        // Portlet status container
        PortletStatusContainer statusContainer = (PortletStatusContainer) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, STATUS_CONTAINER_ATTRIBUTE);
        if (statusContainer == null) {
            statusContainer = new PortletStatusContainer();
            controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, STATUS_CONTAINER_ATTRIBUTE, statusContainer);
        }

        statusContainer.setPortletStatus(pageId, portletName, status);
    }

}
