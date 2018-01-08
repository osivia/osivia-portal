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

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.portlet.aspects.portlet.ContextDispatcherInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.constants.InternalConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.web.portlet.context.PortletApplicationContextUtils;

/**
 * Portal simple tag abstract super-class.
 * 
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public abstract class PortalSimpleTag extends SimpleTagSupport {

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
        // Page context
        PageContext pageContext = (PageContext) this.getJspContext();
        // Servlet request
        ServletRequest servletRequest = pageContext.getRequest();
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


    /**
     * Get portal controller context.
     * 
     * @return portal controller context
     * @throws JspException
     */
    protected PortalControllerContext getPortalControllerContext() throws JspException {
        // Portlet request
        PortletRequest portletRequest = this.getPortletRequest();

        // Portal controller context
        PortalControllerContext portalControllerContext;

        if (portletRequest == null) {
            // Page context
            PageContext pageContext = (PageContext) this.getJspContext();
            // Servlet request
            ServletRequest servletRequest = pageContext.getRequest();
            // Controller context
            ControllerContext controllerContext = (ControllerContext) servletRequest.getAttribute(InternalConstants.ATTR_CONTROLLER_CONTEXT);

            portalControllerContext = new PortalControllerContext(controllerContext);
        } else {
            // Portlet response
            PortletResponse portletResponse = this.getPortletResponse();
            // Portlet context
            PortletContext portletContext = this.getPortletContext();

            portalControllerContext = new PortalControllerContext(portletContext, portletRequest, portletResponse);
        }

        return portalControllerContext;
    }


    /**
     * Get application context.
     * 
     * @return application context
     * @throws JspException
     */
    protected ApplicationContext getApplicationContext() throws JspException {
        // Portlet context
        PortletContext portletContext = this.getPortletContext();

        // Application context
        ApplicationContext applicationContext;
        if (portletContext == null) {
            applicationContext = null;
        } else {
            applicationContext = PortletApplicationContextUtils.getWebApplicationContext(portletContext);
        }

        return applicationContext;
    }

}
