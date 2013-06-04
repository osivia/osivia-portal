package org.osivia.portal.core.assistantpage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;

/**
 * Rename page command.
 * 
 * @see AssistantCommand
 * 
 */
public class RenamePageCommand extends AssistantCommand {

    /** Page ID. */
    private String pageId;
    /** New display name for the current locale. */
    private String displayName;

    /**
     * Default constructor.
     */
    public RenamePageCommand() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param pageId page ID
     * @param displayName new display name for the current locale
     */
    public RenamePageCommand(String pageId, String displayName) {
        this.pageId = pageId;
        this.displayName = displayName;
    }

    /**
     * Utility method used to create a localized string map with other locales values.
     * 
     * @param locale current locale
     * @param displayName new display name
     * @param name name
     * @return the localized string map
     */
    public static Map<Locale, String> createLocalizedStringMap(Locale locale, LocalizedString displayName, String name) {
        Map<Locale, String> map = new HashMap<Locale, String>();
        if (displayName != null) {
            Map<Locale, LocalizedString.Value> oldMap = displayName.getValues();
            Collection<LocalizedString.Value> values = oldMap.values();
            for (LocalizedString.Value value : values) {
                map.put(value.getLocale(), value.getString());
            }
        }
        map.put(locale, name);
        return map;
    }

    @Override
    public ControllerResponse executeAssistantCommand() throws Exception {
        // Récupération page
        PortalObjectId portalObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        PortalObject page = this.getControllerContext().getController().getPortalObjectContainer().getObject(portalObjectId);

        // Changement de nom
        Locale locale = this.getControllerContext().getServerInvocation().getRequest().getLocale();
        Map<Locale, String> displayMap = createLocalizedStringMap(locale, page.getDisplayName(), this.displayName);
        LocalizedString newLocalizedString = new LocalizedString(displayMap, Locale.ENGLISH);
        page.setDisplayName(newLocalizedString);

        // Impact sur les caches du bandeau
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();

        return new UpdatePageResponse(page.getId());
    }

}
