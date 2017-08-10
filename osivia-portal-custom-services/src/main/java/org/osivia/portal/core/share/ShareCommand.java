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
package org.osivia.portal.core.share;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Portal;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.dynamic.DynamicCommand;
import org.osivia.portal.core.web.IWebIdService;


/**
 * Share command.
 * 
 * @author David Chevrier.
 * @see DynamicCommand
 */
public class ShareCommand extends DynamicCommand {

    /** Parent webId (remote proxy case). */
    private String parentWebId;
    /** Parameters. */
    private Map<String, String> parameters;

    /** displayContext */
    private String displayContext;


    /** WebId. */
    private final String webId;

    /** Command info. */
    private final CommandInfo info;


    /**
     * Constructor.
     * 
     * @param webId webId
     */
    public ShareCommand(String webId) {
        super();
        this.webId = webId;
        this.info = new ActionCommandInfo(false);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return this.info;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        // Controller context
        ControllerContext controllerContext = this.getControllerContext();
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

        // Portal persistent name
        String portalPersistentName = null;

        // Extract current portal
        if (portalControllerContext.getControllerCtx() != null) {
            String portalName = (String) ControllerContextAdapter.getControllerContext(portalControllerContext).getAttribute(Scope.REQUEST_SCOPE,
                    "osivia.currentPortalName");

            Portal defaultPortal = ControllerContextAdapter.getControllerContext(portalControllerContext).getController().getPortalObjectContainer()
                    .getContext().getDefaultPortal();

            if (!defaultPortal.getName().equals(portalName)) {
                if (!StringUtils.equals(portalName, "osivia-util")) {
                    portalPersistentName = portalName;
                }
            }
        }

        // Case of remote proxy: <webid>?l=<parentWebId> -> <webId>_c_<parentWebId>
        StringBuilder webIdPath = new StringBuilder();
        webIdPath.append(IWebIdService.CMS_PATH_PREFIX);
        webIdPath.append(this.webId);
        if (StringUtils.isNotBlank(this.parentWebId)) {
            webIdPath.append(IWebIdService.RPXY_WID_MARKER);
            webIdPath.append(this.parentWebId);
        }

        // Parameters
        Map<String, String> parameters;
        if (MapUtils.isEmpty(this.parameters)) {
            parameters = new HashMap<>(1);
        } else {
            parameters = new HashMap<>(this.parameters);
        }
        if (!parameters.containsKey("selectors")) {
            // Force selectors reset
            Map<String, List<String>> selectors = new HashMap<>(0);
            parameters.put("selectors", PageParametersEncoder.encodeProperties(selectors));
        }


        // CMS command
        CmsCommand cmsCommand = new CmsCommand(null, webIdPath.toString(), parameters, null, displayContext, null, null, null, null, null, portalPersistentName);
        // Remove default initialisation
        cmsCommand.setItemScope(null);
        cmsCommand.setInsertPageMarker(false);

        return this.context.execute(cmsCommand);
    }


    /**
     * Getter for parentWebId.
     * 
     * @return the parentWebId
     */
    public String getParentWebId() {
        return parentWebId;
    }

    /**
     * Setter for parentWebId.
     * 
     * @param parentWebId the parentWebId to set
     */
    public void setParentWebId(String parentWebId) {
        this.parentWebId = parentWebId;
    }

    /**
     * Getter for parameters.
     * 
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Setter for parameters.
     * 
     * @param parameters the parameters to set
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Getter for webId.
     * 
     * @return the webId
     */
    public String getWebId() {
        return webId;
    }


    /**
     * Getter for displayContext.
     * 
     * @return the displayContext
     */
    public String getDisplayContext() {
        return displayContext;
    }


    /**
     * Setter for displayContext.
     * 
     * @param displayContext the displayContext to set
     */
    public void setDisplayContext(String displayContext) {
        this.displayContext = displayContext;
    }

}
