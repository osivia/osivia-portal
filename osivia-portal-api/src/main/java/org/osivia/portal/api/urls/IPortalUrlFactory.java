package org.osivia.portal.api.urls;

import java.util.Map;

import org.jboss.portal.core.model.portal.Page;
import org.osivia.portal.api.context.PortalControllerContext;

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

    /** Popup URL adapter open status. */
    int POPUP_URL_ADAPTER_OPEN = 0;
    /** Popup URL adapter close status. */
    int POPUP_URL_ADAPTER_CLOSE = 1;
    /** Popup URL adapter closed notification status. */
    int POPUP_URL_ADAPTER_CLOSED_NOTIFICATION = 2;

    /** Display context refresh. */
    String DISPLAYCTX_REFRESH = "refreshPageAndNavigation";


    /**
     * Get portal CMS contextualized page.
     * TODO: move it in CMS.
     * 
     * @param portalControllerContext portal controller context
     * @param path path
     * @return contextualized page
     * @throws Exception
     */
    Page getPortalCMSContextualizedPage(PortalControllerContext portalControllerContext, String path) throws Exception;


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
    String getCMSUrl(PortalControllerContext portalControllerContext, String pagePath, String cmsPath, Map<String, String> pageParams,
            String contextualization, String displayContext, String hideMetaDatas, String scope, String displayLiveVersion, String windowPermReference);


    /**
     * Get permalink URL.
     * 
     * @param portalControllerContext portal controller context
     * @param permLinkRef permalink reference
     * @param params parameters
     * @param cmsPath CMS path
     * @param permLinkType permalink type
     * @return permalink URL
     * @throws Exception
     */
    String getPermaLink(PortalControllerContext portalControllerContext, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)
            throws Exception;


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
     * @throws Exception
     */
    String getStartPageUrl(PortalControllerContext portalControllerContext, String parentName, String pageName, String templateName, Map<String, String> props,
            Map<String, String> params) throws Exception;


    /**
     * Get start page URL.
     * 
     * @param portalControllerContext portal controller context
     * @param pageName page name
     * @param templateName template name
     * @param props page properties
     * @param params page parameters
     * @return start page URL
     * @throws Exception
     */
    String getStartPageUrl(PortalControllerContext portalControllerContext, String pageName, String templateName, Map<String, String> props,
            Map<String, String> params) throws Exception;


    /**
     * Get destroy page URL.
     * 
     * @param portalControllerContext portal controller context
     * @param parentId parent identifier
     * @param pageId page identifier
     * @return destroy page URL
     */
    String getDestroyPageUrl(PortalControllerContext portalControllerContext, String parentId, String pageId);


    /**
     * Adapt portal URL to navigation.
     * 
     * @param portalControllerContext portal controller context
     * @param orginalUrl original URL
     * @return navigation URL
     * @throws Exception
     */
    String adaptPortalUrlToNavigation(PortalControllerContext portalControllerContext, String orginalUrl) throws Exception;


    /**
     * Adapt portal URL to popup.
     * 
     * @param portalControllerContext portal controller context
     * @param orginalUrl original URL
     * @param adapter adapter status code
     * @return popup URL
     */
    String adaptPortalUrlToPopup(PortalControllerContext portalControllerContext, String orginalUrl, int adapter);


    /**
     * Get start portlet URL.
     * 
     * @param portalControllerContext portal controller context
     * @param portletInstance portlet instance
     * @param windowProperties window properties
     * @param params window parameters
     * @param popup popup indicator
     * @return start portlet URL
     * @throws Exception
     */
    String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties,
            Map<String, String> params,
            boolean popup) throws Exception;


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

}
