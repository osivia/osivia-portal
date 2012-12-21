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

public class ChangePageCMSCommand extends AssistantCommand {

	private String pageId;
	private String category;
	private String contentType;

	public String getPageId() {
		return pageId;
	}

	public ChangePageCMSCommand() {
	}

	public ChangePageCMSCommand(String pageId, String contentType, String category) {
		this.pageId = pageId;
		this.contentType = contentType;
		this.category = category;
	}

	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		if (contentType != null && contentType.length() != 0) {
			page.setDeclaredProperty("pia.cms.contentType", contentType);
		} else {
			page.setDeclaredProperty("pia.cms.contentType", null);
		}
		
		if (category != null && category.length() != 0) {
			page.setDeclaredProperty("pia.cms.category", category);
		} else {
			page.setDeclaredProperty("pia.cms.category", null);
		}


		return new UpdatePageResponse(page.getId());

	}

}
