package org.osivia.portal.core.page.files;

import org.jboss.portal.core.impl.model.portal.ContextImpl;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;

public interface IConfigurationImportManager  {
	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException ;
	AuthorizationDomain getAuthorizationDomain();
	public ContextImpl createRoot(String namespace) throws DuplicatePortalObjectException;
}
