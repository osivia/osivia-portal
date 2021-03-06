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
package org.osivia.portal.core.contribution;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;

/**
 * CMS command used when a document is published
 */
public class PublishContributionCommand extends ControllerCommand {



    private static final String SUCCESS_MESSAGE_PUBLISH = "SUCCESS_MESSAGE_PUBLISH";
    private static final String SUCCESS_MESSAGE_UNPUBLISH = "SUCCESS_MESSAGE_UNPUBLISH";
    private static final String SUCCESS_MESSAGE_ASK_PUBLISH = "SUCCESS_MESSAGE_ASK_PUBLISH"; 
    private static final String SUCCESS_MESSAGE_PUBLISHING_VALIDATED = "SUCCESS_MESSAGE_PUBLISHING_VALIDATED";
    private static final String SUCCESS_MESSAGE_PUBLISHING_REJECTED = "SUCCESS_MESSAGE_PUBLISHING_REJECTED";
    private static final String SUCCESS_CANCEL_PUBLISH = "SUCCESS_CANCEL_PUBLISH";

    ICMSService cmsService;

    private static INotificationsService notifService = NotificationsUtils.getNotificationsService();
    private static IInternationalizationService itlzService = InternationalizationUtils.getInternationalizationService();

    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }

    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(PublishContributionCommand.class);

    /** page ID used for reload after delete */
    private String windowId;

    /** page path used by the ECM */
    private String docPath;

    /** action (publish / unpublish) */
    private String actionCms;
    
    private String backCMSPageMarker;

    
    public String getBackCMSPageMarker() {
        return backCMSPageMarker;
    }


    /**
     * @return the pagePath
     */
    public String getDocPath() {
        return docPath;
    }


    /**
     * @param pagePath the pagePath to set
     */
    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }


    /**
     * @return the pageId
     */
    public String getWindowId() {
        return windowId;
    }

    /**
     * @return the actionCms
     */
    public String getActionCms() {
        return actionCms;
    }

    /**
     * @param actionCms the actionCms to set
     */
    public void setActionCms(String actionCms) {
        this.actionCms = actionCms;
    }


    public PublishContributionCommand(String windowId, String docPath, String actionCms, String backCMSPageMarker) {
        this.windowId = windowId;
        this.docPath = docPath;
        this.actionCms = actionCms;
        this.backCMSPageMarker= backCMSPageMarker;        

    }

    public CommandInfo getInfo() {
        return info;
    }


    public ControllerResponse execute() throws ControllerException {


        try {
            // Contrôle droits

            PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
            Window window = (Window) getControllerContext().getController().getPortalObjectContainer().getObject(poid);

            CMSServiceCtx cmsCtx = new CMSServiceCtx();
            cmsCtx.setControllerContext(getControllerContext());


  
            PortalControllerContext pcc = new PortalControllerContext(getControllerContext());

            if (actionCms.equals(IContributionService.PUBLISH)) {
                
                getCMSService().publishDocument(cmsCtx, docPath);
                return updatePage(window, pcc, SUCCESS_MESSAGE_PUBLISH, EditionState.CONTRIBUTION_MODE_ONLINE);
                
            } else if (actionCms.equals(IContributionService.UNPUBLISH)) {
                
                
                getCMSService().unpublishDocument(cmsCtx, docPath);

                String success = itlzService.getString(SUCCESS_MESSAGE_UNPUBLISH, getControllerContext().getServerInvocation().getRequest().getLocale());
                notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);
                
                
                
                // Back url after cms action (ex: remove, goback)
                if( backCMSPageMarker != null){
                     PageMarkerInfo infos = PageMarkerUtils.getPageMarkerInfo(getControllerContext(), backCMSPageMarker);
                    if( infos !=  null){
                        PortalObjectId pageId = infos.getPageId();

                        URLContext urlContext = ControllerContextAdapter.getControllerContext(pcc).getServerInvocation().getServerContext().getURLContext();
                        RefreshPageCommand resfreshCmd = new RefreshPageCommand(pageId.toString(PortalObjectPath.SAFEST_FORMAT));
                        String resfreshUrl = ControllerContextAdapter.getControllerContext(pcc).renderURL(resfreshCmd, urlContext, URLFormat.newInstance(false, true));
                        
                        // Replace page marker to restore portlet state
                        resfreshUrl =  resfreshUrl.replaceAll("/pagemarker/([0-9]*)/","/pagemarker/"+backCMSPageMarker+"/");

                        return new RedirectionResponse(resfreshUrl);
                    }
                    
                }
                
                
                // relaod navigation tree
                PageProperties.getProperties().setRefreshingPage(true);


                // Redirect to the parent window
                CMSObjectPath parentPath = CMSObjectPath.parse(docPath).getParent();
                String redirectPath = parentPath.toString();

                CmsCommand redirect = new CmsCommand(null, redirectPath, null, null, "destroyedChild", null, null, null, null, null, null);
                ControllerResponse execute = context.execute(redirect);

                return execute;
                
            } else if (actionCms.equals(IContributionService.ASK_PUBLISH)) {
                
                getCMSService().askToPublishDocument(cmsCtx, docPath);
                return updatePage(window, pcc, SUCCESS_MESSAGE_ASK_PUBLISH, EditionState.CONTRIBUTION_MODE_EDITION);
                
            } else if (actionCms.equals(IContributionService.VALIDATE_PUBLISHING)) {
                
                getCMSService().validatePublicationOfDocument(cmsCtx, docPath);
                return updatePage(window, pcc, SUCCESS_MESSAGE_PUBLISHING_VALIDATED, EditionState.CONTRIBUTION_MODE_ONLINE);
                
            } else if (actionCms.equals(IContributionService.REJECT_PUBLISHING)) {
                
                getCMSService().rejectPublicationOfDocument(cmsCtx, docPath);
                return updatePage(window, pcc, SUCCESS_MESSAGE_PUBLISHING_REJECTED, EditionState.CONTRIBUTION_MODE_EDITION);
                
            } else if (actionCms.equals(IContributionService.CANCEL_PUBLISH)) {
                
                getCMSService().cancelPublishWorkflow(cmsCtx, docPath);
                return updatePage(window, pcc, SUCCESS_CANCEL_PUBLISH, EditionState.CONTRIBUTION_MODE_EDITION);
                
            }
            
            return null;



        } catch (Exception e) {
            if (!(e instanceof ControllerException))
                throw new ControllerException(e);
            else
                throw (ControllerException) e;
        }
    }


    /**
     * @param window
     * @param pcc
     * @return
     */
    public ControllerResponse updatePage(Window window, PortalControllerContext pcc, String notificationKey, String state) {
        
        String success = itlzService.getString(notificationKey, getControllerContext().getServerInvocation().getRequest().getLocale());
        notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);
        
        EditionState editionState = new EditionState(state, docPath);
        
         // Restore navigation values   
//        EditionState oldState = ContributionService.getWindowEditionState(getControllerContext(), window.getId());
//        if (oldState != null) {
//            if (oldState.getDocPath().equals(docPath)) {
//                editionState.setBackPageMarker(oldState.getBackPageMarker());
//                }
//        }          
//        
//        editionState.setHasBeenModified(true);
        getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.refreshBack", true);
        getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.mobileRefreshBack", true);
                
        
        ContributionService.setWindowEditionState(getControllerContext(), window.getId(), editionState);
  
        
        
        
        PageProperties.getProperties().setRefreshingPage(true);
        DynamicPortalObjectContainer.clearCache();        
        
        return new UpdatePageResponse(window.getPage().getId());
        
    }

}
