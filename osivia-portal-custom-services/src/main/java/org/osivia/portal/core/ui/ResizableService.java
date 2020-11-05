package org.osivia.portal.core.ui;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.ui.IResizableService;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * jQuery UI resizable component service implementation.
 * 
 * @author Cédric Krommenhoek
 * @see IResizableService
 */
public class ResizableService implements IResizableService {

    /** Resizable container attribute name. */
    private static final String CONTAINER_ATTRIBUTE = "osivia.resizable.container";


    /** Taskbar service. */
    private ITaskbarService taskbarService;


    /**
     * Constructor.
     */
    public ResizableService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getWidth(PortalControllerContext portalControllerContext, boolean linkedToTasks) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Current page object identifier
        PortalObjectId pageId = PortalObjectUtils.getPageId(controllerContext);
        // Current task identifier
        String taskId;
        if (linkedToTasks) {
            taskId = this.taskbarService.getActiveId(portalControllerContext);
        } else {
            taskId = null;
        }

        // Resizable width
        Integer width;

        if (pageId == null) {
            width = null;
        } else {
            // Resizable container
            ResizableContainer container;
            Object attribute = controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, CONTAINER_ATTRIBUTE);
            if (attribute instanceof ResizableContainer) {
                container = (ResizableContainer) attribute;
            } else {
                container = null;
            }
            if (container == null) {
                container = new ResizableContainer();
                controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, CONTAINER_ATTRIBUTE, container);
            }

            width = container.getWidth(pageId, taskId);
        }

        return width;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void saveWidth(PortalControllerContext portalControllerContext, boolean linkedToTasks, Integer width) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Current page object identifier
        PortalObjectId pageId = PortalObjectUtils.getPageId(controllerContext);

        if ((pageId != null) && (width != null)) {
            // Current task identifier
            String taskId;
            if (linkedToTasks) {
                taskId = this.taskbarService.getActiveId(portalControllerContext);
            } else {
                taskId = null;
            }

            // Resizable container
            ResizableContainer container = (ResizableContainer) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, CONTAINER_ATTRIBUTE);
            if (container == null) {
                container = new ResizableContainer();
                controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, CONTAINER_ATTRIBUTE, container);
            }

            container.setWidth(pageId, taskId, width);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getSaveWidthUrl(PortalControllerContext portalControllerContext, boolean linkedToTasks) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // URL context
        URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
        // URL format
        URLFormat urlFormat = URLFormat.newInstance(false, true);

        // Command
        SaveResizableWidthCommand command = new SaveResizableWidthCommand(linkedToTasks);

        return controllerContext.renderURL(command, urlContext, urlFormat);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Element getTagContent(PortalControllerContext portalControllerContext, Boolean enabled, Boolean linkedToTasks, String bodyContent,
            String htmlClasses, Integer minWidth, Integer maxWidth) throws PortalException {
        // Resizable component container DOM element
        Element container;

        if (BooleanUtils.isFalse(enabled)) {
            container = DOM4JUtils.generateDivElement(htmlClasses);
            DOM4JUtils.addText(container, bodyContent);
        } else {
            container = DOM4JUtils.generateDivElement(null);

            // Container HTML classes
            String containerHtmlClasses = htmlClasses;

            // Data attributes
            DOM4JUtils.addDataAttribute(container, "resizable", StringUtils.EMPTY);
            if (minWidth != null) {
                DOM4JUtils.addDataAttribute(container, "min-width", String.valueOf(minWidth));
            }
            if (maxWidth != null) {
                DOM4JUtils.addDataAttribute(container, "max-width", String.valueOf(maxWidth));
            }
            DOM4JUtils.addDataAttribute(container, "save-url", this.getSaveWidthUrl(portalControllerContext, BooleanUtils.isTrue(linkedToTasks)));

            // Saved resizable width
            Integer width = this.getWidth(portalControllerContext, BooleanUtils.isTrue(linkedToTasks));
            if (width != null) {
                containerHtmlClasses += " loaded";
                
                if (minWidth != null)   {
                    width = Math.max(minWidth, width);
                }

                String style = "width: " + width + "px;";
                DOM4JUtils.addAttribute(container, "style", style);
            } else if (minWidth != null) {
                String style = "min-width: " + minWidth + "px;";
                DOM4JUtils.addAttribute(container, "style", style);
            }

            DOM4JUtils.addAttribute(container, "class", containerHtmlClasses);
            DOM4JUtils.addText(container, bodyContent);
        }

        return container;
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
