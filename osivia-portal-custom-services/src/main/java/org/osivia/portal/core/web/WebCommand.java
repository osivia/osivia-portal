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
package org.osivia.portal.core.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.dynamic.DynamicCommand;
import org.osivia.portal.core.page.PagePathUtils;

/**
 * 
 * Format unifié d'url pour les PORTAL_SITE
 * 
 * @author jeanseb
 * 
 */
public class WebCommand extends DynamicCommand {

    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(WebCommand.class);


    @Override
    public CommandInfo getInfo() {
        return info;
    }


    private String webPath;
    private String windowName;
    private boolean supportingPageMarker = true;


      
    public boolean isSupportingPageMarker() {
        return supportingPageMarker;
    }


    
    public void setSupportingPageMarker(boolean supportingPageMarker) {
        this.supportingPageMarker = supportingPageMarker;
    }


    public String getWindowName() {
        return windowName;
    }


    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public String getWebPath() {
        return webPath;
    }

    public WebCommand() {
    }

    public WebCommand(String webPath) {

        this.webPath = webPath;
    }


    private static ICMSServiceLocator cmsServiceLocator;

    IPortalUrlFactory urlFactory;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }


    public IPortalUrlFactory getUrlFactory() throws Exception {

        if (urlFactory == null)
            urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");

        return urlFactory;
    }

    
    ControllerResponse pageResponse;
   
    
   
    
   private ControllerResponse getPageResponse(ControllerContext controllerCtx)  throws InvocationException, ControllerException {
       
       if( pageResponse == null){
           
           // Transformation du requestpath
           CmsCommand cmsCmd = new CmsCommand();
           
           String cmsPath = WebURLFactory.adaptWebURLToCMSPath(controllerCtx, webPath);
           cmsCmd.setCmsPath(cmsPath);
      
           
           
           pageResponse = controllerCtx.execute(cmsCmd);

       }
       
       return pageResponse;
       
   }
    
    
   private PortalObjectId getPageId(ControllerContext controllerCtx) throws InvocationException, ControllerException {
        
       // Transformation du requestpath
       CmsCommand cmsCmd = new CmsCommand();
       
       
       String cmsPath = WebURLFactory.adaptWebURLToCMSPath(controllerCtx, webPath);
       cmsCmd.setCmsPath(cmsPath);
       
       
        // on regarde si la page courante pointe déja sur le contenu
        PortalObjectId pageId = (PortalObjectId) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId");
        if( pageId != null){
            String currentNavPath = PagePathUtils.getNavigationPath(controllerCtx, pageId);

            // for resources, there is no currentNavPath. otherwise test that navpath = cmspath
            if (currentNavPath != null) {
                if (!currentNavPath.equals(cmsPath)) {
                    // Le path de navigation a changé, il faut recréer la page technique
                    pageId = null;
                }
            }
         }
            
        if( pageId == null ){
                // Pour obtenir la page de contextualisation courante
            ControllerResponse ctrlResp = getPageResponse(controllerCtx);
            
            if( ctrlResp instanceof UpdatePageResponse)
                pageId = ((UpdatePageResponse) ctrlResp).getPageId();
        }
        
        return pageId;
        
    }
    
    
    
    public PortalObjectId getWindowId( ControllerContext controllerCtx)  {
        
        try {
         
        if (windowName != null) {
            
             PortalObjectId pageId = getPageId(controllerCtx );
             
             PortalObjectId windowID =  new PortalObjectId("", new PortalObjectPath( pageId.getPath().toString().concat("/").concat(windowName), PortalObjectPath.CANONICAL_FORMAT));
             return windowID;
         }

        return null;
        } catch( Exception e){
            throw new RuntimeException(e);
        }
    }
    
    

    @Override 
    public ControllerResponse execute() throws ControllerException {

        try {

            PortalObjectId windowID = getWindowId( context);
            
            if( windowID != null)   {
 
                String originalPath = "/portal" + windowID;

                // create original command

                // remove non specific parameters

                ServerInvocation invocation = getControllerContext().getServerInvocation();
                
                 
                ControllerCommand originalCmd = getControllerContext()
                        .getController()
                        .getCommandFactory()
                        .doMapping(getControllerContext(), invocation, invocation.getServerContext().getPortalHost(),
                                invocation.getServerContext().getPortalContextPath(), originalPath);
              



                return context.execute(originalCmd);

            }

            // Affichage de la commande CMS
            return getPageResponse(context);


        } catch (Exception e) {
            throw new ControllerException(e);
        }

    }

}
