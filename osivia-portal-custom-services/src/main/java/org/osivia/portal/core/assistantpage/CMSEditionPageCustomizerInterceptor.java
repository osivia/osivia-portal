/**
 * 
 */
package org.osivia.portal.core.assistantpage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityServiceController;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.security.AuthorizationDomainRegistry;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CMSServiceLocator;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.profils.IProfilManager;

public class CMSEditionPageCustomizerInterceptor extends ControllerInterceptor {

	private String targetContextPath;

	private String pageSettingPath;

	private LayoutService serviceLayout;

	private ThemeService serviceTheme;

	private InstanceContainer instanceContainer;

	private IdentityServiceController identityServiceController;

	private RoleModule roleModule;

	private AuthorizationDomainRegistry authorizationDomainRegisrty;

	private PortalObjectContainer portalObjectContainer;

	private IProfilManager profilManager;

	private PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;

	ICMSService cmsService;

	private static ICMSServiceLocator cmsServiceLocator;

	public static ICMSService getCMSService() throws Exception {

		if (cmsServiceLocator == null) {
			cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
		}

		return cmsServiceLocator.getCMSService();

	}

	public PortalAuthorizationManagerFactory getPortalAuthorizationManagerFactory() {
		return portalAuthorizationManagerFactory;
	}

	public void setPortalAuthorizationManagerFactory(PortalAuthorizationManagerFactory portalAuthorizationManagerFactory) {
		this.portalAuthorizationManagerFactory = portalAuthorizationManagerFactory;
	}

	public IProfilManager getProfilManager() {
		return profilManager;
	}

	public void setProfilManager(IProfilManager profilManager) {
		this.profilManager = profilManager;
	}

