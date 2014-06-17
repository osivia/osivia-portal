/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;


public class WebCommandFactoryService extends AbstractCommandFactory  {


    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }

    private static IWebIdService webIdService;

    public static IWebIdService getWebIdService() throws Exception {

        if (webIdService == null) {
            webIdService = Locator.findMBean(IWebIdService.class, IWebIdService.MBEAN_NAME);
        }

        return webIdService;
    }

    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {
        String cmsPath = null;


        String toAnalize = requestPath;


        ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();


        /* Standard decoding */

        toAnalize = requestPath;


        try {

            cmsPath = getWebIdService().webPathToWebId(toAnalize);
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