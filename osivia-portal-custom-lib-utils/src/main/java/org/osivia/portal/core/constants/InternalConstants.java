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
package org.osivia.portal.core.constants;

/**
 * OSIVIA portal internal constants.
 *
 * @author Cédric Krommenhoek
 */
public final class InternalConstants {

    // Environnement variables

    /** JBoss administration portal name. */
    public static final String JBOSS_ADMINISTRATION_PORTAL_NAME = "admin";
    /** Admin action name. */
    public static final String ACTION_ADMIN = "admin";
    /** Administrators LDAP group name. */
    public static final String ADMINISTRATORS_LDAP_GROUP_NAME = "osivia.ldap.defaultAdminRole";

    /** Tabs order property name. */
    public static final String TABS_ORDER_PROPERTY = "order";
    /** Hide default page in tabs property name. */
    public static final String TABS_HIDE_DEFAULT_PAGE_PROPERTY = "osivia.tabs.hideDefaultPage";


    /** Window title property name. */
    public static final String PROP_WINDOW_TITLE = "osivia.title";


    // LDAP

    /** LDAP context factory. */
    public static final String ENV_LDAP_CONTEXT_FACTORY = "ldap.context.factory";
    /** LDAP host. */
    public static final String ENV_LDAP_HOST = "ldap.host";
    /** LDAP port. */
    public static final String ENV_LDAP_PORT = "ldap.port";
    /** LDAP principal DN. */
    public static final String ENV_LDAP_PRINCIPAL = "ldap.manager.dn";
    /** LDAP principal password. */
    public static final String ENV_LDAP_CREDENTIALS = "ldap.manager.pswd";
    /** LDAP users base DN. */
    public static final String ENV_LDAP_USERS_BASE_DN = "ldap.users.base.dn";
    /** LDAP groups base DN. */
    public static final String ENV_LDAP_GROUPS_BASE_DN = "ldap.groups.base.dn";


    // Session attributes

    /** Administrator indicator. */
    public static final String ADMINISTRATOR_INDICATOR_ATTRIBUTE_NAME = "osivia.isAdministrator";


    // Services

    /** Internationalization service. */
    public static final String ATTR_INTERNATIONALIZATION_SERVICE = "osivia.internationalizationService";


    // Portlets instances

    /** Administration portlet. */
    public static final String PORTLET_ADMINISTRATION_INSTANCE_NAME = "osivia-portal-administration-portletInstance";


    // System properties

    /** Portlets rendering. */
    public static final String SYSTEM_PROPERTY_PORTLETS_RENDERING = "portlets.rendering";
    /** Portlets div rendering. */
    public static final String SYSTEM_PROPERTY_PORTLETS_RENDERING_VALUE_DIV = "div";
    /** Page categories prefix. */
    public static final String SYSTEM_PROPERTY_PAGE_CATEGORY_PREFIX = "page.category.prefix";
    /** Adapt resource system property name. */
    public static final String SYSTEM_PROPERTY_ADAPT_RESOURCE = "osivia.adaptResource";


    // Administration constants

    /** Edition mode. */
    public static final String ATTR_TOOLBAR_WIZARD_MODE = "osivia.toolbar.wizardMode";

    /** Toolbar settings content. */
    public static final String ATTR_TOOLBAR_SETTINGS_CONTENT = "osivia.toolbarSettings.settingsContent";
    /** Formatter. */
    public static final String ATTR_TOOLBAR_SETTINGS_FORMATTER = "osivia.toolbarSettings.formatter";
    /** Generic command URL. */
    public static final String ATTR_TOOLBAR_SETTINGS_COMMAND_URL = "osivia.toolbarSettings.commandURL";
    /** Current page. */
    public static final String ATTR_TOOLBAR_SETTINGS_PAGE = "osivia.toolbarSettings.page";
    /** CMS templated indicator. */
    public static final String ATTR_TOOLBAR_SETTINGS_CMS_TEMPLATED = "osivia.toolbarSettings.cmsTemplated";
    /** Draft page indicator. */
    public static final String ATTR_TOOLBAR_SETTINGS_DRAFT_PAGE = "osivia.toolbarSettings.draftPage";
    /** Page currentcategory. */
    public static final String ATTR_TOOLBAR_SETTINGS_PAGE_CUR_CATEGORY = "osivia.toolbarSettings.pageCategory";

