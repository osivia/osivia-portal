package org.osivia.portal.core.error;

import org.jboss.portal.core.controller.ControllerException;
import org.osivia.portal.core.page.UserNotification;


public class UserNotificationException extends ControllerException {
    
    private static final long serialVersionUID = 895884016014573655L;
    private UserNotification userNotification;
    
    public UserNotificationException(UserNotification userNotification) {
        super();
        this.userNotification = userNotification;
    }


    public UserNotification getUserNotification() {
        return userNotification;
    }

    
    public void setUserNotification(UserNotification userNotification) {
        this.userNotification = userNotification;
    }
    


}
