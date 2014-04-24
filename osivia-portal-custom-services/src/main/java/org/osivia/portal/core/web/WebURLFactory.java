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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
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
import org.osivia.portal.api.Constants;
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


    private static ICMSServiceLocator cmsServiceLocator;

    private static final Log logger = LogFactory.getLog(WebURLFactory.class);


    /**
     * Get The CMS Service
     * 
     * @return
     * @throws Exception
     */
    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }

    static IWebIdService webIdService;


    /**
     * Get Webid service
     * 
     * @return
     */
    public static IWebIdService getWebIdService() {

        if (webIdService == null) {
            webIdService = Locator.findMBean(IWebIdService.class, IWebIdService.MBEAN_NAME);
        }

        return webIdService;
    }

    /**
     * Returns the defaut CMS path for current portalSite
     * 
     * @param controllerContext
     * @return null if not a URL Policy is disabled
     */
    private static String getWebPortalBasePath(ControllerContext controllerContext) {

        Portal webPortal = null;
        String basePath = null;
        
        String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);

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



    /**
     * @param controllerContext
     * @param webPath
     * @return
     * @throws ControllerException
     */
    public static String adaptWebURLToCMSPath(ControllerContext controllerContext, String webPath) throws Exception {


        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(controllerContext);

        String domainIdPage = PageProperties.getProperties().getPagePropertiesMap().get("osivia.cms.domainId");

        String cmsPath = null;
        // if webpath is empty or equals '/', get default page associated with the portal
        if (webPath.length() <= 1) {
            cmsPath = getWebPortalBasePath(controllerContext);
        } else {
            cmsPath = getWebIdService().domainAndIdToFetchInfoService(domainIdPage, webPath);
            cmsPath = getCMSService().adaptWebPathToCms(cmsContext, cmsPath);
        }

        return cmsPath;


    }


    /**
     * Converts Classic portal urls to web urls
     * 
     * 
     * @param controllerContext
     * @param invocation
     * @param cmd
     * @param standardURL
     * @return the Web URL
     */
    public static ServerURL doWebMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd, ServerURL standardURL) {

        if (getWebPortalBasePath(controllerContext) == null)
            return null;


        if (cmd instanceof CmsCommand) {


            CmsCommand cmsCmd = (CmsCommand) cmd;
            if (cmsCmd.getCmsPath().startsWith(IWebIdService.PREFIX_WEBPATH)) {


                // Exclude non standards urls
                String domainIdPage = PageProperties.getProperties().getPagePropertiesMap().get("osivia.cms.domainId");

                String[] split = cmsCmd.getCmsPath().split("/"); // retirer le segment domain-id
                String domainId = split[2];


                if (domainId == null || !domainId.equals(domainIdPage)) {
                    return null;
                }
                if (cmsCmd.getPortalPersistentName() != null)
                    return null;
                if (StringUtils.equals("detailedView", cmsCmd.getDisplayContext()))
                    return null;


                String newPath = cmsCmd.getCmsPath().substring(IWebIdService.PREFIX_WEBPATH.length());

                newPath = newPath.substring(domainId.length() + 1);


                // TODO : portlet list : impossible d'afficher des ressources hors portal site en webid
                // avec cette clause
                // if (!StringUtils.isEmpty(cmsCmd.getDisplayLiveVersion()))
                // return null;

                String webPath = newPath;

                // webPath = adaptCMSPathToWebURL(controllerContext, newPath);

                if (webPath != null) {
                    WebCommand webCommand = new WebCommand();
                    webCommand.setWebPath(webPath);
                    webCommand.setSupportingPageMarker(false);


                    ServerURL serverURL = controllerContext.getController().getURLFactory().doMapping(controllerContext, invocation, webCommand);

                    return serverURL;

                }
            }

        }

        if (cmd instanceof PortalObjectCommand) {

            PortalObjectCommand invCmd = (PortalObjectCommand) cmd;

            PortalObjectId poid = invCmd.getTargetId();

            PortalObject po = (PortalObject) controllerContext.getController().getPortalObjectContainer().getObject(poid);

            if (po instanceof Window) {
                Window window = (Window) po;


                WebCommand webCommand = new WebCommand();
                // String pathNavigation = PagePathUtils.getNavigationPath(controllerContext, window.getPage().getId());

                String pageWebId = PagePathUtils.getNavigationWebId(controllerContext, window.getPage().getId());

                if (StringUtils.isEmpty(pageWebId))
                    return null;


                // String webPath = "/" + adaptCMSPathToWebURL(controllerContext, pageWebId);
                // TODO explicite ?
                String webPath = "/" + pageWebId.concat(WebIdService.SUFFIX_WEBPATH);

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


    /**
     * Generate Web URLS as expectec by factory pattern
     * 
     * @see org.jboss.portal.core.controller.command.mapper.URLFactory#doMapping(org.jboss.portal.core.controller.ControllerContext,
     *      org.jboss.portal.server.ServerInvocation, org.jboss.portal.core.controller.ControllerCommand)
     */


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

    /**
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }


}
