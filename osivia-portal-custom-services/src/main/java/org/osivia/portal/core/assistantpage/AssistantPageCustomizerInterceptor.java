/**
 * 
 */
package org.osivia.portal.core.assistantpage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityServiceController;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.security.AuthorizationDomainRegistry;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
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
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.page.PageUtils;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.DynamicWindow;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;



public class AssistantPageCustomizerInterceptor extends ControllerInterceptor implements IFormatter {

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
	
	ICMSService cmsService ;
	
	
	
	private static ICMSServiceLocator cmsServiceLocator ;

	public static ICMSService getCMSService() throws Exception {
		
		if( cmsServiceLocator == null){
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

	private class InstanceComparator implements Comparator<InstanceDefinition> {

		HttpServletRequest compRequest;

		public InstanceComparator(HttpServletRequest compRequest) {
			this.compRequest = compRequest;
		}

		public int compare(InstanceDefinition e1, InstanceDefinition e2) {

			String n1 = e1.getDisplayName().getString(compRequest.getLocale(), true);
			if (n1 == null)
				n1 = e1.getId();
			String n2 = e2.getDisplayName().getString(compRequest.getLocale(), true);
			if (n2 == null)
				n2 = e2.getId();

			return n1.toUpperCase().compareTo(n2.toUpperCase());
		}

	}

	public IProfilManager getProfilManager() {
		return profilManager;
	}

	public void setProfilManager(IProfilManager profilManager) {
		this.profilManager = profilManager;
	}


	
	public String formatInheritedCheckVakue(PortalObject po, String selectName, String propertyName, String selectedValue) throws Exception {

		Map<String, String> supportedValue = new LinkedHashMap<String, String>();
		
		supportedValue.put("0", "Non");
		supportedValue.put("1", "Oui");
			

		StringBuffer select = new StringBuffer();
		
		String disabled = "";
		if(StringUtils.isNotEmpty((String) po.getDeclaredProperty("osivia.cms.basePath")))
			disabled = "disabled='disabled'";
		
		select.append("<select name=\""+selectName+"\"" + disabled + ">");

		if (!supportedValue.isEmpty()) {
			
			/* Héritage */
			
			String parentScope = (String) po.getParent().getProperty(propertyName);
			String inheritedLabel = null;
			if( parentScope != null){
				inheritedLabel = supportedValue.get(parentScope);
			};
			if( inheritedLabel == null)
				inheritedLabel = "Non";
			inheritedLabel =  "Herité [" + inheritedLabel +"]";	
				
			
			if (selectedValue == null || selectedValue.length() == 0) {

				select.append("<option selected=\"selected\" value=\"\">"+inheritedLabel+"</option>");

			} else {

				select.append("<option value=\"\">"+inheritedLabel+"</option>");

			}
			for (String possibleValue : supportedValue.keySet()) {
				if (selectedValue != null && selectedValue.length() != 0 && possibleValue.equals(selectedValue)) {

					select.append("<option selected=\"selected\" value=\"" + possibleValue + "\">"
							+ supportedValue.get(possibleValue) + "</option>");

				} else {

					select.append("<option value=\"" + possibleValue + "\">" + supportedValue.get(possibleValue) + "</option>");

				}
			}
		}

		select.append("</select>");

		return select.toString();

	}
	
	
	

	public String formatContextualization(PortalObject po, String selectedValue) throws Exception {


		Map<String, String> contextualization = new LinkedHashMap<String, String>();
		
		contextualization.put(IPortalUrlFactory.CONTEXTUALIZATION_PORTLET, "Mode portlet");
		contextualization.put(IPortalUrlFactory.CONTEXTUALIZATION_PAGE, "Mode page");
		contextualization.put(IPortalUrlFactory.CONTEXTUALIZATION_PORTAL, "Mode portail");
		
		StringBuffer select = new StringBuffer();
		select.append("<select name=\"contextualization\">");

			
			/* Héritage */
			PortalObject parent = po.getParent();
			String parentScope = (String) parent.getProperty("osivia.cms.contextualization");
			String inheritedLabel = null;
			if( parentScope != null){
				inheritedLabel = contextualization.get(parentScope);
			};
			if( inheritedLabel == null)	{
					inheritedLabel = "Mode portlet";

			}
			inheritedLabel =  "Herité [" + inheritedLabel +"]";	
			

			
			
			if (selectedValue == null || selectedValue.length() == 0) {

				select.append("<option selected=\"selected\" value=\"\">"+inheritedLabel+"</option>");

			} else {

				select.append("<option value=\"\">"+inheritedLabel+"</option>");

			}
			for (String possibleContextualization : contextualization.keySet()) {
				if (selectedValue != null && selectedValue.length() != 0 && possibleContextualization.equals(selectedValue)) {

					select.append("<option selected=\"selected\" value=\"" + possibleContextualization + "\">"
							+ contextualization.get(possibleContextualization) + "</option>");

				} else {

					select.append("<option value=\"" + possibleContextualization + "\">" + contextualization.get(possibleContextualization) + "</option>");

				}
			}


		select.append("</select>");

		return select.toString();

	}
	
	
	public String formatScopeList(PortalObject po, String scopeName, String selectedScope) throws Exception {

		// On sélectionne les profils ayant un utilisateur Nuxeo
		List<ProfilBean> profils = getProfilManager().getListeProfils();

		Map<String, String> scopes = new LinkedHashMap<String, String>();
		
		scopes.put("anonymous", "Anonyme");
		
		String parentScope = (String) po.getParent().getProperty("osivia.cms.scope");
		String inheritedLabel = null;
		if( parentScope != null){
			inheritedLabel = scopes.get(parentScope);
		};
		if( inheritedLabel == null)
			inheritedLabel = "Pas de cache";
		inheritedLabel =  "Herité [" + inheritedLabel +"]";	
		
		scopes.put("__inherited", inheritedLabel);


		for (ProfilBean profil : profils) {
			if (profil.getNuxeoVirtualUser() != null && profil.getNuxeoVirtualUser().length() > 0) {
				scopes.put(profil.getName(), "Profil " + profil.getName());
			}
		}

			

		StringBuffer select = new StringBuffer();
		
		String disabled = "";
		if(StringUtils.isNotEmpty((String) po.getDeclaredProperty("osivia.cms.basePath"))){
			disabled = "disabled='disabled'";
		}
		
		select.append("<select name=\""+scopeName+"\"" + disabled + ">");

		if (!scopes.isEmpty()) {
			
			if (selectedScope == null || selectedScope.length() == 0) {

				select.append("<option selected=\"selected\" value=\"\">Pas de cache</option>");

			} else {

				select.append("<option value=\"\">Pas de cache</option>");

			}
			for (String possibleScope : scopes.keySet()) {
				if (selectedScope != null && selectedScope.length() != 0 && possibleScope.equals(selectedScope)) {

					select.append("<option selected=\"selected\" value=\"" + possibleScope + "\">"
							+ scopes.get(possibleScope) + "</option>");

				} else {

					select.append("<option value=\"" + possibleScope + "\">" + scopes.get(possibleScope) + "</option>");

				}
			}
		}

		select.append("</select>");

		return select.toString();

	}
	

	
	
	public String formatDisplayLiveVersionList(CMSServiceCtx cmxCtx, PortalObject po, String versionName, String selectedVersion ) throws Exception {

		Map<String, String> versions = new LinkedHashMap<String, String>();
		
		versions.put("1", "Live");
		

		String inheritedLabel = null;
		
		/* Calcul du label hérité */
		
		
		if( inheritedLabel == null)	{

			
			Page page = null;

			if (po instanceof Page)
				page = (Page) po;
			if (po instanceof Window)
				page = (Page) po.getParent();

				String spacePath = page.getProperty("osivia.cms.basePath");

				if (spacePath != null) {
					// Publication par path

					CMSItem publishSpaceConfig = getCMSService().getPublicationConfig(cmxCtx, spacePath);
					if( publishSpaceConfig != null)	{
						
						String displayLiveVersion = publishSpaceConfig.getProperties().get("displayLiveVersion");
						
						if( displayLiveVersion != null)
							inheritedLabel = versions.get(displayLiveVersion);

					}
					
					
				}	else	{
					//Heriatge page parent
					String parentVersion = (String) po.getParent().getProperty("osivia.cms.displayLiveVersion");		
					if( parentVersion != null){
						
						inheritedLabel = versions.get(parentVersion);
	
					};
					
				}
		
		}
		
		if( inheritedLabel == null)
			inheritedLabel =  "Publié";
		
		
		inheritedLabel =  "Herité [" + inheritedLabel +"]";	
		
		versions.put("__inherited", inheritedLabel);

			

		StringBuffer select = new StringBuffer();
		
		String disabled = "";
		if(StringUtils.isNotEmpty((String) po.getDeclaredProperty("osivia.cms.basePath"))){
			disabled = "disabled='disabled'";
		}
		
		select.append("<select name=\""+versionName+"\"" + disabled + ">");

		if (!versions.isEmpty()) {
			
			if (selectedVersion == null || selectedVersion.length() == 0) {

				select.append("<option selected=\"selected\" value=\"\">Publiée</option>");

			} else {

				select.append("<option value=\"\">Publiée</option>");

			}
			for (String possibleVersion : versions.keySet()) {
				if (selectedVersion != null && selectedVersion.length() != 0 && possibleVersion.equals(selectedVersion)) {

					select.append("<option selected=\"selected\" value=\"" + possibleVersion + "\">"
							+ versions.get(possibleVersion) + "</option>");

				} else {

					select.append("<option value=\"" + possibleVersion + "\">" + versions.get(possibleVersion) + "</option>");

				}
			}
		}

		select.append("</select>");

		return select.toString();

	}
	

	public String formatPortletFilterScopeList(String name, String selectedScope) throws Exception {


		
		// On sélectionne les profils ayant un utilisateur Nuxeo
			List<ProfilBean> profils = getProfilManager().getListeProfils();

		Map<String, String> scopes = new LinkedHashMap<String, String>();


		for (ProfilBean profil : profils) 
				scopes.put(profil.getName(), "Profil " + profil.getName());
		

		StringBuffer select = new StringBuffer();
		select.append("<select name=\""+name+"\">");

		if (!scopes.isEmpty()) {
			
			
			if (selectedScope == null || selectedScope.length() == 0) {

				select.append("<option selected=\"selected\" value=\"\">Tous les profils</option>");

			} else {

				select.append("<option value=\"\">Tous les profils</option>");

			}
			for (String possibleScope : scopes.keySet()) {
				if (selectedScope != null && selectedScope.length() != 0 && possibleScope.equals(selectedScope)) {

					select.append("<option selected=\"selected\" value=\"" + possibleScope + "\">"
							+ scopes.get(possibleScope) + "</option>");

				} else {

					select.append("<option value=\"" + possibleScope + "\">" + scopes.get(possibleScope) + "</option>");

				}
			}
		}

		select.append("</select>");

		return select.toString();

	}
	

	
	
	
	private void injectPortletSetting( ControllerRequestDispatcher rd, Portal portal, Page page, PageRendition rendition, ControllerContext ctx	) throws Exception	{
		
		HttpServletRequest request = ctx.getServerInvocation().getServerContext()
		.getClientRequest();
		
		List<Window> windows = new ArrayList<Window>();

		String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
		PortalLayout pageLayout = getServiceLayout().getLayout(layoutId, true);

		synchronizeRegionContexts(rendition, page);

		for (Object regionCtxObjet : rendition.getPageResult().getRegions()) {

			RegionRendererContext renderCtx = (RegionRendererContext) regionCtxObjet;

			// on vérifie que cette réion fait partie du layout
			// (elle contient des portlets)
			if (pageLayout.getLayoutInfo().getRegionNames().contains(renderCtx.getId())) {

				String regionId = renderCtx.getId();

				Map regionPorperties = renderCtx.getProperties();

				regionPorperties.put("osivia.wizzardMode", "1");
				regionPorperties.put("osivia.addPortletUrl", "displayAddPortlet('" + renderCtx.getId()
						+ "');return false;");

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

						URLContext urlContext = ctx.getServerInvocation().getServerContext()
								.getURLContext();

						PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
						Window window = (Window) getPortalObjectContainer().getObject(poid);

						if (!((DynamicWindow) window).isSessionWindow()) {

							windowPorperties.put("osivia.windowSettingMode", "wizzard");

							// Commande suppression
							windowPorperties.put("osivia.destroyUrl", "displayWindowDelete('" + windowId
									+ "');return false;");

							// Commande paramètres
							windowPorperties.put("osivia.settingUrl", "displayWindowSettings('" + windowId
									+ "');return false;");

							windows.add(window);

							/* gestion des déplacements */

							MoveWindowCommand upC = new MoveWindowCommand(windowId, "up");
							String upUrl = ctx.renderURL(upC, urlContext,
									URLFormat.newInstance(true, true));
							windowPorperties.put("osivia.upUrl", upUrl);

							MoveWindowCommand downC = new MoveWindowCommand(windowId, "down");
							String downUrl = ctx.renderURL(downC, urlContext,
									URLFormat.newInstance(true, true));
							windowPorperties.put("osivia.downUrl", downUrl);

							MoveWindowCommand previousC = new MoveWindowCommand(windowId, "previousRegion");
							String previousRegionUrl = ctx.renderURL(previousC, urlContext,
									URLFormat.newInstance(true, true));
							windowPorperties.put("osivia.previousRegionUrl", previousRegionUrl);

							MoveWindowCommand nextRegionC = new MoveWindowCommand(windowId, "nextRegion");
							String nextRegionUrl = ctx.renderURL(nextRegionC, urlContext,
									URLFormat.newInstance(true, true));
							windowPorperties.put("osivia.nextRegionUrl", nextRegionUrl);

							/* Titre de la fenetre d'administration */

							String instanceDisplayName = null;
							InstanceDefinition defInstance = getInstanceContainer().getDefinition(
									window.getContent().getURI());
							if (defInstance != null)
								instanceDisplayName = defInstance.getDisplayName().getString(request.getLocale(),
										true);

							if (instanceDisplayName != null)
								windowPorperties.put("osivia.instanceDisplayName", instanceDisplayName);

						}

					}
				}
			}
		}

		rd.setAttribute("osivia.setting.windows", windows);

		/* Insertion des styles */

		String stylesProp = portal.getDeclaredProperty("osivia.liste_styles");

		String[] styles = new String[0];
		if (stylesProp != null)
			styles = stylesProp.split(",");

		// Conversion en tableau
		List<String> portalStyles = new ArrayList<String>();
		for (int i = 0; i < styles.length; i++)
			portalStyles.add(styles[i]);

		rd.setAttribute("osivia.setting.windows.PORTAL_STYLES", portalStyles);

		/* Insertion des portlets */

		Collection<InstanceDefinition> definitions = getInstanceContainer().getDefinitions();
		List<InstanceDefinition> listDefinitions = new ArrayList(definitions);
		Collections.sort(listDefinitions, new InstanceComparator(request));

		rd.setAttribute("osivia.setting.portlets", listDefinitions);
}
		
	
	
	
	
	
	private void injectPageSetting( ControllerRequestDispatcher rd, Portal portal, Page page, PageRendition rendition, ControllerContext ctx	) throws Exception {	
	

		HttpServletRequest request = ctx.getServerInvocation().getServerContext()
		.getClientRequest();
	
	
		URLContext urlContext = ctx.getServerInvocation().getServerContext().getURLContext();

		String serverContext = ctx.getServerInvocation().getServerContext()
				.getPortalContextPath();


	/* Changement de layout */

	List<PortalLayout> layouts = new ArrayList<PortalLayout>();
	for (PortalLayout layout : (Collection<PortalLayout>) getServiceLayout().getLayouts()) {
		String layoutName = layout.getLayoutInfo().getName();
		// TODO : enlever ces tests
		if (!layoutName.equals("nodesk") && !layoutName.equals("phalanx") && !layoutName.equals("generic")
				&& !layoutName.equals("3columns") && !layoutName.equals("1column")) {
			layouts.add(layout);
		}
	}
	Collections.sort(layouts, new Comparator<PortalLayout>() {

			    public int compare(PortalLayout o1, PortalLayout o2)
			    {
			         return o1.getLayoutInfo().getName().compareTo(o2.getLayoutInfo().getName());
			    }
			});

	
	rd.setAttribute("osivia.setting.LAYOUT_LIST", layouts);

	if (page.getDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT) != null) {
		rd.setAttribute("osivia.setting.layout.NAME", page.getDeclaredProperty(
				ThemeConstants.PORTAL_PROP_LAYOUT));
	}

