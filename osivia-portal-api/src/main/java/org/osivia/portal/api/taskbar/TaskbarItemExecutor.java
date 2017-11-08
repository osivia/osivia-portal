package org.osivia.portal.api.taskbar;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Taskbar item executor.
 * 
 * @author Cédric Krommenhoek
 */
public interface TaskbarItemExecutor {

    /**
     * Invoke executor.
     * 
     * @param portalControllerContext portal controller context
     * @param task taskbar task
     * @throws PortalException
     */
    void invoke(PortalControllerContext portalControllerContext, TaskbarTask task) throws PortalException;

}
