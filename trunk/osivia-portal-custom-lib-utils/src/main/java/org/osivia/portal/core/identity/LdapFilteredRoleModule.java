package org.osivia.portal.core.identity;

import java.util.HashSet;
import java.util.Set;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.ldap.LDAPRoleModuleImpl;
import org.jboss.portal.security.SecurityConstants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.profils.FilteredRole;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;


public class LdapFilteredRoleModule extends LDAPRoleModuleImpl {
	
	public Set findRoles() throws IdentityException {

		HashSet<Role> filteredRoles = new HashSet<Role>();

		try {
			IProfilManager profilManager = Locator.findMBean(IProfilManager.class, "osivia:service=ProfilManager");

			// Lrs rôles sont filtrés par rapport aux profils
			for (ProfilBean profil : profilManager.getListeProfils()) {

				try {
					Role role = findRoleByName(profil.getRoleName());
					filteredRoles.add(role);

				} catch (Exception e) {
					// le role n'est pas défini : passage au role suivant
				}
			}

			//filteredRoles.add(new FilteredRole(SecurityConstants.AUTHENTICATED_ROLE_NAME, FilteredRole.AUTHENTICATED_ROLE_DISPLAY_NAME));
				
			if( System.getProperty("ldap.groupes_profils_obligatoires") != null)			
				filteredRoles.add(new FilteredRole(FilteredRole.AUCUN_PROFIL_ROLE_NAME,FilteredRole.AUCUN_PROFIL_ROLE_DISPLAY_NAME));


		} catch (Exception e) {
			throw new IdentityException("No profil service defined");
		}

		return filteredRoles;

	}
	
	
	/* Le role Authenticated est ajouté par surcharge
	 * Sans cela, les portlets de back-office JBoss Portal plantent
	 * @see org.jboss.portal.identity.db.HibernateRoleModuleImpl#findRoleByName(java.lang.String)
	 */
	public Role findRoleByName(String name) throws IdentityException {
		if (SecurityConstants.AUTHENTICATED_ROLE_NAME.equals(name))
			return new FilteredRole(SecurityConstants.AUTHENTICATED_ROLE_NAME, FilteredRole.AUTHENTICATED_ROLE_DISPLAY_NAME);
		else 
			if ("role-aucun-profil".equals(name))
			return new FilteredRole(FilteredRole.AUCUN_PROFIL_ROLE_NAME, FilteredRole.AUCUN_PROFIL_ROLE_DISPLAY_NAME);
		else
			return super.findRoleByName(name);

	}

}
