package org.osivia.portal.administration.ejb;

public class Profil {
	private String nom="";
	private String url="";
	private String role="";
	private String nuxeoVirtualUser="";

	public String getNuxeoVirtualUser() {
		return nuxeoVirtualUser;
	}

	public void setNuxeoVirtualUser(String nuxeoVirtualUser) {
		this.nuxeoVirtualUser = nuxeoVirtualUser;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	@Override
	public boolean equals(Object obj) {
	    Profil ci = (Profil)obj;
	    if(ci !=null)
	    {
	    	if ( this.nom.equals(ci.getNom()) && this.role.equals(ci.getRole()) && this.url.equals(ci.getUrl()))
	    		return true;
	    	else
	    		return false;
	    }
	    else
	    	return false;
	}

}
