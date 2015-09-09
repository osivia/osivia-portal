package org.osivia.portal.core.taskbar;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

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
import org.jboss.portal.core.model.content.Content;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.PortalObjectCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowActionCommand;
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
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.taskbar.TaskbarPlayer;
import org.osivia.portal.api.taskbar.TaskbarState;
import org.osivia.portal.api.taskbar.TaskbarTask;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Taskbar interceptor.
 *
 * @author Cédric Krommenhoek
 * @see ControllerInterceptor
 */
public class TaskbarInterceptor extends ControllerInterceptor {

    /** Taskbar service. */
    private ITaskbarService taskbarService;


    /**
     * Constructor.
     */
    public TaskbarInterceptor() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse invoke(ControllerCommand command) throws Exception, InvocationException {
        // Controller context
        ControllerContext controllerContext = command.getControllerContext();


        if (command instanceof RenderPageCommand) {
            // Render page command
            RenderPageCommand renderPageCommand = (RenderPageCommand) command;
            // Portal
            Portal portal = renderPageCommand.getPortal();

            // Check layout
            if (!PortalObjectUtils.isJBossPortalAdministration(portal)) {
                this.checkLayout(renderPageCommand);
            }
        }


        if ((command instanceof RenderPageCommand)
                || ((command instanceof InvokePortletWindowActionCommand) && (ControllerContext.AJAX_TYPE == controllerContext.getType()))) {
            // Page command
            PageCommand pageCommand = (PageCommand) command;
            // Portal controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

            // Page
            Page page = this.getPage(pageCommand);

            // Taskbar
            if (TaskbarUtils.containsTaskbar(controllerContext)) {
                // Player
                TaskbarPlayer player = null;
                // Task identifier
                String id = null;

                // Base path
                String basePath = page.getProperty("osivia.cms.basePath");
                // Current path
                String currentPath = TaskbarUtils.getCurrentPath(controllerContext, page);


                if (command instanceof RenderPageCommand) {
                    // Refresh cache
                    this.refreshCache(controllerContext, page);

                    // Maximized window
                    Window maximizedWindow = TaskbarUtils.getMaximizedWindow(controllerContext, page);

                    if ((maximizedWindow != null) && !"1".equals(maximizedWindow.getDeclaredProperty("osivia.cms.contextualization"))) {
                        id = maximizedWindow.getDeclaredProperty(ITaskbarService.TASK_ID_WINDOW_PROPERTY);

                        if (id != null) {
                            List<TaskbarTask> customTasks = this.taskbarService.getCustomTasks(portalControllerContext);
                            for (TaskbarTask task : customTasks) {
                                if (id.equals(task.getId())) {
                                    player = task.getTaskbarPlayer();
                                    break;
                                }
                            }
                        }
                    } else if (!StringUtils.equals(currentPath, basePath)) {
                        // Protected current path
                        String protectedCurrentPath = currentPath + "/";

                        // Navigation tasks
                        List<TaskbarTask> navigationTasks = this.taskbarService.getNavigationTasks(portalControllerContext, basePath, currentPath);
                        for (TaskbarTask navigationTask : navigationTasks) {
                            String protectedPath = navigationTask.getPath() + "/";
                            if (StringUtils.startsWith(protectedCurrentPath, protectedPath)) {
                                id = navigationTask.getId();
                                player = navigationTask.getTaskbarPlayer();
                                break;
                            }
                        }
                    } else {
                        id = ITaskbarService.HOME_TASK_ID;
                    }


                    // Taskbar state
                    this.injectTaskbarState(controllerContext, id, player);
                }


                if (id != null) {
                    if (player == null) {
                        this.taskbarService.addEmptyWindow(portalControllerContext, id);
                    } else {
                        this.taskbarService.addWindow(portalControllerContext, player, id);
                    }
                }
            }
        }

        return (ControllerResponse) command.invokeNext();
    }


    /**
     * Check layout attributes.
     *
     * @param renderPageCommand render page command
     * @throws ControllerException
     */
    private void checkLayout(RenderPageCommand renderPageCommand) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();

        // Page
        Page page = this.getPage(renderPageCommand);

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
        ServerInvocation serverInvocation = renderPageCommand.getControllerContext().getServerInvocation();
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

        // Taskbar
        Boolean taskbar = (Boolean) request.getAttribute(ITaskbarService.REQUEST_ATTRIBUTE);
        controllerContext.setAttribute(Scope.REQUEST_SCOPE, ITaskbarService.REQUEST_ATTRIBUTE, taskbar);

        // Visible regions
        controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LAYOUT_VISIBLE_REGIONS,
                request.getAttribute(InternalConstants.ATTR_LAYOUT_VISIBLE_REGIONS));
        if (maximized) {
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LAYOUT_VISIBLE_REGIONS_PARSER_STATE, "maximized");
        }
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
     * Refresh taskbar window cache.
     *
     * @param controllerContext controller context
     * @param page page
     */
    private void refreshCache(ControllerContext controllerContext, Page page) {
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
     * Inject taskbar state into client request attributes.
     *
     * @param controllerContext controller context
     * @param id active task identifier
     * @param player active task player
     */
    private void injectTaskbarState(ControllerContext controllerContext, String id, TaskbarPlayer player) {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
        // HTTP servlet request
        HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();

        // Closed taskbar indicator
        boolean closed = true;
        if ((id != null) && (player != null)) {
            TaskbarState state = this.taskbarService.getTaskbarState(portalControllerContext);
            if ((state != null) && (state.getTask() != null) && (id.equals(state.getTask().getId()))) {
                closed = state.isClosed();
            }
        }
        request.setAttribute(ITaskbarService.CLOSED_REQUEST_ATTRIBUTE, closed);

        // Switchable taskbar indicator
        boolean switchable = (player != null);
        request.setAttribute(ITaskbarService.SWITCHABLE_REQUEST_ATTRIBUTE, switchable);
    }


    /**
     * Setter for taskbarService.
     *
     * @param taskbarService the taskbarService to set
     */
    public void setTaskbarService(ITaskbarService taskbarService) {
        this.taskbarService = taskbarService;
    }

}
