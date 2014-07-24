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

// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


public class StartDynamicWindowCommand extends DynamicCommand {

    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

    @Override
    public CommandInfo getInfo() {
        return info;
    }

    private String pageId;
    private String regionId;
    private String instanceId;
    private String windowName;
    private Map<String, String> dynaProps;
    private Map<String, String> params;
    private String addTobreadcrumb;
    private EditionState editionState;

    public String getPageId() {
        return this.pageId;
    }

    public StartDynamicWindowCommand() {
    }

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

            // close url : lien vers la page dans son état avant le lancement

            boolean computeBackLink = true;


            // Pour les éléments contextualisés, le revenir est géré dans la couche CMS (CMSCommand)
            if ("1".equals(properties.get("osivia.application.close_url")))
                computeBackLink = false;

            // First popup item
            if ("popup".equals(regionId) && "0".equals(this.addTobreadcrumb))
                computeBackLink = false;


            if (computeBackLink) {

                PageMarkerInfo markerInfo = PageMarkerUtils.getLastPageState(this.getControllerContext());

                if (markerInfo != null) {


                    String backUrl = null;

                    if ("1".equals(this.addTobreadcrumb)) {

                        ViewPageCommand pageCmd = new ViewPageCommand(markerInfo.getPageId());

                        PortalURL url = new PortalURLImpl(pageCmd, this.getControllerContext(), null, null);

                        backUrl = url.toString();

                    } else {


                        /* On détermine si on est en mode contextualisé */

                        /*
                         * String cmsNav[] = null;
                         * 
                         * NavigationalStateContext nsContext = (NavigationalStateContext) context
                         * .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
                         * 
                         * PageNavigationalState pns = nsContext.getPageNavigationalState( page.getId().toString(PortalObjectPath.CANONICAL_FORMAT));
                         * 
                         * if( pns != null ) {
                         * cmsNav = pns.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path") );
                         * }
                         * 
                         * 
                         * 
                         * if( cmsNav != null && cmsNav.length > 0) {
                         * // Si contenu contextualisé, renvoi sur le cms
                         * // Pour réinitialiser la page
                         * 
                         * Map<String, String> pageParams = new HashMap<String, String>();
                         * 
                         * IPortalUrlFactory urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
                         * 
                         * backUrl = urlFactory.getCMSUrl(new PortalControllerContext(getControllerContext()), page.getId()
                         * .toString(PortalObjectPath.CANONICAL_FORMAT), cmsNav[0], pageParams, IPortalUrlFactory.CONTEXTUALIZATION_PAGE, null, null, null,null,
                         * null);
                         * 
                         * }
                         * else {
                         */

                        // Mode non contextualisé

                        // Use case : menu > maximized puis a nouvean menu > maximized
                        // le close revient sur l'accueil
                        ViewPageCommand pageCmd = new ViewPageCommand(markerInfo.getPageId());

                        PortalURL url = new PortalURLImpl(pageCmd, this.getControllerContext(), null, null);

                        backUrl = url.toString();
                        backUrl += "?unsetMaxMode=true";
                        // }
                    }

                    if (backUrl.indexOf("/pagemarker/") != -1) {
                        String pageMarker = markerInfo.getPageMarker();
                        backUrl = backUrl.replaceAll("/pagemarker/([0-9]*)/", "/pagemarker/" + pageMarker + "/");
                    }
                    properties.put("osivia.dynamic.close_url", backUrl);
                }

            }


            InstanceDefinition instance = this.getControllerContext().getController().getInstanceContainer().getDefinition(this.instanceId);
            if (instance == null) {
                throw new ControllerException("Instance not defined");
            }

            /*
             * /* On force toutes les windows en mode NORMAL
             */

            /*
             * for (PortalObject po :
             * page.getChildren(PortalObject.WINDOW_MASK)) { Window child =
             * (Window) po; NavigationalStateKey nsKey = new
             * NavigationalStateKey(WindowNavigationalState.class,
             * child.getId()); WindowNavigationalState windowNavState =
             * WindowNavigationalState.create();
             * 
             * // On force la mise en mode normal WindowNavigationalState newNS
             * = WindowNavigationalState.bilto(windowNavState,
             * WindowState.NORMAL, windowNavState.getMode(),
             * windowNavState.getContentState());
             * 
             * 
             * getControllerContext().setAttribute(ControllerCommand.
             * NAVIGATIONAL_STATE_SCOPE, nsKey, newNS); }
             */


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


            PortalObjectId vindowId = new PortalObjectId("", new PortalObjectPath(page.getId().getPath().toString().concat("/").concat(this.windowName),
                    PortalObjectPath.CANONICAL_FORMAT));


            /* Création de la nouvelle fenêtre */
            dynamicCOntainer.addDynamicWindow(new DynamicWindowBean(page.getId(), this.windowName, this.instanceId, properties, controlledPageMarker));


            // TODO : SESSIONDYNA A reactiver pour synchroniser les sessions
            // getControllerContext().setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.session.refresh."+vindowId, "1");


            // Pour forcer le rechargement de la page, on supprime l'ancien
            // windowState
            // pour etre sur qu'elle est considérée comme modifiée
            this.getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, vindowId.toString());

            NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, vindowId);
            WindowNavigationalState windowNavState = WindowNavigationalState.create();

            Map<String, String[]> parameters = new HashMap<String, String[]>();
            for (String keyParam : this.params.keySet()) {
                parameters.put(keyParam, new String[]{this.params.get(keyParam)});
            }


            // On force la maximisation
            WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.MAXIMIZED, windowNavState.getMode(),
                    ParametersStateString.create(parameters));

            this.getControllerContext().setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);

            // Mise à jour des public parameters
            /*
             * AttributeResolver resolver =
             * getControllerContext().getAttributeResolver
             * (NAVIGATIONAL_STATE_SCOPE); ParametersStateString params =
             * ParametersStateString.create(); newNS.setPublicState(resolver,
             * nsKey, params);
             */

            // Suppression du cache
            this.getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, "cached_markup." + vindowId.toString());


            ContributionService.initWindowEditionStates(this.getControllerContext(), vindowId);


            if (this.editionState != null) {
                ContributionService.setWindowEditionState(this.getControllerContext(), vindowId, this.editionState);
            }

            // Maj du breadcrumb
            Breadcrumb breadcrumb = (Breadcrumb) this.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb");

            if (breadcrumb == null) {
                breadcrumb = new Breadcrumb();
            }

            if (!"1".equals(this.addTobreadcrumb)) {
                breadcrumb.getChilds().clear();
            }

            /* ajout du nouvel item */

            PageURL url = new PageURL(page.getId(), this.getControllerContext());

            String name = properties.get("osivia.title");
            if (name == null) {
                name = instance.getDisplayName().getDefaultString();
            }
            BreadcrumbItem item = new BreadcrumbItem(name, url.toString(), vindowId, false);

            if ("navigationPlayer".equals(this.addTobreadcrumb)) {
                item.setNavigationPlayer(true);
            }

            breadcrumb.getChilds().add(item);

            this.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb", breadcrumb);

            return new UpdatePageResponse(page.getId());

        } catch (Exception e) {
            throw new ControllerException(e);
        }

    }

}