	private void injectCMSPortletSetting(ControllerRequestDispatcher rd, Portal portal, Page page, PageRendition rendition, ControllerContext ctx) throws Exception {

		HttpServletRequest request = ctx.getServerInvocation().getServerContext().getClientRequest();

		List<Window> windows = new ArrayList<Window>();

		String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
		PortalLayout pageLayout = getServiceLayout().getLayout(layoutId, true);

		synchronizeRegionContexts(rendition, page);

		// Get edit authorization
		CMSServiceCtx cmsContext = new CMSServiceCtx();
		cmsContext.setServerInvocation(ctx.getServerInvocation());

		String pagePath = (String) ctx.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path");

		CMSPublicationInfos pubInfos = getCMSService().getPublicationInfos(cmsContext, pagePath);

		if (pubInfos.isEditableByUser()) {
			
			// Get live document
			cmsContext.setDisplayLiveVersion("1");
			CMSItem liveDoc = getCMSService().getContent(cmsContext, pagePath);

			for (Object regionCtxObjet : rendition.getPageResult().getRegions()) {

				RegionRendererContext renderCtx = (RegionRendererContext) regionCtxObjet;

				// on vérifie que cette réion fait partie du layout
				// (elle contient des portlets)
				if (pageLayout.getLayoutInfo().getRegionNames().contains(renderCtx.getId())) {

					String regionId = renderCtx.getId();

					Map regionPorperties = renderCtx.getProperties();

					regionPorperties.put("osivia.cmsEditionMode", "preview");

					// TODO : externaliser Nuxeo
					String nuxeoPublicHost = System.getProperty("nuxeo.publicHost");
					String nuxeoPublicPort = System.getProperty("nuxeo.publicPort");
					String nuxeoPrivateHost = System.getProperty("nuxeo.privateHost");
					String nuxeoPrivatePort = System.getProperty("nuxeo.privatePort");
					String nuxeoCtx = "/nuxeo";

					URI uri = null;

					try {
						uri = new URI("http://" + nuxeoPublicHost + ":" + nuxeoPublicPort + nuxeoCtx);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					String url = uri.toString() + "/nxpath/default" + liveDoc.getPath() + "@view_documents?tabIds=%3AATAB_FOLDER_EDIT&conversationId=0NXMAIN5";

					regionPorperties.put("osivia.addCMSFragmentUrl", url);

					// Le mode Ajax est incompatble avec le mode "admin"
					// Le passage du mode admin en mode normal ,'est pas bien
					// géré
					// par le portail, quand il s'agit d'une requête Ajax
					DynaRenderOptions.NO_AJAX.setOptions(regionPorperties);

					for (Object windowCtx : renderCtx.getWindows()) {

						WindowRendererContext wrc = (WindowRendererContext) windowCtx;
						Map windowPorperties = wrc.getProperties();
						String windowId = wrc.getId();

						if (!windowId.endsWith("PIA_EMPTY")) {

							URLContext urlContext = ctx.getServerInvocation().getServerContext().getURLContext();

							PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
							Window window = (Window) getPortalObjectContainer().getObject(poid);

							if ("1".equals(window.getDeclaredProperty("osivia.dynamic.cmsEditable"))) {

								/*
								 * TODO
								 * windowPorperties.put("osivia.windowSettingMode"
								 * , "wizzard");
								 * 
								 * // Commande suppression
								 * windowPorperties.put("osivia.destroyUrl",
								 * "displayWindowDelete('" + windowId +
								 * "');return false;");
								 * 
								 * // Commande paramètres
								 * windowPorperties.put("osivia.settingUrl",
								 * "displayWindowSettings('" + windowId +
								 * "');return false;");
								 * 
								 * windows.add(window);
								 * 
								 * // gestion des déplacements
								 * 
								 * MoveWindowCommand upC = new
								 * MoveWindowCommand(windowId, "up"); String
								 * upUrl = ctx.renderURL(upC, urlContext,
								 * URLFormat.newInstance(true, true));
								 * windowPorperties.put("osivia.upUrl", upUrl);
								 * 
								 * MoveWindowCommand downC = new
								 * MoveWindowCommand(windowId, "down"); String
								 * downUrl = ctx.renderURL(downC, urlContext,
								 * URLFormat.newInstance(true, true));
								 * windowPorperties.put("osivia.downUrl",
								 * downUrl);
								 * 
								 * MoveWindowCommand previousC = new
								 * MoveWindowCommand(windowId,
								 * "previousRegion"); String previousRegionUrl =
								 * ctx.renderURL(previousC, urlContext,
								 * URLFormat.newInstance(true, true));
								 * windowPorperties
								 * .put("osivia.previousRegionUrl",
								 * previousRegionUrl);
								 * 
								 * MoveWindowCommand nextRegionC = new
								 * MoveWindowCommand(windowId, "nextRegion");
								 * String nextRegionUrl =
								 * ctx.renderURL(nextRegionC, urlContext,
								 * URLFormat.newInstance(true, true));
								 * windowPorperties.put("osivia.nextRegionUrl",
								 * nextRegionUrl);
								 * 
								 * //
								 * 
								 * String instanceDisplayName = null;
								 * InstanceDefinition defInstance =
								 * getInstanceContainer().getDefinition(
								 * window.getContent().getURI()); if
								 * (defInstance != null) instanceDisplayName =
								 * defInstance
								 * .getDisplayName().getString(request
								 * .getLocale(), true);
								 * 
								 * if (instanceDisplayName != null)
								 * windowPorperties
								 * .put("osivia.instanceDisplayName",
								 * instanceDisplayName);
								 */

							}

						}
					}
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public ControllerResponse invoke(ControllerCommand cmd) throws Exception {

		ControllerResponse resp = (ControllerResponse) cmd.invokeNext();

		if (resp instanceof PageRendition && cmd instanceof PageCommand) {

			// test si mode assistant activé
			if (!"preview".equals(cmd.getControllerContext().getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.cmsEditionMode")))
				return resp;

			PageRendition rendition = (PageRendition) resp;
			PageCommand rpc = (PageCommand) cmd;
			Page page = rpc.getPage();
			Portal portal = (Portal) rpc.getPage().getPortal();
			ControllerContext ctx = cmd.getControllerContext();
			HttpServletRequest request = cmd.getControllerContext().getServerInvocation().getServerContext().getClientRequest();

			// This is for inject the pageSettings
			ControllerRequestDispatcher rd = cmd.getControllerContext().getRequestDispatcher(getTargetContextPath(), getPageSettingPath());

			if (page instanceof ITemplatePortalObject) {

				injectCMSPortletSetting(rd, portal, page, rendition, ctx);

			}

			rd.include();

		}

		return resp;
	}

	/**
	 * Synchronize context regions with layout
	 * 
	 * if a region is not present in the context, creates a new one
	 * 
	 * @param rendition
	 * @param page
	 * @throws Exception
	 */

	// TODO : mutualiser avec mode EDITION PAGE (AssistantPageCustomizer)
	private void synchronizeRegionContexts(PageRendition rendition, Page page) throws Exception {

		String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
		PortalLayout layout = getServiceLayout().getLayout(layoutId, true);

		for (Object region : layout.getLayoutInfo().getRegionNames()) {

			String regionName = (String) region;
			RegionRendererContext renderCtx = rendition.getPageResult().getRegion(regionName);
			if (renderCtx == null) {
				/* Empty region - must create blank window */

				Map<String, String> windowProps = new HashMap<String, String>();
				windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
				windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
				windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");

				WindowResult wr = new WindowResult("PIA_EMPTY", "", Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
				WindowContext settings = new WindowContext(regionName + "_PIA_EMPTY", regionName, "0", wr);
				rendition.getPageResult().addWindowContext(settings);

				renderCtx = rendition.getPageResult().getRegion2(regionName);

			}

		}
	}

	/**
	 * @return the serviceLayout
	 */
	public LayoutService getServiceLayout() {
		return serviceLayout;
	}

	/**
	 * @param serviceLayout
	 *            the serviceLayout to set
	 */
	public void setServiceLayout(LayoutService serviceLayout) {
		this.serviceLayout = serviceLayout;
	}

	/**
	 * @return the pageSettingPath
	 */
	public String getPageSettingPath() {
		return pageSettingPath;
	}

	/**
	 * @param pageSettingPath
	 *            the pageSettingPath to set
	 */
	public void setPageSettingPath(String pageSettingPath) {
		this.pageSettingPath = pageSettingPath;
	}

	/**
	 * @return the roleModule
	 */
	public RoleModule getRoleModule() throws Exception {
		if (roleModule == null) {
			roleModule = (RoleModule) getIdentityServiceController().getIdentityContext().getObject(IdentityContext.TYPE_ROLE_MODULE);
		}
		return roleModule;
	}

	/**
	 * @param roleModule
	 *            the roleModule to set
	 */
	public void setRoleModule(RoleModule roleModule) {
		this.roleModule = roleModule;
	}

	/**
	 * @return the authorizationDomainRegisrty
	 */
	public AuthorizationDomainRegistry getAuthorizationDomainRegisrty() {
		return authorizationDomainRegisrty;
	}

	/**
	 * @param authorizationDomainRegisrty
	 *            the authorizationDomainRegisrty to set
	 */
	public void setAuthorizationDomainRegisrty(AuthorizationDomainRegistry authorizationDomainRegisrty) {
		this.authorizationDomainRegisrty = authorizationDomainRegisrty;
	}

	/**
	 * @return the identityServiceController
	 */
	public IdentityServiceController getIdentityServiceController() {
		return identityServiceController;
	}

	/**
	 * @param identityServiceController
	 *            the identityServiceController to set
	 */
	public void setIdentityServiceController(IdentityServiceController identityServiceController) {
		this.identityServiceController = identityServiceController;
	}

	/**
	 * @return the targetContextPath
	 */
	public String getTargetContextPath() {
		return targetContextPath;
	}

	/**
	 * @param targetContextPath
	 *            the targetContextPath to set
	 */
	public void setTargetContextPath(String targetContextPath) {
		this.targetContextPath = targetContextPath;
	}

	/**
	 * @return the instanceContainer
	 */
	public InstanceContainer getInstanceContainer() {
		return instanceContainer;
	}

	/**
	 * @param instanceContainer
	 *            the instanceContainer to set
	 */
	public void setInstanceContainer(InstanceContainer instanceContainer) {
		this.instanceContainer = instanceContainer;
	}

	/**
	 * @return the serviceTheme
	 */
	public ThemeService getServiceTheme() {
		return serviceTheme;
	}

	/**
	 * @param serviceTheme
	 *            the serviceTheme to set
	 */
	public void setServiceTheme(ThemeService serviceTheme) {
		this.serviceTheme = serviceTheme;
	}

	public PortalObjectContainer getPortalObjectContainer() {
		return portalObjectContainer;
	}

	public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
		this.portalObjectContainer = portalObjectContainer;
	}

}
