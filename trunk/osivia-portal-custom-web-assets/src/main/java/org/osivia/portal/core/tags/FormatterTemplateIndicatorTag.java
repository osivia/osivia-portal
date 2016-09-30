package org.osivia.portal.core.tags;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.jboss.portal.core.model.portal.Page;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Formatter tag for template indicator.
 * 
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class FormatterTemplateIndicatorTag extends SimpleTagSupport {

    /**
     * Defaut constructor.
     */
    public FormatterTemplateIndicatorTag() {
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
        // Current page
        Page currentPage = (Page) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE);

        // Template indicator
        boolean isTemplate = PortalObjectUtils.isTemplate(currentPage);

        JspWriter out = pageContext.getOut();
        out.write(String.valueOf(isTemplate));
    }

}
