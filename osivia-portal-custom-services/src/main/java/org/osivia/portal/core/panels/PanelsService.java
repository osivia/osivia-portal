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
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.panels.IPanelsService;
import org.osivia.portal.api.panels.Panel;
import org.osivia.portal.api.panels.PanelPlayer;
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


    /**
     * Constructor.
     */
    public PanelsService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public void openPanel(PortalControllerContext portalControllerContext, Panel panel, PanelPlayer player) {
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

            // HTTP servlet request
            HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();
            request.setAttribute(panel.getClosedAttribute(), false);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void closePanel(PortalControllerContext portalControllerContext, Panel panel) {
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
        }

        // HTTP servlet request
        HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();
        request.setAttribute(panel.getClosedAttribute(), true);
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

}
