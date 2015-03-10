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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.DefaultPortalCommandFactory;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.PortalObjectCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPortalCommand;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.windows.PortalWindow;
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
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.page.TabsCustomizerInterceptor;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.osivia.portal.core.tracker.RequestContextUtil;
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

                String restorablePageName = RestorablePageUtils.createRestorableName(controllerContext, pageName, PortalObjectId.parse("/default/templates/publish",PortalObjectPath.CANONICAL_FORMAT).toString( PortalObjectPath.CANONICAL_FORMAT), publishSpace.getPath(), null, null, null, null );


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

        RequestContextUtil.setControllerContext(controllerContext);


        
        
        
        
        // 2.1 : is popup already closed (by javascript)
        if (requestPath.startsWith(POPUP_CLOSED_PATH)) {
            // Remove notifications from the close phase (displayed twice in case of CMS deny exception)
            // The cause: the close associated command is executed twice (during the close and the closed phase)
            PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
            NotificationsUtils.getNotificationsService().getNotificationsList(portalControllerContext).clear();
            
            
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
            path = requestPath.substring(REFRESH_PATH.length() - 1);
            PageProperties.getProperties().setRefreshingPage(true);
        }

        if (requestPath.startsWith(POPUP_OPEN_PATH)) {
            path = requestPath.substring(POPUP_OPEN_PATH.length() - 1);
            popupOpened = true;
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeOriginalPageID",    (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_PAGE_ID));           
        }
        if (requestPath.startsWith(POPUP_CLOSE_PATH)) {
            path = requestPath.substring(POPUP_CLOSE_PATH.length() - 1);
            closePopup = true;
        }

        String newPath = PageMarkerUtils.restorePageState(controllerContext, path);
        
        
        /* Applicative close */
        
        if( "1".equals(controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.closePopupOnAction"))) {
            closePopup = true;
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.closePopupOnAction", null);
        }
        
        


        if (popupClosed) {
            
            // For error displaying in master page
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_PAGE_ID, controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeOriginalPageID"));    
         
            controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupModeClosed", "1");
            controllerContext.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.popupModeClosedWindowID", "1");
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupMode", null);
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID", null);
         }

        if (closePopup) {
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
        
        

        PortalObjectId targetRestoreIdObject = null;
        PortalObjectPath targetRestorePath = null;        
        
        if (cmd instanceof ViewPortalCommand || cmd instanceof ViewPageCommand || cmd instanceof InvokePortletWindowRenderCommand) {

            if (!StringUtils.isEmpty(newPath)) {

                targetRestoreIdObject = ((PortalObjectCommand) cmd).getTargetId();
                
                int portalPathIndex = newPath.indexOf('/', 1);

                if (portalPathIndex != -1) {
                     targetRestorePath = PortalObjectPath.parse(newPath.substring(portalPathIndex), PortalObjectPath.CANONICAL_FORMAT);
                }
            }
        }
        
        if( cmd instanceof RefreshPageCommand){
            
            targetRestoreIdObject =  new PortalObjectId("",PortalObjectPath.parse(((RefreshPageCommand) cmd).getPageId(), PortalObjectPath.SAFEST_FORMAT));
            targetRestorePath = targetRestoreIdObject.getPath();
            
        }
        
        if (targetRestorePath != null) {

            PortalObjectId realPathId = new PortalObjectId("", targetRestorePath);

            PortalObject po = this.getPortalObjectContainer().getObject(realPathId);

            if (po == null) {
                
                String pagePath = targetRestorePath.getName(1);

                // Dynamic page creation : session may have been lost

                if (RestorablePageUtils.isRestorable(pagePath)) {

                    PortalObjectId portalId = null;
                    if (cmd instanceof ViewPortalCommand)
                        portalId = targetRestoreIdObject;
                    else {
                        PortalObjectPath parentPath = targetRestoreIdObject.getPath().getParent();
                        portalId = new PortalObjectId("", parentPath);
                    }

                    // Restore the page
                    RestorablePageUtils.restore(controllerContext, portalId, pagePath);
                    cmd = super.doMapping(controllerContext, invocation, host, contextPath, newPath);
                    
                    if( cmd instanceof ViewPageCommand){
                        // Remove parameters
                        cmd = new ViewPageCommand(((ViewPageCommand) cmd).getTargetId());
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
                   
                   EditionState editionState= new EditionState(EditionState.CONTRIBUTION_MODE_EDITION, editionPath[0]);
                   ContributionService.setWindowEditionState(controllerContext, ((InvokePortletWindowRenderCommand) cmd).getTargetId(), editionState);
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
        
        /* Back url when render parameters are modified */
        
        if( cmd instanceof InvokePortletWindowRenderCommand)    {
            
            // Back is not managed in ajax Mode
            
            if( !(ControllerContext.AJAX_TYPE == controllerContext.getType())) {
            
                boolean updateBack = true;
    
                StateString navigationalState = ((InvokePortletWindowRenderCommand) cmd).getNavigationalState();
                if (navigationalState instanceof ParametersStateString) {
                    Map<String, String[]> params = ((ParametersStateString) navigationalState).getParameters();
    
                    // Exclude breadcrumb
                    String[] displayContext = params.get("_displayContext");
    
                    if (displayContext != null && "breadcrumb".equals(displayContext[0])) {
                        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backPageMarker", null);
                        controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.refreshBack", null);
                        updateBack = false;
                    }
                }
    
                if (controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.popupModeWindowID") != null || popupClosed) {
                    updateBack = false;
    
                }
    
    
                if (updateBack) {
                    String backPageMarker = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");
                    controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backPageMarker", backPageMarker);
                }
            }


        }


        return cmd;

    }

}
