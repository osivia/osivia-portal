package org.osivia.portal.core.taskbar;

import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.panels.IPanelsService;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.taskbar.TaskbarTask;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
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
    public String getActiveId(PortalControllerContext portalControllerContext) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

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
            } else {
                // Base path
                String basePath = page.getProperty("osivia.cms.basePath");
                // Current path
                String currentPath = this.getCurrentPath(controllerContext, page);

                if (StringUtils.equals(currentPath, basePath)) {
                    activeId = ITaskbarService.HOME_TASK_ID;
                } else {
                    // Protected current path
                    String protectedCurrentPath = currentPath + "/";

                    // Navigation tasks
                    List<TaskbarTask> navigationTasks = this.getNavigationTasks(portalControllerContext, basePath, currentPath);

                    for (TaskbarTask navigationTask : navigationTasks) {
                        String protectedPath = navigationTask.getPath() + "/";
                        if (StringUtils.startsWith(protectedCurrentPath, protectedPath)) {
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
     * Get current path.
     *
     * @param controllerContext controller context
     * @param page page
     * @return current path
     */
    private String getCurrentPath(ControllerContext controllerContext, Page page) {
        // State context
        NavigationalStateContext stateContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        // Current page state
        PageNavigationalState pageState = stateContext.getPageNavigationalState(page.getId().toString());

        // Current path
        String currentPath = null;
        if (pageState != null) {
            String[] sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, InternalConstants.ATTR_CMS_PATH));
            if (ArrayUtils.isNotEmpty(sPath)) {
                currentPath = sPath[0];
            }
        }

        return currentPath;
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
