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
package org.osivia.portal.core.portalcommands;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.ecm.EcmCommand;
import org.osivia.portal.api.ecm.IEcmCommandervice;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.assistantpage.AddPortletCommand;
import org.osivia.portal.core.assistantpage.CMSDeleteDocumentCommand;
import org.osivia.portal.core.assistantpage.CMSDeleteFragmentCommand;
import org.osivia.portal.core.assistantpage.CMSPublishDocumentCommand;
import org.osivia.portal.core.assistantpage.ChangeCMSEditionModeCommand;
import org.osivia.portal.core.assistantpage.ChangeModeCommand;
import org.osivia.portal.core.assistantpage.ChangePageCMSPropertiesCommand;
import org.osivia.portal.core.assistantpage.ChangePagePropertiesCommand;
import org.osivia.portal.core.assistantpage.ChangeWindowSettingsCommand;
import org.osivia.portal.core.assistantpage.CreatePageCommand;
import org.osivia.portal.core.assistantpage.DeletePageCommand;
import org.osivia.portal.core.assistantpage.DeleteWindowCommand;
import org.osivia.portal.core.assistantpage.MakeDefaultPageCommand;
import org.osivia.portal.core.assistantpage.MovePageCommand;
import org.osivia.portal.core.assistantpage.MoveWindowCommand;
import org.osivia.portal.core.assistantpage.SaveInheritanceConfigurationCommand;
import org.osivia.portal.core.assistantpage.SaveRegionLayoutCommand;
import org.osivia.portal.core.assistantpage.SecurePageCommand;
import org.osivia.portal.core.assistantpage.ToggleAdvancedCMSToolsCommand;
import org.osivia.portal.core.cms.CMSPutDocumentInTrashCommand;
import org.osivia.portal.core.contribution.ChangeContributionModeCommand;
import org.osivia.portal.core.contribution.PublishContributionCommand;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowInNewPageCommand;
import org.osivia.portal.core.dynamic.StopDynamicPageCommand;
import org.osivia.portal.core.dynamic.StopDynamicWindowCommand;
import org.osivia.portal.core.ecm.EcmCommandDelegate;
import org.osivia.portal.core.page.ParameterizedCommand;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.page.UserWorkspaceCommand;
import org.osivia.portal.core.search.AdvancedSearchCommand;
import org.osivia.portal.core.tasks.UpdateTaskCommand;
import org.osivia.portal.core.ui.SaveResizableWidthCommand;
import org.osivia.portal.core.urls.BackCommand;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;

/**
 * Default command factory service.
 *
 * @see AbstractCommandFactory
 */
public class DefaultCommandFactoryService extends AbstractCommandFactory {

