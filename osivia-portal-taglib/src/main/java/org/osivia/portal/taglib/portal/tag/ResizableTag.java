package org.osivia.portal.taglib.portal.tag;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import org.dom4j.Element;
import org.dom4j.io.HTMLWriter;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.ui.IResizableService;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.taglib.common.PortalBodyTag;

/**
 * jQuery UI resizable component tag.
 * 
 * @author Cédric Krommenhoek
 * @see PortalBodyTag
 */
public class ResizableTag extends PortalBodyTag {

    /** Default serial version identifier. */
    private static final long serialVersionUID = 1L;


    /** Enabled resizing indicator. */
    private Boolean enabled;
    /** Linked to tasks indicator. */
    private Boolean linkedToTasks;
    /** CSS class. */
    private String cssClass;
    /** Min width. */
    private Integer minWidth;
    /** Max width. */
    private Integer maxWidth;


    /** Resizable service. */
    private final IResizableService resizableService;


    /**
     * Constructor.
     */
    public ResizableTag() {
        super();

        // Resizable service
        this.resizableService = Locator.findMBean(IResizableService.class, IResizableService.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int doEndTag() throws JspException {
        // Servlet request
        ServletRequest servletRequest = this.pageContext.getRequest();
        // Controller context
        ControllerContext controllerContext = (ControllerContext) servletRequest.getAttribute(InternalConstants.ATTR_CONTROLLER_CONTEXT);
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

        // Resizable container DOM element
        Element container;
        try {
            container = this.resizableService.getTagContent(portalControllerContext, this.enabled, this.linkedToTasks, this.bodyContent.getString(),
                    this.cssClass, this.minWidth, this.maxWidth);
        } catch (PortalException e) {
            throw new JspException(e);
        }

        // HTML writer
        HTMLWriter htmlWriter = new HTMLWriter(this.pageContext.getOut());
        htmlWriter.setEscapeText(false);
        try {
            htmlWriter.write(container);
        } catch (IOException e) {
            throw new JspException(e);
        }

        return EVAL_PAGE;
    }


    /**
     * Setter for enabled.
     * 
     * @param enabled the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Setter for linkedToTasks.
     * 
     * @param linkedToTasks the linkedToTasks to set
     */
    public void setLinkedToTasks(Boolean linkedToTasks) {
        this.linkedToTasks = linkedToTasks;
    }

    /**
     * Setter for cssClass.
     * 
     * @param cssClass the cssClass to set
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * Setter for minWidth.
     * 
     * @param minWidth the minWidth to set
     */
    public void setMinWidth(Integer minWidth) {
        this.minWidth = minWidth;
    }

    /**
     * Setter for maxWidth.
     * 
     * @param maxWidth the maxWidth to set
     */
    public void setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
    }

}
