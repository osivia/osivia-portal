package org.osivia.portal.core.portalobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.impl.model.portal.AbstractPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PortalImpl;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.page.PortalObjectContainer;



@SuppressWarnings("unchecked")
public  class DynamicPortal extends PortalImpl {
	
	protected static final Log log = LogFactory.getLog(DynamicPortal.class);

	protected AbstractPortalObjectContainer.ContainerContext containerContext;
	protected DynamicPortalObjectContainer dynamicContainer;
	
	PortalObjectContainer container;
	
	PortalImpl orig;
	List<Page> children;
	Map<String, DynamicPage> dynamicChilds;
	
	protected String name;
	
	public DynamicPortal(PortalObjectContainer container, PortalImpl orig,  DynamicPortalObjectContainer dynamicContainer) throws IllegalArgumentException {
		super();
		
		this.dynamicContainer = dynamicContainer;
		this.container = container;
		
		containerContext = orig.getObjectNode().getContext();
		setObjectNode(orig.getObjectNode());	
		
		this.orig = orig;
		
		
		// Optimisation  : ajout cache
		DynamicPortalObjectContainer.addToCache(orig.getId(), this);
	}
	
	
	
	protected Map<String, DynamicPage> getDynamicChilds ()	{
		

		if( dynamicChilds == null){

			dynamicChilds = new HashMap<String, DynamicPage>();
		
			for( DynamicPageBean dynamicPage : dynamicContainer.getDynamicPages())	{
				if(dynamicPage.getParentId().equals(getId()))	{
					DynamicPage child = DynamicTemplatePage.createPage( container, dynamicPage.getParentId(), dynamicPage.getName(), dynamicPage.getDisplayNames(), (PortalObjectImpl) container.getNonDynamicObject(dynamicPage.getTemplateId()), dynamicContainer, dynamicPage)	;

					dynamicChilds.put(child.getName(), child);
				}
			}
		}
		
		return dynamicChilds;
	}
	
	@Override
	public Collection getChildren() {
		
		if( children == null)	{
		
			children = new ArrayList<Page>();
		
		for( Object po: orig.getChildren())	{

				children.add( (Page) po);

			}
		
		children.addAll(getDynamicChilds ().values());
		}


		return children;
	}
	
	
	@Override
	public Collection getChildren(int wantedMask) {
		if( wantedMask != PortalObject.PAGE_MASK)
			return super.getChildren(wantedMask);
		return getChildren();
		
	}

	
	
	@Override
	public PortalObject getChild(String name) {
		
		Page child = getDynamicChilds().get(name);
		
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
	public PortalObject getParent() {
		return orig.getParent();
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

	
	 public Set getSupportedWindowStates()
	   {
	      return orig.getSupportedWindowStates();
	   }
	  public Set getSupportedModes()
	   {
	      return orig.getSupportedModes();
	   }

	

}
