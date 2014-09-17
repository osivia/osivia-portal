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
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSItem;

/**
 * Implementation of IWebIdService.
 *
 * @author Loïc Billon
 * @see IWebIdService
 */
public class WebIdService implements IWebIdService {

    /** Slash separator. */
    private static final String SLASH = "/";
    /** Dot separator. */
    private static final String DOT = ".";


    /** Portal URL factory. */
    private IPortalUrlFactory portalURLFactory;


    /**
     * Default constructor.
     */
    public WebIdService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String webPathToFetchInfoService(String webpath) {
        String[] segments = webpath.split(SLASH);
        String domainId = segments[0];
        String webid = segments[segments.length - 1];
        webid = PREFIX_WEBID_FETCH_PUB_INFO.concat(domainId).concat(SLASH).concat(webid);

        return webid;
    }


    /**
     * {@inheritDoc}
     */
    public String domainAndIdToFetchInfoService(String domainId, String webid) {
        String cmsPath = PREFIX_WEBID_FETCH_PUB_INFO.concat(domainId).concat(SLASH).concat(webid);

        return cmsPath;
    }


    /**
     * {@inheritDoc}
     */
    public String pageUrlToFetchInfoService(String pageUrl) {

        String[] split = pageUrl.split("/");
        String domainId = split[2];
        String webid = split[split.length - 1];

        int indexOfDot = webid.indexOf(".");
        if (indexOfDot >= 0) {
            webid = webid.substring(0, indexOfDot);
        }

        webid = PREFIX_WEBID_FETCH_PUB_INFO.concat(domainId).concat(SLASH).concat(webid);

        return webid;
    }


    /**
     * {@inheritDoc}
     */
    public String webPathToPageUrl(String webpath) {
        return PREFIX_WEBPATH.concat(SLASH).concat(webpath).concat(SUFFIX_WEBPATH);
    }


    /**
     * {@inheritDoc}
     */
    public String itemToPageUrl(CMSItem item) {

        String domainId = item.getProperties().get(DOMAIN_ID);
        String webid = item.getWebId();
        String explicitUrl = item.getProperties().get(EXPLICIT_URL);
        String extension = item.getProperties().get(EXTENSION_URL);
        String webpath = null;

        if (StringUtils.isNotEmpty(webid) && StringUtils.isNotEmpty(domainId)) {
            webpath = SLASH.concat(domainId).concat(SLASH);

            if (StringUtils.isNotEmpty(explicitUrl)) {
                webpath = webpath.concat(explicitUrl).concat(SLASH);
            }

            webpath = webpath.concat(webid);


            if ((item.getType() != null) && item.getType().equals("File")) {
                if (extension != null) {
                    webpath = webpath.concat(DOT).concat(extension);
                }
            }

            return PREFIX_WEBPATH.concat(webpath).concat(SUFFIX_WEBPATH);

        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String webPathToWebId(String webpath) {

        String[] segments = webpath.split("/");
        if (segments.length > 1) {

            String webid = segments[segments.length - 1];

            int indexOfDot = webid.indexOf(".");
            if (indexOfDot >= 0) {
                webid = webid.substring(0, indexOfDot);
            }

            return webid;

        } else {
            return webpath;
        }
    }


    /**
     * {@inheritDoc}
     */
    public String generateCanonicalWebURL(PortalControllerContext portalControllerContext, String domainId, String webId) {
        StringBuilder url = new StringBuilder();

        // Controller context
        ControllerContext controllerContext = (ControllerContext) portalControllerContext.getControllerCtx();
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
     * Getter for portalURLFactory.
     *
     * @return the portalURLFactory
     */
    public IPortalUrlFactory getPortalURLFactory() {
        return this.portalURLFactory;
    }

    /**
     * Setter for portalURLFactory.
     *
     * @param portalURLFactory the portalURLFactory to set
     */
    public void setPortalURLFactory(IPortalUrlFactory portalURLFactory) {
        this.portalURLFactory = portalURLFactory;
    }

}
