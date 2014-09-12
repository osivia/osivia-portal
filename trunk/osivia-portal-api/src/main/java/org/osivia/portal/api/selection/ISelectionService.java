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

    /**
     * Add item to a specified selection.
     * 
     * @param portalCtx portal controller context
     * @param selectionId specified selection identifier
     * @param selectionItem item to add
     * @return true if the item has been added
     */
    boolean addItem(PortalControllerContext portalCtx, String selectionId, SelectionItem selectionItem);


    /**
     * Remove item to a specified selection.
     * 
     * @param portalCtx portal controller context
     * @param selectionId specified selection identifier
     * @param itemId item to remove identifier
     * @return true if the item has been removed
     */
    boolean removeItem(PortalControllerContext portalCtx, String selectionId, String itemId);


    /**
     * Access to a specified selection items set.
     * 
     * @param portalCtx portal controller context
     * @param selectionId specified selection identifier
     * @return the selection items set
     */
    Set<SelectionItem> getSelectionItems(PortalControllerContext portalCtx, String selectionId);


    /**
     * Delete a specified selection.
     * 
     * @param portalCtx portal controller context
     * @param selectionId specified selection identifier
     */
    void deleteSelection(PortalControllerContext portalCtx, String selectionId);

}
