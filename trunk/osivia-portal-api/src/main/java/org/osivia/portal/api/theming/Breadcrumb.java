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

import org.osivia.portal.api.menubar.MenubarItem;

/**
 * Breadcrumb java-bean.
 */
public class Breadcrumb {
	
    /** Breadcrumb children. */
    private final List<BreadcrumbItem> children;
    /** Breadcrumb associated edition menubar items. */
    private final List<MenubarItem> menubarItems;


    /**
     * Constructor.
     */
    public Breadcrumb() {
        super();
        this.children = new ArrayList<BreadcrumbItem>();
        this.menubarItems = new ArrayList<MenubarItem>();
    }


    /**
     * Getter for children.
     * 
     * @return the children
     */
    public List<BreadcrumbItem> getChildren() {
        return children;
    }

    /**
     * Getter for menubarItems.
     * 
     * @return the menubarItems
     */
    public List<MenubarItem> getMenubarItems() {
        return menubarItems;
    }

}
