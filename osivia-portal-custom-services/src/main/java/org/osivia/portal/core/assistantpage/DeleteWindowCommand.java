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
