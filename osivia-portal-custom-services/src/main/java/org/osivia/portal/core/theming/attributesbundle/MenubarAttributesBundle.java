package org.osivia.portal.core.theming.attributesbundle;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.CDATA;
import org.dom4j.Element;
import org.dom4j.dom.DOMCDATA;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.theming.IAttributesBundle;

/**
 * Menubar attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public class MenubarAttributesBundle implements IAttributesBundle {

    /** Page menubar attribute name. */
    private static final String PAGE_MENUBAR_ATTRIBUTE = "osivia.portal.page.menubar";


    /** Singleton instance. */
    private static MenubarAttributesBundle instance;


    /** Menubar attributes names. */
    private final Set<String> names;


    /**
     * Constructor.
     */
    private MenubarAttributesBundle() {
        super();

        this.names = new TreeSet<String>();
        this.names.add(PAGE_MENUBAR_ATTRIBUTE);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static MenubarAttributesBundle getInstance() {
        if (instance == null) {
            instance = new MenubarAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();

        // Menubar
        String menubar = this.generateMenubar(controllerContext);
        attributes.put(PAGE_MENUBAR_ATTRIBUTE, menubar);
    }


    private String generateMenubar(ControllerContext controllerContext) {
        // Menubar items
        List<?> items = (List<?>) controllerContext.getAttribute(Scope.REQUEST_SCOPE, Constants.PORTLET_ATTR_MENU_BAR);

        // Button toolbar
        Element toolbar = DOM4JUtils.generateDivElement("btn-toolbar");
        DOM4JUtils.addAttribute(toolbar, HTMLConstants.ROLE, HTMLConstants.ROLE_TOOLBAR);

        if (CollectionUtils.isNotEmpty(items)) {
            // Associated HTML container
            Element associatedHTMLContainer = DOM4JUtils.generateDivElement("hidden");
            toolbar.add(associatedHTMLContainer);


            // Menubar first group
            Element firstGroup = DOM4JUtils.generateDivElement("btn-group first-group");

            // Menubar specific group
            Element specificGroup = DOM4JUtils.generateDivElement("btn-group");

            // Menubar state group
            Element stateGroup = DOM4JUtils.generateDivElement("btn-group state-group");

            // Menubar CMS group
            Element cmsGroup = DOM4JUtils.generateDivElement("btn-group");

            // Menubar generic group
            Element genericGroup = DOM4JUtils.generateDivElement("btn-group");


            // Dropdown menu button
            Element dropdownButton = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "btn btn-default dropdown-toggle", HTMLConstants.TEXT_DEFAULT, "pencil",
                    null);
            DOM4JUtils.addAttribute(dropdownButton, HTMLConstants.DATA_TOGGLE, "dropdown");
            Element caret = DOM4JUtils.generateElement(HTMLConstants.SPAN, "caret", StringUtils.EMPTY);
            dropdownButton.add(caret);

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
                    element = DOM4JUtils.generateElement(HTMLConstants.SPAN, "label label-info", menubarItem.getTitle());
                } else {
                    linkHTMLClass.append(" btn btn-default");

                    element = DOM4JUtils.generateLinkElement(menubarItem.getUrl(), menubarItem.getTarget(), menubarItem.getOnClickEvent(),
                            linkHTMLClass.toString(), null, menubarItem.getGlyphicon());
                    DOM4JUtils.addAttribute(element, HTMLConstants.TITLE, menubarItem.getTitle());
                    DOM4JUtils.addAttribute(element, HTMLConstants.DATA_TOGGLE, "tooltip");
                    DOM4JUtils.addAttribute(element, HTMLConstants.DATA_PLACEMENT, "bottom");
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
                } else {
                    dropdownContainer = DOM4JUtils.generateDivElement("btn-group");
                    cmsGroup.add(dropdownContainer);
                }

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
        return DOM4JUtils.write(toolbar);
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
