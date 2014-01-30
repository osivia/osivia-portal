package org.osivia.portal.core.urls;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSPutDocumentInTrashCommand;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.dynamic.StopDynamicPageCommand;
import org.osivia.portal.core.dynamic.StopDynamicWindowCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.pagemarker.PortalCommandFactory;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.utils.URLUtils;

/**
 * Portal URL factory implementation.
 * 
 * @author Jean-Sébastien Steux
 * @see IPortalUrlFactory
 */
public class PortalUrlFactory implements IPortalUrlFactory {

    /** Tracker. */
    private ITracker tracker;
    /** Profile manager. */
    private IProfilManager profilManager;


    /**
     * Utility method used to compute add to breadcrumb indicator.
     * 
     * @param request portlet request
     * @return add to breadcrumb indicator
     */
    private String addToBreadcrumb(PortletRequest request) {
        if (request == null) {
            return null;
            // Pas dans un context portlet (appel depuis pagecustomizer), pas de breadcrumb
            // return "0";
        }

        // On regarde si on est dans une window MAXIMIZED
        ControllerContext ctx = (ControllerContext) request.getAttribute("osivia.controller");
        Window window = (Window) request.getAttribute("osivia.window");

        String addToBreadcrumb = "0";

        if ((window != null) && (ctx != null)) {
            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

            WindowNavigationalState windowNavState = (WindowNavigationalState) ctx.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);

            // On regarde si la fenêtre est en vue MAXIMIZED
            if ((windowNavState != null) && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {
                addToBreadcrumb = "1";
            }
        }

        return addToBreadcrumb;
    }


