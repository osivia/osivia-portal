package org.osivia.portal.core.portalobjects;

import java.util.List;

import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.page.PortalObjectContainer;


public interface IDynamicObjectContainer {
	
	public PortalObject getObject(PortalObjectContainer container, PortalObjectId id);
	
	public void startPersistentIteration();
	public void stopPersistentIteration();
	
	
	public void addDynamicWindow( DynamicWindowBean window );
	public List<DynamicWindowBean> getDynamicWindows( );
	public void setDynamicWindows( List<DynamicWindowBean> windows);	
	public void removeDynamicWindow( String dynamicWindowId );	
	
	public List<DynamicWindowBean> getPageWindows( PortalObjectId pageId);
	
	public void addDynamicPage( DynamicPageBean window );
	public List<DynamicPageBean> getDynamicPages( );
	public void setDynamicPages( List<DynamicPageBean> windows);	
	public void removeDynamicPage( String dynamicWindowId );	

}
