package org.osivia.portal.core.portalobjects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.impl.model.portal.AbstractPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.content.Content;
import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.page.PortalObjectContainer;



@SuppressWarnings("unchecked")
public class DynamicPersistentWindow extends DynamicWindow {

	protected WindowImpl orig;
	PortalObjectContainer container;
	protected Map<String, String> declaredProperties ;	
	
	
	private static Log logger = LogFactory.getLog(DynamicPersistentWindow.class);

	

	
	@Override
	public Page getPage() {
		if( page != null)
			return page;
		else	{
			// Test perf 1
			// new DynamicPage((PageImpl)orig.getPage(), dynamicContainer);
			//page = new DynamicPage((PageImpl)orig.getPage(), dynamicContainer);
			//
			Page page = new DynamicPersistentPage(container,(PageImpl)orig.getPage(), dynamicContainer);
			return page;
		}
	}
	
	/* window dynamique */
	public DynamicPersistentWindow( PortalObjectContainer container, DynamicPage page,String path, Object context, DynamicPortalObjectContainer dynamicContainer, String uri, Map<String,String> localProperties,  DynamicWindowBean dynaBean)	{
		
		super( page, path, context, dynamicContainer, dynaBean);
		
		this.container = container;
		
		contentType = ContentType.PORTLET;
		this.uri = uri;
		
		// Content		
		Content content = getContent();
		content.setURI(uri);
		
		
		for( String key : localProperties.keySet())	{
			setLocalProperty(key, localProperties.get(key));
		}
	}
	
	/* Constructeur par encapsulation */
	public DynamicPersistentWindow( PortalObjectContainer container, WindowImpl orig, DynamicPortalObjectContainer dynamicContainer) throws IllegalArgumentException {
		
		super();		
		
		this.container = container;
		this.dynamicContainer = dynamicContainer;
		setObjectNode(orig.getObjectNode());		
		super.setContext(orig.getObjectNode().getContext());
		this.orig = (WindowImpl) orig;
		
		setSessionWindow(false);
		
		uri = orig.getURI();
		
		// Content		
		Content content = getContent();
		content.setURI(uri);
		
		id = orig.getId();
		
		// Optimisation  : ajout cache
		DynamicPortalObjectContainer.addToCache(id, this);
		}
	
	@Override  
	public ContentType getContentType()	{
		if( contentType == null && orig != null)
			contentType = orig.getContentType();
		return contentType;	
	}
	
	@Override
	public PortalObjectId getId() {
		if( id != null)
			return id;
		else
			return orig.getId();
	}
	
	
	@Override
	public LocalizedString getDisplayName() {
		return orig.getDisplayName();
	}

	@Override
	public Map getDisplayNames() {
		return orig.getDisplayNames();
	}


	@Override
	public String getName() {
		if( orig != null)
			return orig.getName();
		return name;
	}

	@Override
	public ObjectNode getObjectNode() {
		return orig.getObjectNode();
	}

	@Override
	public Map<String, String> getProperties()	{
		if( properties != null)
			return properties;
		else
			return ((Window) orig).getProperties();
		
	}
	
	@Override
	public void setDeclaredProperty(String name, String value) {
		if( orig != null)
			 orig.setDeclaredProperty(name, value);
			
	}
	
	@Override
	public String getDeclaredProperty(String name) {
		
		// modif v1.1 : priorité sur template
		
		if( orig != null)
			return orig.getDeclaredProperty(name);
		
		// En priorité les valeurs de l'instance
		String value = null;
		if( getProperties() != null)	{
			value =  getProperties().get(name);
		}

		
		return value;
	}
	
	@Override
	public Map<String, String> getDeclaredProperties() {
		if (declaredProperties == null) {
			declaredProperties = new HashMap<String, String>();
			
			// Ajout v.0.13 : null pointer exception
			if( orig != null)	{
				Map<String, String> declProps = orig.getDeclaredProperties();
				for (String key : declProps.keySet())
					declaredProperties.put(key, declProps.get(key));
				if (properties != null) {
					for (String key : properties.keySet())
					declaredProperties.put(key, properties.get(key));
				}
			} 
		}
		return declaredProperties;
	}
		
	
	 public String toString()
	   {
		 if( orig != null)
	      return orig.toString();
		 else
			 return("dynamicWindow " + getName());
	   }

}
