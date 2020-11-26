package org.osivia.portal.api.editor;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;
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

}
