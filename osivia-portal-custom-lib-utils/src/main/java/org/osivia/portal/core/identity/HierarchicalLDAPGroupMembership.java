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
package org.osivia.portal.core.identity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.identity.CachedUserImpl;
import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.NoSuchUserException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.ldap.LDAPUserImpl;

/**
 * LDAP static group membership module super-implementation for hierarchical LDAP.
 * 
 * @author Cédric Krommenhoek
 * @see AbstractLDAPGroupMembership
 */
public class HierarchicalLDAPGroupMembership extends AbstractLDAPGroupMembership {

    /** Logger. */
    private static final Log logger = LogFactory.getLog(HierarchicalLDAPGroupMembership.class);


    /**
     * Default constructor.
     */
    public HierarchicalLDAPGroupMembership() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Set<Role> searchRolesFromLDAP(User user) throws IdentityException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user instanceof CachedUserImpl) {
            try {
                user = this.getUserModule().findUserById(user.getId());
            } catch (NoSuchUserException e) {
                throw new IdentityException("Illegal state - cached user doesn't exist in identity store: ", e);
            }
        }

        LDAPUserImpl ldapUser = null;
        if (user instanceof LDAPUserImpl) {
            ldapUser = (LDAPUserImpl) user;
        } else {
            throw new IllegalArgumentException("UserMembershipModuleImpl supports only LDAPUserImpl objects");
        }

        Set<Role> roles = new HashSet<Role>();
        try {
            String memberName;
            if (this.isUidAttributeIsDN()) {
                memberName = ldapUser.getDn();
            } else {
                memberName = ldapUser.getUserName();
            }

            String filter = this.getMemberAttributeID().concat("=").concat(memberName);
            List<SearchResult> results = this.getRoleModule().searchRoles(filter, null);
            for (SearchResult result : results) {
                this.addResultHierarchy(roles, result);
            }
        } catch (Exception e) {
            // Do nothing
        }

        return roles;
    }


    /**
     * Utility method used to add result hierarchy to roles.
     *
     * @param roles roles
     * @param resultHierarchy result hierarchy
     * @throws IdentityException
     * @throws NamingException
     */
    @SuppressWarnings("unchecked")
    private void addResultHierarchy(Set<Role> roles, SearchResult resultHierarchy) throws IdentityException, NamingException {
        String[] names = StringUtils.split(resultHierarchy.getName(), ",");
        for (String name : names) {
            List<SearchResult> results = this.getRoleModule().searchRoles(name, null);

            // LDAP request must return a single result
            if (results.size() == 1) {
                SearchResult result = results.get(0);
                DirContext dirContext = (DirContext) result.getObject();
                Role role = this.getRoleModule().createRoleInstance(result.getAttributes(), dirContext.getNameInNamespace());
                roles.add(role);
                dirContext.close();
            } else {
                logger.warn("LDAP request must return a single result for filter '" + name + "'.");
            }
        }
    }

}
