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
 *
 */
package org.osivia.portal.api.urls;

import java.util.Map;

import org.jboss.portal.core.model.portal.Page;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.ecm.EcmCommonCommands;
import org.osivia.portal.api.ecm.EcmViews;

/**
 * Portal URL factory API interface.
 *
 * @author Jean-Sébastien Steux
 */
public interface IPortalUrlFactory {

    /** Contextualization type "portlet". */
    String CONTEXTUALIZATION_PORTLET = "portlet";
    /** Contextualization type "page". */
    String CONTEXTUALIZATION_PAGE = "page";
    /** Contextualization type "portal". */
    String CONTEXTUALIZATION_PORTAL = "portal";

    /** Permalink type "page". */
    String PERM_LINK_TYPE_PAGE = "page";
    /** Permalink type "RSS". */
    String PERM_LINK_TYPE_RSS = "rss";
    /** Permalink type "RSS picture". */
    String PERM_LINK_TYPE_RSS_PICTURE = "rsspicture";
    /** Permalink type "CMS". */
    String PERM_LINK_TYPE_CMS = "cms";
    /** Permalink type share. */
    String PERM_LINK_TYPE_SHARE = "share";

    /** Popup URL adapter open status. */
    int POPUP_URL_ADAPTER_OPEN = 0;
    /** Popup URL adapter close status. */
    int POPUP_URL_ADAPTER_CLOSE = 1;
    /** Popup URL adapter closed notification status. */
    int POPUP_URL_ADAPTER_CLOSED_NOTIFICATION = 2;

    /** Display context refresh. */
    String DISPLAYCTX_REFRESH = "refreshPageAndNavigation";

    /** Display context preview (live version for validation purpose). */
    String DISPLAYCTX_PREVIEW_LIVE_VERSION = "preview";

    String MBEAN_NAME = "osivia:service=UrlFactory";

    /**
     * Get portal CMS contextualized page.
     * TODO: move it in CMS.
     *
     * @param portalControllerContext portal controller context
     * @param path path
     * @return contextualized page
     * @throws Exception
     */
    Page getPortalCMSContextualizedPage(PortalControllerContext portalControllerContext, String path) throws PortalException;


    /**
     * Get CMS URL.
     *
     * @param portalControllerContext portal controller context
     * @param pagePath page path
     * @param cmsPath CMS path
     * @param pageParams page parameters
     * @param contextualization contextualization
     * @param displayContext display context
     * @param hideMetaDatas hide meta datas
     * @param scope scope
     * @param displayLiveVersion display live version
     * @param windowPermReference window perm reference
     * @return CMS url
     */
    String getCMSUrl(PortalControllerContext portalControllerContext, String pagePath, String cmsPath, Map<String, String> pageParams, String contextualization,
            String displayContext, String hideMetaDatas, String scope, String displayLiveVersion, String windowPermReference);


    /**
     * Get permalink URL.
     *
     * @param portalControllerContext portal controller context
     * @param permLinkRef permalink reference
     * @param params parameters
     * @param cmsPath CMS path
     * @param permLinkType permalink type
     * @return permalink URL
     * @throws PortalException
     */
    String getPermaLink(PortalControllerContext portalControllerContext, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)
            throws PortalException;


    /**
     * Get start page URL.
     *
     * @param portalControllerContext portal controller context
     * @param parentName parent page name
     * @param pageName page name
     * @param templateName template name
     * @param props page properties
     * @param params page parameters
     * @return start page URL
     * @throws PortalException
     */
    String getStartPageUrl(PortalControllerContext portalControllerContext, String parentName, String pageName, String templateName, Map<String, String> props,
            Map<String, String> params) throws PortalException;


    /**
     * Get start page URL.
     *
     * @param portalControllerContext portal controller context
     * @param pageName page name
     * @param templateName template name
     * @param props page properties
     * @param params page parameters
     * @return start page URL
     * @throws PortalException
     */
    String getStartPageUrl(PortalControllerContext portalControllerContext, String pageName, String templateName, Map<String, String> props,
            Map<String, String> params) throws PortalException;


