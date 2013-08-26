package org.osivia.portal.core.portalobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.page.PageProperties;
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

		this.containerContext = template.getObjectNode().getContext();
		this.setObjectNode(template.getObjectNode());

		this.template = (PageImpl) template;

		this.parentId = parentId;

		this.id = new PortalObjectId("", new PortalObjectPath(parentId.getPath().toString().concat("/").concat(name),
				PortalObjectPath.CANONICAL_FORMAT));

		// Optimisation : ajout cache
		DynamicPortalObjectContainer.addToCache(this.id, this);
	}

	public PortalObject getParent() {
		if (this.parent == null) {
			this.parent = this.container.getObject(this.parentId);
		}
		return this.parent;
	}

	DynamicWindow createSessionWindow(DynamicWindowBean dynamicWindowBean) {
		return new DynamicTemplateWindow(this, dynamicWindowBean.getName(), this.containerContext, this.dynamicContainer,
				dynamicWindowBean.getUri(), dynamicWindowBean.getProperties(), dynamicWindowBean);
	}

	private List<Window> getWindows() {

		if (this.windows == null) {
			this.windows = new ArrayList<Window>();
			if (this.template != null) {
				Collection childs = this.template.getChildren(PortalObject.WINDOW_MASK);

				for (Object child : childs) {
					if (child instanceof WindowImpl) {
                        this.windows.add(new DynamicTemplateWindow(this, (WindowImpl) child, ((WindowImpl) child).getName(),
								this.containerContext, this.dynamicContainer));
                    }

				}

				// ajout fenetre dynamiques
				this.windows.addAll(this.getDynamicWindows().values());
			}

			return this.windows;
		}

		return this.windows;
	}

	@Override
	public Collection getChildren() {

		if (this.children == null) {

			this.children = new ArrayList<PortalObject>();

			for (Object po : this.template.getChildren()) {

				if (po instanceof WindowImpl) {
                    this.children.add(new DynamicTemplateWindow(this, (WindowImpl) po, ((WindowImpl) po).getName(),
							this.containerContext, this.dynamicContainer));
                }

				// TODO : childs
				/*
				 * if( po instanceof PageImpl) children.add( new
				 * TemplatePage(container, this.getId(), ((PageImpl)
				 * po).getName(), (PortalObjectImpl) po, dynamicContainer,
				 * pageBean)); }
				 */

				this.children.addAll(this.getDynamicWindows().values());
			}

			// Indispensable ???
			if (this.getDeclaredProperty("osivia.cms.layoutRules") != null) {
                this.children.add(CMSTemplatePage.createPage(this.container, this.parentId, this.template, this.dynamicContainer));
            }
		}

		return this.children;
	}

	@Override
	public Collection getChildren(int wantedMask) {

		if (wantedMask != PortalObject.WINDOW_MASK) {
            return this.template.getChildren(wantedMask);
        } else {

			List<Window> windows = this.getWindows();

			/*
			 * for( Window window : windows) { logger.debug("cms.uri" +
			 * window.getProperties().get("osivia.cms.uri")); }
			 */

			return this.getWindows();

		}
	}

	@Override
	public PortalObject getChild(String name) {
		Window child = this.getDynamicWindows().get(name);

		if (child != null) {
            return child;
        } else {
			PortalObject po = this.template.getChild(name);

			if (po instanceof WindowImpl) {
                return new DynamicTemplateWindow(this, (WindowImpl) po, ((WindowImpl) po).getName(), this.containerContext,
						this.dynamicContainer);
            }

			// TODO : template childs

			/*
			 * if( po instanceof PageImpl) return new TemplatePage(container,
			 * this.getId(), po.getName(), (PortalObjectImpl) po,
			 * dynamicContainer, pageBean);
			 */

			if (CMSTemplatePage.PAGE_NAME.equals(name)) {
                return this.container
						.getObject(new PortalObjectId("", this.getId().getPath().getChild(CMSTemplatePage.PAGE_NAME)));
            }

			return null;
		}
	}

	@Override
	public boolean equals(Object arg0) {
		return this.template.equals(arg0);
	}

	@Override
	public org.jboss.portal.common.i18n.LocalizedString getDisplayName() {
		return this.template.getDisplayName();
	}

	@Override
	public Map getDisplayNames() {
		return this.template.getDisplayNames();
	}

	@Override
	public PortalObjectId getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	// TODO : pas forcement necessaire de précharger toutes les propriétés
	// peut-etre plus optimiser de modifier uniquement le getproperty
	// (+performant)
	// voir quand le getProperties est appelé

	@Override
	public Map getProperties() {

		if (this.properties == null) {
			// Les données de la page sont paratagées entre les threads

			Map<String, String> sharedProperties = PageProperties.getProperties().getPagePropertiesMap();
			
			String pageId = this.getId().toString();
			
			String fetchedProperties = sharedProperties.get("osivia.fetchedPortalProperties");

			if ( ! pageId.equals(fetchedProperties)) {

				this.properties = new HashMap<String, String>();

				// Propriétés du template

				Map templateProperties = this.template.getProperties();
				if (templateProperties != null) {
					for (Object key : templateProperties.keySet()) {
						this.properties.put((String) key, (String) templateProperties.get(key));
					}
				}
				


				// Le template est surchargé par les propriétés de la page parent

				Map inheritedProperties = this.getParent().getProperties();
				if (inheritedProperties != null) {
					for (Object key : inheritedProperties.keySet()) {
						if (!ThemeConstants.PORTAL_PROP_LAYOUT.equals(key) && !ThemeConstants.PORTAL_PROP_THEME.equals(key)) {
                            this.properties.put((String) key, (String) inheritedProperties.get(key));
                        } else	{
							if( ThemeConstants.PORTAL_PROP_THEME.equals(key))	{
								// Le theme est surchargé par héritage s'il n'ont pas été défini explicitement dans le template
								if( this.template.getDeclaredProperty( (String) key) == null) {
                                    this.properties.put((String) key, (String) inheritedProperties.get(key));
                                }
							}
						}
					}
				}
				
				// Propriétés locales
				for (Object key : this.localProperties.keySet()) {
					this.properties.put((String) key, this.localProperties.get(key));
				}
			

				this.properties.put("osivia.fetchedPortalProperties", pageId);
				
				sharedProperties= new HashMap<String, String>();
				sharedProperties.putAll(this.properties);

			} else   {
			    // JSS 20130703-001
			    // corrige le bug de mélange de propriétés entre les pages
				this.properties = new HashMap<String, String>();
				this.properties.putAll(sharedProperties);

			}

		}

		return this.properties;
	}

	public String getProperty(String name) {

		return (String) (this.getProperties().get(name));
	}

	@Override
	public ObjectNode getObjectNode() {
		return this.template.getObjectNode();
	}

	@Override
	public void setDeclaredProperty(String name, String value) {
		/*
		 * if( ThemeConstants.PORTAL_PROP_REGION.equals(name) ||
		 * ThemeConstants.PORTAL_PROP_ORDER.equals(name) )
		 */
		this.localProperties.put(name, value);
		/*
		 * else super.setDeclaredProperty(name, value);
		 */
	}
	
	protected boolean getTemplateDeclaredPropertyByDefault( String name)	{
		
		return true;
	}

	@Override
	public String getDeclaredProperty(String name) {

		String value = null;

		value = this.localProperties.get(name);
		
		if (value == null)	{
			if( this.getTemplateDeclaredPropertyByDefault( name)) {
                return this.template.getDeclaredProperty(name);
            } else {
                return null;
            }
		} else {
            return value;
        }
	}

	public String toString() {
		return this.getId().toString();
	}

	public PortalObject getTemplate() {
		return this.template;
	}

	public Page getEditablePage() {
		return null;
	}

	public boolean isClosable() {
		return true;
	}

}
