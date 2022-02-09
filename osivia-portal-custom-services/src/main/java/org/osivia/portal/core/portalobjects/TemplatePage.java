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

import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.IPortalObjectContainer;


/**
 * Gestion des pages CMS dont le contenu est dynamique (CMS_LAYOUT).
 *
 * @author jeanseb
 * @see DynamicPage
 * @see ITemplatePortalObject
 */
public class TemplatePage extends DynamicPage implements ITemplatePortalObject {

    /** Template. */
    private final PageImpl template;
    /** Parent portal object. */
    /** Template. */
    private final String theme;
    private PortalObject parent;
    /** Windows. */
    private List<Window> windows;
    /** Children portal objects. */
    private List<PortalObject> children;
    /** Local properties. */
    private final Map<String, String> localProperties = new HashMap<String, String>();
    /** Portal object identifier. */
    private PortalObjectId id = null;
    /** Parent portal object identifier. */
    private PortalObjectId parentId = null;
    /** Name. */
    private final String name;


    /**
     * Constructor.
     *
     * @param container portal object container
     * @param parentId parent portal object identifier
     * @param name name
     * @param template template portal object
     * @param dynamicContainer dynamic portal object container
     */
    protected TemplatePage(IPortalObjectContainer container, PortalObjectId parentId, String name, PortalObjectImpl template, String theme,  
            DynamicPortalObjectContainer dynamicContainer) {
        super();

        this.name = name;
        this.container = container;
        this.dynamicContainer = dynamicContainer;

        this.containerContext = template.getObjectNode().getContext();
        this.setObjectNode(template.getObjectNode());

        this.template = (PageImpl) template;
        this.theme = theme;

        this.parentId = parentId;

        this.id = new PortalObjectId("", new PortalObjectPath(parentId.getPath().toString().concat("/").concat(name), PortalObjectPath.CANONICAL_FORMAT));

        // Optimisation : ajout cache
        DynamicPortalObjectContainer.addToCache(this.id, this);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PortalObject getParent() {
        if (this.parent == null) {
            this.parent = this.container.getObject(this.parentId);
        }
        return this.parent;
    }

    
    

    /**
     * {@inheritDoc}
     */
    @Override
    DynamicWindow createSessionWindow(DynamicWindowBean dynamicWindowBean) {
        return new DynamicTemplateWindow(this, dynamicWindowBean.getName(), this.containerContext, this.dynamicContainer, dynamicWindowBean.getUri(),
                dynamicWindowBean.getProperties(), dynamicWindowBean);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((this.template == null) ? 0 : this.template.hashCode());
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        TemplatePage other = (TemplatePage) obj;
        if (this.template == null) {
            if (other.template != null) {
                return false;
            }
        } else if (!this.template.equals(other.template)) {
            return false;
        }
        return true;
    }


    /**
     * Access to windows list.
     *
     * @return windows list
     */
    private List<Window> getWindows() {
        if (this.windows == null) {
            this.windows = new ArrayList<Window>();
            if (this.template != null) {
                Collection<?> childs = this.template.getChildren(PortalObject.WINDOW_MASK);

                for (Object child : childs) {
                    if (child instanceof WindowImpl) {
                        this.windows.add(new DynamicTemplateWindow(this, (WindowImpl) child, ((WindowImpl) child).getName(), this.containerContext,
                                this.dynamicContainer));
                    }

                }

                // ajout fenetre dynamiques
                this.windows.addAll(this.getDynamicWindows().values());
            }
        }

        return this.windows;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<PortalObject> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<PortalObject>();

            for (Object po : this.template.getChildren()) {

                if (po instanceof WindowImpl) {
                    this.children.add(new DynamicTemplateWindow(this, (WindowImpl) po, ((WindowImpl) po).getName(), this.containerContext,
                            this.dynamicContainer));
                }

                this.children.addAll(this.getDynamicWindows().values());
            }

            // Indispensable ???
            if (this.getDeclaredProperty("osivia.cms.layoutRules") != null) {
                this.children.add(CMSTemplatePage.createPage(this.container, this.parentId, this.template, this.theme, this.dynamicContainer));
            }
        }

        return this.children;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Collection getChildren(int wantedMask) {
        if (wantedMask != PortalObject.WINDOW_MASK) {
            return this.template.getChildren(wantedMask);
        } else {
            return this.getWindows();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PortalObject getChild(String name) {
        Window child = this.getDynamicWindows().get(name);

        if (child != null) {
            return child;
        } else {
            PortalObject po = this.template.getChild(name);

            if (po instanceof WindowImpl) {
                return new DynamicTemplateWindow(this, (WindowImpl) po, ((WindowImpl) po).getName(), this.containerContext, this.dynamicContainer);
            }

            if (CMSTemplatePage.PAGE_NAME.equals(name)) {
                return this.container.getObject(new PortalObjectId("", this.getId().getPath().getChild(CMSTemplatePage.PAGE_NAME)));
            }

            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public org.jboss.portal.common.i18n.LocalizedString getDisplayName() {
        return this.template.getDisplayName();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<?, ?> getDisplayNames() {
        return this.template.getDisplayNames();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PortalObjectId getId() {
        return this.id;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return this.name;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getProperties() {
        // TODO : pas forcement necessaire de précharger toutes les propriétés
        // peut-etre plus optimiser de modifier uniquement le getproperty
        // (+performant)
        // voir quand le getProperties est appelé

        if (this.properties == null) {
            // Les données de la page sont paratagées entre les threads

            Map<String, String> sharedProperties = PageProperties.getProperties().getPagePropertiesMap();

            String pageId = this.getId().toString();

            String fetchedProperties = sharedProperties.get("osivia.fetchedPortalProperties");

            if (!pageId.equals(fetchedProperties)) {

                this.properties = new HashMap<String, String>();

                // Propriétés du template

                Map<String, String> templateProperties = this.template.getProperties();
                if (templateProperties != null) {
                    for (Object key : templateProperties.keySet()) {
                        this.properties.put((String) key, templateProperties.get(key));
                    }
                }


                // Le template est surchargé par les propriétés de la page parent

                Map<?, ?> inheritedProperties = this.getParent().getProperties();
                if (inheritedProperties != null) {
                    for (Object key : inheritedProperties.keySet()) {
                        if (!ThemeConstants.PORTAL_PROP_LAYOUT.equals(key) && !ThemeConstants.PORTAL_PROP_THEME.equals(key)) {
                            this.properties.put((String) key, (String) inheritedProperties.get(key));
                        } else {
                            if (ThemeConstants.PORTAL_PROP_THEME.equals(key)) {
                                 if (this.theme != null) {
                                    this.properties.put((String) key, this.theme);
                                }else   {
                                    // Le theme est surchargé par héritage s'il n'ont pas été défini explicitement dans le template

                                    if (this.template.getDeclaredProperty((String) key) == null) {
                                        this.properties.put((String) key, (String) inheritedProperties.get(key));
                                    }
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

                sharedProperties = new HashMap<String, String>();
                sharedProperties.putAll(this.properties);

            } else {
                // JSS 20130703-001
                // corrige le bug de mélange de propriétés entre les pages
                this.properties = new HashMap<String, String>();
                this.properties.putAll(sharedProperties);

            }

        }

        return this.properties;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getProperty(String name) {
        return (this.getProperties().get(name));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectNode getObjectNode() {
        return this.template.getObjectNode();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeclaredProperty(String name, String value) {
        this.localProperties.put(name, value);
    }


    /**
     * Access to template default declared property.
     *
     * @param name name
     * @return default declared property
     */
    protected boolean getTemplateDeclaredPropertyByDefault(String name) {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclaredProperty(String name) {

        String value = null;

        value = this.localProperties.get(name);

        if (value == null) {
            if (this.getTemplateDeclaredPropertyByDefault(name)) {
                return this.template.getDeclaredProperty(name);
            } else {
                return null;
            }
        } else {
            return value;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.getId().toString();
    }


    /**
     * {@inheritDoc}
     */
    public PortalObject getTemplate() {
        return this.template;
    }


    /**
     * {@inheritDoc}
     */
    public Page getEditablePage() {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isClosable() {
        return true;
    }


    /**
     * Getter for localProperties.
     * 
     * @return the localProperties
     */
    public Map<String, String> getLocalProperties() {
        return this.localProperties;
    }

}
