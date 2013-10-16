package org.osivia.portal.core.identity;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.ldap.LDAPStaticGroupMembershipModuleImpl;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.profils.FilteredRole;

/**
 * Abstract LDAP static group membership module for virtual groups handling.
 *
 * @author Cédric Krommenhoek
 * @see LDAPStaticGroupMembershipModuleImpl
 */
public abstract class AbstractLDAPGroupMembership extends LDAPStaticGroupMembershipModuleImpl {

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Role> getRoles(User user) throws IdentityException {
        Set<Role> roles = this.searchRolesFromLDAP(user);

        // Remove administrator virtual role and add it if user has default administrator role
        String virtualAdminRoleName = System.getProperty(InternalConstants.ADMINISTRATORS_LDAP_GROUP_NAME);
        if (StringUtils.isNotBlank(virtualAdminRoleName)) {
            Role virtualAdminRole = null;
            Role adminRole = null;
            for (Role role : roles) {
                if (FilteredRole.ADMINISTRATORS_ROLE_NAME.equals(role.getName())) {
                    // User roles contains virtual administrator role
                    adminRole = role;
                }
                if (virtualAdminRoleName.equals(role.getName())) {
                    // User roles contains administrator role name
                    virtualAdminRole = role;
                }
            }

            if (virtualAdminRole != null) {
                if ((adminRole == null) || !FilteredRole.ADMINISTRATORS_ROLE_NAME.equals(virtualAdminRoleName)) {
                    // Add administrator role
                    roles.add(this.getRoleModule().findRoleByName(FilteredRole.ADMINISTRATORS_ROLE_NAME));
                }
            } else if (adminRole != null) {
                // Remove administrator role
                roles.remove(adminRole);
            }
        }

        return roles;
    }


    /**
     * Search roles from LDAP user groups.
     *
     * @param user LDAP user
     * @return roles
     * @throws IdentityException
     */
    protected abstract Set<Role> searchRolesFromLDAP(User user) throws IdentityException;

}
