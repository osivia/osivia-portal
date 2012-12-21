package org.osivia.portal.core.assistantpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.page.PageUtils;


public class MovePageCommand extends AssistantCommand {

	private String pageId;
	private String destinationPageId;

	public MovePageCommand() {
	}

	public MovePageCommand(String pageId, String destinationPageId) {
		this.pageId = pageId;
		this.destinationPageId = destinationPageId;
	}

	public ControllerResponse executeAssistantCommand() throws Exception {
		// Récupération pages

		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		PortalObject destPage = null;
		if (!"0".equals(destinationPageId)) {
			PortalObjectId destPoid = PortalObjectId.parse(destinationPageId, PortalObjectPath.SAFEST_FORMAT);
			destPage = getControllerContext().getController().getPortalObjectContainer().getObject(destPoid);
		}

		/* Mémorisation et tri des pages */

		SortedSet<Page> pages = new TreeSet<Page>(PageUtils.orderComparator);
		for (PortalObject po : (Collection<PortalObject>) page.getParent().getChildren(PortalObject.PAGE_MASK)) {

			Page sister = (Page) po;
			if (!sister.equals(page)) {
				pages.add(sister);
			}
		}

		List<Page> pagesTriees = new ArrayList<Page>(pages);

		/* Remplacement de l'ordre de la page courante */

		int destOrder = 0;
		if (destPage != null) {
			if (destPage.getDeclaredProperty(PageUtils.TAB_ORDER) != null)
				destOrder = Integer.parseInt(destPage.getDeclaredProperty(PageUtils.TAB_ORDER));
		} else {
			// Mise en dernière position
			Page lastPage = pagesTriees.get(pagesTriees.size() - 1);
			if (lastPage.getDeclaredProperty(PageUtils.TAB_ORDER) != null)
				destOrder = Integer.parseInt(lastPage.getDeclaredProperty(PageUtils.TAB_ORDER)) + 1;
			else
				destOrder = 1;
		}

		page.setDeclaredProperty(PageUtils.TAB_ORDER, Integer.toString(destOrder));

		/* Remplacement de l'ordre des pages suivantes */

		boolean modifierOrdre = false;
		for (Page curPage : pagesTriees) {
			if (curPage.equals(destPage)) {
				modifierOrdre = true;
			}

			if (modifierOrdre)
				curPage.setDeclaredProperty(PageUtils.TAB_ORDER, Integer.toString(++destOrder));
		}

		
		//Impact sur les caches du bandeau
		ICacheService cacheService =  Locator.findMBean(ICacheService.class,"pia:service=Cache");
		cacheService.incrementHeaderCount();

		
		return new UpdatePageResponse(page.getId());

	}

}
