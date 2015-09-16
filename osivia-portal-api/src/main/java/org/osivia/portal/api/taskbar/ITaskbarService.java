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

    /** Taskbar window instance. */
    String WINDOW_INSTANCE = "osivia-services-taskbar-instance";

    /** Taskbar home task identifier. */
    String HOME_TASK_ID = "HOME";

    /** Task identifier window property name. */
    String TASK_ID_WINDOW_PROPERTY = "osivia.taskbar.id";


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
     * @throws PortalException
     */
    List<TaskbarTask> getCustomTasks(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Get active task identifier.
     *
     * @param portalControllerContext portal controller context
     * @return task identifier
     * @throws PortalException
     */
    String getActiveId(PortalControllerContext portalControllerContext) throws PortalException;

}
