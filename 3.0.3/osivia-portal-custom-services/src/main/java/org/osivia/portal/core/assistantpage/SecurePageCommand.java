package org.osivia.portal.core.assistantpage;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.identity.Role;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.profils.IProfilManager;

/**
 * Secure page command.
 *
 * @see AssistantCommand
 */
public class SecurePageCommand extends AssistantCommand {

    /** Page identifier. */
    private final String pageId;
    /** View actions. */
    private final List<String> viewActions;


    /**
     * Constructor.
     *
     * @param pageId page identifier
     * @param viewActions view actions
     */
    public SecurePageCommand(String pageId, List<String> viewActions) {
        this.pageId = pageId;
        this.viewActions = viewActions;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public ControllerResponse executeAssistantCommand() throws Exception {
        // Get bundle
        Locale locale = this.getControllerContext().getServerInvocation().getRequest().getLocale();
        Bundle bundle = this.getBundleFactory().getBundle(locale);

        // Get page
        PortalObjectId poid = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        Page page = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

        // Notification properties
        String pageName = PortalObjectUtils.getDisplayName(page, locale);
        String key;
        if (PageType.getPageType(page, this.getControllerContext()).isSpace()) {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CHANGE_RIGHTS_COMMAND_SPACE;
        } else if (PortalObjectUtils.isTemplate(page)) {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CHANGE_RIGHTS_COMMAND_TEMPLATE;
        } else {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CHANGE_RIGHTS_COMMAND_PAGE;
        }

        DomainConfigurator dc = this.getControllerContext().getController().getPortalObjectContainer().getAuthorizationDomain().getConfigurator();

        // Page constraints reconstruction
        Set<RoleSecurityBinding> newConstraints = new HashSet<RoleSecurityBinding>();
        Set<RoleSecurityBinding> oldConstraints = dc.getSecurityBindings(page.getId().toString(PortalObjectPath.CANONICAL_FORMAT));

        IProfilManager profilManager = Locator.findMBean(IProfilManager.class, "osivia:service=ProfilManager");

        for (Role role : profilManager.getFilteredRoles()) {
            Set<String> secureAction = new HashSet<String>();

            // Get old rights for not override other than view
            for (RoleSecurityBinding sbItem : oldConstraints) {
                if (sbItem.getRoleName().equals(role.getName())) {
                    for (Object action : sbItem.getActions()) {
                        secureAction.add(action.toString());
                    }
                }
            }

            // Update view actions
            secureAction.remove(PortalObjectPermission.VIEW_ACTION);
            if (this.viewActions.contains(role.getName())) {
                secureAction.add(PortalObjectPermission.VIEW_ACTION);
            }

            newConstraints.add(new RoleSecurityBinding(secureAction, role.getName()));
        }

        dc.setSecurityBindings(page.getId().toString(PortalObjectPath.CANONICAL_FORMAT), newConstraints);

        // Caches impact
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();

        // Notification
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.getControllerContext());
        String message = bundle.getString(key, pageName);
        NotificationsUtils.getNotificationsService().addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);

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

}
