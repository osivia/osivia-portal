package org.osivia.portal.core.portalobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.impl.portlet.state.LocalPortletInvoker;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.PageUtils;
import org.osivia.portal.core.page.PortalObjectContainer;


/**
 * Gestion des pages CMS dont le contenu est dynamique (CMS_LAYOUT)
 * 
 * @author jeanseb
 * 
 */
public class TemplatePage extends DynamicPage implements ITemplatePortalObject {

	PageImpl template;
	PortalObject parent;
	List<Window> windows;
	List<PortalObject> children;
	Map<String, String> localProperties = new HashMap<String, String>();

	protected PortalObjectId id = null;
	protected PortalObjectId parentId = null;
	String name;

	protected TemplatePage(PortalObjectContainer container, PortalObjectId parentId, String name,
			PortalObjectImpl template, DynamicPortalObjectContainer dynamicContainer) throws IllegalArgumentException {
		super();

		this.name = name;
		this.container = container;
		this.dynamicContainer = dynamicContainer;

		containerContext = template.getObjectNode().getContext();
		setObjectNode(template.getObjectNode());

		this.template = (PageImpl) template;

		this.parentId = parentId;

		this.id = new PortalObjectId("", new PortalObjectPath(parentId.getPath().toString().concat("/").concat(name),
				PortalObjectPath.CANONICAL_FORMAT));

		// Optimisation : ajout cache
		DynamicPortalObjectContainer.addToCache(this.id, this);
	}

	public PortalObject getParent() {
		if (parent == null) {
			parent = container.getObject(parentId);
		}
		return parent;
	}

	DynamicWindow createSessionWindow(DynamicWindowBean dynamicWindowBean) {
		return new DynamicTemplateWindow(this, dynamicWindowBean.getName(), containerContext, dynamicContainer,
				dynamicWindowBean.getUri(), dynamicWindowBean.getProperties(), dynamicWindowBean);
	}

	private List<Window> getWindows() {

		if (windows == null) {
			windows = new ArrayList<Window>();
			if (template != null) {
				Collection childs = template.getChildren(PortalObject.WINDOW_MASK);

				for (Object child : childs) {
					if (child instanceof WindowImpl)
						windows.add(new DynamicTemplateWindow(this, (WindowImpl) child, ((WindowImpl) child).getName(),
								containerContext, dynamicContainer));

				}

				// ajout fenetre dynamiques
				windows.addAll(getDynamicWindows().values());
			}

			return windows;
		}

		return windows;
	}

	@Override
	public Collection getChildren() {

		if (children == null) {

			children = new ArrayList<PortalObject>();

			for (Object po : template.getChildren()) {

				if (po instanceof WindowImpl)
					children.add(new DynamicTemplateWindow(this, (WindowImpl) po, ((WindowImpl) po).getName(),
							containerContext, dynamicContainer));

				// TODO : childs
				/*
				 * if( po instanceof PageImpl) children.add( new
				 * TemplatePage(container, this.getId(), ((PageImpl)
				 * po).getName(), (PortalObjectImpl) po, dynamicContainer,
				 * pageBean)); }
				 */

				children.addAll(getDynamicWindows().values());
			}

			// Indispensable ???
			if (getDeclaredProperty("pia.cms.layoutRules") != null)
				children.add(CMSTemplatePage.createPage(container, parentId, template, dynamicContainer));
		}

		return children;
	}

	@Override
	public Collection getChildren(int wantedMask) {

		if (wantedMask != PortalObject.WINDOW_MASK)
			return template.getChildren(wantedMask);
		else {

			List<Window> windows = getWindows();

			/*
			 * for( Window window : windows) { logger.debug("cms.uri" +
			 * window.getProperties().get("pia.cms.uri")); }
			 */

			return getWindows();

		}
	}

