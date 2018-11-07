package org.osivia.portal.api.taskbar;

import java.util.List;

/**
 * Taskbar items.
 *
 * @author Cédric Krommenhoek
 */
public interface TaskbarItems {

    /**
     * Get taskbar item from his identifier.
     *
     * @param id taskbar item identifier
     * @return taskbar item
     */
    TaskbarItem get(String id);


    /**
     * Get all taskbar items.
     *
     * @return taskbar items
     */
    List<TaskbarItem> getAll();


    /**
     * Add taskbar item.
     *
     * @param taskbarItem taskbar item
     */
    void add(TaskbarItem item);


    /**
     * Add taskbar items.
     * 
     * @param items taskbar items
     */
    void add(List<TaskbarItem> items);


    /**
     * Remove taskbar item from his identifier.
     * 
     * @param id taskbar item identifier
     */
    void remove(String id);

}
