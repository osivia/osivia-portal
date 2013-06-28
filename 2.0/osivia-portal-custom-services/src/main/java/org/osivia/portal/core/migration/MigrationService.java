package org.osivia.portal.core.migration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.page.PageUtils;

/**
 * Migration manager
 * 
 * Doit etre lance en mode singleton !!!
 * 
 * @author jeanseb
 * 
 */

public class MigrationService implements IMigrationManager {
	
	/*
    if( "/osivia-portal-administration".equals( pwa.getContextPath())) {
  	  // Migrations modules are deployed by administration layer
  	  // (also benefits from the cluster singleton thanks to Deployer )
  	  
		IMigrationManager migrationMgr =  Locator.findMBean(IMigrationManager.class, "osivia:service=MigrationService");
		if( migrationMgr != null)
			migrationMgr.migrate();
  	  
    }
    
    */

	public static String LAST_MODULE_ID_PROP = "osivia.migration.lastModuleId"; 
	private static Log logger = LogFactory.getLog(MigrationService.class);
	public PortalObjectContainer portalObjectContainer;

	public PortalObjectContainer getPortalObjectContainer() {
		return portalObjectContainer;
	}

	public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
		this.portalObjectContainer = portalObjectContainer;
	}
	
	public static final Comparator<MigrationModule> orderComparator = new Comparator<MigrationModule>() {
		
		public int compare(MigrationModule o1, MigrationModule o2) {
			return o1.getModuleId() - o2.getModuleId();
		}
	};

	

	private List<MigrationModule> getModulesList()	{
		
		 List<MigrationModule> modules = new ArrayList<MigrationModule>();
		 
		 modules.add(new MigrationModule2060());
		 modules.add(new MigrationModule2061());
		 
		 Collections.sort(modules, orderComparator);
		 
		 return modules;
	}
	
	public void migrate() {
		
		logger.info("migration");
		
		try	{
	
		
		Context context = getPortalObjectContainer().getContext();
		
		String lastModuleId = context.getDeclaredProperty(LAST_MODULE_ID_PROP);
		
		int lastId = 0;
		if( lastModuleId != null && lastModuleId.length() > 0)
			lastId = Integer.parseInt(lastModuleId);
		
		int nbMigration = 0;
		
		for (MigrationModule module: getModulesList()){
			// Check if the module has alreay executed
			
			if( module.getModuleId() > lastId)	{
				
				logger.info("migration module :" + module.getModuleId() + " starting ");
				
				
				File backupFile = new MigrationBackup().backup( module.getModuleId());
				
				logger.info("Saving portal parameters " + module.getModuleId() + " to " + backupFile.getAbsolutePath()		);
				nbMigration++;
				
				module.setPortalObjectContainer(portalObjectContainer);
				module.execute( );
				
				context.setDeclaredProperty(LAST_MODULE_ID_PROP, Integer.toString(module.getModuleId()));
				
				logger.info("migration module :" + module.getModuleId() + " finished");
			}
		}
		
		logger.info("" + nbMigration + " migration module(s) runned");
		
		} catch( Exception e){
			logger.error("migration failed", e);
			throw new RuntimeException( e);
		}
		
		

	}

}