	/* Changement de nom */

	String pageName = null;
	if (page.getDisplayName() != null)
		if (page.getDisplayName().getString(request.getLocale(), true) != null)
			pageName = page.getDisplayName().getString(request.getLocale(), true);

	if (pageName == null)
		pageName = page.getName();

	rd.setAttribute("osivia.setting.page.NAME", pageName);

	RenamePageCommand renamepc = new RenamePageCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT),
			null);

	String renamePageUrl = ctx.renderURL(renamepc, urlContext,
			URLFormat.newInstance(true, true));

	rd.setAttribute("osivia.setting.PAGE_ID",
			URLEncoder.encode(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8"));

	rd.setAttribute("osivia.setting.URL", serverContext + "/commands");

	/* Page par défaut */

	if (page
			.getName()
			.equals(page.getPortal().getDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME)))
		rd.setAttribute("osivia.setting.page.DEFAULT_PAGE", "1");
	else
		rd.setAttribute("osivia.setting.page.DEFAULT_PAGE", "0");
	
	//v1.0.17
	
	if ("1".equals(page.getDeclaredProperty("osivia.draftPage")))
		rd.setAttribute("osivia.setting.page.DRAFT_PAGE", "1");
	else
		rd.setAttribute("osivia.setting.page.DRAFT_PAGE", "0");
	

	/* Sécurité */

	rd.setAttribute("osivia.setting.ROLE_AVAIBLE", getProfilManager().getFilteredRoles());

	DomainConfigurator dc = getAuthorizationDomainRegisrty().getDomain("portalobject").getConfigurator();
	Set<RoleSecurityBinding> constraint = dc.getSecurityBindings(page.getId().toString(
			PortalObjectPath.CANONICAL_FORMAT));
	Map<String, Set<String>> actionForRole = new HashMap<String, Set<String>>();
	for (RoleSecurityBinding roleSecurityBinding : constraint) {
		actionForRole.put(roleSecurityBinding.getRoleName(), roleSecurityBinding.getActions());
	}
	rd.setAttribute("osivia.setting.ACTIONS_FOR_ROLE", actionForRole);

	/* Suppression */

	DeletePageCommand dpc = new DeletePageCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT));
	String deletePageUrl = ctx.renderURL(dpc, urlContext,
			URLFormat.newInstance(true, true));
	rd.setAttribute("osivia.setting.DELETE_PAGE_URL", deletePageUrl);

	/* ordre de la page */

	SortedSet<Page> sisters = new TreeSet<Page>(PageUtils.orderComparator);
	for (PortalObject po : page.getParent().getChildren()) {
		if (po instanceof Page) {
			Page sister = (Page) po;
			if (!sister.equals(page)) {
				sisters.add(sister);
			}
		}

	}
	rd.setAttribute("osivia.setting.page.order.sisters", sisters);

	/* modeles */

	SortedSet<Page> models = new TreeSet<Page>(PageUtils.orderComparator);
	for (PortalObject po : page.getParent().getChildren()) {
		if (po instanceof Page) {
			Page model = (Page) po;
			models.add(model);
		}

	}
	rd.setAttribute("osivia.setting.page.create.models", models);


	

	
	/* CMS */


	String pageCmsBasePath = page.getDeclaredProperty("osivia.cms.basePath");

	if (pageCmsBasePath == null)
		pageCmsBasePath = "";

	rd.setAttribute("osivia.setting.page.CMS_BASE_PATH", pageCmsBasePath);
	
	String scope = page.getDeclaredProperty("osivia.cms.scope");
	rd.setAttribute("osivia.setting.page.CMS_SCOPE_SELECT", formatScopeList( page, "scope", scope));
	
	
	CMSServiceCtx cmxCtx = new CMSServiceCtx();
	cmxCtx.setControllerContext(ctx);
	
	String displayLiveVersion = page.getDeclaredProperty("osivia.cms.displayLiveVersion");
	rd.setAttribute("osivia.setting.page.CMS_DISPLAY_LIVE_VERSION_SELECT", formatDisplayLiveVersionList( cmxCtx, page, "displayLiveVersion", displayLiveVersion));
	
	String navigationScope = page.getDeclaredProperty("osivia.cms.navigationScope");
	rd.setAttribute("osivia.setting.page.CMS_NAVIGATION_SCOPE_SELECT", formatScopeList( page, "navigationScope", navigationScope));

	String pageContextualizationSupport = page.getDeclaredProperty("osivia.cms.pageContextualizationSupport");
	rd.setAttribute("osivia.setting.page.PAGE_CONTEXTUALIZATION_SUPPORT_SELECT", formatInheritedCheckVakue( page, "pageContextualizationSupport", "osivia.cms.pageContextualizationSupport", pageContextualizationSupport));

	String outgoingRecontextualizationSupport = page.getDeclaredProperty("osivia.cms.outgoingRecontextualizationSupport");
	rd.setAttribute("osivia.setting.page.OUTGOING_RECONTEXTUALIZATION_SUPPORT_SELECT", formatInheritedCheckVakue( page, "outgoingRecontextualizationSupport", "osivia.cms.outgoingRecontextualizationSupport", outgoingRecontextualizationSupport));
	
	String navigationMode = page.getDeclaredProperty("osivia.navigationMode");
	String cmsNavigationMode = "0";
	if ("cms".equals(navigationMode))
		cmsNavigationMode = "1";

	rd.setAttribute("osivia.setting.page.CMS_NAVIGATION_MODE", cmsNavigationMode);
	
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public ControllerResponse invoke(ControllerCommand cmd) throws Exception {

		ControllerResponse resp = (ControllerResponse) cmd.invokeNext();

		if (resp instanceof PageRendition && cmd instanceof PageCommand ) {
			
			//test si mode assistant activé
			if (!"wizzard".equals(cmd.getControllerContext().getAttribute(ControllerCommand.SESSION_SCOPE,
					"osivia.windowSettingMode")))
				return resp;

			PageRendition rendition = (PageRendition) resp;
			PageCommand rpc = (PageCommand) cmd;
			Page page = rpc.getPage();
			Portal portal = (Portal) rpc.getPage().getPortal();
			ControllerContext ctx = cmd.getControllerContext();
			HttpServletRequest request = cmd.getControllerContext().getServerInvocation().getServerContext()
					.getClientRequest();
			HttpSession session = request.getSession();

			// This is for inject the pageSettings
			ControllerRequestDispatcher rd = cmd.getControllerContext().getRequestDispatcher(getTargetContextPath(),
					getPageSettingPath());
			
			
			URLContext urlContext = cmd.getControllerContext().getServerInvocation().getServerContext().getURLContext();

			String serverContext = cmd.getControllerContext().getServerInvocation().getServerContext()
					.getPortalContextPath();
		
			// url générique de commande
			
			
			rd.setAttribute("osivia.setting.COMMAND_URL", serverContext + "/commands");
			
			
			rd.setAttribute("osivia.setting.FORMATTER", this);
			
			
			if( page instanceof ITemplatePortalObject)	{
				

				
				// Page basée sur un template
				
				Page editablePage = ((ITemplatePortalObject) page).getEditablePage();
				
				if( editablePage != null)
					injectPageSetting( rd, portal, editablePage, rendition,  ctx	);
				
				//injectPortletSetting(rd, portal, page, rendition, ctx);
				
				ViewPageCommand showPage = new ViewPageCommand(((ITemplatePortalObject)page).getTemplate().getId());
				
				String url = new PortalURLImpl(showPage, cmd.getControllerContext(), null, null).toString();
				url = url + "?init-state=true&edit-template-mode=true";
				
				rd.setAttribute("osivia.setting.page.CMS_TEMPLATE_URL", url);

				
				
			}	else	{
				
				
				// pour mémo : soit un template, soit une page classique
				
				injectPageSetting( rd, portal, page, rendition,  ctx	);
				injectPortletSetting(rd, portal, page, rendition, ctx);
				
			
			}
			
			
			/* Initialisation des caches */
			
			ViewPageCommand initCacheCmd = new ViewPageCommand(page.getId());
			String url = new PortalURLImpl(initCacheCmd, ctx, null, null).toString();
			url = url + "?init-cache=true";
			
			rd.setAttribute("osivia.setting.initCachesUrl", url);



			/* Navigation */

			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);

			IDynamicObjectContainer dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class,
					"osivia:service=DynamicPortalObjectContainer");

			/* Navigation */

			try {
				// ignore dynamic pages
				dynamicObjectContainer.startPersistentIteration();

				PortalObjectContainer portalObjectContainer = Locator.findMBean(PortalObjectContainer.class,
						"portal:container=PortalObject");

				PortalObject po = portalObjectContainer.getObject(portal.getId());

				formatTreeStructure(ctx, printWriter, po);
			} finally {
				dynamicObjectContainer.stopPersistentIteration();
			}

			printWriter.flush();
			printWriter.close();
			String nav = stringWriter.toString();

			rd.setAttribute("osivia.setting.navigation", nav);
			
			
			
			
			
			

			rd.include();

			Map<String, String> windowProps = new HashMap<String, String>();

			windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
			windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
			windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
			WindowResult wr = new WindowResult("Page settings", rd.getMarkup(), Collections.EMPTY_MAP, windowProps,
					null, WindowState.NORMAL, Mode.VIEW);
			WindowContext settings = new WindowContext("PageSettings", "pageSettings", "0", wr);
			rendition.getPageResult().addWindowContext(settings);
			Region region = rendition.getPageResult().getRegion2("pageSettings");
			DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());

		}

		return resp;
	}

	
	
	private void formatTreeStructure(ControllerContext controllerCtx, PrintWriter out, PortalObject parent) throws IOException {
		
		
		/* Controle des droits  et tri des pages */
		
		PortalAuthorizationManager pam = portalAuthorizationManagerFactory.getManager();
		SortedSet<Page> sortedPages = new TreeSet<Page>(PageUtils.orderComparator);
		for (PortalObject po : parent.getChildren(PortalObject.PAGE_MASK)) {

			PortalObjectPermission perm = new PortalObjectPermission(po.getId(), PortalObjectPermission.VIEW_MASK);

			if (pam.checkPermission(perm))
				sortedPages.add((Page) po);
		}

		if (sortedPages.size() > 0) {
			out.print("<ul class=\"navigation-list\">");
		}

		Locale locale = Locale.FRENCH;

		for (Page page : sortedPages) {
			out.print("<li class=\"navigation-item\">");

			String name = page.getDisplayName().getString(locale, true);
			if (name == null)
				name = page.getName();
			
			ViewPageCommand showPage = new ViewPageCommand(page.getId());
			
			String url = new PortalURLImpl(showPage, controllerCtx, null, null).toString();
			url = url + "?init-state=true";

			out.print("<a href=\""+ url + "\">"+name+"</a>");
			
			
			formatTreeStructure(controllerCtx, out, page);
			out.print("</li>");
		}

		if (sortedPages.size() > 0) {

			out.print("</ul>");
		}

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

				WindowResult wr = new WindowResult("PIA_EMPTY", "", Collections.EMPTY_MAP, windowProps, null,
						WindowState.NORMAL, Mode.VIEW);
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
			roleModule = (RoleModule) getIdentityServiceController().getIdentityContext().getObject(
					IdentityContext.TYPE_ROLE_MODULE);
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
