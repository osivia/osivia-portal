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

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;


public class ChangeContributionModeCommand extends ControllerCommand {
    private static final CommandInfo info = new ActionCommandInfo(false);
    
    private String windowID;
    private String newContributionMode;
    private String docPath;
    
    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }
    
    
    public String getNewContributionMode() {
        return newContributionMode;
    }


    
    public void setNewContributionMode(String newContributionMode) {
        this.newContributionMode = newContributionMode;
    }


    
    public String getDocPath() {
        return docPath;
    }


    
    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }


    public String getWindowID() {
        return windowID;
    }

    
    public void setWindowID(String windowID) {
        this.windowID = windowID;
    }

    


    @Override
    public CommandInfo getInfo() {
        return info;
    }
    

    public ChangeContributionModeCommand(String windowID, String newContributionMode, String docPath) {
        super();
        this.windowID = windowID;
        this.newContributionMode = newContributionMode;
        this.docPath = docPath;

    }


    @Override
    public ControllerResponse execute() throws ControllerException {
        // Contrôle droits

        try {
        PortalObjectId poid = PortalObjectId.parse(windowID, PortalObjectPath.SAFEST_FORMAT);
        Window window = (Window) getControllerContext().getController().getPortalObjectContainer().getObject(poid);

        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setControllerContext(getControllerContext());
        
        
        // Get live path
        cmsCtx.setDisplayLiveVersion("1");
        CMSPublicationInfos pubInfos = getCMSService().getPublicationInfos(cmsCtx, docPath);
        
        EditionState oldState = ContributionService.getWindowEditionState(getControllerContext(), window.getId());
        
        EditionState newState = new EditionState(newContributionMode, pubInfos.getDocumentPath());
        if( oldState != null && oldState.getDocPath().equals(pubInfos.getDocumentPath()))   {
            newState.setBackPageMarker(oldState.getBackPageMarker());
            newState.setHasBeenModified(oldState.isHasBeenModified());
        }
        
   
        ContributionService.setWindowEditionState(getControllerContext(), poid, newState);
      
        getControllerContext().setAttribute(REQUEST_SCOPE, "osivia.changeContributionMode", "1");
        
        PageProperties.getProperties().setRefreshingPage(true);        
        DynamicPortalObjectContainer.clearCache();
        
        return new UpdatePageResponse(window.getPage().getId());
        

    } catch (Exception e) {
        if (!(e instanceof ControllerException))
            throw new ControllerException(e);
        else
            throw (ControllerException) e;
    }
}

    

}
