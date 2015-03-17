package org.osivia.portal.core.menubar;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.menubar.IMenubarService;

/**
 * Utility class with null-safe methods for menubar.
 *
 * @author Cédric Krommenhoek
 */
public final class MenubarUtils {

    /**
     * Private constructor : prevent instantiation.
     */
    private MenubarUtils() {
        throw new AssertionError();
    }


    /**
     * Get menubar service.
     *
     * @return menubar service
     */
    public static final IMenubarService getMenubarService() {
        return Locator.findMBean(IMenubarService.class, IMenubarService.MBEAN_NAME);
    }


    /**
     * Create content navbar actions window context.
     *
     * @param portalControllerContext portal controller context
     * @return content navbar actions window context
     */
    public static final WindowContext createContentNavbarActionsWindowContext(PortalControllerContext portalControllerContext) {
        if (portalControllerContext == null) {
            return null;
        }

        // Generate HTML content
        String htmlContent = getMenubarService().generateNavbarContent(portalControllerContext);

        // Window properties
        Map<String, String> windowProperties = new HashMap<String, String>();
        windowProperties.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
        windowProperties.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
        windowProperties.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");

        WindowResult windowResult = new WindowResult(null, htmlContent, Collections.EMPTY_MAP, windowProperties, null, WindowState.NORMAL, Mode.VIEW);
        return new WindowContext(IMenubarService.MENUBAR_WINDOW_ID, IMenubarService.MENUBAR_REGION_NAME, null, windowResult);
    }


    /**
     * Inject content navbar actions region.
     *
     * @param controllerContext controller context
     * @param pageRendition page rendition
     */
    public static final void injectContentNavbarActionsRegion(PortalControllerContext portalControllerContext, PageRendition pageRendition) {
        WindowContext windowContext = createContentNavbarActionsWindowContext(portalControllerContext);
        pageRendition.getPageResult().addWindowContext(windowContext);

        Region region = pageRendition.getPageResult().getRegion2(IMenubarService.MENUBAR_REGION_NAME);
        DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
    }

}
