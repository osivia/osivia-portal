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
