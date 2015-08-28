/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.assistantpage;

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;

/**
 * Change mode command.
 *
 * @see AssistantCommand
 */
public class ChangeModeCommand extends AssistantCommand {

    /** Page identifier. */
	private String pageId;
    /** Mode. */
	private String mode;


    /**
     * Default constructor.
     */
	public ChangeModeCommand() {
        super();
	}

    /**
     * Constructor using fields.
     *
     * @param pageId page identifier
     * @param mode mode
     */
	public ChangeModeCommand(String pageId, String mode) {
		this.pageId = pageId;
		this.mode = mode;
	}


    /**
     * {@inheritDoc}
     */
    public ControllerResponse executeAssistantCommand() throws Exception {
        // Récupération page
        PortalObjectId portalObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        PortalObject page = this.getControllerContext().getController().getPortalObjectContainer().getObject(portalObjectId);

        this.getControllerContext().setAttribute(SESSION_SCOPE, InternalConstants.ATTR_WINDOWS_SETTING_MODE, this.mode);


        if (page instanceof CMSTemplatePage) {
            page = page.getParent();
        }

        return new UpdatePageResponse(page.getId());
    }


    /**
     * Getter for pageId.
     *
     * @return the pageId
     */
    public String getPageId() {
        return this.pageId;
    }

    /**
     * Getter for mode.
     *
     * @return the mode
     */
    public String getMode() {
        return this.mode;
    }

}
