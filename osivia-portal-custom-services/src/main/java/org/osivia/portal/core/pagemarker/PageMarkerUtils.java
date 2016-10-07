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
package org.osivia.portal.core.pagemarker;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.PortalObjectNavigationalStateContext;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.StateString;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.api.path.PortletPathItem;
import org.osivia.portal.api.selection.SelectionItem;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.api.theming.UserPortal;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PortalObjectContainer;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.osivia.portal.core.security.CmsPermissionHelper;
import org.osivia.portal.core.selection.SelectionMapIdentifiers;
import org.osivia.portal.core.selection.SelectionScope;
import org.osivia.portal.core.selection.SelectionService;

/**
 * Utility class for page marker.
 */
public class PageMarkerUtils {

    protected static final Log windowlogger = LogFactory.getLog("PORTAL_WINDOW");

    protected static final Log logger = LogFactory.getLog(PageMarkerUtils.class);

    public static String PAGE_MARKER_PATH = "/pagemarker/";

    /**
     * Private constructor ; this class cannot be instanciated.
     */
    private PageMarkerUtils() {
    }

    public static void dumpPageState(ControllerContext controllerCtx, Page page, String label) {
        if (!windowlogger.isDebugEnabled() || (page == null)) {
            return;
        }

        windowlogger.debug("-------------- DEBUT DUMP " + label);
        windowlogger.debug("   page" + page.getId());

        Collection windows = page.getChildren(PortalObject.WINDOW_MASK);

        Iterator i = windows.iterator();
        while (i.hasNext()) {

            Window window = (Window) i.next();

            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

            WindowNavigationalState ws = (WindowNavigationalState) controllerCtx.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);

            // Sauvegarde more par défaut
            if (ws == null) {
                ws = new WindowNavigationalState(WindowState.NORMAL, Mode.VIEW, null, null);
            }

            if (ws != null) {

                windowlogger.debug("   window :" + window.getName());
                windowlogger.debug("      state :" + ws.getWindowState());
                if (ws.getContentState() != null) {
                    ws.getContentState();
                    if (StateString.decodeOpaqueValue(ws.getContentState().getStringValue()).size() > 0) {
                        windowlogger.debug("      content state :" + ws.getContentState());
                    }
                }
                if (ws.getPublicContentState() != null) {
                    ws.getPublicContentState();
                    if (StateString.decodeOpaqueValue(ws.getPublicContentState().getStringValue()).size() > 0) {
                        windowlogger.debug("      public state :" + ws.getPublicContentState());
                    }
                }
            }
        }

        windowlogger.debug("   breadcrumb");

        Breadcrumb breadcrumb = (Breadcrumb) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb");
        if (breadcrumb != null) {
            for (BreadcrumbItem bi : breadcrumb.getChilds()) {
                windowlogger.debug("      " + bi.getName() + " :" + bi.getId());
            }
        }

        NavigationalStateContext ctx = (NavigationalStateContext) controllerCtx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        PageNavigationalState pns = ctx.getPageNavigationalState(page.getId().toString());

        windowlogger.debug("   pns  :");

        if (pns != null) {
            Map<QName, String[]> pnsParams = pns.getParameters();
            for (QName param : pnsParams.keySet()) {
                String sPNS = "";
                for (int iValue = 0; iValue < pnsParams.get(param).length; iValue++) {
                    if (iValue > 1) {
                        sPNS += ",";
                    }
                    sPNS += pnsParams.get(param)[iValue];

                }
                windowlogger.debug("      " + param + " :" + sPNS);
            }
        }

        IDynamicObjectContainer po = ((PortalObjectContainer) controllerCtx.getController().getPortalObjectContainer()).getDynamicObjectContainer();
        for (DynamicWindowBean dynaWIndow : po.getDynamicWindows()) {
            windowlogger.debug("   dynamic windows  :");
            windowlogger.debug("      " + dynaWIndow.getName());
        }

