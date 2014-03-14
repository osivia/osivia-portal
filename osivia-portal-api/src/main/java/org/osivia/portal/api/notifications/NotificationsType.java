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

/**
 * Notifications types enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum NotificationsType {

    /** Information notifications type. */
    INFO("info", 1),
    /** Advice notifications type. */
    ADVICE("advice", 1),
    /** Success notifications type. */
    SUCCESS("success", 2),
    /** Warning notifications type. */
    WARNING("warning", 3),
    /** Error notifications type. */
    ERROR("error", 4);


    /** HTML class. */
    private final String htmlClass;
    /** Priority. */
    private final int priority;


    /**
     * Constructor.
     *
     * @param htmlClass HTML class
     * @param priority priority
     */
    private NotificationsType(String htmlClass, int priority) {
        this.htmlClass = htmlClass;
        this.priority = priority;
    }


    /**
     * Getter for htmlClass.
     *
     * @return the htmlClass
     */
    public String getHtmlClass() {
        return this.htmlClass;
    }

    /**
     * Getter for priority.
     *
     * @return the priority
     */
    public int getPriority() {
        return this.priority;
    }

}
