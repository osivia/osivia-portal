package org.osivia.portal.core.assistantpage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityServiceController;
import org.jboss.portal.identity.Role;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.profils.IProfilManager;


public class SecurePageCommand extends AssistantCommand {

	private String pageId;
	private List<String> viewActions;

	public String getPageId() {
		return pageId;
	}

	public SecurePageCommand() {
	}

	public SecurePageCommand(String pageId, List<String> viewActions) {
		this.pageId = pageId;
		this.viewActions = viewActions;
	}

	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		DomainConfigurator dc = getControllerContext().getController().getPortalObjectContainer()
				.getAuthorizationDomain().getConfigurator();

		// Recnosntruction des contraintes de la page

		Set<RoleSecurityBinding> newConstraints = new HashSet<RoleSecurityBinding>();
		Set<RoleSecurityBinding> oldConstraints = dc.getSecurityBindings(page.getId().toString(
				PortalObjectPath.CANONICAL_FORMAT));

		IdentityServiceController identityService = Locator.findMBean(IdentityServiceController.class,
				"portal:service=Module,type=IdentityServiceController");

		RoleModule roles = (RoleModule) identityService.getIdentityContext()
				.getObject(IdentityContext.TYPE_ROLE_MODULE);
		
		IProfilManager profilManager = Locator.findMBean(IProfilManager.class,	"osivia:service=ProfilManager");
		
		// On remonte jusqu'au portail
		PortalObject parent = page.getParent();
		while (parent instanceof Page)	{
			parent = parent.getParent();
		}
		


		for (Role role : profilManager.getFilteredRoles()) {

			RoleSecurityBinding sb = null;
			Set<String> secureAction = new HashSet<String>();

			// Récupération des anciens droits pour le rôle (pour ne pas écraser
			// les autres droits que view)
			for (RoleSecurityBinding sbItem : oldConstraints) {
				if (sbItem.getRoleName().equals(role.getName())) {
					for (Object action : sbItem.getActions()) {
						secureAction.add(action.toString());
					}
				}
			}

			// Mise à jour de l'action VIEW
			secureAction.remove(PortalObjectPermission.VIEW_ACTION);
			if (viewActions.contains(role.getName())) {
				secureAction.add(PortalObjectPermission.VIEW_ACTION);
			}

			newConstraints.add(new RoleSecurityBinding(secureAction, role.getName()));
		}

		dc.setSecurityBindings(page.getId().toString(PortalObjectPath.CANONICAL_FORMAT), newConstraints);

		//Impact sur les caches du bandeau
		ICacheService cacheService =  Locator.findMBean(ICacheService.class,"osivia:service=Cache");
		cacheService.incrementHeaderCount();

		
		return new UpdatePageResponse(page.getId());

	}

}
