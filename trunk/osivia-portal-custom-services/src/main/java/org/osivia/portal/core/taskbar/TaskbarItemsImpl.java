package org.osivia.portal.core.taskbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osivia.portal.api.taskbar.TaskbarItem;
import org.osivia.portal.api.taskbar.TaskbarItems;

/**
 * Taskbar items implementation.
 *
 * @author Cédric Krommenhoek
 * @see TaskbarItems
 */
public class TaskbarItemsImpl implements TaskbarItems {

    /** Taskbar items. */
    private final Map<String, TaskbarItem> items;


    /**
     * Constructor.
     */
    public TaskbarItemsImpl() {
        super();
        this.items = new ConcurrentHashMap<String, TaskbarItem>();
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItem get(String id) {
        return this.items.get(id);
    }


    /**
     * {@inheritDoc}
     */
    public <T extends TaskbarItem> List<T> get(Class<T> expectedType) {
        List<T> results = new ArrayList<T>();
        for (TaskbarItem item : this.items.values()) {
            if (expectedType.isInstance(item)) {
                results.add(expectedType.cast(item));
            }
        }
        return results;
    }


    /**
     * {@inheritDoc}
     */
    public List<TaskbarItem> getAll() {
        return new ArrayList<TaskbarItem>(this.items.values());
    }


    /**
     * {@inheritDoc}
     */
    public void add(TaskbarItem item) {
        this.items.put(item.getId(), item);
    }


    /**
     * {@inheritDoc}
     */
    public void add(List<TaskbarItem> items) {
        for (TaskbarItem item : items) {
            this.add(item);
        }
    }

}
