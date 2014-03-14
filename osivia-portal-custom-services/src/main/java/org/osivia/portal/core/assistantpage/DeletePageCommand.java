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

import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
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
 * Delete page command.
 *
 * @see AssistantCommand
 */
public class DeletePageCommand extends AssistantCommand {

    /** Page identifier. */
    private final String pageId;


    /**
     * Constructor.
     *
     * @param pageId page identifier
     */
    public DeletePageCommand(String pageId) {
        this.pageId = pageId;
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
        PortalObject parent = page.getParent();

        // Notification properties
        String pageName = PortalObjectUtils.getDisplayName(page, locale);
        String key;
        if (PageType.getPageType(page, this.getControllerContext()).isSpace()) {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_DELETE_PAGE_COMMAND_SPACE;
        } else if (PortalObjectUtils.isTemplate(page)) {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_DELETE_PAGE_COMMAND_TEMPLATE;
        } else {
            key = InternationalizationConstants.KEY_SUCCESS_MESSAGE_DELETE_PAGE_COMMAND_PAGE;
        }

        // Destruction
        parent.destroyChild(page.getName());

        // Redirect to parent page, or to portal default page
        Page redirectPage = null;
        if (parent instanceof Page) {
            redirectPage = (Page) parent;
        } else if (parent instanceof Portal) {
            redirectPage = ((Portal) parent).getDefaultPage();
        }

        // Impact on header cache
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();

        // Notification
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.getControllerContext());
        String message = bundle.getString(key, pageName);
        NotificationsUtils.getNotificationsService().addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);

        return new UpdatePageResponse(redirectPage.getId());
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
