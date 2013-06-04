package org.osivia.portal.core.formatters;

import java.io.IOException;
import java.util.List;

import org.dom4j.QName;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.core.cms.CMSServiceCtx;

/**
 * Formatter interface.
 * Can be accessed from JSP pages.
 */
public interface IFormatter {

    /** Resource bundle name. */
    static final String RESOURCE_BUNDLE_NAME = "Resource";
    /** Bundle resource key for root node display text. */
    static final String KEY_ROOT_NODE = "ROOT_NODE";
    /** Bundle resource key for virtual end nodes display text. */
    static final String KEY_VIRTUAL_END_NODES = "VIRTUAL_END_NODE";
    /** Bundle resource key for add portlet submit value. */
    static final String KEY_ADD_PORTLET_SUBMIT_VALUE = "ADD_PORTLET";
    /** Bundle resource key for styles window properties label. */
    static final String KEY_WINDOW_PROPERTIES_STYLES = "WINDOW_PROPERTIES_STYLES";
    /** Bundle resource key for no styles window. */
    static final String KEY_WINDOW_PROPERTIES_NO_STYLE = "WINDOW_PROPERTIES_NO_STYLE";
    /** Bundle resource key for styles window properties display link. */
    static final String KEY_WINDOW_PROPERTIES_STYLES_DISPLAY_LINK = "WINDOW_PROPERTIES_STYLES_DISPLAY_LINK";

    /** Suffix for virtual end nodes ID. */
    static final String SUFFIX_VIRTUAL_END_NODES_ID = "VirtualEndNode";

    /** QName for HTML "div" nodes. */
    static final QName QNAME_NODE_DIV = QName.get("div");
    /** QName for HTML "ul" nodes. */
    static final QName QNAME_NODE_UL = QName.get("ul");
    /** QName for HTML "li" nodes. */
    static final QName QNAME_NODE_LI = QName.get("li");
    /** QName for HTML "a" nodes. */
    static final QName QNAME_NODE_A = QName.get("a");
    /** QName for HTML "img" nodes. */
    static final QName QNAME_NODE_IMG = QName.get("img");
    /** QName for HTML "form" nodes. */
    static final QName QNAME_NODE_FORM = QName.get("form");
    /** QName for HTML "input" nodes. */
    static final QName QNAME_NODE_INPUT = QName.get("input");
    /** QName for HTML "label" nodes. */
    static final QName QNAME_NODE_LABEL = QName.get("label");
    /** QName for HTML "textarea" nodes. */
    static final QName QNAME_NODE_TEXTAREA = QName.get("textarea");
    /** QName for HTML "select" nodes. */
    static final QName QNAME_NODE_SELECT = QName.get("select");
    /** QName for HTML "option" nodes. */
    static final QName QNAME_NODE_OPTION = QName.get("option");
    /** QName for HTML "pre" nodes. */
    static final QName QNAME_NODE_PRE = QName.get("pre");
    /** QName for HTML "id" attributes. */
    static final QName QNAME_ATTRIBUTE_ID = QName.get("id");
    /** QName for HTML "class" attributes. */
    static final QName QNAME_ATTRIBUTE_CLASS = QName.get("class");
    /** QName for HTML "rel" attributes. */
    static final QName QNAME_ATTRIBUTE_REL = QName.get("rel");
    /** QName for HTML "href" attributes. */
    static final QName QNAME_ATTRIBUTE_HREF = QName.get("href");
    /** QName for HTML "src" attributes. */
    static final QName QNAME_ATTRIBUTE_SRC = QName.get("src");
    /** QName for HTML "title" attributes. */
    static final QName QNAME_ATTRIBUTE_TITLE = QName.get("title");
    /** QName for HTML "action" attributes. */
    static final QName QNAME_ATTRIBUTE_ACTION = QName.get("action");
    /** QName for HTML "method" attributes. */
    static final QName QNAME_ATTRIBUTE_METHOD = QName.get("method");
    /** QName for HTML "type" attributes. */
    static final QName QNAME_ATTRIBUTE_TYPE = QName.get("type");
    /** QName for HTML "name" attributes. */
    static final QName QNAME_ATTRIBUTE_NAME = QName.get("name");
    /** QName for HTML "value" attributes. */
    static final QName QNAME_ATTRIBUTE_VALUE = QName.get("value");
    /** QName for HTML "onclick" attributes. */
    static final QName QNAME_ATTRIBUTE_ONCLICK = QName.get("onclick");
    /** QName for HTML "style" attributes. */
    static final QName QNAME_ATTRIBUTE_STYLE = QName.get("style");
    /** QName for HTML "checked" attributes. */
    static final QName QNAME_ATTRIBUTE_CHECKED = QName.get("checked");
    /** QName for HTML "selected" attributes. */
    static final QName QNAME_ATTRIBUTE_SELECTED = QName.get("selected");
    /** QName for HTML "for" attributes. */
    static final QName QNAME_ATTRIBUTE_FOR = QName.get("for");
    /** QName for HTML "rows" attributes. */
    static final QName QNAME_ATTRIBUTE_ROWS = QName.get("rows");
    /** QName for HTML "cols" attributes. */
    static final QName QNAME_ATTRIBUTE_COLS = QName.get("cols");

