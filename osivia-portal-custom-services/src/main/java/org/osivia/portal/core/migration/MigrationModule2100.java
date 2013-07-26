package org.osivia.portal.core.migration;

import java.util.Collection;

import org.jboss.portal.core.model.portal.PortalObject;
import org.osivia.portal.core.constants.InternalConstants;

public class MigrationModule2100 extends MigrationModule {

	@Override
	public int getModuleId() {
		return 2100;
	}

	@Override
	public void execute() throws Exception {

		// Set portal Types

		Collection<PortalObject> portals = getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK);

		for (PortalObject po : portals) {
		    
		    String portalType = InternalConstants.PORTAL_TYPE_PORTAL;
		    
		    if( po.getDeclaredProperty("osivia.navigation.menuRootPath") != null) {
                portalType = InternalConstants.PORTAL_TYPE_SPACE;
                po.setDeclaredProperty("osivia.navigation.menuRootPath", null);
		    }

		    
			po.setDeclaredProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE, portalType);
			
		}

	}

}
