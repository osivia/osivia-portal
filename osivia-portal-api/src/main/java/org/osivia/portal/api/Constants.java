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
public class Constants {

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
    /** First tab attribute name. */
    public static final String ATTR_FIRST_TAB = "osivia.firstTab";
    /** Page category attribute name. */
    

    // Generic
    
    public static final String ATTR_PAGE_CATEGORY = "osivia.pageCategory";
    /** User session data attribute name. */
    public static final String ATTR_USER_DATAS = "osivia.userDatas";
    /** Native space object attribute name. */    
    public static final String ATTR_SPACE_CONFIG = "osivia.cms.spaceConfig";
    /** Wizard mode indicator. */
    public static final String ATTR_WIZARD_MODE = "osivia.wizard";
    

    // Breadcrumb

    /** Breadcrumb content attribute name. */
    public static final String ATTR_BREADCRUMB = "osivia.breadcrumb";


    // Search

    /** Search URL attribute name. */
    public static final String ATTR_SEARCH_URL = "osivia.search.url";
    /** Advanced search URL attribute name. */
    public static final String ATTR_ADVANCED_SEARCH_URL = "osivia.advancedSearch.url";


    // SEO
    /** content of meta tags. */
    public static final String ATTR_HEADER_METADATA_CONTENT = "osivia.header.metadata.content";

    // Toolbar

    /** Login URL attribute name. */
    public static final String ATTR_TOOLBAR_LOGIN_URL = "osivia.toolbar.loginURL";
    /** Sign out URL attribute name. */
    public static final String ATTR_TOOLBAR_SIGN_OUT_URL = "osivia.toolbar.signOutURL";
    /** Principal attribute name. */
    public static final String ATTR_TOOLBAR_PRINCIPAL = "osivia.toolbar.principal";
    /** My space URL attribute name. */
    public static final String ATTR_TOOLBAR_MY_SPACE_URL = "osivia.toolbar.mySpaceURL";
    /** User refresh page URL attribute name. */
    public static final String ATTR_TOOLBAR_REFRESH_PAGE_URL = "osivia.toolbar.refreshPageURL";
    /** Administration HTML content attribute name. */
    public static final String ATTR_TOOLBAR_ADMINISTRATION_CONTENT = "osivia.toolbar.administrationContent";


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
    
   
    public static final String PORTLET_PARAM_EDITION_PATH =  "osivia.cms.editionPath";
    

    //PORTLET - OUTPUT

    /** To refresh all page. */
    public static final String PORTLET_ATTR_REFRESH_PAGE = "osivia.refreshPage";
    /** To return to normal mode. */
    public static final String PORTLET_ATTR_UNSET_MAX_MODE = "osivia.unsetMaxMode";
    /** To refresh all CMS Contents. */
    public static final String PORTLET_ATTR_UPDATE_CONTENTS = "osivia.updateContents";     
     
    public static final String PORTLET_VALUE_ACTIVATE = "true"; 
    

    // WINDOWS
    /** The CMS URI of the portlet */    
    public static final String WINDOW_PROP_URI = "osivia.cms.uri";
    /** The version of the content */     
    public static final String WINDOW_PROP_VERSION = "osivia.cms.displayLiveVersion";
    /** The scope of the request */  
    public static final String WINDOW_PROP_SCOPE = "osivia.cms.scope";


    /**
     * Default constructor.
     * Constants instances should NOT be constructed in standard programming.
     * This constructor is public to permit tools that require a JavaBean instance to operate.
     */
    public Constants() {
        super();
    }

}

