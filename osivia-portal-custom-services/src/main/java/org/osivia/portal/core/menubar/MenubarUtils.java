package org.osivia.portal.core.menubar;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.CDATA;
import org.dom4j.Element;
import org.dom4j.dom.DOMCDATA;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.core.context.ControllerContextAdapter;

/**
 * Utility class with null-safe methods for menubar.
 *
 * @author Cédric Krommenhoek
 */
public final class MenubarUtils {

    /** Menubar window identifier. */
    private static final String MENUBAR_WINDOW_ID = "menubar-window";
    /** Menubar region name. */
    private static final String MENUBAR_REGION_NAME = "menubar";


    /**
     * Private constructor : prevent instantiation.
     */
    private MenubarUtils() {
        throw new AssertionError();
    }


    /**
     * Create content navbar actions window context.
     *
     * @param portalControllerContext portal controller context
     * @return content navbar actions window context
     */
    public static final WindowContext createContentNavbarActionsWindowContext(PortalControllerContext portalControllerContext) {
        if (portalControllerContext == null) {
            return null;
        }

        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Generate HTML content
        String htmlContent = generateContentNavbarActionsHTMLContent(controllerContext);

        // Window properties
        Map<String, String> windowProperties = new HashMap<String, String>();
        windowProperties.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
        windowProperties.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
        windowProperties.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");

        WindowResult windowResult = new WindowResult(null, htmlContent, Collections.EMPTY_MAP, windowProperties, null, WindowState.NORMAL, Mode.VIEW);
        return new WindowContext(MENUBAR_WINDOW_ID, MENUBAR_REGION_NAME, null, windowResult);
    }


    /**
     * Inject content navbar actions region.
     *
     * @param controllerContext controller context
     * @param pageRendition page rendition
     */
    public static final void injectContentNavbarActionsRegion(PortalControllerContext portalControllerContext, PageRendition pageRendition) {
        WindowContext windowContext = createContentNavbarActionsWindowContext(portalControllerContext);
        pageRendition.getPageResult().addWindowContext(windowContext);

        Region region = pageRendition.getPageResult().getRegion2(MENUBAR_REGION_NAME);
        DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
    }


