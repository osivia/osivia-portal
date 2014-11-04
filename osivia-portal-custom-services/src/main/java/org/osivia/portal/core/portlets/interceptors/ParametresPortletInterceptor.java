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
import org.dom4j.CDATA;
import org.dom4j.Element;
import org.dom4j.dom.DOMCDATA;
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
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.directory.entity.DirectoryPerson;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.path.PortletPathItem;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.customization.ICustomizationService;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.DynamicWindow;

/**
 * Ajout des attributs spécifiques au PIA dans les requêtes des portlets.
 *
 * @see PortletInvokerInterceptor
 */
public class ParametresPortletInterceptor extends PortletInvokerInterceptor {

    /** Logger. */
    private static Log logger = LogFactory.getLog(ParametresPortletInterceptor.class);

    /** Customization service. */
    private ICustomizationService customizationService;
    /** Internationalization service. */
    private IInternationalizationService internationalizationService;

    /**
     * Default constructor.
     */
    public ParametresPortletInterceptor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException {
        // Controller context
        ControllerContext controllerContext = (ControllerContext) invocation.getAttribute("controller_context");

        Window window = null;

        if (controllerContext != null) {
            Map<String, Object> attributes = invocation.getRequestAttributes();
            if (attributes == null) {
                attributes = new HashMap<String, Object>();
            }

            // Ajout de la window
            String windowId = invocation.getWindowContext().getId();
            if (windowId.charAt(0) == CanonicalFormat.PATH_SEPARATOR) {
                PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.CANONICAL_FORMAT);

                window = (Window) controllerContext.getController().getPortalObjectContainer().getObject(poid);

                attributes.put("osivia.window", window);

                attributes.put("osivia.window.ID", window.getId().toString(PortalObjectPath.SAFEST_FORMAT));

                logger.debug("windowId " + windowId);

                if (window.getDeclaredProperty("osivia.cms.uri") != null) {
                    logger.debug("osivia.cms.uri " + window.getDeclaredProperty("osivia.cms.uri"));
                }
                if (window.getDeclaredProperty("osivia.cms.scope") != null) {
                    logger.debug("osivia.cms.scope " + window.getDeclaredProperty("osivia.cms.scope"));
                }

                /* Le path CMS identifie de manière unique la session */

                NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext
                        .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
                PageNavigationalState pageState = nsContext.getPageNavigationalState(window.getPage().getId().toString());

                if (window instanceof DynamicWindow) {
                    String uniqueID = ((DynamicWindow) window).getDynamicUniqueID();
                    if ((uniqueID != null) && (uniqueID.length() > 1)) {
                        invocation.setAttribute("osivia.window.path", windowId);

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

                EditionState editionState = ContributionService.getWindowEditionState(controllerContext, window.getId());
                attributes.put("osivia.editionState", editionState);

                String webPagePath = (String) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.cms.webPagePath");
                if (webPagePath != null) {
                    attributes.put("osivia.cms.webPagePath", webPagePath);
                }

            }

            if ("wizzard".equals(controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.windowSettingMode"))) {
                attributes.put("osivia.window.wizzard", "true");
            }

            // Ajout de l'identifiant CMS
            String contentId = (String) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.content.id");
            if (contentId != null) {
                attributes.put("osivia.content.id", contentId);
            }

            // Ajout du controleur
            attributes.put("osivia.controller", controllerContext);

            // Ajout du mode admin
            if (PageCustomizerInterceptor.isAdministrator(controllerContext)) {
                attributes.put(InternalConstants.ADMINISTRATOR_INDICATOR_ATTRIBUTE_NAME, true);
            }

            // Pour l'instant les pages markers ne sont pas gérés pour les
            // ressources
            if (!(invocation instanceof ResourceInvocation)) {
                attributes.put("osivia.pageMarker", PageMarkerUtils.getCurrentPageMarker(controllerContext));
            }

            // v 1.0.14 : gestion de la barre de menu
            if (!(invocation instanceof ResourceInvocation)) {

                List<MenubarItem> menuBar = new ArrayList<MenubarItem>();

                attributes.put(Constants.PORTLET_ATTR_MENU_BAR, menuBar);
            }

            // v2.0 : user datas
            Map<String, Object> userDatas = (Map<String, Object>) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.userDatas");
            if (userDatas != null) {
                attributes.put(Constants.PORTLET_ATTR_USER_DATAS, userDatas);
            }

            // v3.3 : new user object
            DirectoryPerson person = (DirectoryPerson) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, Constants.ATTR_LOGGED_PERSON);
            if (person != null) {
                attributes.put(Constants.ATTR_LOGGED_PERSON, person);
            }

            // user datas timestamp
            Long userDatasTs = (Long) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.userDatas.refreshTimestamp");
            attributes.put(Constants.PORTLET_ATTR_USER_DATAS_REFRESH_TS, userDatasTs);

            // HTTP Request
            HttpServletRequest httpRequest = controllerContext.getServerInvocation().getServerContext().getClientRequest();
            attributes.put(Constants.PORTLET_ATTR_HTTP_REQUEST, httpRequest);

            // Space config
            Object spaceConfig = controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.cms.spaceConfig");
            if (spaceConfig != null) {
                attributes.put(Constants.PORTLET_ATTR_SPACE_CONFIG, ((CMSItem) spaceConfig).getNativeItem());
            }

            // Renderset
            Object renderset = controllerContext.getAttribute(Scope.REQUEST_SCOPE, InternalConstants.PARAMETERIZED_RENDERSET_ATTRIBUTE);
            if (renderset != null) {
                attributes.put(InternalConstants.PARAMETERIZED_RENDERSET_ATTRIBUTE, renderset);
            }


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
                        controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.portletPath", portletPath);
                    }
                }

                /* v 1.0.14 : affichage d'une barre de menu */

                if(!Boolean.TRUE.equals(attributes.get("osivia.menubar.hide"))){

                    if (Boolean.TRUE.equals(controllerContext.getAttribute(Scope.REQUEST_SCOPE, "osivia.showMenuBarItem"))) {
                    List<MenubarItem> menubarItems = (List<MenubarItem>) attributes.get(Constants.PORTLET_ATTR_MENU_BAR);
                    if (menubarItems != null) {
                        String title = window.getDeclaredProperty("osivia.title");
                        if (title == null) {
                            title = fr.getTitle();
                        }

                            PortalObjectId popupWindowId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                                    "osivia.popupModeWindowID");

                        String printPortlet = null;


                        // impression
                        printPortlet = window.getDeclaredProperty("osivia.printPortlet");
                        if ((printPortlet == null) && (popupWindowId == null)) {
                            if (WindowState.MAXIMIZED.equals(invocation.getWindowState())) {
                                        printPortlet = "1";
                            }
                        }


                        if ("1".equals(printPortlet)) {
                            // Appel module custom PRINT
                            Map<String, Object> customAttrMap = new HashMap<String, Object>();
                            customAttrMap.put("title", title);
                            customAttrMap.put("menuBar", menubarItems);
                            customAttrMap.put("windowId", windowId);
                                customAttrMap.put("themePath", controllerContext.getAttribute(Scope.REQUEST_SCOPE, "osivia.themePath"));

                            CustomizationContext customCtx = new CustomizationContext(customAttrMap);
                            this.customizationService.customize("MENUBAR_PRINT_ITEM", customCtx);

                            MenubarItem printItem = (MenubarItem) customAttrMap.get("result");
                            if (printItem == null) {
                                String jsTitle = StringEscapeUtils.escapeJavaScript(title);
                                printItem = new MenubarItem("PRINT", "Imprimer", 100, "#", "popup2print('" + jsTitle + "', '" + windowId + "_print');",
                                        "portlet-menuitem-print hidden-xs", null);
                                printItem.setGlyphicon("halflings print");
                            }
                            menubarItems.add(printItem);
                        }

                        /* Add back item */


                        if( window.getDeclaredProperty("osivia.dynamic.close_url") != null) {

                                if (!"1".equals(window.getDeclaredProperty("osivia.dynamic.disable.close"))) {

                                    boolean containsBackItem = false;

                                    // already managed at application level ?

                                    for (MenubarItem item : menubarItems) {
                                        if ("BACK".equals(item.getId())) {
                                            containsBackItem = true;
                                            break;
                                        }
                                    }

                                    if (!containsBackItem) {
                                        MenubarItem backItem = new MenubarItem("BACK", "Revenir", MenubarItem.ORDER_PORTLET_SPECIFIC_CMS,
                                                window.getDeclaredProperty("osivia.dynamic.close_url"), null, null, null);
                                        backItem.setGlyphicon("halflings arrow-left");
                                        backItem.setAjaxDisabled(true);
                                        backItem.setFirstItem(true);
                                        menubarItems.add(backItem);
                                    }
                                }
                            }





                        if (menubarItems.size() > 0) {
                            List<MenubarItem> sortedItems = new ArrayList<MenubarItem>(menubarItems);
                            Collections.sort(sortedItems, new Comparator<MenubarItem>() {

                                public int compare(MenubarItem e1, MenubarItem e2) {
                                    return e1.getOrder() > e2.getOrder() ? 1 : -1;
                                }
                            });

                            if ("1".equals(printPortlet)) {
                                String pre = "<div id='" + windowId + "_print' class='portlet-print-box'>";
                                String post = "</div>";
                                updatedFragment = pre + updatedFragment + post;
                            }

                                updatedFragment = this.generatePortletMenubar(controllerContext, sortedItems) + updatedFragment;
                        }
                    }
                }
            }

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

