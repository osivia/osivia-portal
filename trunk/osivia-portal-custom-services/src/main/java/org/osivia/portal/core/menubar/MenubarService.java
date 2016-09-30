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
import org.osivia.portal.api.cms.EcmDocument;
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

    /** Menubar dropdown menus request attribute name. */
    private static final String DROPDOWN_MENUS_REQUEST_ATTRIBUTE = "osivia.menubar.dropdownMenus";

    /** Portal URL factory. */
    private IPortalUrlFactory urlFactory;
    /** Internationalization service. */
    private IInternationalizationService internationalizationService;
    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;

    /** Menubar group comparator. */
    private final Comparator<MenubarGroup> groupComparator;
    /** Menubar object comparator. */
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


        // Get menubar items, sorted by groups
        Map<MenubarGroup, Set<MenubarItem>> sortedItems = this.getNavbarSortedItems(portalControllerContext);


        // Back
        String backURL = this.urlFactory.getBackURL(portalControllerContext, true);
        if (backURL != null) {
            MenubarItem item = new MenubarItem("BACK", bundle.getString("BACK"), "halflings halflings-arrow-left", MenubarGroup.BACK, 0, backURL, null, null,
                    null);
            this.addSortedItem(sortedItems, MenubarGroup.BACK, item);
        }


        // Refresh
        if (httpServletRequest.getUserPrincipal() != null) {
            String refreshURL = this.urlFactory.getRefreshPageUrl(portalControllerContext);
            if (refreshURL != null) {
                MenubarItem item = new MenubarItem("REFRESH", bundle.getString("REFRESH"), "glyphicons glyphicons-repeat", MenubarGroup.GENERIC, 100,
                        refreshURL, null, null, null);
                this.addSortedItem(sortedItems, MenubarGroup.GENERIC, item);
            }
        }


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
     * @param menubar menubar
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
        DocumentContext<? extends EcmDocument> documentContext;
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
            MenubarGroup group = item.getParent().getGroup();
            this.addSortedItem(sortedItems, group, item);
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
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // HTTP servlet request
        HttpServletRequest httpServletRequest = portalControllerContext.getHttpServletRequest();
        // Bundle
        IBundleFactory bundleFactory = this.internationalizationService.getBundleFactory(this.getClass().getClassLoader());
        Bundle bundle = bundleFactory.getBundle(httpServletRequest.getLocale());

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


        // Configuration dropdown menu
        MenubarDropdown configurationDropdown = new MenubarDropdown(MenubarDropdown.CONFIGURATION_DROPDOWN_MENU_ID, bundle.getString("CONFIGURATION"),
                "glyphicons glyphicons-cogwheel", MenubarGroup.GENERIC, 50, false, false);
        this.addDropdown(portalControllerContext, configurationDropdown);


        // Customized menubar items
        this.addCustomizedMenubarItems(portalControllerContext, menubar);

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
                MenubarGroup group = item.getParent().getGroup();
                this.addSortedItem(sortedItems, group, item);
            }
        }

        return sortedItems;
    }


    /**
     * Add menubar item into items, sorted by groups.
     *
     * @param sortedItems menubar items, sorted by groups
     * @param group menubar group
     * @param item menubar item
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
     * @param sortedItems menubar items, sorted by groups
     * @return toolbar DOM element
     */
    private Element generateToolbar(PortalControllerContext portalControllerContext, Map<MenubarGroup, Set<MenubarItem>> sortedItems) {
        Element toolbar;
        if (MapUtils.isNotEmpty(sortedItems)) {
            // Toolbar
            toolbar = DOM4JUtils.generateElement(HTMLConstants.UL, "menubar no-ajax-link", null);
            DOM4JUtils.addAttribute(toolbar, HTMLConstants.ROLE, HTMLConstants.ROLE_TOOLBAR);

            // Loop on menubar groups
            for (Entry<MenubarGroup, Set<MenubarItem>> sortedItemsEntry : sortedItems.entrySet()) {
                MenubarGroup sortedItemsKey = sortedItemsEntry.getKey();
                Set<MenubarItem> sortedItemsValue = sortedItemsEntry.getValue();

                // Menubar group objects, with dropdown menus
                Set<MenubarObject> objects = new TreeSet<MenubarObject>(this.objectComparator);
                Map<MenubarDropdown, List<MenubarItem>> dropdownMenus = new HashMap<MenubarDropdown, List<MenubarItem>>();
                for (MenubarItem item : sortedItemsValue) {
                    MenubarContainer parent = item.getParent();
                    if (parent instanceof MenubarDropdown) {
                        MenubarDropdown dropdownMenu = (MenubarDropdown) parent;
                        List<MenubarItem> dropdownMenuItems = dropdownMenus.get(dropdownMenu);
                        if (dropdownMenuItems == null) {
                            dropdownMenuItems = new ArrayList<MenubarItem>();
                            dropdownMenus.put(dropdownMenu, dropdownMenuItems);
                        }
                        dropdownMenuItems.add(item);

                        objects.add(dropdownMenu);
                    } else {
                        objects.add(item);
                    }
                }

                // Generate group DOM element
                Element groupLI = this.generateGroupElement(sortedItemsKey, objects, dropdownMenus);
                toolbar.add(groupLI);
            }
        } else {
            toolbar = null;
        }

        return toolbar;
    }


    /**
     * Generate menubar group DOM element.
     *
     * @param group menubar group
     * @param objects menubar group objects
     * @param dropdownMenus menubar dropdown menus content
     * @return DOM element
     */
    private Element generateGroupElement(MenubarGroup group, Set<MenubarObject> objects, Map<MenubarDropdown, List<MenubarItem>> dropdownMenus) {
        // Group LI
        Element groupLI = DOM4JUtils.generateElement(HTMLConstants.LI, group.getHtmlClasses(), null);

        // State items group UL
        Element stateItemsGroupUL = DOM4JUtils.generateElement(HTMLConstants.UL, "hidden-xs", null);

        // Generic group UL
        Element genericGroupUL = DOM4JUtils.generateElement(HTMLConstants.UL, null, null);

        // "hidden-xs" group UL
        Element hiddenXSGroupUL = DOM4JUtils.generateElement(HTMLConstants.UL, "hidden-xs", null);

        // Loop on group objects
        for (MenubarObject object : objects) {
            MenubarItem item = null;

            if (object instanceof MenubarDropdown) {
                MenubarDropdown dropdown = (MenubarDropdown) object;
                List<MenubarItem> dropdownMenuItems = dropdownMenus.get(dropdown);

                if (!dropdown.isReducible() || (dropdownMenuItems.size() > 1)) {
                    // Dropdown menu

                    // Dropdown LI
                    Element dropdownLI = this.generateDropdownElement(dropdown, dropdownMenuItems);
                    genericGroupUL.add(dropdownLI);
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


            if (item != null) {
                // Parent
                Element parent;
                if (item.isState()) {
                    parent = stateItemsGroupUL;
                } else if (StringUtils.contains(item.getHtmlClasses(), "hidden-xs")) {
                    parent = hiddenXSGroupUL;
                } else {
                    parent = genericGroupUL;
                }

                // LI
                Element li = this.generateItemElement(item, false);
                parent.add(li);
            }
        }


        if (CollectionUtils.isNotEmpty(stateItemsGroupUL.elements())) {
            groupLI.add(stateItemsGroupUL);
        }
        if (CollectionUtils.isNotEmpty(genericGroupUL.elements())) {
            groupLI.add(genericGroupUL);
        }
        if (CollectionUtils.isNotEmpty(hiddenXSGroupUL.elements())) {
            groupLI.add(hiddenXSGroupUL);
        }


        return groupLI;
    }


    /**
     * Generate menubar dropdown menu DOM element.
     *
     * @param dropdown menubar dropdown menu
     * @param dropdownMenuItems menubar dropdown menu items
     * @return DOM element
     */
    private Element generateDropdownElement(MenubarDropdown dropdown, List<MenubarItem> dropdownMenuItems) {
        // Dropdown LI
        Element dropdownLI;
        if (dropdown.isBreadcrumb()) {
            dropdownLI = DOM4JUtils.generateElement(HTMLConstants.LI, "dropdown content-navbar-hidden", null);
        } else {
            dropdownLI = DOM4JUtils.generateElement(HTMLConstants.LI, "dropdown", null);
        }

        // Dropdown button
        Element dropdownButton = DOM4JUtils.generateLinkElement("#", null, null, "dropdown-toggle", null, dropdown.getGlyphicon());
        DOM4JUtils.addAttribute(dropdownButton, HTMLConstants.DATA_TOGGLE, "dropdown");
        if (dropdown.getTitle() != null) {
            Element srOnly = DOM4JUtils.generateElement(HTMLConstants.SPAN, "sr-only", dropdown.getTitle());
            dropdownButton.add(srOnly);
        }
        dropdownLI.add(dropdownButton);

        // Dropdown UL
        Element dropdownUL = DOM4JUtils.generateElement(HTMLConstants.UL, "dropdown-menu dropdown-menu-right", null, null, AccessibilityRoles.MENU);
        dropdownLI.add(dropdownUL);


        // Dropdown header
        if (StringUtils.isNotBlank(dropdown.getTitle())) {
            Element dropdownHeaderLI = DOM4JUtils.generateElement(HTMLConstants.LI, "dropdown-header", dropdown.getTitle(), null,
                    AccessibilityRoles.PRESENTATION);
            dropdownUL.add(dropdownHeaderLI);
        }


        boolean first = true;
        for (MenubarItem dropdownMenuItem : dropdownMenuItems) {
            // Dropdown menu divider
            if (dropdownMenuItem.isDivider() && !first) {
                String htmlClasses = "divider";
                if (dropdownMenuItem.isBreadcrumb()) {
                    htmlClasses += " content-navbar-hidden";
                }

                Element dividerLI = DOM4JUtils.generateElement(HTMLConstants.LI, htmlClasses, StringUtils.EMPTY, null, AccessibilityRoles.PRESENTATION);
                dropdownUL.add(dividerLI);
            }

            Element dropdownItemLI = this.generateItemElement(dropdownMenuItem, true);
            dropdownUL.add(dropdownItemLI);

            if (first) {
                first = false;
            }
        }

        return dropdownLI;
    }


    /**
     * Generate menubar item DOM element.
     *
     * @param item menubar item
     * @param dropdownItem dropdown item indicator
     * @return DOM element
     */
    private Element generateItemElement(MenubarItem item, boolean dropdownItem) {
        // HTML classes
        StringBuilder htmlClasses = new StringBuilder();
        if (item.getUrl() == null) {
            if (dropdownItem) {
                htmlClasses.append("dropdown-header ");
            } else {
                htmlClasses.append("text ");
            }
        }
        if (item.isState()) {
            htmlClasses.append("hidden-xs ");
        }
        if (item.isAjaxDisabled()) {
            htmlClasses.append("no-ajax-link ");
        }
        if (item.isActive()) {
            htmlClasses.append("active ");
        }
        if (item.isDisabled()) {
            htmlClasses.append("disabled ");
        }
        if (item.isBreadcrumb()) {
            htmlClasses.append("content-navbar-hidden ");
        }

        // Role
        AccessibilityRoles roleLI;
        AccessibilityRoles roleElement;
        if (dropdownItem) {
            roleLI = AccessibilityRoles.PRESENTATION;
            roleElement = AccessibilityRoles.MENU_ITEM;
        } else {
            roleLI = null;
            roleElement = null;
        }

        // LI
        Element li = DOM4JUtils.generateElement(HTMLConstants.LI, htmlClasses.toString(), null, null, roleLI);


        // Element
        Element element;
        String text = item.getTitle();
        String tooltip = item.getTooltip();
        if (item.getUrl() == null) {
            element = DOM4JUtils.generateElement(HTMLConstants.SPAN, item.getHtmlClasses(), text, item.getGlyphicon(), roleElement);
        } else {
            // Text and tooltip content
            if (!dropdownItem && (item.getGlyphicon() != null) && StringUtils.isBlank(item.getTooltip())) {
                text = null;
                if (StringUtils.isBlank(item.getTooltip())) {
                    tooltip = item.getTitle();
                }
            }

            // Glyphicon
            String glyphicon = item.getGlyphicon();
            if (dropdownItem) {
                glyphicon = StringUtils.trimToEmpty(glyphicon);
            }

            element = DOM4JUtils.generateLinkElement(item.getUrl(), item.getTarget(), item.getOnclick(), item.getHtmlClasses(), text, glyphicon, roleElement);

            // External link indicator
            if (dropdownItem && StringUtils.isNotBlank(item.getTarget())) {
                Element externalIndicator = DOM4JUtils.generateElement(HTMLConstants.SMALL, null, null, "glyphicons glyphicons-new-window-alt", null);
                element.add(externalIndicator);
            }
        }

        // Screen-reader only
        if (StringUtils.isBlank(text) && StringUtils.isNotBlank(tooltip)) {
            Element srOnly = DOM4JUtils.generateElement(HTMLConstants.SPAN, "sr-only", tooltip);
            element.add(srOnly);
        }

        // Tooltip
        if (StringUtils.isNotBlank(tooltip)) {
            DOM4JUtils.addTooltip(element, tooltip);
        }

        // Data
        if (!item.getData().isEmpty()) {
            for (Entry<String, String> data : item.getData().entrySet()) {
                StringBuilder attributeName = new StringBuilder();
                attributeName.append("data-");
                attributeName.append(data.getKey());

                DOM4JUtils.addAttribute(element, attributeName.toString(), data.getValue());
            }
        }
        li.add(element);


        // Associated HTML
        if (StringUtils.isNotBlank(item.getAssociatedHTML())) {
            CDATA cdata = new DOMCDATA(item.getAssociatedHTML());
            li.add(cdata);
        }

        return li;
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
