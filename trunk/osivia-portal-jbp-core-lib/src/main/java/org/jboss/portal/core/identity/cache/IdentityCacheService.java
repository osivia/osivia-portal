package org.jboss.portal.core.identity.cache;

import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * PIA optimisaer le findRoles (trop long à l'affichage dans le back-office)
 */
public class IdentityCacheService
{
   private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(IdentityCacheService.class);

   public final static String JNDI_NAME = "java:portal/IdentityCacheService";

   protected ThreadLocal<Map<String, User>> userNameCache = new ThreadLocal<Map<String, User>>();

   protected ThreadLocal<Map<Object, User>> userIdCache = new ThreadLocal<Map<Object, User>>();

   protected ThreadLocal<Map<Object, Map>> profileCache = new ThreadLocal<Map<Object, Map>>();

   protected ThreadLocal<Map<String, Role>> roleNameCache = new ThreadLocal<Map<String, Role>>();

   protected ThreadLocal<Map<Object, Role>> roleIdCache = new ThreadLocal<Map<Object, Role>>();

  protected ThreadLocal<Set> roles = new ThreadLocal<Set>();

   
   public void cleanup()
   {
      userNameCache.set(null);
      userIdCache.set(null);
      profileCache.set(null);
      roleNameCache.set(null);
      roleIdCache.set(null);
      roles.set(null);

      log.debug("Identity cache invalidated");
   }

   private Map<String, User> getUserNameCache()
   {
      if (userNameCache.get() == null)
      {
         userNameCache.set(new HashMap<String, User>());
      }
      return userNameCache.get();
   }

   private Map<Object, User> getUserIdCache()
   {
      if (userIdCache.get() == null)
      {
         userIdCache.set(new HashMap<Object, User>());
      }
      return userIdCache.get();
   }

   private Map<Object, Map> getProfileCache()
   {
      if (profileCache.get() == null)
      {
         profileCache.set(new HashMap<Object, Map>());
      }
      return profileCache.get();
   }

   private Map<String, Role> getRoleNameCache()
   {
      if (roleNameCache.get() == null)
      {
         roleNameCache.set(new HashMap<String, Role>());
      }
      return roleNameCache.get();
   }

   private Map<Object, Role> getRoleIdCache()
   {
      if (roleIdCache.get() == null)
      {
         roleIdCache.set(new HashMap<Object, Role>());
      }
      return roleIdCache.get();
   }
   
 

   public void storeUser(User user)
   {
      // We want to be transparent so just ignore null argument
      if (user != null)
      {
         getUserIdCache().put(user.getId(), user);
         getUserNameCache().put(user.getUserName(), user);

         if (log.isDebugEnabled())
         {
            log.debug("User cached for id=" + user.getId() + "; username=" + user.getUserName());
         }
      }
   }

   public void invalidateUser(User user)
   {
      // We want to be transparent so just ignore null argument
      if (user != null)
      {
         getUserIdCache().put(user.getId(), null);
         getUserNameCache().put(user.getUserName(), null);

         if (log.isDebugEnabled())
         {
            log.debug("User invalidated in cache for id=" + user.getId() + "; username=" + user.getUserName());
         }
      }
   }

   public void storeProfile(User user, Map profile)
   {
      // We want to be transparent so just ignore null argument
      if (user != null && profile != null)
      {
         getProfileCache().put(user.getId(), profile);

         if (log.isDebugEnabled())
         {
            log.debug("User profile cached for id=" + user.getId());
         }
      }
   }


   public void invalidateProfile(User user)
   {
      // We want to be transparent so just ignore null argument
      if (user != null)
      {
         getProfileCache().put(user.getId(), null);

         if (log.isDebugEnabled())
         {
            log.debug("User profile invalidated in cache for id=" + user.getId());
         }
      }
   }

   public void storeRole(Role role)
   {
      // We want to be transparent so just ignore null argument
      if (role != null)
      {
         getRoleIdCache().put(role.getId(), role);
         getRoleNameCache().put(role.getName(), role);

         if (log.isDebugEnabled())
         {
            log.debug("Role cached for id=" + role.getId() + "; name=" + role.getName());
         }
      }
   }

   public void invalidateRole(Role role)
   {
      // We want to be transparent so just ignore null argument
      if (role != null)
      {
         getRoleIdCache().put(role.getId(), null);
         getRoleNameCache().put(role.getName(), null);

         if (log.isDebugEnabled())
         {
            log.debug("Role invalidated in cache for id=" + role.getId() + "; name=" + role.getName());
         }
      }
   }

   public User findUserByUserName(String userName)
   {
      User user = getUserNameCache().get(userName);

      if (user != null && log.isDebugEnabled())
      {
         log.debug("User retrieved from cache for username=" + user.getUserName());
      }

      return user;
   }

   public User findUserById(Object id)
   {
      User user = getUserIdCache().get(id);

      if (user != null && log.isDebugEnabled())
      {
         log.debug("User retrieved from cache for id=" + user.getId());
      }

      return user;
   }

   public Map findUserProfileById(Object id)
   {
      Map profile = getProfileCache().get(id);

      if (profile != null && log.isDebugEnabled())
      {
         log.debug("User profile retrieved from cache for user id=" + id);
      }

      return profile;
   }

   public Role findRoleByName(String roleName)
   {
      Role role = getRoleNameCache().get(roleName);

      if (role != null && log.isDebugEnabled())
      {
         log.debug("Role retrieved from cache for name=" + role.getName());
      }

      return role;
   }

   public Role findRoleById(Object id)
   {
      Role role = getRoleIdCache().get(id);

      if (role != null && log.isDebugEnabled())
      {
         log.debug("Role retrieved from cache for id=" + role.getId());
      }

      return role;
   }
   
   
   public void storeRoles(User user, Map profile)
   {
      // We want to be transparent so just ignore null argument
      if (user != null && profile != null)
      {
         getProfileCache().put(user.getId(), profile);

         if (log.isDebugEnabled())
         {
            log.debug("User profile cached for id=" + user.getId());
         }
      }
   }


   public Set getRoles()
   {
      return roles.get() ;
      
   }
   
   public void storeRoles(Set rolesToSet)	{
	   roles.set(rolesToSet);
   }

   
}
