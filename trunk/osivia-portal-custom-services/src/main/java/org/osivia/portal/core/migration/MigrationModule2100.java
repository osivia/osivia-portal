package org.osivia.portal.core.migration;

import java.util.Collection;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.core.constants.InternalConstants;

public class MigrationModule2100 extends MigrationModule {

    @Override
    public int getModuleId() {
        return 2100;
    }


    private void renameCMSPathToStandardName(PortalObject portalObject) {


        String value = portalObject.getDeclaredProperty("osivia.nuxeoPath");
        if (value != null) {
            portalObject.setDeclaredProperty("osivia.nuxeoPath", null);
            portalObject.setDeclaredProperty("osivia.cms.uri", value);
        }


        // scan children

        Collection<PortalObject> childrens = portalObject.getChildren();
        for (PortalObject po : childrens) {
            renameCMSPathToStandardName(po);
        }

    }
    
    
    private void renameContextualizationToStandardName(PortalObject portalObject) {


        String value = portalObject.getDeclaredProperty("osivia.cms.forceContextualization");
        if (value != null) {
            portalObject.setDeclaredProperty("osivia.cms.forceContextualization", null);
            portalObject.setDeclaredProperty("osivia.cms.contextualization", value);
        }


        // scan children

        Collection<PortalObject> childrens = portalObject.getChildren();
        for (PortalObject po : childrens) {
            renameContextualizationToStandardName(po);
        }

    }
    
    
       
    
    
    private void removeUnusedScope(PortalObject portalObject) {

        // scan windows
        Collection<PortalObject> windows = portalObject.getChildren(PortalObject.WINDOW_MASK);

        for (PortalObject po : windows) {
            if (po instanceof Window) {
                Window window = (Window) po;

                if (window.getContent() != null) {

                    if ( "toutatice-portail-cms-nuxeo-fileBrowserPortletInstance".equals(window.getContent().getURI())) {
                        if (window.getDeclaredProperty("pia.cms.scope") != null)
                            window.setDeclaredProperty("pia.cms.scope", null);
                    }
                }
            }
        }
        // scan sub pages

        Collection<PortalObject> subPages = portalObject.getChildren(PortalObject.PAGE_MASK);
        for (PortalObject subPage : subPages) {
            removeUnusedScope((Page) subPage);

        }

    }
    

    @Override
    public void execute() throws Exception {

        // Set portal Types

        Collection<PortalObject> portals = getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK);

        for (PortalObject po : portals) {

            
            if( po.getDeclaredProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE) == null) {
                
                String portalType = InternalConstants.PORTAL_TYPE_STATIC_PORTAL;
                

                if (po.getDeclaredProperty("osivia.navigation.menuRootPath") != null) {
                    portalType = InternalConstants.PORTAL_TYPE_SPACE;
                    po.setDeclaredProperty("osivia.navigation.menuRootPath", null);
                }


                po.setDeclaredProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE, portalType);
            }

        }

        // Rename path "osivia.nuxeoPath" to "osivia.cms.uri"
        renameCMSPathToStandardName(getPortalObjectContainer().getContext());
 
        
        // Rename path "osivia.cms.forceContextualization" to "osivia.cms.contextualization"
        renameContextualizationToStandardName(getPortalObjectContainer().getContext());
 
        
        // Delete scope associated with  FileBrowser
        for (PortalObject po : portals) {
            removeUnusedScope(po);

        }

    }

}
