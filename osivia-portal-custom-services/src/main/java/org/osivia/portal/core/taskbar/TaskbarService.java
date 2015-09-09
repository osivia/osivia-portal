package org.osivia.portal.core.taskbar;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.taskbar.TaskbarPlayer;
import org.osivia.portal.api.taskbar.TaskbarState;
import org.osivia.portal.api.taskbar.TaskbarStatus;
import org.osivia.portal.api.taskbar.TaskbarTask;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Taskbar service implementation.
 *
 * @author Cédric Krommenhoek
 * @see ITaskbarService
 */
public class TaskbarService implements ITaskbarService {

    /** Taskbar window style property name. */
    private static final String STYLE_PROPERTY = "osivia.style";


    /** Dynamic object container. */
    private IDynamicObjectContainer dynamicObjectContainer;
    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public TaskbarService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public List<TaskbarTask> getNavigationTasks(PortalControllerContext portalControllerContext, String basePath, String currentPath) throws PortalException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);


        // Navigation tasks
        List<TaskbarTask> navigationTasks;
        try {
            navigationTasks = cmsService.getTaskbarNavigationTasks(cmsContext, basePath, currentPath);
        } catch (CMSException e) {
            throw new PortalException(e);
        }

        return navigationTasks;
    }


    /**
     * {@inheritDoc}
     */
    public List<TaskbarTask> getCustomTasks(PortalControllerContext portalControllerContext) {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // Custom tasks
        return cmsService.getTaskbarCustomTasks(cmsContext);
    }


    /**
     * {@inheritDoc}
     */
    public String getActiveId(PortalControllerContext portalControllerContext, List<? extends TaskbarTask> tasks) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Page
        Page page = PortalObjectUtils.getPage(controllerContext);

        // Active task identifier
        String activeId = null;

        Collection<PortalObject> portalObjects = page.getChildren(PortalObject.WINDOW_MASK);
        for (PortalObject portalObject : portalObjects) {
            Window window = (Window) portalObject;
            if (PLAYER_WINDOW_NAME.equals(window.getName())) {
                activeId = window.getDeclaredProperty(TASK_ID_WINDOW_PROPERTY);
                break;
            }
        }

        if (activeId == null) {
            activeId = this.getSelectedId(controllerContext, tasks, page);
        }

        return activeId;
    }


    /**
     * Get selected task identifier.
     *
     * @param controllerContext controller context
     * @param tasks tasks
     * @param page page
     * @return selected task identifier
     */
    private String getSelectedId(ControllerContext controllerContext, List<? extends TaskbarTask> tasks, Page page) {
        // Selected task identifier
        String selectedId = null;

        // Maximized window
        Window maximizedWindow = TaskbarUtils.getMaximizedWindow(controllerContext, page);

        if ((maximizedWindow != null) && !"1".equals(maximizedWindow.getDeclaredProperty("osivia.cms.contextualization"))) {
            selectedId = maximizedWindow.getDeclaredProperty(ITaskbarService.TASK_ID_WINDOW_PROPERTY);
        } else {
            // Base path
            String basePath = page.getProperty("osivia.cms.basePath");
            // Current path
            String currentPath = TaskbarUtils.getCurrentPath(controllerContext, page);

            if (StringUtils.equals(currentPath, basePath)) {
                selectedId = ITaskbarService.HOME_TASK_ID;
            } else {
                // Protected current path
                String protectedCurrentPath = currentPath + "/";

                for (TaskbarTask task : tasks) {
                    if (!ITaskbarService.HOME_TASK_ID.equals(task.getId()) && (task.getPath() != null)) {
                        String protectedPath = task.getPath() + "/";
                        if (StringUtils.startsWith(protectedCurrentPath, protectedPath)) {
                            selectedId = task.getId();
                            break;
                        }
                    }
                }
            }
        }

        return selectedId;
    }


    /**
     * {@inheritDoc}
     */
    public void addWindow(PortalControllerContext portalControllerContext, TaskbarPlayer player, String id) {
        // Window properties
        Map<String, String> properties;
        if (player.getProperties() == null) {
            properties = new HashMap<String, String>(1);
        } else {
            properties = new HashMap<String, String>(player.getProperties());
        }

        // Style property
        String style = properties.get(STYLE_PROPERTY);
        if (style == null) {
            style = "taskbar-window";
        } else {
            style += " taskbar-window";
        }
        properties.put(STYLE_PROPERTY, style);

        this.addDynamicWindow(portalControllerContext, player.getInstance(), properties, id);
    }


    /**
     * {@inheritDoc}
     */
    public void addEmptyWindow(PortalControllerContext portalControllerContext, String id) {
        Map<String, String> windowProperties = new HashMap<String, String>(1);
        windowProperties.put(STYLE_PROPERTY, "hidden");
        this.addDynamicWindow(portalControllerContext, EMPTY_WINDOW_INSTANCE, windowProperties, id);
    }


    /**
     * Add dynamic window.
     *
     * @param portalControllerContext portal controller context
     * @param instance portlet instance name
     * @param properties window properties
     * @param id task identifier
     */
    private void addDynamicWindow(PortalControllerContext portalControllerContext, String instance, Map<String, String> properties, String id) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Page identifier
        PortalObjectId pageId = PortalObjectUtils.getPageId(controllerContext);

        if (pageId != null) {
            // Window properties
            Map<String, String> windowProperties = new HashMap<String, String>(properties);
            windowProperties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
            windowProperties.put(ThemeConstants.PORTAL_PROP_REGION, ITaskbarService.PLAYER_REGION_NAME);
            windowProperties.put("osivia.hideTitle", "1");
            windowProperties.put(TASK_ID_WINDOW_PROPERTY, id);

            // Page marker
            String pageMarker = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");

            // Dynamic window bean
            DynamicWindowBean window = new DynamicWindowBean(pageId, PLAYER_WINDOW_NAME, instance, windowProperties, pageMarker);

            this.dynamicObjectContainer.addDynamicWindow(window);

            // Suppression du cache
            StringBuilder builder = new StringBuilder();
            builder.append("cached_markup.");
            builder.append(pageId.toString());
            builder.append("/");
            builder.append(PLAYER_WINDOW_NAME);
            controllerContext.removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, builder.toString());
        }
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarState getTaskbarState(PortalControllerContext portalControllerContext) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Taskbar state
        TaskbarState state = null;

        // Taskbar status
        TaskbarStatus taskbarStatus = (TaskbarStatus) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, ITaskbarService.STATUS_PRINCIPAL_ATTRIBUTE);
        if (taskbarStatus != null) {
            String id = this.getStateId(controllerContext);
            state = taskbarStatus.getStates().get(id);
        }

        return state;
    }


    /**
     * {@inheritDoc}
     */
    public void setTaskbarState(PortalControllerContext portalControllerContext, TaskbarState state) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Taskbar status
        TaskbarStatus taskbarStatus = (TaskbarStatus) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, ITaskbarService.STATUS_PRINCIPAL_ATTRIBUTE);
        if (taskbarStatus == null) {
            taskbarStatus = new TaskbarStatus();
            controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, ITaskbarService.STATUS_PRINCIPAL_ATTRIBUTE, taskbarStatus);
        }

        String id = this.getStateId(controllerContext);
        taskbarStatus.getStates().put(id, state);
    }


    /**
     * Get state identifier.
     *
     * @param controllerContext controller context
     * @return state identifier
     */
    private String getStateId(ControllerContext controllerContext) {
        // Page
        Page page = PortalObjectUtils.getPage(controllerContext);

        return page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
    }


    /**
     * Setter for dynamicObjectContainer.
     *
     * @param dynamicObjectContainer the dynamicObjectContainer to set
     */
    public void setDynamicObjectContainer(IDynamicObjectContainer dynamicObjectContainer) {
        this.dynamicObjectContainer = dynamicObjectContainer;
    }

    /**
     * Setter for cmsServiceLocator.
     *
     * @param cmsServiceLocator the cmsServiceLocator to set
     */
    public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
        this.cmsServiceLocator = cmsServiceLocator;
    }

}