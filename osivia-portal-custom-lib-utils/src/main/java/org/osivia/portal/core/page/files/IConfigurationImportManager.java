package org.osivia.portal.core.page.files;

import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;

public interface IConfigurationImportManager  {
	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException ;
}
