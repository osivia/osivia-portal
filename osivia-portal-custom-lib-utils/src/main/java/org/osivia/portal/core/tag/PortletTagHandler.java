package org.osivia.portal.core.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.BooleanUtils;
import org.osivia.portal.core.constants.InternalConstants;

/**
 * Portlet tag handler.
 *
 * @author Cédric Krommenhoek
 * @see org.jboss.portal.theme.tag.PortletTagHandler
 */
public class PortletTagHandler extends org.jboss.portal.theme.tag.PortletTagHandler {


    /**
     * Default constructor.
     */
    public PortletTagHandler() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) this.getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        Boolean layoutParsing = (Boolean) request.getAttribute(InternalConstants.ATTR_LAYOUT_PARSING);
        if (BooleanUtils.isNotTrue(layoutParsing)) {
            super.doTag();
        }
    }

}