    /**
     * Default constructor.
     */
    public DefaultCommandFactoryService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {
        try {
            ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();

            if (parameterMap != null) {
                // Action name
                String action = parameterMap.getValue(DefaultURLFactory.COMMAND_ACTION_PARAMETER_NAME);

                // Window identifier
                String windowId;
                String[] windowIdParameters = parameterMap.get("windowId");
                if (windowIdParameters == null) {
                    windowId = null;
                } else {
                    windowId = URLDecoder.decode(windowIdParameters[0], CharEncoding.UTF_8);
                }

                if ("deleteWindow".equals(action)) {
                    return new DeleteWindowCommand(windowId);
                }

                if ("changeWindowSettings".equals(action)) {
                    List<String> style = null;
                    String mobileCollapse = null;
                    String displayTitle = null;
                    String title = null;
                    String displayDecorators = null;
                    String bootstrapPanelStyle = null;
                    String idPerso = null;
                    String ajaxLink = null;
                    String hideEmptyPortlet = null;
                    String printPortlet = null;
                    String conditionalScope = null;
                    String bshActivation;
                    String bshScript;
                    String cacheID;
                    String selectionDep;
                    String priority;


                    if (windowIdParameters != null) {
                        windowId = URLDecoder.decode(windowIdParameters[0], CharEncoding.UTF_8);

                        style = new ArrayList<String>();
                        if (parameterMap.getValues("style") != null) {
                            style.addAll(Arrays.asList(parameterMap.getValues("style")));
                        }

                        if (parameterMap.get("mobileCollapse") != null) {
                            mobileCollapse = URLDecoder.decode(parameterMap.get("mobileCollapse")[0], CharEncoding.UTF_8);
                        }

                        if (parameterMap.get("displayTitle") != null) {
                            displayTitle = URLDecoder.decode(parameterMap.get("displayTitle")[0], CharEncoding.UTF_8);
                        } else {
                            displayTitle = "0";
                        }

                        if (parameterMap.get("title") != null) {
                            title = URLDecoder.decode(parameterMap.get("title")[0], CharEncoding.UTF_8);
                        } else {
                            title = "";
                        }

                        if (parameterMap.get("displayDecorators") != null) {
                            displayDecorators = URLDecoder.decode(parameterMap.get("displayDecorators")[0], CharEncoding.UTF_8);
                        } else {
                            displayDecorators = "0";
                        }

                        // Maximized to CMS indicator
                        boolean maximizedToCms = BooleanUtils.toBoolean(parameterMap.getValue("maximizedToCms"));

                        if (parameterMap.get("bootstrapPanelStyle") != null) {
                            bootstrapPanelStyle = URLDecoder.decode(parameterMap.get("bootstrapPanelStyle")[0], CharEncoding.UTF_8);
                        }

                        if (parameterMap.get("idPerso") != null) {
                            idPerso = URLDecoder.decode(parameterMap.get("idPerso")[0], CharEncoding.UTF_8);
                        } else {
                            idPerso = "";
                        }

                        if (parameterMap.get("ajaxLink") != null) {
                            ajaxLink = URLDecoder.decode(parameterMap.get("ajaxLink")[0], CharEncoding.UTF_8);
                        } else {
                            ajaxLink = "";
                        }

                        if (parameterMap.get("hideEmptyPortlet") != null) {
                            hideEmptyPortlet = URLDecoder.decode(parameterMap.get("hideEmptyPortlet")[0], CharEncoding.UTF_8);
                        } else {
                            hideEmptyPortlet = "";
                        }

                        if (parameterMap.get("printPortlet") != null) {
                            printPortlet = URLDecoder.decode(parameterMap.get("printPortlet")[0], CharEncoding.UTF_8);
                        } else {
                            printPortlet = "";
                        }

                        // v1.0.25 : affichage conditionnel portlet
                        if (parameterMap.get("conditionalScope") != null) {
                            conditionalScope = URLDecoder.decode(parameterMap.get("conditionalScope")[0], CharEncoding.UTF_8);
                        }

                        // Linked taskbar item identifier
                        String taskbarItemId = parameterMap.getValue("taskbarItemId");

                        // Linked layout item identifier
                        String layoutItemId = parameterMap.getValue("layoutItemId");

                        if (parameterMap.get("bshActivation") != null) {
                            bshActivation = URLDecoder.decode(parameterMap.get("bshActivation")[0], CharEncoding.UTF_8);
                        } else {
                            bshActivation = "0";
                        }

                        if (parameterMap.get("bshScript") != null) {
                            bshScript = URLDecoder.decode(parameterMap.get("bshScript")[0], CharEncoding.UTF_8);
                        } else {
                            bshScript = "";
                        }

                        if (parameterMap.get("cacheID") != null) {
                            cacheID = URLDecoder.decode(parameterMap.get("cacheID")[0], CharEncoding.UTF_8);
                        } else {
                            cacheID = "";
                        }

                        if (parameterMap.get("selectionDep") != null) {
                            selectionDep = URLDecoder.decode(parameterMap.get("selectionDep")[0], "UTF-8");
                        } else {
                            selectionDep = "";
                        }


                        if (parameterMap.get("priority") != null) {
                            priority = URLDecoder.decode(parameterMap.get("priority")[0], CharEncoding.UTF_8);
                        } else {
                            priority = "";
                        }

                        // Selected satellite
                        String satellite = parameterMap.getValue("satellite");

                        // Change window settings command
                        ChangeWindowSettingsCommand command = new ChangeWindowSettingsCommand(windowId, style, mobileCollapse, displayTitle, title,
                                displayDecorators, maximizedToCms, bootstrapPanelStyle, idPerso, ajaxLink, hideEmptyPortlet, printPortlet, conditionalScope,
                                bshActivation, bshScript, cacheID, selectionDep, priority);
                        command.setTaskbarItemId(taskbarItemId);
                        command.setLayoutItemId(layoutItemId);
                        command.setSatellite(satellite);
                        return command;
                    }
                }

                if ("deletePage".equals(action)) {
                    String pageId;

                    if (parameterMap.get("pageId") != null) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        return new DeletePageCommand(pageId);
                    }
                }

                if ("makeDefaultPage".equals(action)) {
                    String pageId = null;

                    if (parameterMap.get("jstreeHomeSelect") != null) {
                        pageId = URLDecoder.decode(parameterMap.get("jstreeHomeSelect")[0], CharEncoding.UTF_8);
                        return new MakeDefaultPageCommand(pageId);
                    }
                }


                if ("securePage".equals(action)) {
                    String pageId = null;

                    List<String> viewAction = new ArrayList<String>();
                    if (parameterMap.getValues("view") != null) {
                        viewAction.addAll(Arrays.asList(parameterMap.getValues("view")));
                    }

                    if (parameterMap.get("pageId") != null) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        return new SecurePageCommand(pageId, viewAction);
                    }
                }

                if ("changePageOrder".equals(action)) {
                    String[] pageIdParameter = parameterMap.get("pageId");
                    String[] destinationIdParameter = parameterMap.get("destination");
                    if ((pageIdParameter != null) && (destinationIdParameter != null)) {
                        String pageId = URLDecoder.decode(pageIdParameter[0], CharEncoding.UTF_8);
                        String destinationId = URLDecoder.decode(destinationIdParameter[0], CharEncoding.UTF_8);
                        return new MovePageCommand(pageId, destinationId);
                    }
                }

                if ("changeMode".equals(action)) {
                    String pageId = null;
                    String mode = null;

                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("mode") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        mode = URLDecoder.decode(parameterMap.get("mode")[0], CharEncoding.UTF_8);
                        return new ChangeModeCommand(pageId, mode);
                    }
                }

