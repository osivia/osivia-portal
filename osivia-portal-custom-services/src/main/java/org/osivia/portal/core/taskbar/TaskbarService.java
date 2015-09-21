package org.osivia.portal.core.taskbar;

import java.util.List;

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
import org.osivia.portal.api.taskbar.TaskbarTask;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Taskbar service implementation.
 *
 * @author Cédric Krommenhoek
 * @see ITaskbarService
 */
public class TaskbarService implements ITaskbarService {

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
    public List<TaskbarTask> getNavigationTasks(PortalControllerContext portalControllerContext, String basePath) throws PortalException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);


        // Navigation tasks
        List<TaskbarTask> navigationTasks;
        try {
            navigationTasks = cmsService.getTaskbarNavigationTasks(cmsContext, basePath);
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
    public String getActiveId(PortalControllerContext portalControllerContext) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // Request
        PortletRequest request = portalControllerContext.getRequest();

        // Active task identifier
        String activeId = null;

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
                    List<BreadcrumbItem> breadcrumbItems = breadcrumb.getChilds();
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
            } else {
                // Base path
                String basePath = page.getProperty("osivia.cms.basePath");
                // Content path
                String contentPath = request.getParameter("osivia.cms.contentPath");

                if (StringUtils.equals(contentPath, basePath)) {
                    activeId = ITaskbarService.HOME_TASK_ID;
                } else {
                    // Protected content path
                    String protectedContentPath = contentPath + "/";

                    // Navigation tasks
                    List<TaskbarTask> navigationTasks = this.getNavigationTasks(portalControllerContext, basePath);

                    for (TaskbarTask navigationTask : navigationTasks) {
                        String protectedPath = navigationTask.getPath() + "/";
                        if (StringUtils.startsWith(protectedContentPath, protectedPath)) {
                            activeId = navigationTask.getId();
                            break;
                        }
                    }
                }
            }
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
