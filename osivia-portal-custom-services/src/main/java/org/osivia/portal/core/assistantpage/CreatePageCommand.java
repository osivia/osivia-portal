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
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.constants.InternationalizationConstants;
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
    /** Parent page identifier. */
    private String parentPageId;
    /** Model identifier. */
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
     * @param name page name
     * @param parentPageId parent page identifier
     * @param modelId model identifier
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
        // Get bundle
        Locale locale = this.getControllerContext().getServerInvocation().getRequest().getLocale();
        Bundle bundle = this.getBundleFactory().getBundle(locale);

        // Get parent
        PortalObjectId portalObjectId = PortalObjectId.parse(this.parentPageId, PortalObjectPath.SAFEST_FORMAT);
        PageContainer parent = (PageContainer) this.getControllerContext().getController().getPortalObjectContainer().getObject(portalObjectId);

        // Notification properties
        String key;
        if (PortalObjectUtils.isTemplate(parent)) {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CREATE_PAGE_COMMAND_TEMPLATE;
        } else {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CREATE_PAGE_COMMAND_PAGE;
        }

        Page newPage;
        if (StringUtils.isNotEmpty(this.modelId)) {
            // Update from model
            PortalObjectId modelPortalObjectId = PortalObjectId.parse(this.modelId, PortalObjectPath.SAFEST_FORMAT);
            Page model = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(modelPortalObjectId);

            // Model should not be parent of the new page
            if ((model.equals(parent)) || PortalObjectUtils.isAncestor(model, parent)) {
                String message = bundle.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_CREATE_PAGE_COMMAND_PARENT_MODEL);
                throw new UserNotificationsException(message);
            } else {
                model.copy(parent, this.name, true);
                newPage = (Page) parent.getChild(this.name);
            }
        } else {
            newPage = parent.createPage(this.name);
        }

        // Name initialization
        Map<Locale, String> displayMap = createLocalizedStringMap(locale, null, this.name);
        LocalizedString localizedString = new LocalizedString(displayMap, Locale.ENGLISH);
        newPage.setDisplayName(localizedString);

        // Impact on header cache
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();

        // Notification
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.getControllerContext());
        String message = bundle.getString(key, this.name);
        NotificationsUtils.getNotificationsService().addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);

        return new UpdatePageResponse(newPage.getId());
    }

}
