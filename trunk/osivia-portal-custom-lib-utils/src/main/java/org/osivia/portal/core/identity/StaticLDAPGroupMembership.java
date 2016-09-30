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
