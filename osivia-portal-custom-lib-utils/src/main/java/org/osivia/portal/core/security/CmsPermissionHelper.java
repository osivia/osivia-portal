/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.osivia.portal.core.security;

import java.util.Locale;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jboss.portal.common.invocation.InvocationContext;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.PortalObjectNavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.tracker.RequestContextUtil;


public class CmsPermissionHelper {

    public enum Level {

        deny, allowOnlineVersion, allowPreviewVersion;
    }

    /** Edition CMS mode. */
    public static final String ATTR_TOOLBAR_CMS_VERSION = "osivia.toolbar.cmsVersion";
    /** Edition CMS mode. */
    public static final String ATTR_TOOLBAR_CMS_EDITION_MODE = "osivia.toolbar.cmsEditionMode";

    // CMS constants
    public static final String CMS_VERSION_PREVIEW = "preview";
    public static final String CMS_VERSION_ONLINE = "online";
    public static final String CMS_VERSION_LIVE = "live";
    public static final String CMS_EDITION_MODE_ON = "1";
    public static final String CMS_EDITION_MODE_OFF = "0";

    private static final String CURRENT_PAGE_SECURITY_LEVEL = "osivia.currentPageSecurityLevel";

    private static final String LAST_ALLOWED_PAGE = "osivia.lastAllowedPage";


    private static final String WARNING_MESSAGE_PREVIEW_VERSION_ONLY = "WARNING_MESSAGE_PREVIEW_VERSION_ONLY";
    private static final String WARNING_MESSAGE_ONLINE_VERSION_ONLY = "WARNING_MESSAGE_ONLINE_VERSION_ONLY";
    private static final String ERROR_MESSAGE_ACCESS_DENIED = "ERROR_MESSAGE_ACCESS_DENIED";


    private static ICMSServiceLocator icmsServiceLocactor = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");