    /**
     * {@inheritDoc}
     */
    public Page getPortalCMSContextualizedPage(PortalControllerContext ctx, String path) throws Exception {
        Window window = (Window) ctx.getRequest().getAttribute("osivia.window");
        if (window != null) {
            Page page = window.getPage();
            // contenu deja contextualise dans la page courante
            if (CmsCommand.isContentAlreadyContextualizedInPage(ControllerContextAdapter.getControllerContext(ctx), page, path)) {
                return page;
            }
        }

        Portal portal = ControllerContextAdapter.getControllerContext(ctx).getController().getPortalObjectContainer().getContext().getDefaultPortal();

        // dans d'autres pages du portail
        PortalObject page = CmsCommand.searchPublicationPage(ControllerContextAdapter.getControllerContext(ctx), portal, path, this.profilManager);
        if (page != null) {
            return (Page) page;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getCMSUrl(PortalControllerContext ctx, String pagePath, String cmsPath, Map<String, String> pageParams, String contextualization,
            String displayContext, String hideMetaDatas, String scope, String displayLiveVersion, String windowPermReference) {
        String portalPersistentName = null;

        if (ctx.getControllerCtx() != null) {
            String portalName = (String) ControllerContextAdapter.getControllerContext(ctx).getAttribute(Scope.REQUEST_SCOPE, "osivia.currentPortalName");

            Portal defaultPortal = ControllerContextAdapter.getControllerContext(ctx).getController().getPortalObjectContainer().getContext()
                    .getDefaultPortal();

            if (!defaultPortal.getName().equals(portalName)) {
                portalPersistentName = portalName;
            }
        }

        ControllerCommand cmd = new CmsCommand(pagePath, cmsPath, pageParams, contextualization, displayContext, hideMetaDatas, scope, displayLiveVersion,
                windowPermReference, this.addToBreadcrumb(ctx.getRequest()), portalPersistentName);
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        return portalURL.toString();
    }


    /**
     * {@inheritDoc}
     */
    public String getStopPortletUrl(PortalControllerContext ctx, String pageId, String windowId) {
        ControllerCommand cmd = new StopDynamicWindowCommand();
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        String url = portalURL.toString();
        url += "&pageId=" + pageId + "&windowId=" + windowId;
        return url;
    }


    /**
     * {@inheritDoc}
     */
    public String getStartPortletInRegionUrl(PortalControllerContext ctx, String pageId, String portletInstance, String region, String windowName,
            Map<String, String> props, Map<String, String> params) {
        ControllerCommand cmd = new StartDynamicWindowCommand();
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        String url = portalURL.toString();
        url += "&pageId=" + pageId + "&regionId=" + region + "&instanceId=" + portletInstance + "&windowName=" + windowName + "&props="
                + WindowPropertiesEncoder.encodeProperties(props) + "&params=" + WindowPropertiesEncoder.encodeProperties(params) + "&addToBreadcrumb="
                + this.addToBreadcrumb(ctx.getRequest());
        return url;
    }


    /**
     * {@inheritDoc}
     */
    public String getPermaLink(PortalControllerContext ctx, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)
            throws Exception {
        String templateInstanciationParentId = null;
        String portalPersistentName = null;

        // Extract current portal
        if (ctx.getControllerCtx() != null) {
            String portalName = (String) ControllerContextAdapter.getControllerContext(ctx).getAttribute(Scope.REQUEST_SCOPE, "osivia.currentPortalName");

            Portal defaultPortal = ControllerContextAdapter.getControllerContext(ctx).getController().getPortalObjectContainer().getContext()
                    .getDefaultPortal();

            if (!defaultPortal.getName().equals(portalName)) {
                portalPersistentName = portalName;
            }
        }

        // Direct CMS Link : use CMSCommand
        if (IPortalUrlFactory.PERM_LINK_TYPE_CMS.equals(permLinkType)) {
            // CmsCommand cmsCommand = new CmsCommand(null, cmsPath, parameters, IPortalUrlFactory.CONTEXTUALIZATION_PORTAL, "permlink", null, null, null, null,
            // null, portalPersistentName);
            CmsCommand cmsCommand = new CmsCommand(null, cmsPath, null, null, null, null, null, null, null, null, portalPersistentName);

            // Remove default initialisation
            cmsCommand.setItemScope(null);

            cmsCommand.setInsertPageMarker(false);
            URLContext urlContext = ControllerContextAdapter.getControllerContext(ctx).getServerInvocation().getServerContext().getURLContext();
            urlContext = urlContext.withAuthenticated(false);
            String permLinkUrl = ControllerContextAdapter.getControllerContext(ctx).renderURL(cmsCommand, urlContext, URLFormat.newInstance(false, true));

            return permLinkUrl;
        }

        // Others permalink (Lists, RSS, ...) : use PermLinkCommand
        if (ctx.getRequest() != null) {
            Window window = (Window) ctx.getRequest().getAttribute("osivia.window");
            if (window != null) {
                Page page = window.getPage();

                if (page instanceof ITemplatePortalObject) {
                    templateInstanciationParentId = URLEncoder.encode(page.getParent().getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");
                }
            }
        }

        PermLinkCommand linkCmd = new PermLinkCommand(permLinkRef, params, templateInstanciationParentId, cmsPath, permLinkType, portalPersistentName);
        URLContext urlContext = ControllerContextAdapter.getControllerContext(ctx).getServerInvocation().getServerContext().getURLContext();

        urlContext = urlContext.withAuthenticated(false);

        return ControllerContextAdapter.getControllerContext(ctx).renderURL(linkCmd, urlContext, URLFormat.newInstance(false, true));
    }


    /**
     * {@inheritDoc}
     */
    public String adaptPortalUrlToNavigation(PortalControllerContext portalCtx, String orginalUrl) {
        // Pattern
        Pattern pattern = Pattern.compile("(https?://([^/:]*)(:[0-9]*)?)?/([^/]*)(/auth)?/(pagemarker/[0-9]+/)?(.*)");

        Matcher matcher = pattern.matcher(orginalUrl);
        if (matcher.matches()) {
            // Controller context
            ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalCtx);
            // Server context
            ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();
            // Client request
            HttpServletRequest request = serverContext.getClientRequest();
            // Context path
            String contextPath = serverContext.getPortalContextPath();
            contextPath = StringUtils.removeEnd(contextPath, "/auth");
            // Server name
            String serverName = request.getServerName();

            // Absolute or relative request indicator
            boolean absolute = (matcher.group(1) != null);

            // Check server name and context path
            if (absolute && !(serverName.equals(matcher.group(2)) && contextPath.substring(1).equals(matcher.group(4)))) {
                return orginalUrl;
            }

            // Current page marker
            String pageMarker = PageMarkerUtils.getCurrentPageMarker(controllerContext);

            // Transformed URL
            StringBuffer transformedUrl = new StringBuffer();
            if (absolute) {
                String baseUrl = URLUtils.createUrl(request);
                transformedUrl.append(baseUrl);
            }
            transformedUrl.append(contextPath);
            transformedUrl.append(StringUtils.trimToEmpty(matcher.group(5)));
            transformedUrl.append("/pagemarker/");
            transformedUrl.append(pageMarker);
            transformedUrl.append("/");
            transformedUrl.append(matcher.group(7));

            return transformedUrl.toString();
        }

        return orginalUrl;
    }


    /**
     * {@inheritDoc}
     */
    public String adaptPortalUrlToPopup(PortalControllerContext portalCtx, String originalUrl, int popupAdapter) throws Exception {
        String url = originalUrl;

        int insertIndex = originalUrl.indexOf(PageMarkerUtils.PAGE_MARKER_PATH);
        if (insertIndex == -1) {
            // Web command
            insertIndex = originalUrl.indexOf("/web/");
        }

        if (insertIndex != -1) {
            if (popupAdapter == IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE) {
                url = url.substring(0, insertIndex) + PortalCommandFactory.POPUP_CLOSE_PATH + url.substring(insertIndex + 1);
            } else if (popupAdapter == IPortalUrlFactory.POPUP_URL_ADAPTER_OPEN) {
                url = url.substring(0, insertIndex) + PortalCommandFactory.POPUP_OPEN_PATH + url.substring(insertIndex + 1);
            } else if (popupAdapter == IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSED_NOTIFICATION) {
                url = url.substring(0, insertIndex) + PortalCommandFactory.POPUP_CLOSED_PATH + url.substring(insertIndex + 1);
            }
        }

        return url;
    }


    /**
     * {@inheritDoc}
     */
    public String getStartPageUrl(PortalControllerContext ctx, String parentName, String pageName, String templateName, Map<String, String> props,
            Map<String, String> params) throws Exception {
        ControllerCommand cmd = new StartDynamicPageCommand();
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        String parentId = URLEncoder.encode(PortalObjectId.parse(parentName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT),
                "UTF-8");
        String templateId = URLEncoder.encode(PortalObjectId.parse(templateName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT),
                "UTF-8");

        String url = portalURL.toString();
        url += "&parentId=" + parentId + "&pageName=" + pageName + "&templateId=" + templateId + "&props=" + WindowPropertiesEncoder.encodeProperties(props)
                + "&params=" + WindowPropertiesEncoder.encodeProperties(params);
        return url;
    }


    /**
     * {@inheritDoc}
     */
    public String getStartPageUrl(PortalControllerContext ctx, String pageName, String templateName, Map<String, String> props, Map<String, String> params)
            throws Exception {
        String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);
        // if (portalName == null)
        // portalName = "default";

        portalName = "/" + portalName;

        return this.getStartPageUrl(ctx, portalName, pageName, templateName, props, params);
    }


    /**
     * {@inheritDoc}
     */
    public String getDestroyPageUrl(PortalControllerContext ctx, String parentId, String pageId) {
        ControllerCommand cmd = new StopDynamicPageCommand();
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        String url = portalURL.toString();
        url += "&parentId=" + parentId + "&pageId=" + pageId;
        return url;
    }


    /**
     * Utility method used to simplify calls to portlet within an other portlet.
     * 
     * @param portalControllerContext portal controller context
     * @param portletInstance portlet instance
     * @param windowProperties window properties
     * @param params window parameters
     * @return start portlet URL
     * @throws Exception
     */
    private String getStartPortletInPopupUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties,
            Map<String, String> params) throws Exception {
        ControllerCommand cmd = new StartDynamicWindowCommand();
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(portalControllerContext), null, null);

