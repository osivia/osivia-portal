package org.osivia.portal.core.urls;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.impl.jsr168.api.ResourceRequestImpl;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.contexte.PortalControllerContext;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.Link;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.dynamic.StopDynamicPageCommand;
import org.osivia.portal.core.dynamic.StopDynamicWindowCommand;
import org.osivia.portal.core.page.MonEspaceCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.tracker.ITracker;


public class PortalUrlFactory implements IPortalUrlFactory {

	private ITracker tracker;
	
	public ITracker getTracker() {
		return tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}
	
	
	private IProfilManager profilManager;
	
	
	public IProfilManager getProfilManager() {
		return profilManager;
	}

	public void setProfilManager(IProfilManager profilManager) {
		this.profilManager = profilManager;
	}
	
	
	
	private String addToBreadcrumb (PortletRequest request)	{
		
		if (request == null)
			return null;
			// Pas dans un context portlet (appel depuis pagecustomizer), pas de breadcrumb
			//return "0";
		
		// On regarde si on est dans une window MAXIMIZED
		
		ControllerContext ctx = (ControllerContext) request.getAttribute("pia.controller");
		Window window = (Window) request.getAttribute("pia.window");
			
			String addToBreadcrumb = "0";

			if (window != null && ctx != null) {
				
				
				NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());
				
				WindowNavigationalState windowNavState = (WindowNavigationalState) ctx.getAttribute(
						ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);
				// On regarde si la fenêtre est en vue MAXIMIZED
				

				if (windowNavState != null && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {

						addToBreadcrumb = "1";

				}
			}


			return addToBreadcrumb;
		}
	
	

	/* 
	 * renvoie la page instanciée qui contient le menu (si elle accepte la recontextualisation)
	 */
	public Page getPortalCMSContextualizedPage(PortalControllerContext ctx, String path) throws Exception	{
		
		
		Window window = (Window) ctx.getRequest().getAttribute("pia.window");
		if( window != null)	{
			Page page = window.getPage();
			// contenu deja contextualise dans la page courante
			if(  CmsCommand.isContentAlreadyContextualizedInPage(page, path))
				return page;
		}
		
		Portal portal = ctx.getControllerCtx().getController().getPortalObjectContainer().getContext().getDefaultPortal();
		
		// dans d'autres pages du portail
		PortalObject page = CmsCommand.searchPublicationPage( ctx.getControllerCtx(), portal, path, getProfilManager()) ;
		if( page != null)	{
			return (Page) page;
		}
		
		return null;
		
			
	}
	
	
	
	
	
	public String getCMSUrl(PortalControllerContext ctx, String pagePath, String cmsPath,  Map<String, String> pageParams, String contextualization, String displayContext, String hideMetaDatas, String scope, String displayLiveVersion, String windowPermReference) {
		
		
		ControllerCommand cmd = new CmsCommand(pagePath, cmsPath, pageParams, contextualization, displayContext,  hideMetaDatas, scope, displayLiveVersion, windowPermReference, addToBreadcrumb(ctx.getRequest()), null);
		PortalURL portalURL  = new PortalURLImpl(cmd, ctx.getControllerCtx(), null, null);
		
		String url = portalURL.toString();
	
		return url;
	}

	
	public String getDestroyProcUrl(PortalControllerContext ctx,String pageId, String windowId)	{
		ControllerCommand cmd = new StopDynamicWindowCommand();
		PortalURL portalURL  = new PortalURLImpl(cmd, ctx.getControllerCtx(), null, null);
		
		String url = portalURL.toString();		
		url +="&pageId="+pageId+"&windowId="+windowId;
		return url;
	}

	public String getStartProcUrl(PortalControllerContext ctx, String pageId, String portletInstance, String region,
			String windowName, Map<String, String> props, Map<String, String> params) {

		
		ControllerCommand cmd = new StartDynamicWindowCommand();
		PortalURL portalURL  = new PortalURLImpl(cmd, ctx.getControllerCtx(), null, null);
		
		
		String url = portalURL.toString();
		url +="&pageId="+pageId+"&regionId="+region+"&instanceId="+portletInstance+"&windowName="+windowName+"&props="+WindowPropertiesEncoder.encodeProperties(props)+"&params="+WindowPropertiesEncoder.encodeProperties(params)+"&addToBreadcrumb="+  addToBreadcrumb(ctx.getRequest());
		return url;
	}
	
