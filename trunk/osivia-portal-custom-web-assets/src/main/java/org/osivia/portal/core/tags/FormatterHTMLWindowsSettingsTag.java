package org.osivia.portal.core.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.formatters.IFormatter;

/**
 * Formatter tag for windows settings HTML content.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class FormatterHTMLWindowsSettingsTag extends SimpleTagSupport {

    /** Formatter. */
    private static IFormatter formatter;


    /**
     * Constructor.
     */
    public FormatterHTMLWindowsSettingsTag() {
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
        // Current page
        Page currentPage = (Page) request.getAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE);
        // Windows
        @SuppressWarnings("unchecked")
        List<Window> windows = (List<Window>) request.getAttribute(InternalConstants.ATTR_WINDOWS_CURRENT_LIST);
        // Controller context
        ControllerContext controllerContext = (ControllerContext) request.getAttribute(InternalConstants.ATTR_CONTROLLER_CONTEXT);

        // Windows settings HTML content
        String content = formatter.formatHtmlWindowsSettings(currentPage, windows, controllerContext);

        JspWriter out = pageContext.getOut();
        out.write(content);
        out.flush();
    }

}
