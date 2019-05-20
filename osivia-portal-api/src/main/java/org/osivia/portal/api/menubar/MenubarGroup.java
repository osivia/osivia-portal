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
package org.osivia.portal.api.menubar;


/**
 * Menubar group enumeration.
 *
 * @author Cédric Krommenhoek
 * @see MenubarContainer
 */
public enum MenubarGroup implements MenubarContainer {

    /** Back menubar group. */
    BACK(1, "d-md-none mr-auto"),
    /** Add menubar group. */
    ADD(2, "contextual"),
    /** Specific menubar group. */
    SPECIFIC(3, null),
    /** CMS menubar group. */
    CMS(4, null),
    /** Generic menubar group. */
    GENERIC(5, null);


    /** Menubar group order. */
    private final int order;
    /** Menubar group htmlClasses. */
    private final String htmlClasses;


    /**
     * Constructor.
     *
     * @param order menubar group order
     */
    private MenubarGroup(int order, String htmlClasses) {
        this.order = order;
        this.htmlClasses = htmlClasses;
    }


    /**
     * {@inheritDoc}
     */
    public MenubarGroup getGroup() {
        return this;
    }


    /**
     * Getter for order.
     *
     * @return the order
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * Getter for htmlClasses.
     *
     * @return the htmlClasses
     */
    public String getHtmlClasses() {
        return this.htmlClasses;
    }

}
