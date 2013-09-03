package org.osivia.portal.core.assistantpage;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PageContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.error.UserNotificationsException;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Create page command.
 *
 * @see AssistantCommand
 */
public class CreatePageCommand extends AssistantCommand {

    /** Page name. */
    private String name;
    /** Parent page ID. */
    private String parentPageId;
    /** Model ID. */
    private String modelId;


    /**
     * Default constructor.
     */
    public CreatePageCommand() {
        super();
    }

    /**
     * Constructor.
     *
     * @param name
     * @param parentPageId
     * @param modelId
     */
    public CreatePageCommand(String name, String parentPageId, String modelId) {
        super();
        this.name = name;
        this.parentPageId = parentPageId;
        this.modelId = modelId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse executeAssistantCommand() throws Exception {
        // Récupération de la page parent
        PortalObjectId portalObjectId = PortalObjectId.parse(this.parentPageId, PortalObjectPath.SAFEST_FORMAT);
        PageContainer parent = (PageContainer) this.getControllerContext().getController().getPortalObjectContainer().getObject(portalObjectId);

        Page newPage;
        // Mise à jour d'après le modèle
        if (StringUtils.isNotEmpty(this.modelId)) {
            PortalObjectId poModeleId = PortalObjectId.parse(this.modelId, PortalObjectPath.SAFEST_FORMAT);
            Page modele = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(poModeleId);

            // Le modèle ne doit pas être parent de la nouvelle page
            if ((modele.equals(parent)) || PortalObjectUtils.isAncestor(modele, parent)) {
                //TODO : internationaliser
                throw new UserNotificationsException("Le modèle ne doit pas être parent de la nouvelle page");
            } else {
                modele.copy(parent, this.name, true);
                newPage = (Page) parent.getChild(this.name);
            }
        } else {
            newPage = parent.createPage(this.name);
        }

        // Initialisation du nom
        Locale locale = this.getControllerContext().getServerInvocation().getRequest().getLocale();
        Map<Locale, String> displayMap = createLocalizedStringMap(locale, null, this.name);
        LocalizedString localizedString = new LocalizedString(displayMap, Locale.ENGLISH);
        newPage.setDisplayName(localizedString);

        // Impact sur les caches du bandeau
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();

        //TODO : internationaliser
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.getControllerContext());
        NotificationsUtils.getNotificationsService().addSimpleNotification(portalControllerContext, "La page a été créée", NotificationsType.SUCCESS);

        return new UpdatePageResponse(newPage.getId());
    }

}
