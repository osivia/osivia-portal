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
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.core.model.portal.Page;
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
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.ExtendedParameters;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.CmsExtendedParameters;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.page.PagePathUtils;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.security.CmsPermissionHelper;

/**
 * Web URL factory.
 *
 * @see URLFactoryDelegate
 */
public class WebURLFactory extends URLFactoryDelegate {

    /** CMS service locator. */
    private static ICMSServiceLocator cmsServiceLocator;
    /** WebId service. */
    private static IWebIdService webIdService;
    /** Web URL service. */
    private static IWebUrlService webUrlService;


    /** Path. */
    private String path;


    /**
     * Constructor.
     */
    public WebURLFactory() {
        super();
    }


    /**
     * Get CMS Service.
     *
     * @return CMS service
     */
    public static ICMSService getCMSService() {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();
    }


    /**
     * Get webId service.
     *
     * @return webId service
     */
    public static IWebIdService getWebIdService() {
        if (webIdService == null) {
            webIdService = Locator.findMBean(IWebIdService.class, IWebIdService.MBEAN_NAME);
        }

        return webIdService;
    }


    /**
     * Get web URL service.
     *
     * @return web URL service
     */
    public static IWebUrlService getWebUrlService() {
        if (webUrlService == null) {
            webUrlService = Locator.findMBean(IWebUrlService.class, IWebUrlService.MBEAN_NAME);
        }
        return webUrlService;
    }


    /**
     * Returns the defaut CMS path for current portalSite.
     *
     * @param controllerContext controller context
     * @return null if not a URL Policy is disabled
     */
    public static String getWebPortalBasePath(ControllerContext controllerContext) {
        String basePath = null;

        String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);
        if (portalName != null) {
            Portal webPortal = (Portal) controllerContext.getController().getPortalObjectContainer()
                    .getObject(PortalObjectId.parse(StringUtils.EMPTY, "/" + portalName, PortalObjectPath.CANONICAL_FORMAT));
            if ((webPortal != null) && InternalConstants.PORTAL_URL_POLICY_WEB.equals(webPortal.getProperty(InternalConstants.PORTAL_PROP_NAME_URL_POLICY))) {
                basePath = webPortal.getDefaultPage().getDeclaredProperty("osivia.cms.basePath");
            }
        }

