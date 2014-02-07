package org.osivia.portal.core.assistantpage;


import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;

public class DeleteWindowCommand extends AssistantCommand {


	private String windowId;

	public String getWindowId() {
		return windowId;
	}

	public DeleteWindowCommand() {
	}

	public DeleteWindowCommand(String windowId) {
		this.windowId = windowId;
	}


	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération window
		PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject window = getControllerContext().getController().getPortalObjectContainer().getObject(poid);
		PortalObject page = window.getParent();

		// Destruction window

		page.destroyChild(window.getName());

		return new UpdatePageResponse(page.getId());

	}

}
