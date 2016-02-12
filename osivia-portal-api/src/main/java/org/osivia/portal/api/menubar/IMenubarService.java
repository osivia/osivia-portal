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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Menubar service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IMenubarService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=MenubarService";

    /** Menubar window identifier. */
    String MENUBAR_WINDOW_ID = "menubar-window";
    /** Menubar region name. */
    String MENUBAR_REGION_NAME = "menubar";


    /**
     * Get menubar dropdown menu.
     *
     * @param portalControllerContext portal controller context
     * @param id menubar dropdown menu identifier
     * @return menubar dropdown menu
     */
    MenubarDropdown getDropdown(PortalControllerContext portalControllerContext, String id);


    /**
     * Add menubar dropdown menu.
     *
     * @param portalControllerContext portal controller context
     * @param dropdown menubar dropdown menu
     */
    void addDropdown(PortalControllerContext portalControllerContext, MenubarDropdown dropdown);


    /**
     * Generate navbar actions menubar HTML content.
     *
     * @param portalControllerContext portal controller context
     * @return HTML content
     */
    String generateNavbarContent(PortalControllerContext portalControllerContext);


    /**
     * Get navbar menubar items, sorted by groups.
     *
     * @param portalControllerContext portal controller context
     * @return sorted menubar items
     */
    Map<MenubarGroup, Set<MenubarItem>> getNavbarSortedItems(PortalControllerContext portalControllerContext);


    /**
     * Generate portlet menubar HTML content.
     *
     * @param portalControllerContext portal controller context
     * @param items portlet menubar items
     * @return HTML content
     */
    String generatePortletContent(PortalControllerContext portalControllerContext, List<MenubarItem> items);


    /**
     * Get portlet menubar items, sorted by groups.
     *
     * @param items menubar items
     * @return sorted menubar items
     */
    Map<MenubarGroup, Set<MenubarItem>> getPortletSortedItems(List<MenubarItem> items);


    /**
     * Get state menubar items.
     *
     * @param portalControllerContext portal controller context
     * @return menubar items
     */
    List<MenubarItem> getStateItems(PortalControllerContext portalControllerContext);

}
