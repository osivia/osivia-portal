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
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.impl.model.portal.AbstractPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.ContextImpl;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PortalImpl;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.page.PortalObjectContainer;



@SuppressWarnings("unchecked")
public  class DynamicContext extends ContextImpl {
	
	protected static final Log log = LogFactory.getLog(DynamicContext.class);

	protected AbstractPortalObjectContainer.ContainerContext containerContext;
	protected DynamicPortalObjectContainer dynamicContainer;
	
	PortalObjectContainer container;
	
	ContextImpl orig;
	List<DynamicPortal> children;
	
	protected String name;
	
	public DynamicContext(PortalObjectContainer container, ContextImpl orig,  DynamicPortalObjectContainer dynamicContainer) throws IllegalArgumentException {
		super();
		
		this.dynamicContainer = dynamicContainer;
		this.container = container;
		
		containerContext = orig.getObjectNode().getContext();
		setObjectNode(orig.getObjectNode());	
		
		this.orig = orig;
		
		
		// Optimisation  : ajout cache
		DynamicPortalObjectContainer.addToCache(orig.getId(), this);
	}
	
	
	  public Portal getDefaultPortal()
	   {
		  ServerInvocation invocation = dynamicContainer.getInvocation();
		  
		  /* Recherche parmi les hosts déclarés */
		  
		  if( invocation != null){
			  HttpServletRequest request = invocation.getServerContext().getClientRequest();
			  String host = request.getServerName();
			  
			  
			  if( host != null)	{
				  getChildren();
				  for( DynamicPortal portal: children){
					 if( host.equals( portal.getDeclaredProperty("osivia.site.hostName")))
							 return portal;
				 }
			  }
		  }

		  
	      PortalObject child = orig.getDefaultPortal();
	      if (child instanceof Portal)
	      {
	         return (Portal)child;
	      }
	      if (child != null)
	      {
	         log.warn("Default child is not a portal " + child);
	      }
	      return null;
	   }
	
	@Override
	public Collection getChildren() {
		
		if( children == null)	{
		
			children = new ArrayList<DynamicPortal>();
		
			for( Object po: orig.getChildren())	{

				children.add( new DynamicPortal(container, (PortalImpl) po, dynamicContainer));
			}
		}


		return children;
	}
	
	
	@Override
	public Collection getChildren(int wantedMask) {
		if( wantedMask != PortalObject.PORTAL_MASK)
			return super.getChildren(wantedMask);
		return getChildren();
		
	}

	
	
	@Override
	public PortalObject getChild(String name) {

		 getChildren();
		 for( DynamicPortal portal: children){
			 if( name.equals(portal.getName()))
					 return portal;
		 }
		
		 return null;
		
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


	

}
