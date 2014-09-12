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
package org.osivia.portal.core.contribution;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.context.ControllerContextAdapter;


public class ContributionService implements IContributionService {

    /** Edition map attribute. */

    public static final String ATTR_ADDITITIONNAL_WINDOW_STATES = "osivia.windowStates";
    public static final String ADD_STATE_EDITION_KEY = "editionMode";
    public static final String STATE_SEPARATOR = ";";
    public static final String STATE_EMPTY = "-";
    
    private static String getStringValue(EditionState editionState) {
        StringBuffer s = new StringBuffer(); 
        
        s.append( editionState.getContributionMode());
        s.append( STATE_SEPARATOR);
        s.append( editionState.getDocPath());
        s.append( STATE_SEPARATOR);       
        s.append(  editionState.getBackPageMarker() != null ? editionState.getBackPageMarker() : STATE_EMPTY);
        s.append( STATE_SEPARATOR);       
        s.append(Boolean.toString(editionState.isHasBeenModified()));
        
        return s.toString();
            
    }

    private static EditionState fromString(String s) {
        String[] tokens = s.split(STATE_SEPARATOR);
        
        String contributionMode = tokens[ 0];
        String docPath = tokens[ 1];
        EditionState editionState =  new EditionState(contributionMode, docPath);
        if( !STATE_EMPTY.equals( tokens[ 2]))
            editionState.setBackPageMarker(tokens[ 2]);
        editionState.setHasBeenModified(Boolean.parseBoolean(tokens[ 3]))    ;   
        return editionState;
        
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

    
    
    public static void updateNavigationalState(ControllerContext portalControllerContext, Map<QName, String[]> newState, EditionState state)   {
        newState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.pageEditionPath"), new String[]{getStringValue(state)});
    }
    
    public static EditionState getNavigationalState(ControllerContext portalControllerContext, PageNavigationalState pns)   {
        
        EditionState editionState = null;
        
        String state[] = pns.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.pageEditionPath"));
        if ((state != null) && (state.length == 1)) {
            editionState = fromString(state[0]);
        }
        return editionState;
    }
    
    
    /**
     * set the current edition state
     * 
     * @param controllerContext jboss portal context
     * @param windowID window identifier
     * @param state new state
     */

    public static void setWindowEditionState(ControllerContext portalControllerContext, PortalObjectId windowID, EditionState state) {


        NavigationalStateContext nsContext = (NavigationalStateContext) portalControllerContext
                .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        PageNavigationalState pageState = nsContext.getPageNavigationalState(windowID.getPath().getParent().toString());


        String sNavPath[] = null;
        if (pageState != null) {
            sNavPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
        }


        if ((sNavPath != null) && (sNavPath.length > 0) && (!windowID.toString().endsWith("CMSPlayerWindow")) && state.getDocPath().equals(sNavPath[0])) {

            /* Page level edition state */

            PageNavigationalState pns = nsContext.getPageNavigationalState(windowID.getPath().getParent().toString());
            Map<QName, String[]> newState = new HashMap<QName, String[]>();
            if (pns != null) {
                Map<QName, String[]> qNameMap = pns.getParameters();
                if ((qNameMap != null) && !qNameMap.isEmpty()) {

                    for (Map.Entry<QName, String[]> entry : qNameMap.entrySet()) {
                        if (!entry.getKey().getLocalPart().equals("osivia.cms.pageEditionPath"))
                            newState.put(entry.getKey(), entry.getValue());
                    }
                }

                updateNavigationalState( portalControllerContext, newState, state);


                nsContext.setPageNavigationalState(windowID.getPath().getParent().toString(), new PageNavigationalState(newState));
            }


        } else {
            // window level edition state
            ParametersStateString states = getWindowStatesMap(portalControllerContext, windowID);
            states.setValue(ADD_STATE_EDITION_KEY, getStringValue(state));
        }
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

        /* If no window state, Get page navigation state */
        
        if(!windowID.toString().endsWith("CMSPlayerWindow")) {

            NavigationalStateContext nsContext = (NavigationalStateContext) portalControllerContext
                    .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
            PageNavigationalState pageState = nsContext.getPageNavigationalState(windowID.getPath().getParent().toString());
    
            
            String sNavPath[] = null;
            if (pageState != null) {
                sNavPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            }
    
    
            if ((sNavPath != null) && (sNavPath.length > 0) ) {

                if (pageState != null) {
                    String cmsEditionValue[] = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.pageEditionPath"));
                    if ((cmsEditionValue != null) && (cmsEditionValue.length == 1)) {
                        EditionState pageEditionState = fromString(cmsEditionValue[0]);
                        return pageEditionState;
                    }
                }
            }
        }


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
     * @see org.osivia.portal.api.contribution.IContributionService#getChangeEditionStateUrl(org.osivia.portal.api.context.PortalControllerContext,
     * org.osivia.portal.api.contribution.IContributionService.EditionState)
     */
    public String getChangeEditionStateUrl(PortalControllerContext portalControllerContext, EditionState state) {

        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");
        if (window != null) {

            ChangeContributionModeCommand chgCmd = new ChangeContributionModeCommand(window.getId().toString(PortalObjectPath.SAFEST_FORMAT),
                    state.getContributionMode(), state.getDocPath());
            URLContext urlContext = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                    .getURLContext();

            String resfreshUrl = ControllerContextAdapter.getControllerContext(portalControllerContext).renderURL(chgCmd, urlContext,
                    URLFormat.newInstance(false, true));

            return resfreshUrl;
        }

        return null;
    }


    /*
     * @see org.osivia.portal.api.contribution.IContributionService#getPublishContributionUrl(org.osivia.portal.api.context.PortalControllerContext,
     * java.lang.String)
     */
    public String getPublishContributionURL(PortalControllerContext portalControllerContext, String docPath) {

        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");

        if (window != null) {

            PublishContributionCommand publishCnd = new PublishContributionCommand(window.getId().toString(PortalObjectPath.SAFEST_FORMAT), docPath,
                    IContributionService.PUBLISH);
            URLContext urlContext = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                    .getURLContext();

            String resfreshUrl = ControllerContextAdapter.getControllerContext(portalControllerContext).renderURL(publishCnd, urlContext,
                    URLFormat.newInstance(false, true));

            return resfreshUrl;
        }

        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getAskPublishContributionURL(PortalControllerContext portalControllerContext, String docPath) {
        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");

        if (window != null) {
            return getRefreshURLByContribution(portalControllerContext, docPath, window, IContributionService.ASK_PUBLISH);
        }

        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCancelPublishingAskContributionURL(PortalControllerContext portalControllerContext, String docPath) {
        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");

        if (window != null) {
            return getRefreshURLByContribution(portalControllerContext, docPath, window, IContributionService.CANCEL_PUBLISH);
        }

        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getValidatePublishContributionURL(PortalControllerContext portalControllerContext, String docPath){
        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");

        if (window != null) {
            return getRefreshURLByContribution(portalControllerContext, docPath, window, IContributionService.VALIDATE_PUBLISHING);
        }

        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getRejectPublishContributionURL(PortalControllerContext portalControllerContext, String docPath){
        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");

        if (window != null) {
            return getRefreshURLByContribution(portalControllerContext, docPath, window, IContributionService.REJECT_PUBLISHING);
        }

        return null;
    }

    /**
     * Get refresh URL for contribution.
     * 
     * @param portalControllerContext
     * @param docPath
     * @param window
     * @return
     */
    protected String getRefreshURLByContribution(PortalControllerContext portalControllerContext, String docPath, Window window, String contribution) {
        PublishContributionCommand publishCnd = new PublishContributionCommand(window.getId().toString(PortalObjectPath.SAFEST_FORMAT), docPath,
                contribution);
        URLContext urlContext = ControllerContextAdapter.getControllerContext(portalControllerContext).getServerInvocation().getServerContext()
                .getURLContext();

        String resfreshUrl = ControllerContextAdapter.getControllerContext(portalControllerContext).renderURL(publishCnd, urlContext,
                URLFormat.newInstance(false, true));

        return resfreshUrl;
    }


    /**
     * {@inheritDoc}
     */
    public String getUnpublishContributionURL(PortalControllerContext portalControllerContext, String docPath) {
        // Current window
        Window window = (Window) portalControllerContext.getRequest().getAttribute("osivia.window");
        if (window != null) {
            // Controller context
            ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
            // URL context
            URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();

            // Window identifier
            String windowId = window.getId().toString(PortalObjectPath.SAFEST_FORMAT);
            // Unpublish command
            PublishContributionCommand command = new PublishContributionCommand(windowId, docPath, IContributionService.UNPUBLISH);

            // URL
            return controllerContext.renderURL(command, urlContext, URLFormat.newInstance(false, true));
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
