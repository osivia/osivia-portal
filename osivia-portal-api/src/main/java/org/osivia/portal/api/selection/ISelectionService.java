package org.osivia.portal.api.selection;

import java.util.Set;


/**
 * Selection service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface ISelectionService {

    /**
     * Add item to a specified selection.
     * 
     * @param request generated request
     * @param selectionId specified selection identifier
     * @param selectionItem item to add
     * @return true if the item has been added
     */
    boolean addItem(Object request, String selectionId, SelectionItem selectionItem);

    /**
     * Remove item to a specified selection.
     * 
     * @param request generated request
     * @param selectionId specified selection identifier
     * @param itemId item to remove identifier
     * @return true if the item has been removed
     */
    boolean removeItem(Object request, String selectionId, String itemId);

    /**
     * Access to a specified selection items set.
     * 
     * @param request generated request
     * @param selectionId specified selection identifier
     * @return the selection items set
     */
    Set<SelectionItem> getSelectionItems(Object request, String selectionId);

    /**
     * Delete a specified selection
     * 
     * @param request generated request
     * @param selectionId specified selection identifier
     */
    void deleteSelection(Object request, String selectionId);

}
