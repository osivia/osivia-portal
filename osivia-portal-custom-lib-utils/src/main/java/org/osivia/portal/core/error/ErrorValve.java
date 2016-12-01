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
 */
package org.osivia.portal.core.error;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.osivia.portal.core.utils.URLUtils;


/**
 * Cette Valve permet d'avoir un affichage standard pour les erreurs 404, 500 et 503 qui n'ont pas été traitées applicativement.
 * Pour l'instant pas d'autre moyen pour un affichage commun à toutes les webapps.
 * 
 * @author Jean-Sébastien Steux
 * @see ValveBase
 */
public class ErrorValve extends ValveBase {

    public static ThreadLocal<Request> mainRequest = new ThreadLocal<Request>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException, IllegalStateException {


        mainRequest.set(request);


        try {

            this.getNext().invoke(request, response);

            int httpErrorCode = 0;
            Throwable cause = null;


            HttpServletRequest httpRequest = request.getRequest();

            // 2.1 / JSS / Multi-sites
            String dotServerName = request.getServerName().replaceAll("\\.", "-dot-");
            String errorPageUri = System.getProperty("portal.error.host." + dotServerName + ".uri");

            if (errorPageUri == null) {
                errorPageUri = System.getProperty("error.defaultPageUri");
            }

            // On ne traite pas la page d'erreur (pas de boucle !!! )
            if (request.getDecodedRequestURI().equals(errorPageUri)) {
                return;
            }

            if (response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                httpErrorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }

            if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND) {
                httpErrorCode = HttpServletResponse.SC_NOT_FOUND;
            }

            if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
                httpErrorCode = HttpServletResponse.SC_FORBIDDEN;
            }

            if (httpErrorCode > 0) {
                // On récupère l'exception transmise par le portail
                cause = (Exception) request.getAttribute("osivia.error_exception");
                if (cause == null) {
                    cause = (Exception) request.getAttribute("javax.servlet.error.exception");
                }


                Principal principal = request.getPrincipal();
                String userId = null;
                if (principal != null)
                    userId = principal.getName();


                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("osivia.url", request.getDecodedRequestURI());
                properties.put("osivia.header.userAgent", request.getHeader("User-Agent"));


                ErrorDescriptor errDescriptor = new ErrorDescriptor(httpErrorCode, cause, null, userId, properties);

                if ((response.getStatus() == 500 || response.getStatus() == 404) && !"1".equals(request.getAttribute("osivia.no_redirection"))) {
                    Long errId = (Long) request.getAttribute("osivia.loggedError");

                    if (errId == null)
                        errId = GlobalErrorHandler.getInstance().logError(errDescriptor);

                    Map<String, String> parameters = new HashMap<String, String>(2);
                    parameters.put("err", String.valueOf(errId));
                    parameters.put("httpCode", String.valueOf(httpErrorCode));
                    String url = URLUtils.createUrl(httpRequest, errorPageUri, parameters);

                    response.sendRedirect(url);
                } else if (response.getStatus() == 403 && !"1".equals(request.getAttribute("osivia.no_redirection"))) {
                    // No error registered if forbidden
                    Map<String, String> parameters = new HashMap<String, String>(1);
                    parameters.put("httpCode", String.valueOf(httpErrorCode));
                    String url = URLUtils.createUrl(httpRequest, errorPageUri, parameters);

                    response.sendRedirect(url);
                }
            }
        } finally {
            mainRequest.set(null);
        }

    }

}
