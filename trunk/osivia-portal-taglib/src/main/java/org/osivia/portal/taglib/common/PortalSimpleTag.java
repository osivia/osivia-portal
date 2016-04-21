package org.osivia.portal.taglib.common;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.jboss.portal.portlet.aspects.portlet.ContextDispatcherInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;

/**
 * Portal simple tag.
 * 
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class PortalSimpleTag extends SimpleTagSupport {

    /**
     * Constructor.
     */
    public PortalSimpleTag() {
        super();
    }


    /**
     * Get portlet request.
     * 
     * @return portlet request
     * @throws JspException
     */
    protected PortletRequest getPortletRequest() throws JspException {
        try {
            // Dispatched request
            HttpServletRequest dispatchedRequest = getDispatchedRequest();
            // Portlet request
            return (PortletRequest) dispatchedRequest.getAttribute("javax.portlet.request");
        } catch (Exception e) {
            throw new JspException("Cannot get portlet request.", e);
        }
    }


    /**
     * Get portlet response.
     * 
     * @return portlet response
     * @throws JspException
     */
    protected PortletResponse getPortletResponse() throws JspException {
        try {
            // Dispatched request
            HttpServletRequest dispatchedRequest = getDispatchedRequest();
            // Portlet request
            return (PortletResponse) dispatchedRequest.getAttribute("javax.portlet.response");
        } catch (Exception e) {
            throw new JspException("Cannot get portlet response.", e);
        }
    }


    /**
     * Get portlet config.
     * 
     * @return portlet config
     * @throws JspException
     */
    protected PortletConfig getPortletConfig() throws JspException {
        try {
            // Dispatched request
            HttpServletRequest dispatchedRequest = getDispatchedRequest();
            // Portlet request
            return (PortletConfig) dispatchedRequest.getAttribute("javax.portlet.config");
        } catch (Exception e) {
            throw new JspException("Cannot get portlet config.", e);
        }
    }


    /**
     * Get portlet context.
     * 
     * @return portlet context
     * @throws JspException
     */
    protected PortletContext getPortletContext() throws JspException {
        try {
            // Portlet config
            PortletConfig portletConfig = this.getPortletConfig();
            // Portlet context
            return portletConfig.getPortletContext();
        } catch (Exception e) {
            throw new JspException("Cannot get portlet context.", e);
        }
    }


    /**
     * Get dispatched request.
     * 
     * @return dispatched request
     */
    private HttpServletRequest getDispatchedRequest() {
        // Page context
        PageContext pageContext = (PageContext) this.getJspContext();
        // Servlet request
        ServletRequest servletRequest = pageContext.getRequest();
        // Portlet invocation
        PortletInvocation invocation = (PortletInvocation) servletRequest.getAttribute(ContextDispatcherInterceptor.REQ_ATT_COMPONENT_INVOCATION);
        // Dispatched request
        return invocation.getDispatchedRequest();
    }

}
