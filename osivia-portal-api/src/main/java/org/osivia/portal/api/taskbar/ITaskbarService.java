package org.osivia.portal.api.taskbar;

import java.util.List;

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

    /** Taskbar window name. */
    String WINDOW_NAME = "taskbar-window";
    /** Taskbar region name page property name. */
    String REGION_NAME_PAGE_PROPERTY = "osivia.taskbar.region";
    /** Task identifier window property name. */
    String TASK_ID_WINDOW_PROPERTY = "osivia.taskbar.id";
    /** Taskbar empty window instance. */
    String EMPTY_WINDOW_INSTANCE = "osivia-services-taskbar-empty-instance";

    /** Taskbar home task identifier. */
    String HOME_TASK_ID = "HOME";


    /**
     * Get taskbar navigation tasks.
     *
     * @param portalControllerContext portal controller context
     * @param basePath CMS base path
     * @param currentPath CMS current path
     * @return tasks
     * @throws PortalException
     */
    List<TaskbarTask> getNavigationTasks(PortalControllerContext portalControllerContext, String basePath, String currentPath) throws PortalException;


    /**
     * Get taskbar custom tasks.
     *
     * @param portalControllerContext portal controller context
     * @return tasks
     */
    List<TaskbarTask> getCustomTasks(PortalControllerContext portalControllerContext);


    /**
     * Get taskbar region name.
     *
     * @param portalControllerContext portal controller context
     * @return taskbar region name
     */
    String getRegion(PortalControllerContext portalControllerContext);


    /**
     * Get active task identifier.
     *
     * @param portalControllerContext portal controller context
     * @param tasks tasks
     * @return task identifier
     */
    String getActiveTaskId(PortalControllerContext portalControllerContext, List<? extends TaskbarTask> tasks);


    /**
     * Add taskbar window.
     *
     * @param portalControllerContext portal controller context
     * @param player taskbar player
     * @param id task identifier
     */
    void addWindow(PortalControllerContext portalControllerContext, TaskbarPlayer player, String id);


    /**
     * Add empty taskbar window.
     *
     * @param portalControllerContext portal controller context
     * @param id task identifier
     */
    void addEmptyWindow(PortalControllerContext portalControllerContext, String id);

}
