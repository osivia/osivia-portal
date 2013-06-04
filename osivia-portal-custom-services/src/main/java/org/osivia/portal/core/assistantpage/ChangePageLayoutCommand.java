package org.osivia.portal.core.assistantpage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.theme.ThemeConstants;

public class ChangePageLayoutCommand extends AssistantCommand {

	private String pageId;
	private String layout;

	public String getPageId() {
		return pageId;
	}

	public ChangePageLayoutCommand() {
	    super();
	}

	public ChangePageLayoutCommand(String pageId, String layout) {
		this.pageId = pageId;
		this.layout = layout;
	}

	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		if (layout != null && layout.length() != 0) {
			page.setDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT, layout);
		} else {
			page.setDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT, null);
		}

		return new UpdatePageResponse(page.getId());

	}

}
