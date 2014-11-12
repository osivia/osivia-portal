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
package org.osivia.portal.core.pagemarker;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.DefaultPortalCommandFactory;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.PortalObjectCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPortalCommand;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSPage;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.RestorablePageUtils;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.TabsCustomizerInterceptor;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.osivia.portal.core.web.WebCommand;


/**
 * 
 * ajout d'un tag /pagemarker dans l'url pour associer à chaque page l'état des
 * portlets
 * 
 * @author jeanseb
 * 
 */
public class PortalCommandFactory extends DefaultPortalCommandFactory {

    protected static final Log logger = LogFactory.getLog(PortalCommandFactory.class);

    public static String POPUP_OPEN_PATH = "/popup_open/";
    public static String POPUP_CLOSE_PATH = "/popup_close/";
    public static String POPUP_CLOSED_PATH = "/popup_closed/";
    public static String POPUP_REFRESH_PATH = "/popup_refresh/";
    public static String REFRESH_PATH = "/refresh/";

    public IDynamicObjectContainer dynamicCOntainer;
    public PortalObjectContainer portalObjectContainer;


    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }

    public IDynamicObjectContainer getDynamicContainer() {

        if (this.dynamicCOntainer == null) {
            this.dynamicCOntainer = Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer");
        }

        return this.dynamicCOntainer;
    }

    public PortalObjectContainer getPortalObjectContainer() {

        if (this.portalObjectContainer == null) {
            this.portalObjectContainer = Locator.findMBean(PortalObjectContainer.class, "portal:container=PortalObject");
        }

        return this.portalObjectContainer;
    }

    private void createPages(ControllerContext controllerContext, List<CMSPage> preloadedPages) {

        CMSServiceCtx userCtx = new CMSServiceCtx();
        userCtx.setControllerContext(controllerContext);

        int order = DynamicPageBean.DYNAMIC_PRELOADEDPAGES_FIRST_ORDER;

        for (CMSPage page : preloadedPages) {

            CMSItem publishSpace = page.getPublishSpace();

            PortalObject parent;

            try {


                String parentPath = page.getParentPath();
                if (parentPath != null) {

                    PortalObjectId poid = PortalObjectId.parse(parentPath, PortalObjectPath.CANONICAL_FORMAT);
                    parent = this.getPortalObjectContainer().getObject(poid);
                } else {
                    parent = this.getPortalObjectContainer().getContext().getDefaultPortal();
                }

                Map displayNames = new HashMap();
                displayNames.put(Locale.FRENCH, publishSpace.getProperties().get("displayName"));


                /* Ajout nom domaine */

                String pubDomain = TabsCustomizerInterceptor.getDomain(publishSpace.getPath());

                if (pubDomain != null) {
                    CMSItem domain = getCMSService().getContent(userCtx, "/" + pubDomain);
                    if (domain != null) {
                        displayNames.put(Locale.FRENCH, domain.getProperties().get("displayName"));
                    }
                }

                Map<String, String> props = new HashMap<String, String>();

                String pageName = "portalSite"
                        + (new CMSObjectPath(publishSpace.getPath(), CMSObjectPath.CANONICAL_FORMAT)).toString(CMSObjectPath.SAFEST_FORMAT);
                
                

                props.put("osivia.cms.basePath", publishSpace.getPath());

                // v2.0-rc7

                // if ("1".equals(publishSpace.getProperties().get("contextualizeInternalContents")))
                // props.put("osivia.cms.pageContextualizationSupport", "1");
                //
                // if ("1".equals(publishSpace.getProperties().get("contextualizeExternalContents")))
                // props.put("osivia.cms.outgoingRecontextualizationSupport", "1");

                props.put("osivia.cms.layoutType", "1");
                props.put("osivia.cms.layoutRules", "return ECMPageTemplate;");
                
                String restorablePageName = RestorablePageUtils.createRestorableName(controllerContext, pageName, PortalObjectId.parse("/default/templates/publish",PortalObjectPath.CANONICAL_FORMAT).toString( PortalObjectPath.CANONICAL_FORMAT), publishSpace.getPath(), displayNames, props, new HashMap<String, String>());


                DynamicPageBean dynaPage = new DynamicPageBean(parent, restorablePageName, pageName, displayNames, PortalObjectId.parse("/default/templates/publish",
                        PortalObjectPath.CANONICAL_FORMAT), props);
                dynaPage.setOrder(order);

                dynaPage.setClosable(false);

                this.getDynamicContainer().addDynamicPage(dynaPage);


            } catch (Exception e) {

                String cmsDebugPath = "";

                if (page.getPublishSpace() != null) {
                    cmsDebugPath = page.getPublishSpace().getPath();
                }


                // Don't block login
                this.log.error("Can't preload user cms page " + cmsDebugPath);
            }


            order++;
        }

    }

    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {

        String path = requestPath;
        boolean popupClosed = false;
        boolean popupOpened = false;
        boolean closePopup = false;


        // 2.1 : is popup already closed (by javascript)
        if (requestPath.startsWith(POPUP_CLOSED_PATH)) {
            path = requestPath.substring(POPUP_CLOSED_PATH.length() - 1);
            popupClosed = true;
        }

        if (requestPath.startsWith(POPUP_REFRESH_PATH)) {
            // Fait pour utiliser le mode refresh (portlet + CMS) pour un portlet
            // NON VALIDE car mode AJAX non activé en CMS
            path = requestPath.substring(POPUP_REFRESH_PATH.length() - 1);
            PageProperties.getProperties().setRefreshingPage(true);
        }
        
        
        if (requestPath.startsWith(REFRESH_PATH)) {
            // Fait pour utiliser le mode refresh (portlet + CMS) pour un portlet
            // NON VALIDE car mode AJAX non activé en CMS
            path = requestPath.substring(REFRESH_PATH.length() - 1);
            PageProperties.getProperties().setRefreshingPage(true);
        }

        if (requestPath.startsWith(POPUP_OPEN_PATH)) {
            path = requestPath.substring(POPUP_OPEN_PATH.length() - 1);
            popupOpened = true;
        }
        if (requestPath.startsWith(POPUP_CLOSE_PATH)) {
            path = requestPath.substring(POPUP_CLOSE_PATH.length() - 1);
            closePopup = true;
        }

        String newPath = PageMarkerUtils.restorePageState(controllerContext, path);


        if (popupClosed) {
            controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupModeClosed", "1");
            controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupModeClosedWindowID", "1");
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", null);
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID", null);
        }

        if (closePopup) {
            // controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeClosing", "1");
            controllerContext.getServerInvocation().getServerContext().getClientRequest().setAttribute("osivia.saveClosingAction", "1");
            controllerContext.getServerInvocation().getServerContext().getClientRequest().setAttribute("osivia.popupModeClosing", "1");
        }

        /*
         * Synchronisation des pages préchargées
         * 
         * A faire après le restorePageState
         */

        List<CMSPage> preloadedPages = (List<CMSPage>) invocation.getAttribute(Scope.REQUEST_SCOPE, "osivia.userPreloadedPages");

        if (preloadedPages != null) {
            this.createPages(controllerContext, preloadedPages);
        }

        ControllerCommand cmd = super.doMapping(controllerContext, invocation, host, contextPath, newPath);
        
        
    
        /* Restauration of pages in case of loose of sessions */
        
        boolean enableRestoredPages = "1".equals(System.getProperty("osivia.url.enableRestoredPages"));
        
        if( enableRestoredPages){
        
            if (cmd instanceof ViewPortalCommand || cmd instanceof ViewPageCommand) {

                if (!StringUtils.isEmpty(newPath)) {

                    PortalObjectId targetIdObject = ((PortalObjectCommand) cmd).getTargetId();

                    PortalObjectPath poPath = PortalObjectPath.parse(newPath, PortalObjectPath.CANONICAL_FORMAT);
                    int pathLenth = poPath.getLength();

                    int targetLength = targetIdObject.getPath().getLength();

                    // one part of url has been missed, might bu due to a lose of session
                    // (_CMS_LAYOUT, dynamic templated page, ...)
                    // We try to restore from url

                    if (pathLenth > targetLength + 1) {

                        String pagePath = poPath.getName(2);

                        // Dynamic page creation : session may have been lost

                        if (RestorablePageUtils.isRestorable(pagePath)) {

                            PortalObjectId portalId = null;
                            if (cmd instanceof ViewPortalCommand)
                                portalId = targetIdObject;
                            else {
                                PortalObjectPath parentPath = targetIdObject.getPath().getParent();
                                portalId = new PortalObjectId("", parentPath);
                            }

                            // Restore the page
                            RestorablePageUtils.restore(controllerContext, portalId, pagePath);
                            cmd = super.doMapping(controllerContext, invocation, host, contextPath, newPath);
                        }
                    }
                }

            }
        }
        
        
        if( cmd instanceof CmsCommand){
            // Le mode CMS déseactive les popup
            // Corrige le bug du permlink alors qu'une popup est ouverte
            if(popupOpened == false && closePopup == false){
                if( controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID") != null)  {
                    controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", null);            
                    controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID", null);        
                }
            }
        }
        
        if( cmd instanceof InvokePortletWindowRenderCommand)    {
            StateString navigationalState = ((InvokePortletWindowRenderCommand) cmd).getNavigationalState();
            if (navigationalState instanceof ParametersStateString)
            {
               Map<String, String[]> params = ((ParametersStateString)navigationalState).getParameters();
               String editionPath[] = params.get(Constants.PORTLET_PARAM_EDITION_PATH);
               if( editionPath != null && editionPath.length > 0){
                   ContributionService.setWindowEditionState(controllerContext, ((InvokePortletWindowRenderCommand) cmd).getTargetId(), new EditionState(EditionState.CONTRIBUTION_MODE_EDITION, editionPath[0]));
                }
            }

        }


        if (popupOpened && (cmd instanceof PortalObjectCommand)) {
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", "command");
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID", ((PortalObjectCommand) cmd).getTargetId());
        }

        if (popupOpened && (cmd instanceof WebCommand)) {
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", "command");
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID", ((WebCommand) cmd).getWindowId(controllerContext));
        }


        if (popupOpened && (cmd instanceof StartDynamicWindowCommand)) {
            // Calcul du popupWindowID
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", "command");
            PortalObjectPath pagePath = PortalObjectPath.parse(((StartDynamicWindowCommand) cmd).getPageId(), PortalObjectPath.SAFEST_FORMAT);
            String windowPath = pagePath.toString(PortalObjectPath.CANONICAL_FORMAT) + "/popupWindow";
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID",
                    new PortalObjectId("", PortalObjectPath.parse(windowPath, PortalObjectPath.CANONICAL_FORMAT)));
        }

        if (closePopup) {
            // On memorise la commande qui sera executée au retour
            // TODO : en plus , transformer la commande et render minimaliste du popup (juste un view)
            controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupModeCloseCmd", cmd);
        }


        return cmd;

    }

}
