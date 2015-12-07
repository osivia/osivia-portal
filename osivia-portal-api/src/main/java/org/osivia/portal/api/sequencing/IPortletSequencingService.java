package org.osivia.portal.api.sequencing;

import java.util.Map;

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
     * Get sequencing attributes.
     * 
     * @param portalControllerContext portal controller context
     * @return attributes
     */
    Map<String, Object> getAttributes(PortalControllerContext portalControllerContext);


    /**
     * Set sequencing attribute.
     *
     * @param portalControllerContext portal controller context
     * @param name attribute name
     * @param value attribute value
     */
    void setAttribute(PortalControllerContext portalControllerContext, String name, Object value);

}
