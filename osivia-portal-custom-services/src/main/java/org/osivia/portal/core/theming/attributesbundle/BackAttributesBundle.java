package org.osivia.portal.core.theming.attributesbundle;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.menubar.MenubarItem;
import org.osivia.portal.api.theming.IAttributesBundle;

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


    /**
     * Private constructor.
     */
    private BackAttributesBundle() {
        super();

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

        // Search "Back" function URL in menubar
        Object menubarAttribute = controllerContext.getAttribute(Scope.REQUEST_SCOPE, Constants.PORTLET_ATTR_MENU_BAR);
        if (menubarAttribute instanceof List<?>) {
            List<?> menubar = (List<?>) menubarAttribute;

            if (CollectionUtils.isNotEmpty(menubar)) {
                for (Object object : menubar) {
                    MenubarItem menubarItem = (MenubarItem) object;
                    if ("BACK".equals(menubarItem.getId())) {
                        String backURL = menubarItem.getUrl();
                        attributes.put(BACK_URL_ATTRIBUTE, backURL);
                        break;
                    }
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
