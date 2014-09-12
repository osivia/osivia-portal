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
package org.osivia.portal.api.theming;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;

/**
 * Regions theming service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IRegionsThemingService {

    /** MBean name. */
    static final String MBEAN_NAME = "osivia:service=RegionsThemingService";


    /**
     * Add rendered region.
     *
     * @param renderPageCommand render page command
     * @param pageRendition page rendition
     * @param renderedRegion rendered region bean
     * @throws ControllerException
     */
    void addRegion(RenderPageCommand renderPageCommand, PageRendition pageRendition, RenderedRegionBean renderedRegion) throws ControllerException;


    /**
     * Decorate portlets region.
     *
     * @param renderPageCommand render page command
     * @param portletsRegion portlets region bean
     */
    void decorateRegion(RenderPageCommand renderPageCommand, PortletsRegionBean portletsRegion);


    /**
     * Get layout context path from render page command.
     * 
     * @param renderPageCommand render page command
     * @return layout context path
     */
    String getLayoutContextPath(RenderPageCommand renderPageCommand);


    /**
     * Get theme context path from render page command.
     * 
     * @param renderPageCommand render page command
     * @return theme context path
     */
    String getThemeContextPath(RenderPageCommand renderPageCommand);


    /**
     * Get attribute from his name.
     *
     * @param renderPageCommand render page command
     * @param name attribute name
     * @return attribute
     */
    Object getAttribute(RenderPageCommand renderPageCommand, PageRendition pageRendition, String name);

}
