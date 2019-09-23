package org.osivia.portal.api.portlet;

import java.util.Enumeration;

import javax.portlet.PortletConfig;
import javax.servlet.ServletContext;

import org.osivia.portal.api.Constants;
import org.springframework.web.context.WebApplicationContext;

/**
 * Application utils
 * 
 * @author Jean-Sébastien
 */

public class PortletAppUtils {

    /**
     * register the application
     * 
     * @param config
     */
    public static void registerApplication(PortletConfig config, Object applicationContext) {
        config.getPortletContext().setAttribute(Constants.PORTLET_ATTR_WEBAPP_CONTEXT + "." + config.getPortletName(), applicationContext);
    }


    /**
     * initialize the servlet application that may have been corrupted after portlet reinitialization
     * (when portlets ant servlet are in the same wars, portlet deployement erases servlet application)
     * 
     * @param config
     */

    public static void refreshServletApplicationContext(ServletContext servletContext) {
        Enumeration<String> attrNames = servletContext.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = attrNames.nextElement();
            Object attrValue = servletContext.getAttribute(attrName);
            if (attrValue instanceof WebApplicationContext) {
                if (!(attrValue instanceof AnnotationPortletApplicationContext)) {
                    servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, attrValue);
                }
            }
        }
    }

    /**
     * erase the servlet application context
     * (when portlets are redeployed without webapp global redeployment, servletContext may be not null
     * during redeployement. Yet servlet applicationcontext is considered as the parent of portlet application,
     * which modifies rules of bean bean creation)
     * The only use case is the redeployment of portal-custom-services.sar
     * 
     * @param config
     */
    
    public static void removeServletApplicationContext(ServletContext servletContext) {
        if( servletContext != null)
            servletContext.removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
         
    }

}
