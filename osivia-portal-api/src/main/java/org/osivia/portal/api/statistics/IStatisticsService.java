package org.osivia.portal.api.statistics;

import javax.servlet.http.HttpSession;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Statistics service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface IStatisticsService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=StatisticsService";


    /**
     * Add user session statistics.
     * 
     * @param portalControllerContext portal controller context
     * @param path current path
     * @throws PortalException
     */
    void incrementsUserStatistics(PortalControllerContext portalControllerContext, String path) throws PortalException;


    /**
     * Aggregate user statistics.
     * 
     * @param httpSession HTTP session
     * @throws PortalException
     */
    void aggregateUserStatistics(HttpSession httpSession) throws PortalException;


    /**
     * Get space statistics.
     * 
     * @param portalControllerContext portal controller context
     * @param path space path
     * @return statistics
     * @throws PortalException
     */
    SpaceStatistics getSpaceStatistics(PortalControllerContext portalControllerContext, String path) throws PortalException;

}
