package org.osivia.portal.core.cms;

import java.util.List;
import java.util.Map;

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

    String adaptCMSPathToWeb(CMSServiceCtx cmsCtx, String basePath, String requestPath, boolean webPath) throws CMSException;

    List<CMSPage> computeUserPreloadedPages(CMSServiceCtx cmsCtx) throws CMSException;

    /**
     * Build and return all windows included in the page.
     * 
     * @param cmsCtx context
     * @param pagePath the path of the page
     * @return the windows
     * @throws CMSException
     */
    List<CMSEditableWindow> getEditableWindows(CMSServiceCtx cmsCtx, String pagePath) throws CMSException;

    /**
     * Get base URL to access ECM.
     * 
     * @param cmsCtx context
     * @return url
     * @throws CMSException
     */
    String getEcmDomain(CMSServiceCtx cmsCtx) throws CMSException;

    /**
     * Get urls used to access ECM specific views.
     * 
     * @param cmsCtx context
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
     * @param cmsCtx context
     * @param pagePath the path of the page
     * @param refURI an unique identifier on the fragment to delete in the current page
     * @throws CMSException
     */
    void deleteFragment(CMSServiceCtx cmsCtx, String pagePath, String refURI) throws CMSException;

    /**
     * Move a CMS fragment on a page (drag & drop).
     * 
     * @param cmsCtx context
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
     * @param cmsCtx context
     * @param type the type name
     * @return the permission
     */
    boolean isCmsWebPage(CMSServiceCtx cmsCtx, String cmsPath) throws CMSException;

    /**
     * Publish the current live version of a document online.
     * 
     * @param cmsCtx context
     * @param pagePath the path of the page
     * @throws CMSException
     */
    void publishDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException;

    /**
     * Unpublish the current online version.
     * 
     * @param cmsCtx context
     * @param pagePath the path of the page
     * @throws CMSException
     */
    void unpublishDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException;

    /**
     * Delete the document.
     * 
     * @param cmsCtx context
     * @param pagePath the path of the page
     * @throws CMSException
     */
    void deleteDocument(CMSServiceCtx cmsCtx, String pagePath) throws CMSException;


    /**
     * Put the document in a deleted state.
     * 
     * @param cmsCtx context
     * @param pagePath the path of the page
     * @throws CMSException
     */
    void putDocumentInTrash(CMSServiceCtx cmsCtx, String docId) throws CMSException;

}
