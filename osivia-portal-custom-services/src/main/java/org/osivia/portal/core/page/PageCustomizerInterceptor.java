package org.osivia.portal.core.page;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.Controller;
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
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.server.config.ServerConfig;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.charte.Breadcrumb;
import org.osivia.portal.api.charte.BreadcrumbItem;
import org.osivia.portal.api.charte.UserPage;
import org.osivia.portal.api.charte.UserPortal;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.path.PortletPathItem;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.pagemarker.PortalCommandFactory;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.DynamicWindow;
import org.osivia.portal.core.profils.IProfilManager;


public class PageCustomizerInterceptor extends ControllerInterceptor {

    protected static final Log windowlogger = LogFactory.getLog("PORTAL_WINDOW");

    /** . */
    protected static final Log logger = LogFactory.getLog(PageCustomizerInterceptor.class);

    /** . */
    private static final PortalObjectId defaultPortalId = PortalObjectId.parse("/", PortalObjectPath.CANONICAL_FORMAT);

    /** . */
    private static PortalObjectId adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);

    /** . */
    private String toolbarPath;


    private String footerPath;


    private String breadcrumbPath;

    private String searchPath;


    /** . */
    private String loginNamespace;

    /** . */
    private ServerConfig config;

    /** . */
    private PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;

    /** . */
    private PortalObjectContainer portalObjectContainer;

    protected IPortalUrlFactory urlFactory;

    private ICacheService cacheService;

    private org.osivia.portal.api.cache.services.ICacheService servicesCacheService;

    private transient IProfilerService profiler;

    private IProfilManager profilManager;

    public static boolean isImportRunning = false;


    public IProfilerService getProfiler() {
        return this.profiler;
    }

    public void setProfiler(IProfilerService profiler) {
        this.profiler = profiler;
    }


    public org.osivia.portal.api.cache.services.ICacheService getServicesCacheService() {
        return this.servicesCacheService;
    }

    public void setServicesCacheService(org.osivia.portal.api.cache.services.ICacheService cacheService) {
        this.servicesCacheService = cacheService;
    }

    public ICacheService getCacheService() {
        return this.cacheService;
    }

    public void setCacheService(ICacheService cacheService) {
        this.cacheService = cacheService;
    }

    public IPortalUrlFactory getUrlFactory() {
        return this.urlFactory;
    }

    public void setUrlFactory(IPortalUrlFactory urlFactory) {
        this.urlFactory = urlFactory;
    }

    public PortalAuthorizationManagerFactory getPortalAuthorizationManagerFactory() {
        return this.portalAuthorizationManagerFactory;
    }

    public void setPortalAuthorizationManagerFactory(PortalAuthorizationManagerFactory portalAuthorizationManagerFactory) {
        this.portalAuthorizationManagerFactory = portalAuthorizationManagerFactory;
    }

    public PortalObjectContainer getPortalObjectContainer() {
        return this.portalObjectContainer;
    }

    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

    public IProfilManager getProfilManager() {
        return this.profilManager;
    }

    public void setProfilManager(IProfilManager profilManager) {
        this.profilManager = profilManager;
    }


    public static boolean isAdministrator(ControllerContext controllerCtx) {

        // On teste si l'utilisateur est un administrateur
        // (ie. autorisé à voir le portal d'administration)

        Boolean isAdmin = (Boolean) controllerCtx.getAttribute(Scope.PRINCIPAL_SCOPE, "osivia.isAdmin");
        if (isAdmin == null) {
            PortalObjectPermission perm = new PortalObjectPermission(adminPortalId, PortalObjectPermission.VIEW_MASK);
            if (controllerCtx.getController().getPortalAuthorizationManagerFactory().getManager().checkPermission(perm)) {
                isAdmin = new Boolean(true);
            } else {
                isAdmin = new Boolean(false);
            }
            controllerCtx.setAttribute(Scope.PRINCIPAL_SCOPE, "osivia.isAdmin", isAdmin);
        }
        return isAdmin;
    }


    public static boolean initShowMenuBarItem(ControllerContext controllerCtx, Portal portal) {

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


    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

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

        nsContext.setPageNavigationalState(pageId, new PageNavigationalState(state));

        /* Init window states */

        unsetMaxMode(page.getChildren(PortalObject.WINDOW_MASK), controllerCtx);
    }


    public static void unsetMaxMode(Collection windows, ControllerContext controllerCtx) {

        // Maj du breadcrumb
        controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb", null);

        // Reinitialtion du path CMS


        Iterator i = windows.iterator();

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


                // On force le rafrachissement de la page (requete Ajax)
                controllerCtx.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.refreshPage", "true");

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
    public static boolean controlDefaultPageCache(PortalObjectContainer portalObjectContainer, ControllerCommand cmd, ControllerContext controllerCtx) {

        boolean isDefaultPageCache = false;

        if (cmd instanceof RenderPageCommand) {

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
        return (isDefaultPageCache);
    }


    @Override
    public ControllerResponse invoke(ControllerCommand cmd) throws Exception {

        // v1.0.16 : lock during import
        if (isImportRunning == true) {
            while (isImportRunning == true) {
                Thread.sleep(1000L);
            }
        }


        long begin = 0;
        boolean error = false;


        if (logger.isDebugEnabled()) {
            logger.debug("PageCustomizerInterceptor test2 commande " + cmd.getClass().getName());
        }

        if (cmd instanceof RenderPageCommand) {
            begin = System.currentTimeMillis();
        }


        if (cmd instanceof RenderPageCommand) {

            // v1.0.10 : réinitialisation des propriétes des windows
            // PageProperties.getProperties().init();


            /* Controle du host */

            Portal portal = ((RenderPageCommand) cmd).getPortal();
            String host = portal.getDeclaredProperty("osivia.site.hostName");
            String reqHost = cmd.getControllerContext().getServerInvocation().getServerContext().getClientRequest().getServerName();

            if ((host != null) && !reqHost.equals(host)) {
                ViewPageCommand viewCmd = new ViewPageCommand(((RenderPageCommand) cmd).getPage().getId());
                String url = new PortalURLImpl(viewCmd, cmd.getControllerContext(), null, null).toString();
                url = url.replaceFirst(reqHost, host);
                url += "?init-state=true";
                return new RedirectionResponse(url.toString());
            }


            RenderPageCommand rpc = (RenderPageCommand) cmd;
            ControllerContext controllerCtx = cmd.getControllerContext();
            HttpServletRequest request = controllerCtx.getServerInvocation().getServerContext().getClientRequest();

            controlDefaultPageCache(this.portalObjectContainer, cmd, controllerCtx);


            if (rpc.getPage().getName().startsWith("exception")) {
                throw new RuntimeException("erreur de test");
            }


            /*
             * Affichage en mode CMS des templates
             */

            // Accès depuis le menu ou initialisation
            // On redirige en mode CMS

            // La page d'accueil (url /portal doit également réinitialiser la page (init-state)
            // pour initialiser le CMS

            boolean defaultPage = false;

            String portalPath = controllerCtx.getServerInvocation().getServerContext().getPortalRequestPath();
            String pageMarker = (String) controllerCtx.getAttribute(Scope.REQUEST_SCOPE, "currentPageMarker");

            /* On teste les 2 cas spécifiques ou le init-state n'est pas initialisé dans l'url */

            // Appel de la page par défaut (/portal)
            if (portalPath.equals("") || portalPath.equals("/")) {
                defaultPage = true;
                ;

            }

            // Déconnexion : page par défaut + pageMarker = 1
            // TODO : test a améliorer ...
            if (rpc.getPage().equals(rpc.getPortal().getDefaultPage()) && ("1".equals(pageMarker))) {
                defaultPage = true;
                ;
            }


            if ("true".equals(request.getParameter("init-state")) || defaultPage) {

                CMSItem pagePublishSpaceConfig = CmsCommand.getPagePublishSpaceConfig(cmd.getControllerContext(), rpc.getPage());


                if (((pagePublishSpaceConfig != null) && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeInternalContents")))

                || ("1".equals(rpc.getPage().getDeclaredProperty("osivia.cms.directContentPublisher")))) {

                    if (!"true".equals(request.getParameter("edit-template-mode"))) {
                        // Redirection en mode CMS

                        String url = this.getUrlFactory().getCMSUrl(new PortalControllerContext(controllerCtx),
                                rpc.getPage().getId().toString(PortalObjectPath.CANONICAL_FORMAT), rpc.getPage().getDeclaredProperty("osivia.cms.basePath"),
                                null, IPortalUrlFactory.CONTEXTUALIZATION_PAGE, null, null, null, null, null);

                        if (request.getParameter("firstTab") != null) {
                            url += "&firstTab=" + request.getParameter("firstTab");
                        }
                        return new RedirectionResponse(url.toString());
                    }
                }
            }


            if ("true".equals(request.getParameter("unsetMaxMode"))) {
                unsetMaxMode(rpc.getPage().getChildren(PortalObject.WINDOW_MASK), controllerCtx);
            }


            if ("true".equals(request.getParameter("init-state"))) {

                // logger.debug("init page");
                // Récupération des fenêtres de la page
                initPageState(rpc.getPage(), controllerCtx);
            }

            if (request.getParameter("firstTab") != null) {
                controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_FIRST_TAB, new Integer(request.getParameter("firstTab")));
            }

            if ("true".equals(request.getParameter("init-cache"))) {
                if ("wizzard".equals(controllerCtx.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.windowSettingMode"))) {
                    this.getServicesCacheService().initCache();
                }
            }


            controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_PAGE_ID, rpc.getPage().getId());


            // Force la valorisation dans le contexte
            isAdministrator(controllerCtx);

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
            cmd.getControllerContext().setAttribute(Scope.REQUEST_SCOPE, "cmsCommand", "1");
        }


        if (cmd instanceof RenderPageCommand) {

            RenderPageCommand rpc = (RenderPageCommand) cmd;
            ControllerContext controllerCtx = cmd.getControllerContext();
            NavigationalStateContext nsContext = (NavigationalStateContext) controllerCtx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

            Page page = rpc.getPage();

            // contexte de navigation CMS ?

            PageNavigationalState pageState = nsContext.getPageNavigationalState(rpc.getPage().getId().toString());

            String sPath[] = null;
            if (pageState != null) {
                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            }

            String pathPublication = null;
            if ((sPath != null) && (sPath.length > 0)) {
                pathPublication = sPath[0];
            }

            if (pathPublication != null) {

                // On est déja dans une cmscommand, auquel cas l'affichage est bon

                if (!"1".equals(cmd.getControllerContext().getAttribute(Scope.REQUEST_SCOPE, "cmsCommand"))) {

                    String navigationScope = page.getProperty("osivia.cms.navigationScope");
                    String basePath = page.getProperty("osivia.cms.basePath");

                    CMSServiceCtx cmxCtx = new CMSServiceCtx();
                    cmxCtx.setControllerContext(controllerCtx);
                    cmxCtx.setScope(navigationScope);

                    // test si mode assistant activé
                    if (InternalConstants.CMS_VERSION_PREVIEW.equals(controllerCtx.getAttribute(ControllerCommand.SESSION_SCOPE,
                            InternalConstants.ATTR_TOOLBAR_CMS_VERSION))) {
                        cmxCtx.setDisplayLiveVersion("1");
                    }

                    CMSItem navItem = getCMSService().getPortalNavigationItem(cmxCtx, basePath, pathPublication);

                    // Affichage en mode page ?

                    if (!basePath.equals(pathPublication) && !"1".equals(navItem.getProperties().get("pageDisplayMode"))) {

                        CMSItem pagePublishSpaceConfig = CmsCommand.getPagePublishSpaceConfig(cmd.getControllerContext(), rpc.getPage());

                        if ((pagePublishSpaceConfig != null) && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeInternalContents"))) {

                            // On regarde l'état des fenetres

                            Iterator i = ((RenderPageCommand) cmd).getPage().getChildren(PortalObject.WINDOW_MASK).iterator();

                            boolean normalState = true;
                            while (i.hasNext()) {

                                Window window = (Window) i.next();

                                NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

                                WindowNavigationalState windowNavState = (WindowNavigationalState) cmd.getControllerContext().getAttribute(
                                        ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);
                                // On regarde si la fenêtre est en vue MAXIMIZED
                                if (WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {
                                    normalState = false;
                                }
                            }

                            if (normalState) {
                                // Redirection en mode CMS
                                HttpServletRequest request = controllerCtx.getServerInvocation().getServerContext().getClientRequest();

                                String url = this.getUrlFactory().getCMSUrl(new PortalControllerContext(controllerCtx),
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


        if ((cmd instanceof RenderPageCommand)
                || ((cmd instanceof RenderWindowCommand) && (ControllerContext.AJAX_TYPE == cmd.getControllerContext().getType()))) {
            initShowMenuBarItem(cmd.getControllerContext(), ((PortalCommand) cmd).getPortal());
            cmd.getControllerContext().setAttribute(Scope.REQUEST_SCOPE, "osivia.currentPortalName", ((PortalCommand) cmd).getPortal().getName());
            cmd.getControllerContext().setAttribute(Scope.REQUEST_SCOPE, "osivia.themePath", getTargetContextPath((PortalCommand) cmd));


            /* Inject cms Path */

            ControllerContext controllerCtx = cmd.getControllerContext();
            NavigationalStateContext nsContext = (NavigationalStateContext) controllerCtx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

            Page page = null;
            if (cmd instanceof RenderPageCommand) {
                page = ((RenderPageCommand) cmd).getPage();
            } else {
                page = ((RenderWindowCommand) cmd).getPage();
            }


            PageNavigationalState pageState = nsContext.getPageNavigationalState(page.getId().toString());

            String sPath[] = null;
            if (pageState != null) {
                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            }

            if ((sPath != null) && (sPath.length > 0)) {
                cmd.getControllerContext().setAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path", sPath[0]);
            }


        }


        // v2.1 Entering and exiting the admin popup mode
        if (cmd instanceof InvokePortletWindowRenderCommand) {
            if ("admin".equals(cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode"))) {
                if (!Mode.ADMIN.equals(((InvokePortletWindowRenderCommand) cmd).getMode())) {
                    // Exiting admin mode
                    cmd.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeClosing", "1");
                }
            } else {
                if (Mode.ADMIN.equals(((InvokePortletWindowRenderCommand) cmd).getMode())) {
                    // Entering admin mode
                    cmd.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", "admin");
                    cmd.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID",
                            ((InvokePortletWindowRenderCommand) cmd).getTargetId());
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

            }
        }


        // Fermeture applicative des popup : les windows n'existent plus
        // injection d'une region permettant de fermer la popup

        if (cmd instanceof RenderPageCommand) {

            PortalObjectId popupWindowId = (PortalObjectId) cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                    "osivia.popupModeWindowID");

            if (popupWindowId != null) {

                if (resp instanceof PageRendition) {

                    PageRendition rendition = (PageRendition) resp;


                    ControllerCommand endPopupCMD = (ControllerCommand) cmd.getControllerContext().getAttribute(ControllerCommand.REQUEST_SCOPE,
                            "osivia.popupModeCloseCmd");
                    if (endPopupCMD == null) {
                        endPopupCMD = new InvokePortletWindowRenderCommand(popupWindowId, Mode.VIEW, WindowState.NORMAL);
                    }

                    String url = new PortalURLImpl(endPopupCMD, cmd.getControllerContext(), null, null).toString();
                    int pageMarkerIndex = url.indexOf(PageMarkerUtils.PAGE_MARKER_PATH);
                    if (pageMarkerIndex != -1) {
                        url = url.substring(0, pageMarkerIndex) + PortalCommandFactory.POPUP_CLOSED_PATH + url.substring(pageMarkerIndex + 1);
                    }

                    StringBuffer popupContent = new StringBuffer();

                    // Inject javascript
                    popupContent.append(" <script type=\"text/javascript\">");

                    if ("1".equals(cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeClosing"))) {
                        String callbackId = popupWindowId.toString(PortalObjectPath.SAFEST_FORMAT);
                        popupContent.append("  parent.setCallbackParams(  '" + callbackId + "',    '" + url + "');");
                        popupContent.append("  parent.jQuery.fancybox.close();");
                    }

                    // redirection if non a popup
                    popupContent.append("  if ( window.self == window.top )	{ ");
                    popupContent.append("  document.location = '" + url + "'");
                    popupContent.append("  } ");


                    popupContent.append(" </script>");

                    Map windowProps = new HashMap();
                    windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
                    windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
                    windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
                    WindowResult res = new WindowResult("", popupContent.toString(), Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
                    WindowContext bloh = new WindowContext("POPUP_HEADER", "popup_header", "0", res);
                    rendition.getPageResult().addWindowContext(bloh);

                    //
                    Region region = rendition.getPageResult().getRegion2("popup_header");
                    DynaRenderOptions.AJAX.setOptions(region.getProperties());


                    if ("1".equals(cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeClosing"))) {
                        cmd.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", null);
                        cmd.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeClosing", null);
                        cmd.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID", null);

                    }
                }
            }
        }


        if ((cmd instanceof InvokePortletWindowActionCommand) || (cmd instanceof InvokePortletWindowRenderCommand)) {

            ControllerContext controllerCtx = cmd.getControllerContext();

            if ("true".equals(controllerCtx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.unsetMaxMode"))) {

                Window window = (Window) ((InvokePortletWindowCommand) cmd).getTarget();

                Collection windows = new ArrayList<PortalObject>(window.getPage().getChildren(PortalObject.WINDOW_MASK));

                unsetMaxMode(windows, controllerCtx);
            }


        }


        /*************************************************************************/
        /* Pour éviter que 2 fenetres soient en mode MAXIMIZE suite à une action */
        /*************************************************************************/


        if ((cmd instanceof InvokePortletWindowActionCommand) || (cmd instanceof InvokePortletWindowRenderCommand)) {

            Window window = (Window) ((InvokePortletWindowCommand) cmd).getTarget();

            // On regarde les changements de navigation
            NavigationalStateContext stateCtx = (NavigationalStateContext) cmd.getControllerContext().getAttributeResolver(
                    ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

            if (stateCtx.getChanges() != null) {

                List<Window> windowsToUpdate = new ArrayList<Window>();

                for (Iterator i = stateCtx.getChanges(); i.hasNext();) {
                    NavigationalStateChange change = (NavigationalStateChange) i.next();

                    NavigationalStateObjectChange update = (NavigationalStateObjectChange) change;

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

                        // est-ce que la fenetre est devenue MAXIMISEE
                        if (!WindowState.MAXIMIZED.equals(oldWindowState)) {

                            if (WindowState.MAXIMIZED.equals(newWindowState)) {
                                // Mettre toutes les fenetres MAXI en mode normal

                                for (PortalObject po : window.getParent().getChildren(PortalObject.WINDOW_MASK)) {

                                    if (!po.getId().equals(key.getId())) {

                                        NavigationalStateKey nsKey2 = new NavigationalStateKey(WindowNavigationalState.class, po.getId());

                                        WindowNavigationalState windowNavState2 = (WindowNavigationalState) cmd.getControllerContext().getAttribute(
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

                    WindowNavigationalState windowNavState2 = (WindowNavigationalState) cmd.getControllerContext().getAttribute(
                            ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey2);

                    // On la force en vue NORMAL
                    WindowNavigationalState newNS2 = WindowNavigationalState.bilto(windowNavState2, WindowState.NORMAL, windowNavState2.getMode(),
                            windowNavState2.getContentState());
                    cmd.getControllerContext().setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey2, newNS2);

                }
            }
        }


        // Ajout PIA : permet de mettre à jour les contextes de windows

        if (cmd instanceof RenderWindowCommand) {

            RenderWindowCommand rwc = (RenderWindowCommand) cmd;

            ControllerContext controllerCtx = cmd.getControllerContext();

            // logger.debug("render window apres"+ rwc.getWindow().getName());

            PageProperties properties = PageProperties.getProperties();

            String windowId = rwc.getWindow().getId().toString(PortalObjectPath.SAFEST_FORMAT);

            Map<String, String> windowProperties = (Map<String, String>) controllerCtx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.windowProperties."
                    + windowId);


            // Should we hide the portlet (empty response + hideEmptyPortlet positionned)
            String emptyResponse = (String) controllerCtx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.emptyResponse." + windowId);
            if ("1".equals(emptyResponse)) {

                if ("1".equals(rwc.getWindow().getDeclaredProperty("osivia.hideEmptyPortlet"))) {

                    // En mode normal (non édition de la page)
                    if (!"wizzard".equals(controllerCtx.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.windowSettingMode"))
                            || (((RenderWindowCommand) cmd).getPage() instanceof ITemplatePortalObject)) {
                        properties.setWindowProperty(windowId, "osivia.hidePortlet", "1");
                    }
                }

            }


            // Inject popup display
            if (cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode") != null) {
                PortalObjectId popupWindowId = (PortalObjectId) cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                        "osivia.popupModeWindowID");
                if (rwc.getWindow().getId().equals(popupWindowId)) {
                    properties.setWindowProperty(windowId, "osivia.popupDisplay", "1");
                }
            }


            PortalObjectId popupWindowId = (PortalObjectId) cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                    "osivia.popupModeWindowID");


            if (popupWindowId == null) {
                properties.setWindowProperty(windowId, "osivia.displayTitle", "1".equals(rwc.getWindow().getDeclaredProperty("osivia.hideTitle")) ? null : "1");
            }

            String title = rwc.getWindow().getDeclaredProperty("osivia.title");

            properties.setWindowProperty(windowId, "osivia.title", title);


            // Static styles

            if (popupWindowId == null) {
                String customStyle = rwc.getWindow().getDeclaredProperty("osivia.style");

                if (customStyle == null) {
                    customStyle = "";
                }

                // Dynamic styles

                String dynamicStyles = windowProperties.get("osivia.dynamicCSSClasses");

                if (dynamicStyles != null) {
                    customStyle += " " + dynamicStyles;
                }

                properties.setWindowProperty(windowId, "osivia.style", customStyle);
            }


            properties.setWindowProperty(windowId, "osivia.ajaxLink", rwc.getWindow().getDeclaredProperty("osivia.ajaxLink"));

            properties.setWindowProperty(windowId, "osivia.displayDecorators", "1".equals(rwc.getWindow().getDeclaredProperty("osivia.hideDecorators")) ? null
                    : "1");


            if (((RenderWindowCommand) cmd).getTarget() instanceof DynamicWindow) {

                DynamicWindow dynaWIndow = (DynamicWindow) ((RenderWindowCommand) cmd).getTarget();
                if (dynaWIndow.isSessionWindow()) {
                    if (!"1".equals(rwc.getWindow().getDeclaredProperty("osivia.dynamic.unclosable"))) {
                        properties.setWindowProperty(
                                windowId,
                                "osivia.closeUrl",
                                this.getUrlFactory().getStopPortletUrl(new PortalControllerContext(controllerCtx),
                                        rwc.getWindow().getParent().getId().toString(PortalObjectPath.SAFEST_FORMAT), windowId));
                    }
                }

            }


            /* Ajout script callback (fermeture par la croix des fancybox) */

            if ((popupWindowId != null) && (popupWindowId.equals(((RenderWindowCommand) cmd).getTargetId()))) {

                if (!"1".equals(cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeClosing"))) {


                    String popupId = popupWindowId.toString(PortalObjectPath.SAFEST_FORMAT);

                    // AJAX refresh ID
                    String callbackId = popupId;

                    // Window dynamique : retour
                    String url = rwc.getWindow().getDeclaredProperty("osivia.dynamic.close_url");

                    if (url != null) {
                        // Pas d'ajax sur windows dynamique
                        callbackId = null;
                    } else {
                        InvokePortletWindowRenderCommand endPopupCMD = new InvokePortletWindowRenderCommand(popupWindowId, Mode.VIEW, WindowState.NORMAL);
                        url = new PortalURLImpl(endPopupCMD, cmd.getControllerContext(), null, null).toString();
                    }

                    int pageMarkerIndex = url.indexOf(PageMarkerUtils.PAGE_MARKER_PATH);
                    if (pageMarkerIndex != -1) {
                        url = url.substring(0, pageMarkerIndex) + PortalCommandFactory.POPUP_CLOSED_PATH + url.substring(pageMarkerIndex + 1);
                    }

                    /* URL de callback forcée par le portlet */

                    String callbackURL = (String) cmd.getControllerContext().getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupCallbackUrl" + popupId);
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
        if (resp instanceof PageRendition) {

            if (cmd instanceof RenderPageCommand) {
                RenderPageCommand rpc = (RenderPageCommand) cmd;

                PageRendition rendition = (PageRendition) resp;

                boolean admin = false;
                if (cmd instanceof RenderPageCommand) {

                    PortalObject portalObject = rpc.getPage().getPortal();
                    admin = "admin".equalsIgnoreCase(portalObject.getName());
                }

                //

                if (admin) {
                    this.injectAdminHeaders(rpc, rendition);
                } else {
                    PortalObjectId popupWindowId = (PortalObjectId) cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID");

                    if( popupWindowId == null) {
                        this.injectStandardHeaders(rpc,  rendition);
                    }
                }

            }

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

        }


        //
        return resp;
    }


    private void injectStandardHeaders(PageCommand rpc, PageRendition rendition) throws Exception {
        // Breadcrumb
        String breadcrumb = this.injectBreadcrumb(rpc, rendition);
        if (breadcrumb != null) {
            Map windowProps = new HashMap();
            windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
            WindowResult res = new WindowResult("", breadcrumb, Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
            WindowContext bloh = new WindowContext("BLEH", "breadcrumb", "0", res);
            rendition.getPageResult().addWindowContext(bloh);

            //
            Region region = rendition.getPageResult().getRegion2("breadcrumb");
            DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
        }

        // Search
        String search = this.injectSearch(rpc);
        if (search != null) {
            Map windowProps = new HashMap();
            windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
            WindowResult res = new WindowResult("", search, Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
            WindowContext bluh = new WindowContext("BLOH", "search", "0", res);
            rendition.getPageResult().addWindowContext(bluh);

            //
            Region region = rendition.getPageResult().getRegion2("search");
            DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
        }

        // Footer
        String footerNav = this.injectFooter(rpc);
        if (footerNav != null) {
            Map windowProps = new HashMap();
            windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");


            /* JSS20130610 : Injection path CMS pour Ajax */

            // On determine le path de navigation cms
            // TODO : ajouter test uniquement en edition CMS


            NavigationalStateContext nsContext = (NavigationalStateContext) rpc.getControllerContext().getAttributeResolver(
                    ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
            PageNavigationalState pageState = nsContext.getPageNavigationalState(rpc.getPage().getId().toString());

            String sPath[] = null;
            if (pageState != null) {
                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            }

            StringBuffer footer = new StringBuffer();

            footer.append(footerNav);

            if ((sPath != null) && (sPath.length == 1)) {

                footer.append("<script type='text/javascript'>\n");
                footer.append("cmsPath = \"");
                footer.append(sPath[0]);
                footer.append("\";\n");

                footer.append("</script>\n");
            }


            WindowResult res = new WindowResult("", footer.toString(), Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
            WindowContext bloh = new WindowContext("BLUH", "footer", "0", res);
            rendition.getPageResult().addWindowContext(bloh);

            //
            Region region = rendition.getPageResult().getRegion2("footer");
            DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
        }

        // Page settings
        StringBuffer pageSettings = new StringBuffer();

        String assistantPageCustomizer = (String) rpc.getControllerContext().getAttribute(ControllerCommand.REQUEST_SCOPE,
                InternalConstants.ATTR_WINDOWS_SETTINGS_CONTENT);
        if (assistantPageCustomizer != null) {
            pageSettings.append(assistantPageCustomizer);
        }

        String toolbarSettings = (String) rpc.getControllerContext().getAttribute(ControllerCommand.REQUEST_SCOPE,
                InternalConstants.ATTR_TOOLBAR_SETTINGS_CONTENT);
        if (toolbarSettings != null) {
            pageSettings.append(toolbarSettings);
        }


        Map<String, String> windowProps = new HashMap<String, String>();

        windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
        windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
        windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
        WindowResult result = new WindowResult("Page settings", pageSettings.toString(), Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL,
                Mode.VIEW);
        WindowContext settings = new WindowContext("PageSettings", "pageSettings", "0", result);
        rendition.getPageResult().addWindowContext(settings);

        Region region = rendition.getPageResult().getRegion2("pageSettings");
        DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
    }


    void injectAdminHeaders(PageCommand rpc, PageRendition rendition) {


        //
        String dashboardNav = this.injectAdminDashboardNav(rpc);
        if (dashboardNav != null) {
            Map windowProps = new HashMap();
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


    public void addSubpagesToSiteMap(CMSServiceCtx cmsCtx, IPortalUrlFactory urlFactory, PortalControllerContext portalCtx, Page page, String basePath,
            CMSItem navItem, List<UserPage> pageList) {

        try {
            // CMSItem cmsItem = getCMSService().getContent(cmsCtx, path);

            UserPage userPage = new UserPage();
            pageList.add(userPage);
            userPage.setName(navItem.getProperties().get("displayName"));
            userPage.setId(navItem.getPath());
            Map<String, String> pageParams = new HashMap<String, String>();
            String url = urlFactory.getCMSUrl(portalCtx, page.getId().toString(PortalObjectPath.CANONICAL_FORMAT), navItem.getPath(), pageParams, null, null,
                    null, null, null, null);
            userPage.setUrl(url);

            List<CMSItem> navItems = getCMSService().getPortalNavigationSubitems(cmsCtx, basePath, navItem.getPath());

            List<UserPage> subPages = new ArrayList<UserPage>(10);
            userPage.setChildren(subPages);


            if (navItems.size() > 0) {

                for (CMSItem navSubItem : navItems) {
                    if ("1".equals(navItem.getProperties().get("menuItem"))) {
                        this.addSubpagesToSiteMap(cmsCtx, urlFactory, portalCtx, page, basePath, navSubItem, subPages);
                    }
                }


            }
        } catch (Exception e) {
            // May be a security issue, don't block footer
            logger.error(e.getMessage());
        }
    }


    public UserPortal getPageSiteMap(PageCommand cc) {
        ControllerContext controllerCtx = cc.getControllerContext();

        PortalAuthorizationManager pam = this.portalAuthorizationManagerFactory.getManager();
        Portal portal = cc.getPortal();
        PortalControllerContext portalCtx = new PortalControllerContext(controllerCtx);

        Locale locale = Locale.FRENCH;

        CMSServiceCtx cmxCtx = new CMSServiceCtx();
        cmxCtx.setControllerContext(controllerCtx);
        cmxCtx.setScope("anonymous");

        UserPortal siteMap = new UserPortal();
        siteMap.setName(portal.getName());
        List<UserPage> mainPages = new ArrayList<UserPage>(10);
        siteMap.setUserPages(mainPages);

        SortedSet<Page> sortedPages = new TreeSet<Page>(PageUtils.orderComparator);
        for (PortalObject po : portal.getChildren(PortalObject.PAGE_MASK)) {
            sortedPages.add((Page) po);
        }

        // Add anonymous portal pages

        for (Page page : sortedPages) {
            PortalObjectPermission perm = new PortalObjectPermission(page.getId(), PortalObjectPermission.VIEW_MASK);
            if (pam.checkPermission(null, perm)) {


                String navigationMode = page.getDeclaredProperty("osivia.navigationMode");

                if ("cms".equals(navigationMode)) {

                    // CMS sub pages

                    // v2.0-rc7

                    // if (("1".equals(page.getDeclaredProperty("osivia.cms.pageContextualizationSupport")) && (page
                    // .getDeclaredProperty("osivia.cms.basePath") != null))) {

                    try {


                        CMSItem pagePublishSpaceConfig = CmsCommand.getPagePublishSpaceConfig(controllerCtx, page);


                        if ((pagePublishSpaceConfig != null) && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeInternalContents"))) {

                            List<CMSItem> navItems = getCMSService().getPortalNavigationSubitems(cmxCtx, page.getDeclaredProperty("osivia.cms.basePath"),
                                    page.getDeclaredProperty("osivia.cms.basePath"));


                            if (navItems != null) {

                                for (CMSItem navItem : navItems) {
                                    if ("1".equals(navItem.getProperties().get("menuItem"))) {
                                        this.addSubpagesToSiteMap(cmxCtx, this.urlFactory, portalCtx, page, page.getDeclaredProperty("osivia.cms.basePath"),
                                                navItem, mainPages);
                                    }
                                }
                            } else {
                                logger.error("getPageSiteMap le path " + page.getDeclaredProperty("osivia.cms.basePath") + " n'est pas accessible");
                            }
                        }


                    } catch (Exception e) {
                        // May be a security issue, don't block footer
                        logger.error(e.getMessage());

                    }


                } else {


                    UserPage userPage = new UserPage();
                    mainPages.add(userPage);


                    ViewPageCommand showSubPage = new ViewPageCommand(page.getId());

                    userPage.setId(page.getId());
                    String subName = page.getDisplayName().getString(locale, true);
                    if (subName == null) {
                        subName = page.getName();
                    }
                    userPage.setName(subName);

                    List<UserPage> childrens = new ArrayList<UserPage>(10);
                    userPage.setChildren(childrens);


                    String url = new PortalURLImpl(showSubPage, controllerCtx, null, null).toString();
                    userPage.setUrl(url + "?init-state=true");


                }
                // }
            }
        }

        return siteMap;
    }


    public UserPortal getCMSSiteMap(PageCommand cc) throws Exception {
        ControllerContext controllerCtx = cc.getControllerContext();

        PortalAuthorizationManager pam = this.portalAuthorizationManagerFactory.getManager();
        Portal portal = cc.getPortal();
        PortalControllerContext portalCtx = new PortalControllerContext(controllerCtx);


        Page cmsPage = cc.getPortal().getDefaultPage();
        String navigationScope = cmsPage.getProperty("osivia.cms.navigationScope");

        Locale locale = Locale.FRENCH;

        CMSServiceCtx cmxCtx = new CMSServiceCtx();
        cmxCtx.setControllerContext(controllerCtx);
        cmxCtx.setScope(navigationScope);


        UserPortal siteMap = new UserPortal();
        siteMap.setName(portal.getName());
        List<UserPage> mainPages = new ArrayList<UserPage>(10);
        siteMap.setUserPages(mainPages);


        // CMS sub pages

        // v2.0-rc7
        // if (("1".equals(cmsPage.getDeclaredProperty("osivia.cms.pageContextualizationSupport")) && (cmsPage
        // .getDeclaredProperty("osivia.cms.basePath") != null))) {

        try {
            CMSItem pagePublishSpaceConfig = CmsCommand.getPagePublishSpaceConfig(controllerCtx, cmsPage);

            if ((pagePublishSpaceConfig != null) && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeInternalContents"))) {


                List<CMSItem> navItems = getCMSService().getPortalNavigationSubitems(cmxCtx, cmsPage.getDeclaredProperty("osivia.cms.basePath"),
                        cmsPage.getDeclaredProperty("osivia.cms.basePath"));


                for (CMSItem navItem : navItems) {
                    if ("1".equals(navItem.getProperties().get("menuItem"))) {
                        this.addSubpagesToSiteMap(cmxCtx, this.urlFactory, portalCtx, cmsPage, cmsPage.getDeclaredProperty("osivia.cms.basePath"), navItem,
                                mainPages);
                    }
                }
            }


        } catch (Exception e) {
            // May be a security issue, don't block footer
            logger.error(e.getMessage());
        }
        // }

        return siteMap;
    }


    public UserPortal getSiteMap(PageCommand cc) throws Exception {

        UserPortal userPortal;

        // String navigationMode = cc.getPortal().getProperty("osivia.navigationMode");

        // if( "cms".equals(navigationMode))
        // userPortal = getCMSSiteMap(cc);
        // else
        userPortal = this.getPageSiteMap(cc);

        return userPortal;
    }


    public String injectFooter(PageCommand cc) throws Exception {

        ControllerContext controllerCtx = cc.getControllerContext();
        ControllerRequestDispatcher rd = controllerCtx.getRequestDispatcher(getTargetContextPath(cc), this.footerPath);

        //
        if (rd != null) {

            UserPortal siteMap = (UserPortal) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_SITE_MAP + "."
                    + cc.getPortal().getName());

            if (siteMap == null) {
                siteMap = this.getSiteMap(cc);


                controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_SITE_MAP + "." + cc.getPortal().getName(), siteMap);

            }

            rd.setAttribute(Constants.ATTR_SITE_MAP, siteMap);
            rd.setAttribute(Constants.ATTR_URL_FACTORY, this.getUrlFactory());
            rd.setAttribute(Constants.ATTR_PORTAL_CTX, new PortalControllerContext(controllerCtx));

            //
            rd.include();
            return rd.getMarkup();
        }

        //
        return null;
    }


    public String injectAdminDashboardNav(PageCommand cc) {
        ControllerContext controllerCtx = cc.getControllerContext();
        ControllerRequestDispatcher rd = controllerCtx.getRequestDispatcher(getTargetContextPath(cc), "/WEB-INF/jsp/header/header.jsp");


        //
        if (rd != null) {
            // Get user
            Controller controller = controllerCtx.getController();
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


            //
            boolean admin = false;
            if (cc instanceof RenderPageCommand) {
                RenderPageCommand rpc = (RenderPageCommand) cc;
                PortalObject portalObject = rpc.getPage().getPortal();
                admin = "admin".equalsIgnoreCase(portalObject.getName());
            }

            // Link to default page of default portal
            // Cannot use defaultPortalId in 2.6.x because the default context doesn't have the view right.
            // Upgrading from 2.6.1 to 2.6.2 would break.
            ViewPageCommand vpc = new ViewPageCommand(this.portalObjectContainer.getContext().getDefaultPortal().getId());
            rd.setAttribute("org.jboss.portal.header.DEFAULT_PORTAL_URL", new PortalURLImpl(vpc, controllerCtx, null, null));


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


    public String injectSearch(PageCommand rpc) throws Exception {

        ControllerContext controllerCtx = rpc.getControllerContext();
        ControllerRequestDispatcher rd = controllerCtx.getRequestDispatcher(getTargetContextPath(rpc), this.searchPath);

        // if( true)
        // return "test";

        //
        if (rd != null) {
            // Pour déterminer la page courante (premier niveau)


            /* Lien de recherche */

            Map<String, String> props = new HashMap<String, String>();
            props.put("osivia.nuxeoPath", "/");
            Map<String, String> params = new HashMap<String, String>();
            params.put("osivia.keywords", "__REPLACE_KEYWORDS__");

            // v1.0.13 : on ouvre tout le temp la meme page

            String searchUrl = this.urlFactory.getStartPageUrl(new PortalControllerContext(controllerCtx), rpc.getPortal().getId().toString(), "search",
                    "/default/templates/search", props, params);
            // String searchUrl = urlFactory.getStartPageUrl(new PortalControllerContext(controllerCtx), rpc.getPortal().getId().toString(), "search" +
            // System.currentTimeMillis(), "/default/templates/search", props, params);
            rd.setAttribute(Constants.ATTR_SEARCH_URL, searchUrl);


            rd.setAttribute(Constants.ATTR_URL_FACTORY, this.getUrlFactory());
            rd.setAttribute(Constants.ATTR_PORTAL_CTX, new PortalControllerContext(controllerCtx));


            rd.include();


            return rd.getMarkup();

        }

        //
        return null;
    }


    public String injectBreadcrumb(PageCommand rpc, PageRendition rendition) throws Exception {

        ControllerContext controllerCtx = rpc.getControllerContext();
        ControllerRequestDispatcher rd = controllerCtx.getRequestDispatcher(getTargetContextPath(rpc), this.breadcrumbPath);


        if (rd != null) {


            /******************************/
            /* Maj du breadcrumb */
            /******************************/


            Map winsCtx = rendition.getPageResult().getWindowContextMap();

            Breadcrumb breadcrumbMemo = (Breadcrumb) rpc.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb");
            if (breadcrumbMemo == null) {
                breadcrumbMemo = new Breadcrumb();
                rpc.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb", breadcrumbMemo);
            }

            Breadcrumb breadcrumbDisplay = new Breadcrumb();


            // Ajout de la page courante en premier élément

            Page page = ((RenderPageCommand) rpc).getPage();


            // On determine le path de navigation cms

            NavigationalStateContext nsContext = (NavigationalStateContext) controllerCtx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);


            PageNavigationalState pageState = nsContext.getPageNavigationalState(page.getId().toString());

            String basePath = page.getProperty("osivia.cms.basePath");

            String sPath[] = null;
            if (pageState != null) {
                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            }

            String pathPublication = null;
            if ((sPath != null) && (sPath.length > 0)) {
                pathPublication = sPath[0];
            }


            // Dnas le cas d'une publication correcte, on n'affiche pas les pages filles
            boolean publication = false;
            if (pathPublication != null) {
                if ((basePath != null) && pathPublication.startsWith(basePath)) {
                    publication = true;
                }
            }


            do {
                boolean displayPage = true;

                // TODO JSSCMS1: on ne devrait pas avoir à faire ce test ....
                // le osivia.cms.basePath devrait etre renseigné dans ce cas (mode puublication)
                // Mais ce n'est pas le cas quand on est dans la page de tete
                // Cas : blog : mode editon > le breadcrum compren la page dynamiqeu

                // Pas d'affichage des pages cms dynamiques en mode édition
                if ("wizzard".equals(controllerCtx.getAttribute(ControllerCommand.SESSION_SCOPE, "osivia.windowSettingMode"))
                        && (page instanceof CMSTemplatePage)) {
                    displayPage = false;
                }


                if (publication) {

                    // String basePath = page.getProperty("osivia.cms.basePath");
                    if (basePath != null) {
                        displayPage = false;
                    } else {
                        if ("1".equals(page.getProperty("osivia.cms.directContentPublisher"))) {
                            displayPage = false;
                        }
                    }

                }

                if (displayPage) {
                    ViewPageCommand viewCmd = new ViewPageCommand(page.getId());
                    String name = page.getDisplayName().getString(Locale.FRENCH, true);
                    if (name == null) {
                        name = page.getName();
                    }

                    String url = new PortalURLImpl(viewCmd, rpc.getControllerContext(), null, null).toString() + "?init-state=true";
                    BreadcrumbItem item = new BreadcrumbItem(name, url.toString(), page.getId(), false);
                    breadcrumbDisplay.getChilds().add(0, item);

                }

                PortalObject parent = page.getParent();

                // Récupération du parent
                if (parent instanceof Page) {
                    page = (Page) parent;
                } else {
                    page = null;
                }
            } while (page != null);


            /*
             * Ajout du path CMS
             */


            page = ((RenderPageCommand) rpc).getPage();

            if (!"1".equals(page.getProperty("osivia.cms.directContentPublisher"))) {

                // On identifie la page de tete de publication

                PortalObject po = page;
                while ((po instanceof Page) && (po.getDeclaredProperty("osivia.cms.basePath") == null)) {
                    po = po.getParent();
                }

                if (pageState != null) {

                    String navigationScope = page.getProperty("osivia.cms.navigationScope");

                    if ((pathPublication != null) && (basePath != null) && pathPublication.startsWith(basePath)) {


                        CMSServiceCtx cmxCtx = new CMSServiceCtx();
                        cmxCtx.setControllerContext(controllerCtx);
                        cmxCtx.setScope(navigationScope);

                        // test si mode assistant activé
                        if (InternalConstants.CMS_VERSION_PREVIEW.equals(controllerCtx.getAttribute(ControllerCommand.SESSION_SCOPE,
                                InternalConstants.ATTR_TOOLBAR_CMS_VERSION))) {
                            cmxCtx.setDisplayLiveVersion("1");
                        }



                        while (pathPublication.contains(basePath)) {

                            Map<String, String> pageParams = new HashMap<String, String>();


                            String url = this.urlFactory.getCMSUrl(new PortalControllerContext(controllerCtx),
                                    po.getId().toString(PortalObjectPath.CANONICAL_FORMAT), pathPublication, pageParams,
                                    IPortalUrlFactory.CONTEXTUALIZATION_PAGE, null, null, null, null, null);


                            CMSItem navItem = getCMSService().getPortalNavigationItem(cmxCtx, basePath, pathPublication);


                            BreadcrumbItem item = new BreadcrumbItem(navItem.getProperties().get("displayName"), url, null, false);
                            breadcrumbDisplay.getChilds().add(0, item);


                            // Get the navigation parent

                            CMSObjectPath parent = CMSObjectPath.parse(pathPublication).getParent();
                            pathPublication = parent.toString();


                        }


                    }
                }


            }

            /*
             * Synchronisation du breadcrum avec l'état de la page
             * (un item est crée automatiquement en vue max)
             *
             * Il s'agit ici de traiter les portlets maximisés manuellement par l'utilisateur
             */


            /*
             * // Find first non navigation portlet
             *
             * int firstPortlet = -1;
             * int iPortlet = 0;
             * for ( BreadcrumbItem item : breadcrumbMemo.getChilds()) {
             *
             * if( !item.isNavigationPlayer()) {
             * firstPortlet = iPortlet;
             * }
             *
             * iPortlet++;
             * }
             *
             *
             * // if( breadcrumbMemo.getChilds().size() == 0) {
             * if( firstPortlet == -1) {
             * // Si la page courante devient MAXIMIZED, on l'ajoute au breadcrumb
             * for (Object winCtx : winsCtx.values()) {
             * WindowContext wctx = (WindowContext)winCtx;
             * if( WindowState.MAXIMIZED.equals(wctx.getWindowState())) {
             *
             * PortalObjectId targetWindowId = PortalObjectId.parse( wctx.getId(), PortalObjectPath.SAFEST_FORMAT);
             * Window window = (Window) getPortalObjectContainer().getObject(targetWindowId);
             *
             * // Les portlets CMS sont dejà enregistrés dans le breadcrum
             * if( ! "1".equals(window.getDeclaredProperty("osivia.portletContextualizedInPage"))){
             *
             * // On supprimer les items courants
             * while( breadcrumbMemo.getChilds().size() > firstPortlet)
             * breadcrumbMemo.getChilds().remove(firstPortlet);
             *
             *
             * String title = wctx.getProperty("osivia.title");
             * if( title == null)
             * title = wctx.getResult().getTitle();
             * page = ((PageCommand) rpc).getPage();
             * ViewPageCommand viewCmd = new ViewPageCommand(page.getId());
             * String url = new PortalURLImpl(viewCmd, rpc.getControllerContext(), null, null).toString();
             * BreadcrumbItem newItem = new BreadcrumbItem(title, url, wctx.getId(), true);
             * breadcrumbMemo.getChilds().add(newItem);
             * }
             * }
             * }
             * } else {
             * //else if (breadcrumbMemo.getChilds().size() == 1) {
             * // Si le premier item était lié à une maximisattion et qu'on repasse
             * // en mode NORMAL, il faut le supprimer
             * BreadcrumbItem firstItem = breadcrumbMemo.getChilds().get(firstPortlet);
             *
             * //if( firstItem.isUserMaximized()){
             * boolean isWindowMaximized = false;
             *
             * for (Object winCtx : winsCtx.values()) {
             * WindowContext wctx = (WindowContext)winCtx;
             * if( WindowState.MAXIMIZED.equals(wctx.getWindowState())) {
             *
             * isWindowMaximized = true;
             * }
             *
             *
             * }
             * if( ! isWindowMaximized) {
             * breadcrumbMemo.getChilds().clear();
             *
             * while( breadcrumbMemo.getChilds().size() > firstPortlet)
             * breadcrumbMemo.getChilds().remove(firstPortlet);
             * }
             * //}
             * }
             */


            /*
             * Synchronisation du breadcrum avec l'état de la page
             * (un item est crée automatiquement en vue max)
             *
             * Il s'agit ici de traiter les portlets maximisés manuellement par l'utilisateur
             */

            // Find first non navigation portlet

            int firstPortlet = -1;
            int iPortlet = 0;
            for (BreadcrumbItem item : breadcrumbMemo.getChilds()) {

                if (!item.isNavigationPlayer()) {
                    firstPortlet = iPortlet;
                    break;
                }

                iPortlet++;
            }


            // Si la page courante devient MAXIMIZED, on l'ajoute au breadcrumb

            for (Object winCtx : winsCtx.values()) {
                WindowContext wctx = (WindowContext) winCtx;
                if (WindowState.MAXIMIZED.equals(wctx.getWindowState())) {

                    PortalObjectId targetWindowId = PortalObjectId.parse(wctx.getId(), PortalObjectPath.SAFEST_FORMAT);
                    Window window = (Window) this.getPortalObjectContainer().getObject(targetWindowId);

                    // Le fenetres dynamiques sont déja ajoutés lors du startDynamicCommand
                    if (!"1".equals(window.getDeclaredProperty("osisia.dynamicStarted"))) {

                        // Les portlets CMS sont dejà enregistrés dans le
                        // breadcrumb
                        if (!"1".equals(window.getDeclaredProperty("osivia.portletContextualizedInPage"))) {

                            // On supprimer les items courants
                            if (firstPortlet != -1) {
                                while (breadcrumbMemo.getChilds().size() > firstPortlet) {
                                    breadcrumbMemo.getChilds().remove(firstPortlet);
                                }
                            }

                            String title = wctx.getProperty("osivia.title");

                            if (title == null) {
                                title = wctx.getResult().getTitle();
                            }
                            page = rpc.getPage();
                            ViewPageCommand viewCmd = new ViewPageCommand(page.getId());
                            String url = new PortalURLImpl(viewCmd, rpc.getControllerContext(), null, null).toString();
                            BreadcrumbItem newItem = new BreadcrumbItem(title, url, wctx.getId(), true);
                            breadcrumbMemo.getChilds().add(newItem);
                        }
                    }
                }
            }


            // else if (breadcrumbMemo.getChilds().size() == 1) {
            // Si le premier item était lié à une maximisattion et qu'on repasse
            // en mode NORMAL, il faut le supprimer

            if (firstPortlet != -1) {

                // if( firstItem.isUserMaximized()){
                boolean isWindowMaximized = false;

                for (Object winCtx : winsCtx.values()) {
                    WindowContext wctx = (WindowContext) winCtx;
                    if (WindowState.MAXIMIZED.equals(wctx.getWindowState())) {

                        isWindowMaximized = true;
                    }


                }
                if (!isWindowMaximized) {

                    while (breadcrumbMemo.getChilds().size() > firstPortlet) {
                        breadcrumbMemo.getChilds().remove(firstPortlet);
                    }

                }
            }


            // Mise à jour et mémorisation de l'item courant
            // (titre, url, path, ...)


            if ((breadcrumbMemo != null) && (breadcrumbMemo.getChilds().size() > 0)) {

                for (Object winCtx : winsCtx.values()) {

                    WindowContext wctx = (WindowContext) winCtx;

                    if (WindowState.MAXIMIZED.equals(wctx.getWindowState())) {

                        BreadcrumbItem last = breadcrumbMemo.getChilds().get(breadcrumbMemo.getChilds().size() - 1);

                        // Maj du path
                        List<PortletPathItem> portletPath = (List<PortletPathItem>) controllerCtx.getAttribute(ControllerCommand.REQUEST_SCOPE,
                                "osivia.portletPath");
                        if (portletPath == null) {


                            // Maj du titre
                            // Dans l'ordre, titre de la window, titre du path, titre du portlet
                            String title = wctx.getProperty("osivia.title");
                            if (title == null) {
                                title = wctx.getResult().getTitle();
                            }

                            // Maj de l'url
                            page = rpc.getPage();
                            ViewPageCommand viewCmd = new ViewPageCommand(page.getId());
                            String url = new PortalURLImpl(viewCmd, rpc.getControllerContext(), null, null).toString();

                            last.setName(title);
                            last.setUrl(url);
                        } else {

                            // Valorise les labels et les urls liés au path

                            int indicePathItem = 0;

                            for (PortletPathItem pathItem : portletPath) {

                                // Set the content as a render parameter
                                ParametersStateString parameters = ParametersStateString.create();

                                for (Entry<String, String> name : pathItem.getRenderParams().entrySet()) {
                                    parameters.setValue(name.getKey(), name.getValue());
                                }


                                // Add public parameters
                                Map<QName, String[]> ps = pageState.getParameters();
                                for (Entry<QName, String[]> pageEntry : ps.entrySet()) {
                                    if (parameters.getValue(pageEntry.getKey().toString()) == null) {
                                        if (pageEntry.getValue().length > 0) {
                                            parameters.setValue(pageEntry.getKey().toString(), pageEntry.getValue()[0]);
                                        }
                                    }
                                }


                                PortalObjectId targetWindowId = PortalObjectId.parse(wctx.getId(), PortalObjectPath.SAFEST_FORMAT);

                                ControllerCommand renderCmd = new InvokePortletWindowRenderCommand(targetWindowId, Mode.VIEW, null, parameters);

                                // Perform a render URL on the target window
                                String url = new PortalURLImpl(renderCmd, rpc.getControllerContext(), null, null).toString();
                                pathItem.setUrl(url);

                                String label = pathItem.getLabel();
                                if ((indicePathItem == 0) && (wctx.getProperty("osivia.title") != null)) {
                                    label = wctx.getProperty("osivia.title");
                                }
                                pathItem.setLabel(label);

                                last.setPortletPath(portletPath);

                                indicePathItem++;
                            }

                        }


                    }
                }
            }

            // Ajout des éléments mémorisés
            if (breadcrumbMemo != null) {
                for (BreadcrumbItem itemMemo : breadcrumbMemo.getChilds()) {

                    if (!itemMemo.isNavigationPlayer()) {
                        if (itemMemo.getPortletPath() != null) {

                            // Ajout des items correspondant au path du portlet

                            int indicePathItem = 0;
                            for (PortletPathItem pathItem : itemMemo.getPortletPath()) {

                                BreadcrumbItem pathDisplayItem = new BreadcrumbItem(pathItem.getLabel(), pathItem.getUrl(), itemMemo.getId(), true);
                                breadcrumbDisplay.getChilds().add(pathDisplayItem);


                                indicePathItem++;
                            }

                        } else {
                            // Pas de path
                            // Ajout de l'item correspondant au titre du portlet

                            breadcrumbDisplay.getChilds().add(itemMemo);
                        }
                    }
                }
            }


            rd.setAttribute(Constants.ATTR_BREADCRUMB, breadcrumbDisplay);

            PortalControllerContext portalCtx = new PortalControllerContext(controllerCtx);

            // TEST V2 PERMALINK : Permalink sur les contenus
            /*
             *
             *
             *
             *
             * // First : Content defined to portlet
             *
             * String docPath = (String) controllerCtx.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.cms.portletContentPath");
             *
             * if( docPath == null) {
             *
             * // Secund : Content associated to window
             *
             * for (Object winCtx : winsCtx.values()) {
             *
             * WindowContext wctx = (WindowContext) winCtx;
             *
             * if (WindowState.MAXIMIZED.equals(wctx.getWindowState())) {
             *
             * PortalObjectId targetWindowId = PortalObjectId.parse( wctx.getId(), PortalObjectPath.SAFEST_FORMAT);
             *
             * Window window = (Window) portalObjectContainer.getObject(targetWindowId);
             *
             * if( window != null) {
             * docPath = window.getProperty("osivia.cms.portletContentPath");
             * }
             * }
             * }
             * }
             *
             *
             *
             *
             * if( docPath == null) {
             *
             * // Else, content from CMS navigation
             * if( pageState != null) {
             *
             * String navPath[] = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
             *
             * String pathNavigation = null;
             * if( navPath != null && navPath.length > 0)
             * pathNavigation = sPath[ 0];
             *
             * docPath = pathNavigation;
             *
             *
             *
             * }
             *
             * }
             *
             * if( docPath != null)
             * rd.setAttribute(Constants.ATTR_PERMLINK_URL, getUrlFactory().getPermaLink(portalCtx, null, null, docPath, IPortalUrlFactory.PERM_LINK_TYPE_CMS));
             */

            /*
             * A combiner avec breadcrumb.jsp
             *
             * <!--
             * <% if( permLinkUrl != null) { %>
             *
             *
             * <a class="permalink" title="Lien permanent" href="<%= permLinkUrl %>"><img
             * src="/toutatice-portail-demo-charte/themes/atomo/images/permalink.png"> </a>
             *
             * <%
             *
             * }
             * %>
             *
             * -->
             */


            rd.setAttribute(Constants.ATTR_URL_FACTORY, this.getUrlFactory());
            rd.setAttribute(Constants.ATTR_PORTAL_CTX, portalCtx);

            // v1.0.17
            if (InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(controllerCtx.getAttribute(ControllerCommand.SESSION_SCOPE,
                    InternalConstants.ATTR_WINDOWS_SETTING_MODE))) {
                rd.setAttribute(InternalConstants.ATTR_TOOLBAR_WIZARD_MODE, "1");
            }


            rd.include();


            return rd.getMarkup();

        }

        //
        return null;
    }


    public String getFooterPath() {
        return this.footerPath;
    }

    public void setFooterPath(String footerPath) {
        this.footerPath = footerPath;
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

        String themeId = page.getProperty(ThemeConstants.PORTAL_PROP_THEME);
        PageService pageService = pc.getControllerContext().getController().getPageService();
        ThemeService themeService = pageService.getThemeService();
        PortalTheme theme = themeService.getThemeById(themeId);
        return theme.getThemeInfo().getContextPath();
    }


    public String getLoginNamespace() {
        return this.loginNamespace;
    }

    public void setLoginNamespace(String loginNamespace) {
        this.loginNamespace = loginNamespace;
    }

    public ServerConfig getConfig() {
        return this.config;
    }

    public void setConfig(ServerConfig config) {
        this.config = config;
    }

    public String getBreadcrumbPath() {
        return this.breadcrumbPath;
    }

    public void setBreadcrumbPath(String breadcrumbPath) {
        this.breadcrumbPath = breadcrumbPath;
    }

    public String getSearchPath() {
        return this.searchPath;
    }

    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }


    public String getToolbarPath() {
        return this.toolbarPath;
    }

    public void setToolbarPath(String toolbarPath) {
        this.toolbarPath = toolbarPath;
    }
}
