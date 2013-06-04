package org.osivia.portal.core.assistantpage;

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;


public class DeletePageCommand extends AssistantCommand {

    private String pageId;

    public String getPageId() {
        return pageId;
    }

    public DeletePageCommand() {
    }

    public DeletePageCommand(String pageId) {
        this.pageId = pageId;
    }

    public ControllerResponse executeAssistantCommand() throws Exception {

        // Récupération page
        PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
        PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);
        PortalObject parent = page.getParent();

        // Destruction window
        parent.destroyChild(page.getName());

        // Redirection vers le parent, ou par défaut
        // vers la page par défaut du portail
        Page redirectPage = null;
        if (parent instanceof Page)
            redirectPage = (Page) parent;
        else if (parent instanceof Portal)
            redirectPage = (Page) ((Portal) parent).getDefaultPage();

        // Impact sur les caches du bandeau
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();


        return new UpdatePageResponse(redirectPage.getId());

    }

}
