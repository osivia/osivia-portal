package org.osivia.portal.core.portalobjects;

import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;

public class NotFetchedPersistentWindow extends DynamicWindow {

	public NotFetchedPersistentWindow(PortalObjectId pageId, WindowImpl orig, String name,
			DynamicPortalObjectContainer dynamicContainer) throws IllegalArgumentException {

		super();

		id = new PortalObjectId("", new PortalObjectPath(pageId.getPath().toString().concat("/").concat(name), PortalObjectPath.CANONICAL_FORMAT));
	}
	
	
	@Override
	public PortalObjectId getId() {
			return id;

	}
	

}
