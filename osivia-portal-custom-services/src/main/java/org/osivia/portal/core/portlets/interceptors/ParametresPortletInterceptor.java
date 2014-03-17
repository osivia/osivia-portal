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
package org.osivia.portal.core.portlets.interceptors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPath.CanonicalFormat;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.ResourceInvocation;
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.UpdateNavigationalStateResponse;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.path.PortletPathItem;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.customization.ICustomizationService;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.DynamicWindow;

/**
 * Ajout des attributs spécifiques au PIA dans les requêtes des portlets
 */
public class ParametresPortletInterceptor extends PortletInvokerInterceptor {

    private static Log logger = LogFactory.getLog(ParametresPortletInterceptor.class);

    public ICustomizationService customizationService;


    public ICustomizationService getCustomizationService() {
        return this.customizationService;
    }


    public void setCustomizationService(ICustomizationService customizationService) {
        this.customizationService = customizationService;
    }


    @Override
    public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException {

        ControllerContext ctx = (ControllerContext) invocation.getAttribute("controller_context");

        Window window = null;


        if (ctx != null) {

            Map<String, Object> attributes = invocation.getRequestAttributes();
            if (attributes == null) {
                attributes = new HashMap<String, Object>();
            }

            // Ajout de la window
            String windowId = invocation.getWindowContext().getId();
            if (windowId.charAt(0) == CanonicalFormat.PATH_SEPARATOR) {
                PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);

                window = (Window) ctx.getController().getPortalObjectContainer().getObject(poid);

                attributes.put("osivia.window", window);

                attributes.put("osivia.window.ID", window.getId().toString(PortalObjectPath.SAFEST_FORMAT));

                logger.debug("windowId " + windowId);

                if (window.getDeclaredProperty("osivia.cms.uri") != null) {
                    logger.debug("osivia.cms.uri " + window.getDeclaredProperty("osivia.cms.uri"));
                }
                if (window.getDeclaredProperty("osivia.cms.scope") != null) {
                    logger.debug("osivia.cms.scope " + window.getDeclaredProperty("osivia.cms.scope"));
                }


                if (window instanceof DynamicWindow) {
                    String uniqueID = ((DynamicWindow) window).getDynamicUniqueID();
                    if ((uniqueID != null) && (uniqueID.length() > 1)) {
                        invocation.setAttribute("osivia.window.path", windowId);


                        /* Le path CMS identifie de manière unique la session */

                        NavigationalStateContext nsContext = (NavigationalStateContext) ctx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
                        PageNavigationalState pageState = nsContext.getPageNavigationalState(window.getPage().getId().toString());

                        String cmsUniqueID[] = null;
                        if (pageState != null) {
                            cmsUniqueID = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.uniqueID"));
                        }


                        if ((cmsUniqueID != null) && (cmsUniqueID.length == 1)) {
                            uniqueID += "_cms_" + cmsUniqueID[0];
                        }
                        invocation.setAttribute("osivia.window.uniqueID", uniqueID);

                    }
                }


                EditionState editionState = ContributionService.getWindowEditionState(ctx, window.getId());
                attributes.put("osivia.editionState", editionState);


            }

            if ("wizzard".equals(ctx.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.windowSettingMode"))) {
                attributes.put("osivia.window.wizzard", "true");
            }

            // Ajout de l'identifiant CMS
            String contentId = (String) ctx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.content.id");
            if (contentId != null) {
                attributes.put("osivia.content.id", contentId);
            }

            // Ajout du controleur
            attributes.put("osivia.controller", ctx);

            // Ajout du mode admin
            if (PageCustomizerInterceptor.isAdministrator(ctx)) {
                attributes.put(InternalConstants.ADMINISTRATOR_INDICATOR_ATTRIBUTE_NAME, true);
            }

            // Pour l'instant les pages markers ne sont pas gérés pour les
            // ressources
            if (!(invocation instanceof ResourceInvocation)) {
                attributes.put("osivia.pageMarker", PageMarkerUtils.getCurrentPageMarker(ctx));
            }

            // v 1.0.14 : gestion de la barre de menu
            if (!(invocation instanceof ResourceInvocation)) {

                List<MenubarItem> menuBar = new ArrayList<MenubarItem>();

                attributes.put("osivia.menuBar", menuBar);
            }

            // v2.0 : user datas
            Map<String, Object> userDatas = (Map<String, Object>) ctx.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.userDatas");
            if (userDatas != null) {
                attributes.put("osivia.userDatas", userDatas);
            }



            // HTTP Request
            HttpServletRequest httpRequest = ctx.getServerInvocation().getServerContext().getClientRequest();
            attributes.put("osivia.httpRequest", httpRequest);
            
            
            
            Object spaceConfig = ctx.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.spaceConfig");
            if( spaceConfig != null)
                attributes.put("osivia.spaceConfig", spaceConfig);
                


            // Set attributes
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


                /* v 1.0.14 : affichage d'une barre de menu */

                if (Boolean.TRUE.equals(ctx.getAttribute(Scope.REQUEST_SCOPE, "osivia.showMenuBarItem"))) {

                    ArrayList<MenubarItem> menuBar = (ArrayList<MenubarItem>) attributes.get("osivia.menuBar");

                    if (menuBar != null) {

                        String title = window.getDeclaredProperty("osivia.title");
                        if (title == null) {
                            title = fr.getTitle();
                        }


                        PortalObjectId popupWindowId = (PortalObjectId) ctx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID");

                        String printPortlet = null;

                        if (popupWindowId == null) {


                            // v1.0.14 : ajout impression
                            printPortlet = window.getDeclaredProperty("osivia.printPortlet");
                            if (printPortlet == null) {
                                if (WindowState.MAXIMIZED.equals(invocation.getWindowState())) {
                                    printPortlet = "1";
                                }
                            }
                        }


                        if ("1".equals(printPortlet)) {

                            // Appel module custom PRINT
                            Map<String, Object> customAttrMap = new HashMap<String, Object>();
                            customAttrMap.put("title", title);
                            customAttrMap.put("menuBar", menuBar);
                            customAttrMap.put("windowId", windowId);
                            customAttrMap.put("themePath", ctx.getAttribute(Scope.REQUEST_SCOPE, "osivia.themePath"));

                            CustomizationContext customCtx = new CustomizationContext(customAttrMap);
                            this.customizationService.customize("MENUBAR_PRINT_ITEM", customCtx);

                            MenubarItem printItem = (MenubarItem) customAttrMap.get("result");
                            if (printItem == null) {
                                String jsTitle = StringEscapeUtils.escapeJavaScript(title);

                                printItem = new MenubarItem("PRINT", "Imprimer", 100, "#", "popup2print('" + jsTitle + "', '" + windowId + "_print');",
                                        "portlet-menuitem-print", null);
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
                            StringBuffer topMenu = new StringBuffer();
                            StringBuffer stateBar = new StringBuffer();

                            boolean emptyMenu = true;

                            String portletPre = "";
                            String portletPost = "";

                            StringBuffer associatedHTML = new StringBuffer();



                            topMenu.append("<a href=\"#\" class=\"portlet-dropdown-menu portlet-menuitem no-ajax-link\" data-dropdown=\"#");
                            topMenu.append(window.getName());
                            topMenu.append("dropdown-1\" title=\"Afficher ou masquer le menu d'édition\">Menu d'édition</a>");
                            topMenu.append("<div id=\"");
                            topMenu.append(window.getName());
                            topMenu.append("dropdown-1\" class=\"dropdown dropdown-tip\" style=\"display: none;\"><ul class=\"dropdown-menu\">");

                            topBar.append("<p class=\"portlet-action-link\">");


                            for (MenubarItem menuItem : sortedItems) {

                                StringBuffer curBuffer = topBar;

                                if (menuItem.isStateItem()) {
                                    curBuffer = stateBar;
                                } else if (menuItem.isDropdownItem()) {
                                    emptyMenu = false;
                                    curBuffer = topMenu;
                                    curBuffer.append("<li>");
                                }


                                if (StringUtils.isNotBlank(menuItem.getUrl())) {
                                    // Link
                                    curBuffer.append("<a");

                                    // Onclick action
                                    if (menuItem.getOnClickEvent() != null) {
                                        curBuffer.append(" onclick=\"" + menuItem.getOnClickEvent() + "\"");
                                    }

                                    // HREF
                                    curBuffer.append(" href=\"" + menuItem.getUrl() + "\"");

                                    // Target
                                    if (menuItem.getTarget() != null) {
                                        curBuffer.append(" target=\"" + menuItem.getTarget() + "\"");
                                    }

                                    // Title
                                    if (menuItem.getTitle() != null) {
                                        curBuffer.append(" title=\"" + menuItem.getTitle() + "\"");
                                    }


                                    if( menuItem.getAssociatedHtml() != null)   {
                                        associatedHTML.append(menuItem.getAssociatedHtml());
                                    }


                                } else {
                                    // Text display
                                    curBuffer.append("<span");
                                }

                                // HTML class
                                String className = StringUtils.EMPTY;
                                if (menuItem.getClassName() != null) {
                                    className += menuItem.getClassName();
                                }
                                if (menuItem.isAjaxDisabled() == true) {
                                    className += " no-ajax-link";
                                }
                                if (StringUtils.isNotBlank(className)) {
                                    curBuffer.append(" class=\"" + "portlet-menuitem " + className + "\"");
                                }

                                curBuffer.append(">");

                                if (menuItem.getTitle() != null) {
                                    curBuffer.append(" " + menuItem.getTitle());
                                }

                                // Closing tag
                                if (StringUtils.isNotBlank(menuItem.getUrl())) {
                                    curBuffer.append("</a>");
                                } else {
                                    curBuffer.append("</span>");
                                }

                                if( menuItem.isDropdownItem())  {
                                    curBuffer = topMenu;
                                    curBuffer.append("</li>");
                                }

                            }

                            topMenu.append("</ul></div>");

                            topBar.append("</p>");


                            if( associatedHTML.length() > 0){
                                topBar.append("<div class=\"portlet-menu-html\">");
                                topBar.append(associatedHTML);
                                topBar.append("</div>");
                            }


                            if ("1".equals(printPortlet)) {
                                portletPre = "<div id=\"" + windowId + "_print\" class=\"portlet-print-box\">";

                                portletPost = "</div>";
                            }

                            updatedFragment = "<div class=\"portlet-bar\">" + (!emptyMenu ? topMenu.toString() : "") + stateBar.toString() + topBar.toString()
                                    + "</div>" + portletPre + updatedFragment + portletPost;
                        }
                    }

                }// if showbar





                if (attributes.get("osivia.asyncReloading.ajaxId") != null) {


                    Map<String, String[]> newNS = new HashMap<String, String[]>();

                    StateString navState = invocation.getNavigationalState();

                    if (navState != null) {

                        Map<String, String[]> oldNS = StateString.decodeOpaqueValue(invocation.getNavigationalState().getStringValue());

                        for (String key : oldNS.keySet()) {
                            newNS.put(key, oldNS.get(key));
                        }
                    }

                    // Ajout ajaxId dans etat navigation

                    newNS.put("ajaxId", new String[]{(String) attributes.get("osivia.asyncReloading.ajaxId")});

                    // To pass cache
                    newNS.put("ajaxTs", new String[]{"" + System.currentTimeMillis()});

                    navState = StateString.create(StateString.encodeAsOpaqueValue(newNS));

                    ControllerCommand renderCmd = new InvokePortletWindowRenderCommand(PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT),
                            invocation.getMode(), invocation.getWindowState(), navState);

                    PortalURL portalURL = new PortalURLImpl(renderCmd, ctx, null, null);

                    StringBuffer reloadingCode = new StringBuffer();

                    reloadingCode.append("<script type=\"text/javascript\">");

                    String safestWindowId = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT);

                    reloadingCode.append("setTimeout( \"asyncUpdatePortlet('" + safestWindowId + "', '" + portalURL.toString() + "')\", 2000); \n");

                    reloadingCode.append("</script>");

                    updatedFragment = updatedFragment + reloadingCode.toString();

                }

                return new FragmentResponse(fr.getProperties(), fr.getAttributes(), fr.getContentType(), fr.getBytes(), updatedFragment, fr.getTitle(),
                        fr.getCacheControl(), fr.getNextModes());

            }
        }

        // On teste si le portlet fait un modification d'état de la page en mode
        // AJAX
        if (response instanceof UpdateNavigationalStateResponse) {

            Map<String, Object> attributes = ((UpdateNavigationalStateResponse) response).getAttributes();
            String synchro = (String) attributes.get("osivia.refreshPage");

            if ("true".equals(synchro)) {
                ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.refreshPage", "true");
            }

            if ("true".equals(attributes.get("osivia.unsetMaxMode"))) {
                ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.unsetMaxMode", "true");
            }
            
            // v2.0.22-RC6 Force to reload portlets and CMS resources
            if ("true".equals(attributes.get("osivia.updateContents")))
                 ctx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.updateContents", "true");


        }

        return response;
    }

}
