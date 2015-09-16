package org.osivia.portal.core.panels;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.commons.lang.BooleanUtils;
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
import org.jboss.portal.core.model.content.Content;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PortalObjectCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.theme.LayoutInfo;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.panels.IPanelsService;
import org.osivia.portal.api.panels.Panel;
import org.osivia.portal.api.panels.PanelPlayer;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Panels interceptor.
 *
 * @author Cédric Krommenhoek
 * @see ControllerInterceptor
 */
public class PanelsInterceptor extends ControllerInterceptor {

    /** Panels service. */
    private IPanelsService panelsService;


    /**
     * Constructor.
     */
    public PanelsInterceptor() {
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
            // Page
            Page page = this.getPage(renderPageCommand);
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, IPanelsService.PAGE_REQUEST_ATTRIBUTE, page);

            // Check layout
            if (!PortalObjectUtils.isJBossPortalAdministration(renderPageCommand.getPortal())) {
                this.checkLayout(controllerContext, page);
            }


            // Refresh cache
            this.refreshCache(controllerContext, page);

            if (BooleanUtils.isTrue((Boolean) controllerContext.getAttribute(Scope.REQUEST_SCOPE, IPanelsService.REQUEST_ATTRIBUTE))) {
                // Generate panels
                this.generatePanels(controllerContext, page);
            }
        }

        return (ControllerResponse) command.invokeNext();
    }


    /**
     * Get page.
     *
     * @param command portal object command
     * @return page
     */
    private Page getPage(PortalObjectCommand command) {
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
        } else if (target instanceof Window) {
            Window window = (Window) target;
            page = window.getPage();
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
     * Refresh windows cache.
     *
     * @param controllerContext controller context
     * @param page page
     */
    private void refreshCache(ControllerContext controllerContext, Page page) {
        // Taskbar window
        Window taskbarWindow = null;
        Collection<PortalObject> portalObjects = page.getChildren(PortalObject.WINDOW_MASK);
        for (PortalObject portalObject : portalObjects) {
            Window window = (Window) portalObject;
            Content content = window.getContent();
            if (ITaskbarService.WINDOW_INSTANCE.equals(content.getURI())) {
                taskbarWindow = window;
                break;
            }
        }
        if (taskbarWindow != null) {
            String key = "cached_markup." + taskbarWindow.getId();
            controllerContext.removeAttribute(Scope.PRINCIPAL_SCOPE, key);
        }
    }


    /**
     * Generate panels.
     *
     * @param controllerContext controller context
     * @param page page
     * @throws PortalException
     */
    private void generatePanels(ControllerContext controllerContext, Page page) throws PortalException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

        // Maximized window
        Window maximizedWindow = PortalObjectUtils.getMaximizedWindow(controllerContext, page);


        // Navigation panel player
        PanelPlayer navigationPlayer = null;

        if (maximizedWindow != null) {
            Content content = maximizedWindow.getContent();
            navigationPlayer = this.panelsService.getNavigationPlayer(portalControllerContext, content.getURI());
        }

        if (navigationPlayer == null) {
            this.panelsService.closePanel(portalControllerContext, Panel.NAVIGATION_PANEL);
        } else {
            this.panelsService.openPanel(portalControllerContext, Panel.NAVIGATION_PANEL, navigationPlayer);
        }
    }


    /**
     * Setter for panelsService.
     *
     * @param panelsService the panelsService to set
     */
    public void setPanelsService(IPanelsService panelsService) {
        this.panelsService = panelsService;
    }

}
