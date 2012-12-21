package org.osivia.portal.core.portalobjects;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.PortalImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.page.PortalObjectContainer;


public class DynamicPersistentPage extends DynamicPage {
	PageImpl orig;
	List<Window> windows;
	PortalObject parent;
	Portal portal;
	
	public DynamicPersistentPage(PortalObjectContainer container, PageImpl orig,  DynamicPortalObjectContainer dynamicContainer) throws IllegalArgumentException {
		super();
		
		this.container = container;
		this.dynamicContainer = dynamicContainer;
		
		containerContext = orig.getObjectNode().getContext();
		setObjectNode(orig.getObjectNode());	
		
		this.orig = (PageImpl) orig;
		
		
		// Optimisation  : ajout cache
		DynamicPortalObjectContainer.addToCache(orig.getId(), this);
	
	}
	
	DynamicWindow createSessionWindow ( DynamicWindowBean dynamicWindowBean)	{
		return new DynamicPersistentWindow( container, this, dynamicWindowBean.getName(), containerContext, dynamicContainer,  dynamicWindowBean.getUri(), dynamicWindowBean.getProperties(), dynamicWindowBean)	;
	}
	
	public PortalObject getParent()	{
		if (parent == null)	{
			// TODO :verifier que  ne pas passer par le container optimise réellement les perfs 
			
			//parent = container.getObject(new PortalObjectId("", orig.getId().getPath().getParent()));
			
			parent = orig.getParent();
			
			if( parent instanceof PageImpl)
				return new DynamicPersistentPage( container, (PageImpl) parent,   dynamicContainer);
			if( parent instanceof PortalImpl)
				return new DynamicPortal( container, (PortalImpl) parent,   dynamicContainer);
			
			return container.getObject(new PortalObjectId("", orig.getId().getPath().getParent()));
			
				
		}
		return parent;	
	}
	
	
	// Ajout v.0.13 : correction perte fenetres dynamiques au login
	   public Portal getPortal()
	   {
		if (portal == null) {
			PortalObject object = orig.getParent();
			while (object != null && !(object instanceof Portal)) {
				object = object.getParent();
			}
			
			if( object instanceof PortalImpl)
				portal = new DynamicPortal(container, (PortalImpl) object, dynamicContainer);
		}

		return portal;
	   }

	
	private List<Window> getWindows() {
		
		if( windows == null)	{
			Collection childs = orig.getChildren( PortalObject.WINDOW_MASK);
			windows = new ArrayList<Window>();
			for( Object child : childs)	{
				windows.add( new DynamicPersistentWindow(container, (WindowImpl)child, dynamicContainer));
			}
			
			//ajout fenetre dynamiques
			windows.addAll(getDynamicWindows ().values());
			
			return windows;
		}


		return windows;
	}

	
	@Override
	public Collection getChildren() {
		
		if( windows == null)	{
		
		windows = new ArrayList<Window>();
		
		for( Object po: orig.getChildren())	{
			if( po instanceof Window)	{
				Window window = (Window) po;
				windows.add( window);
			}
			}
		
		windows.addAll(getDynamicWindows ().values());
		}


		return windows;
	}

	
	List<Window> notFetchedWindows;
	public List<Window> getNotFetchedWindows () {
		
		if( notFetchedWindows == null)	{
		
			Collection childs = orig.getChildren( PortalObject.WINDOW_MASK);
			notFetchedWindows = new ArrayList<Window>();
			for( Object child : childs)	{
				notFetchedWindows.add( new NotFetchedPersistentWindow(getId(), (WindowImpl)child, ((WindowImpl)child).getName(),  dynamicContainer));
			}
			
			//ajout fenetre dynamiques
			notFetchedWindows.addAll(getDynamicWindows ().values());
			
			return notFetchedWindows;
		}


		return notFetchedWindows;
	}
	

	
	@Override
	public Collection getChildren(int wantedMask) {
		
		if( wantedMask != PortalObject.WINDOW_MASK)
			return orig.getChildren( wantedMask);
		else	{
			
//			List<Window> windows = getWindows();
			
			/*
			 for( Window window : windows)	{
				 logger.debug("cms.uri" + window.getProperties().get("pia.cms.uri"));
			 }
*/
				
				return getWindows();
			
		}
	}

  
	
	@Override
	public PortalObject getChild(String name) {
		Window child = getDynamicWindows().get(name);
		
		if( child != null)
			return child;
		else 
			return orig.getChild(name);
	}

	

	

	@Override
	public boolean equals(Object arg0) {
		return orig.equals(arg0);
	}

	

	

	@Override
	public org.jboss.portal.common.i18n.LocalizedString getDisplayName() {
		return orig.getDisplayName();
	}

	@Override
	public Map getDisplayNames() {
		return orig.getDisplayNames();
	}

	@Override
	public PortalObjectId getId() {
		return orig.getId();
	}

	@Override
	public String getName() {
		return orig.getName();
	}
	
	@Override
	public Map getProperties() {
		return orig.getProperties();
	}


	@Override
	public ObjectNode getObjectNode() {
		return orig.getObjectNode();
	}

	@Override
	public void setDeclaredProperty(String name, String value) {
				
			orig.setDeclaredProperty(name, value);
		
	}

	@Override
	public String getDeclaredProperty(String name) {
			return orig.getDeclaredProperty(name);

	}

	
	@Override
	public Map<String, String> getDeclaredProperties() {
		return orig.getDeclaredProperties();
	}
	
	
	   public void setDisplayName(LocalizedString displayName){
		   orig.setDisplayName(displayName);
	   }
	   
	  
}
