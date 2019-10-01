package org.osivia.portal.core.migration;

import org.jboss.portal.core.model.content.Content;
import org.jboss.portal.core.model.portal.*;

import java.util.Collection;

/**
 * Migration module 4.7.00.
 *
 * @author Cédric Krommenhoek
 * @see MigrationModule
 */
public class MigrationModule4700 extends MigrationModule {

    /**
     * Constructor.
     */
    public MigrationModule4700() {
        super();
    }


    @Override
    public int getModuleId() {
        return 4700;
    }


    @Override
    public void execute() throws Exception {
        // Portal object container
        PortalObjectContainer portalObjectContainer = this.getPortalObjectContainer();
        // Portal object context
        Context context = portalObjectContainer.getContext();
        // Portals
        Collection<PortalObject> portals = context.getChildren(PortalObject.PORTAL_MASK);

        for (PortalObject portal : portals) {
            this.update(portal);
        }
    }


    /**
     * Update portal object.
     *
     * @param parent parent portal object
     */
    private void update(PortalObject parent) {
        // Windows
        Collection<PortalObject> windows = parent.getChildren(PortalObject.WINDOW_MASK);
        // Pages
        Collection<PortalObject> pages = parent.getChildren(PortalObject.PAGE_MASK);

        for (PortalObject portalObject : windows) {
            if (portalObject instanceof Window) {
                // Window
                Window window = (Window) portalObject;
                // Window content
                Content windowContent = window.getContent();

                if (windowContent != null) {
                    if ("osivia-services-calendar-instance".equals(windowContent.getURI())) {
                        // Hide decorators indicator
                        boolean hideDecorators = "1".equals(window.getDeclaredProperty("osivia.hideDecorators"));

                        if (!hideDecorators) {
                            window.setDeclaredProperty("osivia.maximized.cms.url", String.valueOf(true));
                        }
                    }
                }
            }
        }

        for (PortalObject portalObject : pages) {
            if (portalObject instanceof Page) {
                // Page
                Page page = (Page) portalObject;

                this.update(page);
            }
        }
    }

}
