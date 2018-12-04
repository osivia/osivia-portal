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
package org.osivia.portal.core.ajax;


import java.io.StringWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SignOutResponse;
import org.jboss.portal.core.controller.handler.AjaxResponse;
import org.jboss.portal.core.controller.handler.CommandForward;
import org.jboss.portal.core.controller.handler.HandlerResponse;
import org.jboss.portal.core.controller.handler.ResponseHandler;
import org.jboss.portal.core.controller.handler.ResponseHandlerException;
import org.jboss.portal.core.controller.portlet.ControllerPageNavigationalState;
import org.jboss.portal.core.controller.portlet.ControllerPortletControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowActionCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.render.RenderWindowCommand;
import org.jboss.portal.core.model.portal.command.response.MarkupResponse;
import org.jboss.portal.core.model.portal.command.response.PortletWindowActionResponse;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.content.WindowRendition;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateChange;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.core.navstate.NavigationalStateObjectChange;
import org.jboss.portal.core.theme.WindowContextFactory;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.impl.render.dynamic.response.UpdatePageLocationResponse;
import org.jboss.portal.theme.impl.render.dynamic.response.UpdatePageStateResponse;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.ThemeContext;
import org.jboss.portal.web.ServletContextDispatcher;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.menubar.MenubarUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.web.WebCommand;

/**
 * Ajoute une commande de redirection (sert notamment pour les erreurs sur les actions Ajax)
 * Coordination des publics parameters en AJAX (par défaut, les publics parameters obligent à recharger la page)
 *
 */
public class AjaxResponseHandler implements ResponseHandler {


    protected static final Log log = LogFactory.getLog(AjaxResponseHandler.class);

    /** . */
    private PortalObjectContainer portalObjectContainer;

    /** . */
    private PageService pageService;

    public PortalObjectContainer getPortalObjectContainer() {
        return this.portalObjectContainer;
    }

    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

