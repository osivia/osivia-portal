package org.osivia.portal.core.assistantpage;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;

/**
 * Change page properties command.
 *
 * @see AssistantCommand
 */
public class ChangePagePropertiesCommand extends AssistantCommand {

    /** Page ID. */
    private String pageId;
    /** Page display name. */
    private String displayName;
    /** Draft page mode. */
    private String draftPage;
    /** Page layout. */
    private String layout;
    /** Page theme. */
    private String theme;


    /**
     * Default constructor.
     */
    public ChangePagePropertiesCommand() {
    }

    /**
     * Constructor using fields.
     *
     * @param pageId page ID
     * @param displayName page display name
     * @param draftPage draft page mode
     * @param layout page layout
     * @param theme page theme
     */
    public ChangePagePropertiesCommand(String pageId, String displayName, String draftPage, String layout, String theme) {
        super();
        this.pageId = pageId;
        this.displayName = displayName;
        this.draftPage = draftPage;
        this.layout = layout;
        this.theme = theme;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse executeAssistantCommand() throws Exception {
        // Get page ID
        PortalObjectId portalObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        PortalObject page = this.getControllerContext().getController().getPortalObjectContainer().getObject(portalObjectId);

        // Display name
        Locale locale = this.getControllerContext().getServerInvocation().getRequest().getLocale();
        Map<Locale, String> displayMap = createLocalizedStringMap(locale, page.getDisplayName(), this.displayName);
        LocalizedString newLocalizedString = new LocalizedString(displayMap, Locale.ENGLISH);
        page.setDisplayName(newLocalizedString);

        // Draft mode
        if ("1".equals(this.draftPage)) {
            page.setDeclaredProperty("osivia.draftPage", "1");
        } else if (page.getDeclaredProperty("osivia.draftPage") != null) {
            page.setDeclaredProperty("osivia.draftPage", null);
        }

        // Layout
        if ((this.layout != null) && (this.layout.length() != 0)) {
            page.setDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT, this.layout);
        } else {
            page.setDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT, null);
        }

        // Theme
        if (StringUtils.isEmpty(this.theme)) {
            page.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, null);
        } else {
            page.setDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME, this.theme);
        }

        // Caches impact
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();

        return new UpdatePageResponse(page.getId());
    }

}
