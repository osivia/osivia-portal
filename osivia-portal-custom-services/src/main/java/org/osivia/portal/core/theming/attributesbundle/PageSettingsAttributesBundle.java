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
package org.osivia.portal.core.theming.attributesbundle;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.Role;
import org.jboss.portal.security.AuthorizationDomainRegistry;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.ecm.EcmViews;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.taskbar.TaskbarItem;
import org.osivia.portal.api.taskbar.TaskbarItemType;
import org.osivia.portal.api.taskbar.TaskbarItems;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.core.assistantpage.MoveWindowCommand;
import org.osivia.portal.core.assistantpage.PortalLayoutComparator;
import org.osivia.portal.core.assistantpage.PortalThemeComparator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.DynamicWindow;
import org.osivia.portal.core.portalobjects.PortalObjectNameComparator;
import org.osivia.portal.core.portalobjects.PortalObjectOrderComparator;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;

/**
 * Page settings attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class PageSettingsAttributesBundle implements IAttributesBundle {

    /** Windows settings fancyboxes prefix. */
    private static final String PREFIX_ID_FANCYBOX_WINDOW_SETTINGS = "window-settings-";
    /** Layout or theme excluded names. */
    private static final String[] EXCLUDED_NAMES = new String[]{"osivia-modal", "osivia-popup", "generic", "3columns", "1column", "renaissance", "industrial",
            "maple", "renewal"};

    /** Singleton instance. */
    private static PageSettingsAttributesBundle instance;


    /** Layout service. */
    private final LayoutService layoutService;
    /** Theme service. */
    private final ThemeService themeService;
    /** Profile manager. */
    private final IProfilManager profileManager;
    /** Authorization domain registry. */
    private final AuthorizationDomainRegistry authorizationDomainRegistry;
    /** Formatter. */
    private final IFormatter formatter;
    /** Portal object container. */
    private final PortalObjectContainer portalObjectContainer;
    /** Portal authorization manager factory. */
    private final PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;
    /** Instance container. */
    private final InstanceContainer instanceContainer;
    /** Taskbar service. */
    private final ITaskbarService taskbarService;
    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Bundle factory. */
    private final IBundleFactory bundleFactory;

    /** Toolbar attributes names. */
    private final Set<String> names;

    /** Layouts and themes collection filter predicate. */
    private final Predicate predicate;


    /**
     * Private constructor.
     */
    private PageSettingsAttributesBundle() {
        super();

        // Layout service
        this.layoutService = Locator.findMBean(LayoutService.class, "portal:service=LayoutService");
        // Theme service
        this.themeService = Locator.findMBean(ThemeService.class, "portal:service=ThemeService");
        // Profile manager
        this.profileManager = Locator.findMBean(IProfilManager.class, "osivia:service=ProfilManager");
        // Authorization domain registry
        this.authorizationDomainRegistry = Locator.findMBean(AuthorizationDomainRegistry.class, "portal:service=AuthorizationDomainRegistry");
        // Formatter
        this.formatter = Locator.findMBean(IFormatter.class, "osivia:service=Interceptor,type=Command,name=AssistantPageCustomizer");
        // Portal object container
        this.portalObjectContainer = Locator.findMBean(PortalObjectContainer.class, "portal:container=PortalObject");
        // Portal authorization manager factory
        this.portalAuthorizationManagerFactory = Locator.findMBean(PortalAuthorizationManagerFactory.class, "portal:service=PortalAuthorizationManagerFactory");
        // Instance container
        this.instanceContainer = Locator.findMBean(InstanceContainer.class, "portal:container=Instance");
        // Taskbar service
        this.taskbarService = Locator.findMBean(ITaskbarService.class, ITaskbarService.MBEAN_NAME);
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        // Bundle factory
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());

        // Attribute names
        this.names = new TreeSet<String>();
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_TEMPLATED);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_DRAFT_PAGE);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_SELECTORS_PROPAGATION);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE_CUR_CATEGORY);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE_CATEGORIES);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_LAYOUTS_LIST);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_LAYOUT);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_THEMES_LIST);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_THEME);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_ROLES);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_ACTIONS_FOR_ROLES);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_SCOPE_SELECT);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_DISPLAY_LIVE_VERSION);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_RECONTEXTUALIZATION_SUPPORT);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_BASE_PATH);
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_WINDOW_SETTINGS);
        this.names.add("osivia.settings.elements");
        this.names.add("osivia.session.reload.url");
        
        // Layouts and themes collection filter predicate
        this.predicate = new LayoutsAndThemesPredicate();
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static PageSettingsAttributesBundle getInstance() {
        if (instance == null) {
            instance = new PageSettingsAttributesBundle();
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

        if (PageCustomizerInterceptor.isAdministrator(controllerContext)) {
            // Fill toolbar attributes
            this.fillToolbarAttributes(renderPageCommand, attributes);

            // Fill page attributes
            this.fillPageAttributes(renderPageCommand, pageRendition, attributes);
        }

        // Reload session URL
        this.fillSessionReloadUrl(controllerContext, attributes);
    }


    /**
     * Utility method used to fill toolbar attributes.
     *
     * @param renderPageCommand render page command
     * @param attributes attributes map
     */
    @SuppressWarnings("unchecked")
    private void fillToolbarAttributes(RenderPageCommand renderPageCommand, Map<String, Object> attributes) {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        // Server context
        ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();

        // Bundle
        Locale locale = serverContext.getClientRequest().getLocale();
        Bundle bundle = this.bundleFactory.getBundle(locale);

        // Current page
        Page page = renderPageCommand.getPage();
        // Current page type
        PageType pageType = PageType.getPageType(page, controllerContext);
        // Templated page indicator
        Boolean templated = pageType.isTemplated();
        if (PageType.CMS_TEMPLATED_PAGE.equals(pageType)) {
            page = (Page) page.getParent();
            pageType = PageType.getPageType(page, controllerContext);
        }

        // is current user administrator ?
        Boolean administrator = PageCustomizerInterceptor.isAdministrator(renderPageCommand.getControllerContext());
        attributes.put(InternalConstants.ATTR_USER_ADMIN, administrator);


        // CMS templated page indicator
        attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_TEMPLATED, templated);

        if (!PageType.DYNAMIC_PAGE.equals(pageType)) {
            // Draft page indicator
            Boolean draftPage = "1".equals(page.getDeclaredProperty("osivia.draftPage"));
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_DRAFT_PAGE, draftPage);

            // Selectors propagation page indicator
            Boolean selectorsPropagation = "1".equals(page.getDeclaredProperty("osivia.cms.propagateSelectors"));
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_SELECTORS_PROPAGATION, selectorsPropagation);


            // categories (optional)
            String pageCategoryPrefix = System.getProperty(InternalConstants.SYSTEM_PROPERTY_PAGE_CATEGORY_PREFIX);

            if( pageCategoryPrefix != null) {

                String category = page.getDeclaredProperty("osivia.pageCategory");
                if( category == null) {
                    category = "";
                }

                attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE_CUR_CATEGORY, category);

                Map<String, String> categories = new LinkedHashMap<String, String>();


                categories.put("", bundle.getString(InternationalizationConstants.KEY_PAGE_NO_CATEGORY));

                TreeSet<OrderedPageCategory> orderedCategories = new TreeSet<OrderedPageCategory>();

                Properties properties = System.getProperties();
                Enumeration<Object>props = properties.keys();
                while(props.hasMoreElements()){

                    String key = (String) props.nextElement();

                    if( key.startsWith(pageCategoryPrefix)) {
                        String curCategory = key.substring(pageCategoryPrefix.length());

                        int curOrder = 100;

                        try {
                         curOrder = Integer.parseInt(curCategory);
                        } catch( NumberFormatException e)   {
                            // non orderable
                        }
                        String curLabel = (String) properties.get( key);

                        orderedCategories.add(new OrderedPageCategory(curOrder, curCategory, curLabel));

                    }
                }


                for(OrderedPageCategory pageCategory : orderedCategories)   {
                    categories.put(pageCategory.getCode(),pageCategory.getLabel());
                }


                attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE_CATEGORIES, categories);
            }


            // Layouts
            List<PortalLayout> layouts = new ArrayList<PortalLayout>(this.layoutService.getLayouts());
            CollectionUtils.filter(layouts, this.predicate);
            Collections.sort(layouts, new PortalLayoutComparator());
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_LAYOUTS_LIST, layouts);

            // Current layout
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_LAYOUT, page.getDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT));

            // Themes
            List<PortalTheme> themes = new ArrayList<PortalTheme>(this.themeService.getThemes());
            CollectionUtils.filter(themes, this.predicate);
            Collections.sort(themes, new PortalThemeComparator());
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_THEMES_LIST, themes);

            // Current theme
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_THEME, page.getDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME));

            // Roles
            List<Role> roles = this.profileManager.getFilteredRoles();
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_ROLES, roles);

            // Actions for roles
            DomainConfigurator domainConfigurator = this.authorizationDomainRegistry.getDomain("portalobject").getConfigurator();
            Set<RoleSecurityBinding> constraints = domainConfigurator.getSecurityBindings(page.getId().toString(PortalObjectPath.CANONICAL_FORMAT));
            Map<String, Set<String>> actionsForRoles = new HashMap<String, Set<String>>();
            if (CollectionUtils.isNotEmpty(constraints)) {
                for (RoleSecurityBinding roleSecurityBinding : constraints) {
                    actionsForRoles.put(roleSecurityBinding.getRoleName(), roleSecurityBinding.getActions());
                }
            }
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_ACTIONS_FOR_ROLES, actionsForRoles);


            // CMS scope select
            String scope = page.getDeclaredProperty(Constants.WINDOW_PROP_SCOPE);
            try {
                attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_SCOPE_SELECT, this.formatter.formatScopeList(page, "scope", scope));
            } catch (Exception e) {
                // Do nothing
            }

            // CMS display live version
            CMSServiceCtx cmsServiceCtx = new CMSServiceCtx();
            cmsServiceCtx.setControllerContext(controllerContext);
            String displayLiveVersion = page.getDeclaredProperty(Constants.WINDOW_PROP_VERSION);
            try {
                attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_DISPLAY_LIVE_VERSION,
                        this.formatter.formatDisplayLiveVersionList(cmsServiceCtx, page, "displayLiveVersion", displayLiveVersion));
            } catch (Exception e) {
                // Do nothing
            }

            // CMS recontextualization support
            String outgoingRecontextualizationSupport = page.getDeclaredProperty("osivia.cms.outgoingRecontextualizationSupport");
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_RECONTEXTUALIZATION_SUPPORT, this.formatInheritedCheckVakue(page,
                    "outgoingRecontextualizationSupport", "osivia.cms.outgoingRecontextualizationSupport", outgoingRecontextualizationSupport));

            // CMS base path
            String pageCmsBasePath = page.getDeclaredProperty("osivia.cms.basePath");
            if (pageCmsBasePath == null) {
                pageCmsBasePath = StringUtils.EMPTY;
            }
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_BASE_PATH, pageCmsBasePath);
        }
    }


    /**
     * Utility method used to format inherited check value.
     *
     * @param po portal object
     * @param selectName select name
     * @param propertyName property name
     * @param selectedValue selected value
     * @return formatted select
     */
    private String formatInheritedCheckVakue(PortalObject po, String selectName, String propertyName, String selectedValue) {
        Map<String, String> supportedValue = new LinkedHashMap<String, String>();

        supportedValue.put("0", "Non");
        supportedValue.put("1", "Oui");

        StringBuffer select = new StringBuffer();

        String disabled = "";
        if (StringUtils.isNotEmpty(po.getDeclaredProperty("osivia.cms.basePath"))) {
            disabled = "disabled='disabled'";
        }

        select.append("<select id=\"cms-contextualization\" name=\"" + selectName + "\" class=\"form-control\" " + disabled + ">");

        if (!supportedValue.isEmpty()) {
            // Héritage
            String parentScope = po.getParent().getProperty(propertyName);
            String inheritedLabel = null;
            if (parentScope != null) {
                inheritedLabel = supportedValue.get(parentScope);
            }
            if (inheritedLabel == null) {
                inheritedLabel = "Non";
            }
            inheritedLabel = "Herité [" + inheritedLabel + "]";

            if ((selectedValue == null) || (selectedValue.length() == 0)) {
                select.append("<option selected=\"selected\" value=\"\">" + inheritedLabel + "</option>");
            } else {
                select.append("<option value=\"\">" + inheritedLabel + "</option>");
            }
            for (Entry<String, String> entry : supportedValue.entrySet()) {
                if ((selectedValue != null) && (selectedValue.length() != 0) && selectedValue.equals(entry.getKey())) {
                    select.append("<option selected=\"selected\" value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>");
                } else {
                    select.append("<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>");
                }
            }
        }

        select.append("</select>");

        return select.toString();
    }


    /**
     * Utility method used to fill toolbar attributes.
     *
     * @param renderPageCommand render page command
     * @param pageRendition page rendition
     * @param attributes attributes map
     */
    @SuppressWarnings("unchecked")
    private void fillPageAttributes(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        // Server context
        ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();

        // Bundle
        Locale locale = serverContext.getClientRequest().getLocale();
        Bundle bundle = this.bundleFactory.getBundle(locale);

        // Current page
        Page page = renderPageCommand.getPage();
        // Current page type
        PageType pageType = PageType.getPageType(page, controllerContext);

        Object windowSettingMode = controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_WINDOWS_SETTING_MODE);
        if (!pageType.isTemplated() && InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(windowSettingMode)) {
            List<Window> windows = new ArrayList<Window>();

            String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
            PortalLayout pageLayout = this.layoutService.getLayout(layoutId, true);

            this.synchronizeRegionContexts(pageRendition, page);

            for (Object regionCtxObjet : pageRendition.getPageResult().getRegions()) {
                RegionRendererContext renderCtx = (RegionRendererContext) regionCtxObjet;

                // On vérifie que cette région fait partie du layout (elle contient des portlets)
                if (pageLayout.getLayoutInfo().getRegionNames().contains(renderCtx.getId())) {
                    Map<String, String> regionProperties = renderCtx.getProperties();

                    PortalObjectId popupWindowId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                            "osivia.popupModeWindowID");

                    if (popupWindowId == null) {
                        regionProperties.put(InternalConstants.ATTR_WINDOWS_WIZARD_MODE, InternalConstants.VALUE_WINDOWS_WIZARD_TEMPLATE_MODE);
                        regionProperties.put(InternalConstants.ATTR_WINDOWS_ADD_PORTLET_URL, "#add-portlet");
                    }

                    // Le mode Ajax est incompatble avec le mode "admin".
                    // Le passage du mode admin en mode normal n'est pas bien géré par le portail, quand il s'agit d'une requête Ajax.
                    DynaRenderOptions.NO_AJAX.setOptions(regionProperties);
                    for (Object windowCtx : renderCtx.getWindows()) {

                        WindowRendererContext wrc = (WindowRendererContext) windowCtx;
                        Map<String, String> windowProperties = wrc.getProperties();
                        String windowId = wrc.getId();

                        if (!windowId.endsWith("PIA_EMPTY") && !WindowState.MAXIMIZED.equals (wrc.getWindowState())) {
                            URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
                            PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
                            DynamicWindow window = (DynamicWindow) this.portalObjectContainer.getObject(poid);

                            if (!window.isSessionWindow() && (popupWindowId == null)) {
                                // Window settings mode
                                windowProperties.put(InternalConstants.ATTR_WINDOWS_SETTING_MODE, InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE);

                                // Commande suppression
                                windowProperties.put(InternalConstants.ATTR_WINDOWS_DELETE_PORTLET_URL, "#delete-portlet");

                                // Commande paramètres
                                windowProperties.put(InternalConstants.ATTR_WINDOWS_DISPLAY_SETTINGS_URL, "#" + PREFIX_ID_FANCYBOX_WINDOW_SETTINGS + windowId);

                                windows.add(window);

                                // Commandes de déplacement
                                MoveWindowCommand upC = new MoveWindowCommand(windowId, MoveWindowCommand.UP);
                                String upUrl = controllerContext.renderURL(upC, urlContext, URLFormat.newInstance(true, true));
                                windowProperties.put(InternalConstants.ATTR_WINDOWS_UP_COMMAND_URL, upUrl);

                                MoveWindowCommand downC = new MoveWindowCommand(windowId, MoveWindowCommand.DOWN);
                                String downUrl = controllerContext.renderURL(downC, urlContext, URLFormat.newInstance(true, true));
                                windowProperties.put(InternalConstants.ATTR_WINDOWS_DOWN_COMMAND_URL, downUrl);

                                MoveWindowCommand previousC = new MoveWindowCommand(windowId, MoveWindowCommand.PREVIOUS_REGION);
                                String previousRegionUrl = controllerContext.renderURL(previousC, urlContext, URLFormat.newInstance(true, true));
                                windowProperties.put(InternalConstants.ATTR_WINDOWS_PREVIOUS_REGION_COMMAND_URL, previousRegionUrl);

                                MoveWindowCommand nextRegionC = new MoveWindowCommand(windowId, MoveWindowCommand.NEXT_REGION);
                                String nextRegionUrl = controllerContext.renderURL(nextRegionC, urlContext, URLFormat.newInstance(true, true));
                                windowProperties.put(InternalConstants.ATTR_WINDOWS_NEXT_REGION_COMMAND_URL, nextRegionUrl);

                                // Admin window title
                                String instanceDisplayName = null;
                                InstanceDefinition defInstance = this.instanceContainer.getDefinition(window.getContent().getURI());
                                if (defInstance != null) {
                                    instanceDisplayName = defInstance.getDisplayName().getString(locale, true);
                                }
                                if (instanceDisplayName != null) {
                                    windowProperties.put(InternalConstants.ATTR_WINDOWS_INSTANCE_DISPLAY_NAME, instanceDisplayName);

                                }
                            }
                        }
                    }
                }
            }


            // Window settings
            List<WindowSettings> windowSettings = this.getWindowSettings(controllerContext, bundle, page.getPortal(), windows);
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_WINDOW_SETTINGS, windowSettings);
        }


        // Elements list
        String elements = this.generateElementsTree(page, controllerContext);
        attributes.put("osivia.settings.elements", elements);

        // Models
        String models = this.generateModelsTree(page, controllerContext);
        attributes.put("osivia.settings.models", models);

        // Page parents
        String pageParents = this.generatePageParentsTree(page, controllerContext);
        attributes.put("osivia.settings.pageParents", pageParents);

        // Template parents
        String templateParents = this.generateTemplateParentsTree(page, controllerContext);
        attributes.put("osivia.settings.templateParents", templateParents);

        // Locations
        String locations = this.generateLocationsTree(page, controllerContext);
        attributes.put("osivia.settings.locations", locations);
    }


    /**
     * Synchronize context regions with layout.
     * if a region is not present in the context, creates a new one.
     *
     * @param rendition page rendition
     * @param page current page
     */
    private void synchronizeRegionContexts(PageRendition rendition, Page page) {
        String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        PortalLayout layout = this.layoutService.getLayout(layoutId, true);

        for (Object region : layout.getLayoutInfo().getRegionNames()) {
            String regionName = (String) region;
            RegionRendererContext renderCtx = rendition.getPageResult().getRegion(regionName);
            if (renderCtx == null) {
                // Empty region - must create blank window
                Map<String, String> windowProps = new HashMap<String, String>();
                windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
                windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
                windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");

                WindowResult wr = new WindowResult("PIA_EMPTY", "", Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
                WindowContext settings = new WindowContext(regionName + "_PIA_EMPTY", regionName, "0", wr);
                rendition.getPageResult().addWindowContext(settings);

                renderCtx = rendition.getPageResult().getRegion2(regionName);
            }
        }
    }


    /**
     * Generate models tree.
     *
     * @param currentPage current page
     * @param controllerContext controller context
     * @return models tree
     */
    private String generateModelsTree(Page currentPage, ControllerContext controllerContext) {
        // Portal
        Portal portal = currentPage.getPortal();

        // UL
        Element ul = DOM4JUtils.generateElement(HTMLConstants.UL, null, StringUtils.EMPTY);

        // Children
        Comparator<PortalObject> comparator = PortalObjectOrderComparator.getInstance();
        Collection<Page> children = this.getPageChildren(controllerContext, portal, comparator, null);
        for (Page child : children) {
            ul.add(this.generateTreeNode(null, controllerContext, child, comparator, true, null, false));
        }

        return DOM4JUtils.write(ul);
    }


    /**
     * Generate page parents tree.
     *
     * @param currentPage current page
     * @param controllerContext controller context
     * @return template parents tree
     */
    private String generatePageParentsTree(Page currentPage, ControllerContext controllerContext) {
        // Locale
        Locale locale = controllerContext.getServerInvocation().getServerContext().getClientRequest().getLocale();
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(locale);

        // Portal
        Portal portal = currentPage.getPortal();
        // Portal identifier
        String portalId;
        try {
            portalId = URLEncoder.encode(portal.getId().toString(PortalObjectPath.SAFEST_FORMAT), CharEncoding.UTF_8);
        } catch (IOException e) {
            portalId = null;
        }

        // Root container
        Element rootContainer = DOM4JUtils.generateElement(HTMLConstants.UL, null, null);

        // Root
        Element root = DOM4JUtils.generateElement(HTMLConstants.LI, "active", bundle.getString("ROOT_NODE"));
        DOM4JUtils.addDataAttribute(root, "path", portalId);
        // DOM4JUtils.addDataAttribute(root, "selected", String.valueOf(true));
        DOM4JUtils.addDataAttribute(root, "folder", String.valueOf(true));
        DOM4JUtils.addDataAttribute(root, "expanded", String.valueOf(true));
        DOM4JUtils.addDataAttribute(root, "retain", String.valueOf(true));
        rootContainer.add(root);

        // UL
        Element ul = DOM4JUtils.generateElement(HTMLConstants.UL, null, StringUtils.EMPTY);
        root.add(ul);

        // Children
        Comparator<PortalObject> comparator = PortalObjectOrderComparator.getInstance();
        Collection<Page> children = this.getPageChildren(controllerContext, portal, comparator, false);
        for (Page child : children) {
            ul.add(this.generateTreeNode(null, controllerContext, child, comparator, false, false, false));
        }

        return DOM4JUtils.write(rootContainer);
    }


    /**
     * Generate template parents tree.
     *
     * @param currentPage current page
     * @param controllerContext controller context
     * @return template parents tree
     */
    private String generateTemplateParentsTree(Page currentPage, ControllerContext controllerContext) {
        // Portal
        Portal portal = currentPage.getPortal();

        // UL
        Element ul = DOM4JUtils.generateElement(HTMLConstants.UL, null, StringUtils.EMPTY);

        // Children
        Comparator<PortalObject> comparator = PortalObjectOrderComparator.getInstance();
        Collection<Page> children = this.getPageChildren(controllerContext, portal, comparator, true);
        for (Page child : children) {
            ul.add(this.generateTreeNode(null, controllerContext, child, comparator, false, true, false));
        }

        return DOM4JUtils.write(ul);
    }


    /**
     * Generate elements tree.
     *
     * @param currentPage current page
     * @param controllerContext controller context
     * @return elements tree
     */
    private String generateElementsTree(Page currentPage, ControllerContext controllerContext) {
        // Locale
        Locale locale = controllerContext.getServerInvocation().getServerContext().getClientRequest().getLocale();

        // Portal
        Portal portal = currentPage.getPortal();

        // UL
        Element ul = DOM4JUtils.generateElement(HTMLConstants.UL, null, StringUtils.EMPTY);

        // Children
        Comparator<PortalObject> comparator = new PortalObjectNameComparator(locale);
        Collection<Page> children = this.getPageChildren(controllerContext, portal, comparator, null);
        for (Page child : children) {
            ul.add(this.generateTreeNode(currentPage, controllerContext, child, comparator, false, null, false));
        }

        return DOM4JUtils.write(ul);
    }


    /**
     * Generate locations tree.
     *
     * @param currentPage current page
     * @param controllerContext controller context
     * @return locations tree
     */
    private String generateLocationsTree(Page currentPage, ControllerContext controllerContext) {
        // Locale
        Locale locale = controllerContext.getServerInvocation().getServerContext().getClientRequest().getLocale();
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(locale);

        // Portal
        Portal portal = currentPage.getPortal();
        // Templates indicator
        boolean templates = PortalObjectUtils.isTemplate(currentPage);

        // UL
        Element ul = DOM4JUtils.generateElement(HTMLConstants.UL, null, StringUtils.EMPTY);

        // Children
        Comparator<PortalObject> comparator = PortalObjectOrderComparator.getInstance();
        Collection<Page> children = this.getPageChildren(controllerContext, portal, comparator, templates);
        for (Page child : children) {
            ul.add(this.generateTreeNode(currentPage, controllerContext, child, comparator, false, templates, true));
        }

        if (!templates) {
            // Virtual end node
            StringBuilder builder = new StringBuilder();
            try {
                builder.append(URLEncoder.encode(portal.getId().toString(PortalObjectPath.SAFEST_FORMAT), CharEncoding.UTF_8));
                builder.append(InternalConstants.SUFFIX_VIRTUAL_END_NODES_ID);
            } catch (IOException e) {
                // Do nothing
            }
            Element virtual = DOM4JUtils.generateElement(HTMLConstants.LI, null, bundle.getString("VIRTUAL_END_NODE"));
            DOM4JUtils.addDataAttribute(virtual, "path", builder.toString());
            DOM4JUtils.addDataAttribute(virtual, "iconclass", "glyphicons glyphicons-asterisk");
            ul.add(virtual);
        }

        return DOM4JUtils.write(ul);
    }


    /**
     * Generate fancytree node DOM4J element.
     *
     * @param currentPage current page, may be null
     * @param controllerContext controller context
     * @param page page
     * @param comparator portal object comparator, may be null
     * @param models models indicator
     * @param templates templates indicator, must be null for templates and non-templates union
     * @param virtualEndNode add virtual end node indicator
     * @return node DOM4J element
     */
    private Element generateTreeNode(Page currentPage, ControllerContext controllerContext, Page page, Comparator<PortalObject> comparator, boolean models,
            Boolean templates, boolean virtualEndNode) {
        // Locale
        Locale locale = controllerContext.getServerInvocation().getServerContext().getClientRequest().getLocale();
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(locale);

        // Title
        String title = PortalObjectUtils.getDisplayName(page, locale);

        // Page identifier
        String pageId;
        try {
            pageId = URLEncoder.encode(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), CharEncoding.UTF_8);
        } catch (IOException e) {
            pageId = null;
        }
        // Page type
        PageType pageType = PageType.getPageType(page, controllerContext);

        // URL
        ViewPageCommand showPage = new ViewPageCommand(page.getId());
        String url = new PortalURLImpl(showPage, controllerContext, null, null).toString();
        url += "?init-state=true";


        // LI
        StringBuilder extraClasses = new StringBuilder();
        Element li = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        DOM4JUtils.addDataAttribute(li, "path", pageId);
        if (PortalObjectUtils.isTemplate(page)) {
            if (page.getParent() instanceof Portal) {
                // Template root
                DOM4JUtils.addDataAttribute(li, "folder", String.valueOf(true));
                DOM4JUtils.addDataAttribute(li, "expanded", String.valueOf(true));
                DOM4JUtils.addDataAttribute(li, "retain", String.valueOf(true));

                if (BooleanUtils.isTrue(templates)) {
                    extraClasses.append("active ");
                }

                if (virtualEndNode) {
                    DOM4JUtils.addDataAttribute(li, "acceptable", String.valueOf(false));
                    extraClasses.append("text-muted not-allowed ");
                }
            } else {
                // Template
                DOM4JUtils.addDataAttribute(li, "iconclass", "glyphicons glyphicons-construction-cone");
            }
        } else if (pageType.isSpace()) {
            // Space
            DOM4JUtils.addDataAttribute(li, "iconclass", "glyphicons glyphicons-global");
        } else {
            // Page
            DOM4JUtils.addDataAttribute(li, "iconclass", "glyphicons glyphicons-more-items");
        }
        if (page.equals(currentPage)) {
            extraClasses.append("current ");
        } else if (PortalObjectUtils.isAncestor(page, currentPage)) {
            DOM4JUtils.addDataAttribute(li, "expanded", String.valueOf(true));
        }
        if (virtualEndNode && (page.equals(currentPage) || PortalObjectUtils.isAncestor(currentPage, page))) {
            DOM4JUtils.addDataAttribute(li, "acceptable", String.valueOf(false));
            extraClasses.append("text-muted not-allowed ");
        }


        // Link
        Element link = DOM4JUtils.generateLinkElement(url, null, null, null, title);
        li.add(link);

        if (pageType.isPortalPage() && !page.getName().equals(title)) {
            // Sub-title
            Element subtitle = DOM4JUtils.generateElement(HTMLConstants.SMALL, null, "(" + page.getName() + ")");
            link.add(subtitle);
        }


        // UL
        Element ul = DOM4JUtils.generateElement(HTMLConstants.UL, null, StringUtils.EMPTY);
        li.add(ul);

        // Children
        if (!(virtualEndNode && pageType.isSpace())) {
            Collection<Page> children = this.getPageChildren(controllerContext, page, comparator, templates);
            if (models && !children.isEmpty()) {
                DOM4JUtils.addDataAttribute(li, "acceptable", String.valueOf(false));
                extraClasses.append("text-muted not-allowed ");
            }
            for (Page child : children) {
                ul.add(this.generateTreeNode(currentPage, controllerContext, child, comparator, models, templates, virtualEndNode));
            }

            if (virtualEndNode && !page.equals(currentPage) && !PortalObjectUtils.isAncestor(currentPage, page)) {
                // Virtual end node
                Element virtual = DOM4JUtils.generateElement(HTMLConstants.LI, null, bundle.getString("VIRTUAL_END_NODE"));
                DOM4JUtils.addDataAttribute(virtual, "path", pageId + InternalConstants.SUFFIX_VIRTUAL_END_NODES_ID);
                DOM4JUtils.addDataAttribute(virtual, "iconclass", "glyphicons glyphicons-asterisk");
                ul.add(virtual);
            }
        }

        DOM4JUtils.addAttribute(li, HTMLConstants.CLASS, extraClasses.toString());

        return li;
    }


    /**
     * Get portal object page children.
     *
     * @param controllerContext controller context
     * @param parent parent portal object
     * @param comparator portal objects comparator, may be null
     * @param templates templates indicator, must be null for templates and non-templates union
     * @return page children
     */
    private Collection<Page> getPageChildren(ControllerContext controllerContext, PortalObject parent, Comparator<PortalObject> comparator, Boolean templates) {
        // Portal authorization manager
        PortalAuthorizationManager authorizationManager = this.portalAuthorizationManagerFactory.getManager();

        // Pages
        Collection<Page> pages;
        if (comparator != null) {
            pages = new TreeSet<Page>(comparator);
        } else {
            pages = new ArrayList<Page>();
        }

        // Parent children
        Collection<PortalObject> children = parent.getChildren(PortalObject.PAGE_MASK);
        for (PortalObject child : children) {
            // Permission
            PortalObjectPermission permission = new PortalObjectPermission(child.getId(), PortalObjectPermission.VIEW_MASK);
            if (authorizationManager.checkPermission(permission)) {
                Page page = (Page) child;
                PageType pageType = PageType.getPageType(page, controllerContext);

                if (!pageType.isTemplated() && (BooleanUtils.isNotTrue(templates) || PortalObjectUtils.isTemplate(page))
                        && (BooleanUtils.isNotFalse(templates) || !PortalObjectUtils.isTemplate(page))) {
                    pages.add(page);
                }
            }
        }

        return pages;
    }


    /**
     * Get window settings.
     *
     * @param bundle internationalization bundle
     * @param portal portal
     * @param windows windows
     * @return window settings
     */
    private List<WindowSettings> getWindowSettings(ControllerContext controllerContext, Bundle bundle, Portal portal, List<Window> windows) {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

        // Profiles
        List<ProfilBean> profiles = this.profileManager.getListeProfils();

        // Taskbar items
        TaskbarItems taskbarItems;
        try {
            taskbarItems = this.taskbarService.getItems(portalControllerContext);
        } catch (PortalException e) {
            taskbarItems = null;
        }


        // Window settings
        List<WindowSettings> windowSettings = new ArrayList<WindowSettings>(windows.size());

        for (Window window : windows) {
            // Identifier
            String id = window.getId().toString(PortalObjectPath.SAFEST_FORMAT);
            WindowSettings settings = new WindowSettings(id);
            windowSettings.add(settings);

            // Title
            String title = StringUtils.trimToEmpty(window.getDeclaredProperty("osivia.title"));
            boolean displayTitle = !StringUtils.equals("1", window.getDeclaredProperty("osivia.hideTitle"));
            boolean displayTitleDecorators = !"1".equals(window.getDeclaredProperty("osivia.hideDecorators"));
            boolean maximizedToCms = BooleanUtils.toBoolean(window.getDeclaredProperty(Constants.WINDOW_PROP_MAXIMIZED_CMS_URL));
            settings.setTitle(title);
            settings.setDisplayTitle(displayTitle);
            settings.setDisplayTitleDecorators(displayTitleDecorators);
            settings.setMaximizedToCms(maximizedToCms);

            // Panel
            boolean displayPanel = BooleanUtils.toBoolean(window.getDeclaredProperty("osivia.bootstrapPanelStyle"));
            boolean panelCollapse = BooleanUtils.toBoolean(window.getDeclaredProperty("osivia.mobileCollapse"));
            settings.setDisplayPanel(displayPanel);
            settings.setPanelCollapse(panelCollapse);

            // Ajax
            boolean ajax = "1".equals(window.getProperty("osivia.ajaxLink"));
            settings.setAjax(ajax);

            // Hide empty portlet
            boolean hideEmpty = "1".equals(window.getProperty("osivia.hideEmptyPortlet"));
            settings.setHideEmpty(hideEmpty);

            // Print
            boolean print = "1".equals(window.getProperty("osivia.printPortlet"));
            settings.setPrint(print);

            // Styles
            String portalStylesProperty = StringUtils.trimToEmpty(portal.getDeclaredProperty("osivia.liste_styles"));
            List<String> portalStyles = Arrays.asList(StringUtils.split(portalStylesProperty, ","));
            String selectedStylesProperty = StringUtils.trimToEmpty(window.getDeclaredProperty("osivia.style"));
            List<String> selectedStyles = Arrays.asList(StringUtils.split(selectedStylesProperty, ","));
            Map<String, Boolean> styles = settings.getStyles();
            for (String portalStyle : portalStyles) {
                boolean selected = selectedStyles.contains(portalStyle);
                styles.put(portalStyle, selected);
            }

            // Scopes
            String selectedScope = window.getProperty("osivia.conditionalScope");
            Map<String, String> scopes = settings.getScopes();
            if (CollectionUtils.isNotEmpty(profiles)) {
                for (ProfilBean profile : profiles) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(bundle.getString("WINDOW_PROPERTIES_SCOPE_PROFILE"));
                    builder.append(" ");
                    builder.append(profile.getName());

                    scopes.put(profile.getName(), builder.toString());
                }
            }
            settings.setSelectedScope(selectedScope);

            // Linked taskbar item
            String taskId = window.getDeclaredProperty(ITaskbarService.LINKED_TASK_ID_WINDOW_PROPERTY);
            settings.setTaskbarItemId(taskId);
            if (taskbarItems != null) {
                for (TaskbarItem item : taskbarItems.getAll()) {
                    if (!TaskbarItemType.TRANSVERSAL.equals(item.getType())) {
                        String label = bundle.getString(item.getKey(), item.getCustomizedClassLoader());
                        settings.getTaskbarItems().put(label, item.getId());
                    }
                }
            }

            // Customization identifier
            String customizationId = window.getDeclaredProperty("osivia.idPerso");
            settings.setCustomizationId(customizationId);

            // Shared cache identifier
            String sharedCacheId = window.getProperty("osivia.cacheID");
            settings.setSharedCacheId(sharedCacheId);

            // BeanShell
            boolean beanShell = "1".equals(window.getDeclaredProperty("osivia.bshActivation"));
            String beanShellContent = window.getProperty("osivia.bshScript");
            settings.setBeanShell(beanShell);
            settings.setBeanShellContent(beanShellContent);

            // Selection dependency indicator
            boolean selectionDependency = "selection".equals(window.getProperty("osivia.cacheEvents"));
            settings.setSelectionDependency(selectionDependency);
            
            // Customization identifier
            String priority = window.getDeclaredProperty("osivia.sequence.priority");
            settings.setPriority(priority);

        }

        return windowSettings;
    }


    /**
     * Fill session reload URL.
     * 
     * @param controllerContext controller context
     * @param attributes attributes
     */
    private void fillSessionReloadUrl(ControllerContext controllerContext, Map<String, Object> attributes) {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(controllerContext);
        
        // HTTP servlet request
        HttpServletRequest servletRequest = controllerContext.getServerInvocation().getServerContext().getClientRequest();
        // HTTP session
        HttpSession session = servletRequest.getSession();
        
        // Reload session indicator
        Boolean reload = (Boolean) session.getAttribute(Constants.SESSION_RELOAD_ATTRIBUTE);
        if (BooleanUtils.isTrue(reload)) {
            // URL
            String url;
            try {
                url = cmsService.getEcmUrl(cmsContext, EcmViews.RELOAD, null, null);
            } catch (CMSException e) {
                url = null;
            }

            attributes.put("osivia.session.reload.url", url);
        }
        session.removeAttribute(Constants.SESSION_RELOAD_ATTRIBUTE);
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }


    /**
     * Layouts and themes collection filter predicate.
     * 
     * @author Cédric Krommenhoek
     * @see Predicate
     */
    private class LayoutsAndThemesPredicate implements Predicate {

        /** Excluded layout and theme names. */
        private final Set<String> excludedNames;


        /**
         * Constructor.
         */
        public LayoutsAndThemesPredicate() {
            super();
            this.excludedNames = new HashSet<>(Arrays.asList(EXCLUDED_NAMES));
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public boolean evaluate(Object object) {
            // Layout or theme name
            String name;
            if (object == null) {
                name = null;
            } else if (object instanceof PortalLayout) {
                PortalLayout layout = (PortalLayout) object;
                name = layout.getLayoutInfo().getName();
            } else if (object instanceof PortalTheme) {
                PortalTheme theme = (PortalTheme) object;
                name = theme.getThemeInfo().getName();
            } else {
                name = null;
            }
            
            return StringUtils.isNotEmpty(name) && !excludedNames.contains(name);
        }

    }

}
