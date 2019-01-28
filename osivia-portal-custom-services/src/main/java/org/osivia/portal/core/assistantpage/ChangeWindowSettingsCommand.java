/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.assistantpage;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.AttributeResolver;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.taskbar.ITaskbarService;

/**
 * Change window settings command.
 *
 * @author Cédric Krommenhoek
 * @see AssistantCommand
 */
public class ChangeWindowSettingsCommand extends AssistantCommand {

    /** Window identifier. */
	private String windowId;
	/** Styles. */
    private List<String> style;

    /** Mobile collapse indicator. */
    private final boolean mobileCollapse;
    /** Hide title indicator. */
    private final boolean hideTitle;
    /** Title value. */
    private final String title;
    /** Hide decorators indicator. */
    private final boolean hideDecorators;
    /** Maximized to CMS indicator. */
    private final boolean maximizedToCms;
	/** Bootstrap panel style indicator. */
    private final boolean bootstrapPanelStyle;

	private String idPerso;
	private String ajaxLink;
	private String hideEmptyPortlet;
	private String printPortlet;
	private String conditionalScope;
	private String bshActivation;
	private String bshScript;
	private String cacheID;
	private String selectionDep;
	private String priority;

    /** Linked taskbar item identifier. */
    private String taskbarItemId;
    /** Selected satellite. */
    private String satellite;


    /**
     * Constructor.
     *
     * @param windowId window identifier
     * @param style styles
     * @param mobileCollapse mobile collapse indicator
     * @param displayTitle display title indicator
     * @param title title value
     * @param displayDecorators display decorators indicator
     * @param maximizedToCms maximized to CMS indicator
     * @param bootstrapPanelStyle Bootstrap panel style indicator
     * @param idPerso
     * @param ajaxLink
     * @param hideEmptyPortlet
     * @param printPortlet
     * @param conditionalScope
     * @param bshActivation
     * @param bshScript
     * @param cacheID
     * @param selectionDep
     */
    public ChangeWindowSettingsCommand(String windowId, List<String> style, String mobileCollapse, String displayTitle, String title, String displayDecorators,
            boolean maximizedToCms, String bootstrapPanelStyle, String idPerso, String ajaxLink, String hideEmptyPortlet, String printPortlet,
            String conditionalScope, String bshActivation, String bshScript, String cacheID, String selectionDep, String priority) {
        this.windowId = windowId;
        this.style = style;

        this.mobileCollapse = BooleanUtils.toBoolean(mobileCollapse);
        this.hideTitle = "0".equals(displayTitle);
        this.title = title;
        this.hideDecorators = "0".equals(displayDecorators);
        this.maximizedToCms = maximizedToCms;
        this.bootstrapPanelStyle = BooleanUtils.toBoolean(bootstrapPanelStyle);

        this.idPerso = idPerso;
        this.ajaxLink = ajaxLink;
        this.hideEmptyPortlet = hideEmptyPortlet;
        this.printPortlet = printPortlet;
        this.conditionalScope = conditionalScope;
        this.bshActivation = bshActivation;
        this.bshScript = bshScript;
        this.cacheID = cacheID;
        this.selectionDep = selectionDep;
        this.priority = priority;
    }


