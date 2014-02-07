package org.osivia.portal.core.assistantpage;

import java.util.Locale;

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Change page CMS properties command.
 *
 * @see AssistantCommand
 */
public class ChangePageCMSPropertiesCommand extends AssistantCommand {

    /** Page identifier. */
    private final String pageId;
    /** CMS base path. */
    private final String cmsBasePath;
    /** Scope. */
    private final String scope;
    /** Page contextualization support. */
    private final String pageContextualizationSupport;
    /** Outgoing recontextualization support. */
    private final String outgoingRecontextualizationSupport;
    /** Navigation scope. */
    private final String navigationScope;
    /** Display live version. */
    private final String displayLiveVersion;


    /**
     * Constructor.
     *
     * @param pageId page identifier
     * @param cmsBasePath CMS base path
     * @param scope scope
     * @param pageContextualizationSupport page contextualization support
     * @param outgoingRecontextualizationSupport outgoing recontextualization support
     * @param navigationScope navigation scope
     * @param displayLiveVersion display live version
     */
    public ChangePageCMSPropertiesCommand(String pageId, String cmsBasePath, String scope, String pageContextualizationSupport,
            String outgoingRecontextualizationSupport, String navigationScope, String displayLiveVersion) {
        this.pageId = pageId;
        this.cmsBasePath = cmsBasePath;
        this.scope = scope;
        this.pageContextualizationSupport = pageContextualizationSupport;
        this.outgoingRecontextualizationSupport = outgoingRecontextualizationSupport;
        this.navigationScope = navigationScope;
        this.displayLiveVersion = displayLiveVersion;

    }


    /**
     * {@inheritDoc}
     */
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
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CHANGE_CMS_PROPERTIES_COMMAND_SPACE;
        } else if (PortalObjectUtils.isTemplate(page)) {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CHANGE_CMS_PROPERTIES_COMMAND_TEMPLATE;
        } else {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CHANGE_CMS_PROPERTIES_COMMAND_PAGE;
        }

        if ((this.cmsBasePath != null) && (this.cmsBasePath.length() != 0)) {
            page.setDeclaredProperty("osivia.cms.basePath", this.cmsBasePath);
        } else {
            page.setDeclaredProperty("osivia.cms.basePath", null);
        }

        if ((this.navigationScope != null) && (this.navigationScope.length() != 0)) {
            page.setDeclaredProperty("osivia.cms.navigationScope", this.navigationScope);
        } else {
            page.setDeclaredProperty("osivia.cms.navigationScope", null);
        }

        if ((this.scope != null) && (this.scope.length() != 0)) {
            page.setDeclaredProperty("osivia.cms.scope", this.scope);
        } else {
            page.setDeclaredProperty("osivia.cms.scope", null);
        }

        if ((this.displayLiveVersion != null) && (this.displayLiveVersion.length() != 0)) {
            page.setDeclaredProperty("osivia.cms.displayLiveVersion", this.displayLiveVersion);
        } else {
            page.setDeclaredProperty("osivia.cms.displayLiveVersion", null);
        }

        if ((this.pageContextualizationSupport != null) && (this.pageContextualizationSupport.length() != 0)) {
            page.setDeclaredProperty("osivia.cms.pageContextualizationSupport", this.pageContextualizationSupport);
        } else {
            page.setDeclaredProperty("osivia.cms.pageContextualizationSupport", null);
        }

        if ((this.outgoingRecontextualizationSupport != null) && (this.outgoingRecontextualizationSupport.length() != 0)) {
            page.setDeclaredProperty("osivia.cms.outgoingRecontextualizationSupport", this.outgoingRecontextualizationSupport);
        } else {
            page.setDeclaredProperty("osivia.cms.outgoingRecontextualizationSupport", null);
        }

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
