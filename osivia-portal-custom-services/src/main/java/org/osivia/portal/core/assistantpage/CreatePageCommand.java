package org.osivia.portal.core.assistantpage;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;


public class CreatePageCommand extends AssistantCommand {

	private String pageId;
	private String name;
	private String creationType;
	private String modeleId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPageId() {
		return pageId;
	}

	public CreatePageCommand() {
	}

	public CreatePageCommand(String pageId, String name, String creationType, String modeleId) {
		this.pageId = pageId;
		this.name = name;
		this.creationType = creationType;
		this.modeleId = modeleId;
	}



	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		Page page = (Page) getControllerContext().getController().getPortalObjectContainer().getObject(poid);
		
		PortalObject parent = null;

		// changement nom

		Page newPage = null;
		if (creationType.equals("child")) {
			parent = page;
		}	else if (creationType.equals("sister")) {
			parent = page.getParent();
		}
		
		// mise à jour d'après le modèle
		if( ! "0".equals(modeleId)){
			PortalObjectId pomodeleId = PortalObjectId.parse(modeleId, PortalObjectPath.SAFEST_FORMAT);

			Page modele = (Page) getControllerContext().getController().getPortalObjectContainer().getObject(pomodeleId);
			modele.copy( parent, name, true);
			newPage = (Page) parent.getChild(name);
			
			// initialisation du nom 
			Map<Locale, String> displayMap = RenamePageCommand.createLocalizedStringMap(Locale.FRENCH, newPage.getDisplayName(),
					name);
			LocalizedString newLocalizedString = new LocalizedString(displayMap, Locale.ENGLISH);
			newPage.setDisplayName(newLocalizedString);

			
		} else	{
			if (parent instanceof Portal) {
				newPage = ((Portal) parent).createPage(name);
			} else if (parent instanceof Page) {
				Page pa = (Page) parent;
				newPage = pa.createPage(name);
			}
		
		}

		
		
		//Impact sur les caches du bandeau
		ICacheService cacheService =  Locator.findMBean(ICacheService.class,"pia:service=Cache");
		cacheService.incrementHeaderCount();

		
		return new UpdatePageResponse(newPage.getId());

	}

}
