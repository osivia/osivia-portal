package org.osivia.portal.core.tags;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.formatters.IFormatter;

/**
 * Formatter tag for portlets list HTML content.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class FormatterHTMLPortletsListTag extends SimpleTagSupport {

    /** Formatter. */
    private static IFormatter formatter;


    /**
     * Constructor.
     */
    public FormatterHTMLPortletsListTag() {
        super();

        if (formatter == null) {
            formatter = Locator.findMBean(IFormatter.class, "osivia:service=Interceptor,type=Command,name=AssistantPageCustomizer");
        }
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
        // Controller context
        ControllerContext controllerContext = (ControllerContext) request.getAttribute(InternalConstants.ATTR_CONTROLLER_CONTEXT);

        // Portlets list HTML content
        String content = formatter.formatHtmlPortletsList(controllerContext);

        JspWriter out = pageContext.getOut();
        out.write(content);
        out.flush();
    }

}
