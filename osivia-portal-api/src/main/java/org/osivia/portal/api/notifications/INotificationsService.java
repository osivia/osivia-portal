package org.osivia.portal.api.notifications;

import java.util.List;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Notifications service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface INotificationsService {

    /** MBean name. */
    static final String MBEAN_NAME = "osivia:service=NotificationsService";


    /**
     * Add a simple notification.
     *
     * @param portalControllerContext portal controller context
     * @param message notification message
     * @param type notification type
     */
    void addSimpleNotification(PortalControllerContext portalControllerContext, String message, NotificationsType type);


    /**
     * Add notifications.
     *
     * @param portalControllerContext portal controller context
     * @param notifications notifications
     */
    void addNotifications(PortalControllerContext portalControllerContext, Notifications notifications);


    /**
     * Get notifications list.
     *
     * @param portalControllerContext portal controller context
     * @return notifications list
     */
    List<Notifications> getNotificationsList(PortalControllerContext portalControllerContext);


    /**
     * Set notifications list.
     *
     * @param portalControllerContext portal controller context
     * @param notificationsList notifications list
     */
    void setNotificationsList(PortalControllerContext portalControllerContext, List<Notifications> notificationsList);


    /**
     * Read and remove notifications.
     *
     * @param portalControllerContext portal controller context
     * @return notifications list
     */
    List<Notifications> readNotificationsList(PortalControllerContext portalControllerContext);

}
