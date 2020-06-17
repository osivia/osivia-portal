package org.osivia.portal.core.ui.layout;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Selected layout items.
 *
 * @author Cédric Krommenhoek
 */
public class SelectedLayoutItems {

    /**
     * Selection.
     */
    private final Map<String, String> selection;
    /**
     * Computed window identifiers.
     */
    private final Map<String, List<String>> computedWindowIds;


    /**
     * Constructor.
     */
    public SelectedLayoutItems() {
        super();
        this.selection = new ConcurrentHashMap<>();
        this.computedWindowIds = new ConcurrentHashMap<>();
    }


    public Map<String, String> getSelection() {
        return selection;
    }

    public Map<String, List<String>> getComputedWindowIds() {
        return computedWindowIds;
    }
}
