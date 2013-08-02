package org.osivia.portal.core.portalobjects;

import java.util.Locale;
import java.util.Map;

import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.dynamic.DynamicPageBean;
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
		return this.pageBean;
	}



	public PortalObject getParent()	{
		if (this.parent == null)	{
			this.parent = this.container.getObject(this.parentId);
		}
		return this.parent;
	}


	private DynamicTemplatePage(PortalObjectContainer container, PortalObjectId parentId, String name, Map displayNames, PortalObjectImpl template,  DynamicPortalObjectContainer dynamicContainer, DynamicPageBean pageBean) throws IllegalArgumentException {
		super( container,  parentId,  name,  template,   dynamicContainer);

		this.pageBean = pageBean;


		//localProperties = new HashMap<String, String>();
		this.localProperties.putAll(pageBean.getPageProperties());

		this.localProperties.put(PageUtils.TAB_ORDER, "" + pageBean.getOrder());
        this.localProperties.put(InternalConstants.PAGE_PROP_NAME_DYNAMIC, InternalConstants.PROP_VALUE_ON);


		this.localDisplayName = displayNames;

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
		String value = this.localProperties.get(name);
		if( value == null) {
            value =  super.getDeclaredProperty(name);
        }
		return value;

	}

	   public LocalizedString getDisplayName()
	   {
	      if (this.localDisplayName != null)
	      {
	         return new LocalizedString(this.localDisplayName, Locale.ENGLISH);
	      }
	      else
	      {
	         return super.getDisplayName();
	      }
	   }

		public boolean isClosable() {
			return this.pageBean.isClosable();
		}


	   public String toString()
	   {
	      return this.getId().toString();
	   }



}