    /**
     * Get destroy page URL.
     *
     * @param portalControllerContext portal controller context
     * @param parentId parent identifier
     * @param pageId page identifier
     * @return destroy page URL
     * @deprecated remove useless attribute parentId
     */
    @Deprecated
    String getDestroyPageUrl(PortalControllerContext portalControllerContext, String parentId, String pageId);


    /**
     * Get destroy page URL.
     *
     * @param portalControllerContext portal controller context
     * @param pageId page identifier
     * @return URL
     */
    String getDestroyPageUrl(PortalControllerContext portalControllerContext, String pageId);


    /**
     * Get destroy page URL.
     *
     * @param portalControllerContext portal controller context
     * @param pageId page identifier
     * @param closeChildren close children indicator
     * @return URL
     */
    String getDestroyPageUrl(PortalControllerContext portalControllerContext, String pageId, boolean closeChildren);


    /**
     * Get destroy current page URL.
     *
     * @param portalControllerContext portal controller context
     * @return URL
     */
    String getDestroyCurrentPageUrl(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Get destroy current page URL.
     *
     * @param portalControllerContext portal controller context
     * @param redirectionUrl redirection URL
     * @return URL
     */
    String getDestroyCurrentPageUrl(PortalControllerContext portalControllerContext, String redirectionUrl) throws PortalException;


    /**
     * Get destroy current page URL.
     * 
     * @param portalControllerContext portal controller context
     * @param redirectionUrl redirection URL
     * @param closeChildren close children indicator
     * @return URL
     * @throws PortalException
     */
    String getDestroyCurrentPageUrl(PortalControllerContext portalControllerContext, String redirectionUrl, boolean closeChildren) throws PortalException;


    /**
     * Adapt portal URL to navigation.
     *
     * @param portalControllerContext portal controller context
     * @param originalUrl original URL
     * @return navigation URL
     * @throws PortalException
     */
    String adaptPortalUrlToNavigation(PortalControllerContext portalControllerContext, String originalUrl) throws PortalException;


    /**
     * Adapt portal URL to popup.
     *
     * @param portalControllerContext portal controller context
     * @param originalUrl original URL
     * @param adapter adapter status code
     * @return popup URL
     */
    String adaptPortalUrlToPopup(PortalControllerContext portalControllerContext, String originalUrl, int adapter);


    /**
     * Get start portlet URL.
     *
     * @param portalControllerContext portal controller context
     * @param portletInstance portlet instance
     * @param windowProperties window properties
     * @param popup popup indicator
     * @return start portlet URL
     * @throws PortalException
     * @deprecated see
     */
    @Deprecated
    String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties, boolean popup)
            throws PortalException;


