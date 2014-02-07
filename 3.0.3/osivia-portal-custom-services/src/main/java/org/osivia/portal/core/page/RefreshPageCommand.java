package org.osivia.portal.core.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;


public class RefreshPageCommand extends ControllerCommand {

	private String pageId;
	private static final CommandInfo info = new ActionCommandInfo(false);

	public CommandInfo getInfo() {
		return info;
	}


	public String getPageId() {
		return pageId;
	}

	public RefreshPageCommand() {
	}

	public RefreshPageCommand(String pageId) {
		this.pageId = pageId;
		}

	public ControllerResponse execute() throws ControllerException {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		PageProperties.getProperties().setRefreshingPage(true);
		//getControllerContext().setAttribute(REQUEST_SCOPE, "osivia.refreshPage", "1");

		return new UpdatePageResponse(page.getId());

	}

}
