package org.osivia.portal.core.assistantpage;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jboss.portal.common.i18n.LocaleFormat;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.NoSuchResourceException;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;


public class RenamePageCommand extends AssistantCommand {

	private String pageId;
	private String displayName;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPageId() {
		return pageId;
	}

	public RenamePageCommand() {
	}

	public RenamePageCommand(String pageId, String displayName) {
		this.pageId = pageId;
		this.displayName = displayName;
	}

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

	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		// changement nom

		Map<Locale, String> displayMap = createLocalizedStringMap(Locale.FRENCH, page.getDisplayName(),
				getDisplayName());
		LocalizedString newLocalizedString = new LocalizedString(displayMap, Locale.ENGLISH);

		page.setDisplayName(newLocalizedString);
		
		//Impact sur les caches du bandeau
		ICacheService cacheService =  Locator.findMBean(ICacheService.class,"pia:service=Cache");
		cacheService.incrementHeaderCount();


		return new UpdatePageResponse(page.getId());

	}

}