    private static IInternationalizationService itlzService = Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);

    private static INotificationsService notifService = Locator.findMBean(INotificationsService.class, INotificationsService.MBEAN_NAME);
    
    
    /**
     * clear the previous cache
     * 
     * @param controllerContext
     */
    
    
    public static void clearCache( ControllerContext controllerContext){
        Set keys = controllerContext.getAttributeResolver(ControllerCommand.REQUEST_SCOPE).getKeys();
        for (Object key: keys)  {
            if( key.toString().startsWith("osivia.currentPageSecurityLevel"))  {
                controllerContext.getAttributeResolver(ControllerCommand.REQUEST_SCOPE).setAttribute(key, null);
            }
        }
    }

    /**
     * Define the current security level of a document.
     *
     * @param ctx the controllerContext
     * @param poid the current page
     * @throws ControllerException
     */
    public static Level getCurrentPageSecurityLevel(InvocationContext ctx, PortalObjectId poid) throws CMSException {

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

        return getCurrentPageSecurityLevel(ctx, cmsPath);

    }

    public static Level getCurrentPageSecurityLevel(InvocationContext ctx, String cmsPath) throws CMSException {
        Level level = null;
        Boolean editableByUser = Boolean.FALSE;
        Boolean published = Boolean.TRUE;
        Boolean belongToPublishSpace = Boolean.TRUE;
        Locale locale;
        // ============ check current session settings
        String cmsVersion = getCurrentCmsVersion(ctx);

        CMSServiceCtx cmsContext = new CMSServiceCtx();


        if (ctx instanceof ControllerContext) {
            // ControllerContext found
            cmsContext.setControllerContext((ControllerContext) ctx);

            locale = ((ControllerContext) ctx).getServerInvocation().getRequest().getLocale();

        } else {
            // Otherwise use a serverContext
            cmsContext.setServerInvocation(RequestContextUtil.getServerInvocation());

            locale = RequestContextUtil.getServerInvocation().getRequest().getLocale();
        }

        if (cmsPath != null) {
            // Get edit authorization

            // ============ If var is yet defined
            Object attribute = ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, CURRENT_PAGE_SECURITY_LEVEL.concat(cmsPath));
            if (attribute != null) {
                level = (Level) attribute;


            } else {
                String liveEditionPath = (String) ctx.getAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LIVE_DOCUMENT);
                cmsContext.setForcedLivePath(liveEditionPath);

                CMSPublicationInfos pubInfos = null;



                pubInfos = icmsServiceLocactor.getCMSService().getPublicationInfos(cmsContext, cmsPath);

                if( !pubInfos.isPublished()) {
                    cmsContext.setDisplayLiveVersion("1");
                }

                boolean isWebPage = icmsServiceLocactor.getCMSService().isCmsWebPage(cmsContext, cmsPath);



                editableByUser = pubInfos.isEditableByUser();
                published = pubInfos.isPublished();
                belongToPublishSpace = pubInfos.getPublishSpacePath() != null;

                if (!isWebPage) {
                    cmsVersion = CMS_VERSION_ONLINE;

                    if ((pubInfos.getPublishSpacePath() != null) && pubInfos.isLiveSpace()) {
                        cmsVersion = CMS_VERSION_LIVE;
                    }
                }


                if ((pubInfos.getPublishSpacePath() != null) && !pubInfos.isLiveSpace() && (pubInfos.getDocumentPath().equals(liveEditionPath) || pubInfos.getLiveId().equals(liveEditionPath))) {
                    cmsVersion = CMS_VERSION_PREVIEW;
                }


                level = definePermissions(ctx, locale, editableByUser, published, cmsVersion, belongToPublishSpace);

                ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, CURRENT_PAGE_SECURITY_LEVEL.concat(cmsPath), level);

                if (level != Level.deny) {
                    ctx.setAttribute(ControllerCommand.SESSION_SCOPE, LAST_ALLOWED_PAGE, cmsPath);
                }

            }
        }
        // ============ store the result in the request

        return level;
    }

    private static Level definePermissions(InvocationContext ctx, Locale locale, Boolean editableByUser, Boolean published, String cmsVersion,
            Boolean belongToPublishSpace) {
        Level level;
        // ============ Permission management

        // online requested
        if (cmsVersion.equals(CMS_VERSION_ONLINE)) {
            if (published) {
                // document is published and can be access in online mode
                level = Level.allowOnlineVersion;

            } else if (!belongToPublishSpace) {
                // document is not in publish space, force preview to access nuxeo objects
                level = Level.allowPreviewVersion;

            } else if (editableByUser) {
                // document is NOT published, user can see the preview version
                level = Level.deny;

                PortalControllerContext pcc = new PortalControllerContext(ctx);
                String message = itlzService.getString(WARNING_MESSAGE_PREVIEW_VERSION_ONLY, locale);

                // Show a notification
                notifService.addSimpleNotification(pcc, message, NotificationsType.ERROR);

            } else {
                // document is NEITHER published NOR editable
                // access is forbidden
                level = Level.deny;

                PortalControllerContext pcc = new PortalControllerContext(ctx);
                String message = itlzService.getString(ERROR_MESSAGE_ACCESS_DENIED, locale);

                // Show a notification
                notifService.addSimpleNotification(pcc, message, NotificationsType.ERROR);
            }
        } else if (cmsVersion.equals(CMS_VERSION_LIVE)) {
            level = Level.allowPreviewVersion;
        }
        // preview requested
        else {
            if (editableByUser) {
                // document editable by user
                level = Level.allowPreviewVersion;
            } else if (published) {
                // document is published but NOT editable, user can see the online version
                level = Level.deny;

                PortalControllerContext pcc = new PortalControllerContext(ctx);
                String message = itlzService.getString(WARNING_MESSAGE_ONLINE_VERSION_ONLY, locale);

                // Show a notification
                notifService.addSimpleNotification(pcc, message, NotificationsType.ERROR);
            } else {
                // document is NEITHER published NOR editable
                // access is forbidden
                level = Level.deny;

                PortalControllerContext pcc = new PortalControllerContext(ctx);
                String message = itlzService.getString(ERROR_MESSAGE_ACCESS_DENIED, locale);

                // Show a notification
                notifService.addSimpleNotification(pcc, message, NotificationsType.ERROR);
            }
        }
        return level;
    }

    public static String getCurrentCmsVersion(InvocationContext context) {

        String cmsVersion = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, ATTR_TOOLBAR_CMS_VERSION);

        if (cmsVersion == null) {
            // first call, define the current cms version to "online".
            cmsVersion = CMS_VERSION_ONLINE;
            context.setAttribute(ControllerCommand.SESSION_SCOPE, ATTR_TOOLBAR_CMS_VERSION, cmsVersion);
        }

        return cmsVersion;
    }

    public static String getCurrentCmsEditionMode(InvocationContext context) {

        String cmsEditionMode = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, ATTR_TOOLBAR_CMS_EDITION_MODE);

        if (cmsEditionMode == null) {
            // first call, define the current cms edition mode to "on".
            cmsEditionMode = CMS_EDITION_MODE_ON;
            context.setAttribute(ControllerCommand.SESSION_SCOPE, ATTR_TOOLBAR_CMS_EDITION_MODE, cmsEditionMode);
        }


        return cmsEditionMode;
    }

    public static void changeCmsMode(InvocationContext context, String pagePath, String version, String editionMode) {

        context.setAttribute(ControllerCommand.SESSION_SCOPE, ATTR_TOOLBAR_CMS_VERSION, version);
        context.setAttribute(ControllerCommand.SESSION_SCOPE, ATTR_TOOLBAR_CMS_EDITION_MODE, editionMode);

        context.removeAttribute(ControllerCommand.REQUEST_SCOPE, CURRENT_PAGE_SECURITY_LEVEL.concat(pagePath));

    }

    public static String getLastAllowedPage(InvocationContext context) {

        String lastAllowedPage = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, LAST_ALLOWED_PAGE);

        // TODO get cms base path
        if (lastAllowedPage == null) {

        }


        return lastAllowedPage;
    }
}
