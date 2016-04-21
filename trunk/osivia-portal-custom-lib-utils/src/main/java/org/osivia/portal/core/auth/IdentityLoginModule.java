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

import org.jboss.portal.common.transaction.Transactions;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.MembershipModule;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.identity.UserStatus;
import org.jboss.portal.identity.auth.UserPrincipal;
import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.TransactionManager;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A login module that uses the user module.
 */
public class IdentityLoginModule extends org.jboss.portal.identity.auth.IdentityLoginModule
{

   protected Group[] getRoleSets() throws LoginException
   {
      try {
    	  
    	  
         TransactionManager tm = (TransactionManager) new InitialContext()
               .lookup("java:/TransactionManager");
         return (Group[]) Transactions.required(tm, new Transactions.Runnable()
         {
            public Object run() throws Exception
            {
               Group rolesGroup = new SimpleGroup("Roles");

               //
               if (additionalRole != null) {
                  rolesGroup.addMember(createIdentity(additionalRole));
               }

               try {
                  User user = getUserModule().findUserByUserName(getUsername());
                  Set roles = getMembershipModule().getRoles(user);

                  //

                  for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
                     Role role = (Role) iterator.next();
                     String roleName = role.getName();
                     try {
                        Principal p = createIdentity(roleName);
                        rolesGroup.addMember(p);
                     } catch (Exception e) {
                        log.debug("Failed to create principal " + roleName, e);
                     }
                  }


               } catch (Exception e) {
						boolean loginError = true;

						if (e instanceof NoSuchUserException) {

							if ("1".equals(System.getProperty("sso.undeclared-user"))) {

								HttpServletRequest request = null;
								try {
									request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
								} catch (Exception exception) {
									log.error(this, e);
									throw new RuntimeException(e);
								}

								Object ssoSuccess = request.getAttribute("ssoSuccess");

								if (ssoSuccess != null) {
									loginError = false;
									rolesGroup.addMember(createIdentity("undeclared-user"));

								} else
									throw new LoginException(e.toString());
							}
						}

						if (loginError)
							throw new LoginException(e.toString());
           }
               //
               return new Group[] { rolesGroup };
            }
         });
      } catch (Exception e) {
         Throwable cause = e.getCause();
         throw new LoginException(cause.toString());
      }
   }

  
}
