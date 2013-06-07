package org.osivia.portal.api.urls;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.jboss.portal.core.model.portal.Page;
import org.osivia.portal.api.contexte.PortalControllerContext;



public interface IPortalUrlFactory {
	
	
	public static String  CONTEXTUALIZATION_PORTLET = "portlet";
	public static String  CONTEXTUALIZATION_PAGE = "page";		
	public static String  CONTEXTUALIZATION_PORTAL = "portal";		

	public static final String PERM_LINK_TYPE_PAGE = "page";
	public static final String PERM_LINK_TYPE_RSS = "rss";	
	public static final String PERM_LINK_TYPE_RSS_PICTURE = "rsspicture";		
	public static final String PERM_LINK_TYPE_CMS = "cms";	

	// TODO : à déplacer dans le cms
	public Page getPortalCMSContextualizedPage(PortalControllerContext ctx, String path) throws Exception;
	
	public String getCMSUrl(PortalControllerContext ctx, String pagePath, String cmsPath,  Map<String, String> pageParams, String contextualization, String displayContext,String hideMetaDatas, String scope, String displayLiveVersion, String windowPermReference); 
	public String getStartProcUrl(PortalControllerContext ctx, String pageId,  String portletInstance, String region, String windowName, Map<String, String> props, Map<String, String> params);
	public String getDestroyProcUrl(PortalControllerContext ctx, String pageId, String windowId);
	public String getPermaLink(PortalControllerContext ctx, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)  throws Exception	;
	
	public String getStartPageUrl(PortalControllerContext ctx, String parentName, String pageName, String templateName,
			Map<String, String> props, Map<String, String> params) throws Exception	;
	
	public String getStartPageUrl(PortalControllerContext ctx, String pageName, String templateName,
			Map<String, String> props, Map<String, String> params) throws Exception	;

	
	
	public String getDestroyPageUrl(PortalControllerContext ctx,String parentId, String pageId) 	;
	
	// Api simplifiée de lancement d'un portlet
	public String getExecutePortletLink(RenderRequest request,  String portletInstance, Map<String, String> windowProperties, Map<String, String> params) throws Exception;
	
	// Ajout des elements de nvigation( pagemarker) a une url portail
	public String adaptPortalUrlToNavigation( PortletRequest request, String orginalUrl)	throws Exception;
	
	 // Ouverture / fermeture d'un popup
    public String adaptPortalUrlToPopup( PortletRequest request, String orginalUrl, boolean closePopup)    throws Exception ;
	
}
