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

import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.commons.collections.EnumerationUtils;
import org.jboss.portal.identity.IdentityException;

/**
 * LDAP filtered role module for hierarchical LDAP.
 *
 * @author Cédric Krommenhoek
 * @see LdapFilteredRoleModule
 */
public class HierarchicalLDAPFilteredRoleModule extends LdapFilteredRoleModule {

    /**
     * Default constructor
     */
    public HierarchicalLDAPFilteredRoleModule() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SearchResult> searchRoles(String filter, Object[] filterArgs) throws NamingException, IdentityException {
        LdapContext ldapContext = this.getConnectionContext().createInitialContext();
        NamingEnumeration<SearchResult> results = null;
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            controls.setReturningObjFlag(true);

            filter = filter.replaceAll("\\\\", "\\\\\\\\");

            if (filterArgs == null) {
                results = ldapContext.search(this.getContainerDN(), filter, controls);
            } else {
                results = ldapContext.search(this.getContainerDN(), filter, filterArgs, controls);
            }
            return EnumerationUtils.toList(results);
        } finally {
            if (results != null) {
                results.close();
            }
            ldapContext.close();
        }
    }

}
