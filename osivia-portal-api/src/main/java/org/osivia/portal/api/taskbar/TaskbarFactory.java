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
     * @param player player
     * @return taskbar item
     */
    TaskbarItem createStapledTaskbarItem(String id, String key, String icon, PanelPlayer player);


    /**
     * Create stapled taskbar item.
     *
     * @param id taskbar item identifier
     * @param key taskbar item internationalization key
     * @param icon taskbar item icon
     * @param template template
     * @return taskbar item
     */
    TaskbarItem createStapledTaskbarItem(String id, String key, String icon, String template);


    /**
     * Create CMS taskbar item.
     *
     * @param id taskbar item identifier
     * @param key taskbar item internationalization key
     * @param icon taskbar item icon
     * @param documentType taskbar item document type
     * @return taskbar item
     */
    TaskbarItem createCmsTaskbarItem(String id, String key, String icon, String documentType);


    /**
     * Hide taskbar item.
     * 
     * @param item taskbar item
     * @param hidden hidden indicator
     */
    void hide(TaskbarItem item, boolean hidden);


    /**
     * Restrict taskbar item access.
     * 
     * @param item taskbar item
     * @param restriction restriction
     */
    void restrict(TaskbarItem item, TaskbarItemRestriction restriction);


    /**
     * Preset taskbar item.
     * 
     * @param item taskbar item
     * @param preset preset indicator
     * @param order taskbar preset order, may be null to keep default value
     */
    void preset(TaskbarItem item, boolean preset, Integer order);


    /**
     * Create taskbar task.
     *
     * @param item taskbar item
     * @param path CMS path
     * @param disabled disabled indicator
     * @return taskbar task
     */
    TaskbarTask createTaskbarTask(TaskbarItem item, String path, boolean disabled);


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
     */
    TaskbarTask createTaskbarTask(String id, String title, String icon, String path, String documentType, boolean disabled);

}
