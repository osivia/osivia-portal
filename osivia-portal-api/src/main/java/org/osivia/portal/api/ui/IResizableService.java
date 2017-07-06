package org.osivia.portal.api.ui;

import org.dom4j.Element;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * jQuery UI resizable component service interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface IResizableService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=ResizableService";


    /**
     * Get rezisable width.
     * 
     * @param portalControllerContext portal controller context
     * @param linkedToTasks linked to tasks indicator
     * @return resizable width, may be null
     * @throws PortalException
     */
    Integer getWidth(PortalControllerContext portalControllerContext, boolean linkedToTasks) throws PortalException;


    /**
     * Save resizable width.
     * 
     * @param portalControllerContext portal controller context
     * @param linkedToTasks linked to tasks indicator
     * @param width resizable width
     * @throws PortalException
     */
    void saveWidth(PortalControllerContext portalControllerContext, boolean linkedToTasks, Integer width) throws PortalException;


    /**
     * Get save resizable width URL.
     * 
     * @param portalControllerContext portal controller context
     * @param linkedToTasks linked to tasks indicator
     * @return URL
     * @throws PortalException
     */
    String getSaveWidthUrl(PortalControllerContext portalControllerContext, boolean linkedToTasks) throws PortalException;


    /**
     * Get resizable tag DOM element.
     * 
     * @param portalControllerContext portal controller context
     * @param enabled enabled resizing indicator
     * @param linkedToTasks linked to tasks indicator
     * @param bodyContent body HTML content
     * @param htmlClasses HTML classes
     * @param minWidth min width
     * @param maxWidth max width
     * @return DOM element
     * @throws PortalException
     */
    Element getTagContent(PortalControllerContext portalControllerContext, Boolean enabled, Boolean linkedToTasks, String bodyContent, String htmlClasses,
            Integer minWidth, Integer maxWidth) throws PortalException;

}
