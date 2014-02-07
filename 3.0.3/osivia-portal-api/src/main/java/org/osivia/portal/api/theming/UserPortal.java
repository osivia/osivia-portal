package org.osivia.portal.api.theming;

import java.io.Serializable;
import java.util.List;

public class UserPortal implements Serializable {
	

	private static final long serialVersionUID = -4680072181726328231L;
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private List<UserPage> pages;

	public List<UserPage> getUserPages() {
		return pages;
	
	}

	public void setUserPages(List<UserPage> pages ) {
		this.pages = pages;
	}

}
