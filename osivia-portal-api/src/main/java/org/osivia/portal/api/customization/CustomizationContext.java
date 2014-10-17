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
 */
package org.osivia.portal.api.customization;

import java.util.Map;

import org.osivia.portal.api.context.PortalControllerContext;


/**
 * Customization context.
 * Identifies inputs ant outputs of a customization point.
 *
 * @author Jean-Sébastien Steux
 */
public class CustomizationContext {

    /** Customization attributes. */
    private final Map<String, Object> attributes;

    /** Portal controller context. */
    private PortalControllerContext portalControllerContext;


    /**
     * Constructor.
     *
     * @param attributes customization attributes
     */
    public CustomizationContext(Map<String, Object> attributes) {
        super();
        this.attributes = attributes;
    }

    /**
     * Constructor.
     *
     * @param attributes customization attributes
     * @param portalControllerContext portal controller context
     */
    public CustomizationContext(Map<String, Object> attributes, PortalControllerContext portalControllerContext) {
        super();
        this.attributes = attributes;
        this.portalControllerContext = portalControllerContext;
    }


    /**
     * Getter for portalControllerContext.
     *
     * @return the portalControllerContext
     */
    public PortalControllerContext getPortalControllerContext() {
        return this.portalControllerContext;
    }

    /**
     * Setter for portalControllerContext.
     *
     * @param portalControllerContext the portalControllerContext to set
     */
    public void setPortalControllerContext(PortalControllerContext portalControllerContext) {
        this.portalControllerContext = portalControllerContext;
    }

    /**
     * Getter for attributes.
     *
     * @return the attributes
     */
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

}
