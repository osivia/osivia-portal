/******************************************************************************
 * JBoss, a division of Red Hat *
 * Copyright 2006, Red Hat Middleware, LLC, and individual *
 * contributors as indicated by the @authors tag. See the *
 * copyright.txt in the distribution for a full listing of *
 * individual contributors. *
 * *
 * This is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as *
 * published by the Free Software Foundation; either version 2.1 of *
 * the License, or (at your option) any later version. *
 * *
 * This software is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU *
 * Lesser General Public License for more details. *
 * *
 * You should have received a copy of the GNU Lesser General Public *
 * License along with this software; if not, write to the Free *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. *
 ******************************************************************************/
package org.osivia.portal.core.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.PagePathUtils;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;


public class WebURLFactory extends URLFactoryDelegate {

    /** . */
    private String path;


    public static ServerURL doWebMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd, ServerURL standardURL) {

        if (cmd instanceof InvokePortletWindowRenderCommand) {

            InvokePortletWindowCommand invCmd = (InvokePortletWindowCommand) cmd;

            PortalObjectId poid = invCmd.getTargetId();

            Window window = (Window) controllerContext.getController().getPortalObjectContainer().getObject(poid);

            // if (PortalObjectUtils.isSpaceSite(window.getPage().getPortal())) {
            if (InternalConstants.PORTAL_URL_POLICY_WEB.equals(window.getPage().getProperty(InternalConstants.PORTAL_PROP_NAME_URL_POLICY))) {

                String basePath = window.getPage().getProperty("osivia.cms.basePath");

                if (basePath != null) {

                    WebCommand webCommand = new WebCommand();
                    String pathNavigation = PagePathUtils.getNavigationPath(controllerContext, window.getPage().getId());
                    webCommand.setCmsPath(pathNavigation);
                    webCommand.setWindowName(window.getName());


                    ServerURL serverURL = controllerContext.getController().getURLFactory().doMapping(controllerContext, invocation, webCommand);
                    serverURL.getParameterMap().append(standardURL.getParameterMap());

                    return serverURL;
                }
            }
        }

        return null;
    }


    public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("No null command accepted");
        }


        if (cmd instanceof WebCommand) {

            WebCommand webCmmand = (WebCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            // asu.setPortalRequestPath(path);
            String cmsPath = webCmmand.getCmsPath();

            String portalRequestPath = path;


            if (cmsPath != null) {


                portalRequestPath += cmsPath;
            }

            asu.setPortalRequestPath(portalRequestPath);


            String windowName = webCmmand.getWindowName();
            if (windowName != null) {
                try {
                    asu.setParameterValue(InternalConstants.PORTAL_WEB_URL_PARAM_WINDOW, URLEncoder.encode(windowName, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // ignore
                }
            }

            asu.setParameterValue(InternalConstants.PORTAL_WEB_URL_PARAM_PAGEMARKER, PageMarkerUtils.getCurrentPageMarker(controllerContext));

            return asu;
        }
        return null;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
