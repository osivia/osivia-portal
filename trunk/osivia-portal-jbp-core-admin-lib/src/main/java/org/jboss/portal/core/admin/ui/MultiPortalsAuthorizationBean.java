package org.jboss.portal.core.admin.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.portal.identity.IdentityException;
import org.jboss.portal.identity.Role;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.SecurityConstants;
import org.osivia.portal.core.page.PageProperties;

/**
 * 
 * OSIVIA : Mise en place du multi-portails
 * 
 * @author jeanseb
 * 
 */
public abstract class MultiPortalsAuthorizationBean extends AuthorizationBean {

	public String getManagedPortalName() {
		return null;
	}

	public Set findPortalRoles() throws IdentityException {

		String savedPortal = null;
		try {

			String portalName = getManagedPortalName();
			if (portalName != null) {

				savedPortal = PageProperties.getProperties().getPagePropertiesMap().get("portalName");

				// Positionnement du portail courant pour les filtres
				PageProperties.getProperties().getPagePropertiesMap().put("portalName", portalName);
			}

			return getRoleModule().findRoles();
		} finally {
			if (savedPortal != null)
				PageProperties.getProperties().getPagePropertiesMap().put("portalName", savedPortal);
		}

	}

	public String[] getRoles() {
		SortedSet roleNames = new TreeSet();

		// Get role names from URI
		String uri = getURI();
		if (uri != null) {
			Set constraints = getDomainConfigurator().getSecurityBindings(uri);
			if (constraints != null) {
				for (Iterator i = constraints.iterator(); i.hasNext();) {
					RoleSecurityBinding binding = (RoleSecurityBinding) i.next();
					roleNames.add(binding.getRoleName());
				}
			}
		}

		// Get other roles from role module
		try {
			roleNames.add(SecurityConstants.UNCHECKED_ROLE_NAME);
			for (Iterator i = findPortalRoles().iterator(); i.hasNext();) {
				Role role = (Role) i.next();
				roleNames.add(role.getName());
			}
		} catch (IdentityException e) {
			e.printStackTrace();
		}

		//
		return (String[]) roleNames.toArray(new String[roleNames.size()]);
	}

	public Map getRoleDisplayNameMap() {
		try {
			Map map = new HashMap();
			for (Iterator i = findPortalRoles().iterator(); i.hasNext();) {
				Role role = (Role) i.next();
				String displayName = role.getDisplayName();
				if (displayName != null) {
					String name = role.getName();
					map.put(name, displayName);
				}
			}
			return map;
		} catch (IdentityException e) {
			return Collections.EMPTY_MAP;
		}
	}

}