        String pageId = URLEncoder.encode(
                PortalObjectPath.parse("/osivia-util/popup", PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

        // Valeurs par défaut
        if (windowProperties.get("osivia.hideDecorators") == null) {
            windowProperties.put("osivia.hideDecorators", "1");
        }
        if (windowProperties.get("theme.dyna.partial_refresh_enabled") == null) {
            windowProperties.put("theme.dyna.partial_refresh_enabled", "false");
        }

        String url = portalURL.toString();
        url += "&pageId=" + pageId + "&regionId=popup&instanceId=" + portletInstance + "&windowName=popupWindow&props="
                + WindowPropertiesEncoder.encodeProperties(windowProperties) + "&params=" + WindowPropertiesEncoder.encodeProperties(params)
                + "&addToBreadcrumb=" + this.addToBreadcrumb(portalControllerContext.getRequest());

        return this.adaptPortalUrlToPopup(portalControllerContext, url, IPortalUrlFactory.POPUP_URL_ADAPTER_OPEN);
    }


    /**
     * {@inheritDoc}
     */
    public String getStartPortletUrl(PortalControllerContext portalCtx, String portletInstance, Map<String, String> windowProperties,
            Map<String, String> params, boolean popup) throws Exception {
        // Maps initialization
        if (windowProperties == null) {
            windowProperties = new HashMap<String, String>();
        }
        if (params == null) {
            params = new HashMap<String, String>();
        }

        if (popup) {
            return this.getStartPortletInPopupUrl(portalCtx, portletInstance, windowProperties, params);
        }

        String region = "virtual";
        String windowName = "dynamicPortlet";

        Window window = (Window) portalCtx.getRequest().getAttribute("osivia.window");
        Page page = window.getPage();
        String pageId = URLEncoder.encode(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

        ControllerCommand cmd = new StartDynamicWindowCommand();
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(portalCtx), null, null);

        // Valeurs par défaut
        if (windowProperties.get("osivia.hideDecorators") == null) {
            windowProperties.put("osivia.hideDecorators", "1");
        }
        if (windowProperties.get("theme.dyna.partial_refresh_enabled") == null) {
            windowProperties.put("theme.dyna.partial_refresh_enabled", "false");
        }

        String url = portalURL.toString();
        url += "&pageId=" + pageId + "&regionId=" + region + "&instanceId=" + portletInstance + "&windowName=" + windowName + "&props="
                + WindowPropertiesEncoder.encodeProperties(windowProperties) + "&params=" + WindowPropertiesEncoder.encodeProperties(params)
                + "&addToBreadcrumb=" + this.addToBreadcrumb(portalCtx.getRequest());
        return url;
    }


    /**
     * {@inheritDoc}
     */
    public String getBasePortalUrl(PortalControllerContext portalControllerContext) {
        HttpServletRequest request = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                .getClientRequest();
        return URLUtils.createUrl(request);
    }


    /**
     * {@inheritDoc}
     */
    public String getRefreshPageUrl(PortalControllerContext ctx) {
        PortalObjectId currentPageId = (PortalObjectId) ControllerContextAdapter.getControllerContext(ctx).getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                Constants.ATTR_PAGE_ID);

        URLContext urlContext = ControllerContextAdapter.getControllerContext(ctx).getServerInvocation().getServerContext().getURLContext();
        RefreshPageCommand resfreshCmd = new RefreshPageCommand(currentPageId.toString(PortalObjectPath.SAFEST_FORMAT));
        String resfreshUrl = ControllerContextAdapter.getControllerContext(ctx).renderURL(resfreshCmd, urlContext, URLFormat.newInstance(false, true));

        return resfreshUrl;
    }