	public String getPermaLink(PortalControllerContext ctx, String permLinkRef, Map<String, String> params, String cmsPath, String permLinkType)  throws Exception	{
		
		String templateInstanciationParentId = null;
		String portalPersistentName = null;
		
		if(  ctx.getRequest() != null){
			Window window = (Window) ctx.getRequest().getAttribute("pia.window");
			if (window != null) {
				Page page = window.getPage();
				if( !page.getPortal().getId().equals(ctx.getControllerCtx().getController().getPortalObjectContainer().getContext().getDefaultPortal().getId()))
				
				 portalPersistentName = page.getPortal().getName();
				
				if (page instanceof ITemplatePortalObject) {
					templateInstanciationParentId = URLEncoder.encode(
							page.getParent().getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

				}
			}
		}
		
		PermLinkCommand linkCmd = new PermLinkCommand(permLinkRef, params, templateInstanciationParentId, cmsPath, permLinkType, portalPersistentName);
		URLContext urlContext = ctx.getControllerCtx().getServerInvocation().getServerContext().getURLContext();
		
		urlContext = urlContext.withAuthenticated(false);
		
		
		String permLinkUrl = ctx.getControllerCtx().renderURL(linkCmd, urlContext, URLFormat.newInstance(false, true));

		return permLinkUrl;
	}	
	
	// API simplifiée
	
	public String getExecutePortletLink(RenderRequest request,  String portletInstance, Map<String, String> windowProperties, Map<String, String> params) throws Exception	{
		

		String region = "virtual";
		String windowName = "dynamicPortlet";
		
		ControllerContext ctx = (ControllerContext) request.getAttribute("pia.controller");
		Window window = (Window) request.getAttribute("pia.window");
		Page page = window.getPage();
		String pageId = URLEncoder.encode(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

		ControllerCommand cmd = new StartDynamicWindowCommand();
		PortalURL portalURL  = new PortalURLImpl(cmd, ctx, null, null);
		
		// Valeurs par défaut
		
		if( windowProperties.get("pia.hideDecorators") == null)
			windowProperties.put("pia.hideDecorators", "1");
		if( windowProperties.get("theme.dyna.partial_refresh_enabled") == null)
			windowProperties.put("theme.dyna.partial_refresh_enabled", "false");		
		
		String url = portalURL.toString();
		url +="&pageId="+pageId+"&regionId="+region+"&instanceId="+portletInstance+"&windowName="+windowName+"&props="+WindowPropertiesEncoder.encodeProperties(windowProperties)+"&params="+WindowPropertiesEncoder.encodeProperties(params)+"&addToBreadcrumb="+ addToBreadcrumb(request);
		return url;
	
		
	
	}
	
	
	
	
	public String getStartPageUrl(PortalControllerContext ctx, String parentName, String pageName, String templateName,
			Map<String, String> props, Map<String, String> params) throws Exception	{
		
		ControllerCommand cmd = new StartDynamicPageCommand();
		PortalURL portalURL  = new PortalURLImpl(cmd, ctx.getControllerCtx(), null, null);
		
		String parentId = URLEncoder.encode(PortalObjectId.parse(parentName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");
		String templateId = URLEncoder.encode(PortalObjectId.parse(templateName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");		
		
		String url = portalURL.toString();
		url +="&parentId="+parentId+"&pageName="+pageName+"&templateId="+templateId+"&props="+WindowPropertiesEncoder.encodeProperties(props)+"&params="+WindowPropertiesEncoder.encodeProperties(params);
		return url;
	}
	
	
	
	public String getStartPageUrl(PortalControllerContext ctx, String pageName, String templateName,
			Map<String, String> props, Map<String, String> params) throws Exception	{
		
		String portalName = PageProperties.getProperties().getPagePropertiesMap().get("portalName");
		if (portalName == null)
			portalName = "default";
		
		portalName = "/" + portalName;
		
		return getStartPageUrl( ctx,  portalName,  pageName,  templateName,
				 props, params);

	}
	
	public String getDestroyPageUrl(PortalControllerContext ctx,String parentId, String pageId)	{
		ControllerCommand cmd = new StopDynamicPageCommand();
		PortalURL portalURL  = new PortalURLImpl(cmd, ctx.getControllerCtx(), null, null);
		
		String url = portalURL.toString();		
		url +="&parentId="+parentId+"&pageId="+pageId;
		return url;
	}

}
