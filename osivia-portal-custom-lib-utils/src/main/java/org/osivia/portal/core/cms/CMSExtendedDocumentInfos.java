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
 */
package org.osivia.portal.core.cms;

import java.util.HashMap;
import java.util.Map;


/**
 * Object containing extended informations about a document (compare to CMSPublicationInfos).
 * 
 * @author david chevrier.
 *
 */
public class CMSExtendedDocumentInfos {
    
    /** Indicates if a task of name taskName is pending on document */
    private Boolean isOnlineTaskPending;
    
    /** Indicates if current user can manage pending task on document. */
    private Boolean canUserValidateOnlineTask;
    
    /** Indicates if current user is the task's initiator. */
    private Boolean isUserOnlineTaskInitiator;
    
    /**
     * @return the isOnlineTaskPending
     */
    public Boolean isOnlineTaskPending() {
        return isOnlineTaskPending;
    }

    /**
     * @param isOnlineTaskPending the isOnlineTaskPending to set
     */
    public void setIsOnlineTaskPending(Boolean isOnlineTaskPending) {
        this.isOnlineTaskPending = isOnlineTaskPending;
    }

    /**
     * @return the canUserValidateOnlineTask
     */
    public Boolean canUserValidateOnlineTask() {
        return canUserValidateOnlineTask;
    }

    /**
     * @param canUserValidateOnlineTask the canUserValidateOnlineTask to set
     */
    public void setCanUserValidateOnlineTask(Boolean canUserValidateOnlineTask) {
        this.canUserValidateOnlineTask = canUserValidateOnlineTask;
    }

    /**
     * @return the isUserOnlineTaskInitiator
     */
    public Boolean isUserOnlineTaskInitiator() {
        return isUserOnlineTaskInitiator;
    }

    /**
     * @param isUserOnlineTaskInitiator the isUserOnlineTaskInitiator to set
     */
    public void setIsUserOnlineTaskInitiator(Boolean isUserOnlineTaskInitiator) {
        this.isUserOnlineTaskInitiator = isUserOnlineTaskInitiator;
    }
    
}
