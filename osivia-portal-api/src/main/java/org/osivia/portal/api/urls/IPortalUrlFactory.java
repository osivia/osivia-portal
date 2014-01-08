package org.osivia.portal.api.urls;

import java.util.Map;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.context.PortalControllerContext;


public interface IPortalUrlFactory {


    public static String CONTEXTUALIZATION_PORTLET = "portlet";
    public static String CONTEXTUALIZATION_PAGE = "page";
    public static String CONTEXTUALIZATION_PORTAL = "portal";

    public static final String PERM_LINK_TYPE_PAGE = "page";
    public static final String PERM_LINK_TYPE_RSS = "rss";
    public static final String PERM_LINK_TYPE_RSS_PICTURE = "rsspicture";
    public static final String PERM_LINK_TYPE_CMS = "cms";

    public static final int POPUP_URL_ADAPTER_OPEN = 0;
    public static final int POPUP_URL_ADAPTER_CLOSE = 1;
    public static final int POPUP_URL_ADAPTER_CLOSED_NOTIFICATION = 2;
    
    public static final String DISPLAYCTX_REFRESH = "refreshPageAndNavigation";


    /* Portal level API */


    // TODO : à déplacer dans le cms
    public Page getPortalCMSContextualizedPage(PortalControllerContext ctx, String path) throws Exception;

    public String getCMSUrl(PortalControllerContext ctx, String pagePath, String cmsPath, Map<String, String> pageParams, String contextualization,
            String displayContext, String hideMetaDatas, String scope, String displayLiveVersion, String windowPermReference);

 
    public String getPermaLink(PortalControllerContext ctx, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)
            throws Exception;

    public String getStartPageUrl(PortalControllerContext ctx, String parentName, String pageName, String templateName, Map<String, String> props,
            Map<String, String> params) throws Exception;

    public String getStartPageUrl(PortalControllerContext ctx, String pageName, String templateName, Map<String, String> props, Map<String, String> params)
            throws Exception;


    public String getDestroyPageUrl(PortalControllerContext ctx, String parentId, String pageId);

    public String adaptPortalUrlToNavigation(PortalControllerContext ctx, String orginalUrl) throws Exception;

    public String adaptPortalUrlToPopup(PortalControllerContext ctx, String orginalUrl, int adapter) throws Exception;

    public String getStartPortletUrl(PortalControllerContext ctx, String portletInstance, Map<String, String> windowProperties, Map<String, String> params, boolean popup)
            throws Exception;
    
    public String getStartPortletInRegionUrl(PortalControllerContext ctx, String pageId, String portletInstance, String region, String windowName,
            Map<String, String> props, Map<String, String> params);

    public String getStopPortletUrl(PortalControllerContext ctx, String pageId, String windowId);

    /**
     * Return the current root portal url. e.g. : http://mydomain.com:8080/portal 
     */
    public String getBasePortalUrl(ServerInvocation invocation);
    
    
    public String getRefreshPageUrl(PortalControllerContext ctx) ;
    
    public String getPutDocumentInTrashUrl(PortalControllerContext ctx, String docId, String docPath);
}
