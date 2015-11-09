/******************************************************************************
 * JBoss, a division of Red Hat *
 * Copyright 2006, Red Hat Middleware, LLC, and individual *
 * contributors as indicated by the @authors tag. See the *
 * copyright.txt in the distribution for a full listing of *
 * individual contributors. *
 * *
 * This is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as *
 * published by the Free Software Foundation; either version 2.1 of *
 * the License, or (at your option) any later version. *
 * *
 * This software is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU *
 * Lesser General Public License for more details. *
 * *
 * You should have received a copy of the GNU Lesser General Public *
 * License along with this software; if not, write to the Free *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. *
 ******************************************************************************/
package org.jboss.portal.core.model.portal.command.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.aspects.server.UserInterceptor;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.NoSuchResourceException;
import org.jboss.portal.core.controller.SecurityException;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.info.ViewCommandInfo;
import org.jboss.portal.core.controller.portlet.ControllerPageNavigationalState;
import org.jboss.portal.core.controller.portlet.ControllerPortletControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.response.MarkupResponse;
import org.jboss.portal.core.model.portal.content.WindowRendition;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.core.theme.WindowContextFactory;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.page.PageResult;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.mt.IMultithreadService;
import org.osivia.portal.core.profils.IProfilManager;

/**
 * Render a full page.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 11068 $
 *
 *
 *          Cette classe implémente le multithread dans le rendu des pages
 *          afin de paralléliser les appels de portlets
 */
public final class RenderPageCommand extends PageCommand {

    /** . */
    private static final CommandInfo info = new ViewCommandInfo();

    /** The windows to render. */
    private Collection<PortalObject> windows;

    /** . */
    private boolean personalizable;

    protected static final Log logger = LogFactory.getLog(RenderPageCommand.class);


    public RenderPageCommand(PortalObjectId pageId) {
        super(pageId);
    }

    /**
     * Get the command info (runtime info about the command)
     *
     * @return info about the command
     */
    @Override
    public CommandInfo getInfo() {
        return info;
    }

    /**
     * Returns the modifiable list of windows.
     *
     * @return the windows on the page
     */
    public Collection<PortalObject> getWindows() {
        return this.windows;
    }

    @Override
    public void acquireResources() throws NoSuchResourceException {
        super.acquireResources();

        // All windows on the page
        this.windows = new ArrayList<PortalObject>(this.getPage().getChildren(PortalObject.WINDOW_MASK));
    }


    private IProfilManager profilManager = null;

