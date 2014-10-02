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

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

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
    /** Page category. */    
    private String category;
    /** Page selectors propagation. */  
    private String selectorsPropagation;




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
    public ChangePagePropertiesCommand(String pageId, String displayName, String draftPage, String layout, String theme,  String category, String selectorsPropagation) {
        super();
        this.pageId = pageId;
        this.displayName = displayName;
        this.draftPage = draftPage;
        this.layout = layout;
        this.theme = theme;
        this.category = category;
        this.selectorsPropagation = selectorsPropagation;
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
        PortalObjectId portalObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        Page page = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(portalObjectId);

        // Notification properties
        String key;
        if (PageType.getPageType(page, this.getControllerContext()).isSpace()) {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CHANGE_PROPERTIES_COMMAND_SPACE;
        } else if (PortalObjectUtils.isTemplate(page)) {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CHANGE_PROPERTIES_COMMAND_TEMPLATE;
        } else {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_CHANGE_PROPERTIES_COMMAND_PAGE;
        }

        // Display name
        Map<Locale, String> displayMap = createLocalizedStringMap(locale, page.getDisplayName(), this.displayName);
        LocalizedString newLocalizedString = new LocalizedString(displayMap, locale);
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

        
        // Page category
        if( System.getProperty(InternalConstants.SYSTEM_PROPERTY_PAGE_CATEGORY_PREFIX) != null) {
            if (category != null && category.length() != 0) {
                page.setDeclaredProperty("osivia.pageCategory", category);
            } else {
                page.setDeclaredProperty("osivia.pageCategory", null);
            }
        }
        
       
        
        // Selectors propagation
        if ("1".equals(this.selectorsPropagation)) {
            page.setDeclaredProperty("osivia.cms.propagateSelectors", "1");
        } else if (page.getDeclaredProperty("osivia.cms.propagateSelectors") != null) {
            page.setDeclaredProperty("osivia.cms.propagateSelectors", null);
        }
        

        
        // Caches impact
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();

        // Notification
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.getControllerContext());
        String message = bundle.getString(key, this.displayName);
        NotificationsUtils.getNotificationsService().addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);

        return new UpdatePageResponse(page.getId());
    }

}
