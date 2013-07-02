package org.osivia.portal.core.assistantpage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.core.constants.InternalConstants;

/**
 * Command used to switch between cms modes
 * 
 */
public class ChangeCMSEditionModeCommand extends ControllerCommand {

	private static final CommandInfo info = new ActionCommandInfo(false);
	protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

    // private static PortalObjectId adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);

	public CommandInfo getInfo() {
		return info;
	}

	private String pageId;

    /** Display the given version, ex : preview, online, ... */
    private String version;

    /** Display the widgets in the cms windows */
    private String editionMode;

    public String getVersion() {
        return this.version;
	}

    public String getEditionMode() {
        return this.editionMode;
    }


	public String getPageId() {
		return this.pageId;
	}

	public ChangeCMSEditionModeCommand() {
	}

    public ChangeCMSEditionModeCommand(String pageId, String version, String editionMode) {
		this.pageId = pageId;
        this.version = version;
        this.editionMode = editionMode;
	}

	public ControllerResponse execute()  {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

        this.getControllerContext().setAttribute(SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_VERSION, this.version);
        this.getControllerContext().setAttribute(SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_EDITION_MODE, this.editionMode);

		return new UpdatePageResponse(page.getId());

	}

}
