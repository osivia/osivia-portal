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

import org.apache.commons.lang.BooleanUtils;
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
import org.osivia.portal.api.ecm.EcmCommonCommands;
import org.osivia.portal.api.ecm.EcmViews;
import org.osivia.portal.api.ecm.IEcmCommandervice;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
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
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.ParameterizedCommand;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.pagemarker.PortalCommandFactory;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.share.ShareCommand;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.utils.URLUtils;
import org.osivia.portal.core.web.WebIdService;

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
    public String getPermaLink(PortalControllerContext ctx, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)
            throws PortalException {

        try {
            String templateInstanciationParentId = null;
            final String portalPersistentName = this.getPortalPersistentName(ctx);

            // Direct CMS Link : use CMSCommand
            if (IPortalUrlFactory.PERM_LINK_TYPE_CMS.equals(permLinkType)) {

                final CmsCommand cmsCommand = new CmsCommand(null, cmsPath, params, null, null, null, null, null, null, null, portalPersistentName);

                // Remove default initialisation
                cmsCommand.setItemScope(null);

                cmsCommand.setInsertPageMarker(false);

                URLContext urlContext = ControllerContextAdapter.getControllerContext(ctx).getServerInvocation().getServerContext().getURLContext();
                urlContext = urlContext.withAuthenticated(false);
                final String permLinkUrl = ControllerContextAdapter.getControllerContext(ctx).renderURL(cmsCommand, urlContext, URLFormat.newInstance(false, true));

                return permLinkUrl;

            } else if(IPortalUrlFactory.PERM_LINK_TYPE_SHARE.equals(permLinkType)){

                final ShareCommand shareCmd = new ShareCommand(StringUtils.substringAfter(cmsPath, WebIdService.CMS_PATH_PREFIX), params);

                URLContext urlContext = ControllerContextAdapter.getControllerContext(ctx).getServerInvocation().getServerContext().getURLContext();
                urlContext = urlContext.withAuthenticated(false);
                final String permLinkUrl = ControllerContextAdapter.getControllerContext(ctx).renderURL(shareCmd, urlContext, URLFormat.newInstance(false, true));

                return permLinkUrl;

            }

            // Others permalink (Lists, RSS, ...) : use PermLinkCommand
            if (ctx.getRequest() != null) {
                final Window window = (Window) ctx.getRequest().getAttribute("osivia.window");
                if (window != null) {
                    final Page page = window.getPage();

                    if (page instanceof ITemplatePortalObject) {
                        templateInstanciationParentId = URLEncoder.encode(page.getParent().getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");
                    }
                }
            }

            final PermLinkCommand linkCmd = new PermLinkCommand(permLinkRef, params, templateInstanciationParentId, cmsPath, permLinkType, portalPersistentName);
            URLContext urlContext = ControllerContextAdapter.getControllerContext(ctx).getServerInvocation().getServerContext().getURLContext();

            urlContext = urlContext.withAuthenticated(false);

            return ControllerContextAdapter.getControllerContext(ctx).renderURL(linkCmd, urlContext, URLFormat.newInstance(false, true));

        } catch (final Exception e) {
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
            final String portalName = (String) ControllerContextAdapter.getControllerContext(portalControllerContext).getAttribute(Scope.REQUEST_SCOPE, "osivia.currentPortalName");

            final Portal defaultPortal = ControllerContextAdapter.getControllerContext(portalControllerContext).getController().getPortalObjectContainer().getContext()
                    .getDefaultPortal();

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
            if( StringUtils.startsWith(suffix, PortalCommandFactory.POPUP_CLOSE_PATH )) {
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
    public String getStartPageUrl(PortalControllerContext ctx, String parentName, String pageName, String templateName, Map<String, String> props,
            Map<String, String> params) throws PortalException {

        try {
            final ControllerCommand cmd = new StartDynamicPageCommand();
            final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

            final String parentId = URLEncoder.encode(PortalObjectId.parse(parentName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT),
                    "UTF-8");
            final String templateId = URLEncoder.encode(PortalObjectId.parse(templateName, PortalObjectPath.CANONICAL_FORMAT)
                    .toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

            String url = portalURL.toString();
            url += "&parentId=" + parentId + "&pageName=" + pageName + "&templateId=" + templateId + "&props="
                    + WindowPropertiesEncoder.encodeProperties(props) + "&params=" + WindowPropertiesEncoder.encodeProperties(params);
            return url;
        } catch (final Exception e) {
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
        final ControllerCommand cmd = new StopDynamicPageCommand();
        final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

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
        final ControllerCommand cmd = new StartDynamicWindowCommand();
        final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(portalControllerContext), null, null);

        final String pageId = URLEncoder.encode(
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
	public String getStartPortletUrl(PortalControllerContext portalCtx, String portletInstance, Map<String, String> windowProperties, boolean popup)
			throws PortalException {

		return this.getStartPortletUrl(portalCtx, portletInstance, windowProperties, null, popup);

	}

    /**
     * {@inheritDoc}
     */
    public String getStartPortletUrl(PortalControllerContext portalCtx, String portletInstance, Map<String, String> windowProperties,
            Map<String, String> params, boolean popup) throws PortalException {
        // Controller context
        final ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalCtx);

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

            final String region = "virtual";
            final String windowName = "dynamicPortlet";

            final String pageId = URLEncoder.encode(PortalObjectUtils.getPageId(controllerContext).toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

            final ControllerCommand cmd = new StartDynamicWindowCommand();
            final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(portalCtx), null, null);

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

        } catch (final Exception e) {
            throw new PortalException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getStartPortletInNewPage(PortalControllerContext portalCtx, String pageName, String pageDisplayName, String portletInstance, Map<String, String> windowProperties,
            Map<String, String> params) throws PortalException {

        try {
            String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);
            // if (portalName == null)
            // portalName = "default";

            portalName = "/" + portalName;


            // Maps initialization
            if (windowProperties == null) {
                windowProperties = new HashMap<String, String>();
            }
            if (params == null) {
                params = new HashMap<String, String>();
            }

            final ControllerCommand cmd = new StartDynamicWindowInNewPageCommand();
            final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(portalCtx), null, null);

            // Valeurs par défaut
            if (windowProperties.get("osivia.hideDecorators") == null) {
                windowProperties.put("osivia.hideDecorators", "1");
            }
            if (windowProperties.get("theme.dyna.partial_refresh_enabled") == null) {
                windowProperties.put("theme.dyna.partial_refresh_enabled", "false");
            }


            final String parentId = URLEncoder.encode(PortalObjectId.parse(portalName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT),
                    "UTF-8");


            final StringBuffer url = new StringBuffer(portalURL.toString());
            url.append("&parentId="+parentId);

            if( pageName != null) {
                url.append("&pageName="+URLEncoder.encode(pageName,"UTF-8"));
            }
            if( pageDisplayName != null) {
                url.append("&pageDisplayName="+URLEncoder.encode(pageDisplayName,"UTF-8"));
            }

            url.append("&instanceId=" + portletInstance +  "&props="
                    + WindowPropertiesEncoder.encodeProperties(windowProperties) + "&params=" + WindowPropertiesEncoder.encodeProperties(params));

            return url.toString();

        } catch (final Exception e) {
            throw new PortalException(e);
        }
    }



    /**
     * {@inheritDoc}
     */
    public String getBasePortalUrl(PortalControllerContext portalControllerContext) {
        final HttpServletRequest request = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                .getClientRequest();
        return URLUtils.createUrl(request);
    }


    /**
     * {@inheritDoc}
     */
    public String getRefreshPageUrl(PortalControllerContext portalControllerContext) {
    	return this.getRefreshPageUrl(portalControllerContext, false);
    }

    /**
     * {@inheritDoc}
     */
    public String getRefreshPageUrl(PortalControllerContext portalControllerContext, boolean newContentNotify) {

        // Controller context
        final ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        final PortalObjectId currentPageId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                Constants.ATTR_PAGE_ID);

        // URL context
        final URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
        // URL format
        final URLFormat urlFormat = URLFormat.newInstance(false, true);

        final RefreshPageCommand resfreshCmd = new RefreshPageCommand(currentPageId.toString(PortalObjectPath.SAFEST_FORMAT));

        if(newContentNotify) {
        	resfreshCmd.setEcmActionReturn("_NOTIFKEY_");
        	resfreshCmd.setNewDocId("_NEWID_");
        }

        // URL
        return controllerContext.renderURL(resfreshCmd, urlContext, urlFormat);
    }

    /**
     * {@inheritDoc}
     */
    public String getPutDocumentInTrashUrl(PortalControllerContext ctx, String docId, String docPath) {



    	final String backPageMarker = (String) ControllerContextAdapter.getControllerContext(ctx).getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backPageMarker");


        final ControllerCommand cmd = new CMSPutDocumentInTrashCommand(docId, docPath, backPageMarker);
        final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);

        return portalURL.toString();
    }


    /**
     * {@inheritDoc}
     */
//    public String getEcmFilesManagementUrl(PortalControllerContext ctx, String cmsPath, EcmOperations parameter) {
//
//        ControllerCommand cmd = new EcmFilesManagementCommand(cmsPath, parameter);
//        PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);
//
//        return portalURL.toString();
//    }

    /**
     * {@inheritDoc}
     */
//	public String getSubscriptionUrl(PortalControllerContext ctx, String cmsPath, EcmOperations parameter) {
//
//		ControllerCommand cmd = new SubscriptionCommand(cmsPath, parameter);
//
//		PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(ctx), null, null);
//		return portalURL.toString();
//	}


    /**
     * {@inheritDoc}
     */
    public String getBackURL(PortalControllerContext portalControllerContext, boolean mobile) {
        // Controller context
        final ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Back page marker

        String backPageMarkerName = "osivia.backPageMarker";
        if( mobile) {
            backPageMarkerName = "osivia.backMobilePageMarker";
        }

        String refreshName = "osivia.refreshBack";
        if( mobile) {
            refreshName = "osivia.mobileRefreshBack";
        }

        final String backPageMarker = (String) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,backPageMarkerName );
        final Boolean refresh = BooleanUtils.isTrue((Boolean) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, refreshName));

        String backURL = null;
        if (backPageMarker != null) {
            final PageMarkerInfo infos = PageMarkerUtils.getPageMarkerInfo(controllerContext, backPageMarker);
            if ((infos != null) && (infos.getPageId() != null)) {
                final PortalObjectId pageId = infos.getPageId();

                final URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();

                if (refresh) {
                    final RefreshPageCommand resfreshCmd = new RefreshPageCommand(pageId.toString(PortalObjectPath.SAFEST_FORMAT));
                    backURL = controllerContext.renderURL(resfreshCmd, urlContext, URLFormat.newInstance(false, true)) +"&backPageMarker="+ backPageMarker;
                } else {
                    final ViewPageCommand rpc = new ViewPageCommand(pageId);
                    backURL = controllerContext.renderURL(rpc, urlContext, URLFormat.newInstance(false, true)) + "?backPageMarker="+ backPageMarker;
                }

               // backURL = backURL.replaceAll("/pagemarker/([0-9]*)/", "/pagemarker/" + backPageMarker + "/");
            }
        }

        return backURL;
    }


    /**
     * {@inheritDoc}
     */
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


	/* (non-Javadoc)
	 * @see org.osivia.portal.api.urls.IPortalUrlFactory#getEcmCommandUrl(org.osivia.portal.api.context.PortalControllerContext, java.lang.String, org.osivia.portal.api.urls.EcmDocumentCommand)
	 */
	public String getEcmCommandUrl(
			PortalControllerContext ctx, String path,
			EcmCommonCommands command) throws PortalException  {

		return this.getEcmCommandUrl(ctx, path, command.name());
	}


	/* (non-Javadoc)
	 * @see org.osivia.portal.api.urls.IPortalUrlFactory#getEcmCommandUrl(org.osivia.portal.api.context.PortalControllerContext, java.lang.String, java.lang.String)
	 */
	public String getEcmCommandUrl(
			PortalControllerContext portalControllerContext, String path,
			String commandName) throws PortalException  {

		final IEcmCommandervice service = Locator.findMBean(IEcmCommandervice.class, IEcmCommandervice.MBEAN_NAME);
		final org.osivia.portal.api.ecm.EcmCommand initialCommand = service.getCommand(commandName);

		if(initialCommand == null) {
			throw new PortalException("command "+commandName+" not found");
		}


		final ControllerCommand cmd = new EcmCommandDelegate(initialCommand, path);

		final PortalURL portalURL = new PortalURLImpl(cmd, ControllerContextAdapter.getControllerContext(portalControllerContext), null, null);
		return portalURL.toString();
	}


    /**
     * {@inheritDoc}
     */
    public String getDestroyCurrentPageUrl(PortalControllerContext portalControllerContext) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Current page
        Page page = PortalObjectUtils.getPage(controllerContext);
        if (page instanceof CMSTemplatePage) {
            page = (Page) page.getParent();
        }
        String pageId = PortalObjectUtils.getHTMLSafeId(page.getId());

        // Parent portal object
        PortalObject parent = page.getParent();
        String parentId = PortalObjectUtils.getHTMLSafeId(parent.getId());

        return getDestroyPageUrl(portalControllerContext, parentId, pageId);
    }

}
