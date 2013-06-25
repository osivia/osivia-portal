package org.osivia.portal.core.page;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.aspects.controller.node.Navigation;
import org.jboss.portal.core.aspects.server.UserInterceptor;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.impl.api.node.PortalNodeImpl;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.User;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.server.ServerRequest;
import org.jboss.portal.server.config.ServerConfig;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.charte.UserPage;
import org.osivia.portal.api.charte.UserPortal;
import org.osivia.portal.api.contexte.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.auth.constants.InternalConstants;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;
import org.osivia.portal.core.profils.ProfilManager;


public class TabsCustomizerInterceptor extends ControllerInterceptor {

    protected static final Log windowlogger = LogFactory.getLog("PORTAL_WINDOW");

    /** . */
    protected static final Log logger = LogFactory.getLog(TabsCustomizerInterceptor.class);


    /** . */
    private String tabsPath;


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


    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }


    @Override
    public ControllerResponse invoke(ControllerCommand cmd) throws Exception {


        ControllerResponse resp;


        resp = (ControllerResponse) cmd.invokeNext();


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
                    if( popupWindowId == null)
                        this.injectStandardHeaders(rpc, rendition);
                }

            }
        }

        //
        return resp;

    }


    void injectStandardHeaders(PageCommand rpc, PageRendition rendition) throws Exception {
        String tabbedNav = this.injectTabbedNav(rpc, rendition);
        if (tabbedNav != null) {
            Map<String, String> windowProps = new HashMap<String, String>();
            windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
            WindowResult res = new WindowResult("", tabbedNav, Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
            WindowContext blah = new WindowContext("BLAH", "tabs", "0", res);
            rendition.getPageResult().addWindowContext(blah);

            //
            Region region = rendition.getPageResult().getRegion2("tabs");
            DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
        }


    }


    void injectAdminHeaders(PageCommand rpc, PageRendition rendition) {


        //
        String tabbedNav = this.injectAdminTabbedNav(rpc);
        if (tabbedNav != null) {
            Map<String, String> windowProps = new HashMap<String, String>();
            windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
            WindowResult res = new WindowResult("", tabbedNav, Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
            WindowContext blah = new WindowContext("BLAH", "navigation", "0", res);
            rendition.getPageResult().addWindowContext(blah);

            //
            Region region = rendition.getPageResult().getRegion2("navigation");
            DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
        }


    }


    public void addSubpagesToCMSHeaderTab(CMSServiceCtx cmsCtx, IPortalUrlFactory urlFactory, PortalControllerContext portalCtx, Page page, String basePath,
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

                for (CMSItem subNavItem : navItems) {
                    if ("1".equals(navItem.getProperties().get("menuItem"))) {
                        this.addSubpagesToCMSHeaderTab(cmsCtx, urlFactory, portalCtx, page, basePath, subNavItem, subPages);
                    }
                }


            }
        } catch (Exception e) {
            // May be a security issue, don't block footer
            logger.error(e.getMessage());
        }
    }


    public List<UserPage> getCMSHeaderTabs(ControllerContext controllerCtx, Page cmsPage) throws Exception {
        String navigationScope = cmsPage.getProperty("osivia.cms.navigationScope");

        CMSServiceCtx cmxCtx = new CMSServiceCtx();
        cmxCtx.setControllerContext(controllerCtx);
        cmxCtx.setScope(navigationScope);

        PortalControllerContext portalCtx = new PortalControllerContext(controllerCtx);


        List<UserPage> mainPages = new ArrayList<UserPage>(10);


        // CMS sub pages


        // if (("1".equals(cmsPage.getDeclaredProperty("osivia.cms.pageContextualizationSupport")) && (cmsPage
        // .getDeclaredProperty("osivia.cms.basePath") != null))) {

        try {

            CMSItem publishSpaceConfig = CmsCommand.getPagePublishSpaceConfig(controllerCtx, cmsPage);

            if ((publishSpaceConfig != null) && "1".equals(publishSpaceConfig.getProperties().get("contextualizeInternalContents"))) {

                List<CMSItem> navItems = getCMSService().getPortalNavigationSubitems(cmxCtx, cmsPage.getDeclaredProperty("osivia.cms.basePath"),
                        cmsPage.getDeclaredProperty("osivia.cms.basePath"));


                for (CMSItem navItem : navItems) {
                    if ("1".equals(navItem.getProperties().get("menuItem"))) {
                        this.addSubpagesToCMSHeaderTab(cmxCtx, this.urlFactory, portalCtx, cmsPage, cmsPage.getDeclaredProperty("osivia.cms.basePath"),
                                navItem, mainPages);


                    }
                }


            }


        } catch (Exception e) {
            // May be a security issue, don't block footer
            logger.error(e.getMessage());
        }
        // }


        return mainPages;
    }


    public String injectAdminTabbedNav(PageCommand rpc) {
        ControllerContext controllerCtx = rpc.getControllerContext();
        ControllerRequestDispatcher rd = controllerCtx
                .getRequestDispatcher(PageCustomizerInterceptor.getTargetContextPath(rpc), "/WEB-INF/jsp/header/tabs.jsp");

        //
        if (rd != null) {
            Page page = rpc.getPage();
            PortalAuthorizationManager pam = this.portalAuthorizationManagerFactory.getManager();
            PortalNodeImpl node = new PortalNodeImpl(pam, page);

            //
            rd.setAttribute("org.jboss.portal.api.PORTAL_NODE", node);
            rd.setAttribute("org.jboss.portal.api.PORTAL_RUNTIME_CONTEXT", Navigation.getPortalRuntimeContext());

            //
            rd.include();
            return rd.getMarkup();
        }

        //
        return null;
    }


    public String injectTabbedNav(PageCommand rpc, PageRendition rendition) throws Exception {

        ControllerContext controllerCtx = rpc.getControllerContext();
        ControllerRequestDispatcher rd = controllerCtx.getRequestDispatcher(PageCustomizerInterceptor.getTargetContextPath(rpc), this.tabsPath);

        // if( true)
        // return "test";

        //
        if (rd != null) {


            User user = (User) controllerCtx.getServerInvocation().getAttribute(Scope.PRINCIPAL_SCOPE, UserInterceptor.USER_KEY);


            UserPortal tabbedNavUserPortal = (UserPortal) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavUserPortal");
            Long headerCount = (Long) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavHeaderCount");
            String headerUsername = (String) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavheaderUsername");

            Long cmsTs = (Long) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.cmsTimeStamp");

            boolean refreshUserPortal = true;

            if ((headerCount != null) && (tabbedNavUserPortal != null) && tabbedNavUserPortal.getName().equals(rpc.getPortal().getName()) && (cmsTs != null)) {

                if ((user != null) && (headerUsername == null)) {
                    // Rafraichir le menu à la connexion
                    logger.debug("connection utilisateur");

                } else {

                    // On vérifie la validité du cache du header et du cache des services
                    if ((headerCount.longValue() == this.getCacheService().getHeaderCount())
                            && (cmsTs > this.getServicesCacheService().getCacheInitialisationTs())) {
                        refreshUserPortal = false;
                    }
                }

                if (headerCount.longValue() > this.getCacheService().getHeaderCount()) {
                    // Peut arriver si les gestionnaire de cache (jboss
                    // cache) a planté
                    // On remet à jour le cache centralisé qui porte la
                    // valeur de référence

                    do {
                        this.getCacheService().incrementHeaderCount();
                    } while (headerCount.longValue() > this.getCacheService().getHeaderCount());
                }
            }

            if (refreshUserPortal) {

                tabbedNavUserPortal = this.getPageBean(rpc);

                controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavUserPortal", tabbedNavUserPortal);
                controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavHeaderCount", new Long(this.getCacheService().getHeaderCount()));

                if (user != null) {
                    controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavheaderUsername", user.getUserName());
                }


                controllerCtx.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.cmsTimeStamp", System.currentTimeMillis());

            }


            // Maj des pages markers du menu

            Iterator<UserPage> mainPages = tabbedNavUserPortal.getUserPages().iterator();
            String pageMarker = (String) controllerCtx.getAttribute(Scope.REQUEST_SCOPE, "currentPageMarker");

            while (mainPages.hasNext()) {

                UserPage userPage = mainPages.next();
                userPage.setUrl(userPage.getUrl().replaceAll("/pagemarker/([^/]*)/", "/pagemarker/" + pageMarker + "/"));
                if (userPage.getClosePageUrl() != null) {
                    userPage.setClosePageUrl(userPage.getClosePageUrl().replaceAll("/pagemarker/([^/]*)/", "/pagemarker/" + pageMarker + "/"));
                }

                for (UserPage userSubPage : userPage.getChildren()) {
                    userSubPage.setUrl(userSubPage.getUrl().replaceAll("/pagemarker/([^/]*)/", "/pagemarker/" + pageMarker + "/"));

                    if (userSubPage.getClosePageUrl() != null) {
                        userSubPage.setClosePageUrl(userSubPage.getClosePageUrl().replaceAll("/pagemarker/([^/]*)/", "/pagemarker/" + pageMarker + "/"));
                    }


                }
            }


            /* Determination de la page courante (premier niveau) */

            PortalObject portal = rpc.getPage();
            Page mainPage = rpc.getPage();

            while (!(portal instanceof Portal)) {
                mainPage = (Page) portal;
                portal = portal.getParent();
            }


            rd.setAttribute(Constants.ATTR_USER_PORTAL, tabbedNavUserPortal);


            String pageCMSPath = null;


            // Navigation CMS

            if ("cms".equals(rpc.getPage().getProperty("osivia.navigationMode"))) {

                // On détermine le path CMS de la page
                // Pour cela on remonte au 1er sous-niveau

                NavigationalStateContext nsContext = (NavigationalStateContext) controllerCtx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

                PageNavigationalState pageState = nsContext.getPageNavigationalState(rpc.getPage().getId().toString());

                if (pageState != null) {
                    String sContentPath[] = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.itemRelPath"));

                    if ((sContentPath != null) && (sContentPath.length == 1)) {


                        String spacePath = rpc.getPage().getProperty("osivia.cms.basePath");

                        if (spacePath != null) {

                            String contentPath = spacePath + sContentPath[0];

                            CMSObjectPath parent = CMSObjectPath.parse(contentPath).getParent();
                            String parentPath = parent.toString();

                            while (parentPath.contains(spacePath) && !(parentPath.equals(spacePath))) {

                                contentPath = parentPath.toString();

                                parent = CMSObjectPath.parse(contentPath).getParent();
                                parentPath = parent.toString();

                            }
                            pageCMSPath = contentPath;
                        }

                    }
                }
            }


            if (pageCMSPath != null) {
                rd.setAttribute(Constants.ATTR_PAGE_ID, pageCMSPath); // path CMS
            } else {
                rd.setAttribute(Constants.ATTR_PAGE_ID, mainPage.getId()); // Path page
            }

            rd.setAttribute(Constants.ATTR_FIRST_TAB, controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_FIRST_TAB));


            rd.setAttribute(Constants.ATTR_URL_FACTORY, this.getUrlFactory());
            rd.setAttribute(Constants.ATTR_PORTAL_CTX, new PortalControllerContext(controllerCtx));

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


    /**
     * Cette structure est générée pour simplifier l'intégration graphique et pour optimiser les performances
     *
     * @param rpc
     * @return
     */

    public UserPortal getPageBean(PageCommand rpc) throws Exception {


        // logger.debug("getPageBean 1" + System.currentTimeMillis());

        ControllerContext controllerCtx = rpc.getControllerContext();
        ServerRequest request = controllerCtx.getServerInvocation().getRequest();

        Portal portal = rpc.getPortal();


        UserPortal userPortal = new UserPortal();
        userPortal.setName(portal.getName());

        PortalAuthorizationManager pam = this.portalAuthorizationManagerFactory.getManager();

        List<UserPage> mainPages = new ArrayList<UserPage>(10);
        userPortal.setUserPages(mainPages);


        SortedSet<Page> sortedPages = new TreeSet<Page>(PageUtils.orderComparator);
        for (PortalObject po : portal.getChildren(PortalObject.PAGE_MASK)) {
            sortedPages.add((Page) po);
        }


        // Il faut masquer la page par défaut si l'utilisateur à un profil autre que le profil par défaut
        String pageToHide = null;
        ProfilBean profil = this.getProfilManager().getProfilPrincipalUtilisateur();
        if ((profil != null) && !ProfilManager.DEFAULT_PROFIL_NAME.equals(profil.getName())) {
            // Sauf pour les administrateurs ...
            if (!PageCustomizerInterceptor.isAdministrator(controllerCtx)) {
                pageToHide = portal.getDeclaredProperty("osivia.unprofiled_home_page");
            }
        }


        for (Page child : sortedPages) {

            PortalObjectId pageIdToControl = child.getId();
            if (child instanceof ITemplatePortalObject) {
                // Dans le cas du template, il faut regarder les droits posées sur le template
                // d'origine
                // De plus, il n'y a pas de personnalisation
                pageIdToControl = ((ITemplatePortalObject) child).getTemplate().getId();
            }


            PortalObjectPermission perm = new PortalObjectPermission(pageIdToControl, PortalObjectPermission.VIEW_MASK);

            if (pam.checkPermission(perm) && ((pageToHide == null) || (!child.getName().equals(pageToHide)))) {

                String navigationMode = child.getDeclaredProperty("osivia.navigationMode");

                if ("cms".equals(navigationMode)) {


                    List<UserPage> cmsPages = this.getCMSHeaderTabs(controllerCtx, child);


                    for (UserPage cmsPage : cmsPages) {
                        mainPages.add(cmsPage);
                    }


                } else {

                    UserPage userPage = new UserPage();
                    mainPages.add(userPage);

                    ViewPageCommand showPage = new ViewPageCommand(child.getId());

                    userPage.setId(child.getId());

                    String url = new PortalURLImpl(showPage, controllerCtx, null, null).toString();
                    userPage.setUrl(url + "?init-state=true");

                    String name = PortalObjectUtils.getDisplayName(child, request.getLocales());
                    userPage.setName(name);


                    if ((child instanceof ITemplatePortalObject) && ((ITemplatePortalObject) child).isClosable()) {

                        String parentId = URLEncoder.encode(child.getParent().getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");
                        String pageId = URLEncoder.encode(child.getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

                        String closePageURL = this.urlFactory.getDestroyPageUrl(new PortalControllerContext(controllerCtx), parentId, pageId);
                        userPage.setClosePageUrl(closePageURL);

                    }


                    List<UserPage> subPages = new ArrayList<UserPage>(10);
                    userPage.setChildren(subPages);

                    SortedSet<Page> sortedSubPages = new TreeSet<Page>(PageUtils.orderComparator);
                    for (PortalObject po : child.getChildren(PortalObject.PAGE_MASK)) {
                        sortedSubPages.add((Page) po);

                    }


                    for (Page childChild : sortedSubPages) {

                        PortalObjectId subpageIdToControl = childChild.getId();
                        if (childChild instanceof ITemplatePortalObject) {
                            // Dans le cas du template, il faut regarder les droits posées sur le template d'origine
                            // De plus, il n'y a pas de personnalisation
                            subpageIdToControl = ((ITemplatePortalObject) childChild).getTemplate().getId();
                        }


                        PortalObjectPermission permSubPage = new PortalObjectPermission(subpageIdToControl, PortalObjectPermission.VIEW_MASK);

                        if (pam.checkPermission(permSubPage) && ((pageToHide == null) || (!childChild.getName().equals(pageToHide)))) {

                            UserPage userSubPage = new UserPage();

                            ViewPageCommand showSubPage = new ViewPageCommand(childChild.getId());

                            userSubPage.setId(childChild.getId());

                            String subName = PortalObjectUtils.getDisplayName(childChild, request.getLocales());
                            userSubPage.setName(subName);

                            String subUrl = new PortalURLImpl(showSubPage, controllerCtx, null, null).toString();

                            userSubPage.setUrl(subUrl + "?init-state=true");

                            subPages.add(userSubPage);
                        }
                    }
                }

            }

        }

        // logger.debug("getPageBean 5" + System.currentTimeMillis());

        return userPortal;

    }


    public String getTabsPath() {
        return this.tabsPath;
    }

    public void setTabsPath(String tabsPath) {
        this.tabsPath = tabsPath;
    }


    public ServerConfig getConfig() {
        return this.config;
    }

    public void setConfig(ServerConfig config) {
        this.config = config;
    }


}
