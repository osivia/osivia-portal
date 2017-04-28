package org.osivia.core.identity;
import org.jboss.logging.Logger;
import org.jboss.portal.common.invocation.AttributeResolver;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.p3p.P3PConstants;
import org.jboss.portal.identity.CachedUserImpl;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.UserProfileModule;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The interceptor is responsible for managing the user identity lifecycle based on the principal name returned by the
 * <code>HttpServletRequest.getUserPrincipal()</code> method.
 * <p/>
 * 
 * OSIVIA : suppress update of props (clustering impact)
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 12465 $
 */
public class UserInterceptor extends ServerInterceptor
{

   /** . */
   public static final String PROFILE_KEY = "profile";

   /** . */
   public static final String USER_KEY = "user";

   /** Our logger. */
   private static final Logger log = Logger.getLogger(UserInterceptor.class);

   /** User. */
   protected UserModule userModule = null;

   /** UserProfile */
   protected UserProfileModule userProfileModule = null;

   /** . */
   protected boolean cacheUser = true;

   public UserModule getUserModule()
   {
      if (userModule == null)
      {
         try
         {
            userModule = (UserModule)new InitialContext().lookup("java:portal/UserModule");
         }
         catch (NamingException e)
         {
            log.error("could not obtain User Module: ", e);
         }
      }
      return userModule;
   }

   public UserProfileModule getUserProfileModule()
   {
      if (userProfileModule == null)
      {
         try
         {
            userProfileModule = (UserProfileModule)new InitialContext().lookup("java:portal/UserProfileModule");
         }
         catch (NamingException e)
         {
            log.error("could not obtain UserProfileModule: ", e);
         }
      }
      return userProfileModule;
   }


   public void setUserModule(UserModule userModule)
   {
      this.userModule = userModule;
   }

   protected void invoke(ServerInvocation invocation) throws Exception, InvocationException
   {
      boolean trace = log.isTraceEnabled();
      HttpServletRequest req = invocation.getServerContext().getClientRequest();

      // Get scope
      AttributeResolver principalScopeResolver = invocation.getContext().getAttributeResolver(ServerInvocation.PRINCIPAL_SCOPE);

      // Get the id
      Principal userPrincipal = req.getUserPrincipal();

      // The user and its profile
      User user = null;
      Map<String, String> profile = null;

      // Fetch user if we can
      if (userPrincipal != null)
      {
         String userName = userPrincipal.getName();

         //
         try
         {
            if (trace)
            {
               log.trace("About to fetch user=" + userName);
            }

            // Try to obtain cached user
            user = (User)principalScopeResolver.getAttribute(USER_KEY);

            //
            if (user == null)
            {
               // Fetch user info
               user = getUserModule().findUserByUserName(userName);

               /*
               // Set Last login date
               getUserProfileModule().setProperty(user, User.INFO_USER_LAST_LOGIN_DATE, "" + new Date().getTime());

               // Set login id
               getUserProfileModule().setProperty(user, P3PConstants.INFO_USER_LOGIN_ID, userName);
               */

               // Get profile
               profile = getUserProfileModule().getProperties(user);

               // Build detached pojo
               user = new CachedUserImpl(user.getId(), user.getUserName());

               // Cache
               invocation.getContext().setAttribute(ServerInvocation.PRINCIPAL_SCOPE, USER_KEY, user);

               // Get a detached object
               profile = new HashMap(profile);

               // Cache
               invocation.getContext().setAttribute(ServerInvocation.PRINCIPAL_SCOPE, PROFILE_KEY, profile);


            }

            //
            if (trace)
            {
               log.trace("Found user=" + userName);
            }
         }
         catch (NoSuchUserException e)
         {
            if (trace)
            {
               log.trace("User not found " + userName + " for principal " + userName + ", will use no user instead");
            }
         }
         catch (Exception e)
         {
            log.error("Cannot retrieve user=" + userName, e);
            throw new InvocationException("Cannot fetch user=" + userName, e);
         }
      }

      try
      {
         // Continue the invocation
         invocation.invokeNext();
      }
      finally
      {
         if (!cacheUser)
         {
            principalScopeResolver.setAttribute(USER_KEY, null);
            principalScopeResolver.setAttribute(PROFILE_KEY, null);
         }
      }
   }

   public boolean isCacheUser()
   {
      return cacheUser;
   }

   public void setCacheUser(boolean cacheUser)
   {
      this.cacheUser = cacheUser;
   }

}
