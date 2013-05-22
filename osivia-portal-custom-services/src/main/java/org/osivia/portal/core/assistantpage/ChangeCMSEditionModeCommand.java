package org.osivia.portal.core.assistantpage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;


public class ChangeCMSEditionModeCommand extends ControllerCommand {
	
	private static final CommandInfo info = new ActionCommandInfo(false);
	protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

	private static PortalObjectId adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);

	public CommandInfo getInfo() {
		return info;
	}

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

	public ControllerResponse execute()  {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		getControllerContext().setAttribute(SESSION_SCOPE, "osivia.cmsEditionMode", mode);
		
		return new UpdatePageResponse(page.getId());

	}

}
