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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.error.UserNotificationsException;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.portalobjects.PortalObjectOrderComparator;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;


/**
 * Move page command.
 *
 * @author Cédric Krommenhoek
 * @see AssistantCommand
 */
public class MovePageCommand extends AssistantCommand {

    /** Page identifier. */
    private final String pageId;
    /** Destination page identifier. */
    private final String destinationPageId;


    /**
     * Constructor.
     *
     * @param pageId page identifier
     * @param destinationPageId destination page identifier
     */
    public MovePageCommand(String pageId, String destinationPageId) {
        super();
        this.pageId = pageId;
        this.destinationPageId = destinationPageId;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public ControllerResponse executeAssistantCommand() throws Exception {
        // Get bundle
        Locale locale = this.getControllerContext().getServerInvocation().getRequest().getLocale();
        Bundle bundle = this.getBundleFactory().getBundle(locale);

        // Pages recuperation
        PortalObjectId pagePortalObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        Page page = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(pagePortalObjectId);
        PortalObject destinationPage = null;
        if (!StringUtils.endsWith(this.destinationPageId, InternalConstants.SUFFIX_VIRTUAL_END_NODES_ID)) {
            PortalObjectId destinationPortalObjectId = PortalObjectId.parse(this.destinationPageId, PortalObjectPath.SAFEST_FORMAT);
            destinationPage = this.getControllerContext().getController().getPortalObjectContainer().getObject(destinationPortalObjectId);
        }

        if (page.equals(destinationPage)) {
            // Do nothing
            return new UpdatePageResponse(page.getId());
        }

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

        // Check parents
        PortalObject parentPage = page.getParent();
        PortalObject parentDestination;
        if (destinationPage == null) {
            if (StringUtils.endsWith(this.destinationPageId, InternalConstants.SUFFIX_VIRTUAL_END_NODES_ID)) {
                String parentDestinationId = StringUtils.removeEnd(this.destinationPageId, InternalConstants.SUFFIX_VIRTUAL_END_NODES_ID);
                PortalObjectId parentDestinationPortalObjectId = PortalObjectId.parse(parentDestinationId, PortalObjectPath.SAFEST_FORMAT);
                parentDestination = this.getControllerContext().getController().getPortalObjectContainer().getObject(parentDestinationPortalObjectId);
            } else {
                // Unknow destination page
                String message = bundle.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_MOVE_PAGE_COMMAND_UNKNOW_DESTINATION);
                throw new UserNotificationsException(message);
            }
        } else {
            parentDestination = destinationPage.getParent();
        }

        if (page.equals(parentDestination) || PortalObjectUtils.isAncestor(page, parentDestination)) {
            // Destination page cannot be a descendant of the current page
            String message = bundle.getString(InternationalizationConstants.KEY_ERROR_MESSAGE_MOVE_PAGE_COMMAND_DESCENDANT_DESTINATION);
            throw new UserNotificationsException(message);
        }

        // Move
        if (!parentPage.equals(parentDestination)) {
            String canonicalId;

            // Save security bindings
            canonicalId = page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
            DomainConfigurator domainConfigurator = this.getControllerContext().getController().getPortalObjectContainer().getAuthorizationDomain().getConfigurator();
            Set<RoleSecurityBinding> securityBindings = domainConfigurator.getSecurityBindings(canonicalId);

            String oldName = page.getName();
            page = (Page) page.copy(parentDestination, oldName, true);
            parentPage.destroyChild(oldName);

            // Restore security bindings
            canonicalId = page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
            domainConfigurator.setSecurityBindings(canonicalId, securityBindings);
        }

        // Pages order access
        SortedSet<Page> pages = new TreeSet<Page>(PortalObjectOrderComparator.getInstance());
        Collection<PortalObject> siblings = parentDestination.getChildren(PortalObject.PAGE_MASK);
        for (PortalObject sibling : siblings) {
            Page siblingPage = (Page) sibling;
            if (!siblingPage.equals(page)) {
                pages.add(siblingPage);
            }
        }
        List<Page> sortedPages = new ArrayList<Page>(pages);

        // Change order
        int orderValue = 1;
        for (Page reorderedPage : sortedPages) {
            if (reorderedPage.equals(destinationPage)) {
                page.setDeclaredProperty(InternalConstants.TABS_ORDER_PROPERTY, String.valueOf(orderValue++));
            }
            reorderedPage.setDeclaredProperty(InternalConstants.TABS_ORDER_PROPERTY, String.valueOf(orderValue++));
        }
        if (destinationPage == null) {
            page.setDeclaredProperty(InternalConstants.TABS_ORDER_PROPERTY, String.valueOf(orderValue++));
        }

        // Impact sur les caches du bandeau
        ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        cacheService.incrementHeaderCount();

        // Notification
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.getControllerContext());
        String message = bundle.getString(key, pageName);
        NotificationsUtils.getNotificationsService().addSimpleNotification(portalControllerContext, message, NotificationsType.SUCCESS);

        return new UpdatePageResponse(page.getId());
    }

}
