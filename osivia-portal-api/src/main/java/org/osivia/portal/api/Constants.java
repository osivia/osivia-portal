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
package org.osivia.portal.api;

/**
 * Constants.
 *
 * @author Cédric Krommenhoek
 */
public final class Constants {

    // API services names

    /** Cache service name. */
    public static final String CACHE_SERVICE_NAME = "CacheService";
    /** Status service name. */
    public static final String STATUS_SERVICE_NAME = "StatusService";
    /** URL service name. */
    public static final String URL_SERVICE_NAME = "UrlService";
    /** WebId service name */
    public static final String WEBID_SERVICE_NAME = "webIdService";
    /** Profile service name. */
    public static final String PROFILE_SERVICE_NAME = "ProfileService";
    /** Formatter service name. */
    public static final String FORMATTER_SERVICE_NAME = "FormatterService";
    /** Notifications service name. */
    public static final String NOTIFICATIONS_SERVICE_NAME = "NotificationsService";
    /** Internationalization service name. */
    public static final String INTERNATIONALIZATION_SERVICE_NAME = "InternationalizationService";
    /** Contribution service name. */
    public static final String CONTRIBUTION_SERVICE_NAME = "ContributionService";
    /** Directory locator service name. */
    public static final String DIRECTORY_SERVICE_LOCATOR_NAME = "DirctoryServiceLocator";

    // JBoss portal objects

    /** JBP key portalName. */
    public static final String PORTAL_NAME = "portalName";
    /** name of the default portal. */
    public static final String PORTAL_NAME_DEFAULT = "default";


    // Header and footer

    /** Site map attribute name. */
    public static final String ATTR_SITE_MAP = "osivia.siteMap";
    /** URL factory attribute name. */
    public static final String ATTR_URL_FACTORY = "osivia.urlfactory";
    /** Portal controller context attribute name. */
    public static final String ATTR_PORTAL_CTX = "osivia.ctrlctx";
    /** User portal attribute name. */
    public static final String ATTR_USER_PORTAL = "osivia.userPortal";
    /** Current page identifier attribute name. */
    public static final String ATTR_PAGE_ID = "osivia.currentPageId";
    /** Current page name attribute name. */
    public static final String ATTR_PAGE_NAME = "osivia.currentPageName";
    /** First tab attribute name. */
    public static final String ATTR_FIRST_TAB = "osivia.firstTab";
    
    /** Logged person. 
     * @deprecated please use ATTR_LOGGED_PERSON_2 */
    @Deprecated
	public static final String ATTR_LOGGED_PERSON = "osivia.loggedPerson";
    /** Logged person. */
    public static final String ATTR_LOGGED_PERSON_2 = "osivia.directory.v2.loggedPerson";

    // Generic

    /** Portal home page URL. */
    public static final String ATTR_PORTAL_HOME_URL = "osivia.home.url";
    /** Page category attribute name. */
    public static final String ATTR_PAGE_CATEGORY = "osivia.pageCategory";
    /** User session data attribute name. */
    public static final String ATTR_USER_DATAS = "osivia.userDatas";
    /** Native space object attribute name. */
    public static final String ATTR_SPACE_CONFIG = "osivia.cms.spaceConfig";
    /** Wizard mode indicator. */
    public static final String ATTR_WIZARD_MODE = "osivia.wizard";
    /** Space site indicator. */
    public static final String ATTR_SPACE_SITE_INDICATOR = "osivia.spaceSite";


    // Breadcrumb

    /** Breadcrumb content attribute name. */
    public static final String ATTR_BREADCRUMB = "osivia.breadcrumb";


    // Search

    /** Search URL attribute name. */
    public static final String ATTR_SEARCH_URL = "osivia.search.url";
    /** Search web URL attribute name. */
    public static final String ATTR_SEARCH_WEB_URL = "osivia.search.web.url";


    // SEO

    /** content of title tag */
    public static final String ATTR_HEADER_TITLE = "osivia.header.title";
    /** content of meta tags. */
    public static final String ATTR_HEADER_METADATA = "osivia.header.metadata";
    /** Portal base URL. */
    public static final String ATTR_HEADER_PORTAL_BASE_URL = "osivia.header.portal.url";
    /** Canonical URL. */
    public static final String ATTR_HEADER_CANONICAL_URL = "osivia.header.canonical.url";
    /** Application name. */
    public static final String ATTR_HEADER_APPLICATION_NAME = "osivia.header.application.name";


    // Toolbar

