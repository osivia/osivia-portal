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
package org.osivia.portal.core.migration;

import java.util.Collection;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;

/**
 * 
 * renomme tous les pia. en osivia.
 * 
 * @author jeanseb
 *
 */
public class MigrationModule2061 extends MigrationModule {

	@Override
	public int getModuleId() {
		return 2061;
	}

	private static String[] toRename = { "test.delai",

	"draftPage", "liste_styles", "ajaxLoading", "style", "hideTitle", "title", "label", "hideDecorators", "idPerso", "ajaxLink", "hideEmptyPortlet",
			"printPortlet", "conditionalScope", "menuBarPolicy", "unprofiled_home_page", "profils", "cms.scope", "cms.basePath",
			"cms.navigationScope", "cms.pageContextualizationSupport", "cms.incomingContextualizationSupport", "cms.outgoingRecontextualizationSupport", "navigationMode",
			"cms.layoutRules", "rssLinkRef", "permaLinkRef", "cms.layoutRules", "nuxeoPath", "propertyName", "document.onlyDescription",
			"cms.hideMetaDatas", "cms.displayLiveVersion", "fragmentTypeId", "nuxeoRequest", "requestInterpretor", "cms.contextualization", "cms.pageSize", "cms.pageSizeMax", "cms.maxItems", "page_accueil_mode_connecte",
			"cms.style", "rssTitle", "displayNuxeoRequest", "cms.openLevels", "cms.nbLevels", "cms.maxLevels", "selectorId", "libelle", "vocabId","vocabName1", 
			"vocabName2", "vocabName3", "rss.flux", "rss.nbItems", "rss.nbItemsMax"

	};

	private void renameDatas(PortalObject portalObject) {

		for (String propertyName : toRename) {
			//if ("test.delai".equals(propertyName)) {

				String value = portalObject.getDeclaredProperty("pia." + propertyName);
				if (value != null) {
					portalObject.setDeclaredProperty("pia." + propertyName, null);
					portalObject.setDeclaredProperty("osivia." + propertyName, value);

				}
			//}
		}

		// scan children

		Collection<PortalObject> childrens = portalObject.getChildren();
		for (PortalObject po : childrens) {
			renameDatas( po);
		}

	}

	@Override
	public void execute() throws Exception {

		Collection<PortalObject> portals = getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK);

		renameDatas(getPortalObjectContainer().getContext());

	}

}
