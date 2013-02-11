package org.osivia.portal.core.portlets.interceptors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPath.CanonicalFormat;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.ResourceInvocation;
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.UpdateNavigationalStateResponse;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.path.PortletPathItem;

import org.osivia.portal.core.customization.ICustomizationService;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;

/**
 * Ajout des attributs spécifiques au PIA dans les requêtes des portlets
 */
public class ParametresPortletInterceptor extends PortletInvokerInterceptor {

	private static Log logger = LogFactory.getLog(ParametresPortletInterceptor.class);
	
	public ICustomizationService customizationService;
	

	public ICustomizationService getCustomizationService() {
		return customizationService;
	}


	public void setCustomizationService(ICustomizationService customizationService) {
		this.customizationService = customizationService;
	}


	public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException {

		ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");

		Window window = null;


		if (ctx != null) {

			Map<String, Object> attributes = invocation.getRequestAttributes();
			if (attributes == null)
				attributes = new HashMap<String, Object>();

			// Ajout de la window
			String windowId = invocation.getWindowContext().getId();
			if (windowId.charAt(0) == CanonicalFormat.PATH_SEPARATOR) {
				PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);

				window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);

				attributes.put("osivia.window", window);

				logger.debug("windowId " + windowId);

				if (window.getDeclaredProperty("osivia.cms.uri") != null) {
					logger.debug("osivia.cms.uri " + window.getDeclaredProperty("osivia.cms.uri"));
				}
				if (window.getDeclaredProperty("osivia.cms.scope") != null) {
					logger.debug("osivia.cms.scope " + window.getDeclaredProperty("osivia.cms.scope"));
				}

			}

