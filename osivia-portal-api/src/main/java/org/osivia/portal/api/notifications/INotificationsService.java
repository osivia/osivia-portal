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
     * Add a simple notification.
     * 
     * @param portalControllerContext portal controller context
     * @param message notification message
     * @param type notification type
     * @param errorCode errorCode
     */
    void addSimpleNotification(PortalControllerContext portalControllerContext, String message, NotificationsType type, Long errorCode);

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
