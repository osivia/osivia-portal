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

import org.osivia.portal.core.cms.CMSItem;

/**
 * Perform transformation for url in webid formats
 * 
 * @author lbi
 * 
 */
public interface IWebIdService {

    /** extension of a document like .html, .jpg, .pdf */
    public static final String EXTENSION_URL = "extensionUrl";
    /** explicit segment of the url (not involved on the resolution of the document) */
    public static final String EXPLICIT_URL = "explicitUrl";
    /** domain where the document is */
    public static final String DOMAIN_ID = "domainID";

    /** prefix used to query document in the ecm */
    public static final String PREFIX_WEBID_FETCH_PUB_INFO = "webId:";
    /** prefix for webpaths used in the cms url */
    public static final String PREFIX_WEBPATH = "/_webid";

    /** default suffix for pages */
    public static final String SUFFIX_WEBPATH = ".html";

    /** MBean name. */
    public static final String MBEAN_NAME = "osivia:service=webIdService";

    /**
     * Get Url for FetchPublicationInfos service
     * 
     * @param webpath full webpath
     * @return webId:domain-id/webid for fetchPublicationInfos
     */
    public String webPathToFetchInfoService(String webpath);

    /**
     * Get Url for FetchPublicationInfos service
     * 
     * @param domainId domainid
     * @param webpath webid
     * @return webId:domain-id/webid for fetchPublicationInfos
     */
    public String domainAndIdToFetchInfoService(String domainId, String webpath);

    /**
     * Get Url for FetchPublicationInfos service
     * 
     * @param pageUrl /_webid/a/path
     * @return webId:domain-id/webid for fetchPublicationInfos
     */
    public String pageUrlToFetchInfoService(String pageUrl);

    /**
     * Get Url shown as a page url
     * 
     * @param webpath full webpath
     * @return /_webid/full/web/path.html
     */
    public String webPathToPageUrl(String webpath);

    /**
     * Get Url shown as a page url from an ECM document
     * 
     * @param item the ECM item
     * @return /_webid/full/web/path.html
     */
    public String itemToPageUrl(CMSItem item);

    /**
     * Get only webid in a webpath
     * 
     * @param webpath : /_webid/full/web/path.html
     * @return webid : "path"
     */
    public String webPathToWebId(String webpath);
}
