package org.osivia.portal.core.auth.constants;


/**
 * HTML constants, used for DOM4J generation.
 * 
 * @author Cédric Krommenhoek
 */
public final class HtmlConstants {

    /** HTML default text. */
    public static final String TEXT_DEFAULT = "&nbsp;";


    // HTML nodes

    /** HTML "a" nodes. */
    public static final String A = "a";
    /** HTML "div" nodes. */
    public static final String DIV = "div";
    /** HTML "form" nodes. */
    public static final String FORM = "form";
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
    /** HTML "option" nodes. */
    public static final String OPTION = "option";
    /** HTML "pre" nodes. */
    public static final String PRE = "pre";
    /** HTML "select" nodes. */
    public static final String SELECT = "select";
    /** HTML "textarea" nodes. */
    public static final String TEXTAREA = "textarea";
    /** HTML "ul" nodes. */
    public static final String UL = "ul";


    // HTML attributes

    /** HTML "action" attributes. */
    public static final String ACTION = "action";
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
    /** HTML "title" attributes. */
    public static final String TITLE = "title";
    /** HTML "type" attributes. */
    public static final String TYPE = "type";
    /** HTML "value" attributes. */
    public static final String VALUE = "value";


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
    public static final String CLASS_FANCYBOX_LABEL = "label";
    /** HTML Fancybox table class. */
    public static final String CLASS_FANCYBOX_TABLE = "fancybox-table";
    /** HTML Fancybox table row class. */
    public static final String CLASS_FANCYBOX_ROW = "fancybox-table-row";
    /** HTML navigation item class for "li" nodes. */
    public static final String CLASS_NAVIGATION_ITEM = "navigation-item";
    /** HTML small inputs like checkboxes class. */
    public static final String CLASS_SMALL_INPUT = "small-input";
    /** HTML toggle row display class. */
    public static final String CLASS_TOGGLE_ROW = "toggle-row";


    /**
     * Private constructor : prevent instantiation.
     */
    private HtmlConstants() {
        throw new AssertionError();
    }

}
