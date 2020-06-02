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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
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
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.ecm.EcmCommand;
import org.osivia.portal.api.ecm.EcmCommonCommands;
import org.osivia.portal.api.ecm.EcmViews;
import org.osivia.portal.api.ecm.IEcmCommandervice;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.PortalUrlType;
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
import org.osivia.portal.core.dynamic.StartDynamicWindowInNewPageCommand;
import org.osivia.portal.core.dynamic.StopDynamicPageCommand;
import org.osivia.portal.core.dynamic.StopDynamicWindowCommand;
import org.osivia.portal.core.ecm.EcmCommandDelegate;
import org.osivia.portal.core.page.*;
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.pagemarker.PortalCommandFactory;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.search.AdvancedSearchCommand;
import org.osivia.portal.core.share.ShareCommand;
import org.osivia.portal.core.sharing.link.LinkSharingCommand;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.utils.URLUtils;
import org.osivia.portal.core.web.IWebIdService;

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
        final ControllerContext ctx = (ControllerContext) request.getAttribute("osivia.controller");
        final Window window = (Window) request.getAttribute("osivia.window");

        String addToBreadcrumb = "0";

        if ((window != null) && (ctx != null)) {
            final NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

            final WindowNavigationalState windowNavState = (WindowNavigationalState) ctx.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);

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
    @Override
    public Page getPortalCMSContextualizedPage(PortalControllerContext ctx, String path) throws PortalException {

        try {
            final Window window = (Window) ctx.getRequest().getAttribute("osivia.window");
            if (window != null) {
                final Page page = window.getPage();
                // contenu deja contextualise dans la page courante
                if (CmsCommand.isContentAlreadyContextualizedInPage(ControllerContextAdapter.getControllerContext(ctx), page, path)) {
                    return page;
                }
            }

            final Portal portal = ControllerContextAdapter.getControllerContext(ctx).getController().getPortalObjectContainer().getContext().getDefaultPortal();

            // dans d'autres pages du portail
            final PortalObject page = CmsCommand.searchPublicationPage(ControllerContextAdapter.getControllerContext(ctx), portal, path, this.profilManager);
            if (page != null) {
                return (Page) page;
            }
        } catch (final Exception e) {
            throw new PortalException(e);
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getCMSUrl(PortalControllerContext portalControllerContext, String pagePath, String cmsPath, Map<String, String> pageParams,
            String contextualization, String displayContext, String hideMetaDatas, String scope, String displayLiveVersion, String windowPermReference) {

        String portalPersistentName = null;

        boolean popup = false;
        if (portalControllerContext.getControllerCtx() != null) {
            final String portalName = (String) ControllerContextAdapter.getControllerContext(portalControllerContext).getAttribute(Scope.REQUEST_SCOPE,
                    "osivia.currentPortalName");

            final Portal defaultPortal = ControllerContextAdapter.getControllerContext(portalControllerContext).getController().getPortalObjectContainer()
                    .getContext().getDefaultPortal();

            if (!defaultPortal.getName().equals(portalName)) {
                if (!StringUtils.equals(portalName, "osivia-util")) {
                    portalPersistentName = portalName;
                } else {
                    popup = true;
                    portalPersistentName = defaultPortal.getName();
                    pagePath = defaultPortal.getDefaultPage().getId().toString(PortalObjectPath.CANONICAL_FORMAT);
                }
            }
        }

        final CmsCommand cmd = new CmsCommand(pagePath, cmsPath, pageParams, contextualization, displayContext, hideMetaDatas, scope, displayLiveVersion,
                windowPermReference, this.addToBreadcrumb(portalControllerContext.getRequest()), portalPersistentName);


        final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(portalControllerContext), null, null);

        String url = portalURL.toString();
        if (popup) {
            url = this.adaptPortalUrlToPopup(portalControllerContext, url, IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE);

        }

        return url;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEcmUrl(PortalControllerContext pcc, EcmViews command, String path, Map<String, String> requestParameters) throws PortalException {

        final CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setControllerContext((ControllerContext) pcc.getControllerCtx());
        String ret = "";
        try {
            ret = getCMSService().getEcmUrl(cmsCtx, command, path, requestParameters);
        } catch (final CMSException e) {
            throw new PortalException(e);
        }


        return ret;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStopPortletUrl(PortalControllerContext ctx, String pageId, String windowId) {
        final ControllerCommand cmd = new StopDynamicWindowCommand();
        final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        String url = portalURL.toString();
        url += "&pageId=" + pageId + "&windowId=" + windowId;
        return url;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartPortletInRegionUrl(PortalControllerContext ctx, String pageId, String portletInstance, String region, String windowName,
            Map<String, String> props, Map<String, String> params) {
        final ControllerCommand cmd = new StartDynamicWindowCommand();
        final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        String url = portalURL.toString();
        url += "&pageId=" + pageId + "&regionId=" + region + "&instanceId=" + portletInstance + "&windowName=" + windowName + "&props="
                + WindowPropertiesEncoder.encodeProperties(props) + "&params=" + WindowPropertiesEncoder.encodeProperties(params) + "&addToBreadcrumb="
                + this.addToBreadcrumb(ctx.getRequest());
        return url;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermaLink(PortalControllerContext portalControllerContext, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)
            throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        try {
            String templateInstanciationParentId = null;
            // Portal persistent name
            String portalPersistentName = this.getPortalPersistentName(portalControllerContext);

            // Direct CMS Link : use CMSCommand
            if (IPortalUrlFactory.PERM_LINK_TYPE_CMS.equals(permLinkType)) {

                final CmsCommand cmsCommand = new CmsCommand(null, cmsPath, params, null, null, null, null, null, null, null, portalPersistentName);

                // Remove default initialisation
                cmsCommand.setItemScope(null);

                cmsCommand.setInsertPageMarker(false);

                URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
                urlContext = urlContext.withAuthenticated(false);
                final String permLinkUrl = controllerContext.renderURL(cmsCommand, urlContext,
                        URLFormat.newInstance(false, true));

                return permLinkUrl;

            } else if (IPortalUrlFactory.PERM_LINK_TYPE_SHARE.equals(permLinkType)) {
                // WebId
                String webId = StringUtils.substringAfter(cmsPath, IWebIdService.CMS_PATH_PREFIX);
                // Parent webId (for remote proxy only)
                String parentWebId;
                if (StringUtils.contains(webId, IWebIdService.RPXY_WID_MARKER)) {
                    String[] splittedWebId = StringUtils.splitByWholeSeparator(webId, IWebIdService.RPXY_WID_MARKER, 2);
                    webId = splittedWebId[0];
                    parentWebId = splittedWebId[1];
                } else {
                    parentWebId = null;
                }

                // Share command
                ShareCommand shareCommand = new ShareCommand(webId);
                shareCommand.setParentWebId(parentWebId);
                shareCommand.setParameters(params);

                // URL context
                URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
                urlContext = urlContext.withAuthenticated(false);

                return controllerContext.renderURL(shareCommand, urlContext, URLFormat.newInstance(false, true));
            }

            if (!IPortalUrlFactory.PERM_LINK_TYPE_PORTLET_RESOURCE.equals(permLinkType)) {
                // Others permalink (Lists, RSS, ...) : use PermLinkCommand
                if (portalControllerContext.getRequest() != null) {
                    final Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");
                    if (window != null) {
                        final Page page = window.getPage();

                        if (page instanceof ITemplatePortalObject) {
                            templateInstanciationParentId = URLEncoder.encode(page.getParent().getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");
                        }
                    }
                }
            }

            // Permalink command
            PermLinkCommand linkCmd = new PermLinkCommand(permLinkRef, params, templateInstanciationParentId, cmsPath, permLinkType,
                    portalPersistentName);

            // URL context
            URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
            urlContext = urlContext.withAuthenticated(false);

            return controllerContext.renderURL(linkCmd, urlContext, URLFormat.newInstance(false, true));
        } catch (Exception e) {
            throw new PortalException(e);
        }
    }


    /**
     * @param portalControllerContext
     * @return the portal persistent name.
     */
    private String getPortalPersistentName(PortalControllerContext portalControllerContext) {
        String portalPersistentName = null;

        // Extract current portal
        if (portalControllerContext.getControllerCtx() != null) {
            final String portalName = (String) ControllerContextAdapter.getControllerContext(portalControllerContext).getAttribute(Scope.REQUEST_SCOPE,
                    "osivia.currentPortalName");

            final Portal defaultPortal = ControllerContextAdapter.getControllerContext(portalControllerContext).getController().getPortalObjectContainer()
                    .getContext().getDefaultPortal();

            if (!defaultPortal.getName().equals(portalName)) {
                if (!StringUtils.equals(portalName, "osivia-util")) {
                    portalPersistentName = portalName;
                }
            }
        }
        return portalPersistentName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String adaptPortalUrlToNavigation(PortalControllerContext portalCtx, String orginalUrl) {
        // Pattern
        final Pattern pattern = Pattern.compile("(https?://([^/:]*)(:[0-9]*)?)?/([^/]*)(/auth)?/(pagemarker/[0-9]+/)?(.*)");

        final Matcher matcher = pattern.matcher(orginalUrl);
        if (matcher.matches()) {
            // Controller context
            final ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalCtx);
            // Server context
            final ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();
            // Client request
            final HttpServletRequest request = serverContext.getClientRequest();
            // Context path
            String contextPath = serverContext.getPortalContextPath();
            contextPath = StringUtils.removeEnd(contextPath, "/auth");
            // Server name
            final String serverName = request.getServerName();

            // Absolute or relative request indicator
            final boolean absolute = (matcher.group(1) != null);

            // Check server name and context path
            if (absolute && !(serverName.equals(matcher.group(2)) && contextPath.substring(1).equals(matcher.group(4)))) {
                return orginalUrl;
            }

            // Current page marker
            final String pageMarker = PageMarkerUtils.getCurrentPageMarker(controllerContext);

            // Transformed URL
            final StringBuffer transformedUrl = new StringBuffer();
            if (absolute) {
                final String baseUrl = URLUtils.createUrl(request);
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
    @Override
    public String adaptPortalUrlToPopup(PortalControllerContext portalCtx, String originalUrl, int popupAdapter) {


        // Controller context
        final ControllerContext controllerContext = (ControllerContext) portalCtx.getControllerCtx();
        // Server context
        final ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();
        // Portal context path
        final String portalContextPath = serverContext.getPortalContextPath();


        final String prefix = StringUtils.substringBefore(originalUrl, portalContextPath);
        String suffix = StringUtils.substringAfter(originalUrl, portalContextPath);

        if (popupAdapter == IPortalUrlFactory.POPUP_URL_ADAPTER_CLOSE) {
            if (StringUtils.startsWith(suffix, PortalCommandFactory.POPUP_CLOSE_PATH)) {
                return originalUrl;
            }
        }

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
        final StringBuilder url = new StringBuilder();
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
    @Override
    public String getStartPageUrl(PortalControllerContext ctx, String parentName, String pageName, String templateName, Map<String, String> props,
            Map<String, String> params) throws PortalException {

        try {
            final ControllerCommand cmd = new StartDynamicPageCommand();
            final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

            final String parentId = URLEncoder
                    .encode(PortalObjectId.parse(parentName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");
            final String templateId = URLEncoder
                    .encode(PortalObjectId.parse(templateName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

            String url = portalURL.toString();
            url += "&parentId=" + parentId + "&pageName=" + pageName + "&templateId=" + templateId + "&props=" + WindowPropertiesEncoder.encodeProperties(props)
                    + "&params=" + WindowPropertiesEncoder.encodeProperties(params);
            return url;
        } catch (final Exception e) {
            throw new PortalException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    @Deprecated
    public String getDestroyPageUrl(PortalControllerContext portalControllerContext, String parentId, String pageId) {
        return this.getDestroyPageUrl(portalControllerContext, pageId);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDestroyPageUrl(PortalControllerContext portalControllerContext, String pageId) {
        return this.getDestroyPageUrl(portalControllerContext, pageId, false);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDestroyPageUrl(PortalControllerContext portalControllerContext, String pageId, boolean closeChildren) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // Controller command
        ControllerCommand command = new StopDynamicPageCommand();
        // Portal URL
        PortalURL portalUrl = new PortalURLImpl(command, controllerContext, null, null);

        // URL
        StringBuilder url = new StringBuilder();
        url.append(portalUrl.toString());
        url.append("&pageId=");
        url.append(pageId);
        if (closeChildren) {
            url.append("&closeChildren=");
            url.append(closeChildren);
        }

        return url.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDestroyCurrentPageUrl(PortalControllerContext portalControllerContext) throws PortalException {
        return this.getDestroyCurrentPageUrl(portalControllerContext, null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDestroyCurrentPageUrl(PortalControllerContext portalControllerContext, String redirectionUrl) throws PortalException {
        return this.getDestroyCurrentPageUrl(portalControllerContext, redirectionUrl, false);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDestroyCurrentPageUrl(PortalControllerContext portalControllerContext, String redirectionUrl, boolean closeChildren)
            throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Current page
        Page page = PortalObjectUtils.getPage(controllerContext);
        if (page instanceof CMSTemplatePage) {
            page = (Page) page.getParent();
        }
        String pageId = PortalObjectUtils.getHTMLSafeId(page.getId());

        // URL
        String url = this.getDestroyPageUrl(portalControllerContext, pageId, closeChildren);

        if (redirectionUrl != null) {
            // ne sense to have a pagemarker since redirection is operated
            // juste after dynamic page destruction
            redirectionUrl = redirectionUrl.replaceAll("/pagemarker/([0-9]*)/", "/");
            try {
                url = url + "&location=" + URLEncoder.encode(redirectionUrl, CharEncoding.UTF_8);
            } catch (UnsupportedEncodingException e) {
                throw new PortalException(e);
            }
        }

        return url;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public String getStartPortletUrl(PortalControllerContext portalCtx, String portletInstance, Map<String, String> windowProperties, boolean popup)
            throws PortalException {
        // Type
        PortalUrlType type;
        if (popup) {
            type = PortalUrlType.POPUP;
        } else {
            type = PortalUrlType.DEFAULT;
        }

        return this.getStartPortletUrl(portalCtx, portletInstance, windowProperties, type);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties)
            throws PortalException {
        return this.getStartPortletUrl(portalControllerContext, portletInstance, windowProperties, PortalUrlType.DEFAULT);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartPortletUrl(PortalControllerContext portalControllerContext, String portletInstance, Map<String, String> windowProperties,
            PortalUrlType type) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Window properties
        if (windowProperties == null) {
            windowProperties = new HashMap<String, String>();
        }

        // URL
        String url;
        try {
            // Current portal name
            String currentPortal = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);

            // Page identifier
            String pageId;
            // Region
            String regionId;
            // Window
            String windowName;

            if (PortalUrlType.POPUP.equals(type)) {
                // Popup
                PortalObjectPath pageObjectPath = PortalObjectPath.parse("/osivia-util/popup", PortalObjectPath.CANONICAL_FORMAT);
                pageId = URLEncoder.encode(pageObjectPath.toString(PortalObjectPath.SAFEST_FORMAT), CharEncoding.UTF_8);
                regionId = "popup";
                windowName = "popupWindow";

                // Default window properties
                windowProperties.put("osivia.currentPortal", currentPortal);
                if (windowProperties.get("osivia.hideDecorators") == null) {
                    windowProperties.put("osivia.hideDecorators", "1");
                }
                if (windowProperties.get("theme.dyna.partial_refresh_enabled") == null) {
                    windowProperties.put("theme.dyna.partial_refresh_enabled", "false");
                }
            } else if (PortalUrlType.MODAL.equals(type)) {
                // Modal
                PortalObjectPath pageObjectPath = PortalObjectPath.parse("/osivia-util/modal", PortalObjectPath.CANONICAL_FORMAT);
                pageId = URLEncoder.encode(pageObjectPath.toString(PortalObjectPath.SAFEST_FORMAT), CharEncoding.UTF_8);
                regionId = "modal-region";
                windowName = "modal-window";

                // Default window properties
                windowProperties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, String.valueOf(true));
                windowProperties.put("osivia.ajaxLink", "1");
                windowProperties.put("osivia.currentPortal", currentPortal);

            } else {
                // Default
                PortalObjectId pageObjectId = PortalObjectUtils.getPageId(controllerContext);
                pageId = URLEncoder.encode(pageObjectId.toString(PortalObjectPath.SAFEST_FORMAT), CharEncoding.UTF_8);
                regionId = "virtual";
                windowName = "dynamicPortlet";

                // Default window properties
                if (windowProperties.get("osivia.hideDecorators") == null) {
                    windowProperties.put("osivia.hideDecorators", "1");
                }
                if (windowProperties.get("theme.dyna.partial_refresh_enabled") == null) {
                    windowProperties.put("theme.dyna.partial_refresh_enabled", "false");
                }
            }


            // Start dynamic window command
            ControllerCommand command = new StartDynamicWindowCommand();

            // Portal URL
            PortalURLImpl portalUrl = new PortalURLImpl(command, controllerContext, null, null);

            // URL
            StringBuilder builder = new StringBuilder();
            builder.append(portalUrl.toString());
            builder.append("&pageId=").append(pageId);
            builder.append("&regionId=").append(regionId);
            builder.append("&windowName=").append(windowName);
            builder.append("&instanceId=").append(portletInstance);
            builder.append("&props=").append(WindowPropertiesEncoder.encodeProperties(windowProperties));
            builder.append("&params=");
            builder.append("&addToBreadcrumb=").append(this.addToBreadcrumb(portalControllerContext.getRequest()));

            if (PortalUrlType.POPUP.equals(type)) {
                url = this.adaptPortalUrlToPopup(portalControllerContext, builder.toString(), IPortalUrlFactory.POPUP_URL_ADAPTER_OPEN);
            } else {
                url = builder.toString();
            }
        } catch (Exception e) {
            throw new PortalException(e);
        }

        return url;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartPortletInNewPage(PortalControllerContext portalCtx, String pageName, String pageDisplayName, String portletInstance,
            Map<String, String> windowProperties, Map<String, String> params) throws PortalException {
        try {
            // Controller context
            ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalCtx);

            // Portal name
            String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);

            if ("osivia-util".equals(portalName)) {
                Page page = PortalObjectUtils.getPage(controllerContext);

                // Window
                Window window;
                if ("popup".equals(page.getName())) {
                    // Popup
                    window = page.getChild("popupWindow", Window.class);
                } else if ("modal".equals(page.getName())) {
                    // Modal
                    window = page.getChild("modal-window", Window.class);
                } else {
                    // Unknown case
                    window = null;
                }

                if (window != null) {
                    portalName = window.getDeclaredProperty("osivia.currentPortal");
                }
            }

            // Portal path
            String portalPath = PortalObjectId.parse("/" + portalName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT);


            // Maps initialization
            if (windowProperties == null) {
                windowProperties = new HashMap<String, String>();
            }
            if (params == null) {
                params = new HashMap<String, String>();
            }

            final ControllerCommand cmd = new StartDynamicWindowInNewPageCommand();
            final PortalURL portalURL = new PortalURLImpl(cmd, controllerContext, null, null);

            // Valeurs par défaut
            if (windowProperties.get("osivia.hideDecorators") == null) {
                windowProperties.put("osivia.hideDecorators", "1");
            }
            if (windowProperties.get("theme.dyna.partial_refresh_enabled") == null) {
                windowProperties.put("theme.dyna.partial_refresh_enabled", "false");
            }


            final String parentId = URLEncoder.encode(portalPath, "UTF-8");

            final StringBuilder url = new StringBuilder(portalURL.toString());
            url.append("&parentId=").append(parentId);

            if (pageName != null) {
                url.append("&pageName=").append(URLEncoder.encode(pageName, "UTF-8"));
            }
            if (pageDisplayName != null) {
                url.append("&pageDisplayName=").append(URLEncoder.encode(pageDisplayName, "UTF-8"));
            }

            url.append("&instanceId=").append(portletInstance).append("&props=").append(WindowPropertiesEncoder.encodeProperties(windowProperties))
                    .append("&params=").append(WindowPropertiesEncoder.encodeProperties(params));

            return url.toString();
        } catch (final Exception e) {
            throw new PortalException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getBasePortalUrl(PortalControllerContext portalControllerContext) {
        final HttpServletRequest request = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                .getClientRequest();
        return URLUtils.createUrl(request);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getRefreshPageUrl(PortalControllerContext portalControllerContext) {
        return this.getRefreshPageUrl(portalControllerContext, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRefreshPageUrl(PortalControllerContext portalControllerContext, boolean newContentNotify) {

        // Controller context
        final ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        final PortalObjectId currentPageId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, Constants.ATTR_PAGE_ID);

        // URL context
        final URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
        // URL format
        final URLFormat urlFormat = URLFormat.newInstance(false, true);

        final RefreshPageCommand resfreshCmd = new RefreshPageCommand(currentPageId.toString(PortalObjectPath.SAFEST_FORMAT));

        if (newContentNotify) {
            resfreshCmd.setEcmActionReturn("_NOTIFKEY_");
            resfreshCmd.setNewDocId("_NEWID_");
        }

        // URL
        return controllerContext.renderURL(resfreshCmd, urlContext, urlFormat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPutDocumentInTrashUrl(PortalControllerContext ctx, String docId, String docPath) {


        final String backPageMarker = (String) ControllerContextAdapter.getControllerContext(ctx).getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                "osivia.backPageMarker");


        final ControllerCommand cmd = new CMSPutDocumentInTrashCommand(docId, docPath, backPageMarker);
        final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        return portalURL.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getBackURL(PortalControllerContext portalControllerContext, boolean mobile) {
        return this.getBackURL(portalControllerContext, mobile, false);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getBackURL(PortalControllerContext portalControllerContext, boolean mobile, boolean refresh) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);


        // Back page marker attribute name
        String backPageMarkerName;
        if (mobile) {
            backPageMarkerName = "osivia.backMobilePageMarker";
        } else {
            backPageMarkerName = "osivia.backPageMarker";
        }
        // Back page marker
        String backPageMarker = (String) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, backPageMarkerName);


        // Refresh indicator attribute name
        String refreshName;
        if (mobile) {
            refreshName = "osivia.mobileRefreshBack";
        } else {
            refreshName = "osivia.refreshBack";
        }

        if (!refresh) {
            refresh = BooleanUtils.isTrue((Boolean) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, refreshName));
        }


        // Back URL
        String backUrl;
        if (backPageMarker == null) {
            backUrl = null;
        } else {
            // Page marker infos
            PageMarkerInfo infos = PageMarkerUtils.getPageMarkerInfo(controllerContext, backPageMarker);

            if ((infos != null) && (infos.getPageId() != null)) {
                // Page portal object identifier
                PortalObjectId pageObjectId = infos.getPageId();
                // URL context
                URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
                // URL format
                URLFormat urlFormat = URLFormat.newInstance(false, true);

                // Controller command
                ControllerCommand command = new BackCommand(pageObjectId, backPageMarker, refresh);
                
                backUrl = controllerContext.renderURL(command, urlContext, urlFormat);
            } else {
                backUrl = null;
            }
        }

        return backUrl;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getHttpErrorUrl(PortalControllerContext portalControllerContext, int httpErrorCode) {
        final HttpServletRequest request = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                .getClientRequest();
        final String uri = System.getProperty("error.defaultPageUri");
        final String url = URLUtils.createUrl(request, uri, null);
        return URLUtils.addParameter(url, "httpCode", String.valueOf(httpErrorCode));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterizedURL(PortalControllerContext portalControllerContext, String cmsPath, String template, String renderset, String layoutState,
            Boolean permalinks) {
        // Controller context
        final ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // URL context
        final URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
        // URL format
        final URLFormat urlFormat = URLFormat.newInstance(false, true);

        // Command
        final ControllerCommand command = new ParameterizedCommand(cmsPath, template, renderset, layoutState, permalinks);

        // URL
        return controllerContext.renderURL(command, urlContext, urlFormat);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEcmCommandUrl(PortalControllerContext ctx, String path, EcmCommonCommands command) throws PortalException {

        return this.getEcmCommandUrl(ctx, path, command.name(), path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEcmCommandUrl(PortalControllerContext ctx, String path, EcmCommonCommands command, String redirectionPath) throws PortalException {

        return this.getEcmCommandUrl(ctx, path, command.name(), redirectionPath);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getEcmCommandUrl(PortalControllerContext portalControllerContext, String path, String commandName, String redirectionPath)
            throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        IEcmCommandervice service = Locator.findMBean(IEcmCommandervice.class, IEcmCommandervice.MBEAN_NAME);
        EcmCommand initialCommand = service.getCommand(commandName);

        if (initialCommand == null) {
            throw new PortalException("command " + commandName + " not found");
        }

        controllerContext.setAttribute(Scope.SESSION_SCOPE, EcmCommand.REDIRECTION_PATH_ATTRIBUTE, redirectionPath);

        final ControllerCommand cmd = new EcmCommandDelegate(initialCommand, path);

        final PortalURL portalURL = new PortalURLImpl(cmd, controllerContext, null, null);
        return portalURL.toString();
    }


    /**
     * {@inheritDoc}
     */
    public String getEcmCommandUrl(PortalControllerContext portalControllerContext, String path, String commandName) throws PortalException {

        final IEcmCommandervice service = Locator.findMBean(IEcmCommandervice.class, IEcmCommandervice.MBEAN_NAME);
        final org.osivia.portal.api.ecm.EcmCommand initialCommand = service.getCommand(commandName);

        if (initialCommand == null) {
            throw new PortalException("command " + commandName + " not found");
        }


        final ControllerCommand cmd = new EcmCommandDelegate(initialCommand, path);

        final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(portalControllerContext), null, null);
        return portalURL.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getHomePageUrl(PortalControllerContext portalControllerContext, boolean refresh) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Current portal
        Portal portal = PortalObjectUtils.getPortal(controllerContext);
        // Portal default page
        Page defaultPage = portal.getDefaultPage();
        // Portal default page identifier
        PortalObjectId defaultPageId = defaultPage.getId();
        
        // Controller command
        ControllerCommand command;
        if (refresh) {
            command = new RefreshPageCommand(defaultPageId.toString(PortalObjectPath.SAFEST_FORMAT));
        } else {
            command = new ViewPageCommand(defaultPageId);
        }

        // Portal URL
        PortalURL portalUrl = new PortalURLImpl(command, controllerContext, null, null);

        return portalUrl.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfiledHomePageUrl(PortalControllerContext portalControllerContext) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Current portal
        Portal portal = PortalObjectUtils.getPortal(controllerContext);
        // Current portal name
        String portalName = portal.getName();

        // Controller command
        ControllerCommand command = new MonEspaceCommand(portalName);
        // Portal URL
        PortalURL portalUrl = new PortalURLImpl(command, controllerContext, true, null);

        return portalUrl.toString();
    }


    @Override
    public String getViewPageUrl(PortalControllerContext portalControllerContext, String id) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Portal object identifier
        PortalObjectId portalObjectId = PortalObjectId.parse(id, PortalObjectPath.CANONICAL_FORMAT);

        // Command
        ViewPageCommand command = new ViewPageCommand(portalObjectId);

        // Portal URL
        PortalURLImpl portalUrl = new PortalURLImpl(command, controllerContext, null, null);

        return portalUrl.toString() + "?init-state=true";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getSharingLinkUrl(PortalControllerContext portalControllerContext, String id) throws PortletException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // URL context
        URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
        // URL format
        URLFormat urlFormat = URLFormat.newInstance(false, true);


        // URL
        String url;

        if (StringUtils.isEmpty(id)) {
            url = null;
        } else {
            // Command
            LinkSharingCommand command = new LinkSharingCommand();
            command.setId(id);

            url = controllerContext.renderURL(command, urlContext, urlFormat);
        }

        return url;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getAdvancedSearchUrl(PortalControllerContext portalControllerContext, String search, boolean advancedSearch) throws PortalException {

        return getAdvancedSearchUrl(portalControllerContext, search, advancedSearch, null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getAdvancedSearchUrl(PortalControllerContext portalControllerContext, String search, boolean advancedSearch, Map<String, List<String>> selectors) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Controller command
        AdvancedSearchCommand command = new AdvancedSearchCommand(search, advancedSearch);
        if(selectors != null) {
        	command.setSelectors(selectors);
    	}
        // Portal URL
        PortalURL portalUrl = new PortalURLImpl(command, controllerContext, false, null);

        return portalUrl.toString();
    }


    @Override
    public String getUserWorkspaceCommandUrl(PortalControllerContext portalControllerContext) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Controller command
        ControllerCommand command = new UserWorkspaceCommand();

        // Portal URL
        PortalURL portalUrl = new PortalURLImpl(command, controllerContext, true, null);

        return portalUrl.toString();
    }

}