    /** Page category list */
    public static final String ATTR_TOOLBAR_SETTINGS_PAGE_CATEGORIES = "osivia.toolbarSettings.pageCategories";

    /** Layouts list. */
    public static final String ATTR_TOOLBAR_SETTINGS_LAYOUTS_LIST = "osivia.toolbarSettings.layoutsList";
    /** Current layout. */
    public static final String ATTR_TOOLBAR_SETTINGS_CURRENT_LAYOUT = "osivia.toolbarSettings.currentLayout";
    /** Themes list. */
    public static final String ATTR_TOOLBAR_SETTINGS_THEMES_LIST = "osivia.toolbarSettings.themesList";
    /** Current theme. */
    public static final String ATTR_TOOLBAR_SETTINGS_CURRENT_THEME = "osivia.toolbarSettings.currentTheme";
    /** Roles. */
    public static final String ATTR_TOOLBAR_SETTINGS_ROLES = "osivia.toolbarSettings.roles";
    /** Actions for roles. */
    public static final String ATTR_TOOLBAR_SETTINGS_ACTIONS_FOR_ROLES = "osivia.toolbarSettings.actionsForRoles";

    /** CMS scope select. */
    public static final String ATTR_TOOLBAR_SETTINGS_CMS_SCOPE_SELECT = "osivia.toolbarSettings.cmsScopeSelect";
    /** CMS display live version. */
    public static final String ATTR_TOOLBAR_SETTINGS_CMS_DISPLAY_LIVE_VERSION = "osivia.toolbarSettings.cmsDisplayLiveVersion";
    /** CMS reconstextualization support. */
    public static final String ATTR_TOOLBAR_SETTINGS_CMS_RECONTEXTUALIZATION_SUPPORT = "osivia.toolbarSettings.cmsRecontextualizationSupport";
    /** CMS base path. */
    public static final String ATTR_TOOLBAR_SETTINGS_CMS_BASE_PATH = "osivia.toolbarSettings.cmsBasePath";



    // Windows constants

    // /** Toolbar settings content. */
    // public static final String ATTR_WINDOWS_SETTINGS_CONTENT = "osivia.windows.settingsContent";
    // /** Formatter. */
    // public static final String ATTR_WINDOWS_FORMATTER = "osivia.windows.formatter";
    // /** Controller context. */
    // public static final String ATTR_WINDOWS_CONTROLLER_CONTEXT = "osivia.windows.controllerContext";
    /** Generic command URL. */
    public static final String ATTR_WINDOWS_COMMAND_URL = "osivia.portlets.commandURL";
    /** Current page. */
    public static final String ATTR_WINDOWS_PAGE = "osivia.portlets.page";
    /** Current windows list. */
    public static final String ATTR_WINDOWS_CURRENT_LIST = "osivia.setting.windows";
    /** Window setting mode. */
    public static final String ATTR_WINDOWS_SETTING_MODE = "osivia.windowSettingMode";
    /** Window setting wizard mode value. */
    public static final String VALUE_WINDOWS_SETTING_WIZARD_MODE = "wizzard";
    /** Wizard mode. */
    public static final String ATTR_WINDOWS_WIZARD_MODE = "osivia.wizzardMode";
    /** Wizard template mode value. */
    public static final String VALUE_WINDOWS_WIZARD_TEMPLATE_MODE = "pageTemplate";
    /** Add portlet URL. */
    public static final String ATTR_WINDOWS_ADD_PORTLET_URL = "osivia.addPortletUrl";
    /** Delete portlet URL. */
    public static final String ATTR_WINDOWS_DELETE_PORTLET_URL = "osivia.destroyUrl";
    /** Display settings portlet URL. */
    public static final String ATTR_WINDOWS_DISPLAY_SETTINGS_URL = "osivia.settingUrl";
    /** Up portlet move command URL. */
    public static final String ATTR_WINDOWS_UP_COMMAND_URL = "osivia.upUrl";
    /** Down portlet move command URL. */
    public static final String ATTR_WINDOWS_DOWN_COMMAND_URL = "osivia.downUrl";
    /** Previous region portlet move command URL. */
    public static final String ATTR_WINDOWS_PREVIOUS_REGION_COMMAND_URL = "osivia.previousRegionUrl";
    /** Next region portlet move command URL. */
    public static final String ATTR_WINDOWS_NEXT_REGION_COMMAND_URL = "osivia.nextRegionUrl";
    /** Instance display name. */
    public static final String ATTR_WINDOWS_INSTANCE_DISPLAY_NAME = "osivia.instanceDisplayName";


