package org.osivia.portal.api.taskbar;

import org.osivia.portal.api.panels.PanelPlayer;

/**
 * Taskbar factory.
 *
 * @author Cédric Krommenhoek
 */
public interface TaskbarFactory {

    /**
     * Create taskbar items.
     *
     * @return taskbar items
     */
    TaskbarItems createTaskbarItems();


    /**
     * Create transversal taskbar item.
     *
     * @param id taskbar item identifier
     * @param key taskbar item internationalization key
     * @param icon taskbar item icon
     * @param player player
     * @return taskbar item
     */
    TaskbarItem createTransversalTaskbarItem(String id, String key, String icon, PanelPlayer player);


    /**
     * Create stapled taskbar item.
     *
     * @param id taskbar item identifier
     * @param key taskbar item internationalization key
     * @param icon taskbar item icon
     * @return taskbar item
     */
    TaskbarItem createStapledTaskbarItem(String id, String key, String icon);


    /**
     * Create CMS taskbar item.
     *
     * @param id taskbar item identifier
     * @param key taskbar item internationalization key
     * @param icon taskbar item icon
     * @param type taskbar item document type
     * @return taskbar item
     */
    TaskbarItem createCmsTaskbarItem(String id, String key, String icon, String documentType);


    /**
     * Create taskbar task.
     *
     * @param item taskbar item
     * @param path CMS path
     * @param disabled disabled indicator
     * @return taskbar task
     * @throws ReflectiveOperationException
     */
    TaskbarTask createTaskbarTask(TaskbarItem item, String path, boolean disabled) throws ReflectiveOperationException;


    /**
     * Create taskbar task.
     *
     * @param id identifier
     * @param title title
     * @param icon icon
     * @param path CMS path
     * @param documentType document type
     * @param disabled disabled indicator
     * @return taskbar task
     * @throws ReflectiveOperationException
     */
    TaskbarTask createTaskbarTask(String id, String title, String icon, String path, String documentType, boolean disabled)
            throws ReflectiveOperationException;

}
