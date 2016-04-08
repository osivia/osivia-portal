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
package org.osivia.portal.core.share;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Portal;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.dynamic.DynamicCommand;
import org.osivia.portal.core.web.IWebIdService;


/**
 * @author David Chevrier.
 *
 */
public class ShareCommand extends DynamicCommand {

    private static final CommandInfo info = new ActionCommandInfo(false);

    /** WebId. */
    private String webId;

    /** Prefixed webId. */
    private String prefixedWebId;

    /** Page parameters. */
    private Map<String, String> params;

    /**
     * Default constructor.
     */
    public ShareCommand() {
    }

    /**
     * Constructor
     *
     * @param webId
     */
    public ShareCommand(String webId) {
        this.webId = webId;
        this.prefixedWebId = IWebIdService.CMS_PATH_PREFIX + webId;
    }

    /**
     * Constructor
     *
     * @param webId
     */
    public ShareCommand(String webId, Map<String, String> params) {
        this.webId = webId;
        this.prefixedWebId = IWebIdService.CMS_PATH_PREFIX + webId;
        this.params = params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return info;
    }

    /**
     * @return the webId
     */
    public String getWebId() {
        return webId;
    }

    /**
     * @param webId the webId to set
     */
    public void setWebId(String webId) {
        this.webId = webId;
    }

    /**
     * @return prefixed webId.
     */
    public String getPrefixedWebId(){
        return  this.prefixedWebId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        // Delegation to cmsCommand.

        final ControllerContext controllerContext = this.getControllerContext();
        final PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

        String portalPersistentName = null;

        // Extract current portal
        if (portalControllerContext.getControllerCtx() != null) {
            final String portalName = (String) ControllerContextAdapter.getControllerContext(portalControllerContext).getAttribute(Scope.REQUEST_SCOPE, "osivia.currentPortalName");

            final Portal defaultPortal = ControllerContextAdapter.getControllerContext(portalControllerContext).getController().getPortalObjectContainer().getContext()
                    .getDefaultPortal();

            if (!defaultPortal.getName().equals(portalName)) {
                if (!StringUtils.equals(portalName, "osivia-util")) {
                    portalPersistentName = portalName;
                }
            }
        }

        final CmsCommand cmsCommand = new CmsCommand(null, this.prefixedWebId, params, null, null, null, null, null, null, null, portalPersistentName);
        // Remove default initialisation
        cmsCommand.setItemScope(null);
        cmsCommand.setInsertPageMarker(false);

        return this.context.execute(cmsCommand);
    }


    /**
     * Getter for params.
     *
     * @return the params
     */
    public Map<String, String> getParams() {
        return params;
    }


    /**
     * Setter for params.
     *
     * @param params the params to set
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

}
