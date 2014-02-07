package org.osivia.portal.core.assistantpage;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.theme.ThemeConstants;

public class AddPortletCommand extends AssistantCommand {

	private String pageId;
	private String regionId;
	private String instanceId;

	public String getPageId() {
		return pageId;
	}

	public AddPortletCommand() {
	}

	public AddPortletCommand(String pageId, String regionId, String portletInstance) {
		this.pageId = pageId;
		this.regionId = regionId;
		this.instanceId = portletInstance;
	}

	public ControllerResponse executeAssistantCommand() throws Exception {
		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		Page page = (Page) getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		// Tri des fenêtes de la région
		SortedMap<Integer, Window> regionWindows = new TreeMap<Integer, Window>();
		for (PortalObject po : (Collection<PortalObject>) page.getChildren(Page.WINDOW_MASK)) {
			Window win = (Window) po;
			if (win.getDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION).equals(regionId)) {
				int order = Integer.parseInt(win.getDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER));
				regionWindows.put(order, win);
			}
		}

		// Ajout du portlet à la fin
		Window window = null;
		window = page.createWindow(instanceId + "_" + String.valueOf(System.currentTimeMillis()), ContentType.PORTLET,
				instanceId);
		if (regionWindows.isEmpty()) {
			window.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER, Integer.toString(0));
		} else {
			window.setDeclaredProperty(ThemeConstants.PORTAL_PROP_ORDER, "" + (regionWindows.lastKey() + 1));
		}
		window.setDeclaredProperty(ThemeConstants.PORTAL_PROP_REGION, regionId);

		return new UpdatePageResponse(page.getId());
	}

}
