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
 */
package org.osivia.portal.core.dynamic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsPageState;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.pagemarker.PortalCommandFactory;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;

/**
 * Start dynamic window command.
 *
 * @see DynamicCommand
 */
public class StartDynamicWindowCommand extends DynamicCommand {

    /** Command info. */
    private static final CommandInfo info = new ActionCommandInfo(false);

    /** CMS service locator. */
    private static ICMSServiceLocator cmsServiceLocator;


    /** Page identifier. */
    private String pageId;
    /** Region identifier. */
    private String regionId;
    /** Instance identifier. */
    private String instanceId;
    /** Window name. */
    private String windowName;
    /** Dynamic properties. */
    private Map<String, String> dynaProps;
    /** Parameters. */
    private Map<String, String> params;
    /** Add to breadcrumb indicator. */
    private String addTobreadcrumb;
    /** Edition state. */
    private EditionState editionState;




    /**
     * Constructor.
     */
    public StartDynamicWindowCommand() {
    }


    /**
     * Constructor.
     *
     * @param pageId page identifier
     * @param regionId region identifier
     * @param portletInstance portlet instance
     * @param windowName window name
     * @param props dynamic properties
     * @param params parameters
     * @param addTobreadcrumb add to breadcrumb indicator
     * @param editionState edition state
     */
    public StartDynamicWindowCommand(String pageId, String regionId, String portletInstance, String windowName, Map<String, String> props,
            Map<String, String> params, String addTobreadcrumb, EditionState editionState) {
        this.pageId = pageId;
        this.regionId = regionId;
        this.instanceId = portletInstance;
        this.windowName = windowName;
        this.dynaProps = props;
        this.params = params;
        this.addTobreadcrumb = addTobreadcrumb;
        this.editionState = editionState;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return info;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        try {
            // Récupération page
            PortalObjectId poid = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
            Page page = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

            if (page == null) {
                // La page dynamique n'existe plus
                // Redirection vers la page par défaut du portail
                Portal portal = this.getControllerContext().getController().getPortalObjectContainer().getContext().getDefaultPortal();
                return new UpdatePageResponse(portal.getDefaultPage().getId());
            }

            IDynamicObjectContainer dynamicCOntainer = Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer");

            Map<String, String> properties = new HashMap<String, String>();
            properties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
            properties.put(ThemeConstants.PORTAL_PROP_REGION, this.regionId);

            for (String dynaKey : this.dynaProps.keySet()) {
                properties.put(dynaKey, this.dynaProps.get(dynaKey));
            }

            properties.put("osisia.dynamicStarted", "1");

            // First popup item
            if ("popup".equals(this.regionId)) {
                properties.put("osivia.dynamic.disable.close", "1");
            }


            if (BooleanUtils.toBoolean(this.dynaProps.get("osivia.navigation.reset"))) {
                // Reset CMS navigation
                this.resetNavigation(page);
            }


            PageMarkerInfo markerInfo = PageMarkerUtils.getLastPageState(this.getControllerContext());

            if (markerInfo != null) {
                String backUrl = null;

                if ("1".equals(this.addTobreadcrumb)) {
                    ViewPageCommand pageCmd = new ViewPageCommand(markerInfo.getPageId());
                    PortalURL url = new PortalURLImpl(pageCmd, this.getControllerContext(), null, null);
                    backUrl = url.toString();
                } else {
                    // Mode non contextualisé

                    // Use case : menu > maximized puis a nouvean menu > maximized
                    // le close revient sur l'accueil
                    ViewPageCommand pageCmd = new ViewPageCommand(markerInfo.getPageId());

                    PortalURL url = new PortalURLImpl(pageCmd, this.getControllerContext(), null, null);

                    backUrl = url.toString();
                    backUrl += "?unsetMaxMode=true";
                }

                // Add REFRESH URL
                if ("1".equals(this.dynaProps.get("osivia.close.refreshPage"))) {
                    int insertIndex = backUrl.indexOf(PageMarkerUtils.PAGE_MARKER_PATH);
                    if (insertIndex == -1) {
                        // Web command
                        insertIndex = backUrl.indexOf("/web/");
                    }

                    if (insertIndex != -1) {
                        backUrl = backUrl.substring(0, insertIndex) + PortalCommandFactory.REFRESH_PATH + backUrl.substring(insertIndex + 1);
                    }
                }

                if (backUrl.indexOf("/pagemarker/") != -1) {
                    String pageMarker = markerInfo.getPageMarker();
                    backUrl = backUrl.replaceAll("/pagemarker/([0-9]*)/", "/pagemarker/" + pageMarker + "/");
                }

                properties.put("osivia.dynamic.close_url", backUrl);


                if (BooleanUtils.toBoolean(this.dynaProps.get("osivia.back.reset"))) {
                    // Back button reset
                    PageCustomizerInterceptor.initPageBackInfos(this.getControllerContext());
                } else {
                    // Back button : exclude cms link and popup
                    if (!"1".equals(properties.get("osivia.dynamic.disable.close"))) {
                        String backPageMarker = (String) this.getControllerContext().getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");
                        this.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backPageMarker", backPageMarker);
                        this.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backMobilePageMarker", backPageMarker);
                    }
                }
            }


            InstanceDefinition instance = this.getControllerContext().getController().getInstanceContainer().getDefinition(this.instanceId);
            if (instance == null) {
                throw new ControllerException("Instance not defined");
            }

            for (PortalObject po : page.getChildren(PortalObject.WINDOW_MASK)) {
                Window child = (Window) po;
                NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, child.getId());

                WindowNavigationalState ws = (WindowNavigationalState) this.getControllerContext().getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE,
                        nsKey);

                if (ws != null) {
                    WindowNavigationalState newNS = new WindowNavigationalState(WindowState.NORMAL, ws.getMode(), ws.getContentState(),
                            ws.getPublicContentState());
                    this.getControllerContext().setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
                }
            }

