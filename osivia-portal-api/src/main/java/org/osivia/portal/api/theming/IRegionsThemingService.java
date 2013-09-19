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
     * Add region.
     * 
     * @param renderPageCommand render page command
     * @param pageRendition page rendition
     * @param renderedRegion rendered region bean
     * @throws ControllerException
     */
    void addRegion(RenderPageCommand renderPageCommand, PageRendition pageRendition, RenderedRegionBean renderedRegion) throws ControllerException;


    /**
     * Get context path from render page command.
     *
     * @param renderPageCommand render page command
     * @return context path
     */
    String getContextPath(RenderPageCommand renderPageCommand);


    /**
     * Get attribute from his name.
     *
     * @param renderPageCommand render page command
     * @param name attribute name
     * @return attribute
     */
    Object getAttribute(RenderPageCommand renderPageCommand, PageRendition pageRendition, String name);

}
