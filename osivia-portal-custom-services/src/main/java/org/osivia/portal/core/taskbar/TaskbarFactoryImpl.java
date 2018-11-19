package org.osivia.portal.core.taskbar;

import org.osivia.portal.api.panels.PanelPlayer;
import org.osivia.portal.api.portlet.PortalGenericPortlet;
import org.osivia.portal.api.taskbar.TaskbarFactory;
import org.osivia.portal.api.taskbar.TaskbarItem;
import org.osivia.portal.api.taskbar.TaskbarItemRestriction;
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
    public TaskbarItemImpl createTransversalTaskbarItem(String id, String key, String icon, PanelPlayer player) {
        TaskbarItemImpl item = this.createTaskbarItem(id, TaskbarItemType.TRANSVERSAL, key, icon);
        item.setPlayer(player);
        return item;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItemImpl createStapledTaskbarItem(String id, String key, String icon, PanelPlayer player) {
        TaskbarItemImpl item = this.createTaskbarItem(id, TaskbarItemType.STAPLED, key, icon);
        item.setPlayer(player);
        return item;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItem createStapledTaskbarItem(String id, String key, String icon, String template) {
        TaskbarItemImpl item = this.createTaskbarItem(id, TaskbarItemType.STAPLED, key, icon);
        item.setTemplate(template);
        return item;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItemImpl createCmsTaskbarItem(String id, String key, String icon, String documentType) {
        TaskbarItemImpl item = this.createTaskbarItem(id, TaskbarItemType.CMS, key, icon);
        item.setDocumentType(documentType);
        return item;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TaskbarItemImpl createHiddenCmsTaskbarItem(String id, String key, String documentType) {
        TaskbarItemImpl item = this.createCmsTaskbarItem(id, key, null, documentType);
        item.setHidden(true);
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
    @Override
    public void restrict(TaskbarItem item, TaskbarItemRestriction restriction) {
        if (item instanceof TaskbarItemImpl) {
            TaskbarItemImpl taskbarItemImpl = (TaskbarItemImpl) item;
            taskbarItemImpl.setRestriction(restriction);
        }
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarTask createTaskbarTask(TaskbarItem item, String title, String path, boolean disabled) {
        TaskbarTaskImpl task = new TaskbarTaskImpl(item);
        task.setTitle(title);
        task.setPath(path);
        task.setDisabled(disabled);
        return task;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarTask createTaskbarTask(String id, String title, String icon, String path, String documentType, boolean disabled) {
        TaskbarItem item = this.createCmsTaskbarItem(id, null, icon, documentType);
        TaskbarTaskImpl task = new TaskbarTaskImpl(item);
        task.setTitle(title);
        task.setPath(path);
        task.setDisabled(disabled);
        return task;
    }


}
