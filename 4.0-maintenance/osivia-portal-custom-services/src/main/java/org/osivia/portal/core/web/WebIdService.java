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
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;

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

	/** CMS service locator. */
	private final ICMSServiceLocator cmsServiceLocator;

    /**
     * Default constructor.
     */
    public WebIdService() {

        super();
		// CMS service locator
		this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class,
				"osivia:service=CmsServiceLocator");

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
    public String itemToPageUrl(CMSServiceCtx cmsContext, CMSItem cmsItem) {
        String domainId = cmsItem.getDomainId();
        String webid = cmsItem.getWebId();
        String explicitUrl = cmsItem.getProperties().get(EXPLICIT_URL);
        String extension = cmsItem.getProperties().get(EXTENSION_URL);

		// compute a path with webIDs of the parents
		ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        StringBuilder parentWebIdPath = new StringBuilder();
		
		if( cmsItem.getPath() != null)    {

            String[] splittedPath = StringUtils.split(cmsItem.getPath(), SLASH);
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


        StringBuilder webPath = new StringBuilder();
        if (StringUtils.isNotEmpty(webid) && StringUtils.isNotEmpty(domainId)) {
            webPath.append(PREFIX_WEBPATH);
            webPath.append(SLASH);
            webPath.append(domainId);
            webPath.append(SLASH);

            if (StringUtils.isNotEmpty(parentWebIdPath.toString())) {
                webPath.append(parentWebIdPath);
                webPath.append(SLASH);
            }
			if (StringUtils.isNotEmpty(explicitUrl)) {
                webPath.append(explicitUrl);
                webPath.append(SLASH);
			}

            webPath.append(webid);

            if ((cmsItem.getType() != null) && cmsItem.getType().equals("File")) {
                if (extension != null) {
                    webPath.append(DOT);
                    webPath.append(extension);
                }
            }

            webPath.append(SUFFIX_WEBPATH);
        }
        return StringUtils.trimToNull(webPath.toString());
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
