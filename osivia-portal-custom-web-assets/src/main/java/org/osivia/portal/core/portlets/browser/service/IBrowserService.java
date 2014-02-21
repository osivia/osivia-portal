package org.osivia.portal.core.portlets.browser.service;

import javax.portlet.PortletException;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Live content browser service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface IBrowserService {

    /**
     * Browse live content for current node children only, in lazy loading JSON data.
     * 
     * @param portalControllerContext portal controller context
     * @param parentPath parent path, may be null for root node
     * @return JSON data
     * @throws PortletException
     */
    String browse(PortalControllerContext portalControllerContext, String parentPath) throws PortletException;


}
