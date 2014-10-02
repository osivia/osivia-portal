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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.Role;
import org.jboss.portal.security.AuthorizationDomainRegistry;
import org.jboss.portal.security.RoleSecurityBinding;
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
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.core.assistantpage.MoveWindowCommand;
import org.osivia.portal.core.assistantpage.PortalLayoutComparator;
import org.osivia.portal.core.assistantpage.PortalThemeComparator;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.portalobjects.DynamicWindow;
import org.osivia.portal.core.profils.IProfilManager;

/**
 * Page settings attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class PageSettingsAttributesBundle implements IAttributesBundle {

    /** Windows settings fancyboxes prefix. */
    private static final String PREFIX_ID_FANCYBOX_WINDOW_SETTINGS = "window-settings-";

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
    /** Instance container. */
    private final InstanceContainer instanceContainer;

    /** Internationalization service. */
    private IInternationalizationService internationalizationService;



    /** Toolbar attributes names. */
    private final Set<String> names;


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
        // Instance container
        this.instanceContainer = Locator.findMBean(InstanceContainer.class, "portal:container=Instance");

         // Internationalization service
         this.internationalizationService = Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);



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
        this.names.add(InternalConstants.ATTR_WINDOWS_CURRENT_LIST);


        // FIXME old regions attributes
        this.names.add(InternalConstants.ATTR_INTERNATIONALIZATION_SERVICE);
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

        Locale locale = serverContext.getClientRequest().getLocale();


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


                categories.put("", this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_NO_CATEGORY, locale));

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
            Collections.sort(layouts, new PortalLayoutComparator());
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_LAYOUTS_LIST, layouts);

            // Current layout
            attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_LAYOUT, page.getDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT));

            // Themes
            List<PortalTheme> themes = new ArrayList<PortalTheme>(this.themeService.getThemes());
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


        // FIXME old regions attributes
        attributes.put(InternalConstants.ATTR_INTERNATIONALIZATION_SERVICE, InternationalizationUtils.getInternationalizationService());
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

        // Current page
        Page page = renderPageCommand.getPage();
        // Current page type
        PageType pageType = PageType.getPageType(page, controllerContext);

        Object windowSettingMode = controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_WINDOWS_SETTING_MODE);
        if (!pageType.isTemplated() && InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(windowSettingMode)) {
            // Locale
            Locale locale = controllerContext.getServerInvocation().getRequest().getLocale();

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

            attributes.put(InternalConstants.ATTR_WINDOWS_CURRENT_LIST, windows);
        }
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
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
