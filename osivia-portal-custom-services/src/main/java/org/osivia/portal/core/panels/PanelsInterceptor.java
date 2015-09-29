package org.osivia.portal.core.panels;

import org.apache.commons.lang.BooleanUtils;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.content.Content;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.panels.IPanelsService;
import org.osivia.portal.api.panels.Panel;
import org.osivia.portal.api.panels.PanelPlayer;
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

            // Hidden panel indicator
            Boolean hidden = this.panelsService.isHidden(portalControllerContext, Panel.NAVIGATION_PANEL);
            if (BooleanUtils.isTrue(hidden) || ((hidden == null) && navigationPlayer.isHidden())) {
                this.panelsService.hidePanel(portalControllerContext, Panel.NAVIGATION_PANEL);
            }
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