    /**
     * Generate content navbar actions HTML content.
     *
     * @param controllerContext controller context
     * @return HTML content
     */
    private static final String generateContentNavbarActionsHTMLContent(ControllerContext controllerContext) {
        // Menubar items
        List<?> items = (List<?>) controllerContext.getAttribute(Scope.REQUEST_SCOPE, Constants.PORTLET_ATTR_MENU_BAR);


        // Dyna-window container
        Element dynaWindowContainer = DOM4JUtils.generateDivElement("dyna-window");

        // Dyna-window identifier
        Element dynaWindowId = DOM4JUtils.generateDivElement(null);
        DOM4JUtils.addAttribute(dynaWindowId, HTMLConstants.ID, MENUBAR_WINDOW_ID);
        dynaWindowContainer.add(dynaWindowId);

        // Dyna-window content
        Element dynaWindowContent = DOM4JUtils.generateDivElement("dyna-window-content");
        dynaWindowId.add(dynaWindowContent);


        if (CollectionUtils.isNotEmpty(items)) {
            // Button toolbar
            Element toolbar = DOM4JUtils.generateElement(HTMLConstants.UL, "menubar", null);
            DOM4JUtils.addAttribute(toolbar, HTMLConstants.ROLE, HTMLConstants.ROLE_TOOLBAR);
            dynaWindowContent.add(toolbar);


            // Menubar first group
            Element firstGroupMenuItem = DOM4JUtils.generateElement(HTMLConstants.LI, "first-group", null);
            Element firstGroup = DOM4JUtils.generateElement(HTMLConstants.UL, null, null);
            firstGroupMenuItem.add(firstGroup);

            // Menubar specific group
            Element specificGroupMenuItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
            Element specificGroup = DOM4JUtils.generateElement(HTMLConstants.UL, null, null);
            specificGroupMenuItem.add(specificGroup);

            // Menubar state group
            Element stateGroupMenuItem = DOM4JUtils.generateElement(HTMLConstants.LI, "state-group", null);
            Element stateGroup = DOM4JUtils.generateElement(HTMLConstants.UL, null, null);
            stateGroupMenuItem.add(stateGroup);

            // Menubar CMS group
            Element cmsGroupMenuItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
            Element cmsGroup = DOM4JUtils.generateElement(HTMLConstants.UL, null, null);
            cmsGroupMenuItem.add(cmsGroup);

            // Menubar generic group
            Element genericGroupMenuItem = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
            Element genericGroup = DOM4JUtils.generateElement(HTMLConstants.UL, null, null);
            genericGroupMenuItem.add(genericGroup);


            // Dropdown menu
            Element dropdownMenu = DOM4JUtils.generateElement(HTMLConstants.UL, "dropdown-menu dropdown-menu-right", null, null, AccessibilityRoles.MENU);


            boolean emptyDropdownMenu = true;
            for (Object item : items) {
                MenubarItem menubarItem = (MenubarItem) item;


                // LI
                Element li;
                if (menubarItem.isDropdownItem()) {
                    li = DOM4JUtils.generateElement(HTMLConstants.LI, null, null, null, AccessibilityRoles.PRESENTATION);
                    if (menubarItem.isStateItem()) {
                        DOM4JUtils.addAttribute(li, HTMLConstants.CLASS, "dropdown-header");
                    }
                } else {
                    li = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
                }

                // Parent
                Element parent;
                if (menubarItem.isDropdownItem()) {
                    emptyDropdownMenu = false;
                    parent = dropdownMenu;
                } else if (menubarItem.isStateItem()) {
                    parent = stateGroup;
                } else if (menubarItem.isFirstItem()) {
                    parent = firstGroup;
                } else if (menubarItem.getOrder() < MenubarItem.ORDER_PORTLET_SPECIFIC_CMS) {
                    parent = specificGroup;
                } else if (menubarItem.getOrder() < MenubarItem.ORDER_PORTLET_GENERIC) {
                    parent = cmsGroup;
                } else {
                    parent = genericGroup;
                }

                parent.add(li);


                // HTML class
                StringBuilder htmlClass = new StringBuilder();
                if (menubarItem.getClassName() != null) {
                    htmlClass.append(menubarItem.getClassName());
                }
                if (menubarItem.isAjaxDisabled()) {
                    htmlClass.append(" no-ajax-link");
                }


                // Element
                Element element;
                if (menubarItem.isDropdownItem()) {
                    if (menubarItem.isStateItem()) {
                        element = DOM4JUtils
                                .generateElement(HTMLConstants.SPAN, htmlClass.toString(), menubarItem.getTitle(), menubarItem.getGlyphicon(),
                                null);
                    } else {
                        element = DOM4JUtils.generateLinkElement(menubarItem.getUrl(), menubarItem.getTarget(), menubarItem.getOnClickEvent(),
                                htmlClass.toString(), menubarItem.getTitle(), menubarItem.getGlyphicon(), AccessibilityRoles.MENU_ITEM);
                        if (menubarItem.getUrl() == null) {
                            DOM4JUtils.addAttribute(element, HTMLConstants.DISABLED, HTMLConstants.DISABLED);
                        }
                    }
                } else if (menubarItem.isStateItem()) {
                    element = DOM4JUtils.generateElement(HTMLConstants.P, null, null);

                    Element label = DOM4JUtils.generateElement(HTMLConstants.SPAN, "label label-info", menubarItem.getTitle());
                    element.add(label);
                } else {
                    if (menubarItem.getGlyphicon() == null) {
                        element = DOM4JUtils.generateLinkElement(menubarItem.getUrl(), menubarItem.getTarget(), menubarItem.getOnClickEvent(),
                                htmlClass.toString(), menubarItem.getTitle(), null);
                    } else {
                        element = DOM4JUtils.generateLinkElement(menubarItem.getUrl(), menubarItem.getTarget(), menubarItem.getOnClickEvent(),
                                htmlClass.toString(), null, menubarItem.getGlyphicon());
                        DOM4JUtils.addTooltip(element, menubarItem.getTitle());
                    }

                    if (menubarItem.getUrl() == null) {
                        DOM4JUtils.addAttribute(element, HTMLConstants.DISABLED, HTMLConstants.DISABLED);
                    }
                }

                // Data attributes
                if (!menubarItem.getData().isEmpty()) {
                    for (Entry<String, String> data : menubarItem.getData().entrySet()) {
                        StringBuilder attributeName = new StringBuilder();
                        attributeName.append("data-");
                        attributeName.append(data.getKey());

                        DOM4JUtils.addAttribute(element, attributeName.toString(), data.getValue());
                    }
                }


                li.add(element);


                // Associated HTML
                if (StringUtils.isNotBlank(menubarItem.getAssociatedHtml())) {
                    CDATA cdata = new DOMCDATA(menubarItem.getAssociatedHtml());
                    li.add(cdata);
                }
            }


            if (!emptyDropdownMenu) {
                // Dropdown menu button
                Element dropdownButton = DOM4JUtils.generateLinkElement("#", null, null, "dropdown-toggle", null, "halflings halflings-pencil");
                DOM4JUtils.addAttribute(dropdownButton, HTMLConstants.DATA_TOGGLE, "dropdown");
                Element caret = DOM4JUtils.generateElement(HTMLConstants.SPAN, "caret", StringUtils.EMPTY);
                dropdownButton.add(caret);

                // LI
                Element li = DOM4JUtils.generateElement(HTMLConstants.LI, "dropdown", null);
                li.add(dropdownButton);
                li.add(dropdownMenu);
                cmsGroup.add(li);
            }

            if (CollectionUtils.isNotEmpty(firstGroup.elements())) {
                toolbar.add(firstGroupMenuItem);
            }
            if (CollectionUtils.isNotEmpty(specificGroup.elements())) {
                toolbar.add(specificGroupMenuItem);
            }
            if (CollectionUtils.isNotEmpty(stateGroup.elements())) {
                toolbar.add(stateGroupMenuItem);
            }
            if (CollectionUtils.isNotEmpty(cmsGroup.elements())) {
                toolbar.add(cmsGroupMenuItem);
            }
            if (CollectionUtils.isNotEmpty(genericGroup.elements())) {
                toolbar.add(genericGroupMenuItem);
            }
        }


        // Write HTML content
        return DOM4JUtils.write(dynaWindowContainer);
    }

}
