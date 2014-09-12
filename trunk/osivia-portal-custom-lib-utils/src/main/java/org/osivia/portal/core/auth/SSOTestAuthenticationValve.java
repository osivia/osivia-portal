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

package org.osivia.portal.core.auth;

import java.io.IOException;
import java.security.Principal;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Context;
import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;



/*
 * Valve de test (ne pas mettre en prod)
 * 
 * permet de tester le comportement SSO - A ACTIVER dans jboss-portal-ha.sar/portal-server.war/WEB-INFcontext.xml
 * 
 * 
<?xml version="1.0"?>
<Context>

  <Valve className="org.osivia.portal.core.auth.SSOTestAuthenticationValve" 	   />
   
</Context>
 * 
  */
public class SSOTestAuthenticationValve extends ValveBase
{
   /** . */
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(SSOTestAuthenticationValve.class);


   
   private String fileEncoding = null;
   private String authType = "FORM";

   public SSOTestAuthenticationValve() 
   {
      super();
      fileEncoding = System.getProperty("file.encoding");
   }


   /**
    * 
    */
   public void invoke(Request request, Response response) throws IOException,
         ServletException
   {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpSession session = httpRequest.getSession();
      request.setAttribute("ssoEnabled", "true");

      // set character encoding before retrieving request parameters
      if(fileEncoding!=null) 
      {
         request.setCharacterEncoding(fileEncoding);
      }
      
      if (request.getParameter("ssouser") != null
            && session.getAttribute("ssouser") == null)
      {
    	  String userName = null;
    	  
         boolean skip = false;


        	 userName = request.getParameter("ssouser");


         if (!skip && userName == null)
         {
            skip = true;
         }

         if(!skip)
         {
            session.setAttribute("ssouser", userName);

   
            // perform the portal JAAS authentication
            String user = userName;
            request.setAttribute("ssoSuccess", new Boolean(true));
            Principal principal = ((Context) this.container).getRealm()
                  .authenticate(user, (String) null);
            if (principal != null)
            {
               this.register(request, response, principal, this.authType, user,
                     (String) null);
            }
         }
      }

      // continue processing the request
      this.getNext().invoke(request, response);
        
     
   }

   /**
    * Register an authenticated Principal and authentication type in our
    * request, in the current session (if there is one), and with our
    * SingleSignOn valve, if there is one. Set the appropriate cookie to be
    * returned.
    * 
    * @param request
    *           The servlet request we are processing
    * @param response
    *           The servlet response we are generating
    * @param principal
    *           The authenticated Principal to be registered
    * @param authType
    *           The authentication type to be registered
    * @param username
    *           Username used to authenticate (if any)
    * @param password
    *           Password used to authenticate (if any)
    */
   private void register(Request request, Response response,
         Principal principal, String authType, String username, String password)
   {
      // Cache the authentication information in our request
      request.setAuthType(authType);
      request.setUserPrincipal(principal);

      Session session = request.getSessionInternal(false);
      // Cache the authentication information in our session, if any
      if (session != null)
      {
         session.setAuthType(authType);
         session.setPrincipal(principal);
         if (username != null)
         {
            session.setNote(Constants.SESS_USERNAME_NOTE, username);
         }
         else
         {
            session.removeNote(Constants.SESS_USERNAME_NOTE);
         }
         if (password != null)
         {
            session.setNote(Constants.SESS_PASSWORD_NOTE, password);
         }
         else
         {
            session.removeNote(Constants.SESS_PASSWORD_NOTE);
         }
      }
   }

  

  
}
