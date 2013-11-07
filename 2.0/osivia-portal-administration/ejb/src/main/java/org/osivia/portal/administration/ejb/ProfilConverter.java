package org.osivia.portal.administration.ejb;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("profilConverter")
@Scope(ScopeType.SESSION)
public class ProfilConverter implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		String [] str = arg2.split(":");
		Profil p =new Profil();
		if( str.length > 0)
			p.setNom(str[0]);
		if( str.length > 1)
			p.setRole(str[1]);
		if( str.length > 2)
			p.setUrl(str[2]);
		if( str.length > 3)
			p.setNuxeoVirtualUser(str[3]);
		return p;
	}
	
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		Profil p = (Profil)arg2;
		return p.getNom() + ":" + p.getRole()  + ":" + p.getUrl() + ":" +  p.getNuxeoVirtualUser();
	}

}