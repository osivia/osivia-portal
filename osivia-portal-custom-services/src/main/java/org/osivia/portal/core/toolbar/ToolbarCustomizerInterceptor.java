package org.osivia.portal.core.toolbar;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.command.WindowCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.Role;
import org.jboss.portal.security.PortalSecurityException;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.assistantpage.AssistantPageCustomizerInterceptor;
import org.osivia.portal.core.assistantpage.CMSEditionPageCustomizerInterceptor;
import org.osivia.portal.core.assistantpage.ChangeCMSEditionModeCommand;
import org.osivia.portal.core.assistantpage.ChangeModeCommand;
import org.osivia.portal.core.assistantpage.DeletePageCommand;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.page.MonEspaceCommand;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.page.PageUtils;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.page.RefreshPageCommand;

/**
 * Toolbar customizer interceptor.
 * 
 * @see ControllerInterceptor
 */
public class ToolbarCustomizerInterceptor extends AssistantPageCustomizerInterceptor {

    /** Toolbar order. */
    private static final String TOOLBAR_ORDER = "0";
    /** Empty renderer. */
    private static final String EMPTY_RENDERER = "emptyRenderer";
    /** Toolbar window title. */
    private static final String WINDOW_TITLE_TOOLBAR = "Toolbar";
    /** Toolbar region name. */
    private static final String REGION_NAME_TOOLBAR = "toolbar";
    /** Mode normal. */
    private static final String MODE_NORMAL = "normal";
    /** Mode wizard. */
    private static final String MODE_WIZARD = "wizzard";
    /** Mode preview. */
    private static final String MODE_PREVIEW = "preview";
    /** Mode admin. */
    private static final String MODE_ADMIN = "admin";

    /** Logger. */
    protected static final Log logger = LogFactory.getLog(ToolbarCustomizerInterceptor.class);

