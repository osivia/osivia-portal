package org.osivia.portal.core.portalobjects;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;

public class DynamicCache {
	
	Map<PortalObjectId, PortalObject> datas = new Hashtable<PortalObjectId, PortalObject>();

	
	public Map<PortalObjectId, PortalObject> getDatas() {
		return datas;
	}
	public void setDatas(Map<PortalObjectId, PortalObject> datas) {
		this.datas = datas;
	}


}
