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

// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.pagemarker.PortalCommandFactory;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


public class StartDynamicWindowInNewPageCommand extends DynamicCommand {

    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

    @Override
    public CommandInfo getInfo() {
        return info;
    }
    private String parentId ;
    private String pageName;
    private String displayName;
    private String instanceId;
    private Map<String, String> dynaProps;
    private Map<String, String> params;




    public StartDynamicWindowInNewPageCommand() {
    }

    public StartDynamicWindowInNewPageCommand(String parentId,  String pageName, String displayName, String portletInstance,  Map<String, String> props,
            Map<String, String> params) {
        this.parentId = parentId;
        this.pageName = pageName;
        this.displayName = displayName;        
        this.instanceId = portletInstance;
        this.dynaProps = props;
        this.params = params;
    }

    @Override
    public ControllerResponse execute() throws ControllerException {

        try {

            /* Create page */

            Map<String, String> props = new HashMap<String, String>();

            Map displayNames = new HashMap();
            if( displayName != null) {
                displayNames.put(Locale.FRENCH, displayName);
            }
            String genericTemplateName = System.getProperty("template.generic.name");
            if( genericTemplateName == null)
                throw new ControllerException("template.generic.name undefined. Cannot instantiate this page");
            
            String genericTemplateRegion = System.getProperty("template.generic.region");
            if( genericTemplateRegion == null)
                throw new ControllerException("template.generic.region undefined. Cannot instantiate this page");


            StartDynamicPageCommand pageCmd = new StartDynamicPageCommand(parentId, pageName, displayNames, PortalObjectId.parse(
                            genericTemplateName, PortalObjectPath.CANONICAL_FORMAT).toString(
                                    PortalObjectPath.SAFEST_FORMAT), props, new HashMap<String, String>());

            PortalObjectId pageId = ((UpdatePageResponse) this.context.execute(pageCmd)).getPageId();
            
            /* Create window */
            
            Map<String, String> windowProps = new HashMap<String, String>();
            windowProps.putAll(dynaProps);
            windowProps.put("osivia.windowState", "normal");
            windowProps.put("osivia.dynamic.disable.close","1");
            
         
            StartDynamicWindowCommand windowCmd = new StartDynamicWindowCommand(pageId.toString(PortalObjectPath.SAFEST_FORMAT), genericTemplateRegion,
                    instanceId, "virtual", windowProps, params, "0", null);


            return this.context.execute(windowCmd);

            
        
        } catch (Exception e) {
            throw new ControllerException(e);
        }

    }

}
