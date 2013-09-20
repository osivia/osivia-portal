package org.osivia.portal.core.theming.attributesbundle;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.constants.InternalConstants;

/**
 * Transversal attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class TransversalAttributesBundle implements IAttributesBundle {

    /** Singleton instance. */
    private static TransversalAttributesBundle instance;


    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;

    /** Toolbar attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private TransversalAttributesBundle() {
        super();

        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");

        this.names = new TreeSet<String>();
        this.names.add(InternalConstants.ATTR_TOOLBAR_SETTINGS_CONTROLLER_CONTEXT);
        this.names.add(Constants.ATTR_PORTAL_CTX);
        this.names.add(Constants.ATTR_URL_FACTORY);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static TransversalAttributesBundle getInstance() {
        if (instance == null) {
            instance = new TransversalAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        attributes.put(InternalConstants.ATTR_TOOLBAR_SETTINGS_CONTROLLER_CONTEXT, controllerContext);
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
        attributes.put(Constants.ATTR_PORTAL_CTX, portalControllerContext);
        // URL factory
        attributes.put(Constants.ATTR_URL_FACTORY, this.urlFactory);
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