    // Transversal constants
    /** Controller context. */
    public static final String ATTR_CONTROLLER_CONTEXT = "osivia.controllerContext";
    /** CMS path. */
    public static final String ATTR_CMS_PATH = "osivia.cms.path";
    /** Command prefix. */
    public static final String ATTR_COMMAND_PREFIX = "osivia.command.prefix";


    /** Classical portal (static pages). */
    public static final String PORTAL_TYPE_STATIC_PORTAL = "static";
    /** ECM mapped portal (ECM space). */
    public static final String PORTAL_TYPE_SPACE = "space";


    // Request constants

    /** Notification attribute name. */
    public static final String ATTR_NOTIFICATIONS = "osivia.notifications";

    /** Layout parsing attribute name. */
    public static final String ATTR_LAYOUT_PARSING = "osivia.layout.parsing";

    /** Layout CMS indicator attribute name. */
    public static final String ATTR_LAYOUT_CMS_INDICATOR = "osivia.layout.cms";

    /** Regions decorators attribute name. */
    public static final String ATTR_REGIONS_DECORATORS = "osivia.regions.decorators";

    /** attribute if user is currently admin */
    public static final String ATTR_USER_ADMIN = "osivia.user.administrator";

    /** Requests filtered by ECM references */
    public static final String PORTAL_CMS_REQUEST_FILTERING_POLICY_LOCAL = "local";
    public static final String PORTAL_CMS_REQUEST_FILTERING_POLICY_NO_FILTER = "none";
    public static final String PORTAL_CMS_REQUEST_FILTERING_POLICY_GLOBAL = "global";

    public static final String PORTAL_PROP_NAME_PORTAL_TYPE = "osivia.portal.portalType";
    public static final String PORTAL_PROP_NAME_CMS_REQUEST_FILTERING_POLICY = "osivia.cms.requestFilteringPolicy";


    // Window properties
    /** Locale language window property name. */
    public static final String WINDOW_PROPERTY_LOCALE_LANGUAGE = "osivia.locale.language";
    /** Locale country window property name. */
    public static final String WINDOW_PROPERTY_LOCALE_COUNTRY = "osivia.locale.country";


    /* Web URLs */
    public static final String PORTAL_URL_POLICY_WEB = "web";
    public static final String PORTAL_PROP_NAME_URL_POLICY = "osivia.urlPolicy";

    public static final String PORTAL_WEB_URL_PARAM_WINDOW = "_w";
    public static final String PORTAL_WEB_URL_PARAM_PAGEMARKER = "_pm";

    public static final String PROP_VALUE_ON = "1";
    public static final String PAGE_PROP_NAME_DYNAMIC = "osivia.page.dynamic";



    // Various technical constants

    /** Suffix for virtual end tree nodes identifiers. */
    public static final String SUFFIX_VIRTUAL_END_NODES_ID = "VirtualEndNode";

    /** Templates path name convention. */
    public static final String TEMPLATES_PATH_NAME = "templates";

    /** Proxy preview. */
    public static final String PROXY_PREVIEW = "proxy_preview";
    /** Fancybox proxy indicator callback. */
    public static final String FANCYBOX_PROXY_CALLBACK = "fancyProxy";
    /** Fancybox live indicator callback. */
    public static final String FANCYBOX_LIVE_CALLBACK = "fancyLive";
    /** Live version document request attribute. */
    public static final String ATTR_LIVE_DOCUMENT = "osivia.cms.liveDocument";

    /** Hide portlet in menu property name. */
    public static final String HIDE_PORTLET_IN_MENU_PROPERTY = "hide";


    /**
     * Private constructor : prevent instantiation.
     */
    private InternalConstants() {
        throw new AssertionError();
    }

}
