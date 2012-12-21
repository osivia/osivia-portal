package org.osivia.portal.core.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.controller.command.response.SignOutResponse;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.profils.ProfilBean;
import org.osivia.portal.core.profils.ProfilManager;


public class MonEspaceCommand extends ControllerCommand {

	/** . */
	private static final CommandInfo info = new ActionCommandInfo(false);
	protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

	/** . */
	private String portalName;

	public MonEspaceCommand() {
		this(null);
	}

	public MonEspaceCommand(String portalName) {
		this.portalName = portalName;
	}

	public CommandInfo getInfo() {
		return info;
	}

	public String getPortalName() {
		return portalName;
	}

	public ControllerResponse execute() throws ControllerException {
		try {

			Portal portal = null;

			String portalName = getPortalName();

			if (portalName != null)
				portal = getControllerContext().getController().getPortalObjectContainer().getContext().getPortal(
						portalName);
			else
				portal = getControllerContext().getController().getPortalObjectContainer().getContext()
						.getDefaultPortal();

			IProfilManager profilManager = Locator.findMBean(IProfilManager.class, "pia:service=ProfilManager");

			/* Calcul de la page d'accueil personnalisée */


			
			ProfilBean profil = profilManager.getProfilPrincipalUtilisateur();

			if (profil == null) 
				return new SecurityErrorResponse("Vous devez être connecté", SecurityErrorResponse.NOT_AUTHORIZED,
						false);
				

			// Accès page profil
			if( profil.getDefaultPageName().length() == 0){
				// Pas de profil
				PageURL url = new PageURL(portal.getId(), getControllerContext());
				return new RedirectionResponse(url.toString()+ "?init-state=true");
			}
			
			PortalObject child = portal.getChild(profil.getDefaultPageName());
			
			

			if (child != null) {
				PageURL url = new PageURL(child.getId(), getControllerContext());

				logger.debug("Redirection page : " + url.toString());
				// Redirection
				return new RedirectionResponse(url.toString() + "?init-state=true");
			} else {
				// Page inexistante, on redirige vers la page par defaut du
				// portail

				logger.error(" Page : " + profil.getDefaultPageName() + "inexistante");
				PageURL url = new PageURL(portal.getId(), getControllerContext());
				return new RedirectionResponse(url.toString()+ "?init-state=true");
			}

		} catch (Exception e) {
			if (!(e instanceof ControllerException))
				throw new ControllerException(e);
			else
				throw (ControllerException) e;
		}

	}
}
