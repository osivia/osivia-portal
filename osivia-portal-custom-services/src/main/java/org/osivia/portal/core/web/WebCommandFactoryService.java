package org.osivia.portal.core.web;

import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.NoSuchResourceException;
import org.jboss.portal.core.controller.SecurityException;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.jboss.portal.core.portlet.PortletRequestDecoder;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.cms.spi.ICMSIntegration;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.management.RuntimeErrorException;


public class WebCommandFactoryService extends AbstractCommandFactory  {


    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }


    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {
        String cmsPath = null;


        String toAnalize = requestPath;


        ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();


        /* Standard decoding */

        toAnalize = requestPath;


        try {


            cmsPath = toAnalize;


            WebCommand cmsCommand = new WebCommand(cmsPath);


            if (parameterMap != null) {
                try {
                    if (parameterMap.get(InternalConstants.PORTAL_WEB_URL_PARAM_WINDOW) != null) {
                        cmsCommand.setWindowName(URLDecoder.decode(parameterMap.get(InternalConstants.PORTAL_WEB_URL_PARAM_WINDOW)[0], "UTF-8"));
                    }
                } catch (UnsupportedEncodingException e) {
                    // ignore
                }

            }

            // Remove implicit parameters
            parameterMap.remove(InternalConstants.PORTAL_WEB_URL_PARAM_WINDOW);
            parameterMap.remove(InternalConstants.PORTAL_WEB_URL_PARAM_PAGEMARKER);

            return cmsCommand;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}