			if ("wizzard".equals(ctx.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.windowSettingMode")))
				attributes.put("osivia.window.wizzard", "true");

			// Ajout de l'identifiant CMS
			String contentId = (String) ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.content.id");
			if (contentId != null)
				attributes.put("osivia.content.id", contentId);

			// Ajout du controleur
			attributes.put("osivia.controller", ctx);

			// Ajout du mode admin
			if (PageCustomizerInterceptor.isAdministrator(ctx))
				attributes.put("osivia.isAdministrator", "true");

			// Pour l'instant les pages markers ne sont pas gérés pour les
			// ressources
			if (!(invocation instanceof ResourceInvocation))
				attributes.put("osivia.pageMarker", PageMarkerUtils.getCurrentPageMarker(ctx));

			// v 1.0.14 : gestion de la barre de menu
			if (!(invocation instanceof ResourceInvocation)) {

				List<MenubarItem> menuBar = new ArrayList<MenubarItem>();

				attributes.put("osivia.menuBar", menuBar);
			}

			// v2.0 : user datas
			Map<String, Object> userDatas = (Map<String, Object>) ctx.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.userDatas");
			if (userDatas != null)
				attributes.put("osivia.userDatas", userDatas);

			invocation.setRequestAttributes(attributes);
		}

		
		
		
		PortletInvocationResponse response = super.invoke(invocation);
		

		
	
		

		if (response instanceof FragmentResponse) {
			
			String windowId = invocation.getWindowContext().getId();			
			
				
			if (windowId.charAt(0) == CanonicalFormat.PATH_SEPARATOR) {
				
				
	
				FragmentResponse fr = (FragmentResponse) response;

				String updatedFragment = fr.getChars();

				Map<String, Object> attributes = ((FragmentResponse) response).getAttributes();

				/* breadcrumb path set by portlet */

				List<PortletPathItem> portletPath = (List<PortletPathItem>) attributes.get("osivia.portletPath");
				if (portletPath != null) {
					if (invocation.getWindowState().equals(WindowState.MAXIMIZED)) {
						ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.portletPath", portletPath);
					}
				}
				
				// TEST V2 PERMALINK
				/*
				
				String portletCMSPath =  (String) attributes.get("osivia.cms.portletContentPath");
				if (portletCMSPath != null) {
					if (invocation.getWindowState().equals(WindowState.MAXIMIZED)) {
						ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.cms.portletContentPath", portletCMSPath);
					}
				}
				*/


				/* v 1.0.14 : affichage d'une barre de menu */

				if (Boolean.TRUE.equals(ctx.getAttribute(Scope.REQUEST_SCOPE, "osivia.showMenuBarItem"))) {

					ArrayList<MenubarItem> menuBar = (ArrayList<MenubarItem>) attributes.get("osivia.menuBar");

					if (menuBar != null) {

						String title = window.getDeclaredProperty("osivia.title");
						if (title == null)
							title = fr.getTitle();

						// v1.0.14 : ajout impression
						String printPortlet = window.getDeclaredProperty("osivia.printPortlet");
						if (printPortlet == null)
							if (WindowState.MAXIMIZED.equals(invocation.getWindowState()))
								printPortlet = "1";

						
						if ("1".equals(printPortlet)) {

							// Appel module custom PRINT
							Map<String, Object> customAttrMap = new HashMap<String, Object>();
							customAttrMap.put("title", title);
							customAttrMap.put("menuBar", menuBar);
							customAttrMap.put("windowId", windowId);
							customAttrMap.put("themePath", ctx.getAttribute(Scope.REQUEST_SCOPE, "osivia.themePath"));

							CustomizationContext customCtx = new CustomizationContext(customAttrMap);
							customizationService.customize("MENUBAR_PRINT_ITEM", customCtx);
							
							MenubarItem printItem = (MenubarItem) customAttrMap.get("result");
							if( printItem == null){
							String jsTitle = StringEscapeUtils.escapeJavaScript(title);

							 printItem =  new MenubarItem("PRINT", "Imprimer", 100, "#", "popup2print('" + jsTitle + "', '" + windowId + "_print');",	"portlet-menuitem-print", null);
							}
							
							menuBar.add(printItem);

						}

						if (menuBar.size() > 0) {

							ArrayList<MenubarItem> sortedItems = (ArrayList<MenubarItem>) menuBar.clone();
							Collections.sort(sortedItems, new Comparator<MenubarItem>() {

								public int compare(MenubarItem e1, MenubarItem e2) {

									return e1.getOrder() > e2.getOrder() ? 1 : -1;
								}
							});

							StringBuffer topBar = new StringBuffer();
							String bottomBar = "";

							topBar.append("<p class=\"portlet-action-link\">");
							for (MenubarItem menuItem : sortedItems) {
								topBar.append("<a");
								if (menuItem.getOnClickEvent() != null)
									topBar.append(" onclick=\"" + menuItem.getOnClickEvent() + "\"");

								if (menuItem.getUrl() != null)
									topBar.append(" href=\"" + menuItem.getUrl() + "\"");

								if (menuItem.getTarget() != null)
									topBar.append(" target=\"" + menuItem.getTarget() + "\"");

								if (menuItem.getTitle() != null)
									topBar.append(" title=\"" + menuItem.getTitle() + "\"");

								String className = "";

								if (menuItem.getClassName() != null)
									className += menuItem.getClassName();

								if (menuItem.isAjaxDisabled() == true)
									className += " no-ajax-link";

								if (className.length() > 0)
									topBar.append(" class=\"" + "portlet-menuitem " + className + "\"");

								topBar.append(">");

								if (menuItem.getTitle() != null)
									topBar.append(" " + menuItem.getTitle());
								topBar.append("</a>");
							}
							topBar.append("</p>");

							if ("1".equals(printPortlet)) {
								topBar.append("<div id=\"" + windowId + "_print\" class=\"portlet-print-box\">");

								bottomBar = "</div>";
							}

							updatedFragment = topBar.toString() + updatedFragment + bottomBar;
						}
					}

				}// if showbar
				

				return new FragmentResponse(fr.getProperties(), fr.getAttributes(), fr.getContentType(), fr.getBytes(), updatedFragment,
						fr.getTitle(), fr.getCacheControl(), fr.getNextModes());

			}
		}

		// On teste si le portlet fait un modification d'état de la page en mode
		// AJAX
		if (response instanceof UpdateNavigationalStateResponse) {

			Map<String, Object> attributes = ((UpdateNavigationalStateResponse) response).getAttributes();
			String synchro = (String) attributes.get("osivia.refreshPage");

			if ("true".equals(synchro))
				ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.refreshPage", "true");

			if ("true".equals((String) attributes.get("osivia.unsetMaxMode")))
				ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.unsetMaxMode", "true");

		}

		return response;
	}

}
