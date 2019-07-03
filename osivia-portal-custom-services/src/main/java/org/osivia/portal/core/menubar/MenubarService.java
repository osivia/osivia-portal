package org.osivia.portal.core.menubar;

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
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.menubar.*;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.Map.Entry;

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
     * Menubar group comparator.
     */
    private final Comparator<MenubarGroup> groupComparator;
    /**
     * Menubar object comparator.
     */
    private final Comparator<MenubarObject> objectComparator;
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
            MenubarItem item = new MenubarItem("BACK", bundle.getString("BACK"), "glyphicons glyphicons-basic-step-back", MenubarGroup.BACK, 0, backURL, null, null,
                    null);
            menubar.add(item);
        }

        // Refresh
        if (httpServletRequest.getUserPrincipal() != null) {
            String refreshURL = this.urlFactory.getRefreshPageUrl(portalControllerContext);
            if (refreshURL != null) {
                MenubarItem item = new MenubarItem("REFRESH", bundle.getString("REFRESH"), "glyphicons glyphicons-basic-reload", MenubarGroup.GENERIC, 100,
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
        Map<MenubarGroup, Set<MenubarItem>> sortedItems = new TreeMap<>(this.groupComparator);

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
            menubar = new ArrayList<>();
        } else {
            menubar = new ArrayList<>(list.size());
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
        Map<MenubarGroup, Set<MenubarItem>> sortedItems = new TreeMap<>(this.groupComparator);

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
            items = new TreeSet<>(this.objectComparator);
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
            dropdownMenus = new HashSet<>();
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
        // Container
        Element container = DOM4JUtils.generateDivElement("menubar");

        if (MapUtils.isNotEmpty(sortedItems)) {
            // Navbar
            Element navbar = DOM4JUtils.generateElement("nav", "navbar navbar-expand navbar-light px-0", null);
            container.add(navbar);

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

                // Generate nav DOM element
                Element nav = this.generateNavElement(container, sortedItemsKey, objects, dropdownMenus);
                navbar.add(nav);
            }
        }

        return container;
    }


    /**
     * Generate nav DOM element.
     *
     * @param container     toolbar container DOM element
     * @param group         menubar group
     * @param objects       menubar group objects
     * @param dropdownMenus menubar dropdown menus content
     * @return DOM element
     */
    private Element generateNavElement(Element container, MenubarGroup group, Set<MenubarObject> objects, Map<MenubarDropdown, List<MenubarItem>> dropdownMenus) {
        // Nav
        Element nav = DOM4JUtils.generateElement(HTMLConstants.UL, "navbar-nav " + StringUtils.trimToEmpty(group.getHtmlClasses()), null);

        // State items
        List<Element> stateItems = new ArrayList<>();
        // Generic items
        List<Element> genericItems = new ArrayList<>();

        // Loop on group objects
        for (MenubarObject object : objects) {
            MenubarItem item = null;

            if (object instanceof MenubarDropdown) {
                MenubarDropdown dropdown = (MenubarDropdown) object;
                List<MenubarItem> dropdownMenuItems = dropdownMenus.get(dropdown);

                if (!dropdown.isReducible() || (dropdownMenuItems.size() > 1)) {
                    // Dropdown item
                    Element dropdownItem = this.generateDropdownElement(container, dropdown, dropdownMenuItems);
                    genericItems.add(dropdownItem);
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
                // LI
                Element li = this.generateItemElement(container, item, false);
                if (item.isState()) {
                    stateItems.add(li);
                } else {
                    genericItems.add(li);
                }
            }
        }


        for (Element item : stateItems) {
            nav.add(item);
        }
        for (Element item : genericItems) {
            nav.add(item);
        }

        return nav;
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
        // HTML classes
        StringBuilder htmlClasses = new StringBuilder();
        htmlClasses.append("nav-item dropdown ");
        if (dropdown.isBreadcrumb()) {
            htmlClasses.append("menubar-breadcrumb-item ");
        }

        // Dropdown container
        Element dropdownContainer = DOM4JUtils.generateElement(HTMLConstants.LI, htmlClasses.toString(), null);

        // Dropdown toggle button
        Element toggleButton = DOM4JUtils.generateLinkElement("javascript:;", null, null, "nav-link dropdown-toggle", null, dropdown.getGlyphicon());
        DOM4JUtils.addAttribute(toggleButton, HTMLConstants.DATA_TOGGLE, "dropdown");
        if (StringUtils.isNotBlank(dropdown.getTitle())) {
            Element srOnly = DOM4JUtils.generateElement(HTMLConstants.SPAN, "sr-only", dropdown.getTitle());
            toggleButton.add(srOnly);
        }
        dropdownContainer.add(toggleButton);

        // Dropdown menu
        Element menu = DOM4JUtils.generateDivElement("dropdown-menu dropdown-menu-right");
        dropdownContainer.add(menu);


        // Dropdown header
        if (StringUtils.isNotBlank(dropdown.getTitle())) {
            Element header = DOM4JUtils.generateElement(HTMLConstants.H3, "dropdown-header", dropdown.getTitle());
            menu.add(header);
        }


        boolean first = true;
        for (MenubarItem dropdownMenuItem : dropdownMenuItems) {
            if (dropdownMenuItem.isVisible()) {
                // Dropdown menu divider
                if (dropdownMenuItem.isDivider() && !first) {
                    Element divider = DOM4JUtils.generateDivElement("dropdown-divider");
                    menu.add(divider);
                }

                Element dropdownItem = this.generateItemElement(container, dropdownMenuItem, true);
                menu.add(dropdownItem);

                if (first) {
                    first = false;
                }
            }
        }

        return dropdownContainer;
    }


    @Override
    public Element generateItemElement(Element container, MenubarItem menubarItem, boolean dropdownItem) {
        // HTML classes
        List<String> htmlClasses = new ArrayList<>();
        htmlClasses.addAll(Arrays.asList(StringUtils.split(StringUtils.trimToEmpty(menubarItem.getHtmlClasses()), " ")));
        if (menubarItem.isAjaxDisabled()) {
            htmlClasses.add("no-ajax-link");
        }
        if (menubarItem.isActive()) {
            htmlClasses.add("active");
        }
        if (menubarItem.isDisabled()) {
            htmlClasses.add("disabled");
        }
        if (menubarItem.isBreadcrumb()) {
            htmlClasses.add("menubar-breadcrumb-item");
        }
        if (dropdownItem && menubarItem.isDisabled()) {
            htmlClasses.add("disabled");
        }


        // URL
        String url;
        if (StringUtils.isEmpty(menubarItem.getUrl())) {
            url = null;
        } else if (menubarItem.isDisabled()) {
            url = "#";
        } else {
            url = menubarItem.getUrl();
        }


        // Item
        Element item;
        if (dropdownItem && StringUtils.isEmpty(url)) {
            // Dropdown header
            htmlClasses.add("dropdown-header");
            item = DOM4JUtils.generateElement("div", StringUtils.join(htmlClasses, " "), null);
        } else if (dropdownItem) {
            // Dropdown item
            htmlClasses.add("dropdown-item");
            item = DOM4JUtils.generateLinkElement(url, menubarItem.getTarget(), menubarItem.getOnclick(), StringUtils.join(htmlClasses, " "), null);
        } else if (StringUtils.isEmpty(url)) {
            // Navbar text
            item = DOM4JUtils.generateElement("span", "navbar-text", null);
        } else {
            // Navbar link
            item = DOM4JUtils.generateLinkElement(url, menubarItem.getTarget(), menubarItem.getOnclick(), "nav-link", null);
        }

        // Item icon
        Element icon;
        if (menubarItem.getCustomizedIcon() != null) {
            icon = menubarItem.getCustomizedIcon().createCopy();
        } else if (StringUtils.isNotEmpty(menubarItem.getGlyphicon())) {
            icon = DOM4JUtils.generateElement("i", menubarItem.getGlyphicon(), StringUtils.EMPTY);
        } else {
            icon = null;
        }
        if (icon != null) {
            item.add(icon);
        }

        // Item text
        Element text;
        if (dropdownItem || (icon == null)) {
            text = DOM4JUtils.generateElement("span", null, menubarItem.getTitle());
        } else {
            text = null;
        }
        if (text != null) {
            item.add(text);
        }

        // Item counter
        if (menubarItem.getCounter() != null) {
            Element counter = DOM4JUtils.generateElement("span", "badge badge-danger", String.valueOf(menubarItem.getCounter()));
            item.add(counter);
        }

        // Item tooltip
        String tooltip;
        if (StringUtils.isNotEmpty(menubarItem.getTooltip())) {
            tooltip = menubarItem.getTooltip();
        } else if (StringUtils.isNotEmpty(menubarItem.getTitle()) && (text == null)) {
            tooltip = menubarItem.getTitle();
        } else {
            tooltip = null;
        }
        if (StringUtils.isNotEmpty(tooltip)) {
            DOM4JUtils.addTooltip(item, tooltip);
        }

        // Item external link indicator
        if (dropdownItem && StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(menubarItem.getTarget())) {
            Element externalIndicator = DOM4JUtils.generateElement(HTMLConstants.SMALL, "d-inline-block align-text-bottom", null, "glyphicons glyphicons-basic-square-new-window", null);
            item.add(externalIndicator);
        }

        // Item data
        if (!menubarItem.getData().isEmpty()) {
            for (Entry<String, String> data : menubarItem.getData().entrySet()) {
                DOM4JUtils.addAttribute(item, "data-" + data.getKey(), data.getValue());
            }
        }


        // Result
        Element result;
        if (dropdownItem) {
            result = item;
        } else {
            htmlClasses.add("nav-item");
            if (menubarItem.isDisabled()) {
                htmlClasses.add("disabled");
            }

            result = DOM4JUtils.generateElement("li", StringUtils.join(htmlClasses, " "), null);
            result.add(item);
        }


        // Associated HTML
        String associatedHTML = menubarItem.getAssociatedHTML();
        if (StringUtils.isNotBlank(associatedHTML) && (container != null)) {
            CDATA cdata = new DOMCDATA(associatedHTML);
            container.add(cdata);
        }

        return result;
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