                    PortalURL portalURL = new PortalURLImpl(renderCmd, controllerContext, null, null);

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
            String synchro = (String) attributes.get(Constants.PORTLET_ATTR_REFRESH_PAGE);

            if (Constants.PORTLET_VALUE_ACTIVATE.equals(synchro)) {
                controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.refreshPage", "true");
            }

            if (Constants.PORTLET_VALUE_ACTIVATE.equals(attributes.get(Constants.PORTLET_ATTR_UNSET_MAX_MODE))) {
                controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.unsetMaxMode", "true");
            }

            // v2.0.22-RC6 Force to reload portlets and CMS resources
            if (Constants.PORTLET_VALUE_ACTIVATE.equals(attributes.get(Constants.PORTLET_ATTR_UPDATE_CONTENTS))) {
                controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.updateContents", "true");
            }

        }

        return response;
    }

    /**
     * Portlet menubar generation.
     *
     * @param controllerContext
     *            controller context
     * @param items
     *            menubar items
     * @return portlet menubar HTML content
     */
    private String generatePortletMenubar(ControllerContext controllerContext, List<MenubarItem> items) {
        // Portlet menubar container
        Element menubarContainer = DOM4JUtils.generateDivElement(null);

        // Portlet menubar
        Element menubar = DOM4JUtils.generateDivElement("btn-toolbar portlet-menubar", AccessibilityRoles.TOOLBAR);
        menubarContainer.add(menubar);

        // Associated HTML container
        Element associatedHTMLContainer = DOM4JUtils.generateDivElement("portlet-menubar-associated-html hidden");
        menubarContainer.add(associatedHTMLContainer);

        // Dropdown menu container
        Element dropdownContainer = DOM4JUtils.generateDivElement("btn-group accessible-dropdown-menu");


        // Menubar left group
        Element firstGroup = DOM4JUtils.generateDivElement("btn-group");

        // Menubar left group
        Element leftGroup = DOM4JUtils.generateDivElement("btn-group");

        // Menubar right group
        Element rightGroup = DOM4JUtils.generateDivElement("btn-group pull-right");

        // Dropdown menu button
        Element dropdownButton = DOM4JUtils
                .generateElement(HTMLConstants.BUTTON, "btn btn-default dropdown-toggle", HTMLConstants.TEXT_DEFAULT, "pencil", null);
        DOM4JUtils.addAttribute(dropdownButton, HTMLConstants.DATA_TOGGLE, "dropdown");
        Element caret = DOM4JUtils.generateElement(HTMLConstants.SPAN, "caret", StringUtils.EMPTY);
        dropdownButton.add(caret);
        dropdownContainer.add(dropdownButton);

        // Dropdown menu
        Element dropdownMenu = DOM4JUtils.generateElement(HTMLConstants.UL, "dropdown-menu", null, null, AccessibilityRoles.MENU);
        dropdownContainer.add(dropdownMenu);

        boolean emptyDropdownMenu = true;
        for (MenubarItem item : items) {
            Element parent;
            if (item.isDropdownItem()) {
                emptyDropdownMenu = false;

                Element dropdownItemContainer = DOM4JUtils.generateElement(HTMLConstants.LI, null, null, null, AccessibilityRoles.PRESENTATION);
                if (item.isStateItem()) {
                    DOM4JUtils.addAttribute(dropdownItemContainer, HTMLConstants.CLASS, "dropdown-header");
                }
                dropdownMenu.add(dropdownItemContainer);

                parent = dropdownItemContainer;
            } else if( item.isFirstItem()){
            	parent = firstGroup;
            }
             else if (item.isStateItem()) {
                parent = leftGroup;
            } else {
                parent = rightGroup;
            }

            StringBuilder linkHTMLClass = new StringBuilder();
            if (item.getClassName() != null) {
                linkHTMLClass.append(item.getClassName());
            }
            if (item.isAjaxDisabled()) {
                linkHTMLClass.append(" no-ajax-link");
            }
            if (!item.isDropdownItem()) {
                linkHTMLClass.append(" btn btn-default");
            }

            Element element;
            if (item.isDropdownItem() && item.isStateItem()) {
                element = DOM4JUtils.generateElement(HTMLConstants.SPAN, linkHTMLClass.toString(), item.getTitle(), item.getGlyphicon(), null);
            } else {
                AccessibilityRoles role = null;
                if (item.isDropdownItem()) {
                    role = AccessibilityRoles.MENU_ITEM;
                }
                element = DOM4JUtils.generateLinkElement(item.getUrl(), item.getTarget(), item.getOnClickEvent(), linkHTMLClass.toString(), item.getTitle(),
                        item.getGlyphicon(), role);
                if (item.getUrl() == null) {
                    DOM4JUtils.addAttribute(element, HTMLConstants.DISABLED, HTMLConstants.DISABLED);
                }
            }
            parent.add(element);

            // Associated HTML
            if (item.getAssociatedHtml() != null) {
                CDATA cdata = new DOMCDATA(item.getAssociatedHtml());
                associatedHTMLContainer.add(cdata);
            }
        }

        menubar.add(firstGroup);
        if (!emptyDropdownMenu) {
            menubar.add(dropdownContainer);
        }
        menubar.add(leftGroup);
        menubar.add(rightGroup);

        // Write HTML content
        return DOM4JUtils.write(menubarContainer);
    }

    /**
     * Getter for customizationService.
     *
     * @return the customizationService
     */
    public ICustomizationService getCustomizationService() {
        return this.customizationService;
    }

    /**
     * Setter for customizationService.
     *
     * @param customizationService
     *            the customizationService to set
     */
    public void setCustomizationService(ICustomizationService customizationService) {
        this.customizationService = customizationService;
    }

    /**
     * Getter for internationalizationService.
     *
     * @return the internationalizationService
     */
    public IInternationalizationService getInternationalizationService() {
        return this.internationalizationService;
    }

    /**
     * Setter for internationalizationService.
     *
     * @param internationalizationService
     *            the internationalizationService to set
     */
    public void setInternationalizationService(IInternationalizationService internationalizationService) {
        this.internationalizationService = internationalizationService;
    }

}
