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

import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;

/**
 * Perform transformation for url in webid formats.
 *
 * @author Loïc Billon
 */
public interface IWebIdService {

    /** Remote proxy webId marker. */
    String RPXY_WID_MARKER = "_c_";

    /** Explicit segment of the URL (not involved on the resolution of the document). */
    String EXPLICIT_URL = "explicitUrl";
    /** Key parameter of parent webid of a document. */
    String PARENT_ID = "parentId";
    /** Key parameter of parent webid of a document. */
    String PARENT_WID = "parentWebId";
    /** Key parameter of parent path of a document. */
    String PARENT_PATH = "parentPath";

    /** Prefix used to query document in the ECM. */
    String FETCH_PATH_PREFIX = "webId:";
    /** Prefix for CMS path. */
    String CMS_PATH_PREFIX = "/_id";

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=webIdService";


    /**
     * Convert webId to fetch publication infos path.
     *
     * @param webId webId
     * @return fetch publication infos path (e.g. webId:example)
     */
    String webIdToFetchPath(String webId);


    /**
     * Convert CMS path to fetch publication infos path.
     *
     * @param cmsPath CMS path (e.g. /_id/example)
     * @return fetch publication infos path (e.g. webId:example)
     */
    String cmsPathToFetchPath(String cmsPath);


    /**
     * Convert webId to CMS path.
     *
     * @param webId webId
     * @return CMS path (e.g. /_id/example)
     */
    String webIdToCmsPath(String webId);


    /**
     * Get first parent webId, if any.
     *
     * @param cmsContext CMS context
     * @param path document CMS path
     * @return parent webId.
     */
    String getParentWebId(CMSServiceCtx cmsContext, String path);


    /**
     * Get Url for FetchPublicationInfos service.
     *
     * @param webpath full webpath
     * @return webId:webid for fetchPublicationInfos
     */
    @Deprecated
    String webPathToFetchInfoService(String webpath);


    /**
     * Get URL shown as a page URL from an ECM document.
     *
     * @param cmsContext CMS context
     * @param cmsItem CMS item
     * @return /_id/full/web/path.html
     */
    @Deprecated
    String itemToPageUrl(CMSServiceCtx cmsContext, CMSItem cmsItem);


    /**
     * Generate canonical web URL.
     * Example : http://www.example.com/portal/web/home.html
     *
     * @param portalControllerContext portal controller context
     * @param domainId domainId, must be null for current domain
     * @param webId webId
     * @return canonical web URL
     */
    @Deprecated
    String generateCanonicalWebURL(PortalControllerContext portalControllerContext, String domainId, String webId);

}
