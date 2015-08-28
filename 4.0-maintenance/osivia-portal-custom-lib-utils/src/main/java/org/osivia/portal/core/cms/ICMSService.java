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
 *
 */
package org.osivia.portal.core.cms;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osivia.portal.api.urls.EcmCommand;
import org.osivia.portal.api.urls.EcmFilesCommand;
import org.osivia.portal.api.urls.Link;

/**
 * CMS service interface.
 */
public interface ICMSService {

    CMSItem getContent(CMSServiceCtx ctx, String path) throws CMSException;


    boolean checkContentAnonymousAccess(CMSServiceCtx cmsCtx, String path) throws CMSException;

    /**
     * Retourne un ensemble d'informations liées à l'espace de publication contenant le document dont le chemin est passé en paramètre.
     *
     * @param ctx contexte du CMSService
     * @param path chemin d'un document
     * @return l' ensemble des informations liées à l'espace de publication dans un objet de type CMSPublicationInfos
     * @throws CMSException
     */
    CMSPublicationInfos getPublicationInfos(CMSServiceCtx ctx, String path) throws CMSException;
    
    /**
     * Get extended infos on document given by its path.
     * 
     * @param ctx CMSService context
     * @param path path of studied ddocument
     * @return extended infos on document given by its path
     * @throws CMSException
     */
    CMSExtendedDocumentInfos getExtendedDocumentInfos(CMSServiceCtx ctx, String path) throws CMSException;

    CMSItem getSpaceConfig(CMSServiceCtx cmsCtx, String publishSpacePath) throws CMSException;

    /**
     * Get portal navigation CMS item.
     *
     * @param ctx CMS service context
     * @param publishSpacePath publish space path
     * @param path current path
     * @return portal navigation CMS item
     * @throws CMSException
     */
    CMSItem getPortalNavigationItem(CMSServiceCtx ctx, String publishSpacePath, String path) throws CMSException;

    /**
     * Get portal navigation CMS sub-items.
     *
     * @param ctx CMS service context
     * @param publishSpacePath publish space path
     * @param path current path
     * @return portal navigation CMS sub-items
     * @throws CMSException
     */
    List<CMSItem> getPortalNavigationSubitems(CMSServiceCtx ctx, String publishSpacePath, String path) throws CMSException;

    /**
     * Get portal CMS sub-items.
     *
     * @param cmsContext CMS service context
     * @param path current path
     * @return portal CMS sub-items
     * @throws CMSException
     */
    List<CMSItem> getPortalSubitems(CMSServiceCtx cmsContext, String path) throws CMSException;

    CMSHandlerProperties getItemHandler(CMSServiceCtx ctx) throws CMSException;

    CMSBinaryContent getBinaryContent(CMSServiceCtx cmsCtx, String type, String path, String parameter) throws CMSException;

    Map<String, String> parseCMSURL(CMSServiceCtx cmsCtx, String requestPath, Map<String, String> requestParameters) throws CMSException;

    /**
     * Transform webid path in cms path
     *
     * @param cmsCtx context
     * @param requestPath the path given in webid
     * @return path adapted
     */
    String adaptWebPathToCms(CMSServiceCtx cmsCtx, String requestPath) throws CMSException;

    // String adaptCMSPathToWeb(CMSServiceCtx cmsCtx, String requestPath, boolean webPath) throws CMSException;

    List<CMSPage> computeUserPreloadedPages(CMSServiceCtx cmsCtx) throws CMSException;


    /**
     * Build and return all editable windows included and inherided in the page.
     *
     * @param cmsContext CMS context
     * @param path current path
     * @param publishSpacePath publish space path
     * @param sitePath site path
     * @param navigationScope navigation scope
     * @param isSpaceSite if the portal is a space site
     * @return editable windows
     * @throws CMSException
     */
    List<CMSEditableWindow> getEditableWindows(CMSServiceCtx cmsContext, String path, String publishSpacePath, String sitePath, String navigationScope, Boolean isSpaceSite)
            throws CMSException;


    /**
     * Get CMS regions inheritance configuration.
     *
     * @param item CMS item
     * @return CMS regions inheritance configuration
     */
    Map<String, RegionInheritance> getCMSRegionsInheritance(CMSItem item);


    /**
     * Save CMS region inheritance.
     *
     * @param cmsContext CMS context
     * @param path current path
     * @param regionName region name
     * @param inheritance CMS region inheritance configuration
     * @throws CMSException
     */
    void saveCMSRegionInheritance(CMSServiceCtx cmsContext, String path, String regionName, RegionInheritance inheritance) throws CMSException;


    /**
     * Get CMS region layouts configuration items.
     *
     * @param cmsContext CMS context
     * @return configuration items
     * @throws CMSException
     */
    Set<CMSConfigurationItem> getCMSRegionLayoutsConfigurationItems(CMSServiceCtx cmsContext) throws CMSException;


    /**
     * Get CMS regions selected layout.
     *
     * @param item CMS item
     * @param regionLayouts region layouts
     * @return CMS regions selected layout
     * @throws CMSException
     */
    Map<String, CMSConfigurationItem> getCMSRegionsSelectedLayout(CMSItem item, Set<CMSConfigurationItem> regionLayouts) throws CMSException;


    /**
     * Save CMS region selected layout.
     *
     * @param cmsContext CMS context
     * @param path current path
     * @param regionName region name
     * @param regionLayoutName selected region layout name
     * @throws CMSException
     */
    void saveCMSRegionSelectedLayout(CMSServiceCtx cmsContext, String path, String regionName, String regionLayoutName) throws CMSException;


    /**
     * Get base URL to access ECM.
     *
     * @param cmsCtx CMS context
     * @return url
     * @throws CMSException
     */
    String getEcmDomain(CMSServiceCtx cmsCtx) throws CMSException;


