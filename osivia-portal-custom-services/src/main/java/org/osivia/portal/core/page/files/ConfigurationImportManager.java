package org.osivia.portal.core.page.files;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.security.impl.JBossAuthorizationDomainRegistry;
import org.osivia.portal.api.locator.Locator;

public class ConfigurationImportManager implements IConfigurationImportManager {
	
	
   ImportPortalObjectContainer poc;
	
	public ConfigurationImportManager() {
		super();
		
	    ConfigurationFileParser parser = new ConfigurationFileParser("/opt/portal/jboss-as/server/production/deploy/jboss-portal-ha.sar/conf/data/default-object.xml");
	    poc = parser.parse(); 
	    
	    JBossAuthorizationDomainRegistry authorizationDomainRegistry= Locator.findMBean(JBossAuthorizationDomainRegistry.class, "portal:service=AuthorizationDomainRegistry");
		authorizationDomainRegistry .addDomain(poc);
	     
	}

	private final Log logger = LogFactory.getLog(FilesPortalObjectContainer.class);

	@Override
	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {
		logger.info("ConfigurationImportManager getObject " + id);
		PortalObject res = poc.getObject(id);
		return res;
	}

}
