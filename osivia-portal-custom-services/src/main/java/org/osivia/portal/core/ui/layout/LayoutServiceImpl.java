package org.osivia.portal.core.ui.layout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.ui.layout.LayoutItem;
import org.osivia.portal.api.ui.layout.LayoutService;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Layout service implementation.
 *
 * @author Cédric Krommenhoek
 * @see LayoutService
 */
public class LayoutServiceImpl implements LayoutService {

    /**
     * Layout items page property.
     */
    private static final String LAYOUT_ITEMS_PROPERTY = "osivia.layout.items";

    /**
     * Selected layout item identifier session attribute suffix.
     */
    private static final String SELECTED_LAYOUT_ITEM_ATTRIBUTE_SUFFIX = ".selected-layout-item.id";


    /**
     * Log.
     */
    private final Log log;


    /**
     * Constructor.
     */
    public LayoutServiceImpl() {
        super();

        // Log
        this.log = LogFactory.getLog(this.getClass());
    }


    @Override
    public List<LayoutItem> getItems(PortalControllerContext portalControllerContext) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);

        // Layout items
        List<LayoutItem> items;

        if (page == null) {
            items = null;
        } else {
            // Page property
            String property = page.getDeclaredProperty(LAYOUT_ITEMS_PROPERTY);

            // Layout items container
            LayoutItemsContainer container;

            if (StringUtils.isEmpty(property)) {
                container = null;
            } else {
                // JSON object mapper
                ObjectMapper mapper = new ObjectMapper();
                try {
                    container = mapper.readValue(property, LayoutItemsContainer.class);
                } catch (JsonProcessingException e) {
                    container = null;
                    this.log.error(e.getLocalizedMessage());
                }
            }

            if ((container == null) || CollectionUtils.isEmpty(container.getItems())) {
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
                items = new ArrayList<>(container.getItems().size());
                for (LayoutItem item : container.getItems()) {
                    if (CollectionUtils.isEmpty(item.getProfiles()) || (CollectionUtils.isNotEmpty(profiles) && CollectionUtils.containsAny(profiles, item.getProfiles()))) {
                        items.add(item);
                    }
                }
            }
        }

        return items;
    }


    @Override
    public void setItems(PortalControllerContext portalControllerContext, List<LayoutItem> items) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);

        if (page != null) {
            // Page property
            String property;

            if (CollectionUtils.isEmpty(items)) {
                property = null;
            } else {
                // Container
                LayoutItemsContainer container = new LayoutItemsContainer();
                List<LayoutItemImpl> containerItems = new ArrayList<>(items.size());
                for (LayoutItem item : items) {
                    if (item instanceof LayoutItemImpl) {
                        containerItems.add((LayoutItemImpl) item);
                    }
                }
                container.setItems(containerItems);

                // JSON object mapper
                ObjectMapper mapper = new ObjectMapper();
                try {
                    property = mapper.writeValueAsString(container);
                } catch (JsonProcessingException e) {
                    property = null;
                    this.log.error(e.getLocalizedMessage());
                }
            }

            page.setDeclaredProperty(LAYOUT_ITEMS_PROPERTY, property);
        }
    }


    @Override
    public LayoutItem getCurrentItem(PortalControllerContext portalControllerContext) {
        // Layout items
        List<LayoutItem> items = this.getItems(portalControllerContext);

        // Current layout item
        LayoutItem currentItem = null;

        if (CollectionUtils.isNotEmpty(items)) {
            // Current page
            Page page = this.getCurrentPage(portalControllerContext);

            // Selected layout item identifier
            String selectedItemId;
            if (page == null) {
                selectedItemId = null;
            } else {
                // HTTP session
                HttpSession session = this.getSession(portalControllerContext);

                selectedItemId = (String) session.getAttribute(page.getId().toString(PortalObjectPath.SAFEST_FORMAT) + SELECTED_LAYOUT_ITEM_ATTRIBUTE_SUFFIX);
            }

            if (StringUtils.isNotEmpty(selectedItemId)) {
                Iterator<LayoutItem> iterator = items.iterator();
                while ((currentItem == null) && iterator.hasNext()) {
                    LayoutItem item = iterator.next();
                    if (StringUtils.equals(selectedItemId, item.getId())) {
                        currentItem = item;
                    }
                }
            }

            if (currentItem == null) {
                currentItem = items.get(0);
            }
        }

        return currentItem;
    }


    @Override
    public void selectItem(PortalControllerContext portalControllerContext, String id) {
        // Current page
        Page page = this.getCurrentPage(portalControllerContext);

        if (page != null) {
            // HTTP session
            HttpSession session = this.getSession(portalControllerContext);

            session.setAttribute(page.getId().toString(PortalObjectPath.SAFEST_FORMAT) + SELECTED_LAYOUT_ITEM_ATTRIBUTE_SUFFIX, id);
        }
    }


    @Override
    public LayoutItem createItem(PortalControllerContext portalControllerContext, String id) {
        // Layout item
        LayoutItemImpl item = new LayoutItemImpl();
        // Identifier
        item.setId(id);
        // Profiles
        ArrayList<String> profiles = new ArrayList<>();
        item.setProfiles(profiles);

        return item;
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

}
