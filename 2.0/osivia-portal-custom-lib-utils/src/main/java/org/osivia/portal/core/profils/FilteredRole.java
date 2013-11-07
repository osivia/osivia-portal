package org.osivia.portal.core.profils;

import org.jboss.portal.identity.Role;

public class FilteredRole implements Role {
	
	public static final java.lang.String AUCUN_PROFIL_ROLE_NAME = "role-aucun-profil";	
	public static final java.lang.String AUCUN_PROFIL_ROLE_DISPLAY_NAME = "Utilisateurs sans profil";	
	public static final java.lang.String AUTHENTICATED_ROLE_DISPLAY_NAME = "Utilisateurs connectés";
	

	private String name;
	private String displayName;

	public FilteredRole(String name, String displayName) {
		super();
		this.name = name;
		this.displayName = displayName;
	}

	public Object getId() {
		return name;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		
	}

	
}
