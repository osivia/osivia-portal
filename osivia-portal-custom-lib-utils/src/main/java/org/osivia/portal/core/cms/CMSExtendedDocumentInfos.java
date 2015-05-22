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

import java.util.Calendar;




/**
 * Object containing extended informations about a document (compare to CMSPublicationInfos).
 * 
 * @author david chevrier.
 *
 */
public class CMSExtendedDocumentInfos {
    
    /** Indicates if a task of name taskName is pending on document */
    private Boolean isOnlineTaskPending = false;
    
    /** Indicates if current user can manage pending task on document. */
    private Boolean canUserValidateOnlineTask = false;
    
    /** Indicates if current user is the task's initiator. */
    private Boolean isUserOnlineTaskInitiator = false;
    
    /** Indicates if a validation workflow is running on a given document. */
    private Boolean isValidationWorkflowRunning = false;
    
    /**
     * A document has a state depending of the user who is browsing it
     */
    public enum SubscriptionStatus {
        /** Default state : can subscribe */
        can_subscribe,
        /** Can unsubscribe if a subscription is already set */
        can_unsubscribe,
        /** If a subscription is defined by other document upper in the hierarchy, or if a group has subscribed before to them */
        has_inherited_subscriptions,
        /** Special cases : Domains, WorkspacesRoot, ... are not allowing subscription */
        no_subscriptions;
    };

    private SubscriptionStatus subscriptionStatus;
    
    public enum LockStatus {
        /** Default state : can lock */
        can_lock,
        /** Can uunlock */
        can_unlock,
        /** a lock is set and is not removable by this user*/
        locked,
        /** No lock avaliable (proxies, versions, ...) */
        no_lock;    	
    }
    
    /** Lock status */
    private LockStatus lockStatus;
    
    /** Owner of the lock */
    private String lockOwner;
    
    /** The time when document has been locked */
    private Calendar lockDate;
    
    /** Drive, folder can be synchronized */
    private boolean canSynchronize = false;
    /** Drive, folder can be unsynchronized */
    private boolean canUnsynchronize = false;

    /** Drive, Root of the synchro */
    private String synchronizationRootPath = null;
    
    /** Drive, DriveEdit direct url */
    private String driveEditURL = null;
    
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
    
    /**
     * @return isValidationWorkflowRunning value
     */
    public Boolean getIsValidationWorkflowRunning() {
        return isValidationWorkflowRunning;
    }

    /**
     * 
     * @param isValidationWorkflowRunning isValidationWorkflowRunning to set
     */
    public void setIsValidationWorkflowRunning(Boolean isValidationWorkflowRunning) {
        this.isValidationWorkflowRunning = isValidationWorkflowRunning;
    }

    /**
     * @return the subscriptionStatus
     */
    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    /**
     * @param subscriptionStatus
     *            the subscriptionStatus to set
     */
    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

	/**
	 * @return the lockStatus
	 */
	public LockStatus getLockStatus() {
		return lockStatus;
	}

	/**
	 * @param lockStatus the lockStatus to set
	 */
	public void setLockStatus(LockStatus lockStatus) {
		this.lockStatus = lockStatus;
	}

	/**
	 * @return the lockOwner
	 */
	public String getLockOwner() {
		return lockOwner;
	}

	/**
	 * @param lockOwner the lockOwner to set
	 */
	public void setLockOwner(String lockOwner) {
		this.lockOwner = lockOwner;
	}

	/**
	 * @return the lockDate
	 */
	public Calendar getLockDate() {
		return lockDate;
	}

	/**
	 * @param lockDate the lockDate to set
	 */
	public void setLockDate(Calendar lockDate) {
		this.lockDate = lockDate;
	}
    


    /**
     * @return the canSynchronize
     */
    public boolean isCanSynchronize() {
        return canSynchronize;
    }


    /**
     * @param canSynchronize the canSynchronize to set
     */
    public void setCanSynchronize(boolean canSynchronize) {
        this.canSynchronize = canSynchronize;
    }


    /**
     * @return the canUnsynchronize
     */
    public boolean isCanUnsynchronize() {
        return canUnsynchronize;
    }


    /**
     * @param canUnsynchronize the canUnsynchronize to set
     */
    public void setCanUnsynchronize(boolean canUnsynchronize) {
        this.canUnsynchronize = canUnsynchronize;
    }


    
    /**
     * @return the synchronizationRootPath
     */
    public String getSynchronizationRootPath() {
        return synchronizationRootPath;
    }


    /**
     * @param synchronizationRootPath the synchronizationRootPath to set
     */
    public void setSynchronizationRootPath(String synchronizationRootPath) {
        this.synchronizationRootPath = synchronizationRootPath;
    }


    /**
     * @return the driveEditURL
     */
    public String getDriveEditURL() {
        return driveEditURL;
    }


    /**
     * @param driveEditURL the driveEditURL to set
     */
    public void setDriveEditURL(String driveEditURL) {
        this.driveEditURL = driveEditURL;
    }

    
}
