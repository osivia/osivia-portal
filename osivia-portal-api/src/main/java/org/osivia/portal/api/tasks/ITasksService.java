package org.osivia.portal.api.tasks;

import java.util.UUID;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Tasks service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface ITasksService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=TasksService";


    /**
     * Get tasks count.
     * 
     * @param portalControllerContext portal controller context
     * @return tasks count
     * @throws PortalException
     */
    int getTasksCount(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Reset tasks count.
     * 
     * @param portalControllerContext portal controller context
     * @throws PortalException
     */
    void resetTasksCount(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Get update task command URL.
     * 
     * @param portalControllerContext portal controller context
     * @param uuid UUID
     * @param actionId action identifier
     * @param redirectionUrl redirection URL
     * @return URL
     * @throws PortalException
     */
    String getCommandUrl(PortalControllerContext portalControllerContext, UUID uuid, String actionId, String redirectionUrl) throws PortalException;

}
