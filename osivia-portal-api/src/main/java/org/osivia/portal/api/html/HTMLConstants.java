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
package org.osivia.portal.api.html;


/**
 * HTML constants, used for DOM4J generation.
 *
 * @author Cédric Krommenhoek
 */
public final class HTMLConstants {

    /** HTML default text. */
    public static final String TEXT_DEFAULT = "&nbsp;";

    /** HTML line break. */
    public static final String LINE_BREAK = "<br>";


    // HTML nodes

    /** HTML "a" nodes. */
    public static final String A = "a";
    /** HTML "button" nodes. */
    public static final String BUTTON = "button";
    /** HTML "code" nodes. */
    public static final String CODE = "code";
    /** HTML "div" nodes. */
    public static final String DIV = "div";
    /** HTML "dd" nodes. */
    public static final String DD = "dd";
    /** HTML "dl" nodes. */
    public static final String DL = "dl";
    /** HTML "dt" nodes. */
    public static final String DT = "dt";
    /** HTML "fieldset" nodes. */
    public static final String FIELDSET = "fieldset";
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
    /** HTML "i" nodes. */
    public static final String I = "i";
    /** HTML "img" nodes. */
    public static final String IMG = "img";
    /** HTML "input" nodes. */
    public static final String INPUT = "input";
    /** HTML "label" nodes. */
    public static final String LABEL = "label";
    /** HTML "legend" nodes. */
    public static final String LEGEND = "legend";
    /** HTML "li" nodes. */
    public static final String LI = "li";
    /** HTML "meta" nodes. */
    public static final String META = "meta";
    /** HTML "nav" nodes. */
    public static final String NAV = "nav";
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
    /** HTML "small" nodes. */
    public static final String SMALL = "small";
    /** HTML "span" nodes. */
    public static final String SPAN = "span";
    /** HTML "table" nodes. */
    public static final String TABLE = "table";
    /** HTML "tbody" nodes. */
    public static final String TBODY = "tbody";
    /** HTML "thead" nodes. */
    public static final String THEAD = "thead";
    /** HTML "textarea" nodes. */
    public static final String TEXTAREA = "textarea";
    /** HTML "td" nodes. */
    public static final String TD = "td";
    /** HTML "tr" nodes. */
    public static final String TR = "tr";
    /** HTML "ul" nodes. */
    public static final String UL = "ul";


    // HTML attributes

    /** HTML "action" attributes. */
    public static final String ACTION = "action";
    /** HTML "alt" attributes. */
    public static final String ALT = "alt";
    /** HTML "aria-hidden" attributes. */
    public static final String ARIA_HIDDEN = "aria-hidden";
    /** HTML "checked" attributes. */
    public static final String CHECKED = "checked";
    /** HTML "class" attributes. */
    public static final String CLASS = "class";
    /** HTML "cols" attributes. */
    public static final String COLS = "cols";
    /** HTML "content" attributes. */
    public static final String CONTENT = "content";
    /** HTML "data-container" attributes. */
    public static final String DATA_CONTAINER = "data-container";
    /** HTML "data-dismiss" attributes. */
    public static final String DATA_DISMISS = "data-dismiss";
    /** HTML "data-original-title" attributes. */
    public static final String DATA_ORIGINAL_TITLE = "data-original-title";
    /** HTML "data-placement" attributes. */
    public static final String DATA_PLACEMENT = "data-bs-placement";
    /** HTML "data-toggle" attributes. */
    public static final String DATA_TOGGLE = "data-bs-toggle";
    /** HTML "disabled" attributes. */
    public static final String DISABLED = "disabled";
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
    /** HTML "onkeyup" attributes. */
    public static final String ONKEYUP = "onkeyup";
    /** HTML "placeholder" attributes. */
    public static final String PLACEHOLDER = "placeholder";
    /** HTML "rel" attributes. */
    public static final String REL = "rel";
    /** HTML "role" attributes. */
    public static final String ROLE = "role";
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
    /** HTML form method "post" value. */
    public static final String FORM_METHOD_POST = "post";
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
    /** HTML role "form" value. */
    public static final String ROLE_FORM = "form";
    /** HTML role "menu" value. */
    public static final String ROLE_MENU = "menu";
    /** HTML role "menuitem" value. */
    public static final String ROLE_MENU_ITEM = "menuitem";
    /** HTML role "navigation" value. */
    public static final String ROLE_NAVIGATION = "navigation";
    /** HTML role "presentation" value. */
    public static final String ROLE_PRESENTATION = "presentation";
    /** HTML role "toolbar" value. */
    public static final String ROLE_TOOLBAR = "toolbar";
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
    /** HTML Fancybox portlet popup with refresh on close class. */
    public static final String CLASS_FANCYFRAME_REFRESH = "fancyframe_refresh";
    /** HTML navigation item class for "li" nodes. */
    public static final String CLASS_NAVIGATION_ITEM = "navigation-item";
    /** HTML small inputs like checkboxes class. */
    public static final String CLASS_SMALL_INPUT = "small-input";
    /** HTML toggle row display class. */
    public static final String CLASS_TOGGLE_ROW = "toggle-row";


    /**
     * Private constructor : prevent instantiation.
     */
    private HTMLConstants() {
        throw new AssertionError();
    }

}
