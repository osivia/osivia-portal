package org.osivia.portal.core.taskbar;

import java.util.Collection;
import java.util.List;

import javax.portlet.ActionRequest;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.PortalObjectCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowActionCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.taskbar.TaskbarPlayer;
import org.osivia.portal.api.taskbar.TaskbarTask;

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

        if ((command instanceof RenderPageCommand)
                || ((command instanceof InvokePortletWindowActionCommand) && (ControllerContext.AJAX_TYPE == controllerContext.getType()))) {
            // Page command
            PageCommand pageCommand = (PageCommand) command;
            // Portal controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

            // Page
            Page page = this.getPage(pageCommand);

            // Taskbar
            String region = page.getDeclaredProperty(ITaskbarService.REGION_NAME_PAGE_PROPERTY);
            if (region != null) {
                // Player
                TaskbarPlayer player = null;
                // Task identifier
                String id = null;

                // Base path
                String basePath = page.getProperty("osivia.cms.basePath");
                // Current path
                String currentPath = TaskbarUtils.getCurrentPath(controllerContext, page);


                if (command instanceof RenderPageCommand) {
                    // Maximized window
                    Window maximizedWindow = TaskbarUtils.getMaximizedWindow(controllerContext, page);

                    if ((maximizedWindow != null) && !"1".equals(maximizedWindow.getDeclaredProperty("osivia.cms.contextualization"))) {
                        id = maximizedWindow.getDeclaredProperty(ITaskbarService.TASK_ID_WINDOW_PROPERTY);

                        if (id != null) {
                            // Refresh cache
                            this.refreshCache(controllerContext, page, region);

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

                        // Active task
                        TaskbarTask activeTask = null;
                        for (TaskbarTask navigationTask : navigationTasks) {
                            String protectedPath = navigationTask.getPath() + "/";
                            if (StringUtils.startsWith(protectedCurrentPath, protectedPath)) {
                                activeTask = navigationTask;
                                break;
                            }
                        }

                        if (activeTask != null) {
                            player = activeTask.getTaskbarPlayer();
                            id = activeTask.getId();
                        }
                    } else {
                        id = ITaskbarService.HOME_TASK_ID;
                    }
                } else if (command instanceof InvokePortletWindowActionCommand) {
                    InvokePortletWindowActionCommand actionCommand = (InvokePortletWindowActionCommand) command;

                    if (actionCommand.getInteractionState() instanceof ParametersStateString) {
                        ParametersStateString interactionState = (ParametersStateString) actionCommand.getInteractionState();

                        // Action name
                        String action = interactionState.getValue(ActionRequest.ACTION_NAME);

                        if (ITaskbarService.OPEN_TASKBAR_ACTION.equals(action)) {
                            // Open taskbar window
                            id = interactionState.getValue("id");

                            List<TaskbarTask> customTasks = this.taskbarService.getCustomTasks(portalControllerContext);
                            boolean navigationSearch = true;
                            for (TaskbarTask task : customTasks) {
                                if (StringUtils.equals(id, task.getId())) {
                                    player = task.getTaskbarPlayer();
                                    navigationSearch = false;
                                    break;
                                }
                            }
                            if (navigationSearch) {
                                List<TaskbarTask> navigationTasks = this.taskbarService.getNavigationTasks(portalControllerContext, basePath, currentPath);
                                for (TaskbarTask task : navigationTasks) {
                                    if (id.equals(task.getId())) {
                                        player = task.getTaskbarPlayer();
                                        break;
                                    }
                                }
                            }
                        } else if (ITaskbarService.CLOSE_TASKBAR_ACTION.equals(action)) {
                            // Close taskbar window
                            this.taskbarService.addEmptyWindow(portalControllerContext, null);
                        }
                    }
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
     * @param region region name
     */
    private void refreshCache(ControllerContext controllerContext, Page page, String region) {
        Window taskbarWindow = null;
        Collection<PortalObject> portalObjects = page.getChildren(PortalObject.WINDOW_MASK);
        for (PortalObject portalObject : portalObjects) {
            Window window = (Window) portalObject;
            if (region.equals(window.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION))) {
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
     * Setter for taskbarService.
     *
     * @param taskbarService the taskbarService to set
     */
    public void setTaskbarService(ITaskbarService taskbarService) {
        this.taskbarService = taskbarService;
    }

}
