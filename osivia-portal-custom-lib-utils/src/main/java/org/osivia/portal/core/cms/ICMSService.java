package org.osivia.portal.core.cms;

import java.util.List;

public interface ICMSService {
	
	public CMSItem getContent( CMSServiceCtx ctx, String path) throws CMSException;
	
	public boolean checkContentAnonymousAccess(CMSServiceCtx cmsCtx, String path) throws CMSException;
	
	public CMSPublicationInfos getPublicationInfos( CMSServiceCtx ctx, String path) throws CMSException;

	//A supprimer ??
	public CMSItem getPortalPublishSpace( CMSServiceCtx ctx, String path) throws CMSException;
	
	public CMSItem getPortalNavigationItem( CMSServiceCtx ctx, String publishSpacePath, String path) throws CMSException;

	public List<CMSItem> getPortalNavigationSubitems( CMSServiceCtx ctx, String publishSpacePath, String path)  throws CMSException;	
	
	public CMSHandlerProperties getItemHandler( CMSServiceCtx ctx)  throws CMSException;	
	
	public CMSBinaryContent getBinaryContent(CMSServiceCtx cmsCtx, String type, String path, String parameter) throws CMSException;

}
