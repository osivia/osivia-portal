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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.SignOutCommand;
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
import org.osivia.portal.api.directory.IDirectoryService;
import org.osivia.portal.api.directory.IDirectoryServiceLocator;
import org.osivia.portal.api.directory.entity.DirectoryPerson;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.urls.EcmCommand;
import org.osivia.portal.api.urls.IPortalUrlFactory;
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
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.page.MonEspaceCommand;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.security.CmsPermissionHelper;

/**
 * Toolbar attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class ToolbarAttributesBundle implements IAttributesBundle {

    /** HTML class "toolbar-administration". */
    private static final String HTML_CLASS_TOOLBAR_ADMINISTRATION = "nav navbar-nav navbar-left";

    /** HTML class "dropdown". */
    private static final String HTML_CLASS_DROPDOWN = "dropdown";
    /** HTML class "dropdown-toggle". */
    private static final String HTML_CLASS_DROPDOWN_TOGGLE = "dropdown-toggle";
    /** HTML class "dropdown-menu". */
    private static final String HTML_CLASS_DROPDOWN_MENU = "dropdown-menu";
    /** HTML class "caret". */
    private static final String HTML_CLASS_DROPDOWN_CARET = "caret";
    /** HTML class "divider". */
    private static final String HTML_CLASS_DROPDOWN_DIVIDER = "divider";

    /** HTML class "fancybox_inline". */
    private static final String HTML_CLASS_FANCYBOX_INLINE = "fancybox_inline";
    /** HTML class "fancybox_refresh". */
    private static final String HTML_CLASS_FANCYFRAME_REFRESH = "fancyframe_refresh";

    /** Pages list URL. */
    private static final String URL_PAGES_LIST = "#pages-list";
    /** Page creation URL. */
    private static final String URL_PAGE_CREATION = "#page-creation";
    /** Template creation URL. */
    private static final String URL_TEMPLATE_CREATION = "#template-creation";
    /** Page suppression URL. */
    private static final String URL_PAGE_SUPPRESSION = "#page-suppression";
    /** Page location URL. */
    private static final String URL_PAGE_LOCATION = "#page-location";
    /** Page properties URL. */
    private static final String URL_PAGE_PROPERTIES = "#page-properties";
    /** Page CMS URL. */
    private static final String URL_PAGE_CMS = "#page-cms";
    /** Page rights URL. */
    private static final String URL_PAGE_RIGHTS = "#page-rights";


    /** Singleton instance. */
    private static ToolbarAttributesBundle instance;


    /** Bundle factory. */
    private final IBundleFactory bundleFactory;
    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;
    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;

    /** Directory service locator. */
    private final IDirectoryServiceLocator directoryServiceLocator;

    /** Administration portal identifier. */
    private final PortalObjectId adminPortalId;

    /** Toolbar attributes names. */
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
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        // Directory service locator
        this.directoryServiceLocator = Locator.findMBean(IDirectoryServiceLocator.class, IDirectoryServiceLocator.MBEAN_NAME);


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
        DirectoryPerson person = null;
        if (principal != null) {
            IDirectoryService directoryService = this.directoryServiceLocator.getDirectoryService();
            if (directoryService != null) {
                person = directoryService.getPerson(principal.getName());
                attributes.put(Constants.ATTR_TOOLBAR_PERSON, person);
            }
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
                PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

                Map<String, String> properties = new HashMap<String, String>();
				properties.put("osivia.hideTitle", "1");

                Map<String, String> parameters = new HashMap<String, String>();

				myProfileUrl = this.urlFactory.getStartPortletInNewPage(portalControllerContext, "myprofile",
						bundle.getString(InternationalizationConstants.KEY_MY_PROFILE), "toutatice-identite-fichepersonne-portailPortletInstance", properties,
						parameters);

            } catch (PortalException e) {
                // Do nothing
            }
        }
        attributes.put(Constants.ATTR_TOOLBAR_MY_PROFILE, myProfileUrl);

        // Refresh page
        RefreshPageCommand refreshPageCommand = new RefreshPageCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT));
        PortalURL refreshPagePortalUrl = new PortalURLImpl(refreshPageCommand, controllerContext, false, null);
        attributes.put(Constants.ATTR_TOOLBAR_REFRESH_PAGE_URL, refreshPagePortalUrl.toString());

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
    }


    /**
     * Utility method used to generate administration HTML content.
     *
     * @param context controller context
     * @param page current page
     * @return HTML data
     * @throws Exception
     */
    private String formatHTMLAdministration(ControllerContext context, Page page) {
        PageType pageType = PageType.getPageType(page, context);

        // Administration root element
        Element administration = DOM4JUtils.generateElement(HTMLConstants.UL, HTML_CLASS_TOOLBAR_ADMINISTRATION, StringUtils.EMPTY);

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
     * @param context controller context
     * @param page current page
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

        // Configuration menu root element
        Element configurationMenu = DOM4JUtils.generateElement(HTMLConstants.LI, HTML_CLASS_DROPDOWN, null);
        administration.add(configurationMenu);

        // Configuration menu title
        String menuTitle = bundle.getString(InternationalizationConstants.KEY_CONFIGURATION_MENU_TITLE);
        Element configurationMenuTitle = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, HTML_CLASS_DROPDOWN_TOGGLE, menuTitle,
                "halflings uni-wrench");
        DOM4JUtils.addAttribute(configurationMenuTitle, HTMLConstants.DATA_TOGGLE, "dropdown");
        Element caret = DOM4JUtils.generateElement(HTMLConstants.SPAN, HTML_CLASS_DROPDOWN_CARET, StringUtils.EMPTY);
        configurationMenuTitle.add(caret);
        configurationMenu.add(configurationMenuTitle);

        // Configuration menu "ul" node
        Element configurationMenuUL = DOM4JUtils.generateElement(HTMLConstants.UL, HTML_CLASS_DROPDOWN_MENU, null, null, AccessibilityRoles.MENU);
        configurationMenu.add(configurationMenuUL);

        // Home
        String homeURL = context.getServerInvocation().getServerContext().getPortalContextPath();
        String homeTitle = bundle.getString(InternationalizationConstants.KEY_HOME);
        Element home = DOM4JUtils.generateLinkElement(homeURL, null, null, null, homeTitle, "halflings home", AccessibilityRoles.MENU_ITEM);
        this.addSubMenuElement(configurationMenuUL, home, null);

        // OSIVIA Portal administration
        String osiviaAdministrationURL = StringUtils.EMPTY;
        try {
            osiviaAdministrationURL = this.urlFactory.getStartPortletUrl(portalControllerContext, InternalConstants.PORTLET_ADMINISTRATION_INSTANCE_NAME, null,
                    true);
        } catch (Exception e) {
            // Do nothing
        }
        String osiviaAdministrationTitle = bundle.getString(InternationalizationConstants.KEY_OSIVIA_ADMINISTRATION);
        Element osiviaAdministration = DOM4JUtils.generateLinkElement(osiviaAdministrationURL, null, null, HTML_CLASS_FANCYFRAME_REFRESH,
                osiviaAdministrationTitle, "settings", AccessibilityRoles.MENU_ITEM);
        this.addSubMenuElement(configurationMenuUL, osiviaAdministration, null);

        // JBoss administration
        ViewPageCommand jbossAdministrationCommand = new ViewPageCommand(this.adminPortalId);
        String jbossAdministrationURL = new PortalURLImpl(jbossAdministrationCommand, context, null, null).toString();
        String jbossAdministrationTitle = bundle.getString(InternationalizationConstants.KEY_JBOSS_ADMINISTRATION);
        Element jbossAdministration = DOM4JUtils.generateLinkElement(jbossAdministrationURL, HTMLConstants.TARGET_NEW_WINDOW, null, null,
                jbossAdministrationTitle, "halflings dashboard", AccessibilityRoles.MENU_ITEM);
        Element jbossAdministrationNewWindowGlyph = DOM4JUtils.generateElement(HTMLConstants.I, "glyphicons new_window_alt", StringUtils.EMPTY);
        jbossAdministration.add(jbossAdministrationNewWindowGlyph);
        this.addSubMenuElement(configurationMenuUL, jbossAdministration, null);

		// Divider
		this.addSubMenuElement(configurationMenuUL, null, HTML_CLASS_DROPDOWN_DIVIDER);

		// Identity administration

		// users
		Map<String, String> properties = new HashMap<String, String>();
		Map<String, String> parameters = new HashMap<String, String>();

		try {

            properties.put(DynaRenderOptions.PARTIAL_REFRESH_ENABLED, Constants.PORTLET_VALUE_ACTIVATE);
            properties.put("osivia.ajaxLink", "1");
			properties.put("osivia.hideTitle", "1");

			String usersAdministrationURL = this.urlFactory.getStartPortletInNewPage(portalControllerContext, "usermanagement",
					bundle.getString(InternationalizationConstants.KEY_USERS_ADMINISTRATION), "toutatice-identite-gestionpersonnes-portailPortletInstance",
					properties, parameters);

			String usersAdministrationTitle = bundle.getString(InternationalizationConstants.KEY_USERS_ADMINISTRATION);
			Element usersAdministration = DOM4JUtils.generateLinkElement(usersAdministrationURL, null, null, null, usersAdministrationTitle, "parents",
					AccessibilityRoles.MENU_ITEM);
			this.addSubMenuElement(configurationMenuUL, usersAdministration, null);
		} catch (PortalException e) {
			// no item if portlet is not present
		}

		try {

			String groupsAdministrationURL = this.urlFactory.getStartPortletInNewPage(portalControllerContext, "groupmanagement",
					bundle.getString(InternationalizationConstants.KEY_GROUPS_ADMINISTRATION), "toutatice-identite-gestiongroupes-portailPortletInstance",
					properties, parameters);


			String groupsAdministrationTitle = bundle.getString(InternationalizationConstants.KEY_GROUPS_ADMINISTRATION);
			Element groupsAdministration = DOM4JUtils.generateLinkElement(groupsAdministrationURL, null, null, null, groupsAdministrationTitle, "group",
					AccessibilityRoles.MENU_ITEM);
			this.addSubMenuElement(configurationMenuUL, groupsAdministration, null);
		} catch (PortalException e) {
			// no item if portlet is not present
		}


		// Divider
		this.addSubMenuElement(configurationMenuUL, null, HTML_CLASS_DROPDOWN_DIVIDER);

        // Pages list
        String pagesListTitle = bundle.getString(InternationalizationConstants.KEY_PAGES_LIST);
        Element pagesList = DOM4JUtils.generateLinkElement(URL_PAGES_LIST, null, null, HTML_CLASS_FANCYBOX_INLINE, pagesListTitle,
                "halflings sort-by-alphabet", AccessibilityRoles.MENU_ITEM);
        this.addSubMenuElement(configurationMenuUL, pagesList, null);

        // Divider
        this.addSubMenuElement(configurationMenuUL, null, HTML_CLASS_DROPDOWN_DIVIDER);

        if (InternalConstants.PORTAL_TYPE_STATIC_PORTAL.equals(page.getPortal().getDeclaredProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE))) {
            // Page creation
            String pageCreationTitle = bundle.getString(InternationalizationConstants.KEY_PAGE_CREATION);
            Element pageCreation = DOM4JUtils.generateLinkElement(URL_PAGE_CREATION, null, null, HTML_CLASS_FANCYBOX_INLINE, pageCreationTitle,
                    "halflings plus", AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(configurationMenuUL, pageCreation, null);
        }

        // Template creation
        String templateCreationTitle = bundle.getString(InternationalizationConstants.KEY_TEMPLATE_CREATION);
        Element templateCreation = DOM4JUtils.generateLinkElement(URL_TEMPLATE_CREATION, null, null, HTML_CLASS_FANCYBOX_INLINE, templateCreationTitle,
                "halflings plus", AccessibilityRoles.MENU_ITEM);
        this.addSubMenuElement(configurationMenuUL, templateCreation, null);

        // Page template access
        String pageTemplateAccessTitle = bundle.getString(InternationalizationConstants.KEY_PAGE_TEMPLATE_ACCESS);
        if (pageType.isTemplated()) {
            // URL
            ITemplatePortalObject templatePortalObject = (ITemplatePortalObject) page;
            ViewPageCommand pageTemplateAccessCommand = new ViewPageCommand(templatePortalObject.getTemplate().getId());
            String pageTemplateAccessURL = new PortalURLImpl(pageTemplateAccessCommand, context, null, null).toString();
            pageTemplateAccessURL += "?init-state=true&edit-template-mode=true&original-portal=" + portalName;

            // Link
            Element pageTemplateAccessLink = DOM4JUtils.generateLinkElement(pageTemplateAccessURL, null, null, null, pageTemplateAccessTitle,
                    "construction_cone", AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(configurationMenuUL, pageTemplateAccessLink, null);
        } else {
            // Link
            Element pageTemplateAccessLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, null, pageTemplateAccessTitle,
                    "construction_cone", AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(configurationMenuUL, pageTemplateAccessLink, "disabled");
        }

        // Divider
        this.addSubMenuElement(configurationMenuUL, null, HTML_CLASS_DROPDOWN_DIVIDER);

        // Caches initialization
        ViewPageCommand cachesInitializationCommand = new ViewPageCommand(page.getId());
        String cachesInitializationURL = new PortalURLImpl(cachesInitializationCommand, context, null, null).toString();
        cachesInitializationURL += "?init-cache=true";
        String cachesInitializationTitle = bundle.getString(InternationalizationConstants.KEY_CACHES_INITIALIZATION);
        Element cachesInitialization = DOM4JUtils.generateLinkElement(cachesInitializationURL, null, null, null, cachesInitializationTitle, "restart",
                AccessibilityRoles.MENU_ITEM);
        this.addSubMenuElement(configurationMenuUL, cachesInitialization, null);
    }


    /**
     * Utility method used to generate edition menu for administration toolbar.
     *
     * @param context controller context
     * @param page current page
     * @param administration administration toolbar element
     */
    private void generateAdministrationEditionMenu(ControllerContext context, Page page, Element administration) {
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(context.getServerInvocation().getRequest().getLocale());
        // Page type
        PageType pageType = PageType.getPageType(page, context);

        // Edition menu root element
        Element editionMenu = DOM4JUtils.generateElement(HTMLConstants.LI, HTML_CLASS_DROPDOWN, null);
        administration.add(editionMenu);

        // Edition menu title
        String menuTitle;
        if (pageType.isSpace()) {
            menuTitle = bundle.getString(InternationalizationConstants.KEY_SPACE_EDITION_MENU_TITLE);
        } else if (PortalObjectUtils.isTemplate(page)) {
            menuTitle = bundle.getString(InternationalizationConstants.KEY_TEMPLATE_EDITION_MENU_TITLE);
        } else {
            menuTitle = bundle.getString(InternationalizationConstants.KEY_PAGE_EDITION_MENU_TITLE);
        }
        Element editionMenuTitle = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, HTML_CLASS_DROPDOWN_TOGGLE, menuTitle,
                "halflings pencil");
        DOM4JUtils.addAttribute(editionMenuTitle, HTMLConstants.DATA_TOGGLE, "dropdown");
        Element caret = DOM4JUtils.generateElement(HTMLConstants.SPAN, HTML_CLASS_DROPDOWN_CARET, StringUtils.EMPTY);
        editionMenuTitle.add(caret);
        editionMenu.add(editionMenuTitle);

        // Edition menu "ul" node
        Element editionMenuUL = DOM4JUtils.generateElement(HTMLConstants.UL, HTML_CLASS_DROPDOWN_MENU, null, null, AccessibilityRoles.MENU);
        editionMenu.add(editionMenuUL);

        if (!pageType.isTemplated()) {
            // Icons display
            String mode = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_WINDOWS_SETTING_MODE);
            ChangeModeCommand changeModeCommand;
            String modeHtmlClass;
            if (InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(mode)) {
                changeModeCommand = new ChangeModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), StringUtils.EMPTY);
                modeHtmlClass = "halflings check";
            } else {
                changeModeCommand = new ChangeModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT),
                        InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE);
                modeHtmlClass = "halflings unchecked";
            }
            String changeModeURL = new PortalURLImpl(changeModeCommand, context, null, null).toString();
            String changeModeTitle = bundle.getString(InternationalizationConstants.KEY_ICONS_DISPLAY);

            Element iconsDisplay = DOM4JUtils
                    .generateLinkElement(changeModeURL, null, null, null, changeModeTitle, modeHtmlClass, AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(editionMenuUL, iconsDisplay, null);

            // Divider
            this.addSubMenuElement(editionMenuUL, null, HTML_CLASS_DROPDOWN_DIVIDER);
        }

        // Page suppression
        String pageSuppressionTitle = bundle.getString(InternationalizationConstants.KEY_SUPPRESSION);
        if (PortalObjectUtils.isPortalDefaultPage(page)) {
            Element pageSuppression = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, null, pageSuppressionTitle, "halflings remove",
                    AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(editionMenuUL, pageSuppression, "disabled");
        } else {
            Element pageSuppression = DOM4JUtils.generateLinkElement(URL_PAGE_SUPPRESSION, null, null, HTML_CLASS_FANCYBOX_INLINE, pageSuppressionTitle,
                    "halflings remove", AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(editionMenuUL, pageSuppression, null);
        }

        // Page location
        String pageLocationTitle = bundle.getString(InternationalizationConstants.KEY_LOCATION);
        Element pageLocation = DOM4JUtils.generateLinkElement(URL_PAGE_LOCATION, null, null, HTML_CLASS_FANCYBOX_INLINE, pageLocationTitle, "halflings sort",
                AccessibilityRoles.MENU_ITEM);
        this.addSubMenuElement(editionMenuUL, pageLocation, null);

        // Divider
        this.addSubMenuElement(editionMenuUL, null, HTML_CLASS_DROPDOWN_DIVIDER);

        // Page properties
        String pagePropertiesTitle = bundle.getString(InternationalizationConstants.KEY_PROPERTIES);
        Element pageProperties = DOM4JUtils.generateLinkElement(URL_PAGE_PROPERTIES, null, null, HTML_CLASS_FANCYBOX_INLINE, pagePropertiesTitle,
                "halflings cog", AccessibilityRoles.MENU_ITEM);
        this.addSubMenuElement(editionMenuUL, pageProperties, null);

        // Page CMS configuration
        String pageCMSTitle = bundle.getString(InternationalizationConstants.KEY_CMS_CONFIGURATION);
        Element pageCMS = DOM4JUtils.generateLinkElement(URL_PAGE_CMS, null, null, HTML_CLASS_FANCYBOX_INLINE, pageCMSTitle, "halflings cog",
                AccessibilityRoles.MENU_ITEM);
        this.addSubMenuElement(editionMenuUL, pageCMS, null);

        // Page rights
        String pageRightsTitle = bundle.getString(InternationalizationConstants.KEY_RIGHTS);
        Element pageRights = DOM4JUtils.generateLinkElement(URL_PAGE_RIGHTS, null, null, HTML_CLASS_FANCYBOX_INLINE, pageRightsTitle, "halflings cog",
                AccessibilityRoles.MENU_ITEM);
        this.addSubMenuElement(editionMenuUL, pageRights, null);
    }


    /**
     * Utility method used to generate web page menu for administration toolbar.
     *
     * @param context controller context
     * @param page current page
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
        Map<String, String> requestParameters = new HashMap<String, String>();

        // CMS context
        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(context.getServerInvocation());
        cmsCtx.setControllerContext(context);

        // CMS publication informations
        String pagePath = (String) context.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path");
        cmsCtx.setDisplayLiveVersion("1");
        CMSPublicationInfos publicationInfos = cmsService.getPublicationInfos(cmsCtx, pagePath);
        // Path
        String path = publicationInfos.getDocumentPath();
        // Published indicator
        Boolean published = publicationInfos.isPublished();

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


        // CMS edition menu root element
        Element cmsEditionMenu = DOM4JUtils.generateElement(HTMLConstants.LI, HTML_CLASS_DROPDOWN, null);
        administration.add(cmsEditionMenu);

        // CMS edition menu title
        String title = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE);
        Element cmsEditionMenuTitle = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, HTML_CLASS_DROPDOWN_TOGGLE, title,
                "halflings pencil");
        DOM4JUtils.addAttribute(cmsEditionMenuTitle, HTMLConstants.DATA_TOGGLE, "dropdown");
        Element caret = DOM4JUtils.generateElement(HTMLConstants.SPAN, HTML_CLASS_DROPDOWN_CARET, StringUtils.EMPTY);
        cmsEditionMenuTitle.add(caret);
        cmsEditionMenu.add(cmsEditionMenuTitle);

        // CMS edition menu "ul" node
        Element cmsEditionMenuUL = DOM4JUtils.generateElement(HTMLConstants.UL, HTML_CLASS_DROPDOWN_MENU, null, null, AccessibilityRoles.MENU);
        cmsEditionMenu.add(cmsEditionMenuUL);


        // Preview required message
        String previewRequired = bundle.getString(InternationalizationConstants.KEY_PTITLE_PREVIEW_MODE_REQUIRED);


        // Toggle avanced CMS tools
        boolean showAdvancedTools = BooleanUtils.toBoolean((Boolean) context.getAttribute(Scope.SESSION_SCOPE, InternalConstants.SHOW_ADVANCED_CMS_TOOLS_INDICATOR));
        String toggleAdvancedToolsTitle = bundle.getString(InternationalizationConstants.KEY_CMS_TOGGLE_ADVANCED_TOOLS);
        String toggleAdvancedToolsGlyphicon;
        if (showAdvancedTools) {
            toggleAdvancedToolsGlyphicon = "halflings check";
        } else {
            toggleAdvancedToolsGlyphicon = "halflings unchecked";
        }
        if (modePreview) {
            // URL
            String pageId = page.getId().toString(PortalObjectPath.SAFEST_FORMAT);
            ControllerCommand toggleAdvancedToolsCommand = new ToggleAdvancedCMSToolsCommand(pageId);
            String toggleAdvancedToolsURL = new PortalURLImpl(toggleAdvancedToolsCommand, context, null, null).toString();

            // Link
            Element toggleAdvancedToolsLink = DOM4JUtils.generateLinkElement(toggleAdvancedToolsURL, null, null, null, toggleAdvancedToolsTitle,
                    toggleAdvancedToolsGlyphicon, AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(cmsEditionMenuUL, toggleAdvancedToolsLink, null);
        } else {
            // Link
            Element toggleAdvancedToolsLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, null, toggleAdvancedToolsTitle,
                    toggleAdvancedToolsGlyphicon, AccessibilityRoles.MENU_ITEM);
            DOM4JUtils.addAttribute(toggleAdvancedToolsLink, HTMLConstants.TITLE, previewRequired);
            this.addSubMenuElement(cmsEditionMenuUL, toggleAdvancedToolsLink, "disabled");
        }


        // Divider
        this.addSubMenuElement(cmsEditionMenuUL, null, HTML_CLASS_DROPDOWN_DIVIDER);


        // CMS create page
        String cmsCreatePageTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_CREATE);
        if (modePreview) {
            // URL
            String cmsCreatePageURL = cmsService.getEcmUrl(cmsCtx, EcmCommand.createPage, path, requestParameters);

            // Link
            Element cmsCreatePageLink = DOM4JUtils.generateLinkElement(cmsCreatePageURL, null, onClickCallback, HTML_CLASS_FANCYFRAME_REFRESH,
                    cmsCreatePageTitle, "halflings plus", AccessibilityRoles.MENU_ITEM);
            DOM4JUtils.addAttribute(cmsCreatePageLink, HTMLConstants.ACCESSKEY, "n");
            this.addSubMenuElement(cmsEditionMenuUL, cmsCreatePageLink, null);
        } else {
            // Link
            Element cmsCreatePageLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, null, cmsCreatePageTitle, "halflings plus",
                    AccessibilityRoles.MENU_ITEM);
            DOM4JUtils.addAttribute(cmsCreatePageLink, HTMLConstants.TITLE, previewRequired);
            this.addSubMenuElement(cmsEditionMenuUL, cmsCreatePageLink, "disabled");
        }


        // CMS edit current page
        String cmsEditPageTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_OPTIONS);
        if (modePreview) {
            // URL
            String cmsEditPageURL = cmsService.getEcmUrl(cmsCtx, EcmCommand.editPage, path, requestParameters);

            Element cmsEditPageLink = DOM4JUtils.generateLinkElement(cmsEditPageURL, null, onClickCallback, HTML_CLASS_FANCYFRAME_REFRESH, cmsEditPageTitle,
                    "halflings pencil", AccessibilityRoles.MENU_ITEM);
            DOM4JUtils.addAttribute(cmsEditPageLink, HTMLConstants.ACCESSKEY, "e");
            this.addSubMenuElement(cmsEditionMenuUL, cmsEditPageLink, null);
        } else {
            Element cmsEditPageLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, null, cmsEditPageTitle, "halflings pencil",
                    AccessibilityRoles.MENU_ITEM);
            DOM4JUtils.addAttribute(cmsEditPageLink, HTMLConstants.TITLE, previewRequired);
            this.addSubMenuElement(cmsEditionMenuUL, cmsEditPageLink, "disabled");
        }


        // CMS publish document
        String cmsPublishTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_PUBLISH);
        if (modePreview) {
            // URL
            CMSPublishDocumentCommand cmsPublishCommand = new CMSPublishDocumentCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                    CMSPublishDocumentCommand.PUBLISH);
            String cmsPublishURL = context.renderURL(cmsPublishCommand, urlContext, URLFormat.newInstance(true, true));

            // Link
            Element cmsPublishLink = DOM4JUtils.generateLinkElement(cmsPublishURL, null, null, null, cmsPublishTitle, "halflings ok-circle",
                    AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(cmsEditionMenuUL, cmsPublishLink, null);
        } else {
            // Link
            Element cmsPublishLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, null, cmsPublishTitle, "halflings ok-circle",
                    AccessibilityRoles.MENU_ITEM);
            DOM4JUtils.addAttribute(cmsPublishLink, HTMLConstants.TITLE, previewRequired);
            this.addSubMenuElement(cmsEditionMenuUL, cmsPublishLink, "disabled");
        }


        // CMS unpublish document
        String cmsUnpublishTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_UNPUBLISH);
        if (modePreview && published && !basePath.equals(pagePath)) {
            // URL
            CMSPublishDocumentCommand cmsUnpublishCommand = new CMSPublishDocumentCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                    CMSPublishDocumentCommand.UNPUBLISH);
            String cmsUnpublishURL = context.renderURL(cmsUnpublishCommand, urlContext, URLFormat.newInstance(true, true));

            // Link
            Element cmsUnpublishLink = DOM4JUtils.generateLinkElement(cmsUnpublishURL, null, null, null, cmsUnpublishTitle, "halflings remove-circle",
                    AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(cmsEditionMenuUL, cmsUnpublishLink, null);
        } else {
            // Link
            Element cmsUnpublishLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, null, cmsUnpublishTitle,
                    "halflings remove-circle", AccessibilityRoles.MENU_ITEM);
            if (!modePreview) {
                DOM4JUtils.addAttribute(cmsUnpublishLink, HTMLConstants.TITLE, previewRequired);
            } else if (!published) {
                String publishRequired = bundle.getString(InternationalizationConstants.KEY_PTITLE_PUBLISH_REQUIRED);
                DOM4JUtils.addAttribute(cmsUnpublishLink, HTMLConstants.TITLE, publishRequired);
            } else {
                String notRootPage = bundle.getString(InternationalizationConstants.KEY_PTITLE_NOT_ROOTPAGE);
                DOM4JUtils.addAttribute(cmsUnpublishLink, HTMLConstants.TITLE, notRootPage);
            }

            this.addSubMenuElement(cmsEditionMenuUL, cmsUnpublishLink, "disabled");
        }


        // CMS delete document
        String cmsDeleteTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_DELETE);
        if (modePreview) {
            // User can only delete a document which its parent is editable.
            CMSObjectPath parent = CMSObjectPath.parse(pagePath).getParent();
            String parentPath = parent.toString();
            CMSPublicationInfos parentPubInfos = cmsService.getPublicationInfos(cmsCtx, parentPath);

            // Check if parent is editable and if user is not at the root of the web site
            if (parentPubInfos.isEditableByUser() && !basePath.equals(pagePath)) {
                // URL
                CMSDeleteDocumentCommand cmsDeleteCommand = new CMSDeleteDocumentCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path);
                String cmsDeleteURL = context.renderURL(cmsDeleteCommand, urlContext, URLFormat.newInstance(true, true));

                // Inline fancybox
                Element divDeleteFancyBox = this.generateDeleteFancyBox(bundle, cmsDeleteURL);
                cmsEditionMenu.add(divDeleteFancyBox);

                // Link
                Element cmsDeleteLink = DOM4JUtils.generateLinkElement("#delete_cms_page", null, null, HTML_CLASS_FANCYBOX_INLINE, cmsDeleteTitle,
                        "halflings remove", AccessibilityRoles.MENU_ITEM);
                this.addSubMenuElement(cmsEditionMenuUL, cmsDeleteLink, null);
            } else {
                // Link
                Element cmsDeleteLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, null, cmsDeleteTitle, "halflings remove",
                        AccessibilityRoles.MENU_ITEM);
                String deleteForbidden = bundle.getString(InternationalizationConstants.KEY_PTITLE_DELETE_FORBIDDEN);
                DOM4JUtils.addAttribute(cmsDeleteLink, HTMLConstants.TITLE, deleteForbidden);
                this.addSubMenuElement(cmsEditionMenuUL, cmsDeleteLink, "disabled");
            }
        } else {
            // Link
            Element cmsDeleteLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, null, cmsDeleteTitle, "halflings remove",
                    AccessibilityRoles.MENU_ITEM);
            DOM4JUtils.addAttribute(cmsDeleteLink, HTMLConstants.TITLE, previewRequired);
            this.addSubMenuElement(cmsEditionMenuUL, cmsDeleteLink, "disabled");
        }


        // Divider
        this.addSubMenuElement(cmsEditionMenuUL, null, HTML_CLASS_DROPDOWN_DIVIDER);


        // Site map
        String siteMapTitle = bundle.getString(InternationalizationConstants.KEY_CMS_SITEMAP);
        // URL
        Map<String, String> windowProperties = new HashMap<String, String>();
        windowProperties.put("osivia.cms.basePath", basePath);
        String siteMapURL = this.urlFactory.getStartPortletUrl(new PortalControllerContext(context), "osivia-portal-custom-web-assets-sitemapPortletInstance",
                windowProperties, true);
        // Link
        Element siteMapLink = DOM4JUtils.generateLinkElement(siteMapURL, null, null, HTML_CLASS_FANCYFRAME_REFRESH, siteMapTitle, "halflings map-marker",
                AccessibilityRoles.MENU_ITEM);
        this.addSubMenuElement(cmsEditionMenuUL, siteMapLink, null);


        // Media library
        String mediaLibraryTitle = bundle.getString(InternationalizationConstants.KEY_CMS_MEDIA_LIB);
        String mediaLibraryURL = cmsService.getEcmUrl(cmsCtx, EcmCommand.gotoMediaLibrary, basePath, requestParameters);
        if (StringUtils.isNotBlank(mediaLibraryURL)) {
            // Link
            Element mediaLibraryLink = DOM4JUtils.generateLinkElement(mediaLibraryURL, HTMLConstants.TARGET_NEW_WINDOW, null, null, mediaLibraryTitle,
                    "halflings picture", AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(cmsEditionMenuUL, mediaLibraryLink, null);
        } else {
            // Link
            Element mediaLibraryLink = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, null, mediaLibraryTitle, "halflings picture",
                    AccessibilityRoles.MENU_ITEM);
            String noMediaLib = bundle.getString(InternationalizationConstants.KEY_PTITLE_NO_MEDIA_LIB);
            DOM4JUtils.addAttribute(mediaLibraryLink, HTMLConstants.TITLE, noMediaLib);
            this.addSubMenuElement(cmsEditionMenuUL, mediaLibraryLink, "disabled");
        }
    }

    /**
     * Utility method used to generation administration toggle version.
     *
     * @param context controller context
     * @param page current page
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
        // Current CMS item path
        String path = liveDoc.getPath();


        // Titles
        String editionTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_EDITION);
        String previewTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_PREVIEW);
        String onlineTitle = bundle.getString(InternationalizationConstants.KEY_CMS_PAGE_ONLINE);


        // LI
        Element li = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
        administration.add(li);


        // Buttons group
        Element buttonsGroup = DOM4JUtils.generateDivElement("btn-group navbar-form");
        li.add(buttonsGroup);


        // Online button
        ChangeCMSEditionModeCommand onlineCommand = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                CmsPermissionHelper.CMS_VERSION_ONLINE, editionMode);
        String onlineURL = new PortalURLImpl(onlineCommand, context, null, null).toString();

        Element online = DOM4JUtils.generateLinkElement(onlineURL, null, null, null, null, "global");

        if (CmsPermissionHelper.getCurrentCmsVersion(context).equals(CmsPermissionHelper.CMS_VERSION_ONLINE)) {
            DOM4JUtils.addAttribute(online, HTMLConstants.CLASS, "btn btn-success active");
            Element displayText = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, onlineTitle);
            online.add(displayText);
        } else {
            DOM4JUtils.addAttribute(online, HTMLConstants.CLASS, "btn btn-default");
            DOM4JUtils.addTooltip(online, onlineTitle);
        }

        buttonsGroup.add(online);


        // Preview button
        ChangeCMSEditionModeCommand previewCommand = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                CmsPermissionHelper.CMS_VERSION_PREVIEW, CmsPermissionHelper.CMS_EDITION_MODE_OFF);
        String previewURL = new PortalURLImpl(previewCommand, context, null, null).toString();

        Element preview = DOM4JUtils.generateLinkElement(previewURL, null, null, null, null, "eye_open");

        if (!CmsPermissionHelper.getCurrentCmsVersion(context).equals(CmsPermissionHelper.CMS_VERSION_ONLINE)
                && CmsPermissionHelper.getCurrentCmsEditionMode(context).equals(CmsPermissionHelper.CMS_EDITION_MODE_OFF)) {
            DOM4JUtils.addAttribute(preview, HTMLConstants.CLASS, "btn btn-info active");
            Element displayText = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, previewTitle);
            preview.add(displayText);
        } else {
            DOM4JUtils.addAttribute(preview, HTMLConstants.CLASS, "btn btn-default");
            DOM4JUtils.addTooltip(preview, previewTitle);
        }

        buttonsGroup.add(preview);


        // Edition button
        ChangeCMSEditionModeCommand editionCommand = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                CmsPermissionHelper.CMS_VERSION_PREVIEW, CmsPermissionHelper.CMS_EDITION_MODE_ON);
        String editionURL = new PortalURLImpl(editionCommand, context, null, null).toString();

        Element edition = DOM4JUtils.generateLinkElement(editionURL, null, null, null, null, "pencil");

        if (!CmsPermissionHelper.getCurrentCmsVersion(context).equals(CmsPermissionHelper.CMS_VERSION_ONLINE)
                && CmsPermissionHelper.getCurrentCmsEditionMode(context).equals(CmsPermissionHelper.CMS_EDITION_MODE_ON)) {
            DOM4JUtils.addAttribute(edition, HTMLConstants.CLASS, "btn btn-warning active");
            Element displayText = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, editionTitle);
            edition.add(displayText);
        } else {
            DOM4JUtils.addAttribute(edition, HTMLConstants.CLASS, "btn btn-default");
            DOM4JUtils.addTooltip(edition, editionTitle);
        }

        buttonsGroup.add(edition);
    }

    /**
     * Fancybox for delete page.
     *
     * @param bundle bundle
     * @param urlDelete the command for delete
     * @return fancybox DOM element
     * @throws UnsupportedEncodingException
     */
    private Element generateDeleteFancyBox(Bundle bundle, String urlDelete) throws UnsupportedEncodingException {
        String[] urlSplit = urlDelete.split("\\?");
        String action = urlSplit[0];
        String[] args = urlSplit[1].split("&");

        // Root
        Element root = DOM4JUtils.generateDivElement("hidden");

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
        Element okButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-default btn-warning", bundle.getString("YES"), "halflings warning-sign",
                null);
        DOM4JUtils.addAttribute(okButton, HTMLConstants.TYPE, HTMLConstants.INPUT_TYPE_SUBMIT);
        form.add(okButton);

        // Cancel button
        Element cancelButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-default", bundle.getString("NO"));
        DOM4JUtils.addAttribute(cancelButton, HTMLConstants.TYPE, HTMLConstants.INPUT_TYPE_BUTTON);
        DOM4JUtils.addAttribute(cancelButton, HTMLConstants.ONCLICK, "closeFancybox()");
        form.add(cancelButton);

        return root;
    }


    /**
     * Generate userbar HTML content.
     *
     * @param controllerContext controller context
     * @param page current page
     * @param principal principal
     * @param person directory person
     * @param mySpaceURL my space URL
     * @param signOutURL sign out URL
     * @return HTML data
     * @throws Exception
     */
    private String formatHTMLUserbar(ControllerContext controllerContext, Page page, Principal principal, DirectoryPerson person, String mySpaceURL,
            String myProfileUrl, String signOutURL) {
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(controllerContext.getServerInvocation().getRequest().getLocale());
        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(controllerContext.getServerInvocation());
        cmsCtx.setControllerContext(controllerContext);


        // User informations
        String userName;
        String userAvatarSrc;
        if (person != null) {
            userName = person.getDisplayName();
            userAvatarSrc = person.getAvatar().getUrl();
        } else if (principal != null) {
            userName = principal.getName();
            try {
                userAvatarSrc = cmsService.getUserAvatar(cmsCtx, userName).getUrl();
            } catch (CMSException e) {
                userAvatarSrc = null;
            }
        } else {
            userName = bundle.getString(InternationalizationConstants.KEY_USER_GUEST);
            try {
                userAvatarSrc = cmsService.getUserAvatar(cmsCtx, "nobody").getUrl();
            } catch (CMSException e) {
                userAvatarSrc = null;
            }
        }


        // Userbar menu root element
        Element userbarMenu = DOM4JUtils.generateElement(HTMLConstants.LI, HTML_CLASS_DROPDOWN, null);

        // Userbar menu title
        Element userbarMenuTitle = DOM4JUtils.generateLinkElement(HTMLConstants.A_HREF_DEFAULT, null, null, HTML_CLASS_DROPDOWN_TOGGLE, null);
        DOM4JUtils.addAttribute(userbarMenuTitle, HTMLConstants.DATA_TOGGLE, "dropdown");
        if (userAvatarSrc != null) {
            Element avatar = DOM4JUtils.generateElement(HTMLConstants.IMG, "avatar", null);
            DOM4JUtils.addAttribute(avatar, HTMLConstants.SRC, userAvatarSrc);
            userbarMenuTitle.add(avatar);
        }
        Element displayName = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, userName);
        userbarMenuTitle.add(displayName);
        Element caret = DOM4JUtils.generateElement(HTMLConstants.SPAN, HTML_CLASS_DROPDOWN_CARET, StringUtils.EMPTY);
        userbarMenuTitle.add(caret);
        userbarMenu.add(userbarMenuTitle);

        // Userbar menu "ul" node
        Element userbarMenuUl = DOM4JUtils.generateElement(HTMLConstants.UL, HTML_CLASS_DROPDOWN_MENU, null, null, AccessibilityRoles.MENU);
        userbarMenu.add(userbarMenuUl);

        if (principal != null) {
            // My space
            // Element mySpace = DOM4JUtils.generateLinkElement(mySpaceURL, null, null, null,
            // bundle.getString(InternationalizationConstants.KEY_MY_SPACE_), "halflings star",
            // AccessibilityRoles.MENU_ITEM);
            // this.addSubMenuElement(userbarMenuUl, mySpace, null, null);


            if (person != null) {
                // View profile

                Element viewProfile = DOM4JUtils.generateLinkElement(myProfileUrl, null, null, null,
                        bundle.getString(InternationalizationConstants.KEY_MY_PROFILE), "halflings user", AccessibilityRoles.MENU_ITEM);
                this.addSubMenuElement(userbarMenuUl, viewProfile, null);

            }


            // Logout
            Element signOut = DOM4JUtils.generateLinkElement(signOutURL, null, null, null, bundle.getString(InternationalizationConstants.KEY_LOGOUT),
                    "halflings log_out", AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(userbarMenuUl, signOut, null);
        } else {
            // Login
            Element login = DOM4JUtils.generateLinkElement(mySpaceURL, null, null, null, bundle.getString(InternationalizationConstants.KEY_LOGIN),
                    "halflings log_in", AccessibilityRoles.MENU_ITEM);
            this.addSubMenuElement(userbarMenuUl, login, null);
        }


        // Write HTML content
        return DOM4JUtils.write(userbarMenu);
    }


    /**
     * Add sub-menu element.
     *
     * @param ul current "ul" element
     * @param element element to add, may be null
     * @param htmlClass HTML class, may be null
     */
    private void addSubMenuElement(Element ul, Element element, String htmlClass) {
        Element li = DOM4JUtils.generateElement(HTMLConstants.LI, htmlClass, null, null, AccessibilityRoles.PRESENTATION);
        if (element != null) {
            li.add(element);
        }
        ul.add(li);
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
