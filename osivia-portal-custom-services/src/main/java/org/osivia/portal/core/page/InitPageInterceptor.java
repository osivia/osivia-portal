package org.osivia.portal.core.page;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.servlet.BufferingRequestWrapper;
import org.jboss.portal.common.servlet.BufferingResponseWrapper;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.theme.LayoutInfo;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.panels.IPanelsService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.web.IWebIdService;

/**
 * Page initialization interceptor.
 * Menu access or re-initialization redirect to CMS mode.
 * Home page should also reset page.
 *
 * @author Cédric Krommenhoek
 * @see ControllerInterceptor
 */
public class InitPageInterceptor extends ControllerInterceptor {

    /** Portal URL factory. */
    private IPortalUrlFactory urlFactory;
    /** WebId service. */
    private IWebIdService webIdService;
    /** Internationalization service. */
    private IInternationalizationService internationalizationService;


    /**
     * Constructor.
     */
    public InitPageInterceptor() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse invoke(ControllerCommand command) throws Exception, InvocationException {
        if (command instanceof RenderPageCommand) {
            // Render page command
            RenderPageCommand renderPageCommand = (RenderPageCommand) command;
            // Controller context
            ControllerContext controllerContext = command.getControllerContext();
            // Server context
            ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();
            // HTTP servlet request
            HttpServletRequest request = serverContext.getClientRequest();
            // Portal controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
            // Page
            Page page = this.getPage(renderPageCommand);
            // Portal
            Portal portal = page.getPortal();

            // Portal request path
            String portalRequestPath = serverContext.getPortalRequestPath();

            // Page marker
            String pageMarker = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "currentPageMarker");

            // Default page indicator
            boolean defaultPage = false;
            if (StringUtils.isEmpty(portalRequestPath) || "/".equals(portalRequestPath)) {
                // Default page
                defaultPage = true;
            } else if (page.equals(portal.getDefaultPage()) && "1".equals(pageMarker)) {
                // Disconnection : default page and page marker = 1
            	// Avoid loop on /pagemarker/1/cms/...
                if (!(portalRequestPath.startsWith("/pagemarker/1") || portalRequestPath.startsWith("/web/"))) {
                    defaultPage = true;
                }
            }

            // State initialization indicator
            boolean initState = BooleanUtils.toBoolean(request.getParameter("init-state"));
            if ("1".equals(controllerContext.getAttribute(Scope.REQUEST_SCOPE, "osivia.RestoreTab"))) {
                initState = false;
            }
            if ("1".equals(page.getProperty("osivia.genericPage"))) {
                initState = false;
            }


            if (initState || defaultPage) {
                CMSItem pagePublishSpaceConfig = CmsCommand.getPagePublishSpaceConfig(controllerContext, page);
                if (((pagePublishSpaceConfig != null) && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeInternalContents")))
                        || ("1".equals(page.getDeclaredProperty("osivia.cms.directContentPublisher")))) {
                    if (!BooleanUtils.toBoolean(request.getParameter("edit-template-mode"))) {
                        // CMS redirection
                        String path = page.getDeclaredProperty("osivia.cms.basePath");

                        if (StringUtils.isNotEmpty(pagePublishSpaceConfig.getWebId())) {
                            path = this.webIdService.webIdToCmsPath(pagePublishSpaceConfig.getWebId());
                        }

                        // Page state initialization
                        PageCustomizerInterceptor.initPageState(page, controllerContext);

                        // Redirection URL
                        String url = this.urlFactory.getCMSUrl(portalControllerContext, page.getId().toString(PortalObjectPath.CANONICAL_FORMAT), path, null,
                                IPortalUrlFactory.CONTEXTUALIZATION_PAGE, "tabs", null, null, null, null);
                        return new RedirectionResponse(url);
                    }
                }
            }


            // Unset maximized mode
            if (BooleanUtils.toBoolean(request.getParameter("unsetMaxMode"))) {
                PageCustomizerInterceptor.unsetMaxMode(page.getChildren(PortalObject.WINDOW_MASK), controllerContext);
            }


            // Page state initialization
            if (initState) {
                PageCustomizerInterceptor.initPageState(page, controllerContext);
            }


            // Another portal template edition warning
            if (BooleanUtils.toBoolean(request.getParameter("init-state")) && BooleanUtils.toBoolean(request.getParameter("edit-template-mode"))) {
                String originalPortalName = request.getParameter("original-portal");
                if (!portal.getName().equals(originalPortalName)) {
                    // For template page, warn if the current portal does not match the main domain
                    String portalDefaultAdviceLabel = this.internationalizationService.getString(InternationalizationConstants.KEY_ADV_PORTAL_DEFAULT,
                            request.getLocale());
                    NotificationsUtils.getNotificationsService().addSimpleNotification(portalControllerContext, portalDefaultAdviceLabel,
                            NotificationsType.WARNING, null);
                }
            }


            // Check layout
            this.checkLayout(controllerContext, page);
        }

        return (ControllerResponse) command.invokeNext();
    }


