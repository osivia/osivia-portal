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
    /** Profile service name. */
    public static final String PROFILE_SERVICE_NAME = "ProfileService";
    /** Formatter service name. */
    public static final String FORMATTER_SERVICE_NAME = "FormatterService";
    /** Notifications service name. */
    public static final String NOTIFICATIONS_SERVICE_NAME = "NotificationsService";
    /** Internationalization service name. */
    public static final String INTERNATIONALIZATION_SERVICE_NAME = "InternationalizationService";

    // JBoss portal objects
    /** JBP key portalName. */
    public static final String PORTAL_NAME = "portalName";
    /** name of the default portal. */
    public static final String PORTAL_NAME_DEFAULT = "default";

    // Footer

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


    // Breadcrumb

    /** Breadcrumb content attribute name. */
    public static final String ATTR_BREADCRUMB = "osivia.breadcrumb";


    // Search

    /** Search URL attribute name. */
    public static final String ATTR_SEARCH_URL = "osivia.search.url";

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


    /**
     * Default constructor.
     * Constants instances should NOT be constructed in standard programming.
     * This constructor is public to permit tools that require a JavaBean instance to operate.
     */
    public Constants() {
        super();
    }

}

