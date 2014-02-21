package org.osivia.portal.api.path;

import javax.portlet.PortletException;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * CMS path service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface ICMSPathService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=CMSPathService";


    /**
     * Browse CMS content in lazy-loading mode.
     * This method return JSON formatted content, useful for JSTree.
     * 
     * @param portalControllerContext portal controller context
     * @param path CMS path
     * @param liveContent live content indicator
     * @param onlyNavigableItems browse only navigable items indicator
     * @return JSON formatted content
     * @throws PortletException
     */
    String browseContentLazyLoading(PortalControllerContext portalControllerContext, String path, boolean liveContent, boolean onlyNavigableItems)
            throws PortletException;

}
