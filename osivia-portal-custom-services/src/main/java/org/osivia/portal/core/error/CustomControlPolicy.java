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
	
	protected ErrorDescriptor getErrorDescriptor(ControllerResponse response, String userId, String context) {		
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
					cause = null;
				}
			}    
		} else if (response instanceof UnavailableResourceResponse) {
			UnavailableResourceResponse unavailable = (UnavailableResourceResponse) response;
			httpErrorCode = HttpServletResponse.SC_NOT_FOUND;
			message =  unavailable.getRef() + " not found ";
		}

		if (httpErrorCode > 0) {
		    if( context != null && message != null)
		        message = context+ " : " + message;
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
