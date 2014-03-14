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
package org.osivia.portal.core.contribution;

import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService;
import org.osivia.portal.core.context.ControllerContextAdapter;


public class ContributionService implements IContributionService {

    /** Edition map attribute. */

    public static final String ATTR_ADDITITIONNAL_WINDOW_STATES = "osivia.windowStates";
    public static final String ADD_STATE_EDITION_KEY = "editionMode";

  
    
     
    private static String getStringValue(EditionState editionState)  {
        return editionState.getContributionMode() + editionState.getDocPath();
    }
    
    private static EditionState fromString( String s)  {
        String contributionMode = s.substring(0,1);
        String docPath = s.substring(1);
        return new EditionState(contributionMode, docPath);
    }

    /**
     * remove the current window edition state
     * 
     * @param controllerContext jboss portal context
     * @param windowID window identifier
     */
    public static void initWindowEditionStates(ControllerContext controllerContext, PortalObjectId windowID) {
        ParametersStateString states = getWindowStatesMap(controllerContext, windowID);
        states.clear();
    }

    /**
     * set the current window state
     * 
     * @param controllerContext jboss portal context
     * @param windowID window identifier
     * @param state new state
     */
    
    public static void setWindowEditionState(ControllerContext portalControllerContext, PortalObjectId windowID, EditionState state) {
        ParametersStateString states = getWindowStatesMap(portalControllerContext, windowID);
        states.setValue(ADD_STATE_EDITION_KEY, getStringValue(state));
    }
    
    /**
     * get current window state
     * 
     * @param portalControllerContext
     * @param windowID
     * @return
     */
    
    public static EditionState getWindowEditionState(ControllerContext portalControllerContext, PortalObjectId windowID) {
        ParametersStateString states = getWindowStatesMap(portalControllerContext, windowID);
        String state = states.getValue(ADD_STATE_EDITION_KEY);
        if (state != null)
            return fromString(state);
        return null;
    }


    /**
     * get all windows states
     * 
     * @param controllerContext
     * @param windowID
     * @return
     */
    
    public static ParametersStateString getWindowStatesMap(ControllerContext controllerContext, PortalObjectId windowID) {

        ParametersStateString windowAddStates = null;

        windowAddStates = (ParametersStateString) controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, ContributionService.ATTR_ADDITITIONNAL_WINDOW_STATES
                + windowID.toString(PortalObjectPath.CANONICAL_FORMAT));

        // If null, initialization
        if (windowAddStates == null) {
            windowAddStates = ParametersStateString.create();
            controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE,
                    ContributionService.ATTR_ADDITITIONNAL_WINDOW_STATES + windowID.toString(PortalObjectPath.CANONICAL_FORMAT), windowAddStates);
        }


        return windowAddStates;
    }

    
    
    /* 
     * @see org.osivia.portal.api.contribution.IContributionService#getEditionState(org.osivia.portal.api.context.PortalControllerContext)
     */
    public EditionState getEditionState(PortalControllerContext portalControllerContext) {


        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");
        if (window != null) {

            ParametersStateString states = getWindowStatesMap(ControllerContextAdapter.getControllerContext(portalControllerContext), window.getId());
            String state = states.getValue(ADD_STATE_EDITION_KEY);
            if (state != null)
                return fromString(state);
        }
        return null;
    }


    /* 
     * @see org.osivia.portal.api.contribution.IContributionService#getChangeEditionStateUrl(org.osivia.portal.api.context.PortalControllerContext, org.osivia.portal.api.contribution.IContributionService.EditionState)
     */
    public String getChangeEditionStateUrl(PortalControllerContext portalControllerContext, EditionState state) {

        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");
        if (window != null) {

            ChangeContributionModeCommand chgCmd = new ChangeContributionModeCommand(window.getId().toString(PortalObjectPath.SAFEST_FORMAT), state.getContributionMode(), state.getDocPath());
            URLContext urlContext = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                    .getURLContext();

            String resfreshUrl = ControllerContextAdapter.getControllerContext(portalControllerContext).renderURL(chgCmd, urlContext,
                    URLFormat.newInstance(false, true));

            return resfreshUrl;
        }

        return null;
    }


    /* 
     * @see org.osivia.portal.api.contribution.IContributionService#getPublishContributionUrl(org.osivia.portal.api.context.PortalControllerContext, java.lang.String)
     */
    public String getPublishContributionUrl(PortalControllerContext portalControllerContext, String docPath) {
        
        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");
        
        if (window != null) {

            PublishContributionCommand publishCnd = new PublishContributionCommand(window.getId().toString(PortalObjectPath.SAFEST_FORMAT), docPath, IContributionService.PUBLISH );
            URLContext urlContext = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                    .getURLContext();

            String resfreshUrl = ControllerContextAdapter.getControllerContext(portalControllerContext).renderURL(publishCnd, urlContext,
                    URLFormat.newInstance(false, true));

            return resfreshUrl;
        }

        return null;
    }


    /**
     * remove the current window edition state
     * 
     * @param controllerContext jboss portal context
     * @param windowID window identifier
     */
    public void removeWindowEditionState(PortalControllerContext portalControllerContext) {
        
        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");
        if (window != null) {
           ParametersStateString states = getWindowStatesMap(ControllerContextAdapter.getControllerContext(portalControllerContext), window.getId());
            states.remove(ADD_STATE_EDITION_KEY);
        }

    }

}
