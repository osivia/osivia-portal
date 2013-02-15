package org.osivia.portal.core.profils;

import org.jboss.portal.identity.Role;

public class FilteredRole implements Role {

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
