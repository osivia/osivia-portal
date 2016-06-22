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
package org.osivia.portal.core.notifications;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.html.HTMLConstants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.api.notifications.NotificationsType;


/**
 * Utility class with null-safe methods for notifications.
 *
 * @author Cédric Krommenhoek
 */
public class NotificationsUtils {

    /** Notifications window identifier. */
    private static final String WINDOW_ID = "notifications-window";
    /** Notifications region name. */
    private static final String REGION_NAME = "notifications";

    /** Message links pattern. */
    private static final Pattern MESSAGE_LINKS_PATTERN = Pattern.compile("^(.*)\\[\\[(.*)\\]\\](.*)$");
    /** Message beginning regex group number. */
    private static final int REGEX_GROUP_BEGINNING = 1;
    /** Message link regex group number. */
    private static final int REGEX_GROUP_LINK = 2;
    /** Message continuation group number. */
    private static final int REGEX_GROUP_CONTINUATION = 3;


    /**
     * Private constructor : prevent instantiation.
     */
    private NotificationsUtils() {
        throw new AssertionError();
    }


    /**
     * Get notifications service.
     *
     * @return notifications service
     */
    public static final INotificationsService getNotificationsService() {
        return Locator.findMBean(INotificationsService.class, INotificationsService.MBEAN_NAME);
    }


    /**
     * Create notifications window context.
     * 
     * @param portalControllerContext portal controller context
     * @return notifications window context
     */
    public static final WindowContext createNotificationsWindowContext(PortalControllerContext portalControllerContext) {
        if (portalControllerContext == null) {
            return null;
        }

        // Read notifications
        List<Notifications> notificationsList = null;
        if (!"1".equals(((ControllerContext) portalControllerContext.getControllerCtx()).getAttribute(ControllerCommand.REQUEST_SCOPE,
                "osivia.popupIgnoreNotifications"))) {
            notificationsList = getNotificationsService().readNotificationsList(portalControllerContext);
        }

        // Generate HTML content
        String htmlContent = generateNotificationsHTMLContent(notificationsList);

        // Window properties
        Map<String, String> windowProperties = new HashMap<String, String>();
        windowProperties.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
        windowProperties.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
        windowProperties.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");

        WindowResult windowResult = new WindowResult(null, htmlContent, Collections.EMPTY_MAP, windowProperties, null, WindowState.NORMAL, Mode.VIEW);
        return new WindowContext(WINDOW_ID, REGION_NAME, null, windowResult);
    }


    /**
     * Inject notifications region.
     *
     * @param controllerContext controller context
     * @param pageRendition page rendition
     */
    public static final void injectNotificationsRegion(PortalControllerContext portalControllerContext, PageRendition pageRendition) {
        WindowContext windowContext = createNotificationsWindowContext(portalControllerContext);
        pageRendition.getPageResult().addWindowContext(windowContext);

        Region region = pageRendition.getPageResult().getRegion2(REGION_NAME);
        DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
    }


    /**
     * @param notificationsList
     * @return
     * @throws IOException
     */
    private static String generateNotificationsHTMLContent(List<Notifications> notificationsList) {
        // Dyna window
        Element dynaWindow = DOM4JUtils.generateDivElement("dyna-window");

        // Window identifier
        Element windowId = DOM4JUtils.generateDivElement(null);
        DOM4JUtils.addAttribute(windowId, HTMLConstants.ID, WINDOW_ID);
        dynaWindow.add(windowId);

        // Dyna window content
        Element dynaWindowContent = DOM4JUtils.generateDivElement("dyna-window-content");
        windowId.add(dynaWindowContent);

        if (notificationsList != null) {
            for (Notifications notifications : notificationsList) {
                NotificationsType type = notifications.getType();

                // Alert
                Element alert = DOM4JUtils.generateDivElement("alert alert-dismissable " + type.getHtmlClass());
                dynaWindowContent.add(alert);

                if (notifications.getErrorCode() != null) {
                    alert.add(DocumentHelper.createComment(notifications.getErrorCode().toString()));
                }

                // Close button
                Element button = DOM4JUtils.generateElement(HTMLConstants.BUTTON, "close", "&times;");
                DOM4JUtils.addAttribute(button, HTMLConstants.TYPE, HTMLConstants.INPUT_TYPE_BUTTON);
                DOM4JUtils.addAttribute(button, HTMLConstants.DATA_DISMISS, "alert");
                DOM4JUtils.addAttribute(button, HTMLConstants.ARIA_HIDDEN, "true");
                alert.add(button);

                // Media
                Element media = DOM4JUtils.generateDivElement("media");
                alert.add(media);

                // Media left
                if (type.getIcon() != null) {
                    Element mediaLeft = DOM4JUtils.generateElement(HTMLConstants.DIV, "media-left media-middle", null, type.getIcon(), null);
                    media.add(mediaLeft);
                }

                // Media body
                Element mediaBody = DOM4JUtils.generateDivElement("media-body");
                media.add(mediaBody);

                // Messages
                List<String> messages = notifications.getMessages();
                if (CollectionUtils.isNotEmpty(messages)) {
                    if (messages.size() == 1) {
                        // Single message
                        String message = messages.get(0);
                        Element text = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, null);
                        messageHandling(text, message);
                        mediaBody.add(text);
                    } else {
                        // Multiple messages
                        Element ul = DOM4JUtils.generateElement(HTMLConstants.UL, null, null);
                        mediaBody.add(ul);

                        for (String message : messages) {
                            Element li = DOM4JUtils.generateElement(HTMLConstants.LI, null, null);
                            messageHandling(li, message);
                            ul.add(li);
                        }
                    }
                }
            }
        }

        // Write HTML content
        return DOM4JUtils.write(dynaWindow);
    }


    /**
     * Utility method used to handle message.
     *
     * @param parent DOM4J parent element
     * @param message message
     */
    private static void messageHandling(Element parent, String message) {
        String beginning;
        String link;
        String continuation = message;

        Matcher matcher = MESSAGE_LINKS_PATTERN.matcher(continuation);

        while (matcher.matches()) {
            beginning = matcher.group(REGEX_GROUP_BEGINNING);
            link = matcher.group(REGEX_GROUP_LINK);
            continuation = matcher.group(REGEX_GROUP_CONTINUATION);

            // Message beginning
            parent.addText(beginning);

            // Link
            String[] split = StringUtils.split(link, "|");
            String text;
            if (split.length > 1) {
                text = split[1];
            } else {
                text = split[0];
            }
            Element a = DOM4JUtils.generateLinkElement(split[0], null, null, "alert-link", text);
            parent.add(a);

            matcher = MESSAGE_LINKS_PATTERN.matcher(continuation);
        }

        parent.addText(continuation);
    }

}
