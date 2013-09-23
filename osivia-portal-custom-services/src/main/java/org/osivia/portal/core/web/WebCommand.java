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


    private String cmsPath;
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

    public void setCmsPath(String cmsPath) {
        this.cmsPath = cmsPath;
    }

    public String getCmsPath() {
        return cmsPath;
    }

    public WebCommand() {
    }

    public WebCommand(String cmsPath) {

        this.cmsPath = cmsPath;
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

    
    UpdatePageResponse pageResponse;
   
    
   
    
   private UpdatePageResponse getPageResponse(ControllerContext controllerCtx)  throws InvocationException, ControllerException {
       
       if( pageResponse == null){
           
           // Transformation du requestpath
           CmsCommand cmsCmd = new CmsCommand();
           cmsCmd.setCmsPath(cmsPath);
      
           pageResponse = (UpdatePageResponse) controllerCtx.execute(cmsCmd);

       }
       
       return pageResponse;
       
   }
    
    
   private PortalObjectId getPageId(ControllerContext controllerCtx) throws InvocationException, ControllerException {
        
       // Transformation du requestpath
       CmsCommand cmsCmd = new CmsCommand();
       cmsCmd.setCmsPath(cmsPath);
       
       
        // on regarde si la page courante pointe déja sur le contenu
        PortalObjectId pageId = (PortalObjectId) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId");
        if( pageId != null){
            String currentNavPath = PagePathUtils.getNavigationPath(controllerCtx, pageId);
            if( !currentNavPath.equals(cmsPath)) {
                // Le path de navigation a changé, il faut recréer la page technique
                pageId = null;
            }
         }
            
        if( pageId == null ){
                // Pour obtenir la page de contextualisation courante
                pageId = getPageResponse(controllerCtx).getPageId();
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

                // TODO : bodouiller les paramètres invocation.getServerContext().getBodyParameterMap()
                // Pour ne pas interférer avec les portlets
                // invocation.getServerContext().getPortalRequestPath()

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
