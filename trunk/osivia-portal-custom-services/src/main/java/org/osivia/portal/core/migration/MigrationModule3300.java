/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.migration;

import java.util.Collection;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.core.constants.InternalConstants;

public class MigrationModule3300 extends MigrationModule {

    @Override
    public int getModuleId() {
        return 3300;
    }

   
    
    private void removeDisplayVersion(PortalObject portalObject) {

        // scan windows
        Collection<PortalObject> windows = portalObject.getChildren(PortalObject.WINDOW_MASK);

        for (PortalObject po : windows) {
            if (po instanceof Window) {
                Window window = (Window) po;

                if (window.getContent() != null) {

                    if ( 
                            ! "toutatice-portail-cms-nuxeo-viewListPortletInstance".equals(window.getContent().getURI())
                        )
                        {
                        if (window.getDeclaredProperty("osivia.cms.displayLiveVersion") != null)
                            window.setDeclaredProperty("osivia.cms.displayLiveVersion", null);
                      
                    }
                }
            }
        }
        // scan sub pages

        Collection<PortalObject> subPages = portalObject.getChildren(PortalObject.PAGE_MASK);
        for (PortalObject subPage : subPages) {
            removeDisplayVersion((Page) subPage);

        }

    }
    

    @Override
    public void execute() throws Exception {

        // Set portal Types

        Collection<PortalObject> portals = getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK);

        
        // Delete obsolete datas associated with  FileBrowser
        for (PortalObject po : portals) {
            removeDisplayVersion(po);

        }

    }

}
