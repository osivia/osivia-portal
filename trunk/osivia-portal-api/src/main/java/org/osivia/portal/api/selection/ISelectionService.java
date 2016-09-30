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
package org.osivia.portal.api.selection;

import java.util.Set;

import org.osivia.portal.api.context.PortalControllerContext;


/**
 * Selection service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface ISelectionService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=SelectionService";


    /**
     * Add item to a specified selection.
     *
     * @param portalControllerContext portal controller context
     * @param id specified selection identifier
     * @param item selection item to add
     * @return true if the item has been added
     */
    boolean addItem(PortalControllerContext portalControllerContext, String id, SelectionItem item);


    /**
     * Remove item to a specified selection.
     *
     * @param portalControllerContext portal controller context
     * @param id specified selection identifier
     * @param itemId item to remove identifier
     * @return true if the item has been removed
     */
    boolean removeItem(PortalControllerContext portalControllerContext, String id, String itemId);


    /**
     * Access to a specified selection items set.
     *
     * @param portalControllerContext portal controller context
     * @param id specified selection identifier
     * @return the selection items set
     */
    Set<SelectionItem> getSelectionItems(PortalControllerContext portalControllerContext, String id);


    /**
     * Delete a specified selection.
     *
     * @param portalControllerContext portal controller context
     * @param id specified selection identifier
     */
    void deleteSelection(PortalControllerContext portalControllerContext, String id);

}
