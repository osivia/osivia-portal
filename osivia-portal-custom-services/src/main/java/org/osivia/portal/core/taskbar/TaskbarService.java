package org.osivia.portal.core.taskbar;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.portlet.PortletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.panels.IPanelsService;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.taskbar.TaskbarFactory;
import org.osivia.portal.api.taskbar.TaskbarItem;
import org.osivia.portal.api.taskbar.TaskbarItems;
import org.osivia.portal.api.taskbar.TaskbarTask;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.page.PagePathUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Taskbar service implementation.
 *
 * @author Cédric Krommenhoek
 * @see ITaskbarService
 */
public class TaskbarService implements ITaskbarService {

    /** Taskbar active task identifier attribute name. */
    private static final String ACTIVE_ID_ATTRIBUTE = "osivia.taskbar.active.id";


    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;

    /** Taskbar item factory. */
    private final TaskbarFactory factory;


    /**
     * Constructor.
     */
    public TaskbarService() {
        super();
        this.factory = new TaskbarFactoryImpl();
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarFactory getFactory() {
        return this.factory;
    }


    /**
     * {@inheritDoc}
     */
    public TaskbarItems getItems(PortalControllerContext portalControllerContext) throws PortalException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // Taskbar items
        TaskbarItems taskbarItems;
        try {
            taskbarItems = cmsService.getTaskbarItems(cmsContext);
        } catch (CMSException e) {
            throw new PortalException(e);
        }

        return taskbarItems;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TaskbarItem getItem(PortalControllerContext portalControllerContext, String id) throws PortalException {
        // Items
        TaskbarItems items = this.getItems(portalControllerContext);

        return items.get(id);
    }


    /**
     * {@inheritDoc}
     */
    public SortedSet<TaskbarItem> getDefaultItems(PortalControllerContext portalControllerContext) throws PortalException {
        // Comparator
        Comparator<? super TaskbarItem> comparator = new TaskbarItemComparator();
        // Default items
        SortedSet<TaskbarItem> defaultItems = new TreeSet<TaskbarItem>(comparator);

        // Items
        List<TaskbarItem> items = this.getItems(portalControllerContext).getAll();
        for (TaskbarItem item : items) {
            if (item.isDefault()) {
                defaultItems.add(item);
            }
        }

        return defaultItems;
    }


    /**
     * {@inheritDoc}
     */
    public List<TaskbarTask> getTasks(PortalControllerContext portalControllerContext, String basePath, boolean navigation) throws PortalException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);
        cmsContext.setDisplayLiveVersion("1");

        // Tasks
        List<TaskbarTask> tasks;
        try {
            tasks = cmsService.getTaskbarTasks(cmsContext, basePath, navigation);
        } catch (CMSException e) {
            throw new PortalException(e);
        }

        return tasks;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public TaskbarTask getTask(PortalControllerContext portalControllerContext, String basePath, String id) throws PortalException {
        TaskbarTask result = null;
        
        // Tasks
        List<TaskbarTask> tasks = this.getTasks(portalControllerContext, basePath, false);
        
        for (TaskbarTask task : tasks) {
            if (StringUtils.equals(id, task.getId())) {
                result = task;
                break;
            }
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    public String getActiveId(PortalControllerContext portalControllerContext) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Get active task identifier in request scope
        String activeId = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, ACTIVE_ID_ATTRIBUTE);

        if (activeId == null) {
            // Request
            PortletRequest request = portalControllerContext.getRequest();

            // Page
            Page page = (Page) controllerContext.getAttribute(Scope.REQUEST_SCOPE, IPanelsService.PAGE_REQUEST_ATTRIBUTE);
            if (page == null) {
                page = PortalObjectUtils.getPage(controllerContext);
            }
            if (page != null) {
                // Maximized window
                Window maximizedWindow = PortalObjectUtils.getMaximizedWindow(controllerContext, page);

                if ((maximizedWindow != null) && !"1".equals(maximizedWindow.getDeclaredProperty("osivia.cms.contextualization"))) {
                    activeId = maximizedWindow.getDeclaredProperty(ITaskbarService.TASK_ID_WINDOW_PROPERTY);
                    if (activeId == null) {
                        // Breadcrumb
                        Breadcrumb breadcrumb = (Breadcrumb) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb");
                        List<BreadcrumbItem> breadcrumbItems = breadcrumb.getChildren();
                        if (CollectionUtils.isNotEmpty(breadcrumbItems)) {
                            for (int i = breadcrumbItems.size() - 1; i >= 0; i--) {
                                BreadcrumbItem breadcrumbItem = breadcrumbItems.get(i);
                                activeId = breadcrumbItem.getTaskId();

                                if (activeId != null) {
                                    break;
                                }
                            }
                        }
                    }
                }

                if (activeId == null) {
                    // Base path
                    String basePath = page.getProperty("osivia.cms.basePath");

                    if (StringUtils.isNotEmpty(basePath)) {
                        // Content path
                        String contentPath;
                        if (request != null) {
                            contentPath = request.getParameter("osivia.cms.contentPath");
                        } else {
                            contentPath = PagePathUtils.getContentPath(controllerContext, page.getId());
                        }
                        // Navigation path
                        String navigationPath;
                        if (request != null) {
                            navigationPath = request.getParameter("osivia.cms.path");
                        } else {
                            navigationPath = PagePathUtils.getNavigationPath(controllerContext, page.getId());
                        }
                        // Task path
                        String taskPath;
                        if (StringUtils.startsWith(contentPath, basePath)) {
                            taskPath = contentPath;
                        } else {
                            // Virtual navigation path
                            taskPath = navigationPath;
                        }

                        if (StringUtils.equals(taskPath, basePath)) {
                            activeId = ITaskbarService.HOME_TASK_ID;
                        } else {
                            // Protected task path
                            String protectedTaskPath = taskPath + "/";

                            // Navigation tasks
                            List<TaskbarTask> navigationTasks = this.getTasks(portalControllerContext, basePath, true);

                            for (TaskbarTask navigationTask : navigationTasks) {
                                String protectedPath = navigationTask.getPath() + "/";
                                if (StringUtils.startsWith(protectedTaskPath, protectedPath)) {
                                    activeId = navigationTask.getId();
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // Save active task identifier in request scope
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, ACTIVE_ID_ATTRIBUTE, activeId);
        }

        return activeId;
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