    /**
     * {@inheritDoc}
     */
	@Override
    public ControllerResponse executeAssistantCommand() throws Exception {
		// Récupération window
		PortalObjectId poid = PortalObjectId.parse(this.windowId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject window = this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);
		PortalObject page = window.getParent();

        // Update properties
		if ((this.style!= null) && (this.style.size() > 0))	{
			String sStyle = "";
			for (String itemStyle: this.style)	{
				if( sStyle.length() > 0) {
                    sStyle += ",";
                }
				sStyle += itemStyle;
			}
			window.setDeclaredProperty("osivia.style", sStyle);
		}
		else if (window.getDeclaredProperty("osivia.style") != null) {
            window.setDeclaredProperty("osivia.style", null);
        }

        // Mobile collapse indicator
        window.setDeclaredProperty("osivia.mobileCollapse", BooleanUtils.toStringTrueFalse(this.mobileCollapse));
        if (this.mobileCollapse) {
            // Force title display
            window.setDeclaredProperty("osivia.hideTitle", null);
            // Force Bootstrap panel style
            window.setDeclaredProperty("osivia.bootstrapPanelStyle", BooleanUtils.toStringTrueFalse(true));
        } else {
            // Display title indicator
            window.setDeclaredProperty("osivia.hideTitle", BooleanUtils.toString(this.hideTitle, "1", null));
            // Bootstrap panel style
            window.setDeclaredProperty("osivia.bootstrapPanelStyle", BooleanUtils.toStringTrueFalse(this.bootstrapPanelStyle));
        }

        // Title value
		if (this.title.length() > 0) {
            window.setDeclaredProperty("osivia.title", this.title);
        } else if (window.getDeclaredProperty("osivia.title") != null) {
            window.setDeclaredProperty("osivia.title", null);
        }

        // Display decorators indicator
        window.setDeclaredProperty("osivia.hideDecorators", BooleanUtils.toString(this.hideDecorators, "1", null));

        // Maximized to CMS indicator
        window.setDeclaredProperty(Constants.WINDOW_PROP_MAXIMIZED_CMS_URL, String.valueOf(this.maximizedToCms));


		if( "1".equals(this.ajaxLink)) {
            // la gestion des liens ajax oblige à utiliser le rafraichissement partiel
			window.setDeclaredProperty("theme.dyna.partial_refresh_enabled", "true");
        }



		if (this.idPerso.length() > 0) {
            window.setDeclaredProperty("osivia.idPerso", this.idPerso);
        } else if (window.getDeclaredProperty("osivia.idPerso") != null) {
            window.setDeclaredProperty("osivia.idPerso", null);
        }

		if ("1".equals(this.ajaxLink)) {
            window.setDeclaredProperty("osivia.ajaxLink", "1");
        } else if (window.getDeclaredProperty("osivia.ajaxLink") != null) {
            window.setDeclaredProperty("osivia.ajaxLink", null);
        }

		if( "1".equals(this.hideEmptyPortlet)) {
            window.setDeclaredProperty("osivia.hideEmptyPortlet", "1");
        } else if (window.getDeclaredProperty("osivia.hideEmptyPortlet") != null) {
            window.setDeclaredProperty("osivia.hideEmptyPortlet", null);
        }

		// v1.0.14 : ajout print

		if ("1".equals(this.printPortlet)) {
            window.setDeclaredProperty("osivia.printPortlet", "1");
        } else if (window.getDeclaredProperty("osivia.printPortlet") != null) {
            window.setDeclaredProperty("osivia.printPortlet", null);
        }


		// Pour rafraichissement barre de menu portlet (item print)
		String scopeKey = "cached_markup." + window.getId();
		AttributeResolver resolver = this.context.getAttributeResolver(Scope.PRINCIPAL_SCOPE);
		resolver.setAttribute(scopeKey, null);

		//v1.0.25 : affichage conditionnel portlet
		if ((this.conditionalScope!= null) && (this.conditionalScope.length() > 1)) {
            window.setDeclaredProperty("osivia.conditionalScope",this.conditionalScope);
        } else if (window.getDeclaredProperty("osivia.conditionalScope") != null) {
            window.setDeclaredProperty("osivia.conditionalScope", null);
        }


        // Linked taskbar item identifier
        window.setDeclaredProperty(ITaskbarService.LINKED_TASK_ID_WINDOW_PROPERTY, StringUtils.trimToNull(this.taskbarItemId));


		if ("1".equals(this.bshActivation)) {
            window.setDeclaredProperty("osivia.bshActivation", "1");
        } else if (window.getDeclaredProperty("osivia.bshActivation") != null) {
            window.setDeclaredProperty("osivia.bshActivation", null);
        }


		if (this.bshScript.length() > 0) {
            window.setDeclaredProperty("osivia.bshScript", this.bshScript);
        } else if (window.getDeclaredProperty("osivia.bshScript") != null) {
            window.setDeclaredProperty("osivia.bshScript", null);
        }

		if (this.cacheID.length() > 0) {
            window.setDeclaredProperty("osivia.cacheID", this.cacheID);
        } else if (window.getDeclaredProperty("osivia.cacheID") != null) {
            window.setDeclaredProperty("osivia.cacheID", null);
        }

		if ("1".equals(this.selectionDep)) {
            window.setDeclaredProperty("osivia.cacheEvents", "selection");
        } else if (window.getDeclaredProperty("osivia.cacheEvents") != null) {
            window.setDeclaredProperty("osivia.cacheEvents", null);
        }

        if (this.priority.length() > 0) {
            window.setDeclaredProperty("osivia.sequence.priority", this.priority);
        } else if (window.getDeclaredProperty("osivia.sequence.priority") != null) {
            window.setDeclaredProperty("osivia.sequence.priority", null);
        }

        // Selected satellite
        window.setDeclaredProperty("osivia.satellite", StringUtils.trimToNull(this.satellite));

		return new UpdatePageResponse(page.getId());
	}


    /**
     * Setter for taskbarItemId.
     * 
     * @param taskbarItemId the taskbarItemId to set
     */
    public void setTaskbarItemId(String taskbarItemId) {
        this.taskbarItemId = taskbarItemId;
    }

    /**
     * Setter for satellite.
     * 
     * @param satellite the satellite to set
     */
    public void setSatellite(String satellite) {
        this.satellite = satellite;
    }

}
