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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
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
import org.jboss.portal.core.model.portal.Window;
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
import org.osivia.portal.core.menubar.MenubarUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;

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


    public HandlerResponse processCommandResponseOriginal(ControllerContext controllerContext, ControllerCommand commeand, ControllerResponse controllerResponse)
            throws ResponseHandlerException {
        if (controllerResponse instanceof PortletWindowActionResponse) {
            PortletWindowActionResponse pwr = (PortletWindowActionResponse) controllerResponse;
            StateString contentState = pwr.getContentState();
            WindowState windowState = pwr.getWindowState();
            Mode mode = pwr.getMode();
            ControllerCommand renderCmd = new InvokePortletWindowRenderCommand(pwr.getWindowId(), mode, windowState, contentState);
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

                        PageNavigationalState oldNS = (PageNavigationalState) update.getOldValue();
                        Object updateOld = update.getOldValue();


                        // Get new window state
                        PageNavigationalState newNS = (PageNavigationalState) update.getNewValue();
                        Object updateNew = update.getNewValue();

                        if( ! updateOld.equals(updateOld)) {
                            fullRefresh = true;
                        }
                        break;
                    }
                    /* v3.0.2 : empecher le rafraichissement systématique de tous les portlets */

                    /*
                     * else if (type == PageNavigationalState.class) {
                     *
                     * // Pas de rechargement de la page si les parametres publics sont modifies
                     *
                     * // force full refresh for now...
                     * // fullRefresh = true;
                     *
                     * // A la place, on recharge les windows
                     * reloadAjaxWindows = true;
                     *
                     * // On peut vérifier que les paramètres publics on bien été modifiés
                     * // PageNavigationalState pp = (PageNavigationalState) update.getNewValue();
                     * }
                     */
                }
            }


            if ("true".equals(controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.refreshPage"))) {
                fullRefresh = true;
            }


            // TODO : rechargement systématique des windows supportant le mode Ajax
            /*
             * if( reloadAjaxWindows && !fullRefresh){
             *
             * Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);
             *
             * for (PortalObject window : windows) {
             *
             * if ("true".equals(window.getProperty("theme.dyna.partial_refresh_enabled"))) {
             * if (!dirtyWindowIds.contains(window.getId()))
             * dirtyWindowIds.add(window.getId());
             * }
             * }
             */

            // v2.0.8 : gestion des evenements de selection

            /* v3.0.2 : empecher le rafraichissement systématique de tous les portlets */
//            if (!reloadAjaxWindows && !fullRefresh) {
                if (!fullRefresh) {
                Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);


                for (PortalObject window : windows) {

                    if ("selection".equals(window.getProperty("osivia.cacheEvents"))) {
                        if ("true".equals(window.getProperty("theme.dyna.partial_refresh_enabled"))) {
                            if (!dirtyWindowIds.contains(window.getId())) {
                                dirtyWindowIds.add(window.getId());
                            }
                        } else {
                            fullRefresh = true;
                        }
                    }
                }
            }


            // Le rafraichissment de la page doit etre explicitement demandé par le portlet

            /* v3.0.2 : empecher le rafraichissement systématique de tous les portlets */

            // if (reloadAjaxWindows) {
            // if (!"true".equals(controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.refreshPage"))) {
            //
            // Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);
            //
            //
            // for (PortalObject window : windows) {
            //
            // if ("true".equals(window.getProperty("theme.dyna.partial_refresh_enabled"))) {
            // if (!dirtyWindowIds.contains(window.getId())) {
            // dirtyWindowIds.add(window.getId());
            // }
            // }
            // }
            // } else {
            // fullRefresh = true;
            // }
            //
            // }



            // Commit changes
            ctx.applyChanges();

            //
            if (!fullRefresh) {
                ArrayList<PortalObject> refreshedWindows = new ArrayList<PortalObject>();
                for (PortalObject child : page.getChildren(PortalObject.WINDOW_MASK)) {
                    PortalObjectId childId = child.getId();
                    if (dirtyWindowIds.contains(childId)) {
                        refreshedWindows.add(child);
                    }
                }

                // Obtain layout
                LayoutService layoutService = this.getPageService().getLayoutService();
                PortalLayout layout = RenderPageCommand.getLayout(layoutService, page);

                //
                UpdatePageStateResponse updatePage = new UpdatePageStateResponse(ctx.getViewId());

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
                                WindowContext windowContext = wcf.createWindowContext(refreshedWindow, rendition);
                                res.addWindowContext(windowContext);
                                this.refreshWindowContext(controllerContext, layout, updatePage, res, windowContext);
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
                        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

                        // Notifications window context
                        WindowContext notificationsWindowContext = NotificationsUtils.createNotificationsWindowContext(portalControllerContext);
                        res.addWindowContext(notificationsWindowContext);
                        this.refreshWindowContext(controllerContext, layout, updatePage, res, notificationsWindowContext);

                        // Menubar window context
                        WindowContext menubarWindowContext = MenubarUtils.createContentNavbarActionsWindowContext(portalControllerContext);
                        res.addWindowContext(menubarWindowContext);
                        this.refreshWindowContext(controllerContext, layout, updatePage, res, menubarWindowContext);
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

}
