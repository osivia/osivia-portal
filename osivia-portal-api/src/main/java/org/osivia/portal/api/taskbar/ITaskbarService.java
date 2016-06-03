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
     * Get navigation tasks.
     *
     * @param portalControllerContext portal controller context
     * @param basePath CMS base path
     * @return tasks
     * @throws PortalException
     */
    List<TaskbarTask> getNavigationTasks(PortalControllerContext portalControllerContext, String basePath) throws PortalException;


    /**
     * Get active task identifier.
     *
     * @param portalControllerContext portal controller context
     * @return task identifier
     * @throws PortalException
     */
    String getActiveId(PortalControllerContext portalControllerContext) throws PortalException;

}
