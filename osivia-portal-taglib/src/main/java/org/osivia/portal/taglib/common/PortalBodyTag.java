package org.osivia.portal.taglib.common;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.jboss.portal.portlet.aspects.portlet.ContextDispatcherInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;

/**
 * Portal body tag abstract super-class.
 * 
 * @author Cédric Krommenhoek
 * @see BodyTagSupport
 */
public abstract class PortalBodyTag extends BodyTagSupport {

    /** Default serial version identifier. */
    private static final long serialVersionUID = 1L;


    /**
     * Constructor.
     */
    public PortalBodyTag() {
        super();
    }


    /**
     * Get portlet request.
     * 
     * @return portlet request
     * @throws JspException
     */
    protected PortletRequest getPortletRequest() throws JspException {
        // Portlet request
        PortletRequest request;

        try {
            // HTTP servlet request
            HttpServletRequest httpServletRequest = getHttpServletRequest();

            if (httpServletRequest == null) {
                request = null;
            } else {
                request = (PortletRequest) httpServletRequest.getAttribute("javax.portlet.request");
            }
        } catch (Exception e) {
            throw new JspException("Cannot get portlet request.", e);
        }

        return request;
    }


    /**
     * Get portlet response.
     * 
     * @return portlet response
     * @throws JspException
     */
    protected PortletResponse getPortletResponse() throws JspException {
        // Portlet response
        PortletResponse response;

        try {
            // HTTP servlet request
            HttpServletRequest httpServletRequest = getHttpServletRequest();

            if (httpServletRequest == null) {
                response = null;
            } else {
                response = (PortletResponse) httpServletRequest.getAttribute("javax.portlet.response");
            }
        } catch (Exception e) {
            throw new JspException("Cannot get portlet response.", e);
        }

        return response;
    }


    /**
     * Get portlet config.
     * 
     * @return portlet config
     * @throws JspException
     */
    protected PortletConfig getPortletConfig() throws JspException {
        // Portlet config
        PortletConfig portletConfig;

        try {
            // HTTP servlet request
            HttpServletRequest httpServletRequest = getHttpServletRequest();

            if (httpServletRequest == null) {
                portletConfig = null;
            } else {
                portletConfig = (PortletConfig) httpServletRequest.getAttribute("javax.portlet.config");
            }
        } catch (Exception e) {
            throw new JspException("Cannot get portlet config.", e);
        }

        return portletConfig;
    }


    /**
     * Get portlet context.
     * 
     * @return portlet context
     * @throws JspException
     */
    protected PortletContext getPortletContext() throws JspException {
        // Portlet context
        PortletContext portletContext;

        try {
            // Portlet config
            PortletConfig portletConfig = this.getPortletConfig();

            if (portletConfig == null) {
                portletContext = null;
            } else {
                portletContext = portletConfig.getPortletContext();
            }
        } catch (Exception e) {
            throw new JspException("Cannot get portlet context.", e);
        }

        return portletContext;
    }


    /**
     * Get HTTP servlet request.
     * 
     * @return HTTP servlet request
     */
    private HttpServletRequest getHttpServletRequest() {
        // Servlet request
        ServletRequest servletRequest = this.pageContext.getRequest();
        // Portlet invocation
        PortletInvocation invocation = (PortletInvocation) servletRequest.getAttribute(ContextDispatcherInterceptor.REQ_ATT_COMPONENT_INVOCATION);

        // HTTP servlet request
        HttpServletRequest httpServletRequest;
        if (invocation == null) {
            httpServletRequest = null;
        } else {
            httpServletRequest = invocation.getDispatchedRequest();
        }

        return httpServletRequest;
    }

}
