package org.jboss.portal.core.identity.cache;

import org.jboss.portal.identity.ldap.LDAPRoleModule;
import org.jboss.portal.identity.ldap.LDAPRoleImpl;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.IdentityException;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Set;
import java.util.List;
import java.util.HashSet;

/**
 * PIA optimisaer le findRoles (trop long à l'affichage dans le back-office)
 * 
 *   mise en place cache sur les roles
 */

public class CachedLDAPRoleModuleWrapper extends LDAPRoleModule implements RoleModule
{
   private LDAPRoleModule ldapRoleModule;

   private IdentityCacheService cacheService;

   public CachedLDAPRoleModuleWrapper(LDAPRoleModule ldapRoleModule, IdentityCacheService cacheService)
   {
      this.ldapRoleModule = ldapRoleModule;
      this.cacheService = cacheService;
   }

   public Role findRoleByName(String name) throws IdentityException, IllegalArgumentException
   {
      Role role = cacheService.findRoleByName(name);

      if (role != null)
      {
         return role;
      }

      return ldapRoleModule.findRoleByName(name);
   }

   public Set findRolesByNames(String[] names) throws IdentityException, IllegalArgumentException
   {

      //Check if all roles needed are in cache. If not just delegate to the wrapped module
      Set roles = new HashSet();

      for (String name : names)
      {
         Role role = cacheService.findRoleByName(name);
         if (role != null)
         {
            roles.add(role);
         }
         else
         {
            roles = ldapRoleModule.findRolesByNames(names);
            break;
         }
      }

      return roles;
   }

   public Role findRoleById(Object id) throws IdentityException, IllegalArgumentException
   {
      Role role = cacheService.findRoleById(id);

      if (role != null)
      {
         return role;
      }

      return ldapRoleModule.findRoleById(id);
   }

   public Role findRoleById(String id) throws IdentityException, IllegalArgumentException
   {
      return this.findRoleById((Object)id);
   }

   public Role createRole(String name, String displayName) throws IdentityException, IllegalArgumentException
   {
      Role role = ldapRoleModule.createRole(name, displayName);

      cacheService.storeRole(role);

      return role;
   }

   public void removeRole(Object id) throws IdentityException, IllegalArgumentException
   {
      ldapRoleModule.removeRole(id);

      // Invalidate this role in cache
      Role role = cacheService.findRoleById(id);
      if (role != null)
      {
         cacheService.invalidateRole(role);
      }
   }

   public int getRolesCount() throws IdentityException
   {
      return ldapRoleModule.getRolesCount();
   }

   public Set findRoles() throws IdentityException
   {
	     Set roles = cacheService.getRoles();

	      if (roles != null)
	      {
	         return roles;
	      }
	      
	      roles = ldapRoleModule.findRoles();
	      cacheService.storeRoles( roles);

	   
      return ldapRoleModule.findRoles();
   }

   public List searchRoles(String filter, Object[] filterArgs) throws NamingException, IdentityException
   {
      return ldapRoleModule.searchRoles(filter, filterArgs);
   }

   // Methods of LDAPRoleModule - need to delegate for compatibility

   public void updateDisplayName(LDAPRoleImpl ldapr, String name) throws IdentityException
   {
      ldapRoleModule.updateDisplayName(ldapr, name);

      cacheService.invalidateRole(ldapr);
   }

   public LDAPRoleImpl createRoleInstance(Attributes attrs, String dn) throws IdentityException
   {
      return ldapRoleModule.createRoleInstance(attrs, dn);
   }

   public Role findRoleByDN(String dn) throws IdentityException, IllegalArgumentException
   {
      return ldapRoleModule.findRoleByDN(dn);
   }


}