                if ("changeCMSEditionMode".equals(action)) {
                    String pageId = null;
                    String pagePath = null;
                    String version = null;
                    String editionMode = null;
                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("version") != null) && (parameterMap.get("editionMode") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        pagePath = URLDecoder.decode(parameterMap.get("pagePath")[0], CharEncoding.UTF_8);
                        version = URLDecoder.decode(parameterMap.get("version")[0], CharEncoding.UTF_8);
                        editionMode = URLDecoder.decode(parameterMap.get("editionMode")[0], CharEncoding.UTF_8);
                        return new ChangeCMSEditionModeCommand(pageId, pagePath, version, editionMode);
                    }
                }

                if ("refreshPage".equals(action)) {
                    String pageId = null;

                    if (parameterMap.get("pageId") != null) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], "UTF-8");

                        RefreshPageCommand refreshPageCommand = new RefreshPageCommand(pageId);

                        if (parameterMap.get("ecmActionReturn") != null) {
                            String ecmActionReturn = URLDecoder.decode(parameterMap.get("ecmActionReturn")[0], "UTF-8");
                            refreshPageCommand.setEcmActionReturn(ecmActionReturn);
                        }
                        if (parameterMap.get("newDocId") != null) {
                            String newDocId = URLDecoder.decode(parameterMap.get("newDocId")[0], "UTF-8");
                            refreshPageCommand.setNewDocId(newDocId);
                        }

                        return refreshPageCommand;
                    }
                }

                // Change page properties command
                if ("changePageProperties".equals(action)) {
                    String pageId = null;

                    if (parameterMap.get("pageId") != null) {
                        // Page ID
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);

                        // Display name
                        String displayName = null;
                        if (parameterMap.get("displayName") != null) {
                            displayName = URLDecoder.decode(parameterMap.get("displayName")[0], CharEncoding.UTF_8);
                        }

                        // Draft mode
                        String draftPage = null;
                        if (parameterMap.get("draftPage") != null) {
                            draftPage = URLDecoder.decode(parameterMap.get("draftPage")[0], CharEncoding.UTF_8);
                        }

                        // Layout
                        String layout = null;
                        if (parameterMap.get("newLayout") != null) {
                            layout = URLDecoder.decode(parameterMap.get("newLayout")[0], CharEncoding.UTF_8);
                        }

                        // Theme
                        String theme = null;
                        if (parameterMap.get("newTheme") != null) {
                            theme = URLDecoder.decode(parameterMap.get("newTheme")[0], CharEncoding.UTF_8);
                        }

                        String category = null;
                        if (parameterMap.get("pageCategory") != null) {
                            category = URLDecoder.decode(parameterMap.get("pageCategory")[0], "UTF-8");
                        }

                        // Selectors
                        String selectorsPropagation = null;
                        if (parameterMap.get("selectorsPropagation") != null) {
                            selectorsPropagation = URLDecoder.decode(parameterMap.get("selectorsPropagation")[0], CharEncoding.UTF_8);
                        }

                        return new ChangePagePropertiesCommand(pageId, displayName, draftPage, layout, theme, category, selectorsPropagation);
                    }
                }

                if ("changeCMSProperties".equals(action)) {
                    String pageId = null;
                    String cmsBasePath = null;
                    String scope = null;
                    String pageContextualizationSupport = null;
                    String outgoingRecontextualizationSupport = null;
                    // String incomingContextualizationSupport = null;
                    String navigationScope = null;
                    String displayLiveVersion = null;

                    if (parameterMap.get("pageId") != null) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        if (parameterMap.get("cmsBasePath") != null) {
                            cmsBasePath = URLDecoder.decode(parameterMap.get("cmsBasePath")[0], CharEncoding.UTF_8);
                        }
                        if (parameterMap.get("scope") != null) {
                            scope = URLDecoder.decode(parameterMap.get("scope")[0], CharEncoding.UTF_8);
                        }
                        if (parameterMap.get("navigationScope") != null) {
                            navigationScope = URLDecoder.decode(parameterMap.get("navigationScope")[0], CharEncoding.UTF_8);
                        }
                        if (parameterMap.get("pageContextualizationSupport") != null) {
                            pageContextualizationSupport = URLDecoder.decode(parameterMap.get("pageContextualizationSupport")[0], CharEncoding.UTF_8);
                        }
                        if (parameterMap.get("outgoingRecontextualizationSupport") != null) {
                            outgoingRecontextualizationSupport = URLDecoder.decode(parameterMap.get("outgoingRecontextualizationSupport")[0],
                                    CharEncoding.UTF_8);
                        }
                        if (parameterMap.get("displayLiveVersion") != null) {
                            displayLiveVersion = URLDecoder.decode(parameterMap.get("displayLiveVersion")[0], CharEncoding.UTF_8);
                        }

                        return new ChangePageCMSPropertiesCommand(pageId, cmsBasePath, scope, pageContextualizationSupport, outgoingRecontextualizationSupport,
                                navigationScope, displayLiveVersion);
                    }
                }

                if ("createPage".equals(action)) {
                    String[] nameParameter = parameterMap.get("name");
                    String[] modelIdParameter = parameterMap.get("model");
                    String[] parentIdParameter = parameterMap.get("parent");

                    if ((nameParameter != null) && (parentIdParameter != null)) {
                        String name = URLDecoder.decode(nameParameter[0], CharEncoding.UTF_8);
                        String modelId;
                        if (modelIdParameter != null) {
                            modelId = URLDecoder.decode(modelIdParameter[0], CharEncoding.UTF_8);
                        } else {
                            modelId = null;
                        }
                        String parentId = URLDecoder.decode(parentIdParameter[0], CharEncoding.UTF_8);
                        return new CreatePageCommand(name, parentId, modelId);
                    }
                }

                if ("addPortlet".equals(action)) {
                    String pageId = null;
                    String instanceId = null;
                    String regionId = null;

                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("regionId") != null) && (parameterMap.get("instanceId") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        regionId = URLDecoder.decode(parameterMap.get("regionId")[0], CharEncoding.UTF_8);
                        instanceId = URLDecoder.decode(parameterMap.get("instanceId")[0], CharEncoding.UTF_8);

                        return new AddPortletCommand(pageId, regionId, instanceId);
                    }
                }

                if ("startDynamicWindow".equals(action)) {
                    String pageId = null;
                    String instanceId = null;
                    String regionId = null;
                    String windowName = null;
                    String windowProps = null;
                    String params = null;
                    String addToBreadcrumb = null;


                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("regionId") != null) && (parameterMap.get("instanceId") != null)
                            && (parameterMap.get("windowName") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        regionId = URLDecoder.decode(parameterMap.get("regionId")[0], CharEncoding.UTF_8);
                        instanceId = URLDecoder.decode(parameterMap.get("instanceId")[0], CharEncoding.UTF_8);
                        windowName = URLDecoder.decode(parameterMap.get("windowName")[0], CharEncoding.UTF_8);
                        windowProps = URLDecoder.decode(parameterMap.get("props")[0], CharEncoding.UTF_8);
                        params = URLDecoder.decode(parameterMap.get("params")[0], CharEncoding.UTF_8);
                        addToBreadcrumb = URLDecoder.decode(parameterMap.get("addToBreadcrumb")[0], CharEncoding.UTF_8);

                        return new StartDynamicWindowCommand(pageId, regionId, instanceId, windowName, WindowPropertiesEncoder.decodeProperties(windowProps),
                                WindowPropertiesEncoder.decodeProperties(params), addToBreadcrumb, null);
                    }
                }

                if ("startDynamicWindowInNewPage".equals(action)) {

                    String parentId = null;
                    String pageName = null;
                    String pageDisplayName = null;
                    String instanceId = null;
                    String windowProps = null;
                    String params = null;


                    if ((parameterMap.get("parentId") != null) && (parameterMap.get("instanceId") != null)) {

                        parentId = URLDecoder.decode(parameterMap.get("parentId")[0], CharEncoding.UTF_8);
                        pageName = URLDecoder.decode(parameterMap.get("pageName")[0], CharEncoding.UTF_8);
                        pageDisplayName = URLDecoder.decode(parameterMap.get("pageDisplayName")[0], CharEncoding.UTF_8);
                        instanceId = URLDecoder.decode(parameterMap.get("instanceId")[0], CharEncoding.UTF_8);
                        windowProps = URLDecoder.decode(parameterMap.get("props")[0], CharEncoding.UTF_8);
                        params = URLDecoder.decode(parameterMap.get("params")[0], CharEncoding.UTF_8);


                        return new StartDynamicWindowInNewPageCommand(parentId, pageName, pageDisplayName, instanceId,
                                WindowPropertiesEncoder.decodeProperties(windowProps), WindowPropertiesEncoder.decodeProperties(params));
                    }
                }


                if ("destroyDynamicWindow".equals(action)) {
                    String pageId = null;

                    if (parameterMap.get("pageId") != null) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        return new StopDynamicWindowCommand(pageId, windowId);
                    }
                }

                if ("startDynamicPage".equals(action)) {
                    String parentId = null;
                    String pageName = null;
                    String templateId = null;
                    String pageProps = null;
                    String pageParams = null;

                    if ((parameterMap.get("parentId") != null) && (parameterMap.get("pageName") != null) && (parameterMap.get("templateId") != null)) {
                        parentId = URLDecoder.decode(parameterMap.get("parentId")[0], CharEncoding.UTF_8);
                        pageName = URLDecoder.decode(parameterMap.get("pageName")[0], CharEncoding.UTF_8);
                        templateId = URLDecoder.decode(parameterMap.get("templateId")[0], CharEncoding.UTF_8);
                        pageProps = URLDecoder.decode(parameterMap.get("props")[0], CharEncoding.UTF_8);
                        pageParams = URLDecoder.decode(parameterMap.get("params")[0], CharEncoding.UTF_8);

                        return new StartDynamicPageCommand(parentId, pageName, null, templateId, WindowPropertiesEncoder.decodeProperties(pageProps),
                                WindowPropertiesEncoder.decodeProperties(pageParams));
                    }
                }

                if ("destroyDynamicPage".equals(action)) {
                    // Page identifier
                    String[] pageIdParameters = parameterMap.get("pageId");
                    String pageId;
                    if (ArrayUtils.isEmpty(pageIdParameters)) {
                        pageId = null;
                    } else {
                        pageId = URLDecoder.decode(pageIdParameters[0], CharEncoding.UTF_8);
                    }

                    if (pageId != null) {
                        // Command
                        StopDynamicPageCommand command = new StopDynamicPageCommand(pageId);

                        // Location
                        String[] locationParameters = parameterMap.get("location");
                        if (ArrayUtils.isNotEmpty(locationParameters)) {
                            String location = URLDecoder.decode(locationParameters[0], CharEncoding.UTF_8);
                            command.setLocation(location);
                        }
                        
                        // Close children indicator
                        String[] closeChildrenParameters = parameterMap.get("closeChildren");
                        if (ArrayUtils.isNotEmpty(closeChildrenParameters)) {
                            boolean closeChildren = BooleanUtils.toBoolean(URLDecoder.decode(closeChildrenParameters[0], CharEncoding.UTF_8));
                            command.setCloseChildren(closeChildren);
                        }

                        return command;
                    }
                }

                if ("moveWindow".equals(action)) {
                    String moveAction = null;

                    if (parameterMap.get("moveAction") != null) {
                        moveAction = URLDecoder.decode(parameterMap.get("moveAction")[0], CharEncoding.UTF_8);

                        return new MoveWindowCommand(windowId, moveAction);
                    }
                }

                if ("permLink".equals(action)) {
                    String permMlinkRef = null;
                    String templateInstanciationParentId = null;
                    String cmsPath = null;
                    String permLinkType = null;
                    String portalPersistentName = null;
                    Map<String, String> params = new HashMap<String, String>();

                    if (parameterMap.get("templateInstanciationParentId") != null) {
                        templateInstanciationParentId = URLDecoder.decode(parameterMap.get("templateInstanciationParentId")[0], CharEncoding.UTF_8);
                    }

                    if (parameterMap.get("permLinkRef") != null) {
                        permMlinkRef = URLDecoder.decode(parameterMap.get("permLinkRef")[0], CharEncoding.UTF_8);
                        for (String name : parameterMap.keySet()) {
                            if (!"action".equals(name) && !"permLinkRef".equals(name)) {
                                params.put(name, URLDecoder.decode(parameterMap.get(name)[0], CharEncoding.UTF_8));
                            }
                        }
                    }

                    if (parameterMap.get("cmsPath") != null) {
                        cmsPath = URLDecoder.decode(parameterMap.get("cmsPath")[0], CharEncoding.UTF_8);
                    }

                    if (parameterMap.get("permLinkType") != null) {
                        permLinkType = URLDecoder.decode(parameterMap.get("permLinkType")[0], CharEncoding.UTF_8);
                    }

                    if (parameterMap.get("portalPersistentName") != null) {
                        portalPersistentName = URLDecoder.decode(parameterMap.get("portalPersistentName")[0], CharEncoding.UTF_8);
                    }

                    return new PermLinkCommand(permMlinkRef, params, templateInstanciationParentId, cmsPath, permLinkType, portalPersistentName);
                }


                // Parameterized command
                if (ParameterizedCommand.ACTION.equals(action)) {
                    // CMS path
                    String cmsPath = null;
                    String[] cmsPathParameterMap = parameterMap.get(ParameterizedCommand.CMS_PATH_PARAMETER);
                    if (cmsPathParameterMap != null) {
                        cmsPath = URLDecoder.decode(cmsPathParameterMap[0], CharEncoding.UTF_8);
                    }

                    // Template
                    String template = null;
                    String[] templateParameterMap = parameterMap.get(ParameterizedCommand.TEMPLATE_PARAMETER);
                    if (templateParameterMap != null) {
                        template = URLDecoder.decode(templateParameterMap[0], CharEncoding.UTF_8);
                    }

                    // Renderset
                    String renderset = null;
                    String[] rendersetParameterMap = parameterMap.get(ParameterizedCommand.RENDERSET_PARAMETER);
                    if (rendersetParameterMap != null) {
                        renderset = URLDecoder.decode(rendersetParameterMap[0], CharEncoding.UTF_8);
                    }

                    // Layout state
                    String layoutState = null;
                    String[] layoutStateParameterMap = parameterMap.get(ParameterizedCommand.LAYOUT_STATE_PARAMETER);
                    if (layoutStateParameterMap != null) {
                        layoutState = URLDecoder.decode(layoutStateParameterMap[0], CharEncoding.UTF_8);
                    }

                    // Permalinks indicator
                    Boolean permalinks = null;
                    String[] permalinksParameterMap = parameterMap.get(ParameterizedCommand.PERMALINKS_PARAMETER);
                    if (permalinksParameterMap != null) {
                        permalinks = BooleanUtils.toBooleanObject(URLDecoder.decode(permalinksParameterMap[0], CharEncoding.UTF_8));
                    }

                    return new ParameterizedCommand(cmsPath, template, renderset, layoutState, permalinks);
                }


                // Advanced search command
                if (AdvancedSearchCommand.COMMAND_ACTION_VALUE.equals(action)) {
                    String[] searchParameterMap = parameterMap.get(AdvancedSearchCommand.SEARCH_PARAMETER_NAME);
                    String[] advancedSearchParameterMap = parameterMap.get(AdvancedSearchCommand.ADVANCED_SEARCH_PARAMETER_NAME);
                    String[] selectorsParameterMap = parameterMap.get(AdvancedSearchCommand.SELECTORS_PARAMETER_NAME);

                    // Search value
                    String search;
                    if (ArrayUtils.isEmpty(searchParameterMap)) {
                        search = null;
                    } else {
                        search = URLDecoder.decode(searchParameterMap[0], CharEncoding.UTF_8);
                    }

                    // Advanced search indicator
                    boolean advancedSearch;
                    if (ArrayUtils.isEmpty(advancedSearchParameterMap)) {
                        advancedSearch = false;
                    } else {
                        advancedSearch = BooleanUtils.toBoolean(URLDecoder.decode(advancedSearchParameterMap[0], CharEncoding.UTF_8));
                    }
                    
                    AdvancedSearchCommand advancedSearchCommand = new AdvancedSearchCommand(search, advancedSearch);
                    
                    // Selectors
                    Map<String, List<String>> selectors;
                    if (ArrayUtils.isEmpty(selectorsParameterMap)) {
                    	selectors = null;
                    } else {
                    	selectors = new HashMap<>();
                    	
                    	String selectorsMapStr = URLDecoder.decode(selectorsParameterMap[0], CharEncoding.UTF_8);
                    	String[] split = StringUtils.split(selectorsMapStr, "&");
                    	for(String selectorStr : split) {
                    		String[] selector = StringUtils.split(selectorStr, "=");
                    		String param = selector[0];
                    		String valuesStr = selector[1];
                    		String[] values = StringUtils.split(valuesStr, ",");
                    		
                    		List<String> valuesArray = new ArrayList<>();
                    		for(String value : values) {
                    			valuesArray.add(value);
                    		}
                    		selectors.put(param, valuesArray);
                    	}
                    	advancedSearchCommand.setSelectors(selectors);
                    }

                    return advancedSearchCommand;
                }

                /* CMS commands */

                if ("CMSDeleteFragment".equals(action)) {

                    String pageId = null;
                    String pagePath = null;
                    String refURI = null;

                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("pagePath") != null) && (parameterMap.get("refURI") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], "UTF-8");
                        pagePath = URLDecoder.decode(parameterMap.get("pagePath")[0], "UTF-8");
                        refURI = URLDecoder.decode(parameterMap.get("refURI")[0], "UTF-8");
                        return new CMSDeleteFragmentCommand(pageId, pagePath, refURI);
                    }
                }


                if ("CMSPublishDocument".equals(action)) {

                    String pageId = null;
                    String pagePath = null;
                    String actionCms = null;

                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("pagePath") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], "UTF-8");
                        pagePath = URLDecoder.decode(parameterMap.get("pagePath")[0], "UTF-8");
                        actionCms = URLDecoder.decode(parameterMap.get("actionCms")[0], "UTF-8");

                        return new CMSPublishDocumentCommand(pageId, pagePath, actionCms);
                    }
                }

                if ("CMSDeleteDocumentCommand".equals(action)) {

                    String pageId = null;
                    String pagePath = null;

                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("pagePath") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], "UTF-8");
                        pagePath = URLDecoder.decode(parameterMap.get("pagePath")[0], "UTF-8");

                        return new CMSDeleteDocumentCommand(pageId, pagePath);
                    }
                }

                if ("CMSPutDocumentInTrashCommand".equals(action)) {

                    String docId = null;
                    String docPath = null;
                    String backCMSPageMarker = null;

                    if ((parameterMap.get("docId") != null) && (parameterMap.get("docPath") != null)) {
                        docId = URLDecoder.decode(parameterMap.get("docId")[0], "UTF-8");
                        docPath = URLDecoder.decode(parameterMap.get("docPath")[0], "UTF-8");
                        if (parameterMap.get("backCMSPageMarker") != null) {
                            backCMSPageMarker = URLDecoder.decode(parameterMap.get("backCMSPageMarker")[0], "UTF-8");
                        }

                        return new CMSPutDocumentInTrashCommand(docId, docPath, backCMSPageMarker);
                    }
                }

                if ("CMSChangeContributionMode".equals(action)) {

                    String windowID = null;
                    String contributionMode = null;
                    String docPath = null;

                    if ((parameterMap.get("windowID") != null) && (parameterMap.get("contributionMode") != null) && (parameterMap.get("docPath") != null)) {
                        windowID = URLDecoder.decode(parameterMap.get("windowID")[0], "UTF-8");
                        contributionMode = URLDecoder.decode(parameterMap.get("contributionMode")[0], "UTF-8");
                        docPath = URLDecoder.decode(parameterMap.get("docPath")[0], "UTF-8");
                        return new ChangeContributionModeCommand(windowID, contributionMode, docPath);
                    }
                }

                if ("PublishContribution".equals(action)) {
                    String docPath = null;
                    String actionCms = null;
                    String backCMSPageMarker = null;

                    if (parameterMap.get("docPath") != null) {
                        docPath = URLDecoder.decode(parameterMap.get("docPath")[0], "UTF-8");
                        actionCms = URLDecoder.decode(parameterMap.get("actionCms")[0], "UTF-8");
                        if (parameterMap.get("backCMSPageMarker") != null) {
                            backCMSPageMarker = URLDecoder.decode(parameterMap.get("backCMSPageMarker")[0], "UTF-8");
                        }

                        return new PublishContributionCommand(windowId, docPath, actionCms, backCMSPageMarker);
                    }
                }


                if (EcmCommandDelegate.class.getSimpleName().equals(action)) {

                    String cmsPath = null;
                    String cmsRedirectionPath = null;
                    String command = null;

                    if ((parameterMap.get("cmsPath") != null) && (parameterMap.get("command") != null)) {
                        cmsPath = URLDecoder.decode(parameterMap.get("cmsPath")[0], "UTF-8");
                        command = URLDecoder.decode(parameterMap.get("command")[0], "UTF-8");
                        cmsRedirectionPath = URLDecoder.decode(parameterMap.get("cmsRedirectionPath")[0], "UTF-8");

                        IEcmCommandervice service = Locator.findMBean(IEcmCommandervice.class, IEcmCommandervice.MBEAN_NAME);
                        EcmCommand initialCommand = service.getCommand(command);
                        controllerContext.setAttribute(Scope.SESSION_SCOPE, EcmCommand.REDIRECTION_PATH_ATTRIBUTE, cmsRedirectionPath);

                        return new EcmCommandDelegate(initialCommand, cmsPath);

                    }
                }


                // Toggle advanced CMS tools command
                if (ToggleAdvancedCMSToolsCommand.ACTION.equals(action)) {
                    if (parameterMap.get("pageId") != null) {
                        String pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        return new ToggleAdvancedCMSToolsCommand(pageId);
                    }
                }


                // Save inheritance configuration command
                if (SaveInheritanceConfigurationCommand.ACTION.equals(action)) {
                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("pagePath") != null) && (parameterMap.get("regionName") != null)
                            && (parameterMap.get("inheritance") != null)) {
                        String pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        String pagePath = URLDecoder.decode(parameterMap.get("pagePath")[0], CharEncoding.UTF_8);
                        String regionName = URLDecoder.decode(parameterMap.get("regionName")[0], CharEncoding.UTF_8);
                        String inheritance = URLDecoder.decode(parameterMap.get("inheritance")[0], CharEncoding.UTF_8);
                        return new SaveInheritanceConfigurationCommand(pageId, pagePath, regionName, inheritance);
                    }
                }


                // Save region layout command
                if (SaveRegionLayoutCommand.ACTION.equals(action) && (parameterMap.get("pagePath") != null) && (parameterMap.get("regionName") != null)
                        && (parameterMap.get("regionLayout") != null)) {
                    if (parameterMap.get("pageId") != null) {
                        String pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        String pagePath = URLDecoder.decode(parameterMap.get("pagePath")[0], CharEncoding.UTF_8);
                        String regionName = URLDecoder.decode(parameterMap.get("regionName")[0], CharEncoding.UTF_8);
                        String regionLayoutName = URLDecoder.decode(parameterMap.get("regionLayout")[0], CharEncoding.UTF_8);
                        return new SaveRegionLayoutCommand(pageId, pagePath, regionName, regionLayoutName);
                    }
                }


                // Update task command
                if (UpdateTaskCommand.ACTION.equals(action)) {
                    // Parameters
                    String[] uuidParameter = parameterMap.get(UpdateTaskCommand.UUID_PARAMETER);
                    String[] actionIdParameter = parameterMap.get(UpdateTaskCommand.ACTION_ID_PARAMETER);
                    String[] variablesParameter = parameterMap.get(UpdateTaskCommand.VARIABLES_PARAMETER);
                    String[] redirectionUrlParameter = parameterMap.get(UpdateTaskCommand.REDIRECTION_URL_PARAMETER);

                    if (ArrayUtils.isNotEmpty(uuidParameter) && ArrayUtils.isNotEmpty(actionIdParameter)) {
                        // UUID
                        UUID uuid = UUID.fromString(URLDecoder.decode(uuidParameter[0], CharEncoding.UTF_8));
                        // Action identifier
                        String actionId = URLDecoder.decode(actionIdParameter[0], CharEncoding.UTF_8);
                        // Variables
                        Map<String, String> variables;
                        if (ArrayUtils.isEmpty(variablesParameter)) {
                            variables = null;
                        } else {
                            String[] properties = StringUtils.split(URLDecoder.decode(variablesParameter[0], CharEncoding.UTF_8), "&");
                            variables = new HashMap<String, String>(properties.length);
                            for (String property : properties) {
                                String[] entry = StringUtils.split(property, "=");
                                if (entry.length == 2) {
                                    String key = StringEscapeUtils.unescapeHtml(entry[0]);
                                    String value = StringEscapeUtils.unescapeHtml(entry[1]);
                                    variables.put(key, value);
                                }
                            }
                        }
                        // Redirection URL
                        String redirectionUrl;
                        if (ArrayUtils.isEmpty(redirectionUrlParameter)) {
                            redirectionUrl = null;
                        } else {
                            redirectionUrl = URLDecoder.decode(redirectionUrlParameter[0], CharEncoding.UTF_8);
                        }

                        return new UpdateTaskCommand(uuid, actionId, variables, redirectionUrl);
                    }
                }


                // Save jQuery UI resizable component value
                if (SaveResizableWidthCommand.ACTION.equals(action)) {
                    // Parameters
                    String[] linkedToTasksParameter = parameterMap.get(SaveResizableWidthCommand.LINKED_TO_TASKS_PARAMETER);
                    String[] widthParameter = parameterMap.get(SaveResizableWidthCommand.WIDTH_PARAMETER);

                    // Linked to tasks indicator
                    boolean linkedToTasks;
                    if (ArrayUtils.isEmpty(linkedToTasksParameter)) {
                        linkedToTasks = false;
                    } else {
                        linkedToTasks = BooleanUtils.toBoolean(URLDecoder.decode(linkedToTasksParameter[0], CharEncoding.UTF_8));
                    }
                    
                    // Resizable width
                    Integer width;
                    if (ArrayUtils.isEmpty(widthParameter)) {
                        width = null;
                    } else {
                        width = NumberUtils.toInt(URLDecoder.decode(widthParameter[0], CharEncoding.UTF_8));
                    }

                    return new SaveResizableWidthCommand(linkedToTasks, width);
                }


                // Back
                if (BackCommand.ACTION.equals(action)) {
                    // Parameters
                    String[] pageIdParameter = parameterMap.get(BackCommand.PAGE_ID_PARAMETER);
                    String[] pageMarkerParameter = parameterMap.get(BackCommand.PAGE_MARKER_PARAMETER);
                    String[] refreshParameter = parameterMap.get(BackCommand.REFRESH_PARAMETER);

                    // Page identifier
                    String pageId;
                    if (ArrayUtils.isEmpty(pageIdParameter)) {
                        pageId = null;
                    } else {
                        pageId = URLDecoder.decode(pageIdParameter[0], CharEncoding.UTF_8);
                    }

                    // Page marker
                    String pageMarker;
                    if (ArrayUtils.isEmpty(pageMarkerParameter)) {
                        pageMarker = null;
                    } else {
                        pageMarker = URLDecoder.decode(pageMarkerParameter[0], CharEncoding.UTF_8);
                    }

                    // Refresh indicator
                    boolean refresh;
                    if (ArrayUtils.isEmpty(refreshParameter)) {
                        refresh = false;
                    } else {
                        refresh = BooleanUtils.toBoolean(URLDecoder.decode(refreshParameter[0], CharEncoding.UTF_8));
                    }

                    if (StringUtils.isNotEmpty(pageId) && StringUtils.isNotEmpty(pageMarker)) {
                        // Page portal object identifer
                        PortalObjectId pageObjectId = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);

                        return new BackCommand(pageObjectId, pageMarker, refresh);
                    }
                }


                // User workspace
                if (UserWorkspaceCommand.ACTION.equals(action)) {
                    return new UserWorkspaceCommand();
                }
            }
        } catch (Exception e) {
            // DO NOTHING

        }

        return null;
    }
}
