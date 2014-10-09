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
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.EcmCommand;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.assistantpage.ToggleSynchronizationCommand;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSPutDocumentInTrashCommand;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
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
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
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
    /** CMS service locator. */
    private static ICMSServiceLocator cmsServiceLocator;


    /**
     * Default constructor.
     */
    public PortalUrlFactory() {
        super();
    }


    /**
     * Static access to CMS service.
     *
     * @return CMS service
     */
    private static ICMSService getCMSService() {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }
        return cmsServiceLocator.getCMSService();
    }


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
    public Page getPortalCMSContextualizedPage(PortalControllerContext ctx, String path) throws PortalException {

        try {
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
        } catch (Exception e) {
            throw new PortalException(e);
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getCMSUrl(PortalControllerContext ctx, String pagePath, String cmsPath, Map<String, String> pageParams, String contextualization,
            String displayContext, String hideMetaDatas, String scope, String displayLiveVersion, String windowPermReference) {
        String portalPersistentName = null;

        boolean popup = false;
        if (ctx.getControllerCtx() != null) {
            String portalName = (String) ControllerContextAdapter.getControllerContext(ctx).getAttribute(Scope.REQUEST_SCOPE, "osivia.currentPortalName");

            Portal defaultPortal = ControllerContextAdapter.getControllerContext(ctx).getController().getPortalObjectContainer().getContext()
                    .getDefaultPortal();

            if (!defaultPortal.getName().equals(portalName)) {
                if (!StringUtils.equals(portalName, "osivia-util")) {
                    portalPersistentName = portalName;
                }   else    {
                    popup = true;
                    portalPersistentName = defaultPortal.getName();
                    pagePath = defaultPortal.getDefaultPage().getId().toString(PortalObjectPath.CANONICAL_FORMAT);
                }
            }
        }

        ControllerCommand cmd = new CmsCommand(pagePath, cmsPath, pageParams, contextualization, displayContext, hideMetaDatas, scope, displayLiveVersion,
                windowPermReference, this.addToBreadcrumb(ctx.getRequest()), portalPersistentName);
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        String url = portalURL.toString();
        if( popup)  {
            url= adaptPortalUrlToPopup(ctx, url, IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE);

        }
        
        

        return url;
    }

    /**
     * {@inheritDoc}
     */
    public String getEcmUrl(PortalControllerContext pcc, EcmCommand command, String path, Map<String, String> requestParameters) throws PortalException {

        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setControllerContext((ControllerContext) pcc.getControllerCtx());
        String ret = "";
        try {
            ret = getCMSService().getEcmUrl(cmsCtx, command, path, requestParameters);
        } catch (CMSException e) {
            throw new PortalException(e);
        }


        return ret;

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
            throws PortalException {

        try {
            String templateInstanciationParentId = null;
            String portalPersistentName = null;

            // Extract current portal
            if (ctx.getControllerCtx() != null) {
                String portalName = (String) ControllerContextAdapter.getControllerContext(ctx).getAttribute(Scope.REQUEST_SCOPE, "osivia.currentPortalName");

                Portal defaultPortal = ControllerContextAdapter.getControllerContext(ctx).getController().getPortalObjectContainer().getContext()
                        .getDefaultPortal();

                if (!defaultPortal.getName().equals(portalName)) {
                    if (!StringUtils.equals(portalName, "osivia-util"))
                        portalPersistentName = portalName;
                }
            }

            // Direct CMS Link : use CMSCommand
            if (IPortalUrlFactory.PERM_LINK_TYPE_CMS.equals(permLinkType)) {

                CmsCommand cmsCommand = new CmsCommand(null, cmsPath, params, null, null, null, null, null, null, null, portalPersistentName);

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

        } catch (Exception e) {
            throw new PortalException(e);
        }
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
    public String adaptPortalUrlToPopup(PortalControllerContext portalCtx, String originalUrl, int popupAdapter) {
        
        
        
        // Controller context
        ControllerContext controllerContext = (ControllerContext) portalCtx.getControllerCtx();
        // Server context
        ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();
        // Portal context path
        String portalContextPath = serverContext.getPortalContextPath();


        String prefix = StringUtils.substringBefore(originalUrl, portalContextPath);
        String suffix = StringUtils.substringAfter(originalUrl, portalContextPath);
        
        if (popupAdapter == IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE) 
            if( StringUtils.startsWith(suffix, PortalCommandFactory.POPUP_CLOSE_PATH ))
                return originalUrl;

        boolean auth = false;
        if (suffix.startsWith("/auth/")) {
            suffix = StringUtils.removeStart(suffix, "/auth/");
            auth = true;
        } else {
            suffix = StringUtils.removeStart(suffix, "/");
        }

        // Popup command
        String popupCommand = null;
        if (popupAdapter == IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE) {
            popupCommand = PortalCommandFactory.POPUP_CLOSE_PATH;
        } else if (popupAdapter == IPortalUrlFactory.POPUP_URL_ADAPTER_OPEN) {
            popupCommand = PortalCommandFactory.POPUP_OPEN_PATH;
        } else if (popupAdapter == IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSED_NOTIFICATION) {
            popupCommand = PortalCommandFactory.POPUP_CLOSED_PATH;
        }

        // URL
        StringBuilder url = new StringBuilder();
        url.append(prefix);
        url.append(portalContextPath);
        if (auth) {
            url.append("/auth");
        }
        if (popupCommand != null) {
            url.append(popupCommand);
        }
        url.append(suffix);

        return url.toString();
    }


    /**
     * {@inheritDoc}
     */
    public String getStartPageUrl(PortalControllerContext ctx, String parentName, String pageName, String templateName, Map<String, String> props,
            Map<String, String> params) throws PortalException {

        try {
            ControllerCommand cmd = new StartDynamicPageCommand();
            PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

            String parentId = URLEncoder.encode(PortalObjectId.parse(parentName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT),
                    "UTF-8");
            String templateId = URLEncoder.encode(PortalObjectId.parse(templateName, PortalObjectPath.CANONICAL_FORMAT)
                    .toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

            String url = portalURL.toString();
            url += "&parentId=" + parentId + "&pageName=" + pageName + "&templateId=" + templateId + "&props="
                    + WindowPropertiesEncoder.encodeProperties(props) + "&params=" + WindowPropertiesEncoder.encodeProperties(params);
            return url;
        } catch (Exception e) {
            throw new PortalException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getStartPageUrl(PortalControllerContext ctx, String pageName, String templateName, Map<String, String> props, Map<String, String> params)
            throws PortalException {
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
            Map<String, String> params, boolean popup) throws PortalException {

        try {
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

        } catch (Exception e) {
            throw new PortalException(e);
        }
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

    	String backPageMarker = null;


        Window window = (Window) ctx.getRequest().getAttribute("osivia.window");
        if (window != null) {
        	// Deleted doc is equals to CMS doc
        	// Navigation is managed by portal (not inside a portlet)
        	// So go back to previous state

            EditionState curState = (EditionState) ctx.getRequest().getAttribute("osivia.editionState");

                // Deleted doc is equals to CMS doc
                // Navigation is managed by portal (not inside a portlet)
                // So go back to previous state
                if( curState != null && curState.getDocPath().equals(window.getDeclaredProperty(Constants.WINDOW_PROP_URI)))    {
                    backPageMarker = curState.getBackPageMarker();

            }

        }


        ControllerCommand cmd = new CMSPutDocumentInTrashCommand(docId, docPath, backPageMarker);
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        return portalURL.toString();
    }


    /**
     * {@inheritDoc}
     */
    public String getSynchronizationCommandUrl(PortalControllerContext ctx, String cmsPath, Boolean enableParameter) {

        ControllerCommand cmd = new ToggleSynchronizationCommand(cmsPath, enableParameter);
        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        return portalURL.toString();
    }


    /**
     * {@inheritDoc}
     */
    public String getBackUrl(PortalControllerContext ctx, boolean refresh) {

        String backPageMarker = null;

        EditionState curState = (EditionState) ctx.getRequest().getAttribute("osivia.editionState");


        backPageMarker = curState.getBackPageMarker();


        String backURL = null;

        if (backPageMarker != null) {
            ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(ctx);

            PageMarkerInfo infos = PageMarkerUtils.getPageMarkerInfo(controllerContext, backPageMarker);
            if (infos != null) {
                PortalObjectId pageId = infos.getPageId();

                URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();

                if (refresh) {
                    RefreshPageCommand resfreshCmd = new RefreshPageCommand(pageId.toString(PortalObjectPath.SAFEST_FORMAT));
                    backURL = controllerContext.renderURL(resfreshCmd, urlContext, URLFormat.newInstance(false, true));
                } else {
                    ViewPageCommand rpc = new ViewPageCommand(pageId);
                    backURL = controllerContext.renderURL(rpc, urlContext, URLFormat.newInstance(false, true));
                 }

                backURL = backURL.replaceAll("/pagemarker/([0-9]*)/", "/pagemarker/" + backPageMarker + "/");

            }
        }

        return backURL;

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