	@Override
	public PortalObject getChild(String name) {
		Window child = getDynamicWindows().get(name);

		if (child != null)
			return child;
		else {
			PortalObject po = template.getChild(name);

			if (po instanceof WindowImpl)
				return new DynamicTemplateWindow(this, (WindowImpl) po, ((WindowImpl) po).getName(), containerContext,
						dynamicContainer);

			// TODO : template childs

			/*
			 * if( po instanceof PageImpl) return new TemplatePage(container,
			 * this.getId(), po.getName(), (PortalObjectImpl) po,
			 * dynamicContainer, pageBean);
			 */

			if (CMSTemplatePage.PAGE_NAME.equals(name))
				return container
						.getObject(new PortalObjectId("", getId().getPath().getChild(CMSTemplatePage.PAGE_NAME)));

			return null;
		}
	}

	@Override
	public boolean equals(Object arg0) {
		return template.equals(arg0);
	}

	@Override
	public org.jboss.portal.common.i18n.LocalizedString getDisplayName() {
		return template.getDisplayName();
	}

	@Override
	public Map getDisplayNames() {
		return template.getDisplayNames();
	}

	@Override
	public PortalObjectId getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	// TODO : pas forcement necessaire de précharger toutes les propriétés
	// peut-etre plus optimiser de modifier uniquement le getproperty
	// (+performant)
	// voir quand le getProperties est appelé

	@Override
	public Map getProperties() {

		if (properties == null) {
			// Les données de la page sont paratagées entre les threads

			Map<String, String> sharedProperties = PageProperties.getProperties().getPagePropertiesMap();
			
			String pageId = getId().toString();
			
			String fetchedProperties = sharedProperties.get("pia.fetchedPortalProperties");

			if ( ! pageId.equals(fetchedProperties)) {

				properties = new HashMap<String, String>();

				// Propriétés du template

				Map templateProperties = template.getProperties();
				if (templateProperties != null) {
					for (Object key : templateProperties.keySet()) {
						properties.put((String) key, (String) templateProperties.get(key));
					}
				}
				


				// Le template est surchargé par les propriétés de la page parent

				Map inheritedProperties = getParent().getProperties();
				if (inheritedProperties != null) {
					for (Object key : inheritedProperties.keySet()) {
						if (!ThemeConstants.PORTAL_PROP_LAYOUT.equals(key) && !ThemeConstants.PORTAL_PROP_THEME.equals(key))
							properties.put((String) key, (String) inheritedProperties.get(key));
						else	{
							if( ThemeConstants.PORTAL_PROP_THEME.equals(key))	{
								// Le theme est surchargé par héritage s'il n'ont pas été défini explicitement dans le template
								if( template.getDeclaredProperty( (String) key) == null)
									properties.put((String) key, (String) inheritedProperties.get(key));
							}
						}
					}
				}
				
				// Propriétés locales
				for (Object key : localProperties.keySet()) {
					properties.put((String) key, (String) localProperties.get(key));
				}

				

				properties.put("pia.fetchedPortalProperties", pageId);

				sharedProperties.putAll(properties);

			} else
				properties = sharedProperties;

		}

		return properties;
	}

	public String getProperty(String name) {

		return (String) (getProperties().get(name));
	}

	@Override
	public ObjectNode getObjectNode() {
		return template.getObjectNode();
	}

	@Override
	public void setDeclaredProperty(String name, String value) {
		/*
		 * if( ThemeConstants.PORTAL_PROP_REGION.equals(name) ||
		 * ThemeConstants.PORTAL_PROP_ORDER.equals(name) )
		 */
		localProperties.put(name, value);
		/*
		 * else super.setDeclaredProperty(name, value);
		 */
	}

	@Override
	public String getDeclaredProperty(String name) {

		String value = null;

		value = localProperties.get(name);
		
		if (value == null)
			return template.getDeclaredProperty(name);
		else
			return value;
	}

	public String toString() {
		return getId().toString();
	}

	public PortalObject getTemplate() {
		return template;
	}

	public Page getEditablePage() {
		return null;
	}

	public boolean isClosable() {
		return true;
	}

}
