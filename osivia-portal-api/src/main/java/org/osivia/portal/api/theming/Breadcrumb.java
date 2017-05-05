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
package org.osivia.portal.api.theming;

import java.util.ArrayList;
import java.util.List;

/**
 * Breadcrumb java-bean.
 */
public class Breadcrumb {

    /** Menu HTML content. */
    private String menu;

    /** Breadcrumb children. */
    private final List<BreadcrumbItem> children;


    /**
     * Constructor.
     */
    public Breadcrumb() {
        super();
        this.children = new ArrayList<BreadcrumbItem>();
    }


    /**
     * Getter for menu.
     * 
     * @return the menu
     */
    public String getMenu() {
        return menu;
    }

    /**
     * Setter for menu.
     * 
     * @param menu the menu to set
     */
    public void setMenu(String menu) {
        this.menu = menu;
    }


    /**
     * Getter for children.
     * 
     * @return the children
     */
    public List<BreadcrumbItem> getChildren() {
        return children;
    }

}
