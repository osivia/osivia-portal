package org.osivia.portal.core.security;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;


public class CmsPermissionHelper {

    public enum Level {

        forbidden, readOnly, readWrite;
    }

    private static final String CURRENT_PAGE_SECURITY_LEVEL = "osivia.currentPageSecurityLevel";

    private static ICMSService icmsService = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator").getCMSService();

    /**
     * Get the current security level of a document.
     * 
     * @param ctx the controllerContext
     * @return the current security level
     */
    public static Level getCurrentPageSecurityLevel(ControllerContext ctx) {

        return (Level) ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, CURRENT_PAGE_SECURITY_LEVEL);
    }

    public static Level getCurrentPageSecurityLevel(ServerInvocation ctx) {

        return (Level) ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, CURRENT_PAGE_SECURITY_LEVEL);
    }

    /**
     * Define the current security level of a document.
     * 
     * @param ctx the controllerContext
     * @param poid the current page
     * @throws ControllerException
     */
    public static void setCurrentPageSecurityLevel(ControllerContext ctx, PortalObjectId poid) throws ControllerException {

        Level level;
        Boolean editableByUser = Boolean.FALSE;
        Boolean published = Boolean.TRUE;

        // ============ check current session settings
        String cmsVersion = (String) ctx.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_VERSION);

        if (cmsVersion == null) {
            // first call, define the current cms version to "online".
            cmsVersion = InternalConstants.CMS_VERSION_ONLINE;
            ctx.setAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_VERSION, cmsVersion);
        }

        // ============ check publications info on this object
        // Get edit authorization
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setServerInvocation(ctx.getServerInvocation());

        NavigationalStateContext nsContext = (NavigationalStateContext) ctx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);


        PageNavigationalState pageState = nsContext.getPageNavigationalState(poid.toString());


        String pagePath = null;
        String sPath[] = null;
        if (pageState != null) {
            sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            if ((sPath != null) && (sPath.length == 1)) {
                pagePath = sPath[0];
            }
        }


        if (pagePath != null) {
            // Get edit authorization


            CMSPublicationInfos pubInfos = null;
            try {
                pubInfos = icmsService.getPublicationInfos(cmsContext, pagePath);
            } catch (CMSException e) {
                throw new ControllerException(e);
            }

            editableByUser = pubInfos.isEditableByUser();
            published = pubInfos.isPublished();

        }


        // ============ Permission management

        // online requested
        if (cmsVersion.equals(InternalConstants.CMS_VERSION_ONLINE)) {
            if (published) {
                // document is published and can be access in online mode
                level = Level.readOnly;
            } else if (editableByUser) {
                // document is NOT published, user can see the preview version
                level = Level.readWrite;
            } else {
                // document is NEITHER published NOR editable
                     // access is forbidden
                level = Level.forbidden;
            }
        }
        // preview requested
        else {
            if (editableByUser) {
                // document editable by user
                level = Level.readWrite;
            } else if (published) {
                // document is published but NOT editable, user can see the online version
                level = Level.readOnly;
            } else {
                // document is NEITHER published NOR editable
                    // access is forbidden
                level = Level.forbidden;
            }
        }

        // ============ store the result in the request
        ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, CURRENT_PAGE_SECURITY_LEVEL, level);
    }
}
