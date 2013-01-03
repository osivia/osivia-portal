package org.osivia.portal.core.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.PortalObjectContainer;

/**
 * Migration manager
 * 
 * @author jeanseb
 * 
 */

public class MigrationService implements IMigrationManager {
	
	/*
    if( "/osivia-portal-administration".equals( pwa.getContextPath())) {
  	  // Migrations modules are deployed by administration layer
  	  // (also benefits from the cluster singleton thanks to Deployer )
  	  
		IMigrationManager migrationMgr =  Locator.findMBean(IMigrationManager.class, "pia:service=MigrationService");
		if( migrationMgr != null)
			migrationMgr.migrate();
  	  
    }
    
    */

	public static String LAST_MODULE_ID_PROP = "pia.migration.lastModuleId"; 
	private static Log logger = LogFactory.getLog(MigrationService.class);
	public PortalObjectContainer portalObjectContainer;

	public PortalObjectContainer getPortalObjectContainer() {
		return portalObjectContainer;
	}

	public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
		this.portalObjectContainer = portalObjectContainer;
	}

	public void migrate() {
		
		logger.info("migration");
		
		Context context = getPortalObjectContainer().getContext();
		
		String lastModuleId = context.getDeclaredProperty(LAST_MODULE_ID_PROP);
		
		context.setDeclaredProperty(LAST_MODULE_ID_PROP, "1");

	}

}
