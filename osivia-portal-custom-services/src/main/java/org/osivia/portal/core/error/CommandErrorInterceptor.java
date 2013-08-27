/**
 * 
 */
package org.osivia.portal.core.error;

import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.UserNotification;

public class CommandErrorInterceptor extends ControllerInterceptor {

	
    private ControllerResponse injectIntoValve(ControllerCommand cmd, Exception e ){
        
        cmd.getControllerContext().getServerInvocation().setAttribute(Scope.REQUEST_SCOPE,"osivia.error_exception", e);
        
        return new ErrorResponse("Portal exception", false);

        
    }
    
	public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
		
		ControllerResponse resp = null;
		
		try	{
			resp = (ControllerResponse) cmd.invokeNext();
			
			
		
		} 
		
		catch( UserNotificationException e)   {
		    
		    /* Business exception are displayed in current page */
		    
            PortalObjectId portalObjectId = (PortalObjectId) cmd.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId");
            
            if( portalObjectId != null){
                 cmd.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, InternalConstants.ATTR_USER_NOTIFICATION , e.getUserNotification());
                 return new UpdatePageResponse(portalObjectId);
            }       else    {
                injectIntoValve (cmd, e);
                
            }
		}
		
		catch( Exception e){
			
			// Les exceptions seront traitées dans la valve
		    return injectIntoValve (cmd, e);
		}

		return resp;
	}

	
}
