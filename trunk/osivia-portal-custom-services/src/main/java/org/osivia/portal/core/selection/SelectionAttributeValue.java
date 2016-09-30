package org.osivia.portal.core.selection;

import java.util.LinkedHashSet;
import java.util.Set;

import org.osivia.portal.api.selection.SelectionItem;
import org.osivia.portal.core.attributes.StorageAttributeValue;

/**
 * Selection attribute value.
 *
 * @author Cédric Krommenhoek
 * @see StorageAttributeValue
 */
public class SelectionAttributeValue implements StorageAttributeValue {

    /** Selection items. */
    private final Set<SelectionItem> items;


    /**
     * Constructor.
     */
    public SelectionAttributeValue() {
        super();
        this.items = new LinkedHashSet<SelectionItem>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public StorageAttributeValue clone() {
        SelectionAttributeValue clone = new SelectionAttributeValue();
        clone.items.addAll(this.items);
        return clone;
    }


    /**
     * Getter for items.
     *
     * @return the items
     */
    public Set<SelectionItem> getItems() {
        return this.items;
    }

}
