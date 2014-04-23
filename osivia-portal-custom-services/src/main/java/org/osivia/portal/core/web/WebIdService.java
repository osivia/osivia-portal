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
 * Implementation of IWebIdService
 * 
 * @author lbi
 * 
 */
public class WebIdService implements IWebIdService {


    private static final String SLASH = "/";
    private static final String DOT = ".";

    /**
     * Get Url for FetchPublicationInfos service
     * 
     * @param webpath full webpath
     * @return webId:domain-id/webid for fetchPublicationInfos
     */
    public String webPathToFetchInfoService(String webpath) {
        String[] segments = webpath.split(SLASH);
        String domainId = segments[0];
        String webid = segments[segments.length - 1];
        webid = PREFIX_WEBID_FETCH_PUB_INFO.concat(domainId).concat(SLASH).concat(webid);

        return webid;
    }

    /**
     * Get Url for FetchPublicationInfos service
     * 
     * @param domainId domainid
     * @param webid webid
     * @return webId:domain-id/webid for fetchPublicationInfos
     */
    public String domainAndIdToFetchInfoService(String domainId, String webid) {
        String cmsPath = PREFIX_WEBID_FETCH_PUB_INFO.concat(domainId).concat(SLASH).concat(webid);

        return cmsPath;
    }

    /**
     * Get Url for FetchPublicationInfos service
     * 
     * @param pageUrl /_webid/a/path
     * @return webId:domain-id/webid for fetchPublicationInfos
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
     * Get Url shown as a page url
     * 
     * @param webpath full webpath
     * @return /_webid/full/web/path.html
     */
    public String webPathToPageUrl(String webpath) {
        return PREFIX_WEBPATH.concat(SLASH).concat(webpath).concat(SUFFIX_WEBPATH);
    }



    /**
     * Get Url shown as a page url from an ECM document
     * 
     * @param item the ECM item
     * @return /_webid/full/web/path.html
     */
    public String itemToPageUrl(CMSItem item) {

        String domainId = item.getProperties().get(DOMAIN_ID);
        String webid = item.getWebId();
        String explicitUrl = item.getProperties().get(EXPLICIT_URL);
        String extension = item.getProperties().get(EXTENSION_URL);
        String webpath = null;

        if (webid != null) {
            webpath = SLASH.concat(domainId).concat(SLASH);

            if (explicitUrl != null) {
                webpath = webpath.concat(explicitUrl).concat(SLASH);
            }

            webpath = webpath.concat(webid);


            if (item.getType() != null && item.getType().equals("File")) {
                if (extension != null) {
                    webpath = webpath.concat(DOT).concat(extension);
                }
            }

            return PREFIX_WEBPATH.concat(webpath).concat(SUFFIX_WEBPATH);

        }

        return null;
    }


    /**
     * Get only webid in a webpath
     * 
     * @param webpath : /_webid/full/web/path.html
     * @return webid : "path"
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

        } else
            return webpath;
    }
}
