package org.osivia.portal.api.portlet;

import javax.portlet.PortletConfig;

import org.osivia.portal.api.Constants;

/**
 * Application utils
 * 
 * @author Jean-Sébastien
 */

public class PortletAppUtils {
    /**
     * register the application
     * @param config
     */
    public static void registerApplication( PortletConfig config, Object applicationContext)   {
        config.getPortletContext().setAttribute(Constants.PORTLET_ATTR_WEBAPP_CONTEXT + "." + config.getPortletName(), applicationContext);
    }

}
