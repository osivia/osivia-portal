package org.osivia.portal.core.ui.layout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.ui.layout.LayoutGroup;
import org.osivia.portal.api.ui.layout.LayoutItem;
import org.osivia.portal.api.ui.layout.LayoutItemsService;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

import javax.naming.Name;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Layout items service implementation.
 *
 * @author Cédric Krommenhoek
 * @see LayoutItemsService
 */
public class LayoutItemsServiceImpl implements LayoutItemsService {

    /**
     * Layout groups page property.
     */
    private static final String LAYOUT_GROUPS_PROPERTY = "osivia.layout.groups";

    /**
     * Selected layout items session attribute suffix.
     */
    private static final String SELECTED_LAYOUT_ITEMS_ATTRIBUTE_SUFFIX = ".selected-layout-items";


    /**
     * Log.
     */
    private final Log log;


    /**
     * Constructor.
     */
    public LayoutItemsServiceImpl() {
        super();

        // Log
        this.log = LogFactory.getLog(this.getClass());
    }


    @Override
    public List<LayoutGroup> getGroups(PortalControllerContext portalControllerContext) {
        // Layout groups implementation
        List<LayoutGroupImpl> groupsImpl = this.getGroupsImpl(portalControllerContext);

        // Layout groups
        List<LayoutGroup> groups;

        if (CollectionUtils.isEmpty(groupsImpl)) {
            groups = null;
        } else {
            groups = new ArrayList<>(groupsImpl.size());
            groups.addAll(groupsImpl);
        }

        return groups;
    }


    @Override
    public LayoutGroup getGroup(PortalControllerContext portalControllerContext, String groupId) {
        // Layout groups
        List<LayoutGroup> groups = this.getGroups(portalControllerContext);

        // Selected layout group
        LayoutGroup group = null;
        if (CollectionUtils.isNotEmpty(groups)) {
            Iterator<LayoutGroup> iterator = groups.iterator();
            while ((group == null) && iterator.hasNext()) {
                LayoutGroup next = iterator.next();
                if (StringUtils.equals(groupId, next.getId())) {
                    group = next;
                }
            }
        }

        if (group == null) {
            // New layout group
            LayoutGroupImpl groupImpl = new LayoutGroupImpl();
            groupImpl.setId(groupId);

            group = groupImpl;
        }

        return group;
    }


