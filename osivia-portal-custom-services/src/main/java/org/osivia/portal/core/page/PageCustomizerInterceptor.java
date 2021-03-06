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
package org.osivia.portal.core.page;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.command.WindowCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowActionCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.render.RenderWindowCommand;
import org.jboss.portal.core.model.portal.command.view.ViewContextCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateChange;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.core.navstate.NavigationalStateObjectChange;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.User;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.server.config.ServerConfig;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.log.LoggerMessage;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.api.player.Player;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.api.status.IStatusService;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.windows.StartingWindowBean;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSPlayHandlerUtils;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.error.IPortalLogger;
import org.osivia.portal.core.menubar.MenubarUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.pagemarker.PortalCommandFactory;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;
import org.osivia.portal.core.portalobjects.DynamicWindow;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.security.CmsPermissionHelper;
import org.osivia.portal.core.security.CmsPermissionHelper.Level;


/**
 * Page customizer interceptor.
 *
 * @see ControllerInterceptor
 */
public class PageCustomizerInterceptor extends ControllerInterceptor {

    /** Window logger. */
    private static final Log windowlogger = LogFactory.getLog("PORTAL_WINDOW");
    

    /** Logger. */
    private static final Log logger = LogFactory.getLog(PageCustomizerInterceptor.class);
    /** JBoss administration portal identifier. */
    private static PortalObjectId adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);
    /** Import running indicator. */
    private static boolean isImportRunning = false;


    /** Login namespace. */
    private String loginNamespace;

    /** Server config. */
    private ServerConfig config;
    /** Portal object container. */
    private PortalObjectContainer portalObjectContainer;
    /** URL factory. */
    private IPortalUrlFactory urlFactory;
    /** Services cache service. */
    private ICacheService servicesCacheService;
    /** Profiler service. */
    private transient IProfilerService profiler;
    /** CMS service locator. */
    private static ICMSServiceLocator cmsServiceLocator;
    /** Taskbar service. */
    private ITaskbarService taskbarService;
    /** Status service. */
    
    private IStatusService statusService;


    /**
     * Constructor.
     */
    public PageCustomizerInterceptor() {
        super();
    }


    /**
     * Static access to CMS service.
     *
     * @return CMS service
     */
    private static ICMSService getCMSService() {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }
        return cmsServiceLocator.getCMSService();
    }


    /**
     * Check if current user is an administrator.
     *
     * @param controllerContext controller context
     * @return true if current user is an administrator
     */
    public static boolean isAdministrator(ControllerContext controllerContext) {
        Boolean isAdministrator = (Boolean) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, "osivia.isAdmin");
        if (isAdministrator == null) {
            PortalAuthorizationManager portalAuthorizationManager = controllerContext.getController().getPortalAuthorizationManagerFactory().getManager();
            PortalObjectPermission permission = new PortalObjectPermission(adminPortalId, PortalObjectPermission.VIEW_MASK);
            isAdministrator = portalAuthorizationManager.checkPermission(permission);

            controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, "osivia.isAdmin", isAdministrator);
        }
        return isAdministrator;
    }


    private static boolean initShowMenuBarItem(ControllerContext controllerCtx, Portal portal) {

        // Uniquement en mode wizzard

        Boolean showMenuBar = (Boolean) controllerCtx.getAttribute(Scope.REQUEST_SCOPE, "osivia.showMenuBarItem");
        if (showMenuBar == null) {

            String menuBarPolicy = portal.getProperty("osivia.menuBarPolicy");
            showMenuBar = new Boolean(true);
            if ("wizzardOnly".equals(menuBarPolicy)) {
                if (!"wizzard".equals(controllerCtx.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.windowSettingMode"))) {
                    showMenuBar = new Boolean(false);
                }

            }

            controllerCtx.setAttribute(Scope.REQUEST_SCOPE, "osivia.showMenuBarItem", showMenuBar);
        }
        return showMenuBar;
    }


    public static void initPageState(Page page, ControllerContext controllerCtx) {


        /* Init page parameters */

        NavigationalStateContext nsContext = (NavigationalStateContext) controllerCtx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

        //
        String pageId = page.getId().toString();

        PageNavigationalState previousPNS = nsContext.getPageNavigationalState(pageId);
        Map<QName, String[]> state = new HashMap<QName, String[]>();


        if (previousPNS != null) {
            state.putAll(previousPNS.getParameters());
        }


        // init cms Path
        state.remove(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
        state.remove(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.pageScope"));
        state.remove(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.itemRelPath"));


        nsContext.setPageNavigationalState(pageId, new PageNavigationalState(state));

        /* Init window states */

        unsetMaxMode(page.getChildren(PortalObject.WINDOW_MASK), controllerCtx);

        initPageBackInfos(controllerCtx);
     }


    public static void initPageBackInfos(ControllerContext controllerCtx) {
        initPageBackInfos( controllerCtx, true);
    }

    public static void initPageBackInfos(ControllerContext controllerCtx, boolean mobile) {
        /* Init back actions */

        controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backPageMarker", null);
        controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.refreshBack", null);


        if( mobile) {
            controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backMobilePageMarker", null);
            controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.mobileRefreshBack", null);
        }
    }


    public static void unsetMaxMode(Collection<PortalObject> windows, ControllerContext controllerCtx) {

        // Maj du breadcrumb
        controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb", null);





        // Reinitialtion du path CMS


        Iterator<PortalObject> i = windows.iterator();

        while (i.hasNext()) {

            Window window = (Window) i.next();

            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

            WindowNavigationalState windowNavState = (WindowNavigationalState) controllerCtx.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);
            // On regarde si la fenêtre est en vue MAXIMIZED


            if ((windowNavState != null) && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {

                // On la force en vue NORMAL
                WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.NORMAL, windowNavState.getMode(),
                        windowNavState.getContentState());
                controllerCtx.setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);

                if (windowlogger.isDebugEnabled()) {
                    windowlogger.debug("initPageState " + window.getId() + ": maximized -> normal");
                }

                /* v3.0.2 : empecher le rafraichissement systématique de tous les portlets */
                /*
                 * // On force le rafrachissement de la page (requete Ajax)
                 * controllerCtx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.refreshPage", "true");
                 */

            }
        }


    }


    /**
     * détermine si les caches globaux de la page d'accueil peuvent être utilisés
     *
     * @param portalObjectContainer
     * @param cmd
     * @param controllerCtx
     * @return
     */
    private static boolean controlDefaultPageCache(PortalObjectContainer portalObjectContainer, ControllerCommand cmd, ControllerContext controllerCtx) {

        boolean isDefaultPageCache = false;

        
        if (cmd instanceof RenderPageCommand) {
        	
            if( "1".equals(System.getProperty("osivia.homePageCache")))	{
        	

	            RenderPageCommand rpc = (RenderPageCommand) cmd;
	
	            // Mode anonyme
	            HttpServletRequest request = controllerCtx.getServerInvocation().getServerContext().getClientRequest();
	
	            String pageMarker = (String) controllerCtx.getAttribute(Scope.REQUEST_SCOPE, "currentPageMarker");
            


	            // Premier acces seulement (pour l'instant)
	            if ("1".equals(pageMarker)) {
	
	                if (request.getUserPrincipal() == null) {
	                    // Page d'accueil
	                    if (rpc.getPage().getId().equals(portalObjectContainer.getContext().getDefaultPortal().getDefaultPage().getId())) {
	
	                        controllerCtx.setAttribute(Scope.REQUEST_SCOPE, "osivia.useGlobalWindowCaches", "1");
	
	                        isDefaultPageCache = true;
	                    }
	                }
	            }
            }

        }
        return (isDefaultPageCache);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
        // Controller context
        ControllerContext controllerContext = cmd.getControllerContext();
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);


        // v1.0.16 : lock during import
        if (isImportRunning == true) {
            while (isImportRunning == true) {
                Thread.sleep(1000L);
            }
        }
        
        if (cmd instanceof RenderPageCommand) {
            if(!statusService.isReady("NX-OVERLOAD"))
                Thread.sleep(500L);
        }
        
        
        long begin = 0;
        boolean error = false;


        if (logger.isDebugEnabled()) {
            logger.debug("PageCustomizerInterceptor test2 commande " + cmd.getClass().getName());
        }



        /* Récupération du domainID */

        String domainComputed = PageProperties.getProperties().getPagePropertiesMap().get("osivia.cms.domainId.computed");

        if (domainComputed == null) {

            PageProperties.getProperties().getPagePropertiesMap().put("osivia.cms.domainId.computed", "1");

            String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);

            if (portalName != null) {

                PortalObjectId portalId = PortalObjectId.parse("/" + portalName, PortalObjectPath.CANONICAL_FORMAT);

                PortalObject po = this.portalObjectContainer.getObject(portalId);

                // Pas de page par defaut pour le portail util (NPE) !!!!
                Page defPage = ((Portal) po).getDefaultPage();

                if (defPage != null) {


                    String basePath = ((Portal) po).getDefaultPage().getDeclaredProperty("osivia.cms.basePath");


                    if (basePath != null) {
                        CMSServiceCtx cmsReadItemContext = new CMSServiceCtx();
                        cmsReadItemContext.setControllerContext(controllerContext);

                        CMSItem spaceConfig = getCMSService().getSpaceConfig(cmsReadItemContext, basePath);

                        if (spaceConfig != null) {
                            String domainId = spaceConfig.getDomainId();

                            if (!StringUtils.isEmpty(domainId)) {
                                PageProperties.getProperties().getPagePropertiesMap().put("osivia.cms.domainId", domainId);
                            }
                        }
                    }
                }
            }
        }





        /* Desactivation of pagemarker */

        String disablePageMarkerComputed = PageProperties.getProperties().getPagePropertiesMap().get("osivia.portal.disablePageMarkerComputed");

        if (disablePageMarkerComputed == null) {

            PageProperties.getProperties().getPagePropertiesMap().put("osivia.portal.disablePageMarkerComputed", "1");

            String portalName = PageProperties.getProperties().getPagePropertiesMap().get(Constants.PORTAL_NAME);

            if (portalName != null) {

                PortalObjectId portalId = PortalObjectId.parse("/" + portalName, PortalObjectPath.CANONICAL_FORMAT);

                PortalObject po = this.portalObjectContainer.getObject(portalId);

                String disablePageMarker = po.getDeclaredProperty("osivia.portal.disablePageMarker");

                if( "1".equals(disablePageMarker)){

                    HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();

                     if (request.getUserPrincipal() == null) {
                          PageProperties.getProperties().getPagePropertiesMap().put("osivia.portal.disablePageMarker", "1");
                     }
                }
            }
        }



        /* Le player d'un item CMS doit être rejoué en cas de refresh
         *
         * (mais on garde les render parameters et l'état)
         *
         * */



        if (cmd instanceof RenderWindowCommand) {
            Window window = ((RenderWindowCommand) cmd).getWindow();

            // Only concerns player window
            if ("CMSPlayerWindow".equals(window.getName()) &&  "1".equals( window.getProperties().get("osivia.cms.contextualization"))) {
                if (PageProperties.getProperties().isRefreshingPage()
                        || "1".equals(controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.changeContributionMode"))) {

                    // original window path
                    String cmsPath = window.getDeclaredProperty(Constants.WINDOW_PROP_URI);
//                    String cmsPath = PagePathUtils.getContentPath(cmd.getControllerContext(), window.getPage().getId());


                    CMSServiceCtx cmsReadItemContext = new CMSServiceCtx();
                    cmsReadItemContext.setControllerContext(controllerContext);



                    String forcedLivePath = null;


                    // Force live version in EDITION mode
                    EditionState state = ContributionService.getWindowEditionState(controllerContext, window.getId());
                    if( (state != null) && EditionState.CONTRIBUTION_MODE_EDITION.equals(state.getContributionMode()) ) {
                        cmsPath = state.getDocPath();
                        forcedLivePath = state.getDocPath();
                    }

                    if( forcedLivePath != null) {
                        cmsReadItemContext.setForcedLivePath(forcedLivePath);
                    }






                    CMSItem cmsItem = getCMSService().getContent(cmsReadItemContext, cmsPath);

                    CMSServiceCtx handlerCtx = new CMSServiceCtx();
                    handlerCtx.setControllerContext(controllerContext);
                    handlerCtx.setDoc(cmsItem.getNativeItem());
                    handlerCtx.setServletRequest(controllerContext.getServerInvocation().getServerContext().getClientRequest());



                    // Restore handle properties
                    CMSPlayHandlerUtils.restoreHandlerProperties(window, handlerCtx);

                    if( forcedLivePath != null) {
                        handlerCtx.setForcedLivePath(forcedLivePath);
                    }



                    // Invoke handler to get player
                    Player contentProperties = getCMSService().getItemHandler(handlerCtx);

                    Map<String,String> windowProps = ((DynamicWindow) window).getDynamicWindowBean().getProperties();

                    for (String propName : contentProperties.getWindowProperties().keySet()) {
                        windowProps.put(propName, contentProperties.getWindowProperties().get(propName));
                    }

                    DynamicPortalObjectContainer.clearCache();

                    // Reload the window
                    ((RenderWindowCommand) cmd).acquireResources();
                }
            }
        }


        if (cmd instanceof RenderPageCommand) {
            begin = System.currentTimeMillis();
            
            if( IPortalLogger.logger.isDebugEnabled()){
                IPortalLogger.logger.debug(new LoggerMessage("page enter"));
            }
            
        }

        // v2.0.22-RC6 Force to reload resources
        if ((cmd instanceof RenderPageCommand) || ((cmd instanceof RenderWindowCommand) && (ControllerContext.AJAX_TYPE == controllerContext.getType()))) {

            //if window is closing, refresh information must be preserved for next output

            if (!"1".equals(controllerContext.getServerInvocation().getServerContext().getClientRequest().getAttribute("osivia.popupModeClosing"))) {

                if ("true".equals(controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.updateContents"))) {
                    PageProperties.getProperties().setRefreshingPage(true);

                    controllerContext.removeAttribute(ControllerCommand.SESSION_SCOPE, "osivia.updateContents");
                }

             }


        }


        if (cmd instanceof RenderPageCommand) {
            RenderPageCommand rpc = (RenderPageCommand) cmd;
            Portal portal = rpc.getPortal();

            HttpServletRequest request = rpc.getControllerContext().getServerInvocation().getServerContext().getClientRequest();

			// Set page properties into request
            request.setAttribute("osivia.pageProperties", rpc.getPage().getProperties());


            /* Controle du host */
            String host = portal.getDeclaredProperty("osivia.site.hostName");
            String reqHost = controllerContext.getServerInvocation().getServerContext().getClientRequest().getServerName();

            if ((host != null) && !reqHost.equals(host)) {
                ViewPageCommand viewCmd = new ViewPageCommand(((RenderPageCommand) cmd).getPage().getId());
                String url = new PortalURLImpl(viewCmd, controllerContext, null, null).toString();
                url = url.replaceFirst(reqHost, host);
                url += "?init-state=true";
                return new RedirectionResponse(url.toString());
            }
            
            
            /* Store all pages */
            Set<String> pagesInSession = (Set<String>) controllerContext.getServerInvocation().getServerContext().getClientRequest().getSession().getAttribute("osivia.pageinSessions");
            if( pagesInSession == null) {
                pagesInSession = new TreeSet();
                controllerContext.getServerInvocation().getServerContext().getClientRequest().getSession().setAttribute("osivia.pageinSessions", pagesInSession);
  
            }
            pagesInSession.add( ((RenderPageCommand) cmd).getTargetId().toString(PortalObjectPath.CANONICAL_FORMAT));      


            controlDefaultPageCache(this.portalObjectContainer, cmd, controllerContext);


            if (rpc.getPage().getName().startsWith("exception")) {
                throw new RuntimeException("erreur de test");
            }

            if ("true".equals(request.getParameter("init-cache"))) {
                if (isAdministrator(controllerContext)) {
                    this.servicesCacheService.initPortalParameters();
                }
            }


            controllerContext.setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, Constants.ATTR_PAGE_ID, rpc.getPage().getId());


            // Force la valorisation dans le contexte
            isAdministrator(controllerContext);



            /*
             * Synchronize LIVE EDITION MODE
             *
             * Is player in edition mode ?
             *
             * */

            Window cmsWindow = ((RenderPageCommand) cmd).getPage().getWindow("CMSPlayerWindow");
            if (cmsWindow != null) {

                NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, cmsWindow.getId());

                WindowNavigationalState windowNavState = (WindowNavigationalState) controllerContext.getAttribute(
                        ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);
                // On regarde si la fenêtre est en vue MAXIMIZED
                if ((windowNavState != null) && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {

                    EditionState state = ContributionService.getWindowEditionState(controllerContext, cmsWindow.getId());

                    if( (state != null) && EditionState.CONTRIBUTION_MODE_EDITION.equals(state.getContributionMode()) ) {
                        controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LIVE_DOCUMENT, state.getDocPath());
                    }
                }
            }
        }


        /*
         * Synchronisation des pages de rubrique CMS quand leur affichage est en mode portlet MAX (et non en mode page)
         * Si tous les portlets sont en mode normal, ll faut forcer un appel CMS pour recharger la page
         *
         * Cas d'un fenetre maximisee qui repasse en mode normal sur action utilisateur
         * Cas d'une fentre dynamique qui est fermée proquant le retour à la page de rubrique
         */

        if (cmd instanceof CmsCommand) {
            // Permet de savoir si on est déjà dans le cas d'une CMSCommand qui appelle une RenderPageCommand
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, "cmsCommand", "1");
        }


        Boolean maximized = null;
        if ((cmd instanceof RenderPageCommand)
                || ((cmd instanceof RenderWindowCommand) && (ControllerContext.AJAX_TYPE == controllerContext.getType()))) {
            PageCommand pageCommand = (PageCommand) cmd;
            Page page = pageCommand.getPage();

            maximized = false;
            for (PortalObject portalObject : page.getChildren(PortalObject.WINDOW_MASK)) {
                Window window = (Window) portalObject;

                NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());
                WindowNavigationalState windowNavState = (WindowNavigationalState) controllerContext.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE,
                        nsKey);

                if ((windowNavState != null) && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {
                    maximized = true;
                    break;
                }
            }

            controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.portal.maximized", maximized);
        }


        /* Analyse du mode d'edition
         *
         * injection pathPublication & isPageInEditionMode
         * */



        String pathPublication = null;

        if ((cmd instanceof RenderPageCommand)
                || ((cmd instanceof RenderWindowCommand) && (ControllerContext.AJAX_TYPE == controllerContext.getType()))) {

            boolean onlinePage = true;


            Page page = null;

            if( cmd instanceof RenderPageCommand) {
                page = ((RenderPageCommand) cmd).getPage();
            }
            if( cmd instanceof RenderWindowCommand) {
                page = ((RenderWindowCommand) cmd).getPage();
            }

            // Online mode indicator

            pathPublication = PagePathUtils.getNavigationPath(controllerContext, page.getId());

            if ((pathPublication != null) && !"1".equals(page.getProperty("osivia.cms.directContentPublisher"))) {

                if (PortalObjectUtils.isSpaceSite(page)) {
                    Level level = CmsPermissionHelper.getCurrentPageSecurityLevel(controllerContext, page.getId());
                    if( level == Level.notAWebPage)
                        onlinePage = true;
                    else    {
                        onlinePage = Level.allowOnlineVersion.equals(level);
                    }
                    
                } else {
                    NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext
                            .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
                    PageNavigationalState ns = nsContext.getPageNavigationalState(page.getId().getPath().toString());
                    if (ns != null) {
                        EditionState editionState = ContributionService.getNavigationalState(controllerContext, ns);
                        onlinePage = (editionState == null) || EditionState.CONTRIBUTION_MODE_ONLINE.equals(editionState.getContributionMode()) || (!pathPublication.equals(editionState.getDocPath()));
                    } else {
                        onlinePage = true;
                    }
                }
            }


            controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.cms.isPageInEditionMode", onlinePage ? Boolean.FALSE : Boolean.TRUE);
       }


        /* Redirection si page display mode */

        if (cmd instanceof RenderPageCommand) {
            RenderPageCommand rpc = (RenderPageCommand) cmd;

            Page page = rpc.getPage();

            if ((pathPublication != null) && !"1".equals(page.getProperty("osivia.cms.directContentPublisher"))) {

                // On est déja dans une cmscommand, auquel cas l'affichage est bon

                if (!"1".equals(controllerContext.getAttribute(Scope.REQUEST_SCOPE, "cmsCommand"))) {

                    String navigationScope = page.getProperty("osivia.cms.navigationScope");
                    String basePath = page.getProperty("osivia.cms.basePath");

                    CMSServiceCtx cmxCtx = new CMSServiceCtx();
                    cmxCtx.setControllerContext(controllerContext);
                    cmxCtx.setScope(navigationScope);

                    Boolean pageInEditionMode = (Boolean) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.isPageInEditionMode");

                    if( BooleanUtils.isTrue(pageInEditionMode)) {
                        cmxCtx.setDisplayLiveVersion("1");
                    }



                    CMSItem navItem = getCMSService().getPortalNavigationItem(cmxCtx, basePath, pathPublication);

                    // Affichage en mode page ?
                    // Si oui, redirection CMS

                    if ( !basePath.equals(pathPublication) && ((navItem == null) || !"1".equals(navItem.getProperties().get("pageDisplayMode")))) {

                        CMSItem pagePublishSpaceConfig = CmsCommand.getPagePublishSpaceConfig(controllerContext, rpc.getPage());

                        if ((pagePublishSpaceConfig != null) && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeInternalContents"))) {
                            if (BooleanUtils.isFalse(maximized)) {
                                // Redirection en mode CMS
                                HttpServletRequest request = controllerContext.getServerInvocation().getServerContext().getClientRequest();

                                String url = this.urlFactory.getCMSUrl(new PortalControllerContext(controllerContext),
                                        rpc.getPage().getId().toString(PortalObjectPath.CANONICAL_FORMAT), pathPublication, null,
                                        IPortalUrlFactory.CONTEXTUALIZATION_PAGE, null, null, null, null, null);

                                if (request.getParameter("firstTab") != null) {
                                    url += "&firstTab=" + request.getParameter("firstTab");
                                }

                                url += "&skipPortletCacheInitialization=1";

                                return new RedirectionResponse(url.toString());
                            }
                        }
                    }
                }

            }
        }

        /* Injection du webPagePath et webPageEditionPath */

        if ((cmd instanceof RenderPageCommand)
                || ((cmd instanceof RenderWindowCommand) && (ControllerContext.AJAX_TYPE == controllerContext.getType()))) {
            initShowMenuBarItem(controllerContext, ((PortalCommand) cmd).getPortal());
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.currentPortalName", ((PortalCommand) cmd).getPortal().getName());
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.themePath", getTargetContextPath((PortalCommand) cmd));


            /* Inject cms Path */
            NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

            Page page = null;
            if (cmd instanceof RenderPageCommand) {
                page = ((RenderPageCommand) cmd).getPage();
            } else {
                page = ((RenderWindowCommand) cmd).getPage();
            }


            PageNavigationalState pageState = nsContext.getPageNavigationalState(page.getId().toString());

            String sPath[] = null;
            String contentPath = null;
            if (pageState != null) {
                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));

                String[] contentPathParameter = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.contentPath"));
                if (ArrayUtils.isNotEmpty(contentPathParameter)) {
                    contentPath = contentPathParameter[0];
                }
            }

            if ((sPath != null) && (sPath.length > 0)) {
                controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path", sPath[0]);
                controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.cms.contentPath", contentPath);


                /* Inject path web page edition */

                CMSServiceCtx cmxCtx = new CMSServiceCtx();
                cmxCtx.setControllerContext(controllerContext);


                Boolean pageInEditionMode = (Boolean) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.isPageInEditionMode");

                if( BooleanUtils.isTrue(pageInEditionMode)) {
                    cmxCtx.setDisplayLiveVersion("1");
                }


                Boolean layoutCMS = (Boolean) controllerContext.getAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LAYOUT_CMS_INDICATOR);
                if (BooleanUtils.isTrue(layoutCMS)) {
                    if (StringUtils.startsWith(contentPath, sPath[0]) && getCMSService().isCmsWebPage(cmxCtx, sPath[0])) {
                        controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.cms.webPagePath", sPath[0]);
                        if (pageInEditionMode) {
                            controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.cms.webPageEditionPath", sPath[0]);
                        }
                    }
                }
            }


            /* Inject space config */
            CMSItem pageConfig = CmsCommand.getPagePublishSpaceConfig(controllerContext, page);

            controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.cms.spaceConfig", pageConfig);
        }


        // v2.1 Entering and exiting the admin popup mode
        if (cmd instanceof InvokePortletWindowRenderCommand) {
            if ("admin".equals(controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode"))) {
                if (!Mode.ADMIN.equals(((InvokePortletWindowRenderCommand) cmd).getMode())) {
                    // Exiting admin mode
                    // Store in session because of redirection in AJAX MODE
                    controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.exitPopupAdminMode", "1");

                }
            } else {
                if (Mode.ADMIN.equals(((InvokePortletWindowRenderCommand) cmd).getMode())) {
                    // Entering admin mode
                    controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", "admin");
                    controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID",
                            ((InvokePortletWindowRenderCommand) cmd).getTargetId());
                }
            }
        }





        if (cmd instanceof RenderPageCommand) {
            NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

            Page page = ((RenderPageCommand) cmd).getPage();

            PageNavigationalState pageState = nsContext.getPageNavigationalState(page.getId().toString());


            String sSelector[] = null;
            if (pageState != null) {
                sSelector = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "selectors"));
            }



            if ((sSelector != null) && (sSelector.length > 0)) {
                Map<String, List<String>> selectors = PageParametersEncoder.decodeProperties(sSelector[0]);
                boolean advancedSearchFilters = false;
                for (String selectorId : selectors.keySet()) {
                    if (!"search".equals(selectorId) && !"selectorChanged".equals(selectorId)) {
                        advancedSearchFilters = true;
                        break;
                    }
                }
                controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.advancedSearch.filters", String.valueOf(advancedSearchFilters));
            }

            /* Masquage de la region advanced search */

            if (!"wizzard".equals(controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.windowSettingMode"))) {


                boolean hideAdvancedSearchFilters = false;

                String advancedSelectors = page.getProperty("osivia.advancedSearchSelectors");
                if (StringUtils.isNotEmpty(advancedSelectors)) {
                    hideAdvancedSearchFilters = true;
                    List<String> selectorsToCheck = Arrays.asList(advancedSelectors.split(","));
                    if ((sSelector != null) && (sSelector.length > 0)) {
                        Map<String, List<String>> selectors = PageParametersEncoder.decodeProperties(sSelector[0]);

                        for (String selectorId : selectors.keySet()) {
                            if (selectorsToCheck.contains(selectorId)) {
                                hideAdvancedSearchFilters = false;
                                break;
                            }
                        }
                    }
                }


                if (hideAdvancedSearchFilters) {
                    controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.advancedSearchFilters", "off");
                }
            }


        }


        ControllerResponse resp;

        try {

            resp = (ControllerResponse) cmd.invokeNext();

        } catch (Exception e) {
            error = true;
            throw e;
        } finally {
            // Profiler

            if (cmd instanceof RenderPageCommand) {
                long end = System.currentTimeMillis();
                long elapsedTime = end - begin;

                Page page = ((PageCommand) cmd).getPage();

                this.profiler.logEvent("PAGE", page.getId().toString(), elapsedTime, error);
                
                 if( IPortalLogger.logger.isDebugEnabled()){
                    IPortalLogger.logger.debug(new LoggerMessage("page exit " + elapsedTime));
                }

                
                if( error == false)
                    IPortalLogger.logger.info(elapsedTime);

            }
        }


       /* Navigation interne aux portlets
        *
        *  Synchronisation de la navigation du portlet (curItemPath) et de la navigation du portail (osivia.cms.contentPath)
        *
        *  Necessaire pour la compataibilité edition front-office dans des portlets externes (type faq)
        */
        /*
       if (cmd instanceof InvokePortletWindowRenderCommand) {

            // navigation intene au portlet
            // curItemPath et contextualisé > contentPath)

            InvokePortletWindowRenderCommand inv = (InvokePortletWindowRenderCommand) cmd;

            StateString navigationalState = inv.getNavigationalState();

            if (navigationalState instanceof ParametersStateString)
            {
               ParametersStateString navigationalParameters = (ParametersStateString)navigationalState;

               //
               Map<String, String[]> parameters = navigationalParameters.getParameters();

               if( parameters.get("curItemPath") != null) {

                   NavigationalStateContext nsContext = (NavigationalStateContext) cmd.getControllerContext()
                           .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

                   PageNavigationalState previousPNS = nsContext.getPageNavigationalState(((InvokePortletWindowRenderCommand) cmd).getPage().getId().toString());
                   Map<QName, String[]> state = new HashMap<QName, String[]>();

                   for( Entry<QName, String[]>  entry: previousPNS.getParameters().entrySet()) {
                       state.put(entry.getKey(), entry.getValue());

                       // Mise à jour du path de contenu
                       state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.contentPath"),
                               new String[] { parameters.get("curItemPath")[0] });

                   }

                   nsContext.setPageNavigationalState(((InvokePortletWindowRenderCommand) cmd).getPage().getId().toString(), new PageNavigationalState(state));

               }
            }
        }
        */


        // Fermeture applicative des popup : les windows n'existent plus
        // injection d'une region permettant de fermer la popup

        if (cmd instanceof RenderPageCommand) {

            PortalObjectId popupWindowId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                    "osivia.popupModeWindowID");

            if (popupWindowId != null) {

                if (resp instanceof PageRendition) {

                    PageRendition rendition = (PageRendition) resp;


                    // Fermeture applicative
                    ControllerCommand endPopupCMD = (ControllerCommand) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE,
                            "osivia.popupModeCloseCmd");

                    if( endPopupCMD instanceof InvokePortletWindowRenderCommand)    {
                        // Si je propage la commande tel quelle, les paramètres publics sont perdus ...
                        // Heureusement, Les paramètres de la commande ont déjà été appliqués...
                        endPopupCMD = new InvokePortletWindowRenderCommand(popupWindowId, Mode.VIEW,null);
                    }

                    String callbackId = popupWindowId.toString(PortalObjectPath.SAFEST_FORMAT);


                    if ("true".equals(controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.refreshClosePopupPage"))) {

                        // Remove AJAX from current request

                        PortalObjectId pageID = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                                "osivia.popupModeOriginalPageID");
                        endPopupCMD = new ViewPageCommand(pageID);
                        callbackId = null;

                        controllerContext.setAttribute(ControllerCommand.SESSION_SCOPE, "osivia.refreshClosePopupPage", null);

                    }


                    if (endPopupCMD instanceof CmsCommand) {
                        // Prevent Ajax refresh on CMS command
                        callbackId = null;
                    }


                    if (endPopupCMD == null) {
                         endPopupCMD = new InvokePortletWindowRenderCommand(popupWindowId, Mode.VIEW, null);
                    }

                    String url = new PortalURLImpl(endPopupCMD, controllerContext, null, null).toString();
                    int pageMarkerIndex = url.indexOf(PageMarkerUtils.PAGE_MARKER_PATH);
                    if (pageMarkerIndex != -1) {
                        url = url.substring(0, pageMarkerIndex) + PortalCommandFactory.POPUP_CLOSED_PATH + url.substring(pageMarkerIndex + 1);
                    }

                    StringBuffer popupContent = new StringBuffer();

                    // Inject javascript
                    popupContent.append(" <script type=\"text/javascript\">");


                    if ("1".equals(controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.exitPopupAdminMode"))) {
                        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.exitPopupAdminMode", null);
                        controllerContext.getServerInvocation().getServerContext().getClientRequest().setAttribute("osivia.popupModeClosing", "1");
                    }


                    if ("1".equals(controllerContext.getServerInvocation().getServerContext().getClientRequest().getAttribute("osivia.popupModeClosing"))) {
                        popupContent.append("  parent.setCallbackParams(  '" + callbackId + "',    '" + url + "');");
                        popupContent.append("  parent.jQuery.fancybox.close();");
                    }

                    // redirection if non a popup
                    popupContent.append("  if ( window.self == window.top )	{ ");
                    popupContent.append("  document.location = '" + url + "'");
                    popupContent.append("  } ");


                    popupContent.append(" </script>");

                    Map<String, String> windowProps = new HashMap<String, String>();
                    windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
                    windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
                    windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
                    WindowResult res = new WindowResult("", popupContent.toString(), Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
                    WindowContext bloh = new WindowContext("POPUP_HEADER", "popup_header", "0", res);
                    rendition.getPageResult().addWindowContext(bloh);

                    //
                    Region region = rendition.getPageResult().getRegion2("popup_header");
                    DynaRenderOptions.AJAX.setOptions(region.getProperties());


                    if ("1".equals(controllerContext.getServerInvocation().getServerContext().getClientRequest().getAttribute("osivia.popupModeClosing"))) {

                        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", null);
                        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeClosing", null);
                        controllerContext.getServerInvocation().getServerContext().getClientRequest().setAttribute("osivia.popupModeClosing", null);

                        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID", null);


                        controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupIgnoreNotifications", "1");

                    }
                }
            }
        }



        /** Fermeture popup suite à une action **/

        if (cmd instanceof InvokePortletWindowActionCommand ) {

            // In session because closing popup generates a redirection and attributes are lost

            if ("true".equals(controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.closePopupOnAction"))) {
                controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.closePopupOnAction", "1");

                if ("true".equals(controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.refreshPage"))) {

                    controllerContext.setAttribute(ControllerCommand.SESSION_SCOPE, "osivia.refreshClosePopupPage", "true");
                }
            }

        }

        
        /** création dynamique d'une window */

        if (cmd instanceof InvokePortletWindowActionCommand ) {
            InvokePortletWindowActionCommand invokePortletWindowActionCommand = (InvokePortletWindowActionCommand) cmd;

            if (cmd.getControllerContext().getAttribute(ControllerCommand.REQUEST_SCOPE, Constants.PORTLET_ATTR_START_WINDOW) != null) {
                // Page
                Page page = invokePortletWindowActionCommand.getPage();
                // Windows
                Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);


                StartingWindowBean startingWindow = (StartingWindowBean) cmd.getControllerContext().getAttribute(ControllerCommand.REQUEST_SCOPE, Constants.PORTLET_ATTR_START_WINDOW);
                // Window properties
                StringBuilder builder;
                String windowName= startingWindow.getName();
                
                // Page marker
                String pageMarker = (String) cmd.getControllerContext().getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");

                // Dynamic window bean
                Window curWindow = (Window) ((InvokePortletWindowCommand) cmd).getTarget();

                
                DynamicWindowBean window = new DynamicWindowBean(curWindow.getPage().getId(), windowName, startingWindow.getPortletInstance(),  startingWindow.getProperties(), pageMarker);
                
                IDynamicObjectContainer dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer");
                dynamicObjectContainer.addDynamicWindow(window);


                // Suppression du cache
                builder = new StringBuilder();
                builder.append("cached_markup.");
                builder.append(curWindow.getPage().getId().toString());
                builder.append("/");
                builder.append(windowName);
                cmd.getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, builder.toString());
                
                
                
                PortalObjectId windowId = new PortalObjectId("", new PortalObjectPath(curWindow.getPage().getId().getPath().toString().concat("/").concat(windowName),
                        PortalObjectPath.CANONICAL_FORMAT));

                // Réinitialisation de l'état
                NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, windowId);
                WindowNavigationalState windowNavState = WindowNavigationalState.create();
                Map<String, String[]> parameters = new HashMap<String, String[]>();

                WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.NORMAL, windowNavState.getMode(),
                            ParametersStateString.create(parameters));

                 cmd.getControllerContext().setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
                 
                PortalObjectId windowID  = PortalObjectId.parse(curWindow.getPage().getId().toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + windowName, PortalObjectPath.CANONICAL_FORMAT);
                cmd.getControllerContext().setAttribute(Scope.REQUEST_SCOPE, "ajax.dynamicWindowID", windowID);


                // Unset max mode
                unsetMaxMode(windows, controllerContext);
            }

        }





        if ((cmd instanceof InvokePortletWindowActionCommand) || (cmd instanceof InvokePortletWindowRenderCommand)) {
            // Current window
            Window window = (Window) ((InvokePortletWindowCommand) cmd).getTarget();

            if ("true".equals(controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.unsetMaxMode"))) {
                Collection<PortalObject> windows = new ArrayList<PortalObject>(window.getPage().getChildren(PortalObject.WINDOW_MASK));

                unsetMaxMode(windows, controllerContext);
            }


            // v2.0.22-RC6 Force to reload resources
            if ("true".equals(controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.updateContents"))) {
                controllerContext.setAttribute(ControllerCommand.SESSION_SCOPE, "osivia.updateContents", "true");
            }

            // Redirection driven at portlet level
            String url = (String) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.redirection.url");
            if( url != null) {
                return new RedirectionResponse(url);
            }


            // Display collapse window content
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, "osivia.collapse.currentWindowId", window.getId());
        }






        /*************************************************************************/
        /* Pour éviter que 2 fenetres soient en mode MAXIMIZE suite à une action */
        /*************************************************************************/


        if ((cmd instanceof InvokePortletWindowActionCommand) || (cmd instanceof InvokePortletWindowRenderCommand)) {

            Window window = (Window) ((InvokePortletWindowCommand) cmd).getTarget();

            // On regarde les changements de navigation
            NavigationalStateContext stateCtx = (NavigationalStateContext) controllerContext.getAttributeResolver(
                    ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

            if (stateCtx.getChanges() != null) {

                List<Window> windowsToUpdate = new ArrayList<Window>();

                for (Iterator<?> i = stateCtx.getChanges(); i.hasNext();) {
                    NavigationalStateChange change = (NavigationalStateChange) i.next();

                    NavigationalStateObjectChange update = (NavigationalStateObjectChange) change;

                    // Get the state key
                    NavigationalStateKey key = update.getKey();

                    // We consider only portal object types
                    Class<?> type = key.getType();
                    if (type == WindowNavigationalState.class) {
                        // Get old window state
                        WindowNavigationalState oldNS = (WindowNavigationalState) update.getOldValue();
                        WindowState oldWindowState = oldNS != null ? oldNS.getWindowState() : null;

                        // Get new window state
                        WindowNavigationalState newNS = (WindowNavigationalState) update.getNewValue();
                        WindowState newWindowState = newNS != null ? newNS.getWindowState() : null;

                        // est-ce que la fenetre est devenue MAXIMISEE
                        if (!WindowState.MAXIMIZED.equals(oldWindowState)) {

                            if (WindowState.MAXIMIZED.equals(newWindowState)) {
                                // Mettre toutes les fenetres MAXI en mode normal

                                for (PortalObject po : window.getParent().getChildren(PortalObject.WINDOW_MASK)) {

                                    if (!po.getId().equals(key.getId())) {

                                        NavigationalStateKey nsKey2 = new NavigationalStateKey(WindowNavigationalState.class, po.getId());

                                        WindowNavigationalState windowNavState2 = (WindowNavigationalState) controllerContext.getAttribute(
                                                ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey2);
                                        // On regarde si la fenêtre est en vue MAXIMIZED

                                        if ((windowNavState2 != null) && WindowState.MAXIMIZED.equals(windowNavState2.getWindowState())) {

                                            windowsToUpdate.add((Window) po);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }


                // Forcage en mode normal

                for (Window windowToUpdate : windowsToUpdate) {


                    if (windowlogger.isDebugEnabled()) {
                        windowlogger.debug("autoresize due to double MAXIMISATION " + windowToUpdate.getId());
                    }


                    NavigationalStateKey nsKey2 = new NavigationalStateKey(WindowNavigationalState.class, windowToUpdate.getId());

                    WindowNavigationalState windowNavState2 = (WindowNavigationalState) controllerContext.getAttribute(
                            ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey2);

                    // On la force en vue NORMAL
                    WindowNavigationalState newNS2 = WindowNavigationalState.bilto(windowNavState2, WindowState.NORMAL, windowNavState2.getMode(),
                            windowNavState2.getContentState());
                    controllerContext.setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey2, newNS2);

                }
            }
        }


        // Ajout PIA : permet de mettre à jour les contextes de windows

        if (cmd instanceof RenderWindowCommand) {

            RenderWindowCommand rwc = (RenderWindowCommand) cmd;

            // logger.debug("render window apres"+ rwc.getWindow().getName());

            PageProperties properties = PageProperties.getProperties();

            Window window = rwc.getWindow();
            String windowId = window.getId().toString(PortalObjectPath.SAFEST_FORMAT);

            Map<String, String> windowProperties = (Map<String, String>) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE,
                    "osivia.windowProperties." + windowId);


            // Should we hide the portlet (empty response + hideEmptyPortlet positionned)
            boolean hidePortlet = false;
            String emptyResponse = (String) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.emptyResponse." + windowId);
            if ("1".equals(emptyResponse)) {
                if ("1".equals(window.getDeclaredProperty("osivia.hideEmptyPortlet"))) {
                    hidePortlet = true;
                }
            }

            if (hidePortlet && (!"wizzard".equals(controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.windowSettingMode"))
                            || (((RenderWindowCommand) cmd).getPage() instanceof ITemplatePortalObject))) {
                properties.setWindowProperty(windowId, "osivia.hidePortlet", "1");
            }


            // Inject popup display
            if (controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode") != null) {
                PortalObjectId popupWindowId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                        "osivia.popupModeWindowID");
                if (window.getId().equals(popupWindowId)) {
                    properties.setWindowProperty(windowId, "osivia.popupDisplay", "1");
                }
            }


            PortalObjectId popupWindowId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                    "osivia.popupModeWindowID");


            if (popupWindowId == null) {
                properties.setWindowProperty(windowId, "osivia.displayTitle", "1".equals(window.getDeclaredProperty("osivia.hideTitle")) ? null : "1");
            }

            // Display collapse window content
            if (window.getId().equals(controllerContext.getAttribute(Scope.REQUEST_SCOPE, "osivia.collapse.currentWindowId"))) {
                properties.setWindowProperty(windowId, "osivia.collapse.forceDisplay", String.valueOf(true));
            }

            // Dynamic window
            if (window.getId().equals(cmd.getControllerContext().getAttribute(Scope.REQUEST_SCOPE, "ajax.dynamicWindowID")))    {
                properties.setWindowProperty(windowId, "osivia.dynamicWindow", String.valueOf(true));
            }
            

            // Mobile collapse indicator
            String mobileCollapse = window.getDeclaredProperty("osivia.mobileCollapse");
            properties.setWindowProperty(windowId, "osivia.mobileCollapse", mobileCollapse);

            // Title
            String title = window.getDeclaredProperty(InternalConstants.PROP_WINDOW_TITLE);
            properties.setWindowProperty(windowId, InternalConstants.PROP_WINDOW_TITLE, title);

            // Title metadata
            boolean contextualized = "1".equals( window.getDeclaredProperty("osivia.cms.contextualization"));
            boolean display = BooleanUtils.toBoolean(window.getDeclaredProperty(InternalConstants.PROP_WINDOW_TITLE_METADATA));
            String path = window.getDeclaredProperty(Constants.WINDOW_PROP_URI);
            if (contextualized && display && StringUtils.isNotEmpty(path)) {
                // CMS context
                CMSServiceCtx cmsContext = new CMSServiceCtx();
                cmsContext.setControllerContext(controllerContext);
                cmsContext.setServletRequest(controllerContext.getServerInvocation().getServerContext().getClientRequest());

                // Title metadata window properties
                Map<String, String> titleMetadataProperties = getCMSService().getTitleMetadataProperties(cmsContext, path);

                for (Map.Entry<String, String> entry : titleMetadataProperties.entrySet()) {
                    properties.setWindowProperty(windowId, entry.getKey(), entry.getValue());
                }
            }


            // Bootstrap panel style indicator
            String bootstrapPanelStyle = window.getDeclaredProperty("osivia.bootstrapPanelStyle");
            properties.setWindowProperty(windowId, "osivia.bootstrapPanelStyle", bootstrapPanelStyle);


            // Static styles
            if (popupWindowId == null) {
                String customStyle = window.getDeclaredProperty("osivia.style");

                if (customStyle == null) {
                    customStyle = "";
                }

                // Dynamic styles
                if( windowProperties != null)   {
                    String dynamicStyles = windowProperties.get( "osivia.dynamicCSSClasses");

                    if( dynamicStyles != null) {
                        customStyle += " " + dynamicStyles;
                    }

                    properties.setWindowProperty(windowId, "osivia.style",  customStyle);
                }
            }

            // Ajax
            String ajax = window.getDeclaredProperty("osivia.ajaxLink");
            properties.setWindowProperty(windowId, "osivia.ajaxLink", ajax);

            // Decorators
            String decorators = "1".equals(window.getDeclaredProperty("osivia.hideDecorators")) ? null : "1";
            properties.setWindowProperty(windowId, "osivia.displayDecorators", decorators);


            if (((RenderWindowCommand) cmd).getTarget() instanceof DynamicWindow) {
                DynamicWindow dynaWIndow = (DynamicWindow) ((RenderWindowCommand) cmd).getTarget();
                if (dynaWIndow.isSessionWindow()) {
                    if (!"1".equals(window.getDeclaredProperty("osivia.dynamic.unclosable"))) {
                        properties.setWindowProperty(windowId, "osivia.closeUrl", this.urlFactory.getStopPortletUrl(portalControllerContext, window.getParent()
                                .getId().toString(PortalObjectPath.SAFEST_FORMAT), windowId));
                    }
                }
            }


            // CMS path
            if (BooleanUtils.toBoolean(window.getDeclaredProperty(Constants.WINDOW_PROP_MAXIMIZED_CMS_URL))) {
                StringBuilder builder = new StringBuilder();
                builder.append(Constants.REQUEST_ATTR_URI);
                builder.append(".");
                builder.append(windowId);

                String cmsPath = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, builder.toString());
                if (cmsPath != null) {
                    String cmsUrl = this.urlFactory.getCMSUrl(portalControllerContext, null, cmsPath, null, null, null, null, null, null, null);
                    properties.setWindowProperty(windowId, Constants.WINDOW_PROP_MAXIMIZED_CMS_URL, cmsUrl);
                }
            }


            // Set current locale in window properties
            Locale locale = controllerContext.getServerInvocation().getServerContext().getClientRequest().getLocale();
            properties.setWindowProperty(windowId, InternalConstants.LOCALE_PROPERTY, locale.toString());


            /* Ajout script callback (fermeture par la croix des fancybox) */

            if ((popupWindowId != null) && (popupWindowId.equals(((RenderWindowCommand) cmd).getTargetId()))) {


                if (!"1".equals(controllerContext.getServerInvocation().getServerContext().getClientRequest().getAttribute("osivia.popupModeClosing"))) {


                    String popupId = popupWindowId.toString(PortalObjectPath.SAFEST_FORMAT);

                    // AJAX refresh ID
                    String callbackId = popupId;

                    // Window dynamique : retour
                    String url = window.getDeclaredProperty("osivia.dynamic.close_url");



                    if (url != null) {
                        // Pas d'ajax sur windows dynamique
                        callbackId = null;
                    } else {
                        InvokePortletWindowRenderCommand endPopupCMD = new InvokePortletWindowRenderCommand(popupWindowId, Mode.VIEW, WindowState.NORMAL);
                        url = new PortalURLImpl(endPopupCMD, controllerContext, null, null).toString();
                    }

                    int pageMarkerIndex = url.indexOf(PageMarkerUtils.PAGE_MARKER_PATH);
                    if (pageMarkerIndex != -1) {
                        url = url.substring(0, pageMarkerIndex) + PortalCommandFactory.POPUP_CLOSED_PATH + url.substring(pageMarkerIndex + 1);
                    }

                    /* URL de callback forcée par le portlet */

                    String callbackURL = (String) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupCallbackUrl" + popupId);
                    if (callbackURL != null) {
                        url = callbackURL;
                    }


                    StringBuffer popupContent = new StringBuffer();


                    // Inject javascript
                    popupContent.append(" <script type=\"text/javascript\">");
                    String callbackIDJS = "null";
                    if (callbackId != null) {
                        callbackIDJS = "'" + callbackId + "'";
                    }
                    popupContent.append("  parent.setCallbackParams(  " + callbackIDJS + ",    '" + url + "');");
                    popupContent.append(" </script>");

                    properties.setWindowProperty(windowId, "osivia.popupScript", popupContent.toString());
                }
            }


        }

        // Insert navigation portlet in the page
        if ((cmd instanceof RenderPageCommand) && (resp instanceof PageRendition)) {
            RenderPageCommand rpc = (RenderPageCommand) cmd;
            PageRendition rendition = (PageRendition) resp;


            // Admin headers
            if (PortalObjectUtils.isJBossPortalAdministration(rpc.getPortal())) {
                this.injectAdminHeaders(rpc, rendition);
            }


            // Inject notifications region
            NotificationsUtils.injectNotificationsRegion(portalControllerContext, rendition);

            // Inject menubar region
            MenubarUtils.injectContentNavbarActionsRegion(portalControllerContext, rendition);



            // A décommenter Juste pour inspecter les sessions dans le debugger
            /*
             * if( true) {
             * HttpServletRequest request = cmd.getControllerContext().getServerInvocation().getServerContext().getClientRequest();
             * Enumeration enumAttrs = request.getSession().getAttributeNames();
             * while( enumAttrs.hasMoreElements()){
             * String attName = (String) enumAttrs.nextElement();
             * Object attrValue = request.getSession().getAttribute(attName);
             * logger.debug(attrValue);
             * }
             * }
             */
            
            
            ViewPageCommand renderCmd = new ViewPageCommand(rpc.getPage().getId());
            PortalURL portalUrl = new PortalURLImpl(renderCmd, controllerContext, null, null);  
            Map pageProps = rendition.getPageResult().getProperties();
            pageProps.put("osivia.popStateUrl", portalUrl.toString() + "?backPageMarker=first");
            
            boolean PageMaximized = BooleanUtils.isTrue((Boolean) controllerContext.getAttribute(Scope.REQUEST_SCOPE,
                    "osivia.portal.maximized"));
            if( PageMaximized)
                pageProps.put("osivia.restoreToNormalModeUrl", portalUrl.toString() + "?unsetMaxMode=true");
            
            
        }

        //
        return resp;
    }


    /**
     * Utility method used to inject JBoss administration headers.
     *
     * @param rpc render page command
     * @param rendition page rendition
     */
    private void injectAdminHeaders(PageCommand rpc, PageRendition rendition) {
        //
        String dashboardNav = this.injectAdminDashboardNav(rpc);
        if (dashboardNav != null) {
            Map<String, String> windowProps = new HashMap<String, String>();
            windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
            WindowResult res = new WindowResult("", dashboardNav, Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
            WindowContext bluh = new WindowContext("BLUH", "dashboardnav", "0", res);
            rendition.getPageResult().addWindowContext(bluh);

            //
            Region region = rendition.getPageResult().getRegion2("dashboardnav");
            DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
        }
    }


    private String injectAdminDashboardNav(PageCommand cc) {
        ControllerContext controllerCtx = cc.getControllerContext();
        ControllerRequestDispatcher rd = controllerCtx.getRequestDispatcher(getTargetContextPath(cc), "/WEB-INF/jsp/header/header.jsp");


        //
        if (rd != null) {
            // Get user
            User user = controllerCtx.getUser();
            rd.setAttribute("org.jboss.portal.header.USER", user);

            Principal principal = controllerCtx.getServerInvocation().getServerContext().getClientRequest().getUserPrincipal();
            rd.setAttribute("org.jboss.portal.header.PRINCIPAL", principal);

            if (principal == null) {
                PortalURL portalURL;

                String configNamespace = this.config.getProperty("core.login.namespace");
                if (this.loginNamespace == null) {
                    this.loginNamespace = configNamespace;
                }

                if ((this.loginNamespace != null) && !this.loginNamespace.toLowerCase().trim().equals("default")) {
                    ViewContextCommand vcc = new ViewContextCommand(new PortalObjectId(this.loginNamespace, new PortalObjectPath()));
                    portalURL = new PortalURLImpl(vcc, controllerCtx, Boolean.TRUE, null);
                } else {
                    portalURL = new PortalURLImpl(cc, controllerCtx, Boolean.TRUE, null);
                }
                String securedLogin = this.config.getProperty("core.login.secured");
                if ((securedLogin != null) && "true".equals(securedLogin.toLowerCase())) {
                    portalURL.setSecure(Boolean.TRUE);
                }
                rd.setAttribute("org.jboss.portal.header.LOGIN_URL", portalURL);
            }

            // Link to default page of default portal
            // Cannot use defaultPortalId in 2.6.x because the default context doesn't have the view right.
            // Upgrading from 2.6.1 to 2.6.2 would break.
            ViewPageCommand vpc = new ViewPageCommand(this.portalObjectContainer.getContext().getDefaultPortal().getId());

            PortalURLImpl portalURL = new PortalURLImpl(vpc, controllerCtx, null, null);
            portalURL.setInitState(true);
            rd.setAttribute("org.jboss.portal.header.DEFAULT_PORTAL_URL", portalURL);


            //
            SignOutCommand cmd = new SignOutCommand();
            rd.setAttribute("org.jboss.portal.header.SIGN_OUT_URL", new PortalURLImpl(cmd, controllerCtx, Boolean.FALSE, null));

            //
            rd.include();
            return rd.getMarkup();
        }

        //
        return null;
    }





    public static String getTargetContextPath(PortalCommand pc) {
        // String themeId = getPortalObjectContainer().getContext().getDefaultPortal().getProperty(ThemeConstants.PORTAL_PROP_THEME);
        // TODO : NE FAIRE QU'UNE FOIS PAR REQUETE !!!
        Page page = null;

        if (pc instanceof PageCommand) {
            page = ((PageCommand) pc).getPage();
        }

        if (pc instanceof WindowCommand) {
            page = ((WindowCommand) pc).getPage();
        }


        if (page == null) {
            throw new IllegalArgumentException("target path not accessible");
        }

        // [v3.0.2] context is found in the current layout
        String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        PageService pageService = pc.getControllerContext().getController().getPageService();
        LayoutService layoutService = pageService.getLayoutService();
        PortalLayout layout = layoutService.getLayoutById(layoutId);
        return layout.getLayoutInfo().getContextPath();
    }


    /**
     * Setter for config.
     *
     * @param config the config to set
     */
    public void setConfig(ServerConfig config) {
        this.config = config;
    }

    /**
     * Setter for portalObjectContainer.
     *
     * @param portalObjectContainer the portalObjectContainer to set
     */
    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

    /**
     * Setter for urlFactory.
     *
     * @param urlFactory the urlFactory to set
     */
    public void setUrlFactory(IPortalUrlFactory urlFactory) {
        this.urlFactory = urlFactory;
    }

    /**
     * Setter for servicesCacheService.
     *
     * @param servicesCacheService the servicesCacheService to set
     */
    public void setServicesCacheService(ICacheService servicesCacheService) {
        this.servicesCacheService = servicesCacheService;
    }

    /**
     * Setter for profiler.
     *
     * @param profiler the profiler to set
     */
    public void setProfiler(IProfilerService profiler) {
        this.profiler = profiler;
    }

    /**
     * Setter for taskbarService.
     * 
     * @param taskbarService the taskbarService to set
     */
    public void setTaskbarService(ITaskbarService taskbarService) {
        this.taskbarService = taskbarService;
    }
    
    /**
     * Setter for taskbarService.
     * 
     * @param statusService the statusService to set
     */
    public void setStatusService(IStatusService statusService) {
        this.statusService = statusService;
    }


}
