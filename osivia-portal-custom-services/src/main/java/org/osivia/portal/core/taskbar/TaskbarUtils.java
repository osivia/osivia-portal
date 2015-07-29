package org.osivia.portal.core.taskbar;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Utility class with null-safe methods for taskbar.
 *
 * @author Cédric Krommenhoek
 */
public class TaskbarUtils {

    /**
     * Private constructor : prevent instantiation.
     */
    private TaskbarUtils() {
        throw new AssertionError();
    }


    /**
     * Get taskbar service.
     *
     * @return taskbar service
     */
    public static final ITaskbarService getTaskbarService() {
        return Locator.findMBean(ITaskbarService.class, ITaskbarService.MBEAN_NAME);
    }


    /**
     * Initialize taskbar window.
     *
     * @param portalControllerContext portal controller context
     * @param page current page
     * @param pageRendition page rendition
     */
    public static final void initializeWindow(PortalControllerContext portalControllerContext, Page page, PageRendition pageRendition) {
        ITaskbarService taskbarService = getTaskbarService();

        // Taskbar region
        String region = taskbarService.getRegion(portalControllerContext);

        if ((region != null) && (page.getWindow(ITaskbarService.WINDOW_NAME) == null)) {
            WindowContext windowContext = createWindowContext(portalControllerContext, region);
            pageRendition.getPageResult().addWindowContext(windowContext);

            taskbarService.addEmptyWindow(portalControllerContext, null);
        }
    }


    /**
     * Create taskbar window context.
     *
     * @param portalControllerContext portal controller context
     * @param region taskbar region name
     * @return window context
     */
    private static final WindowContext createWindowContext(PortalControllerContext portalControllerContext, String region) {
        // Generate HTML content
        String htmlContent = generateHTMLContent();

        // Window identifier
        PortalObjectId windowId = getWindowId(portalControllerContext);

        // Window properties
        Map<String, String> windowProperties = new HashMap<String, String>();
        windowProperties.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
        windowProperties.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
        windowProperties.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
        windowProperties.put("osivia.style", "hidden");

        WindowResult windowResult = new WindowResult(null, htmlContent, Collections.EMPTY_MAP, windowProperties, null, WindowState.NORMAL, Mode.VIEW);
        return new WindowContext(windowId.toString(PortalObjectPath.SAFEST_FORMAT), region, String.valueOf(100), windowResult);
    }


    /**
     * Generate HTML content.
     *
     * @return HTML content
     */
    private static final String generateHTMLContent() {
        // HTML "div" #1
        Element div1 = DOM4JUtils.generateDivElement(null);

        // HTML "div" #2
        Element div2 = DOM4JUtils.generateDivElement("dyna-window-content");
        div1.add(div2);

        // Write HTML content
        return DOM4JUtils.write(div1);
    }


    /**
     * Get taskbar window portal object identifier.
     *
     * @param portalControllerContext portal controller context
     * @return portal object identifier
     */
    private static PortalObjectId getWindowId(PortalControllerContext portalControllerContext) {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Page identifier
        PortalObjectId pageId = PortalObjectUtils.getPageId(controllerContext);

        // Path
        StringBuilder builder = new StringBuilder();
        builder.append(pageId.getPath().toString());
        builder.append("/");
        builder.append(ITaskbarService.WINDOW_NAME);
        PortalObjectPath path = new PortalObjectPath(builder.toString(), PortalObjectPath.CANONICAL_FORMAT);

        return new PortalObjectId(StringUtils.EMPTY, path);
    }


    /**
     * Get maximized window.
     *
     * @param controllerContext controller context
     * @param page page
     * @return window
     */
    public static Window getMaximizedWindow(ControllerContext controllerContext, Page page) {
        Window maximizedWindow = null;
        Collection<PortalObject> portalObjects = page.getChildren(PortalObject.WINDOW_MASK);
        for (PortalObject portalObject : portalObjects) {
            Window window = (Window) portalObject;
            NavigationalStateKey navigationalStateKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());
            WindowNavigationalState windowNavigationalState = (WindowNavigationalState) controllerContext.getAttribute(
                    ControllerCommand.NAVIGATIONAL_STATE_SCOPE, navigationalStateKey);

            if ((windowNavigationalState != null) && WindowState.MAXIMIZED.equals(windowNavigationalState.getWindowState())) {
                maximizedWindow = window;
                break;
            }
        }
        return maximizedWindow;
    }


    /**
     * Get current path.
     *
     * @param controllerContext controller context
     * @param page page
     * @return current path
     */
    public static String getCurrentPath(ControllerContext controllerContext, Page page) {
        // State context
        NavigationalStateContext stateContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        // Current page state
        PageNavigationalState pageState = stateContext.getPageNavigationalState(page.getId().toString());

        // Current path
        String currentPath = null;
        if (pageState != null) {
            String[] sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, InternalConstants.ATTR_CMS_PATH));
            if (ArrayUtils.isNotEmpty(sPath)) {
                currentPath = sPath[0];
            }
        }

        return currentPath;
    }

}