    /** HTML form method "get" value. */
    static final String FORM_METHOD_GET = "get";
    /** HTML input type "submit" value. */
    static final String INPUT_TYPE_SUBMIT = "submit";
    /** HTML input type "button" value. */
    static final String INPUT_TYPE_BUTTON = "button";
    /** HTML input type "text" value. */
    static final String INPUT_TYPE_TEXT = "text";
    /** HTML input type "hidden" value. */
    static final String INPUT_TYPE_HIDDEN = "hidden";
    /** HTML input type "checkbox" value. */
    static final String INPUT_TYPE_CHECKBOX = "checkbox";
    /** HTML checkbox checked value. */
    static final String CHECKED = "checked";
    /** HTML select selected value. */
    static final String SELECTED = "selected";


    String formatScopeList(PortalObject po, String scopeName, String selectedScope) throws Exception;

    String formatRequestFilteringPolicyList(PortalObject po, String policyName, String selectedPolicy) throws Exception;

    String formatDisplayLiveVersionList(CMSServiceCtx ctx, PortalObject po, String scopeName, String selectedVersion) throws Exception;

    String formatContextualization(PortalObject po, String selectedScope) throws Exception;

    /**
     * Format hierarchical tree pages into HTML data, with UL and LI nodes
     * 
     * @param currentPage current page
     * @param context controller context, which contains locales and URL generation data
     * @param idPrefix avoid multiples identifiers with this prefix
     * @return HTML data
     * @throws IOException
     */
    String formatHtmlTreePortalObjects(Page currentPage, ControllerContext context, String idPrefix) throws IOException;

    /**
     * Format hierarchical tree pages into HTML data, with UL and LI nodes
     * 
     * @param currentPage current page
     * @param context controller context, which contains locales and URL generation data
     * @param idPrefix avoid multiples identifiers with this prefix
     * @param displayRoot display root indicator
     * @param displayVirtualEndNodes display virtual end nodes indicator
     * @param sortAlphabetically sort alphabetically indicator
     * @return HTML data
     * @throws IOException
     */
    String formatHtmlTreePortalObjects(Page currentPage, ControllerContext context, String idPrefix, boolean displayRoot, boolean displayVirtualEndNodes,
            boolean sortAlphabetically) throws IOException;

    /**
     * Format portal object ID into HTML-safe identifier.
     * 
     * @param id portal object ID
     * @return HTML-safe format
     * @throws IOException
     */
    String formatHtmlSafeEncodingId(PortalObjectId id) throws IOException;

    /**
     * Format portlets list into HTML fancybox data.
     * 
     * @param context controller context, which contains locales and URL generation data
     * @return HTML fancybox data
     * @throws IOException
     */
    String formatHtmlPortletsList(ControllerContext context) throws IOException;

    /**
     * Format windows settings into HTML fancyboxes data.
     * 
     * @param currentPage current page
     * @param windows current page windows list
     * @param context controller context, which contains locales and URL generation data
     * @return HTML fancyboxes data
     * @throws IOException
     */
    String formatHtmlWindowsSettings(Page currentPage, List<Window> windows, ControllerContext context) throws IOException;

}