    /** Admin portal ID. */
    private static final PortalObjectId adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);

    /** Toolbar path. */
    private String toolbarPath;
    /** Toolbar settings path. */
    private String toolbarSettingsPath;
    /** URL factory. */
    private IPortalUrlFactory urlFactory;
    /** Target settings context path. */
    private String targetSettingsContextPath;


    /**
     * Default constructor.
     */
    public ToolbarCustomizerInterceptor() {
        super();
    }


    /**
     * Invocation.
     * 
     * @param command command
     * @return invocation response
     */
    @Override
    public ControllerResponse invoke(ControllerCommand command) throws Exception {
        ControllerResponse response = (ControllerResponse) command.invokeNext();

        if ((response instanceof PageRendition) && (command instanceof RenderPageCommand)) {
            RenderPageCommand renderPageCommand = (RenderPageCommand) command;
            PageRendition rendition = (PageRendition) response;

            PortalObject portalObject = renderPageCommand.getPage().getPortal();
            boolean admin = MODE_ADMIN.equalsIgnoreCase(portalObject.getName());

            // Toolbar must not be loaded in JBoss portal administration
            if (!admin) {
                // Toolbar
                String toolbarContent = this.injectToolbar(renderPageCommand);
                if (toolbarContent != null) {
                    Map<String, String> windowProps = new HashMap<String, String>();
                    windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, EMPTY_RENDERER);
                    windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, EMPTY_RENDERER);
                    windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, EMPTY_RENDERER);
                    WindowResult result = new WindowResult(WINDOW_TITLE_TOOLBAR, toolbarContent, Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL,
                            Mode.VIEW);
                    WindowContext toolbar = new WindowContext(WINDOW_TITLE_TOOLBAR, REGION_NAME_TOOLBAR, TOOLBAR_ORDER, result);
                    rendition.getPageResult().addWindowContext(toolbar);

                    Region region = rendition.getPageResult().getRegion2(REGION_NAME_TOOLBAR);
                    DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
                }

                // Toolbar settings
                String toolbarSettingsContent = this.injectToolbarSettings(renderPageCommand);
                if (toolbarSettingsContent != null) {
                    // Toolbar settings content save
                    command.getControllerContext().setAttribute(ControllerCommand.REQUEST_SCOPE, Constants.ATTR_TOOLBAR_SETTINGS_CONTENT,
                            toolbarSettingsContent);
                }
            }
        }

        return response;
    }

    /**
     * Toolbar injection.
     * 
     * @param command page command
     * @return toolbar HTML content
     * @throws Exception
     * @throws CMSException
     */
    private String injectToolbar(PageCommand command) throws CMSException, Exception {
        ControllerContext context = command.getControllerContext();
        ControllerRequestDispatcher dispatcher = context.getRequestDispatcher(getTargetThemeContextPath(command), this.toolbarPath);

        if (dispatcher != null) {
            Page page = command.getPage();
            
            Principal principal = context.getServerInvocation().getServerContext().getClientRequest().getUserPrincipal();
            dispatcher.setAttribute(Constants.ATTR_TOOLBAR_PRINCIPAL, principal);

            if (principal == null) {
                PortalURL portalURL = null;

                // Redirection vers mon espace
                MonEspaceCommand monEspaceCommand = new MonEspaceCommand();
                portalURL = new PortalURLImpl(monEspaceCommand, context, Boolean.TRUE, null);
                dispatcher.setAttribute(Constants.ATTR_TOOLBAR_LOGIN_URL, portalURL);
            }

            // Mode d'édition
            String mode = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, Constants.ATTR_WINDOWS_SETTING_MODE);

            if (PageCustomizerInterceptor.isAdministrator(context)) {
                try {
                    PageType pageType = PageType.getPageType(page, context);
                    
                    // Editable page indicator
                    Boolean editablePage = PageType.STATIC_PAGE.equals(pageType);
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_EDITABLE_PAGE, editablePage);

                    ViewPageCommand showAdmin = new ViewPageCommand(adminPortalId);
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_ADMIN_PORTAL_URL, new PortalURLImpl(showAdmin, context, null, null));

                    // Préparation du lien de retour en mode normal
                    String newMode;
                    if (MODE_WIZARD.equals(mode)) {
                        newMode = MODE_NORMAL;
                    } else {
                        newMode = MODE_WIZARD;
                    }
                    ChangeModeCommand changeModeCommand = new ChangeModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), newMode);
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_WIZARD_URL, new PortalURLImpl(changeModeCommand, context, null, null));
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_WIZARD_MODE, newMode);

                    if (PageType.TEMPLATE_PAGE.equals(pageType))  {
                        // Page template access
                        ITemplatePortalObject templatePortalObject = (ITemplatePortalObject) page;
                        ViewPageCommand showPage = new ViewPageCommand(templatePortalObject.getTemplate().getId());
                        String url = new PortalURLImpl(showPage, context, null, null).toString();
                        url += "?init-state=true&edit-template-mode=true";
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_TEMPLATE_ACCESS_URL, url);
                    }

                    // Caches initialization
                    ViewPageCommand initCacheCmd = new ViewPageCommand(page.getId());
                    String url = new PortalURLImpl(initCacheCmd, context, null, null).toString();
                    url += "?init-cache=true";
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_CACHES_INIT_URL, url);
                } catch (PortalSecurityException e) {
                    logger.error(StringUtils.EMPTY, e);
                }
            }

            if (CMSEditionPageCustomizerInterceptor.checkWritePermission(context, page)) {
                    String cmsMode = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, Constants.ATTR_TOOLBAR_CMS_EDITION_MODE);

                    String newCmsMode;
                    if (MODE_PREVIEW.equals(cmsMode)) {
                        newCmsMode = MODE_NORMAL;
                    } else {
                        newCmsMode = MODE_PREVIEW;
                    }
                    ChangeCMSEditionModeCommand changeCmsModeCommand = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT),
                            newCmsMode);
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_CMS_EDITION_URL, new PortalURLImpl(changeCmsModeCommand, context, null, null));
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_CMS_EDITION_MODE, cmsMode);
            }


            RefreshPageCommand refreshCmd = new RefreshPageCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT));
            dispatcher.setAttribute(Constants.ATTR_TOOLBAR_REFRESH_PAGE_URL, new PortalURLImpl(refreshCmd, context, false, null));
          
            // Sign out
            SignOutCommand signOutCommand = new SignOutCommand();
            dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SIGN_OUT_URL, new PortalURLImpl(signOutCommand, context, false, null));

            // Redirection vers mon espace
            if (principal != null) {
                MonEspaceCommand monEspaceCommand = new MonEspaceCommand();
                PortalURL portalURL = new PortalURLImpl(monEspaceCommand, context, true, null);
                dispatcher.setAttribute(Constants.ATTR_TOOLBAR_MON_ESPACE_URL, portalURL);
            }

            if (MODE_WIZARD.equals(mode)) {
                dispatcher.setAttribute(Constants.ATTR_TOOLBAR_WIZARD_MODE, Constants.VALUE_TOOLBAR_WIZARD_MODE);
            }

            dispatcher.include();
            return dispatcher.getMarkup();
        }

        return null;
    }

    /**
     * Toolbar settings injection.
     * 
     * @param command page command
     * @return HTML content
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private String injectToolbarSettings(PageCommand command) throws Exception {
        ControllerContext context = command.getControllerContext();
        ControllerRequestDispatcher dispatcher = context.getRequestDispatcher(this.getTargetSettingsContextPath(), this.toolbarSettingsPath);

        if (dispatcher != null) {
            Page page = command.getPage();

            if (PageCustomizerInterceptor.isAdministrator(context)) {
                try {
                    PageType pageType = PageType.getPageType(page, context);
                    
                    URLContext urlContext = context.getServerInvocation().getServerContext().getURLContext();

                    // Formatter
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_FORMATTER, this);

                    // Controller context
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CONTROLLER_CONTEXT, context);

                    // Generic command URL
                    String serverContext = context.getServerInvocation().getServerContext().getPortalContextPath();
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_COMMAND_URL, serverContext + "/commands");

                    // Page
                    dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_PAGE, page);

                    if (PageType.STATIC_PAGE.equals(pageType)) {
                        // Default page indicator
                        Boolean defaultPage = page.getName().equals(page.getPortal().getDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME));
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_DEFAULT_PAGE, defaultPage);

                        // Draft page indicator
                        Boolean draftPage = "1".equals(page.getDeclaredProperty("osivia.draftPage"));
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_DRAFT_PAGE, draftPage);

                        // Layouts
                        List<PortalLayout> layouts = new ArrayList<PortalLayout>(this.layoutService.getLayouts());
                        Collections.sort(layouts, new PortalLayoutComparator());
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_LAYOUTS_LIST, layouts);

                        // Current layout
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CURRENT_LAYOUT, page.getDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT));

                        // Themes
                        List<PortalTheme> themes = new ArrayList<PortalTheme>(this.themeService.getThemes());
                        Collections.sort(themes, new PortalThemeComparator());
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_THEMES_LIST, themes);

                        // Current theme
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CURRENT_THEME, page.getDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME));

                        // Siblings pages
                        SortedSet<Page> siblings = new TreeSet<Page>(PageUtils.orderComparator);
                        for (PortalObject po : page.getParent().getChildren()) {
                            if (po instanceof Page) {
                                Page sibling = (Page) po;
                                if (!sibling.equals(page)) {
                                    siblings.add(sibling);
                                }
                            }

                        }
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_SIBLINGS_PAGES, siblings);

                        // Roles
                        List<Role> roles = this.profilManager.getFilteredRoles();
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_ROLES, roles);

                        // Actions for roles
                        DomainConfigurator domainConfigurator = this.authorizationDomainRegistry.getDomain("portalobject").getConfigurator();
                        Set<RoleSecurityBinding> constraints = domainConfigurator.getSecurityBindings(page.getId().toString(PortalObjectPath.CANONICAL_FORMAT));
                        Map<String, Set<String>> actionsForRoles = new HashMap<String, Set<String>>();
                        for (RoleSecurityBinding roleSecurityBinding : constraints) {
                            actionsForRoles.put(roleSecurityBinding.getRoleName(), roleSecurityBinding.getActions());
                        }
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_ACTIONS_FOR_ROLES, actionsForRoles);

                        // Page suppression
                        DeletePageCommand deletePageCommand = new DeletePageCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT));
                        String deletePageCommandUrl = context.renderURL(deletePageCommand, urlContext, URLFormat.newInstance(true, true));
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_DELETE_PAGE_COMMAND_URL, deletePageCommandUrl);

                        // CMS scope select
                        String scope = page.getDeclaredProperty("osivia.cms.scope");
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CMS_SCOPE_SELECT, this.formatScopeList(page, "scope", scope));

                        // CMS display live version
                        CMSServiceCtx cmsServiceCtx = new CMSServiceCtx();
                        cmsServiceCtx.setControllerContext(context);
                        String displayLiveVersion = page.getDeclaredProperty("osivia.cms.displayLiveVersion");
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CMS_DISPLAY_LIVE_VERSION,
                                this.formatDisplayLiveVersionList(cmsServiceCtx, page, "displayLiveVersion", displayLiveVersion));

                        // CMS recontextualization support
                        String outgoingRecontextualizationSupport = page.getDeclaredProperty("osivia.cms.outgoingRecontextualizationSupport");
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CMS_RECONTEXTUALIZATION_SUPPORT, this.formatInheritedCheckVakue(page,
                                "outgoingRecontextualizationSupport", "osivia.cms.outgoingRecontextualizationSupport", outgoingRecontextualizationSupport));

                        // CMS base path
                        String pageCmsBasePath = page.getDeclaredProperty("osivia.cms.basePath");
                        if (pageCmsBasePath == null) {
                            pageCmsBasePath = StringUtils.EMPTY;
                        }
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CMS_BASE_PATH, pageCmsBasePath);

                        // CMS navigation mode
                        String navigationMode = page.getDeclaredProperty("osivia.navigationMode");
                        Boolean cmsNavigationMode = "cms".equals(navigationMode);
                        dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SETTINGS_CMS_NAVIGATION_MODE, cmsNavigationMode);
                    }
                } catch (PortalSecurityException e) {
                    logger.error(StringUtils.EMPTY, e);
                }

                dispatcher.include();
                return dispatcher.getMarkup();
            }
        }

        return null;
    }

    /**
     * Utility method used to access the target theme context path.
     * 
     * @param portalCommand command
     * @return the target theme context path
     */
    private static String getTargetThemeContextPath(PortalCommand portalCommand) {
        Page page = null;
        if (portalCommand instanceof PageCommand) {
            page = ((PageCommand) portalCommand).getPage();
        }

        if (portalCommand instanceof WindowCommand) {
            page = ((WindowCommand) portalCommand).getPage();
        }
        if (page == null) {
            throw new IllegalArgumentException("target path not accessible");
        }

        String themeId = page.getProperty(ThemeConstants.PORTAL_PROP_THEME);
        PageService pageService = portalCommand.getControllerContext().getController().getPageService();
        ThemeService themeService = pageService.getThemeService();
        PortalTheme theme = themeService.getThemeById(themeId);
        return theme.getThemeInfo().getContextPath();
    }


    /**
     * Getter for toolbarPath.
     * 
     * @return the toolbarPath
     */
    public String getToolbarPath() {
        return this.toolbarPath;
    }

    /**
     * Setter for toolbarPath.
     * 
     * @param toolbarPath the toolbarPath to set
     */
    public void setToolbarPath(String toolbarPath) {
        this.toolbarPath = toolbarPath;
    }

    /**
     * Getter for toolbarSettingsPath.
     * 
     * @return the toolbarSettingsPath
     */
    public String getToolbarSettingsPath() {
        return this.toolbarSettingsPath;
    }

    /**
     * Setter for toolbarSettingsPath.
     * 
     * @param toolbarSettingsPath the toolbarSettingsPath to set
     */
    public void setToolbarSettingsPath(String toolbarSettingsPath) {
        this.toolbarSettingsPath = toolbarSettingsPath;
    }

    /**
     * Getter for urlFactory.
     * 
     * @return the urlFactory
     */
    public IPortalUrlFactory getUrlFactory() {
        return this.urlFactory;
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
     * Getter for targetSettingsContextPath.
     * 
     * @return the targetSettingsContextPath
     */
    public String getTargetSettingsContextPath() {
        return this.targetSettingsContextPath;
    }

    /**
     * Setter for targetSettingsContextPath.
     * 
     * @param targetSettingsContextPath the targetSettingsContextPath to set
     */
    public void setTargetSettingsContextPath(String targetSettingsContextPath) {
        this.targetSettingsContextPath = targetSettingsContextPath;
    }

}
