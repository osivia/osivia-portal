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

    /** Active taskbar request attribute name. */
    String REQUEST_ATTRIBUTE = "osivia.taskbar";
    /** Closed taskbar indicator request attribute name. */
    String CLOSED_REQUEST_ATTRIBUTE = "osivia.taskbar.closed";
    /** Switchable taskbar indicator request attribute name. */
    String SWITCHABLE_REQUEST_ATTRIBUTE = "osivia.taskbar.switchable";
    /** Taskbar status principal attribute name. */
    String STATUS_PRINCIPAL_ATTRIBUTE = "osivia.taskbar.status";

    /** Taskbar player window name. */
    String PLAYER_WINDOW_NAME = "taskbar-player-window";
    /** Taskbar player window region name. */
    String PLAYER_REGION_NAME = "taskbar-player";
    /** Task identifier window property name. */
    String TASK_ID_WINDOW_PROPERTY = "osivia.taskbar.id";
    /** Taskbar window instance. */
    String WINDOW_INSTANCE = "osivia-services-taskbar-instance";
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
     * Get active task identifier.
     *
     * @param portalControllerContext portal controller context
     * @param tasks tasks
     * @return task identifier
     */
    String getActiveId(PortalControllerContext portalControllerContext, List<? extends TaskbarTask> tasks);


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


    /**
     * Get taskbar state.
     *
     * @param portalControllerContext portal controller context
     * @return taskbar state
     */
    TaskbarState getTaskbarState(PortalControllerContext portalControllerContext);


    /**
     * Set taskbar state.
     *
     * @param portalControllerContext portal controller context
     * @param state taskbar state
     */
    void setTaskbarState(PortalControllerContext portalControllerContext, TaskbarState state);

}
