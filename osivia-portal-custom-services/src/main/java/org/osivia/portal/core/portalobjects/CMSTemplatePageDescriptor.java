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

import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;


/**
 * Contains the template descriptor for cms page creation (uri /_CMS_LAYOUT).
 *
 * @author Jean-Sébastien Steux
 */
public class CMSTemplatePageDescriptor {
    
    /** The template. */
    PortalObjectImpl template;
    
    /** The theme. */
    String theme;
    
    /**
     * Gets the template.
     *
     * @return the template
     */
    public PortalObjectImpl getTemplate() {
        return template;
    }
    
    /**
     * Sets the template.
     *
     * @param template the new template
     */
    public void setTemplate(PortalObjectImpl template) {
        this.template = template;
    }
    
    /**
     * Gets the theme.
     *
     * @return the theme
     */
    public String getTheme() {
        return theme;
    }
    
    /**
     * Sets the theme.
     *
     * @param theme the new theme
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**
     * Instantiates a new CMS template page descriptor.
     *
     * @param template the template
     * @param theme the theme
     */
    public CMSTemplatePageDescriptor(PortalObjectImpl template, String theme) {
        super();
        this.template = template;
        this.theme = theme;
    }

}