    /**
     * {@inheritDoc}
     */
    public String getPutDocumentInTrashUrl(PortalControllerContext ctx, String docId, String docPath) {
        ControllerCommand cmd = new CMSPutDocumentInTrashCommand(docId, docPath);
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        return portalURL.toString();
    }


    /**
     * {@inheritDoc}
     */
    public String getHttpErrorUrl(PortalControllerContext portalControllerContext, int httpErrorCode) {
        HttpServletRequest request = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                .getClientRequest();
        String uri = System.getProperty("error.defaultPageUri");
        String url = URLUtils.createUrl(request, uri, null);
        return URLUtils.addParameter(url, "httpCode", String.valueOf(httpErrorCode));
    }


    /**
     * Getter for tracker.
     * 
     * @return the tracker
     */
    public ITracker getTracker() {
        return this.tracker;
    }

    /**
     * Setter for tracker.
     * 
     * @param tracker the tracker to set
     */
    public void setTracker(ITracker tracker) {
        this.tracker = tracker;
    }

    /**
     * Getter for profilManager.
     * 
     * @return the profilManager
     */
    public IProfilManager getProfilManager() {
        return this.profilManager;
    }

    /**
     * Setter for profilManager.
     * 
     * @param profilManager the profilManager to set
     */
    public void setProfilManager(IProfilManager profilManager) {
        this.profilManager = profilManager;
    }

}
