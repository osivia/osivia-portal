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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.dynamic.DynamicWindowBean;

public class DynamicCache {
	
	Map<PortalObjectId, PortalObject> datas = new Hashtable<PortalObjectId, PortalObject>();
	List<DynamicWindowBean> editablesWindows = null;
	
	public Map<PortalObjectId, PortalObject> getDatas() {
		return datas;
	}
	
    public List<DynamicWindowBean> getEditablesWindows() {
        return editablesWindows;
    }
    
    public void setEditablesWindows(List<DynamicWindowBean> editablesWindows) {
        this.editablesWindows = editablesWindows;
    }
    public void setDatas(Map<PortalObjectId, PortalObject> datas) {
		this.datas = datas;
	}


}
