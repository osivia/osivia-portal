package org.osivia.portal.core.ui.layout;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.osivia.portal.api.ui.layout.LayoutItem;

import java.io.Serializable;
import java.util.List;

/**
 * Layout items container.
 *
 * @author Cédric Krommenhoek
 */
public class LayoutItemsContainer {

    /**
     * Layout items.
     */
    @JsonProperty("items")
    private List<LayoutItemImpl> items;


    /**
     * Constructor.
     */
    public LayoutItemsContainer() {
        super();
    }


    public List<LayoutItemImpl> getItems() {
        return items;
    }

    public void setItems(List<LayoutItemImpl> items) {
        this.items = items;
    }

}
