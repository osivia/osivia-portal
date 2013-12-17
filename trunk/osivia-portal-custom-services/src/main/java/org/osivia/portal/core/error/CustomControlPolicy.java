package org.osivia.portal.core.error;

import javax.servlet.http.HttpServletResponse;

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.jboss.portal.core.model.portal.control.page.PageControlContext;
import org.jboss.portal.identity.User;
import org.osivia.portal.core.error.ErrorDescriptor;




/**
 * C'eest ici que l'on peut traiter le plus finement les erreurs, en particulier
 * les erreurs applicatives
 * 
 * Les auressont traitées dans la Valve
 *
 */
public abstract class CustomControlPolicy {
	
	protected ErrorDescriptor getErrorDescriptor(ControllerResponse response, String userId) {		
		ErrorDescriptor errDescriptor = null;
		int httpErrorCode = -1;
		Throwable cause = null;
		String message = null;		

		if (response instanceof ErrorResponse) {			
			ErrorResponse error = (ErrorResponse) response;
			cause = error.getCause();
			message = error.getMessage();
			httpErrorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

			if (response instanceof SecurityErrorResponse) {
				SecurityErrorResponse ser = (SecurityErrorResponse) response;
				if (ser.getStatus() == SecurityErrorResponse.NOT_AUTHORIZED) {
					httpErrorCode = HttpServletResponse.SC_FORBIDDEN;
				}
			}
		} else if (response instanceof UnavailableResourceResponse) {
			UnavailableResourceResponse unavailable = (UnavailableResourceResponse) response;
			httpErrorCode = HttpServletResponse.SC_NOT_FOUND;
			message = "Resource " + unavailable.getRef() + " not found (error 404).";
		}

		if (httpErrorCode > 0) {
			errDescriptor = new ErrorDescriptor(httpErrorCode, cause, message, userId, null);
		}
		return errDescriptor;
	}

	protected String getUserId(User user) {
		if( user == null)
			return null;
		else
			return user.getUserName();
	}
	
}
