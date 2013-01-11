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

public class CommandErrorInterceptor extends ControllerInterceptor {

	

	public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
		
		ControllerResponse resp = null;
		
		try	{
			resp = (ControllerResponse) cmd.invokeNext();
		
		} catch( Exception e){
			
			// Les exceptions seront traitées dans la valve
			
			cmd.getControllerContext().getServerInvocation().setAttribute(Scope.REQUEST_SCOPE,"osivia.error_exception", e);
			
			return new ErrorResponse("Portal exception", false);
		}

		return resp;
	}

	
}
