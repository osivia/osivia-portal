package org.osivia.portal.core.notifications;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
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
import org.osivia.portal.api.HTMLConstants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.Notifications;


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


    /**
     * Default constructor.
     * NotificationsUtils instances should NOT be constructed in standard programming.
     * This constructor is public to permit tools that require a JavaBean instance to operate.
     */
    public NotificationsUtils() {
        super();
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
     * @param controllerContext controller context
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
        // HTML "div" #1
        Element div1 = new DOMElement(QName.get(HTMLConstants.DIV));
        div1.addAttribute(QName.get(HTMLConstants.CLASS), "dyna-window");

        // HTML "div" #2
        Element div2 = new DOMElement(QName.get(HTMLConstants.DIV));
        div2.addAttribute(QName.get(HTMLConstants.ID), WINDOW_ID);
        div1.add(div2);

        // HTML "div" #3
        Element div3 = new DOMElement(QName.get(HTMLConstants.DIV));
        div3.addAttribute(QName.get(HTMLConstants.CLASS), "dyna-window-content");
        div3.setText(StringUtils.EMPTY);
        div2.add(div3);

        if (CollectionUtils.isNotEmpty(notificationsList)) {
            // Notifications list HTML "div"
            Element divNotificationsList = new DOMElement(QName.get(HTMLConstants.DIV));
            divNotificationsList.addAttribute(QName.get(HTMLConstants.CLASS), "notifications-list");
            div3.add(divNotificationsList);

            for (Notifications notifications : notificationsList) {
                // Notifications
                Element divNotifications = new DOMElement(QName.get(HTMLConstants.DIV));
                divNotifications.addAttribute(QName.get(HTMLConstants.CLASS), "notifications " + notifications.getType().getHtmlClass());
                divNotificationsList.add(divNotifications);

                List<String> messages = notifications.getMessages();
                if (CollectionUtils.isNotEmpty(messages)) {
                    if (messages.size() == 1) {
                        // Single message
                        String message = messages.get(0);
                        Element p = new DOMElement(QName.get(HTMLConstants.P));
                        p.setText(message);
                        divNotifications.add(p);
                    } else {
                        // Multiple messages
                        Element ul = new DOMElement(QName.get(HTMLConstants.UL));
                        divNotifications.add(ul);

                        for (String message : messages) {
                            Element li = new DOMElement(QName.get(HTMLConstants.LI));
                            if (StringUtils.isEmpty(message)) {
                                li.setText(StringUtils.EMPTY);
                            } else {
                                li.setText(message);
                            }
                            ul.add(li);
                        }
                    }
                }
            }
        }

        return div1.asXML();
    }

}
