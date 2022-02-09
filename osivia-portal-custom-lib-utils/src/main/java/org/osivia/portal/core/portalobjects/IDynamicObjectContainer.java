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
package org.osivia.portal.core.portalobjects;

import java.util.List;

import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.page.IPortalObjectContainer;


public interface IDynamicObjectContainer {

	public PortalObject getObject(IPortalObjectContainer container, PortalObjectId id);

	public void startPersistentIteration();
	public void stopPersistentIteration();


	public void addDynamicWindow( DynamicWindowBean window );
	public List<DynamicWindowBean> getDynamicWindows( );
	public void setDynamicWindows( List<DynamicWindowBean> windows);
	public void removeDynamicWindow( String dynamicWindowId );

    public List<DynamicWindowBean> getPageWindows(IPortalObjectContainer container, PortalObjectId pageId, boolean includeCMSWindows);

	public void addDynamicPage( DynamicPageBean window );
	public List<DynamicPageBean> getDynamicPages( );
	public void setDynamicPages( List<DynamicPageBean> windows);
	public void removeDynamicPage( String dynamicWindowId );

}
