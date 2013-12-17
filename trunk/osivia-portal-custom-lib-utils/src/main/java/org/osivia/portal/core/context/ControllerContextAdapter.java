package org.osivia.portal.core.context;

import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.context.PortalControllerContext;


public class ControllerContextAdapter {
    
    public static ControllerContext getControllerContext( PortalControllerContext portalContext){
           return (ControllerContext) portalContext.getControllerCtx();
        
    }

}
