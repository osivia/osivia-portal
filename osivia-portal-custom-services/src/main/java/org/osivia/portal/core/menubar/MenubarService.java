package org.osivia.portal.core.menubar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.CDATA;
import org.dom4j.Element;
import org.dom4j.dom.DOMCDATA;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.DocumentContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.html.AccessibilityRoles;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.menubar.IMenubarService;
import org.osivia.portal.api.menubar.MenubarContainer;
import org.osivia.portal.api.menubar.MenubarDropdown;
import org.osivia.portal.api.menubar.MenubarGroup;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.menubar.MenubarModule;
import org.osivia.portal.api.menubar.MenubarObject;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Menubar service implementation.
 *
 * @author Cédric Krommenhoek
 * @see IMenubarService
 */
public class MenubarService implements IMenubarService {

    /**
     * Menubar dropdown menus request attribute name.
     */
    private static final String DROPDOWN_MENUS_REQUEST_ATTRIBUTE = "osivia.menubar.dropdownMenus";

    /**
     * Portal URL factory.
     */
    private IPortalUrlFactory urlFactory;
    /**
     * Internationalization service.
     */
    private IInternationalizationService internationalizationService;
    /**
     * CMS service locator.
     */
    private ICMSServiceLocator cmsServiceLocator;

    /**
     * Menubar group comparator.
     */
    private final Comparator<MenubarGroup> groupComparator;
    /**
     * Menubar object comparator.
     */
    private final Comparator<MenubarObject> objectComparator;


    /**
     * Constructor.
     */
    public MenubarService() {
        super();
        this.groupComparator = new MenubarGroupComparator();
        this.objectComparator = new MenubarObjectComparator();
    }


