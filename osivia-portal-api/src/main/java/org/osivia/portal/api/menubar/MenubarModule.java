package org.osivia.portal.api.menubar;

import java.util.List;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.DocumentContext;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Menubar module.
 *
 * @author Cédric Krommenhoek
 */
public interface MenubarModule {

    /**
     * Customize menubar.
     *
     * @param portalControllerContext portal controller context
     * @param menubar menubar
     * @param spaceDocumentContext space document context
     * @throws PortalException
     */
    void customizeMenubar(PortalControllerContext portalControllerContext, List<MenubarItem> menubar,
            DocumentContext<? extends EcmDocument> spaceDocumentContext) throws PortalException;

}