    public PageService getPageService() {
        return this.pageService;
    }

    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }
    
    
    private boolean compareParameters(PageNavigationalState oldNS, PageNavigationalState newWS) {
        
        // Null tests
        if (newWS == null) {
            if (oldNS != null)
                return false;
            else return true;
        } else {
            if (oldNS == null)
                return true;
        }

        Map<QName, String[]> m1 = (Map<QName, String[]>) oldNS.getParameters();
        Map<QName, String[]> m2 = (Map<QName, String[]>) newWS.getParameters();
         
        if (m1.size() != m2.size())
            return false;

        Iterator<Entry<QName, String[]>> i = m1.entrySet().iterator();
        
        while (i.hasNext()) {
            Entry<QName, String[]> e = i.next();
            QName key = e.getKey();
            String[] value = e.getValue();
            if (value == null) {
                if (!(m2.get(key) == null && m2.containsKey(key)))
                    return false;
            } else {
                // Compare values
                String[] m1T = value;
                String[] m2T = m2.get(key);
                
                if( m1T.length != m2T.length)
                    return false;
                
                for(int mi=0; mi< m1T.length; mi++){
                    if( !m1T[mi].equals(m2T[mi]))   {
                        return false;
                    }
                }

            }
        }


        return true;

    }


    public HandlerResponse processCommandResponseOriginal(ControllerContext controllerContext, ControllerCommand commeand, ControllerResponse controllerResponse)
            throws ResponseHandlerException {
        if (controllerResponse instanceof PortletWindowActionResponse) {
            PortletWindowActionResponse pwr = (PortletWindowActionResponse) controllerResponse;
            StateString contentState = pwr.getContentState();
            WindowState windowState = pwr.getWindowState();
            Mode mode = pwr.getMode();
            ControllerCommand renderCmd = new InvokePortletWindowRenderCommand(pwr.getWindowId(), mode, windowState, contentState);
            
            controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.ajax.actionWindowID", pwr.getWindowId());
            
            if (renderCmd != null) {
                return new CommandForward(renderCmd, null);
            } else {
                return null;
            }
        } else if (controllerResponse instanceof SignOutResponse) {
            // Get the optional signout location
            String location = ((SignOutResponse) controllerResponse).getLocation();

            final ServerInvocation invocation = controllerContext.getServerInvocation();

            //
            if (location == null) {
                PortalObjectContainer portalObjectContainer = controllerContext.getController().getPortalObjectContainer();
                Portal portal = portalObjectContainer.getContext().getDefaultPortal();
                ViewPageCommand renderCmd = new ViewPageCommand(portal.getId());
                URLContext urlContext = invocation.getServerContext().getURLContext();
                location = controllerContext.renderURL(renderCmd, urlContext.asNonAuthenticated(), null);
            }

            // Indicate that we want a sign out to be done
            invocation.getResponse().setWantSignOut(true);

            // We perform a full refresh
            UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(location);
            return new AjaxResponse(dresp);
        } else if (controllerResponse instanceof UpdatePageResponse)
        // {
        // UpdatePageResponse upr = (UpdatePageResponse)controllerResponse;
        // ViewPageCommand rpc = new ViewPageCommand(upr.getPageId());
        // String url = controllerContext.renderURL(rpc, null, null);
        // UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(url);
        // return new AjaxResponse(dresp);
        // }
        // else if (controllerResponse instanceof UpdateWindowResponse)
        {

            /* v3.0.2 : empecher le rafraichissement systématique de tous les portlets */
            // boolean reloadAjaxWindows = false;

            UpdatePageResponse upw = (UpdatePageResponse) controllerResponse;

            // Obtain page and portal
            // final Window window = (Window)portalObjectContainer.getObject(upw.getWindowId());
            // Page page = (Page)window.getParent();
            final Page page = (Page) this.portalObjectContainer.getObject(upw.getPageId());

            //
            NavigationalStateContext ctx = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

            // The windows marked dirty during the request
            Set dirtyWindowIds = new HashSet();

            // Whether we need a full refresh or not
            boolean fullRefresh = false;


            // If the page has changed, need a full refresh
            // It's useful for error pages
            PortalObjectId portalObjectId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId");
            if( !page.getId().equals(portalObjectId))   {
                fullRefresh = true;
            }

            //

            if (ctx.getChanges() == null) {
                fullRefresh = true;
            } else {
                
                /*  
                 * If public parameters are not changed, recompute only current portlet 
                 * (only for actions)
                 */
                
                
                PortalObjectId filterWindow = null;
                
                PortalObjectId actionWindow = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.ajax.actionWindowID");
                
                boolean actionReload = false;
                if( "true".equals(controllerContext.getServerInvocation().getServerContext().getClientRequest().getParameter("reload.action")))
                    actionReload = true;

                if (actionWindow != null || actionReload) {
                    boolean publicParametersChanged = false;
                    boolean windowModeChange = false;


                    for (Iterator<? extends NavigationalStateChange> i = ctx.getChanges(); i.hasNext();) {
                        NavigationalStateChange change = i.next();

                        NavigationalStateObjectChange update = (NavigationalStateObjectChange) change;
                        NavigationalStateKey key = update.getKey();
                        Class<?> type = key.getType();
                        if (type == WindowNavigationalState.class) {
                            // Get old window state
                            WindowNavigationalState oldNS = (WindowNavigationalState) update.getOldValue();
                            WindowState oldWindowState = oldNS != null ? oldNS.getWindowState() : null;

                            // Get new window state
                            WindowNavigationalState newNS = (WindowNavigationalState) update.getNewValue();
                            WindowState newWindowState = newNS != null ? newNS.getWindowState() : null;

                            // Check if window state requires a refresh
                            if (WindowState.MAXIMIZED.equals(oldWindowState)) {
                                if (!WindowState.MAXIMIZED.equals(newWindowState)) {
                                    windowModeChange = true;
                                }
                            } else if (WindowState.MAXIMIZED.equals(newWindowState)) {
                                windowModeChange = true;
                        } else  if (type == PageNavigationalState.class) {
                            PageNavigationalState oldNS = (PageNavigationalState) update.getOldValue();
                            PageNavigationalState newNS = (PageNavigationalState) update.getNewValue();

                            publicParametersChanged = !compareParameters(oldNS, newNS);

                        }
                    }

                    if ((actionWindow != null &&(!publicParametersChanged && !windowModeChange))) {
                        filterWindow = actionWindow;
                    }

                }

                if(filterWindow != null)
                    dirtyWindowIds.add(filterWindow);
                else
                 for (Iterator i = ctx.getChanges(); i.hasNext();) {
                    NavigationalStateChange change = (NavigationalStateChange) i.next();

                    // A change that modifies potentially the page structure
                    if (!(change instanceof NavigationalStateObjectChange)) {
                        fullRefresh = true;
                        break;
                    }
                    NavigationalStateObjectChange update = (NavigationalStateObjectChange) change;
                    /*
                     * // A change that modifies potentially the page structure
                     * if (update.getType() != NavigationalStateObjectChange.UPDATE)
                     * {
                     * fullRefresh = true;
                     * break;
                     * }
                     */
                    // Get the state key
                    NavigationalStateKey key = update.getKey();

                    // We consider only portal object types
                    Class type = key.getType();
                    if (type == WindowNavigationalState.class) {
                        // Get old window state
                        WindowNavigationalState oldNS = (WindowNavigationalState) update.getOldValue();
                        WindowState oldWindowState = oldNS != null ? oldNS.getWindowState() : null;

                        // Get new window state
                        WindowNavigationalState newNS = (WindowNavigationalState) update.getNewValue();
                        WindowState newWindowState = newNS != null ? newNS.getWindowState() : null;

                        // Check if window state requires a refresh
                        if (WindowState.MAXIMIZED.equals(oldWindowState)) {
                            if (!WindowState.MAXIMIZED.equals(newWindowState)) {
                                fullRefresh = true;
                                break;
                            }
                        } else if (WindowState.MAXIMIZED.equals(newWindowState)) {
                            fullRefresh = true;
                            break;
                        }

                        // Collect the dirty window id
                        dirtyWindowIds.add(key.getId());
                    }   else   if (type == PageNavigationalState.class) {
                        break;
                    }

                }
            }


            if ("true".equals(controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.refreshPage"))) {
                fullRefresh = true;
            }




                if (!fullRefresh) {
                    
                 PortalObjectId dynamicWindowID =  (PortalObjectId) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "ajax.dynamicWindowID");
                 
                 if( dynamicWindowID != null && !dirtyWindowIds.contains(dynamicWindowID))  {
                     dirtyWindowIds.add(dynamicWindowID);
                 }
                    
                    
                    
                Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);


                for (PortalObject window : windows) {

                    if (window.getName().equals(InternalConstants.PORTAL_MENUBAR_WINDOW_NAME)) {
                        dirtyWindowIds.add(window.getId());
                    }

                    if (("selection".equals(window.getProperty("osivia.cacheEvents")))
                            || (StringUtils.isNotBlank(window.getProperty("osivia.sequence.priority")))) {
                        if ("true".equals(window.getProperty("theme.dyna.partial_refresh_enabled"))) {
                            if (!dirtyWindowIds.contains(window.getId())) {
                                dirtyWindowIds.add(window.getId());
                            }
                        } else {
                            fullRefresh = true;
                        }
                    }

                    // Prevent Ajax refresh (useful for keywords selector)
                    if (BooleanUtils.toBoolean(window.getDeclaredProperty(InternalConstants.ATTR_WINDOW_PREVENT_AJAX_REFRESH))) {
                        dirtyWindowIds.remove(window.getId());
                    }
                }
            }



            // Commit changes
            ctx.applyChanges();

            //
            if (!fullRefresh) {
                Set<Window> refreshedWindows = new TreeSet<Window>(new WindowComparator());
                for (PortalObject child : page.getChildren(PortalObject.WINDOW_MASK)) {
                    PortalObjectId childId = child.getId();
                    if (dirtyWindowIds.contains(childId)) {
                        refreshedWindows.add((Window) child);
					}   else    {
                        // TODO
                        // Gestion du back navigateur en Ajax
                        // Les algotrithmes de calcul de changement (ControllerPageNavigationalState/cpns) ne peuvent atre appliqués
                        // sur les retours Ajax car on ne peut pas rejouer les PortletActions en arriere, il faudrait donc réinjecter dans le cpns les 
                        // tous les paramètres modifiés en tenant compte des paramètres publics
                        // Du coup, on rafraichit toutes les windows
                        if( controllerContext.getServerInvocation().getServerContext().getClientRequest().getParameter("backPageMarker") != null) {
                            refreshedWindows.add((Window) child);
                        }
                    }                }

                // Obtain layout
                LayoutService layoutService = this.getPageService().getLayoutService();
                PortalLayout layout = RenderPageCommand.getLayout(layoutService, page);
                
                
                /* Create the reload Url
                 * it's obtained from a view page command associated with a new state 
                 */
                
                String replayUrl = "";
                
                PortalObjectId pageId = null;
                if( controllerCommand instanceof InvokePortletWindowRenderCommand)
                    pageId = ((InvokePortletWindowRenderCommand) controllerCommand).getPage().getId();
                if( controllerCommand instanceof WebCommand)    {
                    pageId = ((UpdatePageResponse) controllerResponse).getPageId();
                }
                
                
                if( pageId != null) {
                    
                    PortalObjectId modalId = PortalObjectId.parse("/osivia-util/modal", PortalObjectPath.CANONICAL_FORMAT);
                    if (!modalId.equals(page.getId())) {
                    
                        // Create a specific page marker for back action
                        String reloadPM = PageMarkerUtils.saveAsANewState(controllerContext, page);
                    
                        ViewPageCommand renderCmd = new ViewPageCommand(pageId);
                        PortalURL portalUrl = new PortalURLImpl(renderCmd, controllerContext, null, null);  
                        replayUrl = portalUrl.toString()+ "?backPageMarker="+reloadPM;
                    }
                 }

                

               //
                UpdatePageStateResponse updatePage = new UpdatePageStateResponse(ctx.getViewId(), replayUrl);

                // Call to the theme framework
                PageResult res = new PageResult(page.getName(), page.getProperties());

                //
                ServerInvocation invocation = controllerContext.getServerInvocation();

                //
                WindowContextFactory wcf = new WindowContextFactory(controllerContext);

                //
                ControllerPortletControllerContext portletControllerContext = new ControllerPortletControllerContext(controllerContext, page);
                ControllerPageNavigationalState pageNavigationalState = portletControllerContext.getStateControllerContext()
                        .createPortletPageNavigationalState(true);

                //
                for (Iterator i = refreshedWindows.iterator(); i.hasNext() && !fullRefresh;) {
                    try {
                        Window refreshedWindow = (Window) i.next();
                        RenderWindowCommand rwc = new RenderWindowCommand(pageNavigationalState, refreshedWindow.getId());
                        WindowRendition rendition = rwc.render(controllerContext);

                        //
                        if (rendition != null) {
                            ControllerResponse resp = rendition.getControllerResponse();

                            //
                            if (resp instanceof MarkupResponse) {
                                // Once this virtual window has been computed, it's not worth sending it into the response
                                // because the menu is displayed in menubar context
                                // Furthermore, dyna.js must parse its content
                                if( !refreshedWindow.getName().equals(InternalConstants.PORTAL_MENUBAR_WINDOW_NAME))    {
                                    WindowContext windowContext = wcf.createWindowContext(refreshedWindow, rendition);
                                    res.addWindowContext(windowContext);
                                    this.refreshWindowContext(controllerContext, layout, updatePage, res, windowContext);
                                }
                            } else {
                                fullRefresh = true;
                            }
                        } else {
                            // We'd better do a full refresh for now
                            // It could be handled as a portlet removal in the protocol between the client side and server side
                            fullRefresh = true;
                        }
                    } catch (Exception e) {
                        log.error("An error occured during the computation of window markup", e);

                        //
                        fullRefresh = true;
                    }
                }


                // Notifications & menubar refresh
                if (!fullRefresh) {
                    try {
                        // Check if current page is a modal
                        PortalObjectId modalId = PortalObjectId.parse("/osivia-util/modal", PortalObjectPath.CANONICAL_FORMAT);
                        if (!modalId.equals(page.getId())) {
                            PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

                            // Notifications window context
                            WindowContext notificationsWindowContext = NotificationsUtils.createNotificationsWindowContext(portalControllerContext);
                            res.addWindowContext(notificationsWindowContext);
                            this.refreshWindowContext(controllerContext, layout, updatePage, res, notificationsWindowContext);

                            // Menubar window context
                            WindowContext menubarWindowContext = MenubarUtils.createContentNavbarActionsWindowContext(portalControllerContext);
                            res.addWindowContext(menubarWindowContext);
                            this.refreshWindowContext(controllerContext, layout, updatePage, res, menubarWindowContext);
                        }
                    } catch (Exception e) {
                        log.error("An error occured during the computation of window markup", e);

                        //
                        fullRefresh = true;
                    }
                }

                //
                if (!fullRefresh) {
                    // Add render to the page
                    // Juste pour le test mais ca marche (récupérabel dans dyna.js sous forme de fragment)

                    // updatePage.addFragment("notification", "message");
                    PageMarkerUtils.savePageState(controllerContext, page);
                                         
                    controllerContext.getServerInvocation().getServerContext().getClientResponse().addHeader("Cache-Control", "no-cache, max-age=0, must-revalidate, no-store");
 
                    return new AjaxResponse(updatePage);
                }
            }

            // We perform a full refresh

            PageMarkerUtils.savePageState(controllerContext, page);


            ViewPageCommand rpc = new ViewPageCommand(page.getId());
            String url = controllerContext.renderURL(rpc, null, null);
            UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(url);
            return new AjaxResponse(dresp);
        } else {
            return null;
        }
    }

    /**
     * Refresh window context.
     *
     * @param controllerContext controller context
     * @param layout portal layout
     * @param updatePage update page response
     * @param res page result
     * @param windowContext window context
     * @throws RenderException
     */
    private void refreshWindowContext(ControllerContext controllerContext, PortalLayout layout, UpdatePageStateResponse updatePage, PageResult res,
            WindowContext windowContext) throws RenderException {
        // Server invocation
        ServerInvocation invocation = controllerContext.getServerInvocation();
        // Server context
        ServerInvocationContext serverContext = invocation.getServerContext();

        // Markup info
        MarkupInfo markupInfo = (MarkupInfo) invocation.getResponse().getContentInfo();

        // Buffer
        StringWriter buffer = new StringWriter();

        // Dispatcher
        ServletContextDispatcher dispatcher = new ServletContextDispatcher(serverContext.getClientRequest(), serverContext
                .getClientResponse(), controllerContext.getServletContainer());

        // Not really used for now in that context, so we can pass null (need to change that of course)
        ThemeContext themeContext = new ThemeContext(null, null);

        // Render context
        RendererContext rendererContext = layout.getRenderContext(themeContext, markupInfo, dispatcher, buffer);

        // Push page
        rendererContext.pushObjectRenderContext(res);

        // Push notifications region
        Region notificationsRegion = res.getRegion2(windowContext.getRegionName());
        rendererContext.pushObjectRenderContext(notificationsRegion);

        // Render
        rendererContext.render(windowContext);

        // Pop region
        rendererContext.popObjectRenderContext();

        // Pop page
        rendererContext.popObjectRenderContext();

        // Add render to the page
        updatePage.addFragment(windowContext.getId(), buffer.toString());
    }


    /**
     * {@inheritDoc}
     */
    public HandlerResponse processCommandResponse(ControllerContext controllerContext, ControllerCommand command, ControllerResponse controllerResponse)
            throws ResponseHandlerException {
        HandlerResponse resp = this.processCommandResponseOriginal(controllerContext, command, controllerResponse);
        if (resp == null) {
            if (controllerResponse instanceof RedirectionResponse) {
                String url = ((RedirectionResponse) controllerResponse).getLocation();
                UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(url);
                return new AjaxResponse(dresp);
            }
        }
        return resp;
    }


    private class WindowComparator implements Comparator<Window> {

        public int compare(Window w1, Window w2) {

            String order1 = w1.getDeclaredProperty("osivia.sequence.priority");
            String order2 = w2.getDeclaredProperty("osivia.sequence.priority");

            // Window with no priority will be executed in parallel mode
            // So they are sorted before priority windows

            if (order1 == null) {
                if (order2 == null) {
                    return w1.getName().compareTo(w2.getName());
                } else {
                    return -1;
                }
            } else if (order2 == null) {
                return 1;
            } else {
                return Integer.valueOf(order1).compareTo(Integer.valueOf(order2));
            }

        }

    }

}
