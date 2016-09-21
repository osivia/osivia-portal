package org.osivia.portal.api.taskbar;

import java.util.List;
import java.util.SortedSet;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Taskbar service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface ITaskbarService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=TaskbarService";

    /** Taskbar task webId prefix. */
    String WEBID_PREFIX = "workspace_";

    /** Taskbar window instance. */
    String WINDOW_INSTANCE = "osivia-services-taskbar-instance";

    /** Taskbar home task identifier. */
    String HOME_TASK_ID = "HOME";

    /** Taskbar task identifier window property name. */
    String TASK_ID_WINDOW_PROPERTY = "osivia.taskbar.id";


    /**
     * Get taskbar item factory.
     *
     * @return taskbar item factory
     */
    TaskbarFactory getFactory();


    /**
     * Get taskbar items.
     *
     * @param portalControllerContext portal controller context
     * @return taskbar items
     * @throws PortalException
     */
    TaskbarItems getItems(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Get default items, sorted by order.
     *
     * @param portalControllerContext portal controller context
     * @return taskbar items
     * @throws PortalException
     */
    SortedSet<TaskbarItem> getDefaultItems(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Get tasks.
     *
     * @param portalControllerContext portal controller context
     * @param basePath CMS base path
     * @param navigation navigation usage indicator
     * @return tasks
     * @throws PortalException
     */
    List<TaskbarTask> getTasks(PortalControllerContext portalControllerContext, String basePath, boolean navigation) throws PortalException;


    /**
     * Get active task identifier.
     *
     * @param portalControllerContext portal controller context
     * @return task identifier
     * @throws PortalException
     */
    String getActiveId(PortalControllerContext portalControllerContext) throws PortalException;

}