    /**
     * Get page.
     *
     * @param command render page command
     * @return page
     */
    private Page getPage(RenderPageCommand command) {
        // Controller context
        ControllerContext controllerContext = command.getControllerContext();
        // Portal object container
        PortalObjectContainer portalObjectContainer = controllerContext.getController().getPortalObjectContainer();
        // Target object identifier
        PortalObjectId targetId = command.getTargetId();

        // Target
        PortalObject target = portalObjectContainer.getObject(targetId);

        // Page
        Page page = null;

        if (target instanceof Page) {
            page = (Page) target;
        }

        return page;
    }


    /**
     * Check layout attributes.
     *
     * @param controllerContext controller context
     * @param page page
     * @throws ControllerException
     */
    private void checkLayout(ControllerContext controllerContext, Page page) throws ControllerException {
        // Layout
        LayoutService layoutService = controllerContext.getController().getPageService().getLayoutService();
        String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        PortalLayout layout = layoutService.getLayout(layoutId, false);
        if (layout == null) {
            throw new ControllerException("Layout " + layoutId + "not found for page " + page.toString());
        }
        LayoutInfo layoutInfo = layout.getLayoutInfo();
        String uri = layoutInfo.getURI();


        // Search maximized window
        boolean maximized = false;
        Collection<PortalObject> children = page.getChildren(PortalObject.WINDOW_MASK);
        for (PortalObject child : children) {
            Window window = (Window) child;
            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());
            WindowNavigationalState windowNavState = (WindowNavigationalState) controllerContext
                    .getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);

            if ((windowNavState != null) && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {
                maximized = true;
                break;
            }
        }


        // At this time, windows displaying is only checked for index and maximized state
        if (maximized) {
            uri = layoutInfo.getURI("maximized");
        }


        // Context path
        String contextPath = layoutInfo.getContextPath();

        // Server invocation
        ServerInvocation serverInvocation = controllerContext.getServerInvocation();
        // Server context
        ServerInvocationContext serverContext = serverInvocation.getServerContext();
        // Servlet context
        ServletContext servletContext = serverContext.getClientRequest().getSession().getServletContext().getContext(contextPath);
        // Locales
        Locale[] locales = serverInvocation.getRequest().getLocales();

        // Request
        BufferingRequestWrapper request = new BufferingRequestWrapper(serverContext.getClientRequest(), contextPath, locales);
        request.setAttribute(InternalConstants.ATTR_LAYOUT_PARSING, true);
        request.setAttribute(InternalConstants.ATTR_LAYOUT_VISIBLE_REGIONS, new HashSet<String>());

        // Response
        BufferingResponseWrapper response = new BufferingResponseWrapper(serverContext.getClientResponse());

        // Request dispatcher
        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(uri);
        try {
            requestDispatcher.include(request, response);
        } catch (Exception e) {
            throw new ControllerException(e);
        }

        // CMS
        Boolean layoutCMS = (Boolean) request.getAttribute(InternalConstants.ATTR_LAYOUT_CMS_INDICATOR);
        controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LAYOUT_CMS_INDICATOR, layoutCMS);

        // Panels
        Boolean panels = (Boolean) request.getAttribute(IPanelsService.REQUEST_ATTRIBUTE);
        controllerContext.setAttribute(Scope.REQUEST_SCOPE, IPanelsService.REQUEST_ATTRIBUTE, panels);

        // Visible regions
        controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LAYOUT_VISIBLE_REGIONS,
                request.getAttribute(InternalConstants.ATTR_LAYOUT_VISIBLE_REGIONS));
        if (maximized) {
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LAYOUT_VISIBLE_REGIONS_PARSER_STATE, "maximized");
        }
    }


    /**
     * Setter for urlFactory.
     *
     * @param urlFactory the urlFactory to set
     */
    public void setUrlFactory(IPortalUrlFactory urlFactory) {
        this.urlFactory = urlFactory;
    }

    /**
     * Setter for webIdService.
     *
     * @param webIdService the webIdService to set
     */
    public void setWebIdService(IWebIdService webIdService) {
        this.webIdService = webIdService;
    }

    /**
     * Setter for internationalizationService.
     *
     * @param internationalizationService the internationalizationService to set
     */
    public void setInternationalizationService(IInternationalizationService internationalizationService) {
        this.internationalizationService = internationalizationService;
    }

}
