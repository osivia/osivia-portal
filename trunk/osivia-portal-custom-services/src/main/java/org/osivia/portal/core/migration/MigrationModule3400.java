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
 */
package org.osivia.portal.core.migration;

import java.util.Collection;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.Window;
import org.osivia.portal.core.constants.InternalConstants;

public class MigrationModule3400 extends MigrationModule {

    @Override
    public int getModuleId() {
        return 3400;
    }


    private void updateProperties(PortalObject portalObject) {

        // scan windows
        Collection<PortalObject> windows = portalObject.getChildren(PortalObject.WINDOW_MASK);

        for (PortalObject po : windows) {
            if (po instanceof Window) {
                Window window = (Window) po;

                if (window.getContent() != null) {

                    if ("toutatice-portail-cms-nuxeo-viewListPortletInstance".equals(window.getContent().getURI())) {

                        /* Renommage et Changement en boolean */

                        if (window.getDeclaredProperty("osivia.cms.showSpaceMenuBar") != null) {
                            if ("1".equals(window.getDeclaredProperty("osivia.cms.showSpaceMenuBar")))
                                window.setDeclaredProperty("osivia.showSpaceMenuBar", "on");
                            window.setDeclaredProperty("osivia.cms.showSpaceMenuBar", null);
                        }

                        if (window.getDeclaredProperty("osivia.requestInterpretor") != null) {
                            if ("beanShell".equals(window.getDeclaredProperty("osivia.requestInterpretor")))
                                window.setDeclaredProperty("osivia.beanShell", "on");

                            window.setDeclaredProperty("osivia.requestInterpretor", null);
                        }
                    }
                }
            }
        }
        // scan sub pages

        Collection<PortalObject> subPages = portalObject.getChildren(PortalObject.PAGE_MASK);
        for (PortalObject subPage : subPages) {
            updateProperties((Page) subPage);

        }

    }


    @Override
    public void execute() throws Exception {

        // Set portal Types

        Collection<PortalObject> portals = getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK);


        // Delete obsolete datas associated with FileBrowser
        for (PortalObject po : portals) {
            updateProperties(po);

        }

    }

}
