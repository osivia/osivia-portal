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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.impl.model.portal.AbstractPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.dynamic.StopDynamicWindowCommand;
import org.osivia.portal.core.page.PortalObjectContainer;



@SuppressWarnings("unchecked")
public abstract class DynamicPage extends PageImpl {
	
	protected static final Log logger = LogFactory.getLog(DynamicPage.class);

	protected AbstractPortalObjectContainer.ContainerContext containerContext;
	protected DynamicPortalObjectContainer dynamicContainer;
	protected PortalObjectContainer container;
	

	Map<String, DynamicWindow> dynamicChilds;
	
	Map<String, DynamicPage> dynamicSubPages;
	
	protected Map<String, String> properties = null;
	
	abstract DynamicWindow createSessionWindow ( DynamicWindowBean dynamicWindowBean);

	protected Map<String, DynamicWindow> getDynamicWindows ()	{
		

		if( dynamicChilds == null){

			dynamicChilds = new HashMap<String, DynamicWindow>();
		
			for( DynamicWindowBean dynamicWindow : dynamicContainer.getPageWindows(getId()))	{
				DynamicWindow child = createSessionWindow (dynamicWindow);
				dynamicChilds.put(child.getName(), child);
			}
		}
		
		return dynamicChilds;
		
	}
	
	

	

	

}
