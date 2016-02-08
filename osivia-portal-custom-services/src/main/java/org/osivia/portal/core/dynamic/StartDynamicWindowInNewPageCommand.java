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
package org.osivia.portal.core.dynamic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.theming.TabGroup;

/**
 * Start dynamic window in new dynamic page command.
 *
 * @see DynamicCommand
 */
public class StartDynamicWindowInNewPageCommand extends DynamicCommand {

    /** Parent identifier. */
    private String parentId;
    /** Page name. */
    private String pageName;
    /** Page display name. */
    private String displayName;
    /** Window portlet instance identifier. */
    private String instanceId;
    /** Window properties. */
    private Map<String, String> dynaProps;
    /** Window parameters. */
    private Map<String, String> params;


    /** Command info. */
    private final CommandInfo info;


    /**
     * Constructor.
     */
    public StartDynamicWindowInNewPageCommand() {
        super();
        this.info = new ActionCommandInfo(false);
    }


    /**
     * Constructor.
     *
     * @param parentId parent identifier
     * @param pageName page name
     * @param displayName page display name
     * @param portletInstance window portlet instance identifier
     * @param props window properties
     * @param params window parameters
     */
    public StartDynamicWindowInNewPageCommand(String parentId, String pageName, String displayName, String portletInstance, Map<String, String> props,
            Map<String, String> params) {
        this();
        this.parentId = parentId;
        this.pageName = pageName;
        this.displayName = displayName;
        this.instanceId = portletInstance;
        this.dynaProps = props;
        this.params = params;
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
        try {
            // Generic template name
            String genericTemplateName = System.getProperty("template.generic.name");
            if (genericTemplateName == null) {
                throw new ControllerException("template.generic.name undefined. Cannot instantiate this page");
            }

            // Generic template region name
            String genericTemplateRegion = System.getProperty("template.generic.region");
            if (genericTemplateRegion == null) {
                throw new ControllerException("template.generic.region undefined. Cannot instantiate this page");
            }


            // Page display names
            Map<Locale, String> displayNames = new HashMap<Locale, String>();
            if (this.displayName != null) {
                displayNames.put(Locale.FRENCH, this.displayName);
            }

            // Template identifier
            String templateId = PortalObjectId.parse(genericTemplateName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT);

            // Page properties
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("osivia.genericPage", "1");
            properties.put(TabGroup.TYPE_PROPERTY, this.dynaProps.get(TabGroup.TYPE_PROPERTY));

            // Start dynamic page command
            StartDynamicPageCommand pageCommand = new StartDynamicPageCommand(this.parentId, this.pageName, displayNames, templateId, properties,
                    new HashMap<String, String>());
            UpdatePageResponse response = (UpdatePageResponse) this.context.execute(pageCommand);


            // New page identifier
            PortalObjectId pageId = response.getPageId();

            // Window properties
            Map<String, String> windowProps = new HashMap<String, String>();
            windowProps.putAll(this.dynaProps);
            windowProps.put("osivia.dynamic.disable.close", "1");

            // Start dynamic window command
            StartDynamicWindowCommand windowCommand = new StartDynamicWindowCommand(pageId.toString(PortalObjectPath.SAFEST_FORMAT), genericTemplateRegion,
                    this.instanceId, "virtual", windowProps, this.params, "0", null);

            return this.context.execute(windowCommand);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

}
