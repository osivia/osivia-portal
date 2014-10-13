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

import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.cms.CMSItem;

/**
 * Perform transformation for url in webid formats.
 *
 * @author Loïc Billon
 */
public interface IWebIdService {

    /** Extension of a document like .html, .jpg, .pdf. */
    String EXTENSION_URL = "extensionUrl";
    /** Explicit segment of the URL (not involved on the resolution of the document). */
    String EXPLICIT_URL = "explicitUrl";
    /** Domain where the document is. */
    String DOMAIN_ID = "domainID";

    /** Prefix used to query document in the ECM. */
    String PREFIX_WEBID_FETCH_PUB_INFO = "webId:";
    /** Prefix for webpaths used in the cms URL. */
    String PREFIX_WEBPATH = "/_webid";

    /** Default suffix for pages. */
    String SUFFIX_WEBPATH = ".html";

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=webIdService";


    /**
     * Get Url for FetchPublicationInfos service.
     *
     * @param webpath full webpath
     * @return webId:domain-id/webid for fetchPublicationInfos
     */
    String webPathToFetchInfoService(String webpath);


    /**
     * Get Url for FetchPublicationInfos service.
     *
     * @param domainId domainid
     * @param webpath webid
     * @return webId:domain-id/webid for fetchPublicationInfos
     */
    String domainAndIdToFetchInfoService(String domainId, String webpath);


    /**
     * Get Url for FetchPublicationInfos service.
     *
     * @param pageUrl /_webid/a/path
     * @return webId:domain-id/webid for fetchPublicationInfos
     */
    String pageUrlToFetchInfoService(String pageUrl);


    /**
     * Get URL shown as a page URL.
     *
     * @param webpath full webpath
     * @return /_webid/full/web/path.html
     */
    String webPathToPageUrl(String webpath);


    /**
     * Get URL shown as a page URL from an ECM document.
     *
     * @param item the ECM item
     * @return /_webid/full/web/path.html
     */
	String itemToPageUrl(ControllerContext ctx, CMSItem item);


    /**
     * Get only webid in a webpath.
     *
     * @param webpath : /_webid/full/web/path.html
     * @return webid : "path"
     */
    String webPathToWebId(String webpath);


    /**
     * Generate canonical web URL.
     * Example : http://www.example.com/portal/web/home.html
     * 
     * @param portalControllerContext portal controller context
     * @param domainId domainId, must be null for current domain
     * @param webId webId
     * @return canonical web URL
     */
    String generateCanonicalWebURL(PortalControllerContext portalControllerContext, String domainId, String webId);

}
