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
import org.osivia.portal.core.page.IPortalObjectContainer;


public class DynamicPersistentPage extends DynamicPage {
	PageImpl orig;
	List<Window> windows;
	PortalObject parent;
	Portal portal;

	public DynamicPersistentPage(IPortalObjectContainer container, PageImpl orig,  DynamicPortalObjectContainer dynamicContainer) throws IllegalArgumentException {
		super();

		this.container = container;
		this.dynamicContainer = dynamicContainer;

		this.containerContext = orig.getObjectNode().getContext();
		this.setObjectNode(orig.getObjectNode());

		this.orig = orig;


		// Optimisation  : ajout cache
		DynamicPortalObjectContainer.addToCache(orig.getId(), this);

	}

	DynamicWindow createSessionWindow ( DynamicWindowBean dynamicWindowBean)	{
		return new DynamicPersistentWindow( this.container, this, dynamicWindowBean.getName(), this.containerContext, this.dynamicContainer,  dynamicWindowBean.getUri(), dynamicWindowBean.getProperties(), dynamicWindowBean)	;
	}

	public PortalObject getParent()	{
		if (this.parent == null)	{
			// TODO :verifier que  ne pas passer par le container optimise réellement les perfs

			//parent = container.getObject(new PortalObjectId("", orig.getId().getPath().getParent()));

			this.parent = this.orig.getParent();

			if( this.parent instanceof PageImpl) {
                this.parent = new DynamicPersistentPage( this.container, (PageImpl) this.parent,   this.dynamicContainer);
            } else if (this.parent instanceof PortalImpl) {
                this.parent =  new DynamicPortal( this.container, (PortalImpl) this.parent,   this.dynamicContainer);
            } else {
                this.parent = this.container.getObject(new PortalObjectId("", this.orig.getId().getPath().getParent()));
            }


		}
		return this.parent;
	}


	// Ajout v.0.13 : correction perte fenetres dynamiques au login
	   public Portal getPortal()
	   {
		if (this.portal == null) {
			PortalObject object = this.orig.getParent();
			while ((object != null) && !(object instanceof Portal)) {
				object = object.getParent();
			}

			if( object instanceof PortalImpl) {
                this.portal = new DynamicPortal(this.container, (PortalImpl) object, this.dynamicContainer);
            }
		}

		return this.portal;
	   }


	private List<Window> getWindows() {

		if( this.windows == null)	{
			Collection childs = this.orig.getChildren( PortalObject.WINDOW_MASK);
			this.windows = new ArrayList<Window>();
			for( Object child : childs)	{
				this.windows.add( new DynamicPersistentWindow(this.container, (WindowImpl)child, this.dynamicContainer));
			}

			//ajout fenetre dynamiques
			this.windows.addAll(this.getDynamicWindows ().values());

			return this.windows;
		}


		return this.windows;
	}


	@Override
	public Collection getChildren() {

		if( this.windows == null)	{

		this.windows = new ArrayList<Window>();

		for( Object po: this.orig.getChildren())	{
			if( po instanceof Window)	{
				Window window = (Window) po;
				this.windows.add( window);
			}
			}

		this.windows.addAll(this.getDynamicWindows ().values());
		}


		return this.windows;
	}


	List<Window> notFetchedWindows;
	public List<Window> getNotFetchedWindows () {

		if( this.notFetchedWindows == null)	{

			Collection childs = this.orig.getChildren( PortalObject.WINDOW_MASK);
			this.notFetchedWindows = new ArrayList<Window>();
			for( Object child : childs)	{
				this.notFetchedWindows.add( new NotFetchedPersistentWindow(this.getId(), (WindowImpl)child, ((WindowImpl)child).getName(),  this.dynamicContainer));
			}

			//ajout fenetre dynamiques
			this.notFetchedWindows.addAll(this.getDynamicWindows ().values());

			return this.notFetchedWindows;
		}


		return this.notFetchedWindows;
	}



	@Override
	public Collection getChildren(int wantedMask) {

		if( wantedMask != PortalObject.WINDOW_MASK) {
            return this.orig.getChildren( wantedMask);
        } else	{

//			List<Window> windows = getWindows();

			/*
			 for( Window window : windows)	{
				 logger.debug("cms.uri" + window.getProperties().get("osivia.cms.uri"));
			 }
*/

				return this.getWindows();

		}
	}



	@Override
	public PortalObject getChild(String name) {
		Window child = this.getDynamicWindows().get(name);

		if( child != null) {
            return child;
        } else {
            return this.orig.getChild(name);
        }
	}





	@Override
	public boolean equals(Object arg0) {
		return this.orig.equals(arg0);
	}





	@Override
	public org.jboss.portal.common.i18n.LocalizedString getDisplayName() {
		return this.orig.getDisplayName();
	}

	@Override
	public Map getDisplayNames() {
		return this.orig.getDisplayNames();
	}

	@Override
	public PortalObjectId getId() {
		return this.orig.getId();
	}

	@Override
	public String getName() {
		return this.orig.getName();
	}

	@Override
	public Map getProperties() {
		return this.orig.getProperties();
	}


	@Override
	public ObjectNode getObjectNode() {
		return this.orig.getObjectNode();
	}

	@Override
	public void setDeclaredProperty(String name, String value) {

			this.orig.setDeclaredProperty(name, value);

	}

	@Override
	public String getDeclaredProperty(String name) {
			return this.orig.getDeclaredProperty(name);

	}


	@Override
	public Map<String, String> getDeclaredProperties() {
		return this.orig.getDeclaredProperties();
	}


	   public void setDisplayName(LocalizedString displayName){
		   this.orig.setDisplayName(displayName);
	   }


}
