package org.osivia.portal.api.editor;

import org.osivia.portal.api.context.PortalControllerContext;

import javax.portlet.PortletException;
import java.io.IOException;

/**
 * Editor module resource.
 *
 * @author Cédric Krommenhoek
 */
public interface EditorModuleResource {

    /**
     * Serve editor module resource.
     *
     * @param portalControllerContext portal controller context
     */
    void serve(PortalControllerContext portalControllerContext) throws PortletException, IOException;

}