    /**
     * Get start portlet URL.
     *
     * @param portalControllerContext portal controller context
     * @param portletInstance portlet instance
     * @param windowProperties window properties
     * @return URL
     * @throws PortalException
     */
    String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties)
            throws PortalException;


    /**
     * Get start portlet URL.
     *
     * @param portalControllerContext portal controller context
     * @param portletInstance portlet instance
     * @param windowProperties window properties
     * @param type portal URL type
     * @return URL
     * @throws PortalException
     */
    String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties, PortalUrlType type)
            throws PortalException;


    /**
     * Gets the start portlet URL in new page.
     *
     * @param portalCtx the portal ctx
     * @param pageName the page name (assumes the unicity of the page)
     * @param pageDisplayName the page display name
     * @param portletInstance the portlet instance
     * @param windowProperties the window properties
     * @param windowParams the window parameters
     * @return the start portlet in new page
     */
    public String getStartPortletInNewPage(PortalControllerContext portalCtx, String pageName, String pageDisplayName, String portletInstance,
            Map<String, String> windowProperties, Map<String, String> windowParams) throws PortalException;

    /**
     * Get start portlet in region URL.
     *
     * @param portalControllerContext portal controller context
     * @param pageId page identifier
     * @param portletInstance portlet instance
     * @param region target region name
     * @param windowName window name
     * @param props window properties
     * @param params window parameters
     * @return start portlet URL
     */
    String getStartPortletInRegionUrl(PortalControllerContext portalControllerContext, String pageId, String portletInstance, String region, String windowName,
            Map<String, String> props, Map<String, String> params);


    /**
     * Get stop portlet URL.
     *
     * @param portalControllerContext portal controller context
     * @param pageId page identifier
     * @param windowId window identifier
     * @return stop portlet URL
     */
    String getStopPortletUrl(PortalControllerContext portalControllerContext, String pageId, String windowId);


    /**
     * Get base portal URL.
     *
     * @param portalControllerContext portal controller context
     * @return base portal URL
     */
    String getBasePortalUrl(PortalControllerContext portalControllerContext);


    /**
     * Get refresh page URL.
     *
     * @param portalControllerContext portal controller context
     * @return refresh page URL
     */
    String getRefreshPageUrl(PortalControllerContext portalControllerContext);


    /**
     * Get refresh page URL and notify creation of new content.
     *
     * @param portalControllerContext portal controller context
     * @param newContentNotify set to true for enable notifications after this command
     * @return refresh page URL
     */
    String getRefreshPageUrl(PortalControllerContext portalControllerContext, boolean newContentNotify);

    /**
     * Get put document in trash URL.
     *
     * @param portalControllerContext portal controller context
     * @param docId document identifier
     * @param docPath document path
     * @return put document in trash URL
     */
    String getPutDocumentInTrashUrl(PortalControllerContext portalControllerContext, String docId, String docPath);


    /**
     * Get HTTP error page URL.
     *
     * @param portalControllerContext portal controller context
     * @param httpErrorCode HTTP error code (example : 404)
     * @return HTTP error page URL
     */
    String getHttpErrorUrl(PortalControllerContext portalControllerContext, int httpErrorCode);


    /**
     * Get an ECM URL (for front office views)
     *
     * @param pcc portal controller context
     * @param command the name of the command (create document, view, ...) managed by the ecm
     * @param path path of the document
     * @param requestParameters params added in the http url
     * @return the url
     */
    String getEcmUrl(PortalControllerContext pcc, EcmViews command, String path, Map<String, String> requestParameters) throws PortalException;


    /**
     * Get back URL.
     *
     * @param portalControllerContext portal controller context
     * @param mobile mobile indicator
     * @return back URL
     */
    String getBackURL(PortalControllerContext portalControllerContext, boolean mobile);


    /**
     * Get parameterized URL.
     *
     * @param portalControllerContext portal controller context
     * @param cmsPath CMS path
     * @param template template, may be null
     * @param renderset renderset, may be null
     * @param layoutState layout state, may be null
     * @param permalinks permalinks indicator, may be null
     * @return parameterized URL
     */
    String getParameterizedURL(PortalControllerContext portalControllerContext, String cmsPath, String template, String renderset, String layoutState,
            Boolean permalinks);

    /**
     * Return an url who fire the ECM
     *
     * @param portalControllerContext
     * @param path
     * @param commandName
     * @return the url
     */
    String getEcmCommandUrl(PortalControllerContext portalControllerContext, String path, EcmCommonCommands commandName) throws PortalException;

    /**
     * Return an url who fire the ECM
     *
     * @param portalControllerContext
     * @param path
     * @param redirectionPath
     * @param commandName
     * @return the url
     */
    String getEcmCommandUrl(PortalControllerContext portalControllerContext, String path, EcmCommonCommands commandName, String redirectionPath)
            throws PortalException;

    /**
     * Return an url who fire the ECM
     *
     * @param portalControllerContext
     * @param path
     * @param param redirectionPath
     * @param commandName
     * @return the url
     */
    String getEcmCommandUrl(PortalControllerContext portalControllerContext, String path, String commandName, String redirectionPath) throws PortalException;


    /**
     * Get home page URL.
     *
     * @param portalControllerContext portal controller context
     * @param refresh refresh page indicator
     * @return URL
     * @throws PortalException
     */
    String getHomePageUrl(PortalControllerContext portalControllerContext, boolean refresh) throws PortalException;

}
