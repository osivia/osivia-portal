package org.osivia.portal.core.theming.attributesbundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.server.request.URLContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.search.AdvancedSearchCommand;

/**
 * Search attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class SearchAttributesBundle implements IAttributesBundle {

    /** Singleton instance. */
    private static SearchAttributesBundle instance;


    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;

    /** Toolbar attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private SearchAttributesBundle() {
        super();

        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");

        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_SEARCH_URL);
        this.names.add(Constants.ATTR_ADVANCED_SEARCH_URL);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static SearchAttributesBundle getInstance() {
        if (instance == null) {
            instance = new SearchAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        // URL context
        URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
        // Portal identifier
        String portalId = renderPageCommand.getPortal().getId().toString();
        // Current page identifier
        String pageId = PortalObjectUtils.getHTMLSafeId(renderPageCommand.getPage().getId());

        // Properties
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("osivia.cms.uri", "/");

        // Parameters
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("osivia.keywords", "__REPLACE_KEYWORDS__");

        try {
            // v1.0.13 : always open the same page
            String searchUrl = this.urlFactory
                    .getStartPageUrl(portalControllerContext, portalId, "search", "/default/templates/search", properties,
                    parameters);
            attributes.put(Constants.ATTR_SEARCH_URL, searchUrl);
        } catch (Exception e) {
            throw new ControllerException(e);
        }

        // Advanced search command URL
        AdvancedSearchCommand advancedSearchCommand = new AdvancedSearchCommand(pageId, StringUtils.EMPTY, true);
        String advancedSearchCommandUrl = controllerContext.renderURL(advancedSearchCommand, urlContext, null);
        attributes.put(Constants.ATTR_ADVANCED_SEARCH_URL, advancedSearchCommandUrl);
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
