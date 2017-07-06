package org.osivia.portal.core.panels;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.panels.IPanelsService;
import org.osivia.portal.api.panels.Panel;
import org.osivia.portal.api.panels.PanelPlayer;
import org.osivia.portal.api.portlet.IPortletStatusService;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;

/**
 * Panels service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IPanelsService
 */
public class PanelsService implements IPanelsService {

    /** Dynamic object container. */
    private IDynamicObjectContainer dynamicObjectContainer;
    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;
    /** Portlet status service. */
    private IPortletStatusService portletStatusService;
    /** Taskbar service. */
    private ITaskbarService taskbarService;


    /**
     * Constructor.
     */
    public PanelsService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void openPanel(PortalControllerContext portalControllerContext, Panel panel, PanelPlayer player) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Page identifier
        Page page = (Page) controllerContext.getAttribute(Scope.REQUEST_SCOPE, PAGE_REQUEST_ATTRIBUTE);
        PortalObjectId pageId = null;
        if (page != null) {
            pageId = page.getId();
        }

        if (pageId != null) {
            StringBuilder builder;

            // Window properties
            Map<String, String> windowProperties = new HashMap<String, String>();
            if (player.getProperties() != null) {
                windowProperties.putAll(player.getProperties());
            }
            windowProperties.put(ThemeConstants.PORTAL_PROP_ORDER, "1");
            windowProperties.put(ThemeConstants.PORTAL_PROP_REGION, panel.getRegionName());
            windowProperties.put("osivia.hideTitle", "1");

            // Style
            String style = windowProperties.get("osivia.style");
            builder = new StringBuilder();
            if (StringUtils.isNotBlank(style)) {
                builder.append(style);
                builder.append(",");
            }
            builder.append(panel.getRegionName());
            windowProperties.put("osivia.style", builder.toString());


            // Page marker
            String pageMarker = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");

            // Dynamic window bean
            DynamicWindowBean window = new DynamicWindowBean(pageId, panel.getWindowName(), player.getInstance(), windowProperties, pageMarker);

            this.dynamicObjectContainer.addDynamicWindow(window);

            // Suppression du cache
            builder = new StringBuilder();
            builder.append("cached_markup.");
            builder.append(pageId.toString());
            builder.append("/");
            builder.append(panel.getWindowName());
            controllerContext.removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, builder.toString());


            // Update panel status
            this.updatePanelStatus(portalControllerContext, panel, null);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void closePanel(PortalControllerContext portalControllerContext, Panel panel) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Page identifier
        Page page = (Page) controllerContext.getAttribute(Scope.REQUEST_SCOPE, PAGE_REQUEST_ATTRIBUTE);
        PortalObjectId pageId = null;
        if (page != null) {
            pageId = page.getId();
        }

        if (pageId != null) {
            // Window identifier
            StringBuilder builder = new StringBuilder();
            builder.append(pageId.getPath().toString());
            builder.append("/");
            builder.append(panel.getWindowName());
            PortalObjectPath windowPath = new PortalObjectPath(builder.toString(), PortalObjectPath.CANONICAL_FORMAT);
            PortalObjectId windowId = new PortalObjectId(StringUtils.EMPTY, windowPath);

            this.dynamicObjectContainer.removeDynamicWindow(windowId.toString(PortalObjectPath.SAFEST_FORMAT));


            // Update panel status
            this.updatePanelStatus(portalControllerContext, panel, true);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void showPanel(PortalControllerContext portalControllerContext, Panel panel) throws PortalException {
        this.updatePanelStatus(portalControllerContext, panel, false);
    }


    /**
     * {@inheritDoc}
     */
    public void hidePanel(PortalControllerContext portalControllerContext, Panel panel) throws PortalException {
        this.updatePanelStatus(portalControllerContext, panel, true);
    }


    /**
     * Update panel status.
     *
     * @param portalControllerContext portal controller context
     * @param panel panel
     * @param hidden hidden panel indicator
     * @throws PortalException
     */
    private void updatePanelStatus(PortalControllerContext portalControllerContext, Panel panel, Boolean hidden) throws PortalException {
        // Active task identifier
        String taskId = this.taskbarService.getActiveId(portalControllerContext);

        // Panel status
        PanelStatus panelStatus = this.portletStatusService.getStatus(portalControllerContext, panel.getWindowName(), PanelStatus.class);
        if (panelStatus == null) {
            panelStatus = new PanelStatus(taskId);
            this.portletStatusService.setStatus(portalControllerContext, panel.getWindowName(), panelStatus);
        }

        // Hidden panel indicator
        if (hidden != null) {
            panelStatus.setHidden(hidden);
        }

        // HTTP servlet request
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        HttpServletRequest httpServletRequest = controllerContext.getServerInvocation().getServerContext().getClientRequest();
        httpServletRequest.setAttribute(Panel.NAVIGATION_PANEL.getClosedAttribute(), panelStatus.isHidden());
    }


    /**
     * {@inheritDoc}
     */
    public Boolean isHidden(PortalControllerContext portalControllerContext, Panel panel) throws PortalException {
        Boolean hidden;

        // Panel status
        PanelStatus panelStatus = this.portletStatusService.getStatus(portalControllerContext, panel.getWindowName(), PanelStatus.class);
        if (panelStatus == null) {
            hidden = null;
        } else {
            // Active task identifier
            String taskId = this.taskbarService.getActiveId(portalControllerContext);

            if (StringUtils.equals(taskId, panelStatus.getTaskId())) {
                hidden = panelStatus.isHidden();
            } else {
                hidden = null;
            }
        }

        return hidden;
    }


    /**
     * {@inheritDoc}
     */
    public void resetTaskDependentPanels(PortalControllerContext portalControllerContext) throws PortalException {
        this.portletStatusService.resetTaskDependentStatus(portalControllerContext);
    }


    /**
     * {@inheritDoc}
     */
    public PanelPlayer getNavigationPlayer(PortalControllerContext portalControllerContext, String instance) {
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        return cmsService.getNavigationPanelPlayer(instance);
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

    /**
     * Setter for portletStatusService.
     *
     * @param portletStatusService the portletStatusService to set
     */
    public void setPortletStatusService(IPortletStatusService portletStatusService) {
        this.portletStatusService = portletStatusService;
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
