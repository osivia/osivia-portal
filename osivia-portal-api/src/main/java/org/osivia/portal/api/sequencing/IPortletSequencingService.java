package org.osivia.portal.api.sequencing;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Portlet sequencing service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IPortletSequencingService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=PortletSequencingService";


    /**
     * Get sequencing attribute.
     *
     * @param portalControllerContext portal controller context
     * @param name attribute name
     * @return attribute
     */
    Object getAttribute(PortalControllerContext portalControllerContext, String name);


    /**
     * Set sequencing attribute.
     * 
     * @param portalControllerContext portal controller context
     * @param name attribute name
     * @param value attribute value
     */
    void setAttribute(PortalControllerContext portalControllerContext, String name, Object value);

}
