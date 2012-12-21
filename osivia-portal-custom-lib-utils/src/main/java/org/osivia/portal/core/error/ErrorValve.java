package org.osivia.portal.core.error;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;


/**
 * Cette Valve permet d'avoir un affichage standard pour les erreurs 404, 500 et 503 qui n'ont pas été traitées
 * applicativement
 * 
 * Pour  l'instant pas d'autre moyen pour un affichage commun à toutes les webapps
 * 
 * @author cap2j
 *
 */
public class ErrorValve extends ValveBase {

	public void invoke(Request request, Response response) throws IOException, ServletException, IllegalStateException {

		this.getNext().invoke(request, response);

		int httpErrorCode = 0;
		Throwable cause = null;

		String message = null;
		
		HttpServletRequest httpRequest = request.getRequest();
		
		String errorPageUri = System.getProperty("error.default_page_uri");
		
		// On ne traite pas la page d'erreur (pas de boucle !!! )
		if( request.getDecodedRequestURI().equals(errorPageUri))
			return;

		if (response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
			httpErrorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			message = "No Stack Trace. Check server.log";
		}

		if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND) {
			httpErrorCode = HttpServletResponse.SC_NOT_FOUND;
			message = "Resource " + request.getDecodedRequestURI() + " not found (error 404).";
		}

		if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
			httpErrorCode = HttpServletResponse.SC_FORBIDDEN;
			message = "Resource " + request.getDecodedRequestURI() + " forbidden (error 403).";
		}



		if (httpErrorCode > 0) {
			
			// On récupère l'exception transmise par le portail
			cause = (Exception) request.getAttribute("pia.error_exception");
			
			String userId = request.getRemoteUser();
			
			
			ErrorDescriptor errDescriptor = new ErrorDescriptor(httpErrorCode, cause, message, userId, null);

			if (response.getStatus() == 500 || response.getStatus() == 404 ) {
				
				long errId = GlobalErrorHandler.getInstance().registerError(errDescriptor);
	

				URL url = new URL("http", httpRequest.getServerName(), httpRequest.getServerPort(), errorPageUri);

				String sUrl = url.toString() + "?err=" + errId + "&httpCode=" + httpErrorCode;

				response.sendRedirect(sUrl);
			}
			
			if (response.getStatus() == 403 ) {
				
	
				// No error registered if forbidden
				URL url = new URL("http", httpRequest.getServerName(), httpRequest.getServerPort(), errorPageUri);

				String sUrl = url.toString() + "?httpCode=" + httpErrorCode;

				response.sendRedirect(sUrl);
			}


		}
	}

}
