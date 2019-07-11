package org.osivia.portal.api.tasks;

import java.util.List;
import java.util.UUID;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Tasks service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface ITasksService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=TasksService";

    /** Customized host system property. */
    String HOST_PROPERTY = "osivia.tasks.host";


    /**
     * Get current user tasks.
     * 
     * @param portalControllerContext portal controller context
     * @return tasks
     * @throws PortalException
     */
    List<EcmDocument> getTasks(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Get current user task.
     * 
     * @param portalControllerContext portal controller context
     * @param path task path
     * @return task
     * @throws PortalException
     */
    EcmDocument getTask(PortalControllerContext portalControllerContext, String path) throws PortalException;


    /**
     * Get current user task.
     * 
     * @param portalControllerContext portal controller context
     * @param uuid task UUID
     * @return task
     * @throws PortalException
     */
    EcmDocument getTask(PortalControllerContext portalControllerContext, UUID uuid) throws PortalException;


    /**
     * Get current user tasks count.
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
     * @return URL
     * @throws PortalException
     */
    String getCommandUrl(PortalControllerContext portalControllerContext, UUID uuid, String actionId) throws PortalException;
    
    
    /**
     * Gets  view task url.
     *
     * @param portalControllerContext the portal controller context
     * @param uuid the uuid
     * @return the view task url
     * @throws PortalException the portal exception
     */
    String getViewTaskUrl(PortalControllerContext portalControllerContext, UUID uuid) throws PortalException;

}