    @Override
    public void setGroup(PortalControllerContext portalControllerContext, LayoutGroup group) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);

        if ((group != null) && (page != null)) {
            // Page property
            String property;

            // Layout groups
            List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);
            if (CollectionUtils.isEmpty(groups)) {
                groups = new ArrayList<>(1);
            } else {
                // Remove layout group
                boolean removed = false;
                Iterator<LayoutGroupImpl> iterator = groups.iterator();
                while (!removed && iterator.hasNext()) {
                    LayoutGroup next = iterator.next();
                    if (StringUtils.equals(group.getId(), next.getId())) {
                        iterator.remove();
                        removed = true;
                    }
                }
            }

            // Layout items
            List<LayoutItemImpl> items;
            if (CollectionUtils.isEmpty(group.getItems())) {
                items = new ArrayList<>(0);
            } else {
                items = new ArrayList<>(group.getItems().size());
                for (LayoutItem item : group.getItems()) {
                    LayoutItemImpl itemImpl;
                    if (item instanceof LayoutItemImpl) {
                        itemImpl = (LayoutItemImpl) item;
                    } else {
                        itemImpl = new LayoutItemImpl();
                        try {
                            BeanUtils.copyProperties(itemImpl, item);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            itemImpl = null;
                            this.log.error(e.getLocalizedMessage());
                        }
                    }

                    if (itemImpl != null) {
                        items.add(itemImpl);
                    }
                }
            }

            // Added layout group implementation
            LayoutGroupImpl groupImpl = new LayoutGroupImpl();
            groupImpl.setId(group.getId());
            groupImpl.setLabel(group.getLabel());
            groupImpl.setItemsImpl(items);
            groups.add(groupImpl);

            // Container
            LayoutGroupsContainer container = new LayoutGroupsContainer();
            container.setGroups(groups);

            // JSON object mapper
            ObjectMapper mapper = new ObjectMapper();
            try {
                property = mapper.writeValueAsString(container);
            } catch (JsonProcessingException e) {
                property = null;
                this.log.error(e.getLocalizedMessage());
            }

            page.setDeclaredProperty(LAYOUT_GROUPS_PROPERTY, property);
        }
    }


    @Override
    public List<LayoutItem> getItems(PortalControllerContext portalControllerContext, String groupId) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // Administrator indicator
        boolean admin = PageCustomizerInterceptor.isAdministrator(controllerContext);

        // Layout group
        LayoutGroup group = this.getGroup(portalControllerContext, groupId);


        // Layout items
        List<LayoutItem> items;

        if (group == null) {
            items = null;
        } else {
            // Current person
            Person person = this.getCurrentPerson(portalControllerContext);
            // Current person profiles
            List<String> profiles;
            if ((person == null) || CollectionUtils.isEmpty(person.getProfiles())) {
                profiles = null;
            } else {
                profiles = new ArrayList<>(person.getProfiles().size());
                for (Name name : person.getProfiles()) {
                    Name suffix = name.getSuffix(name.size());
                    String profile = StringUtils.substringBefore(suffix.toString(), "=");
                    if (StringUtils.isNotEmpty(profile)) {
                        profiles.add(profile);
                    }
                }
            }

            // Layout items
            items = new ArrayList<>(group.getItems().size());
            for (LayoutItem item : group.getItems()) {
                if (admin || CollectionUtils.isEmpty(item.getProfiles()) || (CollectionUtils.isNotEmpty(profiles) && CollectionUtils.containsAny(profiles, item.getProfiles()))) {
                    items.add(item);
                }
            }
        }

        return items;
    }


    @Override
    public List<LayoutItem> getCurrentItems(PortalControllerContext portalControllerContext) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);

        // Layout groups
        List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);

        // Current layout items
        List<LayoutItem> currentItems;

        if ((page == null) || CollectionUtils.isEmpty(groups)) {
            currentItems = null;
        } else {
            currentItems = new ArrayList<>(groups.size());

            // Selected layout items
            SelectedLayoutItems selectedLayoutItems = this.getSelectedLayoutItems(portalControllerContext, page);

            for (LayoutGroupImpl group : groups) {
                if (CollectionUtils.isNotEmpty(group.getItems())) {
                    LayoutItem currentItem = this.getCurrentItem(selectedLayoutItems, group);
                    currentItems.add(currentItem);
                }
            }
        }

        return currentItems;
    }


    @Override
    public LayoutItem getCurrentItem(PortalControllerContext portalControllerContext, String groupId) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);
        //  Layout group
        LayoutGroup group = this.getGroup(portalControllerContext, groupId);

        // Current layout item
        LayoutItem currentItem = null;

        if ((page != null) && (group != null)) {
            // Selected layout items
            SelectedLayoutItems selectedLayoutItems = this.getSelectedLayoutItems(portalControllerContext, page);

            if (CollectionUtils.isNotEmpty(group.getItems())) {
                currentItem = this.getCurrentItem(selectedLayoutItems, group);
            }
        }

        return currentItem;
    }


    @Override
    public void selectItem(PortalControllerContext portalControllerContext, String id) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);
        // Layout groups
        List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);

        if (StringUtils.isNotEmpty(id) && (page != null) && CollectionUtils.isNotEmpty(groups)) {
            // Related layout group
            LayoutGroupImpl group = this.getRelatedGroup(groups, id);

            if (group != null) {
                // Selected layout items
                SelectedLayoutItems selectedLayoutItems = this.getSelectedLayoutItems(portalControllerContext, page);
                selectedLayoutItems.getSelection().put(group.getId(), id);
                selectedLayoutItems.getComputedWindowIds().remove(group.getId());
            }
        }
    }


    @Override
    public boolean isSelected(PortalControllerContext portalControllerContext, String itemId) {
        // Selected layout item indicator
        boolean selected = false;

        // Current layout items
        List<LayoutItem> items = this.getCurrentItems(portalControllerContext);

        if (CollectionUtils.isNotEmpty(items)) {
            Iterator<LayoutItem> iterator = items.iterator();
            while (!selected && iterator.hasNext()) {
                LayoutItem item = iterator.next();
                selected = StringUtils.equals(itemId, item.getId());
            }
        }

        return selected;
    }


    @Override
    public void markWindowAsRendered(PortalControllerContext portalControllerContext, Window window) {
        // Layout item identifier
        String id = window.getDeclaredProperty(LINKED_ITEM_ID_WINDOW_PROPERTY);
        // Layout groups
        List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);
        // Related layout group
        LayoutGroupImpl group = this.getRelatedGroup(groups, id);

        if (group != null) {
            // Window identifier
            String windowId = window.getId().toString();
            // Selected layout items
            SelectedLayoutItems selectedLayoutItems = this.getSelectedLayoutItems(portalControllerContext, window.getPage());

            // Computed window identifiers
            List<String> computedWindowIds = selectedLayoutItems.getComputedWindowIds().get(group.getId());
            if (computedWindowIds == null) {
                computedWindowIds = new ArrayList<>();
                selectedLayoutItems.getComputedWindowIds().put(group.getId(), computedWindowIds);
            }

            if (!computedWindowIds.contains(windowId)) {
                computedWindowIds.add(windowId);
            }
        }
    }


    @Override
    public boolean isDirty(PortalControllerContext portalControllerContext, Window window) {
        // Layout item identifier
        String id = window.getDeclaredProperty(LINKED_ITEM_ID_WINDOW_PROPERTY);
        // Layout groups
        List<LayoutGroupImpl> groups = this.getGroupsImpl(portalControllerContext);
        // Related layout group
        LayoutGroupImpl group = this.getRelatedGroup(groups, id);

        // Dirty window indicator
        boolean dirty;

        if ((group == null) || !this.isSelected(portalControllerContext, id)) {
            dirty = true;
        } else {
            // Window identifier
            String windowId = window.getId().toString();
            // Selected layout items
            SelectedLayoutItems selectedLayoutItems = this.getSelectedLayoutItems(portalControllerContext, window.getPage());

            // Computed window identifiers
            List<String> computedWindowIds = selectedLayoutItems.getComputedWindowIds().get(group.getId());

            dirty = CollectionUtils.isEmpty(computedWindowIds) || !computedWindowIds.contains(windowId);
        }

        return dirty;
    }


    /**
     * Get selected layout items.
     *
     * @param portalControllerContext portal controller context
     * @param page                    current page
     * @return selected layout items
     */
    private SelectedLayoutItems getSelectedLayoutItems(PortalControllerContext portalControllerContext, Page page) {
        // HTTP session
        HttpSession session = this.getSession(portalControllerContext);

        // Selected layout items
        SelectedLayoutItems selectedLayoutItems;

        // HTTP session attribute name
        String name = page.getId().toString(PortalObjectPath.SAFEST_FORMAT) + SELECTED_LAYOUT_ITEMS_ATTRIBUTE_SUFFIX;

        // HTTP session attribute
        Object attribute = session.getAttribute(name);

        if (attribute instanceof SelectedLayoutItems) {
            selectedLayoutItems = (SelectedLayoutItems) attribute;
        } else {
            selectedLayoutItems = new SelectedLayoutItems();
            session.setAttribute(name, selectedLayoutItems);
        }

        return selectedLayoutItems;
    }


    /**
     * Get HTTP session.
     *
     * @param portalControllerContext portal controller context
     * @return HTTP session
     */
    private HttpSession getSession(PortalControllerContext portalControllerContext) {
        // HTTP servlet request
        HttpServletRequest servletRequest = portalControllerContext.getHttpServletRequest();

        // HTTP session
        HttpSession session;
        if (servletRequest == null) {
            session = null;
        } else {
            session = servletRequest.getSession();
        }

        return session;
    }


    /**
     * Get current page.
     *
     * @param portalControllerContext portal controller context
     * @return page
     */
    private Page getCurrentPage(PortalControllerContext portalControllerContext) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        return PortalObjectUtils.getPage(controllerContext);
    }


    /**
     * Get current person.
     *
     * @param portalControllerContext portal controller context
     * @return person
     */
    private Person getCurrentPerson(PortalControllerContext portalControllerContext) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // Server invocation
        ServerInvocation invocation = controllerContext.getServerInvocation();

        return (Person) invocation.getAttribute(Scope.SESSION_SCOPE, Constants.ATTR_LOGGED_PERSON_2);
    }


    /**
     * Get layout groups implementation.
     *
     * @param portalControllerContext portal controller context
     * @return layout groups
     */
    private List<LayoutGroupImpl> getGroupsImpl(PortalControllerContext portalControllerContext) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);

        // Layout groups
        List<LayoutGroupImpl> groups;

        if (page == null) {
            groups = null;
        } else {
            // Page property
            String property = page.getDeclaredProperty(LAYOUT_GROUPS_PROPERTY);

            // Layout groups container
            LayoutGroupsContainer container;

            if (StringUtils.isEmpty(property)) {
                container = null;
            } else {
                // JSON object mapper
                ObjectMapper mapper = new ObjectMapper();
                try {
                    container = mapper.readValue(property, LayoutGroupsContainer.class);
                } catch (JsonProcessingException e) {
                    container = null;
                    this.log.error(e.getLocalizedMessage());
                }
            }

            if ((container == null) || CollectionUtils.isEmpty(container.getGroups())) {
                groups = null;
            } else {
                groups = container.getGroups();

                for (LayoutGroupImpl group : groups) {
                    // Copy items implementation to generic items
                    List<LayoutItemImpl> itemsImpl = group.getItemsImpl();
                    if (CollectionUtils.isNotEmpty(itemsImpl)) {
                        group.getItems().addAll(itemsImpl);
                    }
                }
            }
        }

        return groups;
    }


    /**
     * Get current layout item.
     *
     * @param selectedLayoutItems selected layout items
     * @param group               current layout group
     * @return layout item
     */
    private LayoutItem getCurrentItem(SelectedLayoutItems selectedLayoutItems, LayoutGroup group) {
        // Current layout item
        LayoutItem currentItem = null;

        if (MapUtils.isNotEmpty(selectedLayoutItems.getSelection())) {
            String currentItemId = selectedLayoutItems.getSelection().get(group.getId());
            if (StringUtils.isNotEmpty(currentItemId)) {
                Iterator<? extends LayoutItem> iterator = group.getItems().iterator();
                while ((currentItem == null)) {
                    LayoutItem item = iterator.next();
                    if (StringUtils.equals(currentItemId, item.getId())) {
                        currentItem = item;
                    }
                }
            }
        }

        if (currentItem == null) {
            currentItem = group.getItems().get(0);
        }

        return currentItem;
    }


    /**
     * Get related layout group.
     *
     * @param groups layout groups
     * @param id     layout item identifier
     * @return layout group
     */
    private LayoutGroupImpl getRelatedGroup(List<LayoutGroupImpl> groups, String id) {
        LayoutGroupImpl relatedGroup = null;
        Iterator<LayoutGroupImpl> groupsIterator = groups.iterator();
        while ((relatedGroup == null) && groupsIterator.hasNext()) {
            LayoutGroupImpl group = groupsIterator.next();
            if (CollectionUtils.isNotEmpty(group.getItems())) {
                Iterator<LayoutItem> itemsIterator = group.getItems().iterator();
                while ((relatedGroup == null) && itemsIterator.hasNext()) {
                    LayoutItem item = itemsIterator.next();
                    if (StringUtils.equals(id, item.getId())) {
                        relatedGroup = group;
                    }
                }
            }
        }

        return relatedGroup;
    }

}