    protected IProfilManager getProfilManager() throws Exception {
        if (this.profilManager == null) {
            // TODO : optimisation recherc profil manager
            this.profilManager = Locator.findMBean(IProfilManager.class, "osivia:service=ProfilManager");
        }
        return this.profilManager;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Page initPage() {
        return (Page) this.getTarget();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void enforceSecurity(PortalAuthorizationManager pam) throws SecurityException {

        //
        super.enforceSecurity(pam);

        // Check if the user can personalize the page
        // PortalObjectPermission perm = new PortalObjectPermission(page.getId(), PortalObjectPermission.PERSONALIZE_MASK);
        // personalizable = pam.checkPermission(perm);
    }

    /**
     * execute the command
     *
     * @throws InvocationException
     */
    @SuppressWarnings("unchecked")
    @Override
    public ControllerResponse execute() throws ControllerException, InvocationException {
        try {
            PageService pageService = this.context.getController().getPageService();
            ThemeService themeService = pageService.getThemeService();
            LayoutService layoutService = pageService.getLayoutService();

            //
            PortalLayout layout = getLayout(layoutService, this.page);


            // The theme for the page
            PortalTheme theme = null;

            //
            ControllerPortletControllerContext portletControllerContext = new ControllerPortletControllerContext(this.context, this.page);
            ControllerPageNavigationalState pageNavigationalState = portletControllerContext.getStateControllerContext().createPortletPageNavigationalState(
                    true);

            // Determine theme
            if (this.personalizable) {
                ControllerContext controllerCtx = (ControllerContext) this.getContext();
                User user = controllerCtx.getUser();
                if (user != null) {
                    UserProfileModule userProfileModule = null;

                    // MARK: identity code change
                    try {
                        userProfileModule = (UserProfileModule) new InitialContext().lookup("java:portal/UserProfileModule");
                    } catch (NamingException ignore) {
                        // Name is not bound anymore, it could happen during a shutdown, we don't do anything
                    }

                    //

                    // If its possible use cachec user profile to obtain theme
                    Map<?, ?> profile = (Map<?, ?>) this.getContext().getAttribute(ServerInvocation.PRINCIPAL_SCOPE, UserInterceptor.PROFILE_KEY);

                    String themeId = null;

                    if (profile == null) {
                        themeId = (String) userProfileModule.getProperty(user, User.INFO_USER_THEME);
                    } else {
                        themeId = (String) profile.get(User.INFO_USER_THEME);
                    }

                    if (themeId != null) {
                        theme = themeService.getThemeById(themeId);
                    }
                }
            }
            if (theme == null) {
                // If nothing get it from the object properties
                String themeId = this.page.getProperty(ThemeConstants.PORTAL_PROP_THEME);
                theme = themeService.getThemeById(themeId);
            }


            // V2.1 : popup
            if (this.getControllerContext().getAttribute(Scope.PRINCIPAL_SCOPE, "osivia.popupMode") != null) {
                layout = layoutService.getLayoutById("osivia-popup");
                theme = themeService.getThemeById("osivia-popup");
            }


            // TODO : optimiser acces service multi-threads
            InitialContext initialContext = new InitialContext();
            IMultithreadService multithreadService = (IMultithreadService) initialContext.lookup("java:multithread");

            boolean isMultiThreadEnabled = true;
            if ("false".equals(System.getProperty("portlets.enableMultiThread"))) {
                isMultiThreadEnabled = false;
            }

            Set<String> visibleRegions = (Set<String>) this.getControllerContext().getAttribute(Scope.REQUEST_SCOPE,
                    InternalConstants.ATTR_LAYOUT_VISIBLE_REGIONS);
            String visibleRegionsLayoutState = (String) this.getControllerContext().getAttribute(Scope.REQUEST_SCOPE,
                    InternalConstants.ATTR_LAYOUT_VISIBLE_REGIONS_PARSER_STATE);


            // v1.0.25 : affichage conditionnel portlet

            List<Window> filteredWindows = new ArrayList<Window>();


            // filtre sur popup
            if (this.getControllerContext().getAttribute(Scope.PRINCIPAL_SCOPE, "osivia.popupMode") != null) {
                isMultiThreadEnabled = false;
            }

            for (PortalObject window : this.windows) {
                if (window instanceof Window) {
                    boolean addWindow = true;

                    // filtre sur popup
                    if (this.getControllerContext().getAttribute(Scope.PRINCIPAL_SCOPE, "osivia.popupMode") != null) {

                        if (this.getControllerContext().getServerInvocation().getServerContext().getClientRequest().getAttribute("osivia.popupModeClosing") != null) {
                            addWindow = false;
                        }

                        PortalObjectId popupWindowId = (PortalObjectId) (((ControllerContext) this.getContext()).getAttribute(
                                ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID"));
                        if (popupWindowId != null) {
                            if (!window.getId().equals(popupWindowId)) {
                                addWindow = false;
                            }
                        }
                    }


                    // Affichage conditionnel / profil
                    String conditionalScope = window.getProperty("osivia.conditionalScope");
                    if (conditionalScope != null) {
                        addWindow = false;
                        ControllerContext controllerCtx = (ControllerContext) this.getContext();
                        Boolean isAdmin = (Boolean) controllerCtx.getAttribute(Scope.PRINCIPAL_SCOPE, "osivia.isAdmin");
                        if (isAdmin != null) {
                            if (isAdmin) {
                                addWindow = true;
                            }
                        }

                        if (addWindow == false) {
                            addWindow = this.getProfilManager().verifierProfilUtilisateur(conditionalScope);
                        }
                    }

                    // Is this window defined in a visible region ????
                    if (addWindow && (visibleRegions != null)) {
                        String region = window.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION);

                        if (!visibleRegions.contains(region)) {

                            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

                            WindowNavigationalState windowNavState = (WindowNavigationalState) ((ControllerContext) this.getContext()).getAttribute(
                                    ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);
                            // On regarde si la fenêtre est en vue MAXIMIZED

                            if ((windowNavState != null) && WindowState.MAXIMIZED.equals(windowNavState.getWindowState())) {
                                // Current window is maximized
                                // Must be displayed even if its original region is hidden

                            } else {
                                if (StringUtils.equals("virtual", region)) {
                                    // virtual window doesn't exist in normal mode
                                    addWindow = false;
                                } else {
                                    // ACTION : remove or warn

                                    // remove window only in maximized mode
                                    if (StringUtils.equals("maximized", visibleRegionsLayoutState)) {
                                        addWindow = false;
                                    } else if ("1".equals(window.getProperty("osivia.dynamic.cmsEditable"))) {
                                        addWindow = false;
                                    } else if (!StringUtils.equals(window.getName(), InternalConstants.PORTAL_MENUBAR_WINDOW_NAME)) {
                                        // otherwise warn
                                        log.warn("this window seems not to be present in any visible region, should check ... : "
                                                + window.getId().toString(PortalObjectPath.CANONICAL_FORMAT));
                                    }
                                }
                            }
                        }
                    }

                    if (addWindow) {
                        filteredWindows.add((Window) window);
                    }
                }
            }

            if ((multithreadService != null) && isMultiThreadEnabled) {
                ControllerResponse pageResult = multithreadService.execute(this.page, this.context, filteredWindows, layout, theme, pageService,
                        pageNavigationalState);
                return pageResult;
            } else {
                // Call the portlet container to create the markup fragment(s) for each portlet that needs to render itself
                PageResult pageResult = new PageResult(this.getPage().getName(), new HashMap<Object, Object>(this.getPage().getProperties()));

                // The window context factory
                WindowContextFactory wcFactory = new WindowContextFactory(this.context);

                // Render the windows
                for (PortalObject po : filteredWindows) {
                    if (po instanceof Window) {
                        Window window = (Window) po;
                        RenderWindowCommand renderCmd = new RenderWindowCommand(pageNavigationalState, window.getId());

                        //
                        WindowRendition rendition = null;

                        //
                        if (renderCmd != null) {
                            rendition = renderCmd.render(this.context);
                        }

                        // We ignore null result objects
                        if (rendition != null) {
                            // Get the controller response
                            ControllerResponse response = rendition.getControllerResponse();

                            // Null means we skip the window
                            if (response != null) {
                                if (response instanceof MarkupResponse) {
                                    // If this is a markup response we aggregate it
                                    pageResult.addWindowContext(wcFactory.createWindowContext(window, rendition));
                                } else if (response != null) {
                                    // Otherwise we return it
                                    return response;
                                }
                            }
                        }
                    }
                }

                //
                return new PageRendition(layout, theme, pageResult, pageService);
            }
        } catch (Exception e) {
            rethrow(e);
        }

        //
        return null;
    }


    /**
     * Get the portal layout to use for the provided page.
     * <p>
     * The name of the layout to use can be defined as a property in the portal, or the individual page. The page property overwrites the portal property. If no
     * property was set, a default layout with the name "nodesk" is assumed.
     * </p>
     *
     * @param layoutService the layout service that allows access to the layout
     * @param page the page that hosts the markup container to render (the page, region, window,...)
     * @return a <code>PortalLayout</code> for the defined layout name
     */
    public static PortalLayout getLayout(LayoutService layoutService, Page page) {
        String layoutIdString = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        return layoutService.getLayoutById(layoutIdString);
    }

}
