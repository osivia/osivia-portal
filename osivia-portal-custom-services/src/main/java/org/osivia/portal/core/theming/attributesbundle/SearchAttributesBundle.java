/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.theming.attributesbundle;

import java.util.HashMap;
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
import org.osivia.portal.core.web.IWebIdService;

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
    private final IPortalUrlFactory portalURLFactory;
    /** WebId service. */
    private final IWebIdService webIdService;

    /** Toolbar attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private SearchAttributesBundle() {
        super();

        // Portal URL factory
        this.portalURLFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        // WebId service
        this.webIdService = Locator.findMBean(IWebIdService.class, IWebIdService.MBEAN_NAME);

        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_SEARCH_URL);
        this.names.add(Constants.ATTR_SEARCH_WEB_URL);
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
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
        // Portal identifier
        String portalId = renderPageCommand.getPortal().getId().toString();


        // Properties
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("osivia.cms.uri", "/");

        // Parameters
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("osivia.keywords", "__REPLACE_KEYWORDS__");

        try {
            // v1.0.13 : always open the same page
            String searchUrl = this.portalURLFactory
                    .getStartPageUrl(portalControllerContext, portalId, "search", "/default/templates/search", properties,
                    parameters);
            attributes.put(Constants.ATTR_SEARCH_URL, searchUrl);
        } catch (Exception e) {
            throw new ControllerException(e);
        }

        // Search web URL
        String searchWebURL = this.webIdService.generateCanonicalWebURL(portalControllerContext, null, "search");
        attributes.put(Constants.ATTR_SEARCH_WEB_URL, searchWebURL);
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
