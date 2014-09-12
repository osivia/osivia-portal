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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Notifications bean.
 *
 * @author Cédric Krommenhoek
 */
public final class Notifications {

    /** Notifications type. */
    private final NotificationsType type;
    /** Messages. */
    private final List<String> messages;
    /** Creation timestamp. */
    private final long creationTime;
    /** Expiration timestamp. */
    private final long expirationTime;
    /** Error code (if exceptions occured). */
    private Long errorCode;

    /**
     * Constructor.
     *
     * @param type notifications type
     */
    public Notifications(NotificationsType type) {
        super();
        this.type = type;
        this.messages = new ArrayList<String>();
        this.creationTime = new Date().getTime();
        this.expirationTime = 0;
    }

    /**
     * Contructor with duration.
     *
     * @param type notifications type
     * @param duration duration, in millisecond
     */
    public Notifications(NotificationsType type, int duration) {
        super();
        this.type = type;
        this.messages = new ArrayList<String>();
        this.creationTime = new Date().getTime();
        this.expirationTime = this.creationTime + duration;
    }


    /**
     * Add single message to messages list.
     *
     * @param message message
     */
    public void addMessage(String message) {
        this.messages.add(message);
    }


    /**
     * Add multiple messages to messages list.
     * 
     * @param messages messages
     */
    public void addMessages(List<String> messages) {
        this.messages.addAll(messages);
    }


    /**
     * Getter for type.
     *
     * @return the type
     */
    public NotificationsType getType() {
        return this.type;
    }

    /**
     * Getter for messages.
     *
     * @return the messages
     */
    public List<String> getMessages() {
        return this.messages;
    }

    /**
     * Getter for creationTime.
     *
     * @return the creationTime
     */
    public long getCreationTime() {
        return this.creationTime;
    }

    /**
     * Getter for expirationTime.
     *
     * @return the expirationTime
     */
    public long getExpirationTime() {
        return this.expirationTime;
    }


    /**
     * Getter for errorCode.
     *
     * @return the errorCode
     */
    public Long getErrorCode() {
        return this.errorCode;
    }


    /**
     * Setter for errorCode.
     *
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }


}
