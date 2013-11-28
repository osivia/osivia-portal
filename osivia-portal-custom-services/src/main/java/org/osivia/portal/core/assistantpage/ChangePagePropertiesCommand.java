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

public class ChangePagePropertiesCommand extends AssistantCommand {

	private String pageId;
	private String draftPage;
	private String category;


	public String getPageId() {
		return pageId;
	}

	public ChangePagePropertiesCommand() {
	}

	public ChangePagePropertiesCommand(String pageId, String draftPage, String category) {
		this.pageId = pageId;
		this.draftPage = draftPage;
		this.category = category;
	}

	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		if ("1".equals(draftPage))
			page.setDeclaredProperty("osivia.draftPage", "1");
		else if (page.getDeclaredProperty("osivia.draftPage") != null)
			page.setDeclaredProperty("osivia.draftPage", null);
		
		// v2.0.22 : catégories
		if( System.getProperty("page.category.prefix") != null)	{
			if (category != null && category.length() != 0) {
				page.setDeclaredProperty("osivia.pageCategory", category);
			} else {
				page.setDeclaredProperty("osivia.pageCategory", null);
			}
		}

		return new UpdatePageResponse(page.getId());

	}

}
