package org.osivia.portal.api;

public class Constants {

    public static String ATTR_SITE_MAP = "osivia.siteMap";
    public static String ATTR_URL_FACTORY = "osivia.urlfactory";
    public static String ATTR_PORTAL_CTX = "osivia.ctrlctx";
    public static String ATTR_USER_PORTAL = "osivia.userPortal";
    public static String ATTR_PAGE_ID = "osivia.currentPageId";
    public static String ATTR_FIRST_TAB = "osivia.firstTab";


    // Breadcrumb

    /** Breadcrumb content attribute name. */
    public static final String ATTR_BREADCRUMB = "osivia.breadcrumb";


    // Search

    /** Search URL attribute name. */
    public static final String ATTR_SEARCH_URL = "osivia.search.url";


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


    /**
     * Private constructor : prevent instantiation.
     */
    private Constants() {
        throw new AssertionError();
    }

}
