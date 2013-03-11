package org.osivia.portal.core.migration;

import org.jboss.portal.core.model.portal.PortalObjectContainer;

public abstract class MigrationModule {
  public abstract int getModuleId();
  public abstract void execute ()  throws Exception;
  
	public PortalObjectContainer portalObjectContainer;

	public PortalObjectContainer getPortalObjectContainer() {
		return portalObjectContainer;
	}

	public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer)  {
		this.portalObjectContainer = portalObjectContainer;
	}

}
