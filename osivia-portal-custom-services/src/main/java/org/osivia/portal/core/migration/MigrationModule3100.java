package org.osivia.portal.core.migration;

import java.util.Collection;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.core.constants.InternalConstants;

public class MigrationModule3100 extends MigrationModule {

    @Override
    public int getModuleId() {
        return 3100;
    }

   
    
    private void updateFileExplorer(PortalObject portalObject) {

        // scan windows
        Collection<PortalObject> windows = portalObject.getChildren(PortalObject.WINDOW_MASK);

        for (PortalObject po : windows) {
            if (po instanceof Window) {
                Window window = (Window) po;

                if (window.getContent() != null) {

                    if ( "toutatice-portail-cms-nuxeo-fileBrowserPortletInstance".equals(window.getContent().getURI())) {
                        if (window.getDeclaredProperty("osivia.cms.displayLiveVersion") != null)
                            window.setDeclaredProperty("osivia.cms.displayLiveVersion", null);
                        if (window.getDeclaredProperty("osivia.cms.changeDisplayMode") != null)
                            window.setDeclaredProperty("osivia.cms.changeDisplayMode", null);
                        if (window.getDeclaredProperty("osivia.cms.forceContextualization") != null)
                            window.setDeclaredProperty("osivia.cms.forceContextualization", null);
                        if (window.getDeclaredProperty("osivia.cms.scope") != null)
                            window.setDeclaredProperty("osivia.cms.scope", null);
 
                       
                    }
                }
            }
        }
        // scan sub pages

        Collection<PortalObject> subPages = portalObject.getChildren(PortalObject.PAGE_MASK);
        for (PortalObject subPage : subPages) {
            updateFileExplorer((Page) subPage);

        }

    }
    

    @Override
    public void execute() throws Exception {

        // Set portal Types

        Collection<PortalObject> portals = getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK);

        
        // Delete obsolete datas associated with  FileBrowser
        for (PortalObject po : portals) {
            updateFileExplorer(po);

        }

    }

}
