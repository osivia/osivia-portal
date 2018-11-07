package org.osivia.portal.core.taskbar;

import java.util.Comparator;

import org.osivia.portal.api.taskbar.TaskbarItem;

/**
 * Taskbar item comparator.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see TaskbarItem
 */
public class TaskbarItemComparator implements Comparator<TaskbarItem> {

    /**
     * Constructor.
     */
    public TaskbarItemComparator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public int compare(TaskbarItem item1, TaskbarItem item2) {
        int result;
        if (item1 == null) {
            result = -1;
        } else if (item2 == null) {
            result = 1;
        } else {
            Integer order1 = item1.getOrder();
            Integer order2 = item2.getOrder();
            result = order1.compareTo(order2);

            if (result == 0) {
                String id1 = item1.getId();
                String id2 = item2.getId();
                result = id1.compareTo(id2);
            }
        }
        return result;
    }

}
