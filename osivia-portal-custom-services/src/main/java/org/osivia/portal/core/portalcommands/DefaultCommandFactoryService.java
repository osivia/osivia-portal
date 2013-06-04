package org.osivia.portal.core.portalcommands;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.CharEncoding;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.core.assistantpage.AddPortletCommand;
import org.osivia.portal.core.assistantpage.ChangeCMSEditionModeCommand;
import org.osivia.portal.core.assistantpage.ChangeModeCommand;
import org.osivia.portal.core.assistantpage.ChangePageCMSPropertiesCommand;
import org.osivia.portal.core.assistantpage.ChangePageLayoutCommand;
import org.osivia.portal.core.assistantpage.ChangePagePropertiesCommand;
import org.osivia.portal.core.assistantpage.ChangePageThemeCommand;
import org.osivia.portal.core.assistantpage.ChangeWindowSettingsCommand;
import org.osivia.portal.core.assistantpage.CreatePageCommand;
import org.osivia.portal.core.assistantpage.DeletePageCommand;
import org.osivia.portal.core.assistantpage.DeleteWindowCommand;
import org.osivia.portal.core.assistantpage.MakeDefaultPageCommand;
import org.osivia.portal.core.assistantpage.MovePageCommand;
import org.osivia.portal.core.assistantpage.MoveWindowCommand;
import org.osivia.portal.core.assistantpage.RenamePageCommand;
import org.osivia.portal.core.assistantpage.SecurePageCommand;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.dynamic.StopDynamicPageCommand;
import org.osivia.portal.core.dynamic.StopDynamicWindowCommand;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.urls.WindowPropertiesEncoder;


public class DefaultCommandFactoryService extends AbstractCommandFactory {

    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {

        try {

            ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();


            if (parameterMap != null) {
                String action = parameterMap.getValue("action");

                if ("deleteWindow".equals(action)) {
                    String windowId = null;

                    if (parameterMap.get("windowId") != null) {
                        windowId = URLDecoder.decode(parameterMap.get("windowId")[0], CharEncoding.UTF_8);
                        return new DeleteWindowCommand(windowId);
                    }
                }

                if ("changeWindowSettings".equals(action)) {
                    String windowId = null;
                    List<String> style = null;
                    String displayTitle = null;
                    String title = null;
                    String displayDecorators = null;
                    String idPerso = null;
                    String ajaxLink = null;
                    String hideEmptyPortlet = null;
                    String printPortlet = null;
                    String conditionalScope = null;
                    String bshActivation;
                    String bshScript;
                    String cacheID;
                    String selectionDep;


                    if (parameterMap.get("windowId") != null) {

                        windowId = URLDecoder.decode(parameterMap.get("windowId")[0], CharEncoding.UTF_8);


                        style = new ArrayList<String>();
                        if (parameterMap.getValues("style") != null) {
                            style.addAll(Arrays.asList(parameterMap.getValues("style")));
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
                            selectionDep = URLDecoder.decode(((String[]) parameterMap.get("selectionDep"))[0], "UTF-8");
                        } else {
                            selectionDep = "";
                        }


                        return new ChangeWindowSettingsCommand(windowId, style, displayTitle, title, displayDecorators, idPerso, ajaxLink, hideEmptyPortlet,
                                printPortlet, conditionalScope, bshActivation, bshScript, cacheID, selectionDep);
                    }
                }

                if ("deletePage".equals(action)) {
                    String pageId;

                    if (parameterMap.get("pageId") != null) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        return new DeletePageCommand(pageId);
                    }
                }

                if ("renamePage".equals(action)) {
                    String pageId;
                    String displayName;

                    if (((parameterMap.get("pageId") != null) || (parameterMap.get("jstreeRenamePageSelect") != null))
                            && (parameterMap.get("displayName") != null)) {
                        if (parameterMap.get("pageId") == null) {
                            pageId = URLDecoder.decode(parameterMap.get("jstreeRenamePageSelect")[0], CharEncoding.UTF_8);
                        } else {
                            pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        }
                        displayName = URLDecoder.decode(parameterMap.get("displayName")[0], CharEncoding.UTF_8);
                        return new RenamePageCommand(pageId, displayName);
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
                    String pageId;
                    String destinationId;

                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("jstreePageOrder") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        destinationId = URLDecoder.decode(parameterMap.get("jstreePageOrder")[0], CharEncoding.UTF_8);
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
                    String mode = null;

                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("mode") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        mode = URLDecoder.decode(parameterMap.get("mode")[0], CharEncoding.UTF_8);
                        return new ChangeCMSEditionModeCommand(pageId, mode);
                    }
                }


                if ("refreshPage".equals(action)) {
                    String pageId = null;


                    if (parameterMap.get("pageId") != null) {
                        pageId = URLDecoder.decode(((String[]) parameterMap.get("pageId"))[0], "UTF-8");
                        return new RefreshPageCommand(pageId);
                    }
                }

                // Change page layout command
                if ("changeLayout".equals(action)) {

                    String pageId = null;
                    String layout = null;

                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("newLayout") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        layout = URLDecoder.decode(parameterMap.get("newLayout")[0], CharEncoding.UTF_8);

                        return new ChangePageLayoutCommand(pageId, layout);
                    }
                }
                
                // Change page theme command
                if ("changeTheme".equals(action)) {
                    String pageId;
                    String theme;
                    
                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("newTheme") != null)) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        theme = URLDecoder.decode(parameterMap.get("newTheme")[0], CharEncoding.UTF_8);
                        
