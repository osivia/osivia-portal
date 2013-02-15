package org.osivia.portal.core.profils;

import java.util.List;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.identity.Role;

public interface IProfilManager {
	
	/* Pour lecture et mise à jour depuis l'admin */
	public  List<ProfilBean> getListeProfils( Portal portal);
	public void setListeProfils( Portal portal, List<ProfilBean> profils)  ;
	
	/* Pour utilisation */
	public List<Role> getFilteredRoles( ) ;
	public ProfilBean getProfilPrincipalUtilisateur();
	public ProfilBean getProfil(String name);
	public boolean verifierProfilUtilisateur(String name);
	public  List<ProfilBean> getListeProfils( );

}