        return basePath;
    }


    /**
     * Adapt web URL to CMS path.
     *
     * @param controllerContext controller context
     * @param webPath web path
     * @param extendedParameters extended parameters
     * @param reload reload indicator
     * @return CMS path
     * @throws ControllerException
     */
    public static String adaptWebURLToCMSPath(ControllerContext controllerContext, String webPath, ExtendedParameters extendedParameters, boolean reload)
            throws Exception {
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(controllerContext);
        if (CmsPermissionHelper.getCurrentCmsVersion(controllerContext).equals(CmsPermissionHelper.CMS_VERSION_PREVIEW)) {
            cmsContext.setDisplayLiveVersion("1");
        }

        if (extendedParameters != null) {
            String parentId = extendedParameters.getParameter(CmsExtendedParameters.parentId.name());
            if (StringUtils.isNotBlank(parentId)) {
                cmsContext.setParentId(parentId);
            } else {
                String parentPath = extendedParameters.getParameter(CmsExtendedParameters.parentPath.name());
                if (StringUtils.isNotBlank(parentPath)) {
                    cmsContext.setParentPath(parentPath);
                }
            }
        }

        // Base path
        String basePath = WebURLFactory.getWebPortalBasePath(controllerContext);

        // WebId
        String webId = getWebUrlService().getWebId(cmsContext, basePath, webPath);

        // Path to fetch
        String pathToFetch = getWebIdService().webIdToFetchPath(webId);

        if (reload) {
            cmsContext.setForceReload(true);
        }

        return getCMSService().adaptWebPathToCms(cmsContext, pathToFetch);
    }


    /**
     * Converts classic portal URL to web URL.
     *
     * @param controllerContext controller context
     * @param invocation server invocation
     * @param command controller command
     * @param standardURL standard URL
     * @return web URL
     */
    public static ServerURL doWebMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand command, ServerURL standardURL) {
        if (getWebPortalBasePath(controllerContext) == null) {
            return null;
        }

        if (command instanceof CmsCommand) {
            CmsCommand cmsCommand = (CmsCommand) command;
            if (cmsCommand.getCmsPath().startsWith(IWebIdService.CMS_PATH_PREFIX)) {
                // CMS context
                CMSServiceCtx cmsContext = new CMSServiceCtx();
                cmsContext.setControllerContext(controllerContext);
                if (CmsPermissionHelper.getCurrentCmsVersion(controllerContext).equals(CmsPermissionHelper.CMS_VERSION_PREVIEW)) {
                    cmsContext.setDisplayLiveVersion("1");
                }

                if (cmsCommand.getPortalPersistentName() != null) {
                    return null;
                }
                if (StringUtils.equals("detailedView", cmsCommand.getDisplayContext())) {
                    return null;
                }

                // WebId
                String webId = StringUtils.removeStart(cmsCommand.getCmsPath(), IWebIdService.CMS_PATH_PREFIX);
                webId = StringUtils.substringAfterLast(webId, "/");

                // Base path
                String basePath = getWebPortalBasePath(controllerContext);

                // Web path
                String webPath = getWebUrlService().getWebPath(cmsContext, basePath, webId);

                // Web command
                WebCommand webCommand = new WebCommand(webPath);
                webCommand.setSupportingPageMarker(false);
                webCommand.setExtendedParameters(cmsCommand.getExtendedParameters());

                return controllerContext.getController().getURLFactory().doMapping(controllerContext, invocation, webCommand);
            }
        }

        if (command instanceof PortalObjectCommand) {
            PortalObjectCommand portalObjectCommand = (PortalObjectCommand) command;
            PortalObjectId targetId = portalObjectCommand.getTargetId();
            PortalObject portalObject = controllerContext.getController().getPortalObjectContainer().getObject(targetId);
            if (portalObject instanceof Window) {
                Window window = (Window) portalObject;
                Page page = window.getPage();

                // WebId
                String webId = PagePathUtils.getNavigationWebId(controllerContext, page.getId());
                if (StringUtils.isEmpty(webId)) {
                    return null;
                }


                // Compliqué en retour de fetcher en fonction du webId de l'url
                // Car pour récupérer le mode de contribution, il faut le windowId ...
                EditionState editionState = ContributionService.getWindowEditionState(controllerContext, window.getId());
                if ((editionState != null) && editionState.getContributionMode().equals(EditionState.CONTRIBUTION_MODE_EDITION)) {
                    return null;
                }


                // CMS context
                CMSServiceCtx cmsContext = new CMSServiceCtx();
                cmsContext.setControllerContext(controllerContext);

                // Base path
                String basePath = getWebPortalBasePath(controllerContext);

                // Web path
                String webPath = getWebUrlService().getWebPath(cmsContext, basePath, webId);

                // Web command
                WebCommand webCommand = new WebCommand(webPath);
                webCommand.setWindowName(window.getName());
//                if (command instanceof InvokePortletWindowResourceCommand) {
//                    webCommand.setSupportingPageMarker(false);
//                }

                ServerURL serverURL = controllerContext.getController().getURLFactory().doMapping(controllerContext, invocation, webCommand);
                serverURL.getParameterMap().append(standardURL.getParameterMap());

                return serverURL;
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("No null command accepted");
        }

        if (command instanceof WebCommand) {
            WebCommand webCommand = (WebCommand) command;

            //
            AbstractServerURL asu = new AbstractServerURL();
            // asu.setPortalRequestPath(path);
            String cmsPath = webCommand.getWebPath();

            String portalRequestPath = this.path;


            if (cmsPath != null) {
                portalRequestPath += cmsPath;
            }

            asu.setPortalRequestPath(portalRequestPath);

            ExtendedParameters extendedParameters = webCommand.getExtendedParameters();
            if (extendedParameters != null) {

                String parentId = extendedParameters.getParameter(CmsExtendedParameters.parentId.name());

                if (StringUtils.isNotBlank(parentId)) {
                    try {
                        asu.setParameterValue("parentId", URLEncoder.encode(parentId, "UTF-8"));
                    } catch (Exception e) {
                        // ignore
                    }
                } else {
                    String parentPath = extendedParameters.getParameter(CmsExtendedParameters.parentPath.name());

                    if (StringUtils.isNotBlank(parentPath)) {
                        try {
                            asu.setParameterValue("parentPath", URLEncoder.encode(parentPath, "UTF-8"));
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                }
            }


            String windowName = webCommand.getWindowName();
            if (windowName != null) {
                try {
                    asu.setParameterValue(InternalConstants.PORTAL_WEB_URL_PARAM_WINDOW, URLEncoder.encode(windowName, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // ignore
                }
            }

            if (webCommand.isSupportingPageMarker()) {
                asu.setParameterValue(InternalConstants.PORTAL_WEB_URL_PARAM_PAGEMARKER, PageMarkerUtils.getCurrentPageMarker(controllerContext));
            }

            return asu;
        }

        return null;
    }


    /**
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Setter for path.
     *
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

}