    /**
     * Get urls used to access ECM specific views.
     *
     * @param cmsCtx CMS context
     * @param command type of command acceded (ex : create, edit, etc.)
     * @param path the path of the page
     * @param requestParameters GET params added in the URL
     * @return url
     * @throws CMSException
     */
    String getEcmUrl(CMSServiceCtx cmsCtx, EcmCommand command, String path, Map<String, String> requestParameters) throws CMSException;


    /**
     * Remove a CMS fragment on a page.
     *
     * @param cmsCtx CMS context
     * @param pagePath the path of the page
     * @param refURI an unique identifier on the fragment to delete in the current page
     * @throws CMSException
     */
    void deleteFragment(CMSServiceCtx cmsCtx, String pagePath, String refURI) throws CMSException;


    /**
     * Move a CMS fragment on a page (drag & drop).
     *
     * @param cmsCtx CMS context
     * @param pagePath the path of the page
     * @param fromRegion the identifier of the region from the fragment is moved
     * @param fromPos position in the fromRegion (from 0 (top) to N-1 ( number of current fgts in the region)
     * @param toRegion the identifier of the region where the fragment is dropped
     * @param toPos the new position of the fgt in the toRegion
     * @param refUri the id of the window moved
     * @throws CMSException
     */
    void moveFragment(CMSServiceCtx cmsCtx, String pagePath, String fromRegion, Integer fromPos, String toRegion, Integer toPos, String refUri)
            throws CMSException;


    /**
     * Return true if the document type is allowed in CMS mode for creation and edition.
     *
     * @param cmsCtx CMS context
     * @param cmsPath CMS path
     * @return the permission
     */
    boolean isCmsWebPage(CMSServiceCtx cmsCtx, String cmsPath) throws CMSException;


    /**
     * Publish the current live version of a document online.
     *
     * @param cmsCtx CMS context
     * @param pagePath the path of the page
     * @throws CMSException
     */
    void publishDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException;


    /**
     * Unpublish the current online version.
     *
     * @param cmsCtx CMS context
     * @param pagePath the path of the page
     * @throws CMSException
     */
    void unpublishDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException;


    /**
     * Start Publication workflow.
     *
     * @param cmsCtx CMS context
     * @param pagePath the path of the page
     * @throws CMSException
     */
    void askToPublishDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException;


    /**
     * Cancel Publication workflow.
     *
     * @param cmsCtx CMS context
     * @param pagePath the path of the page
     * @throws CMSException
     */
    void cancelPublishWorkflow(CMSServiceCtx cmsCtx, String pagePath) throws CMSException;


    /**
     * Accept publication of current document.
     *
     * @param cmsCtx CMS context
     * @param pagePath the path of the page
     * @throws CMSException
     */
    void validatePublicationOfDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException;


    /**
     * Reject publication of current document.
     *
     * @param cmsCtx CMS context
     * @param pagePath the path of the page
     * @throws CMSException
     */
    void rejectPublicationOfDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException;


    /**
     * Delete the document.
     *
     * @param cmsCtx CMS context
     * @param path page path
     * @throws CMSException
     */
    void deleteDocument(CMSServiceCtx cmsCtx, String path) throws CMSException;


    /**
     * Put the document in a deleted state.
     *
     * @param cmsCtx CMS context
     * @param docId document identifier
     * @throws CMSException
     */
    void putDocumentInTrash(CMSServiceCtx cmsCtx, String docId) throws CMSException;


    /**
     * Get user avatar.
     *
     * @param cmsCtx CMS context
     * @param username user identifier
     * @return user avatar
     */
    Link getUserAvatar(CMSServiceCtx cmsCtx, String username) throws CMSException;


    /**
     * Refresh the user avatar.
     *
     * @param cmsCtx cms context
     * @param username username
     * @return the timestamp associated with the refresh event
     */
    String refreshUserAvatar(CMSServiceCtx cmsCtx, String username);

    
    /**
     * Get binary url 
     *
     * @param cmsCtx CMS context
     * @param username user identifier
     * @return user avatar
     */
    public Link getBinaryResourceURL(CMSServiceCtx cmsCtx, BinaryDescription binary) throws CMSException;


    /**
     * Refresh the user avatar.
     *
     * @param cmsCtx cms context
     * @param username username
     * @return the timestamp associated with the refresh event
     */
    String refreshBinaryResource(CMSServiceCtx cmsCtx, String path);


    /**
     * Validate the delegation ticket
     *
     * @param cmsCtx cms context
     * @param username username
     * @return the timestamp associated with the refresh event
     */
     public BinaryDelegation validateBinaryDelegation(CMSServiceCtx cmsCtx, String path) ;

    /**
     * Synchronize or disable synchronization between local folder and ECM folder
     *
     * @param cmsCtx cms context
     * @param pagePath the current folder
     * @param enable enable or disable
     * @throws CMSException
     */
    void setSynchronization(CMSServiceCtx cmsCtx, String pagePath, Boolean enable) throws CMSException;

    
    /**
     * Maange synchronization and checkouts from ECM folder
     *
     * @param cmsCtx cms context
     * @param pagePath the current folder
     * @param enable enable or disable
     * @throws CMSException
     */
    void getEcmFilesUrl(CMSServiceCtx cmsCtx, String pagePath, EcmFilesCommand filesCommand) throws CMSException;

	/**
	 * Subscribe/unsubscribe to all notifications on the document
	 * 
	 * @param cmsCtx
	 * @param cmsPath
	 *            the current doc
	 * @param unsubscribe
	 * @throws CMSException
	 */
	void subscribe(CMSServiceCtx cmsCtx, String cmsPath, boolean unsubscribe) throws CMSException;
    

}