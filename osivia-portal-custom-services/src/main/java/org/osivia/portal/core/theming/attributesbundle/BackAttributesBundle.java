package org.osivia.portal.core.theming.attributesbundle;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;

/**
 * "Back" function attributes bundle.
 * 
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public class BackAttributesBundle implements IAttributesBundle {

    /** "Back" function URL request attribute name. */
    private static final String BACK_URL_ATTRIBUTE = "osivia.back.url";

    /** Singleton instance. */
    private static BackAttributesBundle instance;


    /** Attribute names. */
    private final Set<String> names;

    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;


    /**
     * Private constructor.
     */
    private BackAttributesBundle() {
        super();

        // URL Factory
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");

        // Attribute names
        this.names = new TreeSet<String>();
        this.names.add(BACK_URL_ATTRIBUTE);
    }


    /**
     * Get singleton instance.
     * 
     * @return singleton instance
     */
    public static BackAttributesBundle getInstance() {
        if (instance == null) {
            instance = new BackAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();


        String backPageMarker = (String) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backPageMarker");


        if (backPageMarker != null) {
            String backUrl = urlFactory.getBackUrl(new PortalControllerContext(controllerContext));
            attributes.put(BACK_URL_ATTRIBUTE, backUrl);
        }


    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
