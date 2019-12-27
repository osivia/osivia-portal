package org.osivia.portal.api.statistics;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.servlet.http.HttpSession;

/**
 * Statistics module interface.
 *
 * @author Cédric Krommenhoek
 */
public interface StatisticsModule {

    /**
     * Increments statistics.
     *
     * @param portalControllerContext portal controller context
     * @param path                    document path
     */
    void increments(PortalControllerContext portalControllerContext, String path) throws PortalException;


    /**
     * Update statistics.
     *
     * @param portalControllerContext portal controller context
     * @param httpSession
     */
    void update(PortalControllerContext portalControllerContext, HttpSession httpSession) throws PortalException;

}
