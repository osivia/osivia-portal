package org.osivia.portal.api.editor;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Editor service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface EditorService {

    /**
     * MBean name.
     */
    String MBEAN_NAME = "osivia:service=EditorService";

    /**
     * Window property prefix.
     */
    String WINDOW_PROPERTY_PREFIX = "osivia.editor.";


    /**
     * Get modules.
     *
     * @param portalControllerContext portal controller context
     * @return modules
     */
    List<EditorModule> getModules(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Serve resource.
     *
     * @param portalControllerContext portal controller context
     * @param editorId                editor identifier
     */
    void serveResource(PortalControllerContext portalControllerContext, String editorId) throws PortletException, IOException;


    /**
     * Get temporary attached pictures.
     *
     * @param portalControllerContext portal controller context
     * @param path                    parent document path
     * @return temporary attached pictures
     */
    List<EditorTemporaryAttachedPicture> getTemporaryAttachedPictures(PortalControllerContext portalControllerContext, String path) throws PortalException, IOException;


    /**
     * Add temporary attached picture.
     *
     * @param portalControllerContext portal controller context
     * @param path                    parent document path
     * @param picture                 temporary attached picture
     */
    void addTemporaryAttachedPicture(PortalControllerContext portalControllerContext, String path, EditorTemporaryAttachedPicture picture) throws PortalException, IOException;


    /**
     * Clear temporary attached pictures.
     *
     * @param portalControllerContext portal controller context
     * @param path                    parent document path
     */
    void clearTemporaryAttachedPictures(PortalControllerContext portalControllerContext, String path) throws PortalException, IOException;


    /**
     * Clear all temporary attached pictures.
     * @param httpSession HTTP session
     */
    void clearAllTemporaryAttachedPictures(HttpSession httpSession) throws PortalException;

}
