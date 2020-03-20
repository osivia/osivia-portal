package org.osivia.portal.core.ui.layout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections.CollectionUtils;
import org.osivia.portal.api.ui.layout.LayoutGroup;
import org.osivia.portal.api.ui.layout.LayoutItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout group implementation.
 *
 * @author Cédric Krommenhoek
 * @see LayoutGroup
 */
public class LayoutGroupImpl implements LayoutGroup {

    /**
     * Items.
     */
    @JsonIgnore
    private final List<LayoutItem> items;


    /**
     * Identifier.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Label.
     */
    @JsonProperty("label")
    private String label;

    /**
     * Items implementation.
     */
    @JsonProperty("items")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<LayoutItemImpl> itemsImpl;


    /**
     * Constructor.
     */
    public LayoutGroupImpl() {
        super();
        this.items = new ArrayList<>();
    }


    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public List<LayoutItem> getItems() {
        return this.items;
    }

    public List<LayoutItemImpl> getItemsImpl() {
        return this.itemsImpl;
    }

    public void setItemsImpl(List<LayoutItemImpl> itemsImpl) {
        this.itemsImpl = itemsImpl;
    }
}
