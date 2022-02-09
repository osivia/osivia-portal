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

import java.util.HashMap;
import java.util.Map;

import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.content.Content;
import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.page.IPortalObjectContainer;


/**
 * Dynamic persistant window.
 *
 * @see DynamicWindow
 */
@SuppressWarnings("unchecked")
public class DynamicPersistentWindow extends DynamicWindow {

    /** Original window. */
    private WindowImpl orig;
    /** Portal object container. */
    private final IPortalObjectContainer container;
    /** Declared properties. */
    private Map<String, String> declaredProperties;


    /**
     * {@inheritDoc}
     */
    @Override
    public Page getPage() {
        if (this.page != null) {
            return this.page;
        } else {
            Page page = new DynamicPersistentPage(this.container, (PageImpl) this.orig.getPage(), this.dynamicContainer);
            return page;
        }
    }


    /**
     * Constructor for dynamic window.
     *
     * @param container portal object container
     * @param page dynamic page
     * @param path path
     * @param context context
     * @param dynamicContainer dynamic portal object container
     * @param uri URI
     * @param localProperties local properties
     * @param dynaBean dynamic window bean
     */
    public DynamicPersistentWindow(IPortalObjectContainer container, DynamicPage page, String path, Object context,
            DynamicPortalObjectContainer dynamicContainer, String uri, Map<String, String> localProperties, DynamicWindowBean dynaBean) {
        super(page, path, context, dynamicContainer, dynaBean);

        this.container = container;

        this.contentType = ContentType.PORTLET;
        this.uri = uri;

        // Content
        Content content = this.getContent();
        content.setURI(uri);

        for (String key : localProperties.keySet()) {
            this.setLocalProperty(key, localProperties.get(key));
        }
    }

    /**
     * Constructor with encapsulation.
     * 
     * @param container portal object container
     * @param orig original window
     * @param dynamicContainer dynamic portal object container
     */
    public DynamicPersistentWindow(IPortalObjectContainer container, WindowImpl orig, DynamicPortalObjectContainer dynamicContainer) {
        super();

        this.container = container;
        this.dynamicContainer = dynamicContainer;
        this.setObjectNode(orig.getObjectNode());
        super.setContext(orig.getObjectNode().getContext());
        this.orig = orig;

        this.setSessionWindow(false);

        this.uri = orig.getURI();

        // Content
        Content content = this.getContent();
        content.setURI(this.uri);

        this.id = orig.getId();

        // Optimisation : ajout cache
        DynamicPortalObjectContainer.addToCache(this.id, this);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ContentType getContentType() {
        if ((this.contentType == null) && (this.orig != null)) {
            this.contentType = this.orig.getContentType();
        }
        return this.contentType;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PortalObjectId getId() {
        if (this.id != null) {
            return this.id;
        } else {
            return this.orig.getId();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizedString getDisplayName() {
        return this.orig.getDisplayName();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<?, ?> getDisplayNames() {
        return this.orig.getDisplayNames();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        if (this.orig != null) {
            return this.orig.getName();
        }
        return this.name;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectNode getObjectNode() {
        return this.orig.getObjectNode();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getProperties() {
        if (this.properties != null) {
            return this.properties;
        } else {
            return ((Window) this.orig).getProperties();
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeclaredProperty(String name, String value) {
        if (this.orig != null) {
            this.orig.setDeclaredProperty(name, value);
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclaredProperty(String name) {
        // modif v1.1 : priorité sur template
        if (this.orig != null) {
            return this.orig.getDeclaredProperty(name);
        }

        // En priorité les valeurs de l'instance
        String value = null;
        if (this.getProperties() != null) {
            value = this.getProperties().get(name);
        }

        return value;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getDeclaredProperties() {
        if (this.declaredProperties == null) {
            this.declaredProperties = new HashMap<String, String>();

            // Ajout v.0.13 : null pointer exception
            if (this.orig != null) {
                Map<String, String> declProps = this.orig.getDeclaredProperties();
                for (String key : declProps.keySet()) {
                    this.declaredProperties.put(key, declProps.get(key));
                }
                if (this.properties != null) {
                    for (String key : this.properties.keySet()) {
                        this.declaredProperties.put(key, this.properties.get(key));
                    }
                }
            }
        }
        return this.declaredProperties;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (this.orig != null) {
            return this.orig.toString();
        } else {
            return ("dynamicWindow " + this.getName());
        }
    }

}
