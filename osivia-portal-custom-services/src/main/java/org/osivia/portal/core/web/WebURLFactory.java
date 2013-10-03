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

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PortalObjectCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowResourceCommand;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.PagePathUtils;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;


public class WebURLFactory extends URLFactoryDelegate {

    /** . */
    private String path;

    
    private static ICMSServiceLocator cmsServiceLocator ;

    public static ICMSService getCMSService() throws Exception {
        
        if( cmsServiceLocator == null){
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }
    
        return cmsServiceLocator.getCMSService();

    }

    private static String getWebPortalBasePath(ControllerContext controllerContext) {

        Portal webPortal = null;
        String basePath = null;
        
        String portalName = PageProperties.getProperties().getPagePropertiesMap().get("portalName");

        if (portalName != null) {

            webPortal = (Portal) controllerContext.getController().getPortalObjectContainer()
                    .getObject(PortalObjectId.parse("", "/" + portalName, PortalObjectPath.CANONICAL_FORMAT));

            if (InternalConstants.PORTAL_URL_POLICY_WEB.equals(webPortal.getProperty(InternalConstants.PORTAL_PROP_NAME_URL_POLICY))) {

                if (webPortal != null) {
                    basePath = webPortal.getDefaultPage().getDeclaredProperty("osivia.cms.basePath");
                }
            }
        }

        return basePath;
    }


    public static String adaptCMSPathToWebURL(ControllerContext controllerContext, String cmsPath)  {

        String basePath = getWebPortalBasePath(controllerContext);
        String webPath = null;

        if (basePath != null && cmsPath != null) {
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setControllerContext(controllerContext);
            
            try {
                webPath = getCMSService().adaptCMSPathToWeb(cmsContext,basePath, cmsPath, false);

                // Si webPath vide, pas de suffixe .proxy seul
                if (".proxy".equals(webPath)) {
                    return "";
                }
            } catch( Exception e)   {
                // TODO : logger
            }
        }

        return webPath;
    }

    public static String adaptWebURLToCMSPath(ControllerContext controllerContext, String webPath) {

        String basePath = getWebPortalBasePath(controllerContext);

        if (StringUtils.isNotEmpty(webPath))    {
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setControllerContext(controllerContext);
            
            try {
                return getCMSService().adaptCMSPathToWeb(cmsContext,basePath, webPath, true);
            } catch( Exception e)   {
                // TODO : logger
            }
            
            return null;
        } else
            return basePath;
    }
 


    public static ServerURL doWebMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd, ServerURL standardURL)  {

        if (getWebPortalBasePath(controllerContext) == null)
            return null;


        if (cmd instanceof CmsCommand) {

            CmsCommand cmsCmd = (CmsCommand) cmd;

            // Exclude non standards urls
            if (cmsCmd.getPortalPersistentName() != null)
                return null;
            if (StringUtils.equals("detailedView", cmsCmd.getDisplayContext()))
                return null;
            if (!StringUtils.isEmpty(cmsCmd.getDisplayLiveVersion()))
                return null;


            String webPath = adaptCMSPathToWebURL(controllerContext, cmsCmd.getCmsPath());

            if (webPath != null) {
                WebCommand webCommand = new WebCommand();
                webCommand.setWebPath(webPath);
                webCommand.setSupportingPageMarker(false);


                ServerURL serverURL = controllerContext.getController().getURLFactory().doMapping(controllerContext, invocation, webCommand);

                return serverURL;

            }
        }


        if (cmd instanceof PortalObjectCommand) {

            PortalObjectCommand invCmd = (PortalObjectCommand) cmd;

            PortalObjectId poid = invCmd.getTargetId();

            PortalObject po = (PortalObject) controllerContext.getController().getPortalObjectContainer().getObject(poid);

            if (po instanceof Window) {
                Window window = (Window) po;


                WebCommand webCommand = new WebCommand();
                String pathNavigation = PagePathUtils.getNavigationPath(controllerContext, window.getPage().getId());
                
                if( pathNavigation == null)
                    return null;


                String webPath = adaptCMSPathToWebURL(controllerContext, pathNavigation);


                webCommand.setWebPath(webPath);
                webCommand.setWindowName(window.getName());

                if (cmd instanceof InvokePortletWindowResourceCommand) {
                    webCommand.setSupportingPageMarker(false);
                }


                ServerURL serverURL = controllerContext.getController().getURLFactory().doMapping(controllerContext, invocation, webCommand);
                serverURL.getParameterMap().append(standardURL.getParameterMap());

                return serverURL;
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
            String cmsPath = webCmmand.getWebPath();

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

            if (webCmmand.isSupportingPageMarker())
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