        windowlogger.debug("-------------- FIN DUMP " + label);
    }

    /**
     * Utility method used to save the page state.
     *
     * @param controllerCtx
     *            controller context
     * @param page
     *            page to save
     */
    @SuppressWarnings("unchecked")
    public static void savePageState(ControllerContext controllerCtx, Page page) {
        // TEST PERF
        /*
         * if( true) return;
         */

        String pageMarker = getCurrentPageMarker(controllerCtx);

        dumpPageState(controllerCtx, page, "AVANT savePageState " + pageMarker);

        PageMarkerInfo markerInfo = new PageMarkerInfo(pageMarker);
        markerInfo.setLastTimeStamp(new Long(System.currentTimeMillis()));

        if( page != null)   {
        markerInfo.setPageId(page.getId());

        Map<PortalObjectId, WindowStateMarkerInfo> windowInfos = new HashMap<PortalObjectId, WindowStateMarkerInfo>();
        markerInfo.setWindowInfos(windowInfos);

        // Sauvegarde des etats
        Collection windows = page.getChildren(PortalObject.WINDOW_MASK);

        Iterator i = windows.iterator();
        while (i.hasNext()) {
            Window window = (Window) i.next();

            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

            WindowNavigationalState ws = (WindowNavigationalState) controllerCtx.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);

            // Sauvegarde more par défaut
            if (ws == null) {
                ws = new WindowNavigationalState(WindowState.NORMAL, Mode.VIEW, null, null);
            }
            ParametersStateString addParams = (ParametersStateString) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, ContributionService.ATTR_ADDITITIONNAL_WINDOW_STATES + window.getId().toString(PortalObjectPath.CANONICAL_FORMAT));
            List<PortletPathItem> portletPath = (List<PortletPathItem>) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.PORTLET_ATTR_PORTLET_PATH + window.getId().toString(PortalObjectPath.CANONICAL_FORMAT));



            windowInfos.put(window.getId(), new WindowStateMarkerInfo(ws.getWindowState(), ws.getMode(), ws.getContentState(), ws.getPublicContentState(), addParams, portletPath));
        }

        // Sauvegarde etat page
        NavigationalStateContext ctx = (NavigationalStateContext) controllerCtx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        PageNavigationalState pns = ctx.getPageNavigationalState(page.getId().toString());
        markerInfo.setPageNavigationState(pns);

        // Sauvegarde des fenêtres dynamiques
        IDynamicObjectContainer po = ((PortalObjectContainer) controllerCtx.getController().getPortalObjectContainer()).getDynamicObjectContainer();
        markerInfo.setDynamicWindows(po.getDynamicWindows());

        // Sauvegarde des pages dynamiques
        markerInfo.setDynamicPages(po.getDynamicPages());

        // sauvegarde breadcrumb
        Breadcrumb breadcrumb = (Breadcrumb) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb");
        if (breadcrumb != null) {
            Breadcrumb savedBreadcrum = new Breadcrumb();
            for (BreadcrumbItem bi : breadcrumb.getChilds()) {
                savedBreadcrum.getChilds().add(bi);

            }
            markerInfo.setBreadcrumb(savedBreadcrum);
        }


        // sauvegarde menu
        UserPortal userPortal = (UserPortal) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavUserPortal");
        if (userPortal != null) {
            markerInfo.setTabbedNavHeaderUserPortal(userPortal);
        }
        Long headerCount = (Long) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavHeaderCount");
        if (headerCount != null) {
            markerInfo.setTabbedNavheaderCount(headerCount);
        }
        String userName = (String) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavheaderUsername");
        if (userName != null) {
            markerInfo.setTabbedNavheaderUsername(userName);
        }

        Integer firstTab = (Integer) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_FIRST_TAB);
        if (firstTab != null) {
            markerInfo.setFirstTab(firstTab);
        }

        PortalObjectId currentPageId = (PortalObjectId) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_PAGE_ID);
        if (currentPageId != null) {
            markerInfo.setCurrentPageId(currentPageId);
        }

        String backPageMarker = (String) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backPageMarker");
        if( backPageMarker != null) {
            markerInfo.setBackPageMarker(backPageMarker);
        }

        Boolean refreshBack = (Boolean) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.refreshBack");
        if( refreshBack != null) {
            markerInfo.setRefreshBack(refreshBack);
        }

        String mobileBackPageMarker = (String) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backMobilePageMarker");
        if( mobileBackPageMarker != null) {
            markerInfo.setMobileBackPageMarker(mobileBackPageMarker);
        }

        Boolean mobileRefreshBack = (Boolean) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.mobileRefreshBack");
        if( mobileRefreshBack != null) {
            markerInfo.setMobileRefreshBack(mobileRefreshBack);
        }

        // Restauration mode popup
        String popupMode = (String) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode");
        markerInfo.setPopupMode(popupMode);
        PortalObjectId popupModeWindowID = (PortalObjectId) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID");
        markerInfo.setPopupModeWindowID(popupModeWindowID);
        PortalObjectId popupModeOrginalPageID = (PortalObjectId) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeOrginalPageID");
        markerInfo.setPopupModeOrginalPageID(popupModeOrginalPageID);

        // Sauvegarde de l'ensemble des sélections
        Map<SelectionMapIdentifiers, Set<SelectionItem>> selectionsMap = (Map<SelectionMapIdentifiers, Set<SelectionItem>>) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                SelectionService.ATTR_SELECTIONS_MAP);
        if (selectionsMap != null) {

            markerInfo.setSelectionsMap(selectionsMap);
        }


        // Notifications list
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerCtx);
        List<Notifications> notificationsList = NotificationsUtils.getNotificationsService().getNotificationsList(portalControllerContext);
        if (CollectionUtils.isNotEmpty(notificationsList)) {
            markerInfo.setNotificationsList(notificationsList);
        }


        // Save closing popup action
        if ("1".equals(controllerCtx.getAttribute(Scope.REQUEST_SCOPE, "osivia.saveClosingAction"))
                && "1".equals(controllerCtx.getAttribute(Scope.REQUEST_SCOPE, "osivia.popupModeClosing"))) {
            markerInfo.setClosingPopupAction(true);
        }


        Long selectionTs = (Long) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, SelectionService.ATTR_SELECTIONS_TIMESTAMP);
        if (selectionTs != null) {
            markerInfo.setSelectionTs(selectionTs);
        }
        }

        // Mémorisation marker dans la session
        Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerCtx.getAttribute(Scope.SESSION_SCOPE, "markers");
        if (markers == null) {
            markers = new LinkedHashMap<String, PageMarkerInfo>();
            controllerCtx.setAttribute(Scope.SESSION_SCOPE, "markers", markers);
        }

        // On mémorise les 50 dernières entrées
        if (markers.size() > 50) {
            try {
                // Tri pour avoir les markers qui n'ont pas été accédés depuis
                // le + longtemps
                List<PageMarkerInfo> list = new LinkedList(markers.values());

                Collections.sort(list, new Comparator<PageMarkerInfo>() {
                    public int compare(PageMarkerInfo o1, PageMarkerInfo o2) {
                        return o1.getLastTimeStamp().compareTo(o2.getLastTimeStamp());
                    }
                });

                markers.remove(list.get(0).getPageMarker());
            } catch (ClassCastException e) {
                // Déploiement à chaud
                markers = new LinkedHashMap<String, PageMarkerInfo>();
                controllerCtx.setAttribute(Scope.SESSION_SCOPE, "markers", markers);
            }
        }

        markers.put(pageMarker, markerInfo);
        controllerCtx.setAttribute(Scope.SESSION_SCOPE, "lastSavedPageMarker", pageMarker);

        // NON NECESSAIRE
        // controllerCtx.setAttribute(Scope.SESSION_SCOPE, "markers", markers);
    }

    public static String generateNewPageMarker(ControllerContext controllerCtx) {
        String lastPageMarker = (String) controllerCtx.getAttribute(Scope.SESSION_SCOPE, "lastPageMarker");

        if (lastPageMarker != null) {
            long test = Long.parseLong(lastPageMarker);

            Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerCtx.getAttribute(Scope.SESSION_SCOPE, "markers");

            if (markers != null) {
                do {
                    test++;
                    lastPageMarker = Long.toString(test);
                } while (markers.get(lastPageMarker) != null);
            } else {
                lastPageMarker = "1";
            }
        } else {
            lastPageMarker = "1";
        }

        controllerCtx.setAttribute(Scope.SESSION_SCOPE, "lastPageMarker", lastPageMarker);

        return lastPageMarker;
    }

    public static String getCurrentPageMarker(ControllerContext controllerCtx) {
        String pageMarker = (String) controllerCtx.getAttribute(Scope.REQUEST_SCOPE, "currentPageMarker");

        if (pageMarker == null) {
            if (ControllerContext.AJAX_TYPE == controllerCtx.getType()) {
                // On reprend le marker de la page d'origine pour les requetes
                // Ajax
                pageMarker = (String) controllerCtx.getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");
                if (pageMarker == null) {
                    pageMarker = generateNewPageMarker(controllerCtx);
                }
            } else {
                pageMarker = generateNewPageMarker(controllerCtx);
            }

            controllerCtx.setAttribute(Scope.REQUEST_SCOPE, "currentPageMarker", pageMarker);
        }

        return pageMarker;
    }

    /**
     * Utility method used to restore the page state.
     *
     * @param controllerContext
     *            controller context
     * @param requestPath
     *            request path
     * @return new path
     */
    public static String restorePageState(ControllerContext controllerContext, String requestPath) {
        String newPath = requestPath;
        String newTabPath = null;


        String currentPageMarker = null;



        if (requestPath.startsWith(PAGE_MARKER_PATH)) {
            int beginMarker = PAGE_MARKER_PATH.length();
            int endMarker = requestPath.indexOf('/', beginMarker);
            if ((beginMarker != -1) && (endMarker != -1)) {

                currentPageMarker = requestPath.substring(beginMarker, endMarker);
                newPath = requestPath.substring(endMarker);
            }
        } else    {
            ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();
            String[] pagemarkers = parameterMap.get(InternalConstants.PORTAL_WEB_URL_PARAM_PAGEMARKER);
            if( (pagemarkers != null) && (pagemarkers.length == 1)) {
                currentPageMarker = pagemarkers[ 0];
            }
        }



       /* Tab restoration */

        PageMarkerInfo tabMarkerInfo = null;

        HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();

        if ("true".equals(request.getParameter("init-state")) && !"true".equals(request.getParameter("edit-template-mode"))) {

            Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerContext.getAttribute(Scope.SESSION_SCOPE, "markers");

            if (markers != null) {

                // Searching the most recent version for this tabs

                List<PageMarkerInfo> list = new LinkedList(markers.values());

                // Inverte orders to obtain the most recent first
                Collections.sort(list, new Comparator<PageMarkerInfo>() {

                    public int compare(PageMarkerInfo o1, PageMarkerInfo o2) {
                        return o2.getLastTimeStamp().compareTo(o1.getLastTimeStamp());
                    }
                });



                String tabPagePath = StringUtils.removeStart(newPath, "/portal");

                for (PageMarkerInfo pagemarkerInfo : list) {
                    // Is it a subpage of the tab ?

                    if ((pagemarkerInfo.getPageId() != null) && StringUtils.startsWith(pagemarkerInfo.getPageId().toString(), tabPagePath)) {
                        newTabPath = "/portal" + pagemarkerInfo.getPageId().toString();
                        tabMarkerInfo = pagemarkerInfo;
                        break;
                    }
                }
            }
        }

        /* Tab restoration */

        String backPageMarker = null;


        if (request.getParameter("backPageMarker") != null) {
            backPageMarker = request.getParameter("backPageMarker");
        }











        /*
         * Restauration automatique si pagemarker désactivé
         * (utile uniquement sur les fermetures de popup pour propager popupModeClosing
         */
        if (currentPageMarker == null) {
            String lastPageMarker = (String) controllerContext.getAttribute(Scope.SESSION_SCOPE, "lastSavedPageMarker");
            if (lastPageMarker != null) {
                Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerContext.getAttribute(Scope.SESSION_SCOPE, "markers");
                try {
                    PageMarkerInfo markerInfo = markers.get(lastPageMarker);
                    if ((markerInfo != null) && (markerInfo.getPageId() != null)) {
                        try {
                            Page page = (Page) controllerContext.getController().getPortalObjectContainer().getObject(markerInfo.getPageId());
                            if ("1".equals(page.getProperty("osivia.portal.disablePageMarker"))) {
                                currentPageMarker = lastPageMarker;
                            }
                        } catch (Exception e) {
                            // Do nothing
                        }
                    }
                } catch (ClassCastException e) {
                    // Cas d'un redéploiement à chaud
                }

            }
        }


        /**********************************************************************
         * Restauration de l'état des windows en fonction du marqueur de
         * page
         *
         * Permet de gérer les backs du navigateur
         **********************************************************************/

        // Permet de controler qu'on effectue une seule fois le
        // traitement
        String controlledPageMarker = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");

        // TEST PERF
        // if( false)
        //v2.1 WORKSPACE : en AJAX, les pagemarker arrivent à 0 -> pas de restauration
        if (! "0".equals(currentPageMarker) && (controlledPageMarker == null) && (currentPageMarker != null)) {
            // Traitement lié au back du navigateur

            Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerContext.getAttribute(Scope.SESSION_SCOPE, "markers");
            if (markers != null) {
                PageMarkerInfo markerInfo = null;

                try {
                    markerInfo = markers.get(currentPageMarker);

                } catch (ClassCastException e) {
                    // Cas d'un redéploiement à chaud
                }

                if (markerInfo != null) {


                    markerInfo.setLastTimeStamp(System.currentTimeMillis());



                    if (true) {

                        PageMarkerInfo restorePageInfo = markerInfo;
                        if( tabMarkerInfo != null)  {
                            // In case of a tab, we restore the tab's page info
                            restorePageInfo = tabMarkerInfo;
                            if( restorePageInfo != null)   {
                                restorePageInfo.setLastTimeStamp(System.currentTimeMillis());

                                // And if tab was already active, we reinit the page
                                if( tabMarkerInfo.getPageId().equals(markerInfo.getPageId()))   {
                                    newTabPath = null;
                                }
                            }
                        }

                        if( backPageMarker != null) {
                            restorePageInfo = markers.get(backPageMarker);
                            if( restorePageInfo != null)   {
                                restorePageInfo.setLastTimeStamp(System.currentTimeMillis());
                            }
                        }

                        // Restauration des pages dynamiques
                        if( markerInfo.getDynamicPages() != null)   {
                            IDynamicObjectContainer poc = ((PortalObjectContainer) controllerContext.getController().getPortalObjectContainer()).getDynamicObjectContainer();
                            poc.setDynamicPages(markerInfo.getDynamicPages());
                        }

                        Page page = restorePageState(controllerContext, restorePageInfo);



                        // restauration menu
                        UserPortal userPortal = markerInfo.getTabbedNavHeaderUserPortal();
                        if (userPortal != null) {
                            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavUserPortal", userPortal);
                        }
                        if (markerInfo.getTabbedNavheaderCount() != null) {
                            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavHeaderCount", markerInfo.getTabbedNavheaderCount());
                        }
                        if (markerInfo.getTabbedNavheaderUsername() != null) {
                            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavheaderUsername", markerInfo.getTabbedNavheaderUsername());
                        }
                        if (markerInfo.getFirstTab() != null) {
                            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.firstTab", markerInfo.getFirstTab());
                        }


                        // Restauration mode popup
                        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", markerInfo.getPopupMode());
                        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID", markerInfo.getPopupModeWindowID());
                        if (markerInfo.isClosingPopupAction()) {
                            controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.popupModeClosing", "1");
                        }
                        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeOrginalPageID", markerInfo.getPopupModeOriginalPageID());



                        // Restauration de l'ensemble des sélection
                        Map<SelectionMapIdentifiers, Set<SelectionItem>> selectionsMap = markerInfo.getSelectionsMap();
                        if (selectionsMap != null) {
                            PortalObjectId pageId = page.getId();

                            Map<SelectionMapIdentifiers, Set<SelectionItem>> newSelectionsMap = new HashMap<SelectionMapIdentifiers, Set<SelectionItem>>(selectionsMap.size());

                            try {

                                // Parcours de tous les éléments de la map
                                Set<Entry<SelectionMapIdentifiers, Set<SelectionItem>>> entrySet = selectionsMap.entrySet();
                                for (Entry<SelectionMapIdentifiers, Set<SelectionItem>> entry : entrySet) {
                                    SelectionMapIdentifiers selectionMapIdentifiers = entry.getKey();
                                    Set<SelectionItem> selectionSet = entry.getValue();

                                    SelectionScope scope = selectionMapIdentifiers.getScope();
                                    if ((SelectionScope.SCOPE_NAVIGATION.equals(scope)) || ((SelectionScope.SCOPE_PAGE.equals(scope)) && pageId.equals(selectionMapIdentifiers.getPageId()))) {
                                        // Scope navigation ou scope page concernant la page courante
                                        Set<SelectionItem> newSelectionSet = new LinkedHashSet<SelectionItem>(selectionSet);
                                        newSelectionsMap.put(selectionMapIdentifiers, newSelectionSet);
                                    } else {
                                        // Scope session ou scope page ne concernant pas la page courante
                                        newSelectionsMap.put(selectionMapIdentifiers, selectionSet);
                                    }
                                }

                            } catch (ClassCastException e) {
                                // Pb lie au reload du service, on ignore la map
                            }

                            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, SelectionService.ATTR_SELECTIONS_MAP, newSelectionsMap);
                        }

                        if (markerInfo.getSelectionTs() != null) {
                            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, SelectionService.ATTR_SELECTIONS_TIMESTAMP, markerInfo.getSelectionTs());
                        }

                        if (CollectionUtils.isNotEmpty(markerInfo.getNotificationsList())) {
                            PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
                            NotificationsUtils.getNotificationsService().setNotificationsList(portalControllerContext,
                                    markerInfo.getNotificationsList());
                        }

                        if (page != null) {
                            dumpPageState(controllerContext, page, "APRES restorePageState " + currentPageMarker);
                        }
                    }
                } else {
                    // logger.error("restauration fenetres impossible - pas d'état à restaurer");
                }
            }
        }

        controllerContext.setAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker", currentPageMarker);

        if( newTabPath != null) {

            // Inhibit the standard tab
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.RestoreTab", "1");
            newPath = newTabPath;
        }

        if (controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb") == null) {
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb", new Breadcrumb());
        }

        DynamicPortalObjectContainer.clearCache();
        CmsPermissionHelper.clearCache(controllerContext);



        return newPath;
    }














    private static Page restorePageState(ControllerContext controllerContext, PageMarkerInfo markerInfo) {
        // Restautation etat page
        NavigationalStateContext ctx = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        PageNavigationalState pns = markerInfo.getPageNavigationState();
        if (pns != null) {

            //
            ctx.setPageNavigationalState(markerInfo.getPageId().toString(), pns);

            // Restauration de preview nécessaire pour calcul editableWindows
            EditionState state= ContributionService.getNavigationalState(controllerContext, pns);

            if ((state != null) && EditionState.CONTRIBUTION_MODE_EDITION.equals( state.getContributionMode())) {
                controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LIVE_DOCUMENT , state.getDocPath());
            }
        }



        // Restauration des pages dynamiques
        IDynamicObjectContainer poc = ((PortalObjectContainer) controllerContext.getController().getPortalObjectContainer()).getDynamicObjectContainer();


        Page page = null;
        if( markerInfo.getPageId() != null) {
            page = (Page) controllerContext.getController().getPortalObjectContainer().getObject(markerInfo.getPageId());
        }

        // Cas des pages dynamiques qui n'existent plus
        if (page != null) {
            // Restauration des fenêtres dynamiques
            poc.setDynamicWindows(markerInfo.getDynamicWindows());

            // Restauration des etats des windows
            for (PortalObject po : page.getChildren(PortalObject.WINDOW_MASK)) {


                Window child = (Window) po;

                WindowStateMarkerInfo wInfo = markerInfo.getWindowInfos().get(child.getId());

                if (wInfo != null) {


                    // On stocke directement dans le
                    // scope session
                    // pour se rebrancher sur le
                    // traitement standard de jboss
                    // portal

                    WindowNavigationalState newNS = new WindowNavigationalState(wInfo.getWindowState(), wInfo.getMode(), wInfo.getContentState(), wInfo.getPublicContentState());

                    String windowCanonicalName = child.getId().toString(PortalObjectPath.CANONICAL_FORMAT);


                    controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, windowCanonicalName, newNS);

                    StateString additionnalState = wInfo.getAdditionnalState();
                    if (additionnalState != null) {
                        additionnalState = ParametersStateString.create(additionnalState);
                    }
                    controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, ContributionService.ATTR_ADDITITIONNAL_WINDOW_STATES + windowCanonicalName, additionnalState);

                    if( wInfo.getPortletPath() != null) {
                        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.PORTLET_ATTR_PORTLET_PATH + windowCanonicalName, wInfo.getPortletPath());
                    }

                }
            }
        }

        controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LIVE_DOCUMENT , null);



        ((PortalObjectNavigationalStateContext) ctx).applyChanges();

        // restauration breadcrumb
        Breadcrumb savedBreadcrum = markerInfo.getBreadcrumb();
        if (savedBreadcrum != null) {
            Breadcrumb breadcrumb = new Breadcrumb();
            for (BreadcrumbItem bi : savedBreadcrum.getChilds()) {
                breadcrumb.getChilds().add(bi);
            }
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb", breadcrumb);
        }


        if (markerInfo.getCurrentPageId() != null) {
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId", markerInfo.getCurrentPageId());
        }

        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backPageMarker", null);

        if (markerInfo.getBackPageMarker() != null) {
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backPageMarker", markerInfo.getBackPageMarker());
        }

        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backMobilePageMarker", null);

        if (markerInfo.getMobileBackPageMarker() != null) {
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backMobilePageMarker", markerInfo.getMobileBackPageMarker());
        }

        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.refreshBack", markerInfo.isRefreshBack());
        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.mobileRefreshBack", markerInfo.isMobileRefreshBack());

        return page;
    }

    public static PageMarkerInfo getLastPageState(ControllerContext controllerContext) {
        // Permet de controler qu'on effectue une seule fois le traitement
        String controlledPageMarker = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");

        PageMarkerInfo markerInfo = null;

        if (controlledPageMarker != null) {
            // Traitement lié au back du navigateur
            Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerContext.getAttribute(Scope.SESSION_SCOPE, "markers");
            if (markers != null) {
                try {
                    markerInfo = markers.get(controlledPageMarker);
                } catch (ClassCastException e) {
                    // Cas d'un redéploiement à chaud
                }
            }
        }

        return markerInfo;
    }


    public static PageMarkerInfo  getPageMarkerInfo(ControllerContext controllerContext, String pageMarker)  {

        PageMarkerInfo markerInfo = null;

        Map<String, PageMarkerInfo> markers = (Map<String, PageMarkerInfo>) controllerContext.getAttribute(Scope.SESSION_SCOPE, "markers");
        if (markers != null) {
            try {
                markerInfo = markers.get(pageMarker);
            } catch (ClassCastException e) {
                // Cas d'un redéploiement à chaud
            }
        }

        return markerInfo;
    }


}
