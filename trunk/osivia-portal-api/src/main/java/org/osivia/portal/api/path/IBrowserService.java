package org.osivia.portal.api.path;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Documents browser service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface IBrowserService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=BrowserService";


    /**
     * Browse documents into JSON data.
     * 
     * @param portalControllerContext portal controller context
     * @return JSON data
     * @throws PortalException
     */
    String browse(PortalControllerContext portalControllerContext) throws PortalException;

}