    /** Login URL attribute name. */
    public static final String ATTR_TOOLBAR_LOGIN_URL = "osivia.toolbar.loginURL";
    /** Sign out URL attribute name. */
    public static final String ATTR_TOOLBAR_SIGN_OUT_URL = "osivia.toolbar.signOutURL";
    /** Principal alias */
    public static final String ATTR_TOOLBAR_PERSON = "osivia.toolbar.person";
    /** Principal attribute name. */
    public static final String ATTR_TOOLBAR_PRINCIPAL = "osivia.toolbar.principal";
    /** User content. */
    public static final String ATTR_TOOLBAR_USER_CONTENT = "osivia.toolbar.userContent";
    /** My space URL attribute name. */
    public static final String ATTR_TOOLBAR_MY_SPACE_URL = "osivia.toolbar.mySpaceURL";
    /** User refresh page URL attribute name. */
    public static final String ATTR_TOOLBAR_REFRESH_PAGE_URL = "osivia.toolbar.refreshPageURL";
    /** Administration HTML content attribute name. */
    public static final String ATTR_TOOLBAR_ADMINISTRATION_CONTENT = "osivia.toolbar.administrationContent";
    /** User's profile */
    public static final String ATTR_TOOLBAR_MY_PROFILE = "osivia.toolbar.myprofile";
    /** Toolbar tasks URL. */
    public static final String ATTR_TOOLBAR_TASKS_URL = "osivia.toolbar.tasks.url";
    /** Toolbar tasks count. */
    public static final String ATTR_TOOLBAR_TASKS_COUNT = "osivia.toolbar.tasks.count";


    // SEO

    /** title of the document. */
    public static final String HEADER_TITLE = "osivia.header.title";
    /** metas of the document. */
    public static final String HEADER_META = "osivia.header.meta";


    //PORTLET - INPUT

    /** Menu bar. */
    public static final String PORTLET_ATTR_MENU_BAR = "osivia.menuBar";
    /** User datas map. */
    public static final String PORTLET_ATTR_USER_DATAS = "osivia.userDatas";
    /** User datas Map timestamp. */
    public static final String PORTLET_ATTR_USER_DATAS_REFRESH_TS = "osivia.userDatas.refreshTimestamp";
    /** HTTP request. */
    public static final String PORTLET_ATTR_HTTP_REQUEST = "osivia.httpRequest";
    /** Space configuration request. */
    public static final String PORTLET_ATTR_SPACE_CONFIG =  "osivia.spaceConfig";
    /** Redirection URL  */
    public static final String PORTLET_ATTR_REDIRECTION_URL =  "osivia.redirection.url";
    /** Online path. */
    public static final String PORTLET_PARAM_ONLINE_PATH = "osivia.cms.onlinePath";
    /** Edition path  */
    public static final String PORTLET_PARAM_EDITION_PATH =  "osivia.cms.editionPath";



    //PORTLET - OUTPUT

    /** To redisplay the whole all page. */
    public static final String PORTLET_ATTR_DISPLAY_PAGE = "osivia.displayPage";
    /** To return to normal mode. */
    public static final String PORTLET_ATTR_UNSET_MAX_MODE = "osivia.unsetMaxMode";
    /** To refresh all Contents (portlets + CMS). */
    public static final String PORTLET_ATTR_UPDATE_CONTENTS = "osivia.updateContents";
    /** To close popup. */
    public static final String PORTLET_ATTR_POPUP_CLOSE = "osivia.closePopup";
    /** To set breadcrumb portlet path. */
    public static final String PORTLET_ATTR_PORTLET_PATH = "osivia.portletPath";

    public static final String PORTLET_VALUE_ACTIVATE = "true";


    // WINDOWS

    /** The CMS URI of the portlet. */
    public static final String WINDOW_PROP_URI = "osivia.cms.uri";
    /** The version of the content. */
    public static final String WINDOW_PROP_VERSION = "osivia.cms.displayLiveVersion";
    /** The scope of the request. */
    public static final String WINDOW_PROP_SCOPE = "osivia.cms.scope";
    /** Maximized CMS URL window property name. */
    public static final String WINDOW_PROP_MAXIMIZED_CMS_URL = "osivia.maximized.cms.url";


    // REQUEST

    /** Portlet CMS URI request attribute name. */
    public static final String REQUEST_ATTR_URI = WINDOW_PROP_URI;


    /**
     * Private constructor : prevent instantiation.
     */
    private Constants() {
        throw new AssertionError();
    }

}

