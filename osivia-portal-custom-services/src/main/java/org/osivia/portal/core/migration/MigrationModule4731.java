package org.osivia.portal.core.migration;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.model.portal.*;

import java.util.Collection;

/**
 * Migration module for version 4.7.31.
 *
 * @author Cédric Krommenhoek
 * @see MigrationModule
 */
public class MigrationModule4731 extends MigrationModule {

    /**
     * Module identifier.
     */
    private static final int MODULE_ID = 4731;


    /**
     * Constructor.
     */
    public MigrationModule4731() {
        super();
    }


    @Override
    public int getModuleId() {
        return MODULE_ID;
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
        for (PortalObject portalObject : parent.getChildren()) {
            if (portalObject instanceof Window) {
                Window window = (Window) portalObject;

                // Conditional scope
                String scope = window.getDeclaredProperty("osivia.conditionalScope");
                if (StringUtils.isNotEmpty(scope)) {
                    window.setDeclaredProperty("osivia.conditionalScope", null);
                    window.setDeclaredProperty("osivia.conditionalScopes", scope);
                }
            } else if (portalObject instanceof Page) {
                Page page = (Page) portalObject;

                // Loop on sub-pages
                this.update(page);
            }
        }
    }

}
