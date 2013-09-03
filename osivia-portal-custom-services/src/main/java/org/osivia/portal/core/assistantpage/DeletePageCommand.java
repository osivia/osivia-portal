package org.osivia.portal.core.assistantpage;

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.notifications.NotificationsUtils;


public class DeletePageCommand extends AssistantCommand {

    private String pageId;

    public String getPageId() {
        return this.pageId;
    }

    public DeletePageCommand() {
    }

    public DeletePageCommand(String pageId) {
        this.pageId = pageId;
    }

    public ControllerResponse executeAssistantCommand() throws Exception {

        // Récupération page
        PortalObjectId poid = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        PortalObject page = this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);
        PortalObject parent = page.getParent();

        // Destruction window
        parent.destroyChild(page.getName());

        // Redirection vers le parent, ou par défaut
        // vers la page par défaut du portail
        Page redirectPage = null;
        if (parent instanceof Page) {
            redirectPage = (Page) parent;
        } else if (parent instanceof Portal) {
            redirectPage = ((Portal) parent).getDefaultPage();
        }

        // Impact sur les caches du bandeau
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();

        // Notification
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.getControllerContext());
        NotificationsUtils.getNotificationsService().addSimpleNotification(portalControllerContext, "La page a bien été supprimée.",
                NotificationsType.SUCCESS);

        return new UpdatePageResponse(redirectPage.getId());

    }

}
