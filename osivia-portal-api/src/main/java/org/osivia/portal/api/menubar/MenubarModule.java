package org.osivia.portal.api.menubar;

import java.util.List;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.DocumentContext;
import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Menubar module.
 *
 * @author Cédric Krommenhoek
 */
public interface MenubarModule {

    /**
     * Customize space.
     *
     * @param portalControllerContext portal controller context
     * @param menubar menubar
     * @param spaceDocumentContext space document context
     * @throws PortalException
     */
    void customizeSpace(PortalControllerContext portalControllerContext, List<MenubarItem> menubar, DocumentContext spaceDocumentContext)
            throws PortalException;


    /**
     * Customize document.
     * 
     * @param portalControllerContext portal controller context
     * @param menubar menubar
     * @param documentContext document context
     * @throws PortalException
     */
    void customizeDocument(PortalControllerContext portalControllerContext, List<MenubarItem> menubar, DocumentContext documentContext) throws PortalException;

}
