package org.osivia.portal.core.assistantpage;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import java.util.List;

import org.jboss.portal.common.invocation.AttributeResolver;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.NoSuchResourceException;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.core.mt.CacheEntry;


public class ChangeWindowSettingsCommand extends AssistantCommand {

	private String windowId;
	List<String> style;
	private String displayTitle;
	private String title;
	private String displayDecorators;
	private String partialRefresh;
	private String idPerso;
	private String ajaxLink;
	private String hideEmptyPortlet;
	private String printPortlet;
	private String conditionalScope;

	public ChangeWindowSettingsCommand() {
	}

	public ChangeWindowSettingsCommand(String windowId, List<String> style, String displayTitle, String title, String displayDecorators,
			String partialRefresh, String idPerso, String ajaxLink, String hideEmptyPortlet, String printPortlet, String conditionalScope) {
		this.windowId = windowId;
		this.style = style;
		this.displayTitle = displayTitle;
		this.title = title;
		this.displayDecorators = displayDecorators;
		this.partialRefresh = partialRefresh;
		this.idPerso = idPerso;
		this.ajaxLink = ajaxLink;
		this.hideEmptyPortlet=hideEmptyPortlet;
		this.printPortlet = printPortlet;
		this.conditionalScope = conditionalScope;
	}

	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération window
		PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject window = getControllerContext().getController().getPortalObjectContainer().getObject(poid);
		PortalObject page = window.getParent();

		// modification propriétés

		if (style!= null && style.size() > 0)	{
			String sStyle = "";
			for (String itemStyle: style)	{
				if( sStyle.length() > 0)
					sStyle += ",";
				sStyle += itemStyle;
			}
			window.setDeclaredProperty("osivia.style", sStyle);
		}
		else if (window.getDeclaredProperty("osivia.style") != null)
			window.setDeclaredProperty("osivia.style", null);

		if ("0".equals(displayTitle))
			window.setDeclaredProperty("osivia.hideTitle", "1");
		else if (window.getDeclaredProperty("osivia.hideTitle") != null)
			window.setDeclaredProperty("osivia.hideTitle", null);
		
		if (title.length() > 0)
			window.setDeclaredProperty("osivia.title", title);
		else if (window.getDeclaredProperty("osivia.title") != null)
			window.setDeclaredProperty("osivia.title", null);
		

		if ("0".equals(displayDecorators))
			window.setDeclaredProperty("osivia.hideDecorators", "1");
		else if (window.getDeclaredProperty("osivia.hideDecorators") != null)
			window.setDeclaredProperty("osivia.hideDecorators", null);
		
		if ("1".equals(partialRefresh))	
			window.setDeclaredProperty("theme.dyna.partial_refresh_enabled", "true");
		else	{
			if( !"1".equals(ajaxLink))
				// la gestion des liens ajax oblige à utiliser le rafraichissement partiel
				window.setDeclaredProperty("theme.dyna.partial_refresh_enabled", "false");
			else
				window.setDeclaredProperty("theme.dyna.partial_refresh_enabled", "true");
		}
		
	
		if (idPerso.length() > 0)
			window.setDeclaredProperty("osivia.idPerso", idPerso);
		else if (window.getDeclaredProperty("osivia.idPerso") != null)
			window.setDeclaredProperty("osivia.idPerso", null);

		if ("1".equals(ajaxLink))
			window.setDeclaredProperty("osivia.ajaxLink", "1");
		else if (window.getDeclaredProperty("osivia.ajaxLink") != null)
			window.setDeclaredProperty("osivia.ajaxLink", null);
		
		if( "1".equals(hideEmptyPortlet))
			window.setDeclaredProperty("osivia.hideEmptyPortlet", "1");
		else if (window.getDeclaredProperty("osivia.hideEmptyPortlet") != null)
			window.setDeclaredProperty("osivia.hideEmptyPortlet", null);	
		
		// v1.0.14 : ajout print
		
		if ("1".equals(printPortlet))
			window.setDeclaredProperty("osivia.printPortlet", "1");
		else if (window.getDeclaredProperty("osivia.printPortlet") != null)
			window.setDeclaredProperty("osivia.printPortlet", null);
		
		
		// Pour rafraichissement barre de menu portlet (item print)
		String scopeKey = "cached_markup." + window.getId();
		AttributeResolver resolver = context.getAttributeResolver(Scope.PRINCIPAL_SCOPE);
		resolver.setAttribute(scopeKey, null);
		
		//v1.0.25 : affichage conditionnel portlet
		if (conditionalScope!= null && conditionalScope.length() > 1)
			window.setDeclaredProperty("osivia.conditionalScope",conditionalScope);
		else if (window.getDeclaredProperty("osivia.conditionalScope") != null)
			window.setDeclaredProperty("osivia.conditionalScope", null);		


	

		return new UpdatePageResponse(page.getId());

	}

}
