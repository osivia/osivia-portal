package org.osivia.portal.core.security;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jboss.portal.common.invocation.InvocationContext;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.PortalObjectNavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.tracker.RequestContextUtil;


public class CmsPermissionHelper {

    public enum Level {

        forbidden, readOnly, readWrite;
    }

    private static final String CURRENT_PAGE_SECURITY_LEVEL = "osivia.currentPageSecurityLevel";

    private static ICMSService icmsService = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator").getCMSService();


    /**
     * Define the current security level of a document.
     * 
     * @param ctx the controllerContext
     * @param poid the current page
     * @throws ControllerException
     */
    public static Level getCurrentPageSecurityLevel(InvocationContext ctx, PortalObjectId poid) throws ControllerException {


        Level level = null;
        Boolean editableByUser = Boolean.FALSE;
        Boolean published = Boolean.TRUE;

        PortalObjectPath pagePath = poid.getPath();

        // // ============ Try to get a context
        ServerInvocation invocation = RequestContextUtil.getServerInvocation();
        //
        // HttpServletRequest request = invocation.getServerContext().getClientRequest();
        //
        PageNavigationalState pageState;
        //
        // // Le controller context est le meme pour tous les threads, on le stocke dans la requete
        // InvocationContext ctx = (ControllerContext) request.getAttribute("osivia.controllerContext");

        CMSServiceCtx cmsContext = new CMSServiceCtx();


        if (ctx instanceof ControllerContext) {
            // ControllerContext found
            NavigationalStateContext nsContext = (NavigationalStateContext) ctx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

            pageState = nsContext.getPageNavigationalState(poid.toString());

            cmsContext.setControllerContext((ControllerContext) ctx);


        } else {
            // Otherwise use a serverContext
            // ctx = invocation.getServerContext();

            PortalObjectNavigationalStateContext pnsCtx = new PortalObjectNavigationalStateContext(invocation.getContext().getAttributeResolver(
                    ControllerCommand.PRINCIPAL_SCOPE));

            pageState = pnsCtx.getPageNavigationalState(pagePath.toString());

            cmsContext.setServerInvocation(invocation);
        }


        // ============ check current session settings
        String cmsVersion = getCurrentCmsVersion(ctx);

        // ============ check publications info on this object
        // Get edit authorization

        String cmsPath = null;
        String sPath[] = null;
        if (pageState != null) {
            sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            if ((sPath != null) && (sPath.length == 1)) {
                cmsPath = sPath[0];
            }
        }


        if (cmsPath != null) {
            // Get edit authorization

            // ============ If var is yet defined
            Object attribute = ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, CURRENT_PAGE_SECURITY_LEVEL.concat(cmsPath));
            if (attribute != null) {
                level = (Level) attribute;


            } else {


                CMSPublicationInfos pubInfos = null;
                try {
                    pubInfos = icmsService.getPublicationInfos(cmsContext, cmsPath);
                } catch (CMSException e) {
                    throw new ControllerException(e);
                }

                editableByUser = pubInfos.isEditableByUser();
                published = pubInfos.isPublished();

                level = definePermissions(editableByUser, published, cmsVersion);

                ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, CURRENT_PAGE_SECURITY_LEVEL.concat(cmsPath), level);

            }
        }
        // ============ store the result in the request

        return level;

    }

    private static Level definePermissions(Boolean editableByUser, Boolean published, String cmsVersion) {
        Level level;
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
        return level;
    }

    public static String getCurrentCmsVersion(InvocationContext context) {

        String cmsVersion = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_VERSION);

        if (cmsVersion == null) {
            // first call, define the current cms version to "online".
            cmsVersion = InternalConstants.CMS_VERSION_ONLINE;
            context.setAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_VERSION, cmsVersion);
        }

        return cmsVersion;
    }

    public static String getCurrentCmsEditionMode(InvocationContext context) {

        String cmsEditionMode = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_EDITION_MODE);

        if (cmsEditionMode == null) {
            // first call, define the current cms edition mode to "on".
            cmsEditionMode = InternalConstants.CMS_EDITION_MODE_ON;
            context.setAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_EDITION_MODE, cmsEditionMode);
        }

        return cmsEditionMode;
    }
}
