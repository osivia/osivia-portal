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
package org.osivia.portal.core.cms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
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
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
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

/**
 * CMS command used to remove a document
 */
public class CMSPutDocumentInTrashCommand extends ControllerCommand {

    private static final String SUCCESS_MESSAGE_DELETE = "SUCCESS_MESSAGE_DELETE";

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
    protected static final Log logger = LogFactory.getLog(CMSPutDocumentInTrashCommand.class);

    
   

    /** document Id used by the ECM */
    private String docId;
    
    /** document path used by the ECM */
    private String docPath;
    
    
    /** return actions */
    private String backCMSPageMarker;

    
    public String getBackCMSPageMarker() {
		return backCMSPageMarker;
	}



	public String getDocPath() {
        return docPath;
    }


    
    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }


    /**
     * @return the pagePath
     */
    public String getDocId() {
        return docId;
    }


    /**
     * @param pagePath the pagePath to set
     */
    public void setDocId(String docId) {
        this.docId = docId;
    }




    public CMSPutDocumentInTrashCommand( String docPath, String docId, String backCMSPageMarker) {

        this.docId = docId;
        this.docPath = docPath;
        this.backCMSPageMarker= backCMSPageMarker;
    }

    public CommandInfo getInfo() {
        return info;
    }


    public ControllerResponse execute() throws ControllerException {


        try {
            // Contrôle droits
 
            CMSServiceCtx cmsCtx = new CMSServiceCtx();
            cmsCtx.setControllerContext(getControllerContext());
 
            PortalControllerContext pcc = new PortalControllerContext(getControllerContext());


            getCMSService().putDocumentInTrash(cmsCtx, docId);

            String success = itlzService.getString(SUCCESS_MESSAGE_DELETE, getControllerContext().getServerInvocation().getRequest().getLocale());
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


        } catch (Exception e) {
            if (!(e instanceof ControllerException))
                throw new ControllerException(e);
            else
                throw (ControllerException) e;
        }
    }

}
