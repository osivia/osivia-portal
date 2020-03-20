package org.osivia.portal.api.ui.layout;

import java.util.List;

/**
 * Layout group interface.
 *
 * @author Cédric Krommenhoek
 */
public interface LayoutGroup {

    /**
     * Get identifier.
     *
     * @return identifier
     */
    String getId();


    /**
     * Get label.
     *
     * @return label
     */
    String getLabel();


    /**
     * Set label.
     *
     * @param label label
     */
    void setLabel(String label);


    /**
     * Get layout items.
     *
     * @return layout items
     */
    List<LayoutItem> getItems();

}
