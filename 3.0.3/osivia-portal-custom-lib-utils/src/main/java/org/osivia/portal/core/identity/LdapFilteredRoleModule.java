package org.osivia.portal.core.identity;

import java.util.HashSet;
import java.util.Set;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.ldap.LDAPRoleModuleImpl;
import org.jboss.portal.security.SecurityConstants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.profils.FilteredRole;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;

/**
 * LDAP filtered role module.
 *
 * @see LDAPRoleModuleImpl
 */
public class LdapFilteredRoleModule extends LDAPRoleModuleImpl {

    /**
     * Default constructor.
     */
    public LdapFilteredRoleModule() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Role> findRoles() throws IdentityException {
        HashSet<Role> filteredRoles = new HashSet<Role>();

        try {
            IProfilManager profilManager = Locator.findMBean(IProfilManager.class, "osivia:service=ProfilManager");

            // Roles are filtered against profiles.
            for (ProfilBean profil : profilManager.getListeProfils()) {
                try {
                    Role role = this.findRoleByName(profil.getRoleName());
                    filteredRoles.add(role);
                } catch (Exception e) {
                    // Unknown role, go to next role
                }
            }

            // Unchecked virtual role
            if (System.getProperty("ldap.groupes_profils_obligatoires") != null) {
                filteredRoles.add(new FilteredRole(FilteredRole.UNCHECKED_ROLE_NAME, FilteredRole.UNCHECKED_ROLE_DISPLAY_NAME));
            }

            // Administrators virtual role
            if (System.getProperty(InternalConstants.ADMINISTRATORS_LDAP_GROUP_NAME) != null) {
                filteredRoles.add(new FilteredRole(FilteredRole.ADMINISTRATORS_ROLE_NAME, FilteredRole.ADMINISTRATORS_ROLE_DISPLAY_NAME));
            }
        } catch (Exception e) {
            throw new IdentityException("No profile service defined");
        }

        return filteredRoles;
    }


    /**
     * {@inheritDoc}
     * 
     * Roles "Authenticated", "Unchecked" et "Administrators" are added by overloading.
     */
    public Role findRoleByName(String name) throws IdentityException {
        if (SecurityConstants.AUTHENTICATED_ROLE_NAME.equals(name)) {
            return new FilteredRole(SecurityConstants.AUTHENTICATED_ROLE_NAME, FilteredRole.AUTHENTICATED_ROLE_DISPLAY_NAME);
        } else if (FilteredRole.UNCHECKED_ROLE_NAME.equals(name)) {
            return new FilteredRole(FilteredRole.UNCHECKED_ROLE_NAME, FilteredRole.UNCHECKED_ROLE_DISPLAY_NAME);
        } else if (FilteredRole.ADMINISTRATORS_ROLE_NAME.equals(name)) {
            return new FilteredRole(FilteredRole.ADMINISTRATORS_ROLE_NAME, FilteredRole.ADMINISTRATORS_ROLE_NAME);
        } else {
            return super.findRoleByName(name);
        }
    }

}