    /**
     * {@inheritDoc}
     */
    public MenubarDropdown getDropdown(PortalControllerContext portalControllerContext, String id) {
        MenubarDropdown result = null;

        // Search loop
        Set<MenubarDropdown> dropdownMenus = this.getDropdownMenus(portalControllerContext);
        if (dropdownMenus != null) {
            for (MenubarDropdown dropdown : dropdownMenus) {
                if (StringUtils.equals(id, dropdown.getId())) {
                    result = dropdown;
                    break;
                }
            }
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    public void addDropdown(PortalControllerContext portalControllerContext, MenubarDropdown dropdown) {
        Set<MenubarDropdown> dropdownMenus = this.getDropdownMenus(portalControllerContext);
        dropdownMenus.add(dropdown);
    }


    /**
     * {@inheritDoc}
     */
    public String generateNavbarContent(PortalControllerContext portalControllerContext) {
        // HTTP servlet request
        HttpServletRequest httpServletRequest = portalControllerContext.getHttpServletRequest();
        // Bundle
        IBundleFactory bundleFactory = this.internationalizationService.getBundleFactory(this.getClass().getClassLoader());
        Bundle bundle = bundleFactory.getBundle(httpServletRequest.getLocale());


        // Menubar
        List<MenubarItem> menubar = this.getMenubar(portalControllerContext, false);

        // Back
        String backURL = this.urlFactory.getBackURL(portalControllerContext, true);
        if (backURL != null) {
            MenubarItem item = new MenubarItem("BACK", bundle.getString("BACK"), "halflings halflings-arrow-left", MenubarGroup.BACK, 0, backURL, null, null,
                    null);
            menubar.add(item);
        }

        // Refresh
        if (httpServletRequest.getUserPrincipal() != null) {
            String refreshURL = this.urlFactory.getRefreshPageUrl(portalControllerContext);
            if (refreshURL != null) {
                MenubarItem item = new MenubarItem("REFRESH", bundle.getString("REFRESH"), "glyphicons glyphicons-repeat", MenubarGroup.GENERIC, 100,
                        refreshURL, null, null, null);
                menubar.add(item);
            }
        }


        // Get menubar items, sorted by groups
        Map<MenubarGroup, Set<MenubarItem>> sortedItems = this.getNavbarSortedItems(portalControllerContext);


        // Dyna-window container
        Element dynaWindowContainer = DOM4JUtils.generateDivElement("dyna-window");

        // Dyna-window identifier
        Element dynaWindowId = DOM4JUtils.generateDivElement(null);
        DOM4JUtils.addAttribute(dynaWindowId, HTMLConstants.ID, MENUBAR_WINDOW_ID);
        dynaWindowContainer.add(dynaWindowId);

        // Dyna-window content
        Element dynaWindowContent = DOM4JUtils.generateDivElement("dyna-window-content");
        dynaWindowId.add(dynaWindowContent);


        // Generate toolbar
        Element toolbar = this.generateToolbar(portalControllerContext, sortedItems);
        if (toolbar != null) {
            dynaWindowContent.add(toolbar);
        }


        // Write HTML content
        return DOM4JUtils.write(dynaWindowContainer);
    }


    /**
     * Add customized menubar items.
     *
     * @param portalControllerContext portal controller context
     * @param menubar                 menubar
     */
    private void addCustomizedMenubarItems(PortalControllerContext portalControllerContext, List<MenubarItem> menubar) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(portalControllerContext);

        // Current page
        Page page = PortalObjectUtils.getPage(controllerContext);
        // Base path
        String basePath = page.getProperty("osivia.cms.basePath");

        // Document context
        DocumentContext documentContext;
        try {
            documentContext = cmsService.getDocumentContext(cmsContext, basePath);
        } catch (CMSException e) {
            documentContext = null;
        }


        // Menubar modules
        List<MenubarModule> modules = cmsService.getMenubarModules(cmsContext);
        for (MenubarModule module : modules) {
            try {
                module.customizeSpace(portalControllerContext, menubar, documentContext);
            } catch (PortalException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public Map<MenubarGroup, Set<MenubarItem>> getNavbarSortedItems(PortalControllerContext portalControllerContext) {
        // Items map
        Map<MenubarGroup, Set<MenubarItem>> sortedItems = new TreeMap<MenubarGroup, Set<MenubarItem>>(this.groupComparator);

        // Customized menubar
        List<MenubarItem> customizedMenubar = this.getCustomizedMenubar(portalControllerContext);
        for (MenubarItem item : customizedMenubar) {
            if (item.getParent() != null) {
                MenubarGroup group = item.getParent().getGroup();
                this.addSortedItem(sortedItems, group, item);
            }
        }

        return sortedItems;
    }


    /**
     * Get customized menubar.
     *
     * @param portalControllerContext portal controller context
     * @return customized menubar
     */
    private List<MenubarItem> getCustomizedMenubar(PortalControllerContext portalControllerContext) {
        // HTTP servlet request
        HttpServletRequest httpServletRequest = portalControllerContext.getHttpServletRequest();
        // Bundle
        IBundleFactory bundleFactory = this.internationalizationService.getBundleFactory(this.getClass().getClassLoader());
        Bundle bundle = bundleFactory.getBundle(httpServletRequest.getLocale());

        // Menubar
        List<MenubarItem> menubar = getMenubar(portalControllerContext, true);


        // Configuration dropdown menu
        MenubarDropdown configurationDropdown = new MenubarDropdown(MenubarDropdown.CONFIGURATION_DROPDOWN_MENU_ID, bundle.getString("CONFIGURATION"),
                "glyphicons glyphicons-cogwheel", MenubarGroup.GENERIC, 50, false, false);
        this.addDropdown(portalControllerContext, configurationDropdown);


        // Customized menubar items
        this.addCustomizedMenubarItems(portalControllerContext, menubar);

        return menubar;
    }


    /**
     * Get menubar.
     *
     * @param portalControllerContext portal controller context
     * @param clone                   cloned menubar indicator
     * @return menubar
     */
    private List<MenubarItem> getMenubar(PortalControllerContext portalControllerContext, boolean clone) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Items list
        List<?> list = (List<?>) controllerContext.getAttribute(Scope.REQUEST_SCOPE, Constants.PORTLET_ATTR_MENU_BAR);

        // Menubar items
        List<MenubarItem> menubar;
        if (list == null) {
            menubar = new ArrayList<MenubarItem>();
        } else {
            menubar = new ArrayList<MenubarItem>(list.size());
            for (Object object : list) {
                if (object instanceof MenubarItem) {
                    MenubarItem item = (MenubarItem) object;
                    menubar.add(item);
                }
            }
        }

        if (!clone) {
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, Constants.PORTLET_ATTR_MENU_BAR, menubar);
        }

        return menubar;
    }


    /**
     * {@inheritDoc}
     */
    public String generatePortletContent(PortalControllerContext portalControllerContext, List<MenubarItem> items) {
        // Get menubar items, sorted by groups
        Map<MenubarGroup, Set<MenubarItem>> sortedItems = this.getPortletSortedItems(items);

        Element toolbar = this.generateToolbar(portalControllerContext, sortedItems);
        return DOM4JUtils.write(toolbar);
    }


    /**
     * {@inheritDoc}
     */
    public Map<MenubarGroup, Set<MenubarItem>> getPortletSortedItems(List<MenubarItem> items) {
        // Items map
        Map<MenubarGroup, Set<MenubarItem>> sortedItems = new TreeMap<MenubarGroup, Set<MenubarItem>>(this.groupComparator);

        if (items != null) {
            for (MenubarItem item : items) {
                if (item.getParent() != null) {
                    MenubarGroup group = item.getParent().getGroup();
                    this.addSortedItem(sortedItems, group, item);
                }
            }
        }

        return sortedItems;
    }


    /**
     * Add menubar item into items, sorted by groups.
     *
     * @param sortedItems menubar items, sorted by groups
     * @param group       menubar group
     * @param item        menubar item
     */
    private void addSortedItem(Map<MenubarGroup, Set<MenubarItem>> sortedItems, MenubarGroup group, MenubarItem item) {
        Set<MenubarItem> items = sortedItems.get(group);
        if (items == null) {
            items = new TreeSet<MenubarItem>(this.objectComparator);
            sortedItems.put(group, items);
        }

        items.add(item);
    }


    /**
     * Get menubar dropdown menus.
     *
     * @param portalControllerContext portal controller context
     * @return menubar dropdown menus
     */
    @SuppressWarnings("unchecked")
    private Set<MenubarDropdown> getDropdownMenus(PortalControllerContext portalControllerContext) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Menubar dropdown menus
        Set<MenubarDropdown> dropdownMenus = (Set<MenubarDropdown>) controllerContext.getAttribute(Scope.REQUEST_SCOPE, DROPDOWN_MENUS_REQUEST_ATTRIBUTE);

        if (dropdownMenus == null) {
            dropdownMenus = new HashSet<MenubarDropdown>();
            controllerContext.setAttribute(Scope.REQUEST_SCOPE, DROPDOWN_MENUS_REQUEST_ATTRIBUTE, dropdownMenus);
        }

        return dropdownMenus;
    }


    /**
     * Generate toolbar DOM element.
     *
     * @param portalControllerContext portal controller context
     * @param sortedItems             menubar items, sorted by groups
     * @return toolbar DOM element
     */
    private Element generateToolbar(PortalControllerContext portalControllerContext, Map<MenubarGroup, Set<MenubarItem>> sortedItems) {
        // Menubar container
        Element container;

        if (MapUtils.isNotEmpty(sortedItems)) {
            container = DOM4JUtils.generateDivElement("menubar d-flex align-items-center justify-content-end gap-2 no-ajax-link");

            // Loop on menubar groups
            for (Entry<MenubarGroup, Set<MenubarItem>> sortedItemsEntry : sortedItems.entrySet()) {
                MenubarGroup sortedItemsKey = sortedItemsEntry.getKey();
                Set<MenubarItem> sortedItemsValue = sortedItemsEntry.getValue();

                // Menubar group objects, with dropdown menus
                Set<MenubarObject> objects = new TreeSet<>(this.objectComparator);
                Map<MenubarDropdown, List<MenubarItem>> dropdownMenus = new HashMap<>();
                for (MenubarItem item : sortedItemsValue) {
                    if (item.isVisible()) {
                        MenubarContainer parent = item.getParent();
                        if (parent instanceof MenubarDropdown) {
                            MenubarDropdown dropdownMenu = (MenubarDropdown) parent;
                            List<MenubarItem> dropdownMenuItems = dropdownMenus.get(dropdownMenu);
                            if (dropdownMenuItems == null) {
                                dropdownMenuItems = new ArrayList<>();
                                dropdownMenus.put(dropdownMenu, dropdownMenuItems);
                            }
                            dropdownMenuItems.add(item);

                            if (!dropdownMenu.isTemporary()) {
                                objects.add(dropdownMenu);
                            }
                        } else {
                            objects.add(item);
                        }
                    }
                }

                // Generate group DOM element
                Element group = this.generateGroupElement(container, sortedItemsKey, objects, dropdownMenus);
                container.add(group);
            }
        } else {
            container = null;
        }

        return container;
    }


    /**
     * Generate menubar group DOM element.
     *
     * @param container     toolbar container DOM element
     * @param group         menubar group
     * @param objects       menubar group objects
     * @param dropdownMenus menubar dropdown menus content
     * @return DOM element
     */
    private Element generateGroupElement(Element container, MenubarGroup group, Set<MenubarObject> objects,
                                         Map<MenubarDropdown, List<MenubarItem>> dropdownMenus) {
        // Group toolbar
        Element toolbar = DOM4JUtils.generateDivElement("btn-toolbar align-items-center gap-2 " + StringUtils.trimToEmpty(group.getHtmlClasses()));
        DOM4JUtils.addAttribute(toolbar, "role", "toolbar");

        // State items group
        Element stateItemsGroup = DOM4JUtils.generateDivElement("d-none d-md-flex gap-1");

        // Generic button group
        Element genericGroup = DOM4JUtils.generateDivElement("btn-group btn-group-sm");
        DOM4JUtils.addAttribute(toolbar, "role", "group");

        // Loop on group objects
        for (MenubarObject object : objects) {
            MenubarItem item = null;

            if (object instanceof MenubarDropdown) {
                MenubarDropdown dropdown = (MenubarDropdown) object;
                List<MenubarItem> dropdownMenuItems = dropdownMenus.get(dropdown);

                if (!dropdown.isReducible() || (dropdownMenuItems.size() > 1)) {
                    // Dropdown group
                    Element dropdownGroup = this.generateDropdownElement(container, dropdown, dropdownMenuItems);
                    genericGroup.add(dropdownGroup);
                } else {
                    // Direct link
                    item = dropdownMenuItems.get(0);

                    // Glyphicon
                    if (StringUtils.isBlank(item.getGlyphicon())) {
                        item.setGlyphicon(dropdown.getGlyphicon());
                    }
                }
            } else if (object instanceof MenubarItem) {
                item = (MenubarItem) object;
            }


            if (item != null && item.isVisible()) {
                // Parent
                Element parent;
                if (item.isState()) {
                    parent = stateItemsGroup;
                } else {
                    parent = genericGroup;
                }

                // Item element
                Element itemElement = this.generateItemElement(container, item, false);
                parent.add(itemElement);
            }
        }


        if (CollectionUtils.isNotEmpty(stateItemsGroup.elements())) {
            toolbar.add(stateItemsGroup);
        }
        if (CollectionUtils.isNotEmpty(genericGroup.elements())) {
            toolbar.add(genericGroup);
        }


        return toolbar;
    }


    /**
     * Generate menubar dropdown menu DOM element.
     *
     * @param container         toolbar container DOM element
     * @param dropdown          menubar dropdown menu
     * @param dropdownMenuItems menubar dropdown menu items
     * @return DOM element
     */
    private Element generateDropdownElement(Element container, MenubarDropdown dropdown, List<MenubarItem> dropdownMenuItems) {
        // Dropdown group
        Element dropdownGroup = DOM4JUtils.generateDivElement("btn-group btn-group-sm");
        DOM4JUtils.addAttribute(dropdownGroup, "role", "group");

        // Dropdown button
        Element dropdownButton = DOM4JUtils.generateElement("button", "btn btn-outline-secondary dropdown-toggle", null, dropdown.getGlyphicon(), null);
        DOM4JUtils.addDataAttribute(dropdownButton, "bs-toggle", "dropdown");
        DOM4JUtils.addAriaAttribute(dropdownButton, "expanded", String.valueOf(false));
        if (StringUtils.isNotBlank(dropdown.getTitle())) {
            Element title = DOM4JUtils.generateElement("span", "visually-hidden", dropdown.getTitle());
            dropdownButton.add(title);
        }
        dropdownGroup.add(dropdownButton);

        // Dropdown UL
        Element dropdownUL = DOM4JUtils.generateElement("ul", "dropdown-menu dropdown-menu-end", null);
        dropdownGroup.add(dropdownUL);

        // Dropdown header
        if (StringUtils.isNotBlank(dropdown.getTitle())) {
            Element dropdownHeaderLI = DOM4JUtils.generateElement("li", null, null);
            dropdownUL.add(dropdownHeaderLI);

            Element dropdownHeader = DOM4JUtils.generateElement("h3", "dropdown-header", dropdown.getTitle());
            dropdownHeaderLI.add(dropdownHeader);
        }


        boolean first = true;
        for (MenubarItem dropdownMenuItem : dropdownMenuItems) {
            if (dropdownMenuItem.isVisible()) {
                // Dropdown menu divider
                if ((dropdownMenuItem.isDivider() && !first) || (StringUtils.isNotBlank(dropdownMenuItem.getCategoryTitle()))) {
                    Element dividerLI = DOM4JUtils.generateElement("li", null, null);
                    dropdownUL.add(dividerLI);

                    Element divider = DOM4JUtils.generateElement("hr", "dropdown-divider", null);
                    dividerLI.add(divider);
                }

                if (StringUtils.isNotBlank(dropdownMenuItem.getCategoryTitle())) {
                    Element dropdownHeaderLI = DOM4JUtils.generateElement("li", null, null);
                    dropdownUL.add(dropdownHeaderLI);

                    Element dropdownHeader = DOM4JUtils.generateElement("h3", "dropdown-header", dropdownMenuItem.getCategoryTitle());
                    dropdownHeaderLI.add(dropdownHeader);
                }

                Element dropdownItemLI = this.generateItemElement(container, dropdownMenuItem, true);
                dropdownUL.add(dropdownItemLI);

                if (first) {
                    first = false;
                }
            }
        }

        return dropdownGroup;
    }


    /**
     * Generate menubar item DOM element.
     *
     * @param container    toolbar container DOM element
     * @param item         menubar item
     * @param dropdownItem dropdown item indicator
     * @return DOM element
     */
    private Element generateItemElement(Element container, MenubarItem item, boolean dropdownItem) {
        // Element
        Element element;
        String text = item.getTitle();
        String tooltip = item.getTooltip();
        if (item.getUrl() == null) {
            element = DOM4JUtils.generateElement("span", item.getHtmlClasses(), text, item.getGlyphicon(), null);
        } else {
            // Text and tooltip content
            if (!dropdownItem && (item.getGlyphicon() != null) && StringUtils.isBlank(item.getTooltip())) {
                text = null;
                if (StringUtils.isBlank(item.getTooltip())) {
                    tooltip = item.getTitle();
                }
            }

            // HTML classes
            String htmlClasses = StringUtils.trimToEmpty(item.getHtmlClasses());
            if (dropdownItem) {
                htmlClasses += " dropdown-item";
            } else {
                htmlClasses += " btn btn-outline-secondary";
            }
            if (item.isAjaxDisabled()) {
                htmlClasses += " no-ajax-link";
            }
            if (item.isActive()) {
                htmlClasses += " active";
            }
            if (item.isDisabled()) {
                htmlClasses += " disabled";
            }

            if (item.isDisabled()) {
                element = DOM4JUtils.generateLinkElement("#", null, null, htmlClasses, null);
                DOM4JUtils.addAttribute(element, "disabled", StringUtils.EMPTY);
            } else {
                element = DOM4JUtils.generateLinkElement(item.getUrl(), item.getTarget(), item.getOnclick(), htmlClasses, null);
            }


            // Icon
            Element iconContainer = DOM4JUtils.generateElement("span", "icon-container", StringUtils.EMPTY);
            element.add(iconContainer);

            String glyphicon = item.getGlyphicon();
            if (dropdownItem) {
                glyphicon = StringUtils.trimToEmpty(glyphicon);
            }

            if (item.getCustomizedIcon() != null) {
                iconContainer.add(item.getCustomizedIcon());
            } else if (StringUtils.isNotEmpty(glyphicon)) {
                DOM4JUtils.addGlyphiconText(iconContainer, glyphicon, null);
            }

            // Text
            DOM4JUtils.addText(element, text);


            // External link indicator
            if (dropdownItem && StringUtils.isNotBlank(item.getTarget())) {
                Element externalIndicator = DOM4JUtils.generateElement("small", null, null, "glyphicons glyphicons-new-window-alt", null);
                element.add(externalIndicator);
            }
        }

        // Screen-reader only
        if (StringUtils.isBlank(text) && StringUtils.isNotBlank(tooltip)) {
            Element srOnly = DOM4JUtils.generateElement("span", "visually-hidden", tooltip);
            element.add(srOnly);
        }

        // Tooltip
        if (StringUtils.isNotBlank(tooltip)) {
            DOM4JUtils.addTooltip(element, tooltip);
        }

        // Counter
        if (item.getCounter() != null) {
            Element counterOuter = DOM4JUtils.generateElement("span", "counter small", null);
            element.add(counterOuter);

            Element counterInner = DOM4JUtils.generateElement("span", "badge bg-danger", String.valueOf(item.getCounter()));
            counterOuter.add(counterInner);
        }

        // Data
        if (!item.getData().isEmpty()) {
            for (Entry<String, String> data : item.getData().entrySet()) {
                DOM4JUtils.addDataAttribute(element, data.getKey(), data.getValue());
            }
        }


        // Associated HTML
        String associatedHTML = item.getAssociatedHTML();
        if (StringUtils.isNotBlank(associatedHTML)) {
            CDATA cdata = new DOMCDATA(associatedHTML);
            container.add(cdata);
        }


        // Item parent
        Element parent;
        if (dropdownItem) {
            parent = DOM4JUtils.generateElement("li", null, null);
            parent.add(element);
        } else if (item.isState()) {
            parent = DOM4JUtils.generateDivElement("d-none d-md-block");
            parent.add(element);
        } else {
            parent = element;
        }

        return parent;
    }


    /**
     * {@inheritDoc}
     */
    public List<MenubarItem> getStateItems(PortalControllerContext portalControllerContext) {
        // Get menubar items, sorted by groups
        Map<MenubarGroup, Set<MenubarItem>> sortedItems = this.getNavbarSortedItems(portalControllerContext);

        List<MenubarItem> stateItems = new ArrayList<MenubarItem>();

        for (Entry<MenubarGroup, Set<MenubarItem>> entry : sortedItems.entrySet()) {
            Set<MenubarItem> items = entry.getValue();
            for (MenubarItem item : items) {
                if (item.isState()) {
                    stateItems.add(item);
                }
            }
        }

        return stateItems;
    }


    /**
     * Setter for urlFactory.
     *
     * @param urlFactory the urlFactory to set
     */
    public void setUrlFactory(IPortalUrlFactory urlFactory) {
        this.urlFactory = urlFactory;
    }

    /**
     * Setter for internationalizationService.
     *
     * @param internationalizationService the internationalizationService to set
     */
    public void setInternationalizationService(IInternationalizationService internationalizationService) {
        this.internationalizationService = internationalizationService;
    }

    /**
     * Setter for cmsServiceLocator.
     *
     * @param cmsServiceLocator the cmsServiceLocator to set
     */
    public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
        this.cmsServiceLocator = cmsServiceLocator;
    }

}