                        return new ChangePageThemeCommand(pageId, theme);
                    }
                }

                if ("changePageProperties".equals(action)) {

                    String pageId = null;


                    if (parameterMap.get("pageId") != null) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);

                        String draftPage = null;
                        if (parameterMap.get("draftPage") != null) {
                            draftPage = URLDecoder.decode(parameterMap.get("draftPage")[0], CharEncoding.UTF_8);
                        }


                        return new ChangePagePropertiesCommand(pageId, draftPage);
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
                    String cmsNavigationMode = null;
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
                            outgoingRecontextualizationSupport = URLDecoder.decode(parameterMap.get("outgoingRecontextualizationSupport")[0], CharEncoding.UTF_8);
                        }
                        // if( parameterMap.get("incomingContextualizationSupport") != null)
                        // incomingContextualizationSupport = URLDecoder.decode(((String[]) parameterMap.get("incomingContextualizationSupport"))[0], CharEncoding.UTF_8);
                        if (parameterMap.get("cmsNavigationMode") != null) {
                            cmsNavigationMode = URLDecoder.decode(parameterMap.get("cmsNavigationMode")[0], CharEncoding.UTF_8);
                        }
                        if (parameterMap.get("displayLiveVersion") != null) {
                            displayLiveVersion = URLDecoder.decode(parameterMap.get("displayLiveVersion")[0], CharEncoding.UTF_8);
                        }

                        return new ChangePageCMSPropertiesCommand(pageId, cmsBasePath, scope, pageContextualizationSupport, outgoingRecontextualizationSupport,
                                navigationScope, cmsNavigationMode, displayLiveVersion);
                    }
                }

                if ("createPage".equals(action)) {
                    String name;
                    String parentId;
                    String modelId;

                    if ((parameterMap.get("name") != null) && (parameterMap.get("jstreeParentSelect") != null)
                            && (parameterMap.get("jstreeModelSelect") != null)) {
                        name = URLDecoder.decode(parameterMap.get("name")[0], CharEncoding.UTF_8);
                        parentId = URLDecoder.decode(parameterMap.get("jstreeParentSelect")[0], CharEncoding.UTF_8);
                        modelId = URLDecoder.decode(parameterMap.get("jstreeModelSelect")[0], CharEncoding.UTF_8);

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
                                WindowPropertiesEncoder.decodeProperties(params), addToBreadcrumb);
                    }
                }

                if ("destroyDynamicWindow".equals(action)) {

                    String windowId = null;
                    String pageId = null;

                    if ((parameterMap.get("pageId") != null) && (parameterMap.get("windowId") != null)) {
                        windowId = URLDecoder.decode(parameterMap.get("windowId")[0], CharEncoding.UTF_8);
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


                    String pageId = null;

                    if (parameterMap.get("pageId") != null) {
                        pageId = URLDecoder.decode(parameterMap.get("pageId")[0], CharEncoding.UTF_8);
                        return new StopDynamicPageCommand(pageId);
                    }
                }


                if ("moveWindow".equals(action)) {

                    String windowId = null;
                    String moveAction = null;

                    if ((parameterMap.get("windowId") != null) && (parameterMap.get("moveAction") != null)) {
                        windowId = URLDecoder.decode(parameterMap.get("windowId")[0], CharEncoding.UTF_8);
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


            }
        } catch (Exception e) {
            // DO NOTHING

        }

        return null;
    }

}
