package org.osivia.portal.api.portlet;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Portlet status service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IPortletStatusService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=PortletStatusService";

    /** Portlet status container attribute name. */
    String STATUS_CONTAINER_ATTRIBUTE = "osivia.portletStatus.container";


    /**
     * Get portlet status.
     *
     * @param portalControllerContext portal controller context
     * @param portletName portlet name
     * @param type portlet status type
     * @return portlet status
     */
    <T extends PortletStatus> T getStatus(PortalControllerContext portalControllerContext, String portletName, Class<T> type);


    /**
     * Set portlet status.
     *
     * @param portalControllerContext portal controller context
     * @param portletName portlet name
     * @param status portlet status
     */
    void setStatus(PortalControllerContext portalControllerContext, String portletName, PortletStatus status);


    /**
     * Reset task dependent portlet status.
     * 
     * @param portalControllerContext portal controller context
     */
    void resetTaskDependentStatus(PortalControllerContext portalControllerContext);

}
