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
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.internationalization.InternationalizationUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    private static final Pattern MESSAGE_LINKS_PATTERN = Pattern.compile("^(.*)\\[\\[(.*)]](.*)$");
    /** Popup type link indicator with associated link class. */
    private static final String[] POPUP_TYPE_LINK_INDICATOR = {"[POPUP]","fancyframe_refresh"};
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
    public static INotificationsService getNotificationsService() {
        return Locator.findMBean(INotificationsService.class, INotificationsService.MBEAN_NAME);
    }


    /**
     * Create notifications window context.
     * 
     * @param portalControllerContext portal controller context
     * @return notifications window context
     */
    public static WindowContext createNotificationsWindowContext(PortalControllerContext portalControllerContext) {
        if (portalControllerContext == null) {
            return null;
        }

        // HTTP servlet request
        HttpServletRequest servletRequest = portalControllerContext.getHttpServletRequest();

        // Locale
        Locale locale;
        if (servletRequest == null) {
            locale = null;
        } else {
            locale = servletRequest.getLocale();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }


        // Read notifications
        List<Notifications> notificationsList = null;
        if (!"1".equals(((ControllerContext) portalControllerContext.getControllerCtx()).getAttribute(ControllerCommand.REQUEST_SCOPE,
                "osivia.popupIgnoreNotifications"))) {
            notificationsList = getNotificationsService().readNotificationsList(portalControllerContext);
        }

        // Generate HTML content
        String htmlContent = generateNotificationsHTMLContent(notificationsList, locale);

        // Window properties
        Map<String, String> windowProperties = new HashMap<>();
        windowProperties.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
        windowProperties.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
        windowProperties.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");

        WindowResult windowResult = new WindowResult(null, htmlContent, Collections.EMPTY_MAP, windowProperties, null, WindowState.NORMAL, Mode.VIEW);
        return new WindowContext(WINDOW_ID, REGION_NAME, null, windowResult);
    }


    /**
     * Inject notifications region.
     *
     * @param portalControllerContext portal controller context
     * @param pageRendition page rendition
     */
    public static void injectNotificationsRegion(PortalControllerContext portalControllerContext, PageRendition pageRendition) {
        WindowContext windowContext = createNotificationsWindowContext(portalControllerContext);
        pageRendition.getPageResult().addWindowContext(windowContext);

        Region region = pageRendition.getPageResult().getRegion2(REGION_NAME);
        DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
    }



    /**
     * Generate notifications HTML content
     * 
     * @param notificationsList notifications list
     * @param locale user locale
     * @return HTML content
     */
    private static String generateNotificationsHTMLContent(List<Notifications> notificationsList, Locale locale) {
        // Internationalization service
        IInternationalizationService i18nService = InternationalizationUtils.getInternationalizationService();

        // Container
        Element container = DOM4JUtils.generateDivElement("notifications-container");

        // Ajax waiter toast container
        Element ajaxWaiterToastContainer = DOM4JUtils.generateDivElement("toast-container position-fixed top-0 start-50 translate-middle-x p-3");
        container.add(ajaxWaiterToastContainer);

        // Ajax waiter toast
        Element ajaxWaiterToast = DOM4JUtils.generateDivElement("toast fade text-bg-info border-0");
        DOM4JUtils.addAttribute(ajaxWaiterToast, "id", "ajax-waiter");
        DOM4JUtils.addAttribute(ajaxWaiterToast, "role", "status");
        DOM4JUtils.addDataAttribute(ajaxWaiterToast, "bs-autohide", String.valueOf(false));
        DOM4JUtils.addAriaAttribute(ajaxWaiterToast, "live", "polite");
        DOM4JUtils.addAriaAttribute(ajaxWaiterToast, "atomic", String.valueOf(true));
        ajaxWaiterToastContainer.add(ajaxWaiterToast);

        // Ajax waiter toast body
        Element ajaxWaiterToastBody = DOM4JUtils.generateDivElement("toast-body");
        DOM4JUtils.addGlyphiconText(ajaxWaiterToastBody, "glyphicons glyphicons-basic-refresh", i18nService.getString("AJAX_REFRESH", locale));
        ajaxWaiterToast.add(ajaxWaiterToastBody);

        // Dyna window
        Element dynaWindow = DOM4JUtils.generateDivElement("dyna-window");
        container.add(dynaWindow);

        // Window identifier
        Element windowId = DOM4JUtils.generateDivElement(null);
        DOM4JUtils.addAttribute(windowId, HTMLConstants.ID, WINDOW_ID);
        dynaWindow.add(windowId);

        // Dyna window content
        Element dynaWindowContent = DOM4JUtils.generateDivElement("dyna-window-content");
        windowId.add(dynaWindowContent);

        // Toast container
        Element toastContainer = DOM4JUtils.generateDivElement("toast-container position-fixed top-0 end-0 p-3");
        dynaWindowContent.add(toastContainer);

        if (notificationsList != null) {
            for (Notifications notifications : notificationsList) {
                NotificationsType type = notifications.getType();

                // Toast
                Element toast = DOM4JUtils.generateDivElement("toast fade show border-" + type.getHtmlClass());
                DOM4JUtils.addAttribute(toast, "role", "alert");
                if (NotificationsType.ERROR.equals(type)) {
                    DOM4JUtils.addDataAttribute(toast, "bs-autohide", String.valueOf(false));
                }
                DOM4JUtils.addAriaAttribute(toast, "live", "assertive");
                DOM4JUtils.addAriaAttribute(toast, "atomic", String.valueOf(true));
                toastContainer.add(toast);

                // Toast header
                Element toastHeader = DOM4JUtils.generateDivElement("toast-header text-bg-" + type.getHtmlClass());
                DOM4JUtils.addGlyphiconText(toastHeader, type.getIcon(), null);
                toast.add(toastHeader);

                // Toast header title
                Element toastHeaderTitle = DOM4JUtils.generateElement("strong", "ms-1 me-auto", null);
                if (notifications.getErrorCode() != null) {
                    toastHeaderTitle.add(DocumentHelper.createComment(notifications.getErrorCode().toString()));
                } else {
                    DOM4JUtils.addText(toastHeaderTitle, i18nService.getString("NOTIFICATION_TYPE_" + StringUtils.upperCase(type.name()), locale));
                }
                toastHeader.add(toastHeaderTitle);

                // Close toast button
                Element closeToastButton = DOM4JUtils.generateElement("button", "btn-close btn-close-white", null);
                DOM4JUtils.addAttribute(closeToastButton, "type", "button");
                DOM4JUtils.addDataAttribute(closeToastButton, "bs-dismiss", "toast");
                DOM4JUtils.addAriaAttribute(closeToastButton, "label", i18nService.getString("CLOSE", locale));
                toastHeader.add(closeToastButton);

                // Messages
                List<String> messages = notifications.getMessages();
                if (CollectionUtils.isNotEmpty(messages)) {
                    // Toast body
                    Element toastBody = DOM4JUtils.generateDivElement("toast-body");
                    toast.add(toastBody);

                    if (messages.size() == 1) {
                        // Single message
                        String message = messages.get(0);
                        Element text = DOM4JUtils.generateElement(HTMLConstants.SPAN, null, null);
                        messageHandling(text, message);
                        toastBody.add(text);
                    } else {
                        // Multiple messages
                        Element ul = DOM4JUtils.generateElement(HTMLConstants.UL, null, null);
                        toastBody.add(ul);

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
        return DOM4JUtils.write(container);
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
            String linkClass = StringUtils.EMPTY;
            if (split.length > 1) {
                text = split[1];
                if(split.length > 2){
                    linkClass = StringUtils.equalsIgnoreCase(POPUP_TYPE_LINK_INDICATOR[0], split[2]) ? POPUP_TYPE_LINK_INDICATOR[1] : linkClass;
                }
            } else {
                text = split[0];
            }
            Element a = DOM4JUtils.generateLinkElement(split[0], null, null, "alert-link ".concat(linkClass), text);
            parent.add(a);

            matcher = MESSAGE_LINKS_PATTERN.matcher(continuation);
        }

        parent.addText(continuation);
    }

}
