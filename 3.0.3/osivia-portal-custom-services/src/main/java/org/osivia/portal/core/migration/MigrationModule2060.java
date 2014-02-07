package org.osivia.portal.core.migration;

import java.util.Collection;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;

public class MigrationModule2060 extends MigrationModule {

	@Override
	public int getModuleId() {
		return 2060;
	}

	private void removeUnusedScope(PortalObject portalObject) {

		// scan windows
		Collection<PortalObject> windows = portalObject.getChildren(PortalObject.WINDOW_MASK);

		for (PortalObject po : windows) {
			if (po instanceof Window) {
				Window window = (Window) po;

				if (window.getContent() != null) {

					if ("toutatice-portail-cms-nuxeo-viewDocumentPortletInstance".equals(window.getContent().getURI())) {
						if (window.getDeclaredProperty("pia.cms.scope") != null)
							window.setDeclaredProperty("pia.cms.scope", null);
					}
				}
			}
		}
		// scan sub pages

		Collection<PortalObject> subPages = portalObject.getChildren(PortalObject.PAGE_MASK);
		for (PortalObject subPage : subPages) {
			removeUnusedScope((Page) subPage);

		}

	}

	@Override
	public void execute() throws Exception {

		// Set all portal renderers to OSIVIARenderer

		Collection<PortalObject> portals = getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK);

		for (PortalObject po : portals) {
			if (!"template".equals(po.getName()) && !"admin".equals(po.getName())) {
				po.setDeclaredProperty(ThemeConstants.PORTAL_PROP_RENDERSET, "OsiviaDefaultRenderer");
			}
		}

		// Delete all toutatice2 page

		Portal adminPortal = getPortalObjectContainer().getContext().getPortal("admin");

		if (adminPortal.getPage("toutatice2") != null) {
			adminPortal.destroyChild("toutatice2");
		}

		// Delete scope associated with ViewDocumentPortlet

		for (PortalObject po : portals) {
			removeUnusedScope(po);

		}

	}

}
