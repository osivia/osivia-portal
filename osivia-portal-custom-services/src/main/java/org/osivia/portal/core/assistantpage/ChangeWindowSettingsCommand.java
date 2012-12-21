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
			window.setDeclaredProperty("pia.style", sStyle);
		}
		else if (window.getDeclaredProperty("pia.style") != null)
			window.setDeclaredProperty("pia.style", null);

		if ("0".equals(displayTitle))
			window.setDeclaredProperty("pia.hideTitle", "1");
		else if (window.getDeclaredProperty("pia.hideTitle") != null)
			window.setDeclaredProperty("pia.hideTitle", null);
		
		if (title.length() > 0)
			window.setDeclaredProperty("pia.title", title);
		else if (window.getDeclaredProperty("pia.title") != null)
			window.setDeclaredProperty("pia.title", null);
		

		if ("0".equals(displayDecorators))
			window.setDeclaredProperty("pia.hideDecorators", "1");
		else if (window.getDeclaredProperty("pia.hideDecorators") != null)
			window.setDeclaredProperty("pia.hideDecorators", null);
		
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
			window.setDeclaredProperty("pia.idPerso", idPerso);
		else if (window.getDeclaredProperty("pia.idPerso") != null)
			window.setDeclaredProperty("pia.idPerso", null);

		if ("1".equals(ajaxLink))
			window.setDeclaredProperty("pia.ajaxLink", "1");
		else if (window.getDeclaredProperty("pia.ajaxLink") != null)
			window.setDeclaredProperty("pia.ajaxLink", null);
		
		if( "1".equals(hideEmptyPortlet))
			window.setDeclaredProperty("pia.hideEmptyPortlet", "1");
		else if (window.getDeclaredProperty("pia.hideEmptyPortlet") != null)
			window.setDeclaredProperty("pia.hideEmptyPortlet", null);	
		
		// v1.0.14 : ajout print
		
		if ("1".equals(printPortlet))
			window.setDeclaredProperty("pia.printPortlet", "1");
		else if (window.getDeclaredProperty("pia.printPortlet") != null)
			window.setDeclaredProperty("pia.printPortlet", null);
		
		
		// Pour rafraichissement barre de menu portlet (item print)
		String scopeKey = "cached_markup." + window.getId();
		AttributeResolver resolver = context.getAttributeResolver(Scope.PRINCIPAL_SCOPE);
		resolver.setAttribute(scopeKey, null);
		
		//v1.0.25 : affichage conditionnel portlet
		if (conditionalScope!= null && conditionalScope.length() > 1)
			window.setDeclaredProperty("pia.conditionalScope",conditionalScope);
		else if (window.getDeclaredProperty("pia.conditionalScope") != null)
			window.setDeclaredProperty("pia.conditionalScope", null);		


	

		return new UpdatePageResponse(page.getId());

	}

}
