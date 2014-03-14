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
package org.osivia.portal.core.page;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.aspects.controller.node.Navigation;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.impl.api.node.PortalNodeImpl;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Tabs customizer interceptor.
 *
 * @see ControllerInterceptor
 */
public class TabsCustomizerInterceptor extends ControllerInterceptor {

    /** Portal authorization manager factory. */
    private PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;


    /**
     * Default constructor.
     */
    public TabsCustomizerInterceptor() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse invoke(ControllerCommand controllerCommand) throws Exception {
        ControllerResponse controllerResponse;
        controllerResponse = (ControllerResponse) controllerCommand.invokeNext();

        if ((controllerCommand instanceof RenderPageCommand) && (controllerResponse instanceof PageRendition)) {
            RenderPageCommand renderPageCommand = (RenderPageCommand) controllerCommand;
            PageRendition pageRendition = (PageRendition) controllerResponse;

            // Current portal
            Portal portal = renderPageCommand.getPortal();

            if (PortalObjectUtils.isJBossPortalAdministration(portal)) {
                // Inject JBoss administration headers
                this.injectAdminHeaders(renderPageCommand, pageRendition);
            }
        }

        return controllerResponse;
    }


    /**
     * Utility method used to inject JBoss administration headers.
     *
     * @param renderPageCommand render page command
     * @param pageRendition page rendition
     */
    private void injectAdminHeaders(RenderPageCommand renderPageCommand, PageRendition pageRendition) {
        String tabbedNav = this.injectAdminTabbedNav(renderPageCommand);
        if (tabbedNav != null) {
            Map<String, String> windowProps = new HashMap<String, String>();
            windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
            windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
            WindowResult res = new WindowResult("", tabbedNav, Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
            WindowContext blah = new WindowContext("BLAH", "navigation", "0", res);
            pageRendition.getPageResult().addWindowContext(blah);

            Region region = pageRendition.getPageResult().getRegion2("navigation");
            DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
        }
    }


    /**
     * Utility method used to inject JBoss administration tabbed nav.
     *
     * @param renderPageCommand render page command
     * @return tabbed nav content
     */
    private String injectAdminTabbedNav(RenderPageCommand renderPageCommand) {
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        ControllerRequestDispatcher dispatcher = controllerContext.getRequestDispatcher(PageCustomizerInterceptor.getTargetContextPath(renderPageCommand),
                "/WEB-INF/jsp/header/tabs.jsp");

        if (dispatcher != null) {
            Page page = renderPageCommand.getPage();
            PortalAuthorizationManager pam = this.portalAuthorizationManagerFactory.getManager();
            PortalNodeImpl node = new PortalNodeImpl(pam, page);

            dispatcher.setAttribute("org.jboss.portal.api.PORTAL_NODE", node);
            dispatcher.setAttribute("org.jboss.portal.api.PORTAL_RUNTIME_CONTEXT", Navigation.getPortalRuntimeContext());

            dispatcher.include();
            return dispatcher.getMarkup();
        }

        return null;
    }


    /**
     * Getter for portalAuthorizationManagerFactory.
     *
     * @return the portalAuthorizationManagerFactory
     */
    public PortalAuthorizationManagerFactory getPortalAuthorizationManagerFactory() {
        return this.portalAuthorizationManagerFactory;
    }

    /**
     * Setter for portalAuthorizationManagerFactory.
     *
     * @param portalAuthorizationManagerFactory the portalAuthorizationManagerFactory to set
     */
    public void setPortalAuthorizationManagerFactory(PortalAuthorizationManagerFactory portalAuthorizationManagerFactory) {
        this.portalAuthorizationManagerFactory = portalAuthorizationManagerFactory;
    }

}
