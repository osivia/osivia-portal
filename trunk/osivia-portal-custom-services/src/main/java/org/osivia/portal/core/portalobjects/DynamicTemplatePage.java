package org.osivia.portal.core.portalobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.page.PageUtils;
import org.osivia.portal.core.page.PortalObjectContainer;


public class DynamicTemplatePage extends TemplatePage implements ITemplatePortalObject {
	
	

 Map localDisplayName;
	
	public static DynamicTemplatePage createPage(PortalObjectContainer container, PortalObjectId parentId, String name,  Map displayNames,PortalObjectImpl template,  DynamicPortalObjectContainer dynamicContainer, DynamicPageBean pageBean, PortalObjectId poid){
		DynamicTemplatePage page = null;
		try	{
			 page = new DynamicTemplatePage(container, parentId, name, displayNames, template, dynamicContainer, pageBean);
		} catch( Exception e){
			PortalObjectId pageId = new PortalObjectId("", new PortalObjectPath(parentId.getPath().toString()
					.concat("/").concat(name), PortalObjectPath.CANONICAL_FORMAT));
			
			// Page non accessible, le template peut avoir été supprimé (auquel cas le template est null)
			// On supprime la page dynamique pour ne plus rencontrer d'erreurs

			
			dynamicContainer.removeDynamicPage(pageId.toString(PortalObjectPath.SAFEST_FORMAT));
			
			throw new RuntimeException("Page "+ pageId + " has not be created. Exception = "+e.getMessage()+". Check the template " + poid.toString());

		}
		
		return page;
		
	}
	

	DynamicPageBean pageBean;

	

	public DynamicPageBean getPageBean() {
		return pageBean;
	}



	public PortalObject getParent()	{
		if (parent == null)	{
			parent = container.getObject(parentId);
		}
		return parent;	
	}

	
	private DynamicTemplatePage(PortalObjectContainer container, PortalObjectId parentId, String name, Map displayNames, PortalObjectImpl template,  DynamicPortalObjectContainer dynamicContainer, DynamicPageBean pageBean) throws IllegalArgumentException {
		super( container,  parentId,  name,  template,   dynamicContainer);
	
		this.pageBean = pageBean;
		
		
		//localProperties = new HashMap<String, String>();
		this.localProperties.putAll(pageBean.getPageProperties());
		
		this.localProperties.put(PageUtils.TAB_ORDER, "" + pageBean.getOrder());
		
		localDisplayName = displayNames;
		
		/*
		
		// TODO : analyser si on peut faire du lazy fetching sur les propriétés
		
		properties = new HashMap<String, String>();
		
		for( Object key : template.getProperties().keySet())	{
			properties.put((String)key, (String)template.getProperties().get(key));
		}
		
		// Surcharge par les propriétés de la page

		this.properties.putAll(pageBean.getPageProperties());
		*/

	}
	
	public String getDeclaredProperty(String name) {
		String value = localProperties.get(name);
		if( value == null)
			value =  super.getDeclaredProperty(name);
		return value;

	}
	
	   public LocalizedString getDisplayName()
	   {
	      if (localDisplayName != null)
	      {
	         return new LocalizedString(localDisplayName, Locale.ENGLISH);
	      }
	      else
	      {
	         return super.getDisplayName();
	      }
	   }
	
		public boolean isClosable() {
			return pageBean.isClosable();
		}

	
	   public String toString()
	   {
	      return getId().toString();
	   }


	
}
