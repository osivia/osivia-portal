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
    INFO("info", "glyphicons glyphicons-basic-circle-info", 1),
    /** Success notifications type. */
    SUCCESS("success", "glyphicons glyphicons-basic-circle-check", 2),
    /** Warning notifications type. */
    WARNING("warning", "glyphicons glyphicons-basic-circle-alert", 3),
    /** Error notifications type. */
    ERROR("danger", "glyphicons glyphicons-basic-circle-remove", 4);


    /** HTML class. */
    private final String htmlClass;
    /** Icon. */
    private final String icon;
    /** Priority. */
    private final int priority;


    /**
     * Constructor.
     *
     * @param htmlClass HTML class
     * @param icon icon
     * @param priority priority
     */
    NotificationsType(String htmlClass, String icon, int priority) {
        this.htmlClass = htmlClass;
        this.icon = icon;
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
     * Getter for icon.
     * 
     * @return the icon
     */
    public String getIcon() {
        return icon;
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
