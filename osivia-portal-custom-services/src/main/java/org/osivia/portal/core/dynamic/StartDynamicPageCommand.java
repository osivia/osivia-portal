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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.TabGroup;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;

/**
 * Start dynamic page command.
 *
 * @see DynamicCommand
 */
public class StartDynamicPageCommand extends DynamicCommand {

    /** Parent identifier. */
    private String parentId;
    /** Template identifier. */
    private String templateId;
    /** Page name. */
    private String pageName;
    /** Display names. */
    private Map<Locale, String> displayNames;
    /** Properties. */
    private Map<String, String> properties;
    /** Parameters. */
    private Map<String, String> parameters;
    /** CMS parameters. */
    private Map<String, String> cmsParameters;


    /** Command info. */
    private final CommandInfo info;

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Dynamic object container. */
    private final IDynamicObjectContainer dynamicContainer;


    /**
     * Constructor.
     */
    public StartDynamicPageCommand() {
        super();
        this.info = new ActionCommandInfo(false);

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        // Dynamic object container
        this.dynamicContainer = Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer");
    }


    /**
     * Constructor.
     *
     * @param parentId parent identifier
     * @param pageName page name
     * @param displayNames page display names
     * @param templateId template identifier
     * @param properties page properties
     * @param parameters page parameters
     */
    public StartDynamicPageCommand(String parentId, String pageName, Map<Locale, String> displayNames, String templateId, Map<String, String> properties,
            Map<String, String> parameters) {
        this();
        this.parentId = parentId;
        this.pageName = pageName;
        this.templateId = templateId;
        this.displayNames = displayNames;
        this.properties = properties;
        this.parameters = parameters;
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
            // Portal controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(this.getControllerContext());

            // CMS service
            ICMSService cmsService = this.cmsServiceLocator.getCMSService();
            // CMS context
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setPortalControllerContext(portalControllerContext);


            // Récupération page
            PortalObjectId poid = PortalObjectId.parse(this.parentId, PortalObjectPath.SAFEST_FORMAT);
            PortalObject parent = this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

            PortalObjectId potemplateid = PortalObjectId.parse(this.templateId, PortalObjectPath.SAFEST_FORMAT);
            String potemplatepath = potemplateid.toString(PortalObjectPath.CANONICAL_FORMAT);

            // Base path
            String basePath = this.properties.get("osivia.cms.basePath");


            String pageName = RestorablePageUtils.createRestorableName(this.getControllerContext(), this.pageName, this.templateId, basePath, this.displayNames,
                    this.properties, this.parameters, this.cmsParameters);
            String pageUniqueName = this.pageName;


            PortalObjectId pageId = new PortalObjectId("",
                    new PortalObjectPath(parent.getId().getPath().toString().concat("/").concat(pageName), PortalObjectPath.CANONICAL_FORMAT));


            PortalObject currentPortal = parent;
            while (!(currentPortal instanceof Portal)) {
                currentPortal = currentPortal.getParent();
            }

            // Templates defined in others portals may be redefined localy
            String templatePortal = potemplateid.getPath().getName(0);
            if (!templatePortal.equals(currentPortal.getName())) {
                // Build local path
                String localPath = "/" + currentPortal.getName() + potemplatepath.substring(templatePortal.length() + 1);
                PortalObjectPath localTemplatePath = new PortalObjectPath(localPath, PortalObjectPath.CANONICAL_FORMAT);

                PortalObjectId polocaltemplateId = new PortalObjectId("", localTemplatePath);

                // If exists in current portal, get it
                if (this.getControllerContext().getController().getPortalObjectContainer().getObject(polocaltemplateId) != null) {
                    potemplateid = polocaltemplateId;
                }
            }

            // Page properties
            Map<String, String> properties = new HashMap<String, String>(this.properties);

            // Tab group
            CMSItem spaceConfig;
            EcmDocument document;
            if (basePath != null) {
                spaceConfig = cmsService.getSpaceConfig(cmsContext, basePath);
                document = (EcmDocument) spaceConfig.getNativeItem();
            } else {
                spaceConfig = null;
                document = null;
            }
            String tabType = properties.get(TabGroup.TYPE_PROPERTY);
            if ((document != null) || (tabType != null)) {
                Map<String, TabGroup> tabGroups = cmsService.getTabGroups(cmsContext);
                for (TabGroup tabGroup : tabGroups.values()) {
                    if (tabGroup.contains(portalControllerContext, document, tabType, properties)) {
                        properties.put(TabGroup.NAME_PROPERTY, tabGroup.getName());
                        if (tabGroup.maintains(portalControllerContext, document, tabType, properties)) {
                            properties.put(TabGroup.MAINTAINS_PROPERTY, String.valueOf(true));
                        }
                        break;
                    }
                }

                if ((spaceConfig != null) && (properties.get(TabGroup.NAME_PROPERTY) == null)) {
                    // Parent CMS item
                    CMSItem parentItem = spaceConfig;

                    while ((parentItem != null) && (parentItem.getType() != null) && !parentItem.getType().isRoot()) {
                        try {
                            // Parent path
                            CMSObjectPath parentObjectPath = CMSObjectPath.parse(parentItem.getPath()).getParent();
                            String parentPath = parentObjectPath.toString();

                            parentItem = cmsService.getSpaceConfig(cmsContext, parentPath);
                        } catch (Exception e) {
                            parentItem = null;
                        }
                    }

                    if (parentItem != null) {
                        properties.put("osivia.cms.root", String.valueOf(parentItem.equals(spaceConfig)));
                        properties.put("osivia.cms.rootPath", parentItem.getPath());
                        properties.put("osivia.cms.rootDisplayName", parentItem.getProperties().get("displayName"));
                        properties.put(TabGroup.NAME_PROPERTY, parentItem.getWebId());
                    }
                }
            }

            // Mémorisation de la page avant l'appel
            PageMarkerInfo lastPageState = PageMarkerUtils.getLastPageState(this.getControllerContext());

            if (lastPageState != null) {
                // Pas de retour si ouverture depuis un popup
                if (lastPageState.getPopupMode() == null) {
                    // Last page identifier
                    String lastPageId;
                    if (lastPageState.getPageId() == null) {
                        lastPageId = null;
                    } else {
                        lastPageId = lastPageState.getPageId().toString();
                    }

                    // Pas de retour si ouverture depuis une modale
                    if (!StringUtils.equals("/osivia-util/modal", lastPageId)) {
                        properties.put("osivia.dynamic.close_page_path", lastPageState.getPageId().toString());
                    }
                }
            }

            DynamicPageBean pageBean = new DynamicPageBean(parent, pageName, pageUniqueName, this.displayNames, potemplateid, properties);

            this.dynamicContainer.addDynamicPage(pageBean);

            PortalObject page = this.getControllerContext().getController().getPortalObjectContainer().getObject(pageId);


            // Maj des paramètres publics de la page
            NavigationalStateContext nsContext = (NavigationalStateContext) this.context.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
            Map<QName, String[]> state = new HashMap<QName, String[]>();
            for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
                state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, entry.getKey()), new String[]{entry.getValue()});
            }
            nsContext.setPageNavigationalState(pageId.toString(), new PageNavigationalState(state));

            // Suppression des anciens renders params
            for (PortalObject po : page.getChildren(PortalObject.WINDOW_MASK)) {
                this.getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, po.getId().toString());

                // Suppression du cache
                this.getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, "cached_markup." + po.getId().toString());

            }

            // Maj du breadcrumb
            this.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb", null);

            // Mise à jour du pagemarker
            PageCustomizerInterceptor.initPageBackInfos(this.getControllerContext());

            // Rafaîchir le bandeau
            this.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavRefresh", "1");

            return new UpdatePageResponse(pageId);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }


    /**
     * Setter for cmsParameters.
     *
     * @param cmsParameters the cmsParameters to set
     */
    public void setCmsParameters(Map<String, String> cmsParameters) {
        this.cmsParameters = cmsParameters;
    }

}
