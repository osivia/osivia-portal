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
import java.util.Set;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.db.HibernateRoleModuleImpl;
import org.jboss.portal.security.SecurityConstants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.profils.FilteredRole;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;


/**
 * Ce module permet de filtrer les rôles qui peuvent être présent en grand nombre dans l'annuaire.
 * En plus, le rôle Athenticated est ajouté.
 *
 * @author jeanseb
 */
public class FilteredRoleModule extends HibernateRoleModuleImpl {


    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Role> findRoles() throws IdentityException {
        HashSet<Role> filteredRoles = new HashSet<Role>();

        try {
            IProfilManager profilManager = Locator.findMBean(IProfilManager.class, "osivia:service=ProfilManager");

            // Lrs rôles sont filtrés par rapport aux profils
            for (ProfilBean profil : profilManager.getListeProfils()) {

                try {
                    Role role = super.findRoleByName(profil.getRoleName());
                    filteredRoles.add(role);

                } catch (Exception e) {
                    // le role n'est pas défini : passage au role suivant
                }
            }

            filteredRoles.add(new FilteredRole(SecurityConstants.AUTHENTICATED_ROLE_NAME, FilteredRole.AUTHENTICATED_ROLE_DISPLAY_NAME));

            if (System.getProperty("ldap.groupes_profils_obligatoires") != null) {
                filteredRoles.add(new FilteredRole(FilteredRole.UNCHECKED_ROLE_NAME, FilteredRole.UNCHECKED_ROLE_DISPLAY_NAME));
            }
        } catch (Exception e) {
            throw new IdentityException("No profil service defined");
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
