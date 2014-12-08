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
package org.osivia.portal.core.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.context.ControllerContextAdapter;

/**
 * Notifications service implementation.
 *
 * @author Cédric Krommenhoek
 * @see INotificationsService
 */
public class NotificationsService implements INotificationsService {

    /**
     * Default constructor.
     */
    public NotificationsService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void addSimpleNotification(PortalControllerContext portalControllerContext, String message, NotificationsType type) {
        Notifications notifications = new Notifications(type);
        notifications.addMessage(message);
        this.addNotifications(portalControllerContext, notifications);
    }

    /**
     * {@inheritDoc}
     */
    public void addSimpleNotification(PortalControllerContext portalControllerContext, String message, NotificationsType type, Long errorCode) {
        Notifications notifications = new Notifications(type);
        notifications.addMessage(message);
        notifications.setErrorCode(errorCode);
        this.addNotifications(portalControllerContext, notifications);
    }

    /**
     * {@inheritDoc}
     */
    public void addNotifications(PortalControllerContext portalControllerContext, Notifications notifications) {
        if (notifications != null) {
            List<Notifications> notificationsList = this.getNotificationsList(portalControllerContext);
            notificationsList.add(notifications);
            this.setNotificationsList(portalControllerContext, notificationsList);
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final List<Notifications> getNotificationsList(PortalControllerContext portalControllerContext) {
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        List<Notifications> notificationsList = (List<Notifications>) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE,
                InternalConstants.ATTR_NOTIFICATIONS);
        if (notificationsList == null) {
            notificationsList = new ArrayList<Notifications>();
        }

        // Remove expired notifications
        long currentTime = new Date().getTime();
        List<Notifications> refinedNotificationsList = new ArrayList<Notifications>(notificationsList.size());
        for (Notifications notifications : notificationsList) {
            if ((notifications.getExpirationTime() == 0) || (notifications.getExpirationTime() > currentTime)) {
                refinedNotificationsList.add(notifications);
            }
        }

        return refinedNotificationsList;
    }


    /**
     * {@inheritDoc}
     */
    public final void setNotificationsList(PortalControllerContext portalControllerContext, List<Notifications> notificationsList) {
        ControllerContext controllerContext = (ControllerContext) portalControllerContext.getControllerCtx();
        controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, InternalConstants.ATTR_NOTIFICATIONS, notificationsList);
    }


    /**
     * {@inheritDoc}
     */
    public List<Notifications> readNotificationsList(PortalControllerContext portalControllerContext) {
        long currentTime = new Date().getTime();

        // Get and sort notifications list
        List<Notifications> notificationsList = this.getNotificationsList(portalControllerContext);
        Collections.sort(notificationsList, NotificationsComparator.getInstance());

        // Save persistent notifications
        List<Notifications> newNotificationsList = new ArrayList<Notifications>();
        for (Notifications notifications : notificationsList) {
            if (notifications.getExpirationTime() > currentTime) {
                newNotificationsList.add(notifications);
            }
        }
        this.setNotificationsList(portalControllerContext, newNotificationsList);

        return notificationsList;
    }




}
