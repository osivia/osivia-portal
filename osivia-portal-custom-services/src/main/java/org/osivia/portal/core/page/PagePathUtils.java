package org.osivia.portal.core.page;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;

/**
 * Get page current properties.
 * 
 */
public class PagePathUtils {

    /** cms page path. */
    private static final String CMS_PATH = "osivia.cms.path";
    
    /** cms content path. */
    private static final String CMS_CONTENT_PATH = "osivia.cms.contentPath";

    /**
     * Comupte path.
     * 
     * @param controllerCtx the controller
     * @param pageId the page
     * @param var the requested property
     * @return the path
     */
    private static String computePath(ControllerContext controllerCtx, PortalObjectId pageId, String var) {
        NavigationalStateContext nsContext = (NavigationalStateContext) controllerCtx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        PageNavigationalState pageState = nsContext.getPageNavigationalState(pageId.toString());

        String[] sPath = null;
        if (pageState != null) {
            sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, var));
        }

        String pathPublication = null;
        if ((sPath != null) && (sPath.length > 0)) {
            pathPublication = sPath[0];
        }

        return pathPublication;
    }

    /**
     * Return the cms page path.
     * 
     * @param controllerCtx the controller
     * @param pageId the page
     * @return cms page path
     */
    public static String getNavigationPath(ControllerContext controllerCtx, PortalObjectId pageId) {
        return computePath(controllerCtx, pageId, CMS_PATH);
    }
    
    /**
     * Return the cms content path.
     * 
     * @param controllerCtx the controller
     * @param pageId the page
     * @return cms content path
     */
    public static String getContentPath(ControllerContext controllerCtx, PortalObjectId pageId) {
        return computePath(controllerCtx, pageId, CMS_CONTENT_PATH);
    }
}
