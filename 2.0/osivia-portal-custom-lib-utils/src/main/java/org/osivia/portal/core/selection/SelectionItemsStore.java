package org.osivia.portal.core.selection;

import java.util.Map;
import java.util.Set;

import org.osivia.portal.api.selection.SelectionItem;

/**
 * Selection item bean.
 * 
 * @author JS Steux
 */
public class SelectionItemsStore {

    /** update timestamps. */
    private Long updateTimestamp;
    

	/** list of items  */
    private  Set<SelectionItem> items;

    public Long getUpdateTimestamp() {
		return updateTimestamp;
	}

	public Set<SelectionItem> getItems() {
		return items;
	}


    /**
     * Default contructor.
     */
    public SelectionItemsStore(  Long updateTimestamp, Set<SelectionItem> items) {
        super();
        this.items = items;
        this.updateTimestamp = updateTimestamp;
    }


    

}
