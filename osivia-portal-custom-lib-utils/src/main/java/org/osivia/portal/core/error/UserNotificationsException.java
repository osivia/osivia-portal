/**
 *
 */
package org.osivia.portal.core.error;

import java.util.List;

import org.jboss.portal.core.controller.ControllerException;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.api.notifications.NotificationsType;


/**
 * User notifications exception.
 *
 * @author Cédric Krommenhoek
 * @see ControllerException
 */
public final class UserNotificationsException extends ControllerException {

    /** Default serial version. */
    private static final long serialVersionUID = 1L;

    /** User notifications. */
    private final Notifications notifications;


    /**
     * Constructor using fields.
     *
     * @param notifications user notifications
     */
    public UserNotificationsException(Notifications notifications) {
        super();
        this.notifications = notifications;
    }


    /**
     * Constructor.
     *
     * @param message single message
     */
    public UserNotificationsException(String message) {
        super();
        this.notifications = new Notifications(NotificationsType.ERROR);
        this.notifications.addMessage(message);
    }


    /**
     * Constructor.
     * 
     * @param messages multiple messages
     */
    public UserNotificationsException(List<String> messages) {
        super();
        this.notifications = new Notifications(NotificationsType.ERROR);
        this.notifications.addMessages(messages);
    }


    /**
     * Getter for notifications.
     *
     * @return the notifications
     */
    public Notifications getNotifications() {
        return this.notifications;
    }

}
