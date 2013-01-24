package org.osivia.portal.core.portalobjects;

import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.page.PortalObjectContainer;


public class CMSTemplatePage extends TemplatePage implements ITemplatePortalObject {
	
	public static final String PAGE_NAME = "_CMS_LAYOUT";
	
	public static CMSTemplatePage createPage(PortalObjectContainer container, PortalObjectId parentId, PortalObjectImpl template,  DynamicPortalObjectContainer dynamicContainer){
		CMSTemplatePage page = null;
		

		 page = new CMSTemplatePage(container, parentId,  template, dynamicContainer);
				return page;
		
	}
	
	private CMSTemplatePage(PortalObjectContainer container, PortalObjectId parentId, PortalObjectImpl template,  DynamicPortalObjectContainer dynamicContainer) throws IllegalArgumentException {
		super( container,  parentId,  PAGE_NAME,  template,   dynamicContainer);
	
	}
	
	protected boolean getTemplateDeclaredPropertyByDefault( String name)	{
		
		// JSS 20130124
		// le basePath d'un template doit etre ignoré
		// Corrige le bug dde contextualisation positionné par errueur sur un template
		
		if( "osivia.cms.basePath".equals(name))	{
			return false;
		}
		
		return true;
	}

	

	
	public Page getEditablePage() {
		PortalObject parent = getParent();
		if( parent instanceof Page && (! (parent instanceof ITemplatePortalObject)))
			return (Page) parent;
		else
			return null;
	}



}
