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
 */
package org.osivia.portal.core.web;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Implementation of IWebIdService.
 *
 * @author Loïc Billon
 * @see IWebIdService
 */
public class WebIdService implements IWebIdService {

    /** Slash separator. */
    private static final String SLASH = "/";
    /** Query separator. */
    private static final String QUERY_SEPARATOR = "?";


    /** Portal URL factory. */
    private IPortalUrlFactory portalURLFactory;

    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public WebIdService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String webIdToFetchPath(String webId) {
        String fetchPath;
        if (webId != null) {
            fetchPath = FETCH_PATH_PREFIX.concat(webId);
        } else {
            fetchPath = null;
        }
        return fetchPath;
    }


    /**
     * {@inheritDoc}
     */
    public String cmsPathToFetchPath(String cmsPath) {
        String webId = StringUtils.substringAfterLast(cmsPath, SLASH);
        return this.webIdToFetchPath(webId);
    }


    /**
     * {@inheritDoc}
     */
    public String webIdToCmsPath(String webId) {
        StringBuilder path = new StringBuilder();
        path.append(CMS_PATH_PREFIX);
        path.append(SLASH);
        path.append(webId);
        return path.toString();
    }


    /**
     * {@inheritDoc}
     */
    public String getParentWebId(CMSServiceCtx cmsContext, String path) {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        // Parent webId
        String parentWebId = null;

        String parentPath = cmsService.getParentPath(path);
        if (StringUtils.isNotBlank(parentPath)) {
            try {
                CMSItem parent = cmsService.getContent(cmsContext, parentPath);
                parentWebId = parent.getWebId();
            } catch (CMSException e) {
                // Do nothing
            }
        }

        return parentWebId;
    }


    /**
     * {@inheritDoc}
     */
    @Deprecated
    public String webPathToFetchInfoService(String webpath) {
        String[] segments = webpath.split(SLASH);
        String webid = segments[segments.length - 1];
        return FETCH_PATH_PREFIX.concat(webid);
    }


    /**
     * {@inheritDoc}
     */
    @Deprecated
    public String itemToPageUrl(CMSServiceCtx cmsContext, CMSItem cmsItem) {
        String webid = cmsItem.getWebId();
        String explicitUrl = cmsItem.getProperties().get(EXPLICIT_URL);

        // compute a path with webIDs of the parents
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        StringBuilder parentWebIdPath = new StringBuilder();

        String path = cmsItem.getPath();

        Portal portal = PortalObjectUtils.getPortal(cmsContext.getControllerContext());
        boolean isWebMode = PortalObjectUtils.isSpaceSite(portal);

        if (StringUtils.isNotBlank(path)) {

            if (isWebMode) {

                String[] splittedPath = StringUtils.split(path, SLASH);
                StringBuilder pathToCheck = new StringBuilder();
                pathToCheck.append(SLASH);
                pathToCheck.append(splittedPath[0]);
                for (int i = 1; i < (splittedPath.length - 1); i++) {
                    pathToCheck.append(SLASH);
                    pathToCheck.append(splittedPath[i]);

                    try {
                        CMSItem parentItem = cmsService.getContent(cmsContext, pathToCheck.toString());
                        String parentWebId = parentItem.getWebId();

                        if (parentWebId != null) {
                            if (StringUtils.isNotBlank(parentWebIdPath.toString())) {
                                parentWebIdPath.append(SLASH);
                            }
                            parentWebIdPath.append(parentWebId);
                        }
                    } catch (CMSException e) {
                        // Do nothing
                    }
                }
            }
        }

        StringBuilder webPath = new StringBuilder();
        if (StringUtils.isNotEmpty(webid)) {
            webPath.append(CMS_PATH_PREFIX);
            webPath.append(SLASH);

            if (StringUtils.isNotEmpty(parentWebIdPath.toString())) {
                webPath.append(parentWebIdPath);
                webPath.append(SLASH);
            }
            if (StringUtils.isNotEmpty(explicitUrl)) {
                webPath.append(explicitUrl);
                webPath.append(SLASH);
            }

            // webid can have parameters so path can be null
            // (it can not be resolved directly)
            String parameters = StringUtils.EMPTY;
            if (StringUtils.contains(webid, QUERY_SEPARATOR)) {
                parameters = QUERY_SEPARATOR + StringUtils.substringAfter(webid, QUERY_SEPARATOR);
                webid = StringUtils.substringBefore(webid, QUERY_SEPARATOR);
            }

            webPath.append(webid);

            if (StringUtils.isNotEmpty(parameters)) {
                webPath.append(parameters);
            }

        }

        return StringUtils.trimToNull(webPath.toString());
    }


    /**
     * {@inheritDoc}
     */
    @Deprecated
    public String generateCanonicalWebURL(PortalControllerContext portalControllerContext, String domainId, String webId) {
        StringBuilder url = new StringBuilder();

        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // Server context
        ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();

        // Portal base URL
        url.append(this.portalURLFactory.getBasePortalUrl(portalControllerContext));
        // Portal context path
        url.append(serverContext.getPortalContextPath());

        url.append("/web");

        // DomainId
        if (StringUtils.isNotBlank(domainId)) {
            url.append("/");
            url.append(domainId);
        }

        // WebId
        url.append("/");
        url.append(webId);
        url.append(".html");

        return url.toString();
    }





    /**
     * Setter for portalURLFactory.
     *
     * @param portalURLFactory the portalURLFactory to set
     */
    public void setPortalURLFactory(IPortalUrlFactory portalURLFactory) {
        this.portalURLFactory = portalURLFactory;
    }

    /**
     * Setter for cmsServiceLocator.
     *
     * @param cmsServiceLocator the cmsServiceLocator to set
     */
    public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
        this.cmsServiceLocator = cmsServiceLocator;
    }

}
