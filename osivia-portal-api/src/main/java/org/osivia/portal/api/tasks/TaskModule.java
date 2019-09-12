package org.osivia.portal.api.tasks;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.context.PortalControllerContext;

import java.util.Map;

/**
 * Task module interface.
 *
 * @author Cédric Krommenhoek
 */
public interface TaskModule {

    /**
     * Adapt task item.
     *
     * @param portalControllerContext portal controller context
     * @param document                task document
     * @param properties              task item properties
     */
    void adaptTaskItem(PortalControllerContext portalControllerContext, EcmDocument document, Map<String, String> properties) throws PortalException;

}
