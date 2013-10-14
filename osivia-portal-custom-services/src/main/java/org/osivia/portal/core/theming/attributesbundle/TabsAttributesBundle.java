package org.osivia.portal.core.theming.attributesbundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.BooleanUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.aspects.server.UserInterceptor;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.User;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.server.ServerRequest;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.theming.UserPage;
import org.osivia.portal.api.theming.UserPortal;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.PortalObjectOrderComparator;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;
import org.osivia.portal.core.profils.ProfilManager;

/**
 * Tabs attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class TabsAttributesBundle implements IAttributesBundle {

    /** Singleton instance. */
    private static TabsAttributesBundle instance;

    /** Global cache service. */
    private final org.osivia.portal.core.cache.global.ICacheService globalCacheService;
    /** Services cache service. */
    private final org.osivia.portal.api.cache.services.ICacheService servicesCacheService;
    /** Portal authorization manager factory. */
    private final PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;
    /** Profile manager. */
    private final IProfilManager profileManager;
    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;

    /** Toolbar attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private TabsAttributesBundle() {
        super();

        // Global cache service
        this.globalCacheService = Locator.findMBean(org.osivia.portal.core.cache.global.ICacheService.class, "osivia:service=Cache");
        // Services cache service
        this.servicesCacheService = Locator.findMBean(org.osivia.portal.api.cache.services.ICacheService.class, "osivia:service=CacheServices");
        // Portal authorization manager factory
        this.portalAuthorizationManagerFactory = Locator.findMBean(PortalAuthorizationManagerFactory.class, "portal:service=PortalAuthorizationManagerFactory");
        // Profile manager
        this.profileManager = Locator.findMBean(IProfilManager.class, "osivia:service=ProfilManager");
        // URL factory
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");

        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_USER_PORTAL);
        this.names.add(Constants.ATTR_PAGE_ID);
        this.names.add(Constants.ATTR_FIRST_TAB);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static TabsAttributesBundle getInstance() {
        if (instance == null) {
            instance = new TabsAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Attributes initialization to prevent multiple fill call
        for (String attributeName : this.names) {
            attributes.put(attributeName, null);
        }

        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();

        PortalObjectId popupWindowId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID");
        if (popupWindowId == null) {
            // User
            User user = (User) controllerContext.getServerInvocation().getAttribute(Scope.PRINCIPAL_SCOPE, UserInterceptor.USER_KEY);

            UserPortal tabbedNavUserPortal = (UserPortal) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavUserPortal");
            Long headerCount = (Long) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavHeaderCount");
            String headerUsername = (String) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavheaderUsername");
            Long cmsTs = (Long) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.cmsTimeStamp");

            boolean refreshUserPortal = true;
            if ((headerCount != null) && (tabbedNavUserPortal != null) && tabbedNavUserPortal.getName().equals(renderPageCommand.getPortal().getName())
                    && (cmsTs != null)) {
                if ((user == null) || (headerUsername != null)) {
                    // Check header and services caches validity
                    if ((headerCount.longValue() == this.globalCacheService.getHeaderCount()) && (cmsTs > this.servicesCacheService.getCacheInitialisationTs())) {
                        refreshUserPortal = false;
                    }
                }

                if (headerCount.longValue() > this.globalCacheService.getHeaderCount()) {
                    // Can occurs if JBoss cache manager has crashed : update global cache with baseline value
                    do {
                        this.globalCacheService.incrementHeaderCount();
                    } while (headerCount.longValue() > this.globalCacheService.getHeaderCount());
                }
            }

            if (refreshUserPortal) {
                tabbedNavUserPortal = this.getPageBean(renderPageCommand);

                controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavUserPortal", tabbedNavUserPortal);
                controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavHeaderCount",
                        new Long(this.globalCacheService.getHeaderCount()));

                if (user != null) {
                    controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavheaderUsername", user.getUserName());
                }

                controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.cmsTimeStamp", System.currentTimeMillis());
            }

            // Update menu page markers
            Iterator<UserPage> mainPages = tabbedNavUserPortal.getUserPages().iterator();
            String pageMarker = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "currentPageMarker");

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

            // Get first level page
            Page mainPage = renderPageCommand.getPage();
            PortalObject parent = mainPage.getParent();
            while (parent instanceof Page) {
                mainPage = (Page) parent;
                parent = mainPage.getParent();
            }

            // User portal
            attributes.put(Constants.ATTR_USER_PORTAL, tabbedNavUserPortal);
            // Page identifier
            attributes.put(Constants.ATTR_PAGE_ID, mainPage.getId());
            // First tab
            attributes.put(Constants.ATTR_FIRST_TAB, controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_FIRST_TAB));

        }
    }


    /**
     * Utility method used to get user portal pages.
     *
     * @param renderPageCommand render page command
     * @return user portal pages
     * @throws ControllerException
     */
    private UserPortal getPageBean(RenderPageCommand renderPageCommand) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        // Server request
        ServerRequest request = controllerContext.getServerInvocation().getRequest();
        // Portal
        Portal portal = renderPageCommand.getPortal();

        // Hide default page indicator
        boolean hideDefaultPage = BooleanUtils.toBoolean(portal.getDeclaredProperty(InternalConstants.TABS_HIDE_DEFAULT_PAGE_PROPERTY));
        // Default page
        Page defaultPage = portal.getDefaultPage();

        // User portal
        UserPortal userPortal = new UserPortal();
        userPortal.setName(portal.getName());

        // Portal authorization manager
        PortalAuthorizationManager portalAuthorizationManager = this.portalAuthorizationManagerFactory.getManager();

        // Main pages
        List<UserPage> mainPages = new ArrayList<UserPage>();
        userPortal.setUserPages(mainPages);

        // Sorted pages
        SortedSet<Page> sortedPages = new TreeSet<Page>(PortalObjectOrderComparator.getInstance());
        for (PortalObject po : portal.getChildren(PortalObject.PAGE_MASK)) {
            sortedPages.add((Page) po);
        }


        // Hide default page if user profile is not default profile...
        String pageToHide = null;
        ProfilBean profil = this.profileManager.getProfilPrincipalUtilisateur();
        if ((profil != null) && !ProfilManager.DEFAULT_PROFIL_NAME.equals(profil.getName())) {
            // ...except for administrators !
            if (!PageCustomizerInterceptor.isAdministrator(controllerContext)) {
                pageToHide = portal.getDeclaredProperty("osivia.unprofiled_home_page");
            }
        }


        for (Page child : sortedPages) {
            // Check if child must be hidden
            if (hideDefaultPage && child.equals(defaultPage)) {
                continue;
            }

            PortalObjectId pageIdToControl = child.getId();
            if (child instanceof ITemplatePortalObject) {
                // In case of template, check original template rights ; moreover, there is no customization
                pageIdToControl = ((ITemplatePortalObject) child).getTemplate().getId();
            }

            // Permission
            PortalObjectPermission permission = new PortalObjectPermission(pageIdToControl, PortalObjectPermission.VIEW_MASK);

            if (portalAuthorizationManager.checkPermission(permission) && ((pageToHide == null) || (!child.getName().equals(pageToHide)))) {
                UserPage userPage = new UserPage();
                mainPages.add(userPage);

                // View page command
                ViewPageCommand showPage = new ViewPageCommand(child.getId());

                userPage.setId(child.getId());

                String url = new PortalURLImpl(showPage, controllerContext, null, null).toString();
                userPage.setUrl(url + "?init-state=true");

                String name = PortalObjectUtils.getDisplayName(child, request.getLocales());
                userPage.setName(name);

                if ((child instanceof ITemplatePortalObject) && ((ITemplatePortalObject) child).isClosable()) {
                    try {
                        String parentId = URLEncoder.encode(child.getParent().getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");
                        String pageId = URLEncoder.encode(child.getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

                        String closePageURL = this.urlFactory.getDestroyPageUrl(new PortalControllerContext(controllerContext), parentId, pageId);
                        userPage.setClosePageUrl(closePageURL);
                    } catch (UnsupportedEncodingException e) {
                        throw new ControllerException(e);
                    }
                }


                List<UserPage> subPages = new ArrayList<UserPage>();
                userPage.setChildren(subPages);

                SortedSet<Page> sortedSubPages = new TreeSet<Page>(PortalObjectOrderComparator.getInstance());
                for (PortalObject po : child.getChildren(PortalObject.PAGE_MASK)) {
                    sortedSubPages.add((Page) po);
                }


                for (Page childChild : sortedSubPages) {
                    PortalObjectId subpageIdToControl = childChild.getId();
                    if (childChild instanceof ITemplatePortalObject) {
                        // In case of template, check original template rights ; moreover, there is no customization
                        subpageIdToControl = ((ITemplatePortalObject) childChild).getTemplate().getId();
                    }


                    PortalObjectPermission permSubPage = new PortalObjectPermission(subpageIdToControl, PortalObjectPermission.VIEW_MASK);
                    if (portalAuthorizationManager.checkPermission(permSubPage) && ((pageToHide == null) || (!childChild.getName().equals(pageToHide)))) {
                        UserPage userSubPage = new UserPage();

                        // View sub page command
                        ViewPageCommand showSubPage = new ViewPageCommand(childChild.getId());

                        userSubPage.setId(childChild.getId());

                        String subName = PortalObjectUtils.getDisplayName(childChild, request.getLocales());
                        userSubPage.setName(subName);

                        String subUrl = new PortalURLImpl(showSubPage, controllerContext, null, null).toString();

                        userSubPage.setUrl(subUrl + "?init-state=true");

                        subPages.add(userSubPage);
                    }
                }
            }
        }

        return userPortal;
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
