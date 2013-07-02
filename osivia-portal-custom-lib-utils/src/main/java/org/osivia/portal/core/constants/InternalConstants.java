package org.osivia.portal.core.constants;


public final class InternalConstants {

    /** Admin action name. */
    public static final String ACTION_ADMIN = "admin";

    /** Window title property name. */
    public static final String PROP_WINDOW_TITLE = "osivia.title";

    /** Internationalization service. */
    public static final String ATTR_INTERNATIONALIZATION_SERVICE = "osivia.internationalizationService";

    /** Edition mode. */
    public static final String ATTR_TOOLBAR_WIZARD_MODE = "osivia.toolbar.wizardMode";

    /** Edition CMS mode. */
    public static final String ATTR_TOOLBAR_CMS_VERSION = "osivia.toolbar.cmsVersion";
    /** Edition CMS mode. */
    public static final String ATTR_TOOLBAR_CMS_EDITION_MODE = "osivia.toolbar.cmsEditionMode";


    // Toolbar settings constants

    /** Toolbar settings content. */
    public static final String ATTR_TOOLBAR_SETTINGS_CONTENT = "osivia.toolbarSettings.settingsContent";
    /** Formatter. */
    public static final String ATTR_TOOLBAR_SETTINGS_FORMATTER = "osivia.toolbarSettings.formatter";
    /** Controller context. */
    public static final String ATTR_TOOLBAR_SETTINGS_CONTROLLER_CONTEXT = "osivia.toolbarSettings.controllerContext";
    /** Generic command URL. */
    public static final String ATTR_TOOLBAR_SETTINGS_COMMAND_URL = "osivia.toolbarSettings.commandURL";
    /** Current page. */
    public static final String ATTR_TOOLBAR_SETTINGS_PAGE = "osivia.toolbarSettings.page";
    /** Default page indicator. */
    public static final String ATTR_TOOLBAR_SETTINGS_DEFAULT_PAGE = "osivia.toolbarSettings.defaultPage";
    /** Draft page indicator. */
    public static final String ATTR_TOOLBAR_SETTINGS_DRAFT_PAGE = "osivia.toolbarSettings.draftPage";
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
    /** Delete page command URL. */
    public static final String ATTR_TOOLBAR_SETTINGS_DELETE_PAGE_COMMAND_URL = "osivia.toolbarSettings.deletePageCommandURL";
    /** CMS scope select. */
    public static final String ATTR_TOOLBAR_SETTINGS_CMS_SCOPE_SELECT = "osivia.toolbarSettings.cmsScopeSelect";
    /** CMS display live version. */
    public static final String ATTR_TOOLBAR_SETTINGS_CMS_DISPLAY_LIVE_VERSION = "osivia.toolbarSettings.cmsDisplayLiveVersion";
    /** CMS reconstextualization support. */
    public static final String ATTR_TOOLBAR_SETTINGS_CMS_RECONTEXTUALIZATION_SUPPORT = "osivia.toolbarSettings.cmsRecontextualizationSupport";
    /** CMS base path. */
    public static final String ATTR_TOOLBAR_SETTINGS_CMS_BASE_PATH = "osivia.toolbarSettings.cmsBasePath";
    /** CMS navigation mode indicator. */
    public static final String ATTR_TOOLBAR_SETTINGS_CMS_NAVIGATION_MODE = "osivia.toolbarSettings.cmsNavigationMode";


    // Windows constants

    /** Toolbar settings content. */
    public static final String ATTR_WINDOWS_SETTINGS_CONTENT = "osivia.windows.settingsContent";
    /** Formatter. */
    public static final String ATTR_WINDOWS_FORMATTER = "osivia.windows.formatter";
    /** Controller context. */
    public static final String ATTR_WINDOWS_CONTROLLER_CONTEXT = "osivia.windows.controllerContext";
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


    // Various technical constants

    /** Suffix for virtual end tree nodes identifiers. */
    public static final String SUFFIX_VIRTUAL_END_NODES_ID = "VirtualEndNode";


    // CMS constants
    public static final String CMS_VERSION_PREVIEW = "preview";
    public static final String CMS_VERSION_ONLINE = "online";
    public static final String CMS_EDITION_MODE_ON = "1";
    public static final String CMS_EDITION_MODE_OFF = "0";

    /**
     * Private constructor : prevent instantiation.
     */
    private InternalConstants() {
        throw new AssertionError();
    }

}
