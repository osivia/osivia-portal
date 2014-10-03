/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.migration;

import org.jboss.portal.core.model.portal.PortalObjectContainer;

public abstract class MigrationModule {
  public abstract int getModuleId();
  public boolean isOnlyOnceModule() {
      return false;
  }
  public abstract void execute ()  throws Exception;
  
	public PortalObjectContainer portalObjectContainer;

	public PortalObjectContainer getPortalObjectContainer() {
		return portalObjectContainer;
	}

	public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer)  {
		this.portalObjectContainer = portalObjectContainer;
	}

}
