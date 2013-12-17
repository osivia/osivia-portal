package org.osivia.portal.core.assistantpage;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.NoSuchResourceException;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;

public class MakeDefaultPageCommand extends AssistantCommand {

	private String pageId;

	public String getPageId() {
		return pageId;
	}

	public MakeDefaultPageCommand() {
	}

	public MakeDefaultPageCommand(String pageId) {
		this.pageId = pageId;
	}

	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);
		
		
		Portal portal = ((Page) page).getPortal();
		portal.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, page.getName());
		return new UpdatePageResponse(page.getId());

	}

}
