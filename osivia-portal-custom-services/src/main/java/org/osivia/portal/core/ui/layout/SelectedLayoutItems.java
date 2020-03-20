package org.osivia.portal.core.ui.layout;

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
     * Constructor.
     */
    public SelectedLayoutItems() {
        super();
        this.selection = new ConcurrentHashMap<>();
    }


    public Map<String, String> getSelection() {
        return selection;
    }
}
