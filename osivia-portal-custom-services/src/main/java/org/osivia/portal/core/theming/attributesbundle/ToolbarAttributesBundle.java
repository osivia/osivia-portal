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
 */
package org.osivia.portal.core.theming.attributesbundle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.IDirProvider;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.portal.api.ecm.EcmCommonCommands;
import org.osivia.portal.api.ecm.EcmViews;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.IMenubarService;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.tasks.ITasksService;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.api.urls.Link;
import org.osivia.portal.api.urls.PortalUrlType;
import org.osivia.portal.core.assistantpage.CMSDeleteDocumentCommand;
import org.osivia.portal.core.assistantpage.CMSEditionPageCustomizerInterceptor;
import org.osivia.portal.core.assistantpage.CMSPublishDocumentCommand;
import org.osivia.portal.core.assistantpage.ChangeCMSEditionModeCommand;
import org.osivia.portal.core.assistantpage.ChangeModeCommand;
import org.osivia.portal.core.assistantpage.ToggleAdvancedCMSToolsCommand;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.cms.Satellite;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.menubar.MenubarUtils;
import org.osivia.portal.core.page.MonEspaceCommand;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.security.CmsPermissionHelper;

/**
 * Toolbar attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class ToolbarAttributesBundle implements IAttributesBundle {

    /**
     * Toolbar menubar state items request attribute name.
     */
    private static final String TOOLBAR_MENUBAR_STATE_ITEMS_ATTRIBUTE = "osivia.toolbar.menubar.stateItems";

    /**
     * HTML class "fancybox_refresh".
     */
    private static final String HTML_CLASS_FANCYFRAME_REFRESH = "fancyframe_refresh";

    /**
     * Pages list URL.
     */
    private static final String URL_PAGES_LIST = "#pages-list";
    /**
     * Page creation URL.
     */
    private static final String URL_PAGE_CREATION = "#page-creation";
    /**
     * Template creation URL.
     */
    private static final String URL_TEMPLATE_CREATION = "#template-creation";
    /**
     * Page suppression URL.
     */
    private static final String URL_PAGE_SUPPRESSION = "#page-suppression";
    /**
     * Page location URL.
     */
    private static final String URL_PAGE_LOCATION = "#page-location";
    /**
     * Page properties URL.
     */
    private static final String URL_PAGE_PROPERTIES = "#page-properties";
    /**
     * Page CMS URL.
     */
    private static final String URL_PAGE_CMS = "#page-cms";
    /**
     * Page rights URL.
     */
    private static final String URL_PAGE_RIGHTS = "#page-rights";


    /**
     * Singleton instance.
     */
    private static ToolbarAttributesBundle instance;


    /**
     * Bundle factory.
     */
    private final IBundleFactory bundleFactory;
    /**
     * Portal URL factory.
     */
    private final IPortalUrlFactory urlFactory;
    /**
     * CMS service locator.
     */
    private final ICMSServiceLocator cmsServiceLocator;
    /**
     * Directory service locator.
     */
    private final IDirProvider directoryProvider;
    /**
     * Menubar service.
     */
    private final IMenubarService menubarService;
    /**
     * Tasks service.
     */
    private final ITasksService tasksService;
    /**
     * Instance container.
     */
    private final InstanceContainer instanceContainer;

    /**
     * Administration portal identifier.
     */
    private final PortalObjectId adminPortalId;

    /**
     * Toolbar attributes names.
     */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private ToolbarAttributesBundle() {
        super();

        // Internationalization service
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
        // URL factory
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        // Directory service locator
        this.directoryProvider = Locator.findMBean(IDirProvider.class, IDirProvider.MBEAN_NAME);
        // Menubar service
        this.menubarService = MenubarUtils.getMenubarService();
        // Tasks service
        this.tasksService = Locator.findMBean(ITasksService.class, ITasksService.MBEAN_NAME);
        // Instance container
        this.instanceContainer = Locator.findMBean(InstanceContainer.class, "portal:container=Instance");


        this.adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);

        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_TOOLBAR_PRINCIPAL);
        this.names.add(Constants.ATTR_TOOLBAR_PERSON);
        this.names.add(Constants.ATTR_TOOLBAR_LOGIN_URL);
        this.names.add(Constants.ATTR_TOOLBAR_MY_SPACE_URL);
        this.names.add(Constants.ATTR_TOOLBAR_REFRESH_PAGE_URL);
        this.names.add(Constants.ATTR_TOOLBAR_SIGN_OUT_URL);
        this.names.add(Constants.ATTR_TOOLBAR_ADMINISTRATION_CONTENT);
        this.names.add(Constants.ATTR_TOOLBAR_USER_CONTENT);
        this.names.add(Constants.ATTR_TOOLBAR_MY_PROFILE);
        this.names.add(Constants.ATTR_TOOLBAR_USER_SETTINGS_URL);
        this.names.add(Constants.ATTR_TOOLBAR_TASKS_URL);
        this.names.add(Constants.ATTR_TOOLBAR_TASKS_COUNT);
        this.names.add(TOOLBAR_MENUBAR_STATE_ITEMS_ATTRIBUTE);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static ToolbarAttributesBundle getInstance() {
        if (instance == null) {
            instance = new ToolbarAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
        // Server context
        ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();

        // Current page
        Page page = renderPageCommand.getPage();
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(controllerContext.getServerInvocation().getRequest().getLocale());

        // Principal
        Principal principal = serverContext.getClientRequest().getUserPrincipal();
        attributes.put(Constants.ATTR_TOOLBAR_PRINCIPAL, principal);

        // Person
        Person person = null;
        PersonService personService = null;
        if (principal != null) {
            personService = this.directoryProvider.getDirService(PersonService.class);
            person = personService.getPerson(principal.getName());
            attributes.put(Constants.ATTR_TOOLBAR_PERSON, person);

            String ownAvatarDisabled = page.getPortal().getProperty("own.avatar.disabled");
            if (StringUtils.isNotBlank(ownAvatarDisabled) && ownAvatarDisabled.equals("true") && person != null) {
                person.setAvatar(null);
            }


            attributes.put(Constants.ATTR_TOOLBAR_PERSON, person);

        }

        // My space
        MonEspaceCommand mySpaceCommand = new MonEspaceCommand();
        PortalURL mySpacePortalUrl = new PortalURLImpl(mySpaceCommand, controllerContext, true, null);
        if (principal == null) {
            attributes.put(Constants.ATTR_TOOLBAR_LOGIN_URL, mySpacePortalUrl.toString());
        } else {
            attributes.put(Constants.ATTR_TOOLBAR_MY_SPACE_URL, mySpacePortalUrl.toString());
        }


        // My profile
        String myProfileUrl = null;
        if (person != null) {

            // View profile
            try {

                Link l = personService.getMyCardUrl(portalControllerContext);

                if (l != null) {
                    myProfileUrl = l.getUrl();
                    attributes.put(Constants.ATTR_TOOLBAR_MY_PROFILE, myProfileUrl);
                }

            } catch (PortalException e) {
                // Do nothing
            }
        }


        // User settings
        String userSettingsInstance = "osivia-services-user-settings-instance";
        String userSettingsUrl;
        if ((person == null) || (this.instanceContainer.getDefinition(userSettingsInstance) == null)) {
            userSettingsUrl = null;
        } else {
            // Title
            String title = bundle.getString("USER_SETTINGS");

            // Window properties
            Map<String, String> properties = new HashMap<>();
            properties.put(InternalConstants.PROP_WINDOW_TITLE, title);
            properties.put("osivia.hideTitle", "1");
            properties.put("osivia.ajaxLink", "1");
            properties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, String.valueOf(true));

            try {
                userSettingsUrl = this.urlFactory.getStartPortletInNewPage(portalControllerContext, "user-settings", title, userSettingsInstance, properties,
                        null);
            } catch (PortalException e) {
                userSettingsUrl = null;
            }
        }
        attributes.put(Constants.ATTR_TOOLBAR_USER_SETTINGS_URL, userSettingsUrl);

        // Refresh page
        String refreshPageURL = this.urlFactory.getRefreshPageUrl(portalControllerContext);
        attributes.put(Constants.ATTR_TOOLBAR_REFRESH_PAGE_URL, refreshPageURL);

        // Logout
        SignOutCommand signOutCommand = new SignOutCommand();
        PortalURL signOutPortalUrl = new PortalURLImpl(signOutCommand, controllerContext, false, null);
        attributes.put(Constants.ATTR_TOOLBAR_SIGN_OUT_URL, signOutPortalUrl.toString());

        // Administration content
        String administrationContent = this.formatHTMLAdministration(controllerContext, page);
        attributes.put(Constants.ATTR_TOOLBAR_ADMINISTRATION_CONTENT, administrationContent);

        // Userbar content
        String userbarContent = this.formatHTMLUserbar(controllerContext, page, principal, person, mySpacePortalUrl.toString(), myProfileUrl,
                signOutPortalUrl.toString());
        attributes.put(Constants.ATTR_TOOLBAR_USER_CONTENT, userbarContent);

        // Tasks
        if (principal != null) {
            if (this.instanceContainer.getDefinition("osivia-services-tasks-instance") != null) {
                // Tasks URL
                String tasksUrl;
                try {
                    tasksUrl = this.urlFactory.getStartPortletUrl(portalControllerContext, "osivia-services-tasks-instance", null, PortalUrlType.MODAL);
                } catch (PortalException e) {
                    tasksUrl = null;
                }
                attributes.put(Constants.ATTR_TOOLBAR_TASKS_URL, tasksUrl);

                // Tasks count
                int tasksCount;
                try {
                    tasksCount = this.tasksService.getTasksCount(portalControllerContext);
                } catch (PortalException e) {
                    tasksCount = 0;
                }
                attributes.put(Constants.ATTR_TOOLBAR_TASKS_COUNT, tasksCount);
            }
        }

        // Mobile state items
        List<MenubarItem> stateItems = this.menubarService.getStateItems(portalControllerContext);
        attributes.put(TOOLBAR_MENUBAR_STATE_ITEMS_ATTRIBUTE, stateItems);
    }


    /**
     * Utility method used to generate administration HTML content.
     *
     * @param context controller context
     * @param page    current page
     * @return HTML data
     * @throws Exception
     */
    private String formatHTMLAdministration(ControllerContext context, Page page) {
        PageType pageType = PageType.getPageType(page, context);

        // Administration root element
        Element administration = DOM4JUtils.generateElement(HTMLConstants.UL, "navbar-nav", StringUtils.EMPTY);

        if (PageCustomizerInterceptor.isAdministrator(context)) {
            // Configuration menu
            this.generateAdministrationConfigurationMenu(context, page, administration);

            if (!(PageType.DYNAMIC_PAGE.equals(pageType) || (PortalObjectUtils.isSpaceSite(page) && !PortalObjectUtils.isTemplate(page)))) {
                // Edition menu
                this.generateAdministrationEditionMenu(context, page, administration);
            }
        }

        // Check if layout contains CMS
        boolean spaceSite = PortalObjectUtils.isSpaceSite(page);
        Boolean layoutCMS = (Boolean) context.getAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LAYOUT_CMS_INDICATOR);
        try {
            if (spaceSite && BooleanUtils.isTrue(layoutCMS) && CMSEditionPageCustomizerInterceptor.checkWritePermission(context, page)
                    && CMSEditionPageCustomizerInterceptor.checkWebPagePermission(context, page)) {
                // Web page menu
                this.generateAdministrationWebPageMenu(context, page, administration);
                this.generateAdministrationToggleVersion(context, page, administration);
            }
        } catch (Exception e) {
            // Do nothing
        }

        // Write HTML content
        return DOM4JUtils.write(administration);
    }


    /**
     * Utility method used to generate configuration menu for administration toolbar.
     *
     * @param context        controller context
     * @param page           current page
     * @param administration administration toolbar element
     */
    private void generateAdministrationConfigurationMenu(ControllerContext context, Page page, Element administration) {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(context);
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(context.getServerInvocation().getRequest().getLocale());
        // Page type
        PageType pageType = PageType.getPageType(page, context);
        // Portal name
        String portalName = page.getPortal().getName();


        // fonctionnal administration
        String customAdminPage = page.getProperty("osivia.portal.admin.page");
        if (customAdminPage != null) {
            ViewPageCommand customAdminPageCommand = new ViewPageCommand(new PortalObjectId("", PortalObjectPath.parse(customAdminPage, PortalObjectPath.CANONICAL_FORMAT)));
            String customAdminPageURL = new PortalURLImpl(customAdminPageCommand, context, null, null).toString();
            Element functionalhome = DOM4JUtils.generateLinkElement(customAdminPageURL, null, null, "nav-link", "", "glyphicons glyphicons-basic-cogwheel");
            administration.add(functionalhome);
        }

        // Configuration menu dropdown element
        Element configurationDropdown = DOM4JUtils.generateElement(HTMLConstants.LI, "nav-item dropdown", null);
        administration.add(configurationDropdown);

        // Configuration menu dropdown title
        String configurationTitle = bundle.getString(InternationalizationConstants.KEY_CONFIGURATION_MENU_TITLE);
        Element configurationDropdownTitle = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "nav-link dropdown-toggle", null, "glyphicons glyphicons-basic-wrench");
        Element title = DOM4JUtils.generateElement("span", "d-sm-none d-md-inline", configurationTitle);
        configurationDropdownTitle.add(title);
        DOM4JUtils.addDataAttribute(configurationDropdownTitle, "bs-toggle", "dropdown");
        configurationDropdown.add(configurationDropdownTitle);

        // Configuration menu
        Element configurationMenu = DOM4JUtils.generateElement(HTMLConstants.UL, "dropdown-menu", null);
        configurationDropdown.add(configurationMenu);

        // Menu header
        Element headerItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        configurationMenu.add(headerItem);
        Element header = DOM4JUtils.generateElement(HTMLConstants.H3, "dropdown-header", configurationTitle);
        headerItem.add(header);

        // Home
        Element homeItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        configurationMenu.add(homeItem);
        String homeURL = context.getServerInvocation().getServerContext().getPortalContextPath();
        String homeTitle = bundle.getString(InternationalizationConstants.KEY_HOME);
        Element home = DOM4JUtils.generateLinkElement(homeURL, null, null, "dropdown-item", homeTitle, "glyphicons glyphicons-basic-home");
        homeItem.add(home);

        // OSIVIA Portal administration
        Element osiviaAdministrationItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        configurationMenu.add(osiviaAdministrationItem);
        String osiviaAdministrationURL = StringUtils.EMPTY;
        try {
            osiviaAdministrationURL = this.urlFactory.getStartPortletUrl(portalControllerContext, InternalConstants.PORTLET_ADMINISTRATION_INSTANCE_NAME, null,
                    PortalUrlType.POPUP);
        } catch (Exception e) {
            // Do nothing
        }
        String osiviaAdministrationTitle = bundle.getString(InternationalizationConstants.KEY_OSIVIA_ADMINISTRATION);
        Element osiviaAdministration = DOM4JUtils.generateLinkElement(osiviaAdministrationURL, null, null, "dropdown-item " + HTML_CLASS_FANCYFRAME_REFRESH,
                osiviaAdministrationTitle, "glyphicons glyphicons-basic-cogwheel");
        osiviaAdministrationItem.add(osiviaAdministration);

        // JBoss administration
        Element jbossAdministrationItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        configurationMenu.add(jbossAdministrationItem);
        ViewPageCommand jbossAdministrationCommand = new ViewPageCommand(this.adminPortalId);
        String jbossAdministrationURL = new PortalURLImpl(jbossAdministrationCommand, context, null, null).toString();
        String jbossAdministrationTitle = bundle.getString(InternationalizationConstants.KEY_JBOSS_ADMINISTRATION);
        Element jbossAdministration = DOM4JUtils.generateLinkElement(jbossAdministrationURL, "jboss", null, "dropdown-item", jbossAdministrationTitle,
                "glyphicons glyphicons-basic-server-cogwheel", AccessibilityRoles.MENU_ITEM);
        Element jbossAdministrationNewWindowGlyph = DOM4JUtils.generateElement(HTMLConstants.SMALL, "d-inline-block align-text-bottom", null, "glyphicons glyphicons-basic-square-new-window", null);
        jbossAdministration.add(jbossAdministrationNewWindowGlyph);
        jbossAdministrationItem.add(jbossAdministration);


        // ECM administration
        this.generateEcmAdministration(context, bundle, configurationMenu);


        // Divider
        this.addDropdownDivider(configurationMenu);


        // Pages list
        Element pagesListItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        configurationMenu.add(pagesListItem);
        String pagesListTitle = bundle.getString(InternationalizationConstants.KEY_PAGES_LIST);
        Element pagesList = DOM4JUtils.generateLinkElement("javascript:;", null, null, "dropdown-item", pagesListTitle, "glyphicons glyphicons-basic-sort-alphabetically");
        DOM4JUtils.addDataAttribute(pagesList, "fancybox", StringUtils.EMPTY);
        DOM4JUtils.addDataAttribute(pagesList, "src", URL_PAGES_LIST);
        pagesListItem.add(pagesList);


        // Divider
        this.addDropdownDivider(configurationMenu);


        if (InternalConstants.PORTAL_TYPE_STATIC_PORTAL.equals(page.getPortal().getDeclaredProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE))) {
            // Page creation
            Element pageCreationItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
            configurationMenu.add(pageCreationItem);
            String pageCreationTitle = bundle.getString(InternationalizationConstants.KEY_PAGE_CREATION);
            Element pageCreation = DOM4JUtils.generateLinkElement("javascript:;", null, null, "dropdown-item", pageCreationTitle, "glyphicons glyphicons-basic-square-plus");
            DOM4JUtils.addDataAttribute(pageCreation, "fancybox", StringUtils.EMPTY);
            DOM4JUtils.addDataAttribute(pageCreation, "src", URL_PAGE_CREATION);
            pageCreationItem.add(pageCreation);
        }

        // Template creation
        Element templateCreationItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        configurationMenu.add(templateCreationItem);
        String templateCreationTitle = bundle.getString(InternationalizationConstants.KEY_TEMPLATE_CREATION);
        Element templateCreation = DOM4JUtils.generateLinkElement("javascript:;", null, null, "dropdown-item", templateCreationTitle, "glyphicons glyphicons-basic-square-plus");
        DOM4JUtils.addDataAttribute(templateCreation, "fancybox", StringUtils.EMPTY);
        DOM4JUtils.addDataAttribute(templateCreation, "src", URL_TEMPLATE_CREATION);
        templateCreationItem.add(templateCreation);

        // Page template access
        String pageTemplateAccessTitle = bundle.getString(InternationalizationConstants.KEY_PAGE_TEMPLATE_ACCESS);
        if (pageType.isTemplated()) {
            // URL
            ITemplatePortalObject templatePortalObject = (ITemplatePortalObject) page;
            ViewPageCommand pageTemplateAccessCommand = new ViewPageCommand(templatePortalObject.getTemplate().getId());
            String pageTemplateAccessURL = new PortalURLImpl(pageTemplateAccessCommand, context, null, null).toString();
            pageTemplateAccessURL += "?init-state=true&edit-template-mode=true&original-portal=" + portalName;

            // Link
            Element pageTemplateAccessLinkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
            configurationMenu.add(pageTemplateAccessLinkItem);
            Element pageTemplateAccessLink = DOM4JUtils.generateLinkElement(pageTemplateAccessURL, null, null, "dropdown-item", pageTemplateAccessTitle,
                    "glyphicons glyphicons-basic-construction-cone");
            pageTemplateAccessLinkItem.add(pageTemplateAccessLink);
        } else {
            // Link
            Element pageTemplateAccessLinkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
            configurationMenu.add(pageTemplateAccessLinkItem);
            Element pageTemplateAccessLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", pageTemplateAccessTitle,
                    "glyphicons glyphicons-basic-construction-cone");
            pageTemplateAccessLinkItem.add(pageTemplateAccessLink);
        }


        // Divider
        this.addDropdownDivider(configurationMenu);


        // Caches initialization
        Element cachesInitializationItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        configurationMenu.add(cachesInitializationItem);
        ViewPageCommand cachesInitializationCommand = new ViewPageCommand(page.getId());
        String cachesInitializationURL = new PortalURLImpl(cachesInitializationCommand, context, null, null).toString();
        cachesInitializationURL += "?init-cache=true";
        String cachesInitializationTitle = bundle.getString(InternationalizationConstants.KEY_CACHES_INITIALIZATION);
        Element cachesInitialization = DOM4JUtils.generateLinkElement(cachesInitializationURL, null, null, "dropdown-item", cachesInitializationTitle,
                "glyphicons glyphicons-restart");
        cachesInitializationItem.add(cachesInitialization);
    }


    /**
     * Generate ECM administration.
     *
     * @param controllerContext controller context
     * @param bundle            internationalization bundle
     * @param menu              menu DOM element
     */
    private void generateEcmAdministration(ControllerContext controllerContext, Bundle bundle, Element menu) {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setServerInvocation(controllerContext.getServerInvocation());
        cmsContext.setControllerContext(controllerContext);

        // Satellites
        Set<Satellite> satellites;
        try {
            satellites = cmsService.getSatellites();
        } catch (CMSException e) {
            satellites = null;
        }
        List<Satellite> allSatellites;
        if (CollectionUtils.isEmpty(satellites)) {
            allSatellites = new ArrayList<>(1);
        } else {
            allSatellites = new ArrayList<>(satellites);
        }
        allSatellites.add(0, Satellite.MAIN);


        // Request parameters
        Map<String, String> requestParameters = new HashMap<>();

        for (Satellite satellite : allSatellites) {
            cmsContext.setSatellite(satellite);

            // URL
            String url;
            try {
                url = cmsService.getEcmUrl(cmsContext, EcmViews.globalAdministration, StringUtils.EMPTY, requestParameters);
            } catch (CMSException e) {
                url = HTMLConstants.A_HREF_DEFAULT;
            }
            // Title
            String title;
            if (satellite.isMain()) {
                title = bundle.getString("SUBMENU_ECM_MAIN_ADMINISTRATION");
            } else {
                title = bundle.getString("SUBMENU_ECM_SATELLITE_ADMINISTRATION", StringUtils.defaultIfBlank(satellite.getLabel(), satellite.getId()));
            }
            // Link DOM element
            Element linkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
            menu.add(linkItem);
            Element link = DOM4JUtils.generateLinkElement(url, satellite.getId(), null, "dropdown-item", title, "glyphicons glyphicons-basic-hard-drive-cogwheel");
            // New window indicator DOM element
            Element indicator = DOM4JUtils.generateElement(HTMLConstants.SMALL, "d-inline-block align-text-bottom", null, "glyphicons glyphicons-basic-square-new-window", null);
            link.add(indicator);

            linkItem.add(link);
        }
    }


    /**
     * Utility method used to generate edition menu for administration toolbar.
     *
     * @param context        controller context
     * @param page           current page
     * @param administration administration toolbar element
     */
    private void generateAdministrationEditionMenu(ControllerContext context, Page page, Element administration) {
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(context.getServerInvocation().getRequest().getLocale());
        // Page type
        PageType pageType = PageType.getPageType(page, context);

        // Edition dropdown element
        Element editionDropdown = DOM4JUtils.generateElement(HTMLConstants.LI, "nav-item dropdown", null);
        administration.add(editionDropdown);

        // Edition dropdown title
        String editionTitle;
        if (pageType.isSpace()) {
            editionTitle = bundle.getString(InternationalizationConstants.KEY_SPACE_EDITION_MENU_TITLE);
        } else if (PortalObjectUtils.isTemplate(page)) {
            editionTitle = bundle.getString(InternationalizationConstants.KEY_TEMPLATE_EDITION_MENU_TITLE);
        } else {
            editionTitle = bundle.getString(InternationalizationConstants.KEY_PAGE_EDITION_MENU_TITLE);
        }
        Element editionDropdownTitle = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "nav-link dropdown-toggle", null, "glyphicons glyphicons-basic-pencil");
        Element title = DOM4JUtils.generateElement("span", "d-sm-none d-md-inline", editionTitle);
        editionDropdownTitle.add(title);
        DOM4JUtils.addDataAttribute(editionDropdownTitle, "bs-toggle", "dropdown");
        editionDropdown.add(editionDropdownTitle);

        // Edition menu node
        Element editionMenu = DOM4JUtils.generateElement(HTMLConstants.UL, "dropdown-menu", null);
        editionDropdown.add(editionMenu);

        // Menu header
        Element headerItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        editionMenu.add(headerItem);
        Element header = DOM4JUtils.generateElement(HTMLConstants.H3, "dropdown-header", editionTitle);
        headerItem.add(header);

        if (!pageType.isTemplated()) {
            // Icons display
            String mode = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_WINDOWS_SETTING_MODE);
            ChangeModeCommand changeModeCommand;
            String modeHtmlClass;
            if (InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(mode)) {
                changeModeCommand = new ChangeModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), StringUtils.EMPTY);
                modeHtmlClass = "glyphicons glyphicons-basic-square-selected";
            } else {
                changeModeCommand = new ChangeModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT),
                        InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE);
                modeHtmlClass = "glyphicons glyphicons-basic-square-empty";
            }
            String changeModeURL = new PortalURLImpl(changeModeCommand, context, null, null).toString();
            String changeModeTitle = bundle.getString(InternationalizationConstants.KEY_ICONS_DISPLAY);

            Element iconsDisplayItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
            editionMenu.add(iconsDisplayItem);
            Element iconsDisplay = DOM4JUtils.generateLinkElement(changeModeURL, null, null, "dropdown-item", changeModeTitle, modeHtmlClass);
            iconsDisplayItem.add(iconsDisplay);

            // Divider
            this.addDropdownDivider(editionMenu);
        }

        // Page suppression
        Element pageSuppressionItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        editionMenu.add(pageSuppressionItem);
        String pageSuppressionTitle = bundle.getString(InternationalizationConstants.KEY_SUPPRESSION);
        if (PortalObjectUtils.isPortalDefaultPage(page)) {
            Element pageSuppression = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", pageSuppressionTitle,
                    "glyphicons glyphicons-basic-bin");
            pageSuppressionItem.add(pageSuppression);
        } else {
            Element pageSuppression = DOM4JUtils.generateLinkElement("javascript:;", null, null, "dropdown-item", pageSuppressionTitle, "glyphicons glyphicons-basic-bin");
            DOM4JUtils.addDataAttribute(pageSuppression, "fancybox", StringUtils.EMPTY);
            DOM4JUtils.addDataAttribute(pageSuppression, "src", URL_PAGE_SUPPRESSION);
            pageSuppressionItem.add(pageSuppression);
        }

        // Page location
        Element pageLocationItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        editionMenu.add(pageLocationItem);
        String pageLocationTitle = bundle.getString(InternationalizationConstants.KEY_LOCATION);
        Element pageLocation = DOM4JUtils.generateLinkElement("javascript:;", null, null, "dropdown-item", pageLocationTitle, "glyphicons glyphicons-basic-block-move");
        DOM4JUtils.addDataAttribute(pageLocation, "fancybox", StringUtils.EMPTY);
        DOM4JUtils.addDataAttribute(pageLocation, "src", URL_PAGE_LOCATION);
        pageLocationItem.add(pageLocation);

        // Divider
        this.addDropdownDivider(editionMenu);

        // Page properties
        Element pagePropertiesItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        editionMenu.add(pagePropertiesItem);
        String pagePropertiesTitle = bundle.getString(InternationalizationConstants.KEY_PROPERTIES);
        Element pageProperties = DOM4JUtils.generateLinkElement("javascript:;", null, null, "dropdown-item", pagePropertiesTitle, "glyphicons glyphicons-basic-cogwheel");
        DOM4JUtils.addDataAttribute(pageProperties, "fancybox", StringUtils.EMPTY);
        DOM4JUtils.addDataAttribute(pageProperties, "src", URL_PAGE_PROPERTIES);
        pagePropertiesItem.add(pageProperties);

        // Page CMS configuration
        Element pageCmsItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        editionMenu.add(pageCmsItem);
        String pageCMSTitle = bundle.getString(InternationalizationConstants.KEY_CMS_CONFIGURATION);
        Element pageCms = DOM4JUtils.generateLinkElement("javascript:;", null, null, "dropdown-item", pageCMSTitle, "glyphicons glyphicons-basic-cogwheel");
        DOM4JUtils.addDataAttribute(pageCms, "fancybox", StringUtils.EMPTY);
        DOM4JUtils.addDataAttribute(pageCms, "src", URL_PAGE_CMS);
        pageCmsItem.add(pageCms);

        // Page rights
        Element pageRightsItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        editionMenu.add(pageRightsItem);
        String pageRightsTitle = bundle.getString(InternationalizationConstants.KEY_RIGHTS);
        Element pageRights = DOM4JUtils.generateLinkElement("javascript:;", null, null, "dropdown-item", pageRightsTitle, "glyphicons glyphicons-basic-cogwheel");
        DOM4JUtils.addDataAttribute(pageRights, "fancybox", StringUtils.EMPTY);
        DOM4JUtils.addDataAttribute(pageRights, "src", URL_PAGE_RIGHTS);
        pageRightsItem.add(pageRights);
    }


    /**
     * Utility method used to generate web page menu for administration toolbar.
     *
     * @param context        controller context
     * @param page           current page
     * @param administration administration toolbar element
     * @throws Exception
     */
    private void generateAdministrationWebPageMenu(ControllerContext context, Page page, Element administration) throws Exception {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(context);
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(context.getServerInvocation().getRequest().getLocale());
        // URL context
        URLContext urlContext = context.getServerInvocation().getServerContext().getURLContext();

        // Preview mode indicator
        boolean modePreview = CmsPermissionHelper.getCurrentCmsVersion(context).equals(CmsPermissionHelper.CMS_VERSION_PREVIEW);
        // CMS base path
        String basePath = page.getProperty("osivia.cms.basePath");

        // Request parameters
        Map<String, String> requestParameters = new HashMap<>();

        // CMS context
        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(context.getServerInvocation());
        cmsCtx.setControllerContext(context);

        // CMS publication information
        String pagePath = (String) context.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path");
        cmsCtx.setDisplayLiveVersion("1");
        CMSPublicationInfos publicationInfos = cmsService.getPublicationInfos(cmsCtx, pagePath);
        // Path
        String path = publicationInfos.getDocumentPath();
        // Published indicator
        boolean published = publicationInfos.isPublished();
        // beingModified indicator
        boolean beingModified = publicationInfos.isBeingModified();

        // Content
        CMSItem content = cmsService.getContent(cmsCtx, path);
        cmsCtx.setDoc(content.getNativeItem());

        // Close URL
        String closeUrl = this.urlFactory.getCMSUrl(portalControllerContext, null, "_NEWID_", null, null, IPortalUrlFactory.DISPLAYCTX_REFRESH, null, null,
                null, null);
        // ECM base URL
        String ecmBaseUrl = cmsService.getEcmDomain(cmsCtx);

        // Callback on click action
        StringBuilder builder = new StringBuilder();
        builder.append("javascript:setCallbackFromEcmParams('");
        builder.append(closeUrl);
        builder.append("', '");
        builder.append(ecmBaseUrl);
        builder.append("');");
        String onClickCallback = builder.toString();


        // CMS edition dropdown element
        Element cmsEditionDropdown = DOM4JUtils.generateElement(HTMLConstants.LI, "nav-item dropdown", null);
        administration.add(cmsEditionDropdown);

        // CMS edition title
        String cmsEditionTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE);
        Element cmsEditionDropdownTitle = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "nav-link dropdown-toggle", null, "glyphicons glyphicons-basic-pencil");
        Element title = DOM4JUtils.generateElement("span", "d-sm-none d-md-inline", cmsEditionTitle);
        cmsEditionDropdownTitle.add(title);
        DOM4JUtils.addDataAttribute(cmsEditionDropdownTitle, "bs-toggle", "dropdown");
        cmsEditionDropdown.add(cmsEditionDropdownTitle);

        // CMS edition menu node
        Element cmsEditionMenu = DOM4JUtils.generateElement(HTMLConstants.UL, "dropdown-menu", null);
        cmsEditionDropdown.add(cmsEditionMenu);

        // Menu header
        Element headerItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(headerItem);
        Element header = DOM4JUtils.generateElement(HTMLConstants.H3, "dropdown-header", cmsEditionTitle);
        headerItem.add(header);

        // Preview required message
        String previewRequired = bundle.getString(InternationalizationConstants.KEY_PTITLE_PREVIEW_MODE_REQUIRED);

        // Toggle advanced CMS tools
        Element toggleAdvancedToolsLinkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(toggleAdvancedToolsLinkItem);
        boolean showAdvancedTools = BooleanUtils.toBoolean((Boolean) context.getAttribute(Scope.SESSION_SCOPE, InternalConstants.SHOW_ADVANCED_CMS_TOOLS_INDICATOR));
        String toggleAdvancedToolsTitle = bundle.getString(InternationalizationConstants.KEY_CMS_TOGGLE_ADVANCED_TOOLS);
        String toggleAdvancedToolsGlyphicon;
        if (showAdvancedTools) {
            toggleAdvancedToolsGlyphicon = "glyphicons glyphicons-basic-square-selected";
        } else {
            toggleAdvancedToolsGlyphicon = "glyphicons glyphicons-basic-square-empty";
        }
        if (modePreview) {
            // URL
            String pageId = page.getId().toString(PortalObjectPath.SAFEST_FORMAT);
            ControllerCommand toggleAdvancedToolsCommand = new ToggleAdvancedCMSToolsCommand(pageId);
            String toggleAdvancedToolsURL = new PortalURLImpl(toggleAdvancedToolsCommand, context, null, null).toString();

            // Link
            Element toggleAdvancedToolsLink = DOM4JUtils.generateLinkElement(toggleAdvancedToolsURL, null, null, "dropdown-item", toggleAdvancedToolsTitle, toggleAdvancedToolsGlyphicon);
            toggleAdvancedToolsLinkItem.add(toggleAdvancedToolsLink);
        } else {
            // Link
            Element toggleAdvancedToolsLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", toggleAdvancedToolsTitle,
                    toggleAdvancedToolsGlyphicon);
            DOM4JUtils.addAttribute(toggleAdvancedToolsLink, HTMLConstants.TITLE, previewRequired);
            toggleAdvancedToolsLinkItem.add(toggleAdvancedToolsLink);
        }


        // Divider
        this.addDropdownDivider(cmsEditionMenu);

        // CMS create page
        Element cmsCreatePageLinkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(cmsCreatePageLinkItem);
        String cmsCreatePageTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_CREATE);
        if (modePreview) {
            // URL
            String cmsCreatePageURL = cmsService.getEcmUrl(cmsCtx, EcmViews.createPage, path, requestParameters);

            // Link
            Element cmsCreatePageLink = DOM4JUtils.generateLinkElement(cmsCreatePageURL, null, onClickCallback, "dropdown-item " + HTML_CLASS_FANCYFRAME_REFRESH,
                    cmsCreatePageTitle, "glyphicons glyphicons-basic-square-plus");
            DOM4JUtils.addAttribute(cmsCreatePageLink, HTMLConstants.ACCESSKEY, "n");
            cmsCreatePageLinkItem.add(cmsCreatePageLink);
        } else {
            // Link
            Element cmsCreatePageLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", cmsCreatePageTitle,
                    "glyphicons glyphicons-basic-square-plus");
            DOM4JUtils.addAttribute(cmsCreatePageLink, HTMLConstants.TITLE, previewRequired);
            cmsCreatePageLinkItem.add(cmsCreatePageLink);
        }


        // CMS edit current page
        Element cmsEditPageLinkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(cmsEditPageLinkItem);
        String cmsEditPageTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_OPTIONS);
        if (modePreview) {
            // URL
            String cmsEditPageURL = cmsService.getEcmUrl(cmsCtx, EcmViews.editPage, path, requestParameters);

            Element cmsEditPageLink = DOM4JUtils.generateLinkElement(cmsEditPageURL, null, onClickCallback, "dropdown-item " + HTML_CLASS_FANCYFRAME_REFRESH, cmsEditPageTitle,
                    "glyphicons glyphicons-basic-pencil");
            DOM4JUtils.addAttribute(cmsEditPageLink, HTMLConstants.ACCESSKEY, "e");
            cmsEditPageLinkItem.add(cmsEditPageLink);
        } else {
            Element cmsEditPageLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", cmsEditPageTitle,
                    "glyphicons glyphicons-basic-pencil", AccessibilityRoles.MENU_ITEM);
            DOM4JUtils.addAttribute(cmsEditPageLink, HTMLConstants.TITLE, previewRequired);
            cmsEditPageLinkItem.add(cmsEditPageLink);
        }

        // Edit page attachments
        Element cmsEditPageAttachmentsLinkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(cmsEditPageAttachmentsLinkItem);
        String cmsEditPageAttachmentsTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_ATTACHMENTS);
        final String editPageAttachmentsGlyph = "glyphicons glyphicons-basic-paperclip";
        if (modePreview) {
            String cmsEditPageAttachmentsURL = cmsService.getEcmUrl(cmsCtx, EcmViews.editAttachments, path, requestParameters);
            Element cmsEditPageAttachmentsLink = DOM4JUtils.generateLinkElement(cmsEditPageAttachmentsURL, null, onClickCallback, "dropdown-item " + HTML_CLASS_FANCYFRAME_REFRESH,
                    cmsEditPageAttachmentsTitle, editPageAttachmentsGlyph);
            cmsEditPageAttachmentsLinkItem.add(cmsEditPageAttachmentsLink);
        } else {
            Element cmsEditPageAttachmentsLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", cmsEditPageAttachmentsTitle,
                    editPageAttachmentsGlyph);
            DOM4JUtils.addAttribute(cmsEditPageAttachmentsLink, HTMLConstants.TITLE, previewRequired);
            cmsEditPageAttachmentsLinkItem.add(cmsEditPageAttachmentsLink);
        }

        // Move
        Element moveLinkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(moveLinkItem);
        String moveTitle = bundle.getString("MOVE");
        if (basePath.equals(pagePath)) {
            Element moveLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", moveTitle, "glyphicons glyphicons-basic-block-move");
            DOM4JUtils.addAttribute(moveLink, HTMLConstants.TITLE, bundle.getString("PTITLE_PREVENT_ROOT_MOVE"));
            moveLinkItem.add(moveLink);
        } else if (modePreview) {
            // URL
            String url = cmsService.getMoveUrl(cmsCtx);

            Element moveLink = DOM4JUtils.generateLinkElement(url, null, null, "dropdown-item fancyframe_refresh", moveTitle, "glyphicons glyphicons-basic-block-move");
            moveLinkItem.add(moveLink);
        } else {
            Element moveLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", moveTitle, "glyphicons glyphicons-basic-block-move");
            DOM4JUtils.addAttribute(moveLink, HTMLConstants.TITLE, previewRequired);
            moveLinkItem.add(moveLink);
        }


        // Reorder
        String reorderTitle = bundle.getString("REORDER");
        String reorderUrl = cmsService.getReorderUrl(cmsCtx);
        if (reorderUrl != null) {
            Element reorderLinkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
            cmsEditionMenu.add(reorderLinkItem);
            if (modePreview) {
                Element reorderLink = DOM4JUtils.generateLinkElement(reorderUrl, null, null, "dropdown-item fancyframe_refresh", reorderTitle, "glyphicons glyphicons-basic-sort");
                reorderLinkItem.add(reorderLink);
            } else {
                Element reorderLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", reorderTitle, "glyphicons glyphicons-basic-sort");
                DOM4JUtils.addAttribute(reorderLink, HTMLConstants.TITLE, previewRequired);
                reorderLinkItem.add(reorderLink);
            }
        }

        // CMS publish document
        Element cmsPublishLinkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(cmsPublishLinkItem);
        String cmsPublishTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_PUBLISH);
        if (modePreview) {
            // URL
            CMSPublishDocumentCommand cmsPublishCommand = new CMSPublishDocumentCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                    CMSPublishDocumentCommand.PUBLISH);
            String cmsPublishURL = context.renderURL(cmsPublishCommand, urlContext, URLFormat.newInstance(true, true));

            // Link
            Element cmsPublishLink = DOM4JUtils.generateLinkElement(cmsPublishURL, null, null, "dropdown-item", cmsPublishTitle, "glyphicons glyphicons-basic-circle-check");
            cmsPublishLinkItem.add(cmsPublishLink);
        } else {
            // Link
            Element cmsPublishLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", cmsPublishTitle,
                    "glyphicons glyphicons-basic-circle-check");
            DOM4JUtils.addAttribute(cmsPublishLink, HTMLConstants.TITLE, previewRequired);
            cmsPublishLinkItem.add(cmsPublishLink);
        }


        // CMS unpublish document
        Element cmsUnpublishLinkItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(cmsUnpublishLinkItem);
        String cmsUnpublishTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_UNPUBLISH);
        if (basePath.equals(pagePath)) {
            Element cmsUnpublishLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", cmsUnpublishTitle,
                    "glyphicons glyphicons-basic-circle-remove");
            DOM4JUtils.addAttribute(cmsUnpublishLink, HTMLConstants.TITLE, bundle.getString("PTITLE_PREVENT_ROOT_UNPUBLISH"));
            cmsUnpublishLinkItem.add(cmsUnpublishLink);
        } else if (modePreview && published) {
            // URL
            CMSPublishDocumentCommand cmsUnpublishCommand = new CMSPublishDocumentCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                    CMSPublishDocumentCommand.UNPUBLISH);
            String cmsUnpublishURL = context.renderURL(cmsUnpublishCommand, urlContext, URLFormat.newInstance(true, true));

            // Link
            Element cmsUnpublishLink = DOM4JUtils.generateLinkElement(cmsUnpublishURL, null, null, "dropdown-item", cmsUnpublishTitle, "glyphicons glyphicons-basic-circle-remove");
            cmsUnpublishLinkItem.add(cmsUnpublishLink);
        } else {
            // Link
            Element cmsUnpublishLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", cmsUnpublishTitle,
                    "glyphicons glyphicons-basic-circle-remove");
            if (!modePreview) {
                DOM4JUtils.addAttribute(cmsUnpublishLink, HTMLConstants.TITLE, previewRequired);
            } else {
                String publishRequired = bundle.getString(InternationalizationConstants.KEY_PTITLE_PUBLISH_REQUIRED);
                DOM4JUtils.addAttribute(cmsUnpublishLink, HTMLConstants.TITLE, publishRequired);
            }
            cmsUnpublishLinkItem.add(cmsUnpublishLink);
        }

        // Erase modifications
        Element eraseItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(eraseItem);
        String cmsEraseTitle = bundle.getString("SUBMENU_CMS_PAGE_ERASE");
        if (beingModified && modePreview && published) {
            String cmsEraseModificationURL = this.urlFactory.getEcmCommandUrl(portalControllerContext, path, EcmCommonCommands.eraseModifications);

            cmsEditionDropdown.add(this.generateEraseFancyBox(bundle, cmsEraseModificationURL));

            Element cmsEraseModificationLink = DOM4JUtils.generateLinkElement("javascript:;", null, null, "dropdown-item", cmsEraseTitle, "glyphicons glyphicons-basic-eraser");
            DOM4JUtils.addDataAttribute(cmsEraseModificationLink, "fancybox", StringUtils.EMPTY);
            DOM4JUtils.addDataAttribute(cmsEraseModificationLink, "src", "#erase_cms_page");
            eraseItem.add(cmsEraseModificationLink);
        } else {
            Element eraseLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", cmsEraseTitle, "glyphicons glyphicons-basic-eraser");
            eraseItem.add(eraseLink);
        }

        // CMS delete document
        Element deleteItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(deleteItem);
        String cmsDeleteTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_DELETE);
        if (basePath.equals(pagePath)) {
            // Link
            Element cmsDeleteLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", cmsDeleteTitle, "glyphicons glyphicons-basic-bin");
            DOM4JUtils.addAttribute(cmsDeleteLink, HTMLConstants.TITLE, bundle.getString("PTITLE_PREVENT_ROOT_SUPPRESSION"));
            deleteItem.add(cmsDeleteLink);
        } else if (modePreview) {
            // User can only delete a document which its parent is editable.
            CMSObjectPath parent = CMSObjectPath.parse(pagePath).getParent();
            String parentPath = parent.toString();
            CMSPublicationInfos parentPubInfos = cmsService.getPublicationInfos(cmsCtx, parentPath);

            // Check if parent is editable
            if (parentPubInfos.isEditableByUser()) {
                // URL
                CMSDeleteDocumentCommand cmsDeleteCommand = new CMSDeleteDocumentCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path);
                String cmsDeleteURL = context.renderURL(cmsDeleteCommand, urlContext, URLFormat.newInstance(true, true));

                // Inline fancybox
                Element divDeleteFancyBox = this.generateDeleteFancyBox(bundle, cmsDeleteURL);
                cmsEditionDropdown.add(divDeleteFancyBox);

                // Link
                Element cmsDeleteLink = DOM4JUtils.generateLinkElement("javascript:;", null, null, "dropdown-item", cmsDeleteTitle, "glyphicons glyphicons-basic-bin");
                DOM4JUtils.addDataAttribute(cmsDeleteLink, "fancybox", StringUtils.EMPTY);
                DOM4JUtils.addDataAttribute(cmsDeleteLink, "src", "#delete_cms_page");
                deleteItem.add(cmsDeleteLink);
            } else {
                // Link
                Element cmsDeleteLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", cmsDeleteTitle,
                        "glyphicons glyphicons-basic-bin");
                String deleteForbidden = bundle.getString(InternationalizationConstants.KEY_PTITLE_DELETE_FORBIDDEN);
                DOM4JUtils.addAttribute(cmsDeleteLink, HTMLConstants.TITLE, deleteForbidden);
                deleteItem.add(cmsDeleteLink);
            }
        } else {
            // Link
            Element cmsDeleteLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", cmsDeleteTitle, "glyphicons glyphicons-basic-bin");
            DOM4JUtils.addAttribute(cmsDeleteLink, HTMLConstants.TITLE, previewRequired);
            deleteItem.add(cmsDeleteLink);
        }


        // Divider
        this.addDropdownDivider(cmsEditionMenu);


        // Sitemap
        Element sitemapItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(sitemapItem);
        Map<String, String> sitemapProperties = new HashMap<>();
        sitemapProperties.put("osivia.cms.path", path);
        sitemapProperties.put("osivia.cms.basePath", basePath);
        String sitemapUrl = this.urlFactory.getStartPortletUrl(portalControllerContext, "osivia-portal-sitemap-instance", sitemapProperties,
                PortalUrlType.POPUP);
        Element sitemapLink = DOM4JUtils.generateLinkElement(sitemapUrl, null, null, "dropdown-item " + HTML_CLASS_FANCYFRAME_REFRESH,
                bundle.getString(InternationalizationConstants.KEY_CMS_SITEMAP), "glyphicons glyphicons-basic-map-marker");
        sitemapItem.add(sitemapLink);


        // Media library
        Element mediaLibraryItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        cmsEditionMenu.add(mediaLibraryItem);
        String mediaLibraryTitle = bundle.getString(InternationalizationConstants.KEY_CMS_MEDIA_LIB);
        String mediaLibraryURL = cmsService.getEcmUrl(cmsCtx, EcmViews.gotoMediaLibrary, basePath, requestParameters);
        if (StringUtils.isNotBlank(mediaLibraryURL)) {
            // Link
            Element mediaLibraryLink = DOM4JUtils.generateLinkElement(mediaLibraryURL, HTMLConstants.TARGET_NEW_WINDOW, null, "dropdown-item", mediaLibraryTitle,
                    "glyphicons glyphicons-basic-book-library");
            mediaLibraryItem.add(mediaLibraryLink);
        } else {
            // Link
            Element mediaLibraryLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "dropdown-item disabled", mediaLibraryTitle,
                    "glyphicons glyphicons-basic-book-library");
            String noMediaLib = bundle.getString(InternationalizationConstants.KEY_PTITLE_NO_MEDIA_LIB);
            DOM4JUtils.addAttribute(mediaLibraryLink, HTMLConstants.TITLE, noMediaLib);
            mediaLibraryItem.add(mediaLibraryLink);
        }
    }

    /**
     * Utility method used to generation administration toggle version.
     *
     * @param context        controller context
     * @param page           current page
     * @param administration administration toolbar element
     * @throws CMSException
     */
    private void generateAdministrationToggleVersion(ControllerContext context, Page page, Element administration) throws CMSException {
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(context.getServerInvocation().getRequest().getLocale());
        // Edition mode
        String editionMode = CmsPermissionHelper.getCurrentCmsEditionMode(context);

        // CMS context
        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(context.getServerInvocation());
        cmsCtx.setControllerContext(context);

        // Current CMS item
        String pagePath = (String) context.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path");
        cmsCtx.setDisplayLiveVersion("1");
        CMSItem liveDoc = cmsService.getContent(cmsCtx, pagePath);
        CMSPublicationInfos publicationInfos = cmsService.getPublicationInfos(cmsCtx, pagePath);
        // Current CMS item path
        String path = liveDoc.getPath();
        // beingModified indicator
        boolean beingModified = publicationInfos.isBeingModified();


        // Titles
        String editionTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_EDITION);
        String previewTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_PREVIEW);
        String onlineTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_ONLINE);


        // LI
        Element li = DOM4JUtils.generateElement(HTMLConstants.LI, "d-flex align-items-center my-2 mx-sm-3 my-sm-0", null);
        administration.add(li);


        // Buttons group
        Element buttonsGroup = DOM4JUtils.generateDivElement("btn-group btn-group-sm navbar-form");
        li.add(buttonsGroup);


        // Online button
        ChangeCMSEditionModeCommand onlineCommand = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                CmsPermissionHelper.CMS_VERSION_ONLINE, editionMode);
        String onlineURL = new PortalURLImpl(onlineCommand, context, null, null).toString();

        Element online = DOM4JUtils.generateLinkElement(onlineURL, null, null, null, null, "glyphicons glyphicons-basic-globe");

        if (CmsPermissionHelper.getCurrentCmsVersion(context).equals(CmsPermissionHelper.CMS_VERSION_ONLINE)) {
            DOM4JUtils.addAttribute(online, HTMLConstants.CLASS, "btn btn-success active");
            Element displayText = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, onlineTitle);
            online.add(displayText);
        } else {
            DOM4JUtils.addAttribute(online, HTMLConstants.CLASS, "btn btn-outline-secondary");
            DOM4JUtils.addTooltip(online, onlineTitle);
        }

        buttonsGroup.add(online);


        // Preview button
        ChangeCMSEditionModeCommand previewCommand = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                CmsPermissionHelper.CMS_VERSION_PREVIEW, CmsPermissionHelper.CMS_EDITION_MODE_OFF);
        String previewURL = new PortalURLImpl(previewCommand, context, null, null).toString();

        Element preview = DOM4JUtils.generateLinkElement(previewURL, null, null, null, null, "glyphicons glyphicons-basic-eye");

        if (!CmsPermissionHelper.getCurrentCmsVersion(context).equals(CmsPermissionHelper.CMS_VERSION_ONLINE)
                && CmsPermissionHelper.getCurrentCmsEditionMode(context).equals(CmsPermissionHelper.CMS_EDITION_MODE_OFF)) {
            DOM4JUtils.addAttribute(preview, HTMLConstants.CLASS, "btn btn-info active");
            Element displayText = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, previewTitle);
            preview.add(displayText);
        } else {
            DOM4JUtils.addAttribute(preview, HTMLConstants.CLASS, "btn btn-outline-secondary");
            DOM4JUtils.addTooltip(preview, previewTitle);
        }

        buttonsGroup.add(preview);


        // Edition button
        ChangeCMSEditionModeCommand editionCommand = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                CmsPermissionHelper.CMS_VERSION_PREVIEW, CmsPermissionHelper.CMS_EDITION_MODE_ON);
        String editionURL = new PortalURLImpl(editionCommand, context, null, null).toString();

        Element edition = DOM4JUtils.generateLinkElement(editionURL, null, null, null, null, "glyphicons glyphicons-basic-pencil");

        if (!CmsPermissionHelper.getCurrentCmsVersion(context).equals(CmsPermissionHelper.CMS_VERSION_ONLINE)
                && CmsPermissionHelper.getCurrentCmsEditionMode(context).equals(CmsPermissionHelper.CMS_EDITION_MODE_ON)) {
            DOM4JUtils.addAttribute(edition, HTMLConstants.CLASS, "btn btn-warning active");
            Element displayText = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, editionTitle);
            edition.add(displayText);
        } else {
            DOM4JUtils.addAttribute(edition, HTMLConstants.CLASS, "btn btn-outline-secondary");
            DOM4JUtils.addTooltip(edition, editionTitle);
        }

        buttonsGroup.add(edition);

        if (beingModified) {
            // Current modification indicator
            final Element modificationIndicator = DOM4JUtils.generateElement("span", "badge bg-secondary", bundle.getString("MODIFICATION_MESSAGE"), "glyphicons glyphicons-basic-star-of-life", AccessibilityRoles.PRESENTATION);
            ((Element) modificationIndicator.elements().get(1)).addAttribute(HTMLConstants.CLASS, "hidden-sm");
            li.add(modificationIndicator);
        }
    }

    /**
     * Fancybox for delete page.
     *
     * @param bundle    bundle
     * @param urlDelete the command for delete
     * @return fancybox DOM element
     * @throws UnsupportedEncodingException
     */
    private Element generateDeleteFancyBox(Bundle bundle, String urlDelete) throws UnsupportedEncodingException {
        String[] urlSplit = urlDelete.split("\\?");
        String action = urlSplit[0];
        String[] args = urlSplit[1].split("&");

        // Root
        Element root = DOM4JUtils.generateDivElement("d-none");

        // Container
        Element container = DOM4JUtils.generateDivElement("container-fluid");
        DOM4JUtils.addAttribute(container, HTMLConstants.ID, "delete_cms_page");
        root.add(container);

        // Form
        Element form = DOM4JUtils.generateElement(HTMLConstants.FORM, "text-center", null, null, AccessibilityRoles.FORM);
        DOM4JUtils.addAttribute(form, HTMLConstants.ACTION, action);
        DOM4JUtils.addAttribute(form, HTMLConstants.METHOD, HTMLConstants.FORM_METHOD_GET);
        container.add(form);

        // Message
        Element message = DOM4JUtils.generateElement(HTMLConstants.P, null, bundle.getString("CMS_DELETE_CONFIRM_MESSAGE"));
        form.add(message);

        // Hidden fields
        for (String arg : args) {
            String[] argSplit = arg.split("=");
            Element hidden = DOM4JUtils.generateElement(HTMLConstants.INPUT, null, null);
            DOM4JUtils.addAttribute(hidden, HTMLConstants.TYPE, HTMLConstants.INPUT_TYPE_HIDDEN);
            DOM4JUtils.addAttribute(hidden, HTMLConstants.NAME, argSplit[0]);
            DOM4JUtils.addAttribute(hidden, HTMLConstants.VALUE, URLDecoder.decode(argSplit[1], CharEncoding.UTF_8));
            form.add(hidden);
        }

        // OK button
        Element okButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-secondary btn-warning", bundle.getString("YES"), "halflings halflings-alert",
                null);
        DOM4JUtils.addAttribute(okButton, HTMLConstants.TYPE, HTMLConstants.INPUT_TYPE_SUBMIT);
        form.add(okButton);

        // Cancel button
        Element cancelButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-secondary", bundle.getString("NO"));
        DOM4JUtils.addAttribute(cancelButton, HTMLConstants.TYPE, HTMLConstants.INPUT_TYPE_BUTTON);
        DOM4JUtils.addAttribute(cancelButton, HTMLConstants.ONCLICK, "closeFancybox()");
        form.add(cancelButton);

        return root;
    }

    /**
     * Generate erase confirmation fancybox.
     *
     * @param bundle   bundle
     * @param urlErase the command for delete
     * @return fancybox DOM element
     * @throws UnsupportedEncodingException
     */
    private Element generateEraseFancyBox(Bundle bundle, String urlErase) throws UnsupportedEncodingException {
        // Root
        Element root = DOM4JUtils.generateDivElement("d-none");

        // Container
        Element container = DOM4JUtils.generateDivElement("container-fluid text-center");
        DOM4JUtils.addAttribute(container, HTMLConstants.ID, "erase_cms_page");
        root.add(container);

        // Message
        Element message = DOM4JUtils.generateElement(HTMLConstants.P, null, bundle.getString("CMS_ERASE_CONFIRM_MESSAGE"));
        container.add(message);

        // OK button
        Element okButton = DOM4JUtils.generateLinkElement(urlErase, null, null, "btn btn-secondary btn-warning", bundle.getString("YES"),
                "halflings halflings-alert");
        container.add(okButton);

        // Cancel button
        Element cancelButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-secondary", bundle.getString("NO"));
        DOM4JUtils.addAttribute(cancelButton, HTMLConstants.TYPE, HTMLConstants.INPUT_TYPE_BUTTON);
        DOM4JUtils.addAttribute(cancelButton, HTMLConstants.ONCLICK, "closeFancybox()");
        container.add(cancelButton);

        return root;
    }


    /**
     * Generate userbar HTML content.
     *
     * @param controllerContext controller context
     * @param page              current page
     * @param principal         principal
     * @param person            directory person
     * @param mySpaceURL        my space URL
     * @param signOutURL        sign out URL
     * @return HTML data
     * @throws Exception
     */
    private String formatHTMLUserbar(ControllerContext controllerContext, Page page, Principal principal, Person person, String mySpaceURL, String myProfileUrl,
                                     String signOutURL) {
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(controllerContext.getServerInvocation().getRequest().getLocale());
        // CMS service
        // ICMSService cmsService = cmsServiceLocator.getCMSService();
        // CMS context
        // CMSServiceCtx cmsCtx = new CMSServiceCtx();
        // cmsCtx.setServerInvocation(controllerContext.getServerInvocation());
        // cmsCtx.setControllerContext(controllerContext);


        // User informations
        String userName;
        String userAvatarSrc = null;
        if (person != null) {
            userName = person.getDisplayName();
            if (person.getAvatar() != null) {
                userAvatarSrc = person.getAvatar().getUrl();
            } else {
                userAvatarSrc = null;
            }
        } else if (principal != null) {
            userName = principal.getName();
            // try {
            // userAvatarSrc = cmsService.getUserAvatar(cmsCtx, userName).getUrl();
            // } catch (CMSException e) {
            // userAvatarSrc = null;
            // }
        } else {
            userName = bundle.getString(InternationalizationConstants.KEY_USER_GUEST);
            // try {
            // userAvatarSrc = cmsService.getUserAvatar(cmsCtx, "nobody").getUrl();
            // } catch (CMSException e) {
            // userAvatarSrc = null;
            // }
        }


        // Userbar dropdown element
        Element userbarDropdown = DOM4JUtils.generateElement(HTMLConstants.LI, "nav-item dropdown", null);

        // Userbar menu title
        Element userbarDropdownTitle = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, "nav-link dropdown-toggle", null);
        DOM4JUtils.addAttribute(userbarDropdownTitle, HTMLConstants.DATA_TOGGLE, "dropdown");
        if (userAvatarSrc != null) {
            Element avatar = DOM4JUtils.generateElement(HTMLConstants.IMG, "avatar", null);
            DOM4JUtils.addAttribute(avatar, HTMLConstants.SRC, userAvatarSrc);
            userbarDropdownTitle.add(avatar);
        }
        Element displayName = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, userName);
        userbarDropdownTitle.add(displayName);
        userbarDropdown.add(userbarDropdownTitle);

        // Userbar menu node
        Element userbarMenu = DOM4JUtils.generateElement(HTMLConstants.DIV, "dropdown-menu", null);
        userbarDropdown.add(userbarMenu);

        if (principal != null) {
            // My space
            // Element mySpace = DOM4JUtils.generateLinkElement(mySpaceURL, null, null, null,
            // bundle.getString(InternationalizationConstants.KEY_MY_SPACE_), "halflings star",
            // AccessibilityRoles.MENU_ITEM);
            // this.addSubMenuElement(userbarMenuUl, mySpace, null, null);


            if (person != null) {
                // View profile

                Element viewProfile = DOM4JUtils.generateLinkElement(myProfileUrl, null, null, "dropdown-item",
                        bundle.getString(InternationalizationConstants.KEY_MY_PROFILE), "glyphicons glyphicons-basic-user");
                userbarMenu.add(viewProfile);

            }


            // Logout
            Element signOut = DOM4JUtils.generateLinkElement(signOutURL, null, null, "dropdown-item", bundle.getString(InternationalizationConstants.KEY_LOGOUT),
                    "glyphicons glyphicons-basic-log-out");
            userbarMenu.add(signOut);
        } else {
            // Login
            Element login = DOM4JUtils.generateLinkElement(mySpaceURL, null, null, "dropdown-item", bundle.getString(InternationalizationConstants.KEY_LOGIN),
                    "glyphicons glyphicons-basic-log-in");
            userbarMenu.add(login);
        }


        // Write HTML content
        return DOM4JUtils.write(userbarDropdown);
    }


    /**
     * Add dropdown divider.
     *
     * @param menu dropdown menu
     */
    private void addDropdownDivider(Element menu) {
        Element dividerItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        menu.add(dividerItem);

        Element divider = DOM4JUtils.generateElement(HTMLConstants.HR, "dropdown-divider", null);
        dividerItem.add(divider);
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
