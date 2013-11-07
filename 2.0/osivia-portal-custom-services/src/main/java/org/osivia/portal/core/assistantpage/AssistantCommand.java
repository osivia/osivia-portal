package org.osivia.portal.core.assistantpage;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;


public abstract class AssistantCommand extends ControllerCommand {

	private static final CommandInfo info = new ActionCommandInfo(false);
	protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

	private static PortalObjectId adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);

	public AssistantCommand() {
	}

	public CommandInfo getInfo() {
		return info;
	}

	protected abstract ControllerResponse executeAssistantCommand() throws Exception;

	public ControllerResponse execute() throws ControllerException {
		
	

		try {
			// Contrôle droits
			PortalObjectPermission perm = new PortalObjectPermission(adminPortalId, PortalObjectPermission.VIEW_MASK);
			if (!getControllerContext().getController().getPortalAuthorizationManagerFactory().getManager()
					.checkPermission(perm))
				throw new SecurityException("Commande interdite");
			
			ControllerResponse res =  executeAssistantCommand();
			
		
			if( res instanceof UpdatePageResponse){
				// On transforme en redirction pour commiter la transaction
				// pour que les threads associés aux windows
				// voient les données directement des l'affichage de la page
				
				
				
				PageURL url = new PageURL( ((UpdatePageResponse)res).getPageId(), getControllerContext());
				res =  new RedirectionResponse(url.toString() + "?init-state=true");
				//res =  new RedirectionResponse(url.toString() );

			}

			return res;
				

		} catch (Exception e) {
			if (!(e instanceof ControllerException))
				throw new ControllerException(e);
			else
				throw (ControllerException) e;
		} 
	}

}
