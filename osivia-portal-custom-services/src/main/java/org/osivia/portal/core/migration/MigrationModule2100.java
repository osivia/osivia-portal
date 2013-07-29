package org.osivia.portal.core.migration;

import java.util.Collection;

import org.jboss.portal.core.model.portal.PortalObject;
import org.osivia.portal.core.constants.InternalConstants;

public class MigrationModule2100 extends MigrationModule {

    @Override
    public int getModuleId() {
        return 2100;
    }


    private void renamePath(PortalObject portalObject) {


        String value = portalObject.getDeclaredProperty("osivia.nuxeoPath");
        if (value != null) {
            portalObject.setDeclaredProperty("osivia.nuxeoPath", null);
            portalObject.setDeclaredProperty("osivia.cms.uri", value);
        }


        // scan children

        Collection<PortalObject> childrens = portalObject.getChildren();
        for (PortalObject po : childrens) {
            renamePath(po);
        }

    }

    @Override
    public void execute() throws Exception {

        // Set portal Types

        Collection<PortalObject> portals = getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK);

        for (PortalObject po : portals) {

            String portalType = InternalConstants.PORTAL_TYPE_STATIC_PORTAL;

            if (po.getDeclaredProperty("osivia.navigation.menuRootPath") != null) {
                portalType = InternalConstants.PORTAL_TYPE_SPACE;
                po.setDeclaredProperty("osivia.navigation.menuRootPath", null);
            }


            po.setDeclaredProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE, portalType);

        }

        // Rename path "osivia.nuxeoPath" to "osivia.cms.uri"
        renamePath(getPortalObjectContainer().getContext());

    }

}
