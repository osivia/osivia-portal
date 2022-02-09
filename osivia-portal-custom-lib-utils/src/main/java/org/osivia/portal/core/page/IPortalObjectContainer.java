package org.osivia.portal.core.page;

import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;

public interface IPortalObjectContainer {
	public PortalObject getNonDynamicObject(PortalObjectId id) throws IllegalArgumentException ;
	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException ;
	public IDynamicObjectContainer getDynamicObjectContainer() ;
	public void flushNaturalIdCache();

}
