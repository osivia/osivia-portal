package org.osivia.portal.core.page.files;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;

public class ConfigurationImportManager implements IConfigurationImportManager {
	
	private final Log logger = LogFactory.getLog(FilesPortalObjectContainer.class);

	@Override
	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {
		logger.info("ConfigurationImportManager getObject " + id);
		return null;
	}

}
