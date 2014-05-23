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
package org.osivia.portal.api.contribution;

import org.osivia.portal.api.context.PortalControllerContext;


public interface IContributionService {

    static final String MBEAN_NAME = "osivia:service=ContributionService";

    public static final String PUBLISH = "publish";
    public static final String UNPUBLISH = "unpublish";

    /**
     * Window edition state types enumeration.
     * 
     * @author JS Steux
     */
    public class EditionState {


        public static String CONTRIBUTION_MODE_ONLINE = "o";
        public static String CONTRIBUTION_MODE_EDITION = "e";


        private final String contributionMode;
        private final String docPath;


        public String getContributionMode() {
            return contributionMode;
        }

        public String getDocPath() {
            return docPath;
        }

        public EditionState(String contributionMode, String docPath) {
            this.contributionMode = contributionMode;
            this.docPath = docPath;
        }

        public String getStringValue() {
            return contributionMode + docPath;
        }

        public static EditionState fromString(String s) {
            String contributionMode = s.substring(0, 1);
            String docPath = s.substring(1);
            return new EditionState(contributionMode, docPath);
        }


    }


    /**
     * Change current window state
     * 
     * @param portalControllerContext
     * @param state
     * @return
     */
    String getChangeEditionStateUrl(PortalControllerContext portalControllerContext, EditionState state);

    
    /**
     * Return a publish contribution URL for current document.
     * 
     * @param portalControllerContext portal controller context
     * @param docPath current document path
     * @return publish URL
     */
    String getPublishContributionURL(PortalControllerContext portalControllerContext, String docPath);


    /**
     * Get unpublish contribution URL for current document.
     * @param portalControllerContext portal controller context
     * @param docPath current document path
     * @return unpublish URL
     */
    String getUnpublishContributionURL(PortalControllerContext portalControllerContext, String docPath);


    /**
     * get current window state
     * 
     * @param portalControllerContext
     * @return
     */
    EditionState getEditionState(PortalControllerContext portalControllerContext);

    
    /**
     * remove current window state
     * 
     * @param portalControllerContext
     * @return
     */
    void removeWindowEditionState(PortalControllerContext portalControllerContext);

}
