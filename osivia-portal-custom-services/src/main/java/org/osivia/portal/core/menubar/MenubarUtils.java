package org.osivia.portal.core.menubar;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** Content navbar actions window identifier. */
    private static final String CONTENT_NAVBAR_ACTIONS_WINDOW_ID = "content-navbar-actions-window";
    /** Content navbar actions region name. */
    private static final String CONTENT_NAVBAR_ACTIONS_REGION_NAME = "content-navbar-actions";


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
        return new WindowContext(CONTENT_NAVBAR_ACTIONS_WINDOW_ID, CONTENT_NAVBAR_ACTIONS_REGION_NAME, null, windowResult);
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

        Region region = pageRendition.getPageResult().getRegion2(CONTENT_NAVBAR_ACTIONS_REGION_NAME);
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
        DOM4JUtils.addAttribute(dynaWindowId, HTMLConstants.ID, CONTENT_NAVBAR_ACTIONS_WINDOW_ID);
        dynaWindowContainer.add(dynaWindowId);

        // Dyna-window content
        Element dynaWindowContent = DOM4JUtils.generateDivElement("dyna-window-content");
        dynaWindowId.add(dynaWindowContent);


        if (CollectionUtils.isNotEmpty(items)) {
            // Button toolbar
            Element toolbar = DOM4JUtils.generateElement(HTMLConstants.UL, "content-navbar-actions-menubar", null);
            DOM4JUtils.addAttribute(toolbar, HTMLConstants.ROLE, HTMLConstants.ROLE_TOOLBAR);
            dynaWindowContent.add(toolbar);

            // Associated HTML container
            Element associatedHTMLContainer = DOM4JUtils.generateDivElement("hidden");
            toolbar.add(associatedHTMLContainer);


            // Menubar first group
            Element firstGroup = DOM4JUtils.generateElement(HTMLConstants.LI, "first-group", null);

            // Menubar specific group
            Element specificGroup = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);

            // Menubar state group
            Element stateGroup = DOM4JUtils.generateElement(HTMLConstants.LI, "state-group", null);

            // Menubar CMS group
            Element cmsGroup = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);

            // Menubar generic group
            Element genericGroup = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);


            // Dropdown menu
            Element dropdownMenu = DOM4JUtils.generateElement(HTMLConstants.UL, "dropdown-menu dropdown-menu-right", null, null, AccessibilityRoles.MENU);


            boolean emptyDropdownMenu = true;
            for (Object item : items) {
                MenubarItem menubarItem = (MenubarItem) item;

                Element parent;
                if (menubarItem.isDropdownItem()) {
                    emptyDropdownMenu = false;

                    Element dropdownItemContainer = DOM4JUtils.generateElement(HTMLConstants.LI, null, null, null, AccessibilityRoles.PRESENTATION);
                    if (menubarItem.isStateItem()) {
                        DOM4JUtils.addAttribute(dropdownItemContainer, HTMLConstants.CLASS, "dropdown-header");
                    }
                    dropdownMenu.add(dropdownItemContainer);

                    parent = dropdownItemContainer;
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


                StringBuilder linkHTMLClass = new StringBuilder();
                if (menubarItem.getClassName() != null) {
                    linkHTMLClass.append(menubarItem.getClassName());
                }
                if (menubarItem.isAjaxDisabled()) {
                    linkHTMLClass.append(" no-ajax-link");
                }


                Element element;
                if (menubarItem.isDropdownItem()) {
                    if (menubarItem.isStateItem()) {
                        element = DOM4JUtils.generateElement(HTMLConstants.SPAN, linkHTMLClass.toString(), menubarItem.getTitle(), menubarItem.getGlyphicon(),
                                null);
                    } else {
                        element = DOM4JUtils.generateLinkElement(menubarItem.getUrl(), menubarItem.getTarget(), menubarItem.getOnClickEvent(),
                                linkHTMLClass.toString(), menubarItem.getTitle(), menubarItem.getGlyphicon(), AccessibilityRoles.MENU_ITEM);
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
                                linkHTMLClass.toString(), menubarItem.getTitle(), null);
                    } else {
                        element = DOM4JUtils.generateLinkElement(menubarItem.getUrl(), menubarItem.getTarget(), menubarItem.getOnClickEvent(),
                                linkHTMLClass.toString(), null, menubarItem.getGlyphicon());
                        DOM4JUtils.addTooltip(element, menubarItem.getTitle());
                    }

                    if (menubarItem.getUrl() == null) {
                        DOM4JUtils.addAttribute(element, HTMLConstants.DISABLED, HTMLConstants.DISABLED);
                    }
                }
                parent.add(element);


                // Associated HTML
                if (StringUtils.isNotBlank(menubarItem.getAssociatedHtml())) {
                    CDATA cdata = new DOMCDATA(menubarItem.getAssociatedHtml());
                    associatedHTMLContainer.add(cdata);
                }
            }


            if (!emptyDropdownMenu) {
                Element dropdownContainer;
                if (CollectionUtils.isEmpty(cmsGroup.elements())) {
                    dropdownContainer = cmsGroup;
                    DOM4JUtils.addAttribute(cmsGroup, HTMLConstants.CLASS, "dropdown");
                } else {
                    Element ul = DOM4JUtils.generateElement(HTMLConstants.UL, null, null);
                    cmsGroup.add(ul);

                    dropdownContainer = DOM4JUtils.generateElement(HTMLConstants.LI, "dropdown", null);
                    ul.add(dropdownContainer);
                }

                // Dropdown menu button
                Element dropdownButton = DOM4JUtils.generateLinkElement("#", null, null, "dropdown-toggle", null, "pencil");
                DOM4JUtils.addAttribute(dropdownButton, HTMLConstants.DATA_TOGGLE, "dropdown");
                Element caret = DOM4JUtils.generateElement(HTMLConstants.SPAN, "caret", StringUtils.EMPTY);
                dropdownButton.add(caret);

                dropdownContainer.add(dropdownButton);
                dropdownContainer.add(dropdownMenu);
            }

            if (CollectionUtils.isNotEmpty(firstGroup.elements())) {
                toolbar.add(firstGroup);
            }
            if (CollectionUtils.isNotEmpty(specificGroup.elements())) {
                toolbar.add(specificGroup);
            }
            if (CollectionUtils.isNotEmpty(stateGroup.elements())) {
                toolbar.add(stateGroup);
            }
            if (CollectionUtils.isNotEmpty(cmsGroup.elements())) {
                toolbar.add(cmsGroup);
            }
            if (CollectionUtils.isNotEmpty(genericGroup.elements())) {
                toolbar.add(genericGroup);
            }
        }


        // Write HTML content
        return DOM4JUtils.write(dynaWindowContainer);
    }

}
