package org.osivia.portal.core.identity;

import java.util.Set;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.User;


public class StaticLDAPGroupMembership extends AbstractLDAPGroupMembership {

    @Override
    protected Set<Role> searchRolesFromLDAP(User user) throws IdentityException {
        return super.getRoles(user);
    }

}