            String controlledPageMarker = (String) this.getControllerContext().getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");

            PortalObjectId windowId = new PortalObjectId("", new PortalObjectPath(page.getId().getPath().toString().concat("/").concat(this.windowName),
                    PortalObjectPath.CANONICAL_FORMAT));

            // Création de la nouvelle fenêtre
            dynamicCOntainer.addDynamicWindow(new DynamicWindowBean(page.getId(), this.windowName, this.instanceId, properties, controlledPageMarker));


            // TODO : SESSIONDYNA A reactiver pour synchroniser les sessions
            // getControllerContext().setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.session.refresh."+vindowId, "1");


            // Pour forcer le rechargement de la page, on supprime l'ancien windowState pour etre sur qu'elle est considérée comme modifiée
            this.getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, windowId.toString());

            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, windowId);
            WindowNavigationalState windowNavState = WindowNavigationalState.create();

            Map<String, String[]> parameters = new HashMap<String, String[]>();
            for (String keyParam : this.params.keySet()) {
                parameters.put(keyParam, new String[]{this.params.get(keyParam)});
            }

            if (!StringUtils.equals(this.dynaProps.get("osivia.windowState"), "normal")) {
                // On force la maximisation
                WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.MAXIMIZED, windowNavState.getMode(),
                        ParametersStateString.create(parameters));

                this.getControllerContext().setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
            }

            // Suppression du cache
            this.getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, "cached_markup." + windowId.toString());

            ContributionService.initWindowEditionStates(this.getControllerContext(), windowId);

            if (this.editionState != null) {
                ContributionService.setWindowEditionState(this.getControllerContext(), windowId, this.editionState);
            }

            // Maj du breadcrumb
            Breadcrumb breadcrumb = (Breadcrumb) this.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb");

            if (breadcrumb == null) {
                breadcrumb = new Breadcrumb();
            }

            if (!"1".equals(this.addTobreadcrumb)) {
                breadcrumb.getChilds().clear();
            }

            // Ajout du nouvel item
            PageURL url = new PageURL(page.getId(), this.getControllerContext());

            String name = properties.get("osivia.title");
            if (name == null) {
                name = instance.getDisplayName().getDefaultString();
            }
            BreadcrumbItem item = new BreadcrumbItem(name, url.toString(), windowId, false);

            if ("navigationPlayer".equals(this.addTobreadcrumb)) {
                item.setNavigationPlayer(true);
            }

            // Task identifier
            if (this.dynaProps != null) {
                String taskId = this.dynaProps.get(ITaskbarService.TASK_ID_WINDOW_PROPERTY);
                item.setTaskId(taskId);
            }

            breadcrumb.getChilds().add(item);

            this.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb", breadcrumb);

            return new UpdatePageResponse(page.getId());
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }


    private void resetNavigation(Page page) throws CMSException, ControllerException {
        // Base path
        String basePath = page.getProperty("osivia.cms.basePath");

        if (basePath != null) {
            // CMS service
            ICMSService cmsService = this.getCMSService();
            // CMS context
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setControllerContext(this.context);
            // Navigation item
            CMSItem navItem = cmsService.getPortalNavigationItem(cmsContext, basePath, basePath);

            CMSPublicationInfos pubInfos = cmsService.getPublicationInfos(cmsContext, basePath);

            Page parent = (Page) page.getParent();


            CmsPageState pageState = new CmsPageState(this.getControllerContext(), parent, null, IPortalUrlFactory.CONTEXTUALIZATION_PAGE, navItem, basePath,
                    basePath, page,
                    null, basePath, pubInfos, true, basePath, null, false);
            pageState.initState();
        }
    }


    /**
     * Get CMS service.
     *
     * @return CMS service
     */
    private ICMSService getCMSService() {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        }
        return cmsServiceLocator.getCMSService();
    }


    /**
     * Getter for pageId.
     *
     * @return the pageId
     */
    public String getPageId() {
        return this.pageId;
    }

}
