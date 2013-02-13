package org.osivia.portal.core.cms;

import java.util.List;
import java.util.Map;

public interface ICMSService {
	
	public CMSItem getContent( CMSServiceCtx ctx, String path) throws CMSException;
	
	public boolean checkContentAnonymousAccess(CMSServiceCtx cmsCtx, String path) throws CMSException;
	
	/**
	 * Retourne un ensemble d'informations liées à l'espace de publication contenant le document dont le chemin est passé en paramètre.
	 * @param ctx contexte du CMSService
	 * @param path chemin d'un document
	 * @return l' ensemble des informations liées à l'espace de publication dans un objet de type CMSPublicationInfos
	 * @throws CMSException
	 */
	public CMSPublicationInfos getPublicationInfos( CMSServiceCtx ctx, String path) throws CMSException;
	
	public CMSItem getPublicationConfig(CMSServiceCtx cmsCtx, String publishSpacePath) throws CMSException;

	//A supprimer ??
	public CMSItem getPortalPublishSpace( CMSServiceCtx ctx, String path) throws CMSException;
	
	public CMSItem getPortalNavigationItem( CMSServiceCtx ctx, String publishSpacePath, String path) throws CMSException;

	public List<CMSItem> getPortalNavigationSubitems( CMSServiceCtx ctx, String publishSpacePath, String path)  throws CMSException;	
	
	public CMSHandlerProperties getItemHandler( CMSServiceCtx ctx)  throws CMSException;	
	
	public CMSBinaryContent getBinaryContent(CMSServiceCtx cmsCtx, String type, String path, String parameter) throws CMSException;
	
	public Map<String, String> parseCMSURL(CMSServiceCtx cmsCtx, String requestPath, Map<String, String> requestParameters)  throws CMSException ;

	public List<CMSPage> computeUserPreloadedPages(CMSServiceCtx cmsCtx)  throws CMSException ;

}
