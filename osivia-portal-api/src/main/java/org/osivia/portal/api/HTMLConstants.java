package org.osivia.portal.api;


/**
 * HTML constants, used for DOM4J generation.
 *
 * @author Cédric Krommenhoek
 */
public final class HTMLConstants {

    /** HTML default text. */
    public static final String TEXT_DEFAULT = "&nbsp;";



    // HTML nodes

    /** HTML "a" nodes. */
    public static final String A = "a";
    /** HTML "div" nodes. */
    public static final String DIV = "div";
    /** HTML "figcaption" nodes. */
    public static final String FIGCAPTION = "figcaption";
    /** HTML "figure" nodes. */
    public static final String FIGURE = "figure";
    /** HTML "form" nodes. */
    public static final String FORM = "form";
    /** HTML "h1" nodes. */
    public static final String H1 = "h1";
    /** HTML "h2" nodes. */
    public static final String H2 = "h2";
    /** HTML "h3" nodes. */
    public static final String H3 = "h3";
    /** HTML "h4" nodes. */
    public static final String H4 = "h4";
    /** HTML "h5" nodes. */
    public static final String H5 = "h5";
    /** HTML "hr" nodes. */
    public static final String HR = "hr";
    /** HTML "img" nodes. */
    public static final String IMG = "img";
    /** HTML "input" nodes. */
    public static final String INPUT = "input";
    /** HTML "label" nodes. */
    public static final String LABEL = "label";
    /** HTML "li" nodes. */
    public static final String LI = "li";
    /** HTML "ol" nodes. */
    public static final String OL = "ol";
    /** HTML "option" nodes. */
    public static final String OPTION = "option";
    /** HTML "p" nodes. */
    public static final String P = "p";
    /** HTML "pre" nodes. */
    public static final String PRE = "pre";
    /** HTML "select" nodes. */
    public static final String SELECT = "select";
    /** HTML SPAN nodes. */
    public static final String SPAN = "span";
    /** HTML "textarea" nodes. */
    public static final String TEXTAREA = "textarea";
    /** HTML "ul" nodes. */
    public static final String UL = "ul";


    // HTML attributes

    /** HTML "action" attributes. */
    public static final String ACTION = "action";
    /** HTML "alt" attributes. */
    public static final String ALT = "alt";
    /** HTML "checked" attributes. */
    public static final String CHECKED = "checked";
    /** HTML "class" attributes. */
    public static final String CLASS = "class";
    /** HTML "cols" attributes. */
    public static final String COLS = "cols";
    /** HTML "for" attributes. */
    public static final String FOR = "for";
    /** HTML "href" attributes. */
    public static final String HREF = "href";
    /** HTML "id" attributes. */
    public static final String ID = "id";
    /** HTML "method" attributes. */
    public static final String METHOD = "method";
    /** HTML "name" attributes. */
    public static final String NAME = "name";
    /** HTML "onclick" attributes. */
    public static final String ONCLICK = "onclick";
    /** HTML "rel" attributes. */
    public static final String REL = "rel";
    /** HTML "rows" attributes. */
    public static final String ROWS = "rows";
    /** HTML "selected" attributes. */
    public static final String SELECTED = "selected";
    /** HTML "src" attributes. */
    public static final String SRC = "src";
    /** HTML "style" attributes. */
    public static final String STYLE = "style";
    /** HTML "target" attributes. */
    public static final String TARGET = "target";
    /** HTML "title" attributes. */
    public static final String TITLE = "title";
    /** HTML "type" attributes. */
    public static final String TYPE = "type";
    /** HTML "value" attributes. */
    public static final String VALUE = "value";
    /** HTML "accesskey" attributes. */
    public static final String ACCESSKEY = "accesskey";

    // HTML attributes values

    /** HTML default href. */
    public static final String A_HREF_DEFAULT = "#";
    /** HTML form method "get" value. */
    public static final String FORM_METHOD_GET = "get";
    /** HTML input checkbox checked value. */
    public static final String INPUT_CHECKED = "checked";
    /** HTML input select selected value. */
    public static final String INPUT_SELECTED = "selected";
    /** HTML input type "submit" value. */
    public static final String INPUT_TYPE_SUBMIT = "submit";
    /** HTML input type "button" value. */
    public static final String INPUT_TYPE_BUTTON = "button";
    /** HTML input type "text" value. */
    public static final String INPUT_TYPE_TEXT = "text";
    /** HTML input type "hidden" value. */
    public static final String INPUT_TYPE_HIDDEN = "hidden";
    /** HTML input type "checkbox" value. */
    public static final String INPUT_TYPE_CHECKBOX = "checkbox";
    /** HTML display none style. */
    public static final String STYLE_DISPLAY_NONE = "display: none;";
    /** HTML new window target. */
    public static final String TARGET_NEW_WINDOW = "_blank";


    // HTML classes

    /** HTML Fancybox container class. */
    public static final String CLASS_FANCYBOX_CONTAINER = "fancybox-content";
    /** HTML Fancybox table cell class. */
    public static final String CLASS_FANCYBOX_CELL = "fancybox-table-cell";
    /** HTML Fancybox center content class. */
    public static final String CLASS_FANCYBOX_CENTER_CONTENT = "fancybox-center-content";
    /** HTML Fancybox form class. */
    public static final String CLASS_FANCYBOX_FORM = "fancybox-form";
    /** HTML Fancybox cell label class. */
    public static final String CLASS_FANCYBOX_LABEL = "fancybox-label";
    /** HTML Fancybox table class. */
    public static final String CLASS_FANCYBOX_TABLE = "fancybox-table";
    /** HTML Fancybox table row class. */
    public static final String CLASS_FANCYBOX_ROW = "fancybox-table-row";
    /** HTML Fancybox portlet popup class. */
    public static final String CLASS_FANCYFRAME = "fancyframe";
    /** HTML Fancybox portlet popup with refresh on close class. */
    public static final String CLASS_FANCYFRAME_REFRESH = "fancyframe_refresh";
    /** HTML navigation item class for "li" nodes. */
    public static final String CLASS_NAVIGATION_ITEM = "navigation-item";
    /** HTML small inputs like checkboxes class. */
    public static final String CLASS_SMALL_INPUT = "small-input";
    /** HTML toggle row display class. */
    public static final String CLASS_TOGGLE_ROW = "toggle-row";
    /** HTML check class. */
    public static final String CLASS_CHECK = "check";
    /** HTML uncheck class. */
    public static final String CLASS_UNCHECK = "uncheck";





    /**
     * Private constructor : prevent instantiation.
     */
    private HTMLConstants() {
        throw new AssertionError();
    }

}
