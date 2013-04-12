package org.osivia.portal.core.assistantpage;

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;


public class ChangeCMSEditionModeCommand extends AssistantCommand {

	private String pageId;
	private String mode;

	public String getMode() {
		return mode;
	}

	public String getPageId() {
		return pageId;
	}

	public ChangeCMSEditionModeCommand() {
	}

	public ChangeCMSEditionModeCommand(String pageId, String mode) {
		this.pageId = pageId;
		this.mode = mode;
	}

	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		getControllerContext().setAttribute(SESSION_SCOPE, "osivia.cmsEditionMode", mode);
		

		if( page instanceof CMSTemplatePage)	{
			page = (Page) page.getParent();
		}

		return new UpdatePageResponse(page.getId());

	}

}
