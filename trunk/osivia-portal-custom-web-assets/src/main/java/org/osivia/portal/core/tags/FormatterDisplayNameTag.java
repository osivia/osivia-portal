package org.osivia.portal.core.tags;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.jboss.portal.core.model.portal.PortalObject;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Formatter tag for portal object display name.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class FormatterDisplayNameTag extends SimpleTagSupport {

    /** Portal object. */
    private PortalObject object;


    /**
     * Default constructor.
     */
    public FormatterDisplayNameTag() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Context
        PageContext pageContext = (PageContext) this.getJspContext();
        // Request
        ServletRequest request = pageContext.getRequest();

        // Display name
        String displayName = PortalObjectUtils.getDisplayName(this.object, request.getLocale());

        JspWriter out = pageContext.getOut();
        out.write(displayName);
    }


    /**
     * Getter for object.
     *
     * @return the object
     */
    public PortalObject getObject() {
        return this.object;
    }

    /**
     * Setter for object.
     *
     * @param object the object to set
     */
    public void setObject(PortalObject object) {
        this.object = object;
    }

}
