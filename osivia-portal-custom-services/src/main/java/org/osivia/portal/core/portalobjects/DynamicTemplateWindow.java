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



@SuppressWarnings("unchecked")
public class DynamicTemplateWindow extends DynamicWindow {

	protected WindowImpl templateOrig;
	private boolean templatedWindow = false;

	private static Log logger = LogFactory.getLog(DynamicTemplateWindow.class);
	

	
	
	/* window dynamique */
	public DynamicTemplateWindow( TemplatePage page,String path, Object context, DynamicPortalObjectContainer dynamicContainer, String uri, Map<String,String> localProperties, DynamicWindowBean dynaBean)	{
		
		super( page, path, context, dynamicContainer, dynaBean);
		
		contentType = ContentType.PORTLET;
		this.uri = uri;
		
		// Content		
		Content content = getContent();
		content.setURI(uri);
		
		
		for( String key : localProperties.keySet())	{
			setLocalProperty(key, localProperties.get(key));
		}
	}
	
	
	/* Constructeur par ajout */
	public DynamicTemplateWindow( TemplatePage page, WindowImpl templateOrig, String name, Object context, DynamicPortalObjectContainer dynamicContainer)	{
		super();
		
		this.dynamicContainer = dynamicContainer;
		this.page = page;
		this.templateOrig = templateOrig;
		this.name = name;
		
		templatedWindow = true;

		id = new PortalObjectId("", new PortalObjectPath( page.getId().getPath().toString().concat("/").concat(name), PortalObjectPath.CANONICAL_FORMAT));
		containerContext = (AbstractPortalObjectContainer.ContainerContext)context;
		// v2 20121106 : Initialisation à null (sinon les propriétés sont vides)
		//properties = new HashMap<String, String>();
		
		
		contentType = ContentType.PORTLET;
		this.uri =templateOrig.getURI();
		
		// Optimisation  : ajout cache
		
		DynamicPortalObjectContainer.addToCache(id, this);
	}
	



	@Override
	public LocalizedString getDisplayName() {
		return templateOrig.getDisplayName();
	}

	@Override
	public Map getDisplayNames() {
		return templateOrig.getDisplayNames();
	}
	
	public PortalObject getParent()	{
		return page;	
	}

	

	@Override
	public String getName() {
		if( templateOrig != null)
			return templateOrig.getName();
		return name;
	}

	@Override
	public ObjectNode getObjectNode() {
		return templateOrig.getObjectNode();
	}

	@Override
	public Map<String, String> getProperties()	{
		if( properties == null)	{
			properties =  ((Window) templateOrig).getProperties();
		}
		return properties;
		
	}
	
	@Override
	public void setDeclaredProperty(String name, String value) {
		}
	
	@Override
	public String getDeclaredProperty(String name) {

		
		// En priorité les valeurs de l'instance
		String value = null;
		if( getProperties() != null)	{
			value =  getProperties().get(name);
		}
		/*
		if( templateOrig != null)	{
			// Les propriétés de la page surchargent celles des templates
			// TODO : surcharge + fine (attribut par attribut)

			if( "1".equals(templateOrig.getDeclaredProperty("osivia.propsOverload")))	{
				value = ((DynamicTemplatePage) getPage()).getPageBean().getWindowProperties().get(name);
				if( value != null)
					return value;
			}
			
		}*/
		
		// Sinon les valeurs par délégation
		if( value == null)	
			if( templateOrig != null)	{
				value = templateOrig.getDeclaredProperty(name);
				return value;
			}
		
		return value;
	}
	
	 public String toString()
	   {
		 if( templateOrig != null)
	      return templateOrig.toString();
		 else
			 return("DynamicTemplateWindow " + getName());
	   }

}
