package org.osivia.portal.core.taskbar;

import org.osivia.portal.api.panels.PanelPlayer;
import org.osivia.portal.api.portlet.PortalGenericPortlet;
import org.osivia.portal.api.taskbar.TaskbarFactory;
import org.osivia.portal.api.taskbar.TaskbarItem;
import org.osivia.portal.api.taskbar.TaskbarItemType;
import org.osivia.portal.api.taskbar.TaskbarItems;
import org.osivia.portal.api.taskbar.TaskbarTask;

/**
 * Taskbar factory implementation.
 *
 * @author Cédric Krommenhoek
 * @see TaskbarFactory
 */
public class TaskbarFactoryImpl implements TaskbarFactory {

    /**
     * Constructor.
     */
    public TaskbarFactoryImpl() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItems createTaskbarItems() {
        return new TaskbarItemsImpl();
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItem createTransversalTaskbarItem(String id, String key, String icon, PanelPlayer player) {
        TaskbarItemImpl item = this.createTaskbarItem(id, TaskbarItemType.TRANSVERSAL, key, icon);
        item.setPlayer(player);
        return item;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItem createStapledTaskbarItem(String id, String key, String icon) {
        TaskbarItemImpl item = this.createTaskbarItem(id, TaskbarItemType.STAPLED, key, icon);
        return item;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItem createCmsTaskbarItem(String id, String key, String icon, String documentType) {
        TaskbarItemImpl item = this.createTaskbarItem(id, TaskbarItemType.CMS, key, icon);
        item.setDocumentType(documentType);
        return item;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItem createDefaultCmsTaskbarItem(String id, String key, String icon, String documentType, int order) {
        TaskbarItemImpl item = (TaskbarItemImpl) this.createCmsTaskbarItem(id, key, icon, documentType);
        item.setDefaultItem(true);
        item.setOrder(order);
        return item;
    }


    /**
     * Create generic taskbar item.
     *
     * @param id identifier
     * @param type type
     * @param key internationalization key
     * @param icon icon
     * @return taskbar item
     */
    private TaskbarItemImpl createTaskbarItem(String id, TaskbarItemType type, String key, String icon) {
        TaskbarItemImpl item = new TaskbarItemImpl();
        item.setId(id);
        item.setType(type);
        item.setKey(key);
        item.setIcon(icon);
        item.setCustomizedClassLoader(PortalGenericPortlet.CLASS_LOADER_CONTEXT.get());
        return item;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarTask createTaskbarTask(TaskbarItem item, String path, boolean disabled) throws ReflectiveOperationException {
        TaskbarTaskImpl task = new TaskbarTaskImpl(item);
        task.setPath(path);
        task.setDisabled(disabled);
        return task;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarTask createTaskbarTask(String id, String title, String icon, String path, String documentType, boolean disabled)
            throws ReflectiveOperationException {
        TaskbarItem item = this.createCmsTaskbarItem(id, null, icon, documentType);
        TaskbarTaskImpl task = new TaskbarTaskImpl(item);
        task.setTitle(title);
        task.setPath(path);
        task.setDisabled(disabled);
        return task;
    }

}
