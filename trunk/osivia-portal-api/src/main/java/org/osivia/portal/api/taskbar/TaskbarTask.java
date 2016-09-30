package org.osivia.portal.api.taskbar;

/**
 * Taskbar task.
 *
 * @author Cédric Krommenhoek
 * @see TaskbarItem
 */
public interface TaskbarTask extends TaskbarItem {

    /**
     * Get title.
     *
     * @return title
     */
    String getTitle();


    /**
     * Get task CMS path.
     *
     * @return CMS path
     */
    String getPath();


    /**
     * Check if task is disabled.
     *
     * @return true if task is disabled
     */
    boolean isDisabled();

}
