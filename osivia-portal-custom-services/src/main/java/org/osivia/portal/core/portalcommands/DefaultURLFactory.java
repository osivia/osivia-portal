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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.api.ecm.EcmCommand;
import org.osivia.portal.core.assistantpage.CMSDeleteDocumentCommand;
import org.osivia.portal.core.assistantpage.CMSDeleteFragmentCommand;
import org.osivia.portal.core.assistantpage.CMSPublishDocumentCommand;
import org.osivia.portal.core.assistantpage.ChangeCMSEditionModeCommand;
import org.osivia.portal.core.assistantpage.ChangeModeCommand;
import org.osivia.portal.core.assistantpage.DeletePageCommand;
import org.osivia.portal.core.assistantpage.DeleteWindowCommand;
import org.osivia.portal.core.assistantpage.MoveWindowCommand;
import org.osivia.portal.core.assistantpage.SaveInheritanceConfigurationCommand;
import org.osivia.portal.core.assistantpage.SaveRegionLayoutCommand;
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
import org.osivia.portal.core.search.AdvancedSearchCommand;
import org.osivia.portal.core.tasks.UpdateTaskCommand;
import org.osivia.portal.core.ui.SaveResizableWidthCommand;
import org.osivia.portal.core.urls.BackCommand;


/**
 * Default URL factory.
 *
 * @see URLFactoryDelegate
 */
public class DefaultURLFactory extends URLFactoryDelegate {

    /** Generic command action parameter name. */
    public static final String COMMAND_ACTION_PARAMETER_NAME = "action";

    /** Path. */
    private String path;


    /**
     * Default constructor.
     */
    public DefaultURLFactory() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("No null command accepted");
        }

        if (cmd instanceof DeleteWindowCommand) {
            DeleteWindowCommand command = (DeleteWindowCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            String windowId = command.getWindowId();

            try {
                asu.setParameterValue("action", "deleteWindow");

                asu.setParameterValue("windowId", URLEncoder.encode(windowId, "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }

        if (cmd instanceof DeletePageCommand) {
            DeletePageCommand command = (DeletePageCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            String pageId = command.getPageId();

            try {
                asu.setParameterValue("action", "deletePage");

                asu.setParameterValue("pageId", URLEncoder.encode(pageId, "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }

        if (cmd instanceof ChangeModeCommand) {
            ChangeModeCommand command = (ChangeModeCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            String pageId = command.getPageId();
            String mode = command.getMode();

            try {
                asu.setParameterValue("action", "changeMode");

                asu.setParameterValue("pageId", URLEncoder.encode(pageId, "UTF-8"));
                asu.setParameterValue("mode", URLEncoder.encode(mode, "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }

        if (cmd instanceof ChangeCMSEditionModeCommand) {
            ChangeCMSEditionModeCommand command = (ChangeCMSEditionModeCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            String pageId = command.getPageId();
            String pagePath = command.getPagePath();
            String version = command.getVersion();
            String editionMode = command.getEditionMode();

            try {
                asu.setParameterValue("action", "changeCMSEditionMode");

                asu.setParameterValue("pageId", URLEncoder.encode(pageId, "UTF-8"));
                asu.setParameterValue("pagePath", URLEncoder.encode(pagePath, "UTF-8"));
                asu.setParameterValue("version", URLEncoder.encode(version, "UTF-8"));
                asu.setParameterValue("editionMode", URLEncoder.encode(editionMode, "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }


        if (cmd instanceof RefreshPageCommand) {
            RefreshPageCommand command = (RefreshPageCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            String pageId = command.getPageId();


            try {
                asu.setParameterValue("action", "refreshPage");

                asu.setParameterValue("pageId", URLEncoder.encode(pageId, "UTF-8"));
                
                if(command.getEcmActionReturn() != null) {
                	asu.setParameterValue("ecmActionReturn", URLEncoder.encode(command.getEcmActionReturn(), "UTF-8"));
                }
                if(command.getNewDocId() != null) {
                	asu.setParameterValue("newDocId", URLEncoder.encode(command.getNewDocId(), "UTF-8"));
                }                

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }

        if (cmd instanceof MoveWindowCommand) {
            MoveWindowCommand command = (MoveWindowCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            String windowId = command.getWindowId();
            String moveAction = command.getMoveAction();

            try {
                asu.setParameterValue("action", "moveWindow");

                asu.setParameterValue("windowId", URLEncoder.encode(windowId, "UTF-8"));
                asu.setParameterValue("moveAction", URLEncoder.encode(moveAction, "UTF-8"));


            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }


        if (cmd instanceof StartDynamicWindowCommand) {
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            asu.setParameterValue("action", "startDynamicWindow");
            return asu;


        }

        if (cmd instanceof StartDynamicPageCommand) {
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            asu.setParameterValue("action", "startDynamicPage");
            return asu;
        }
        
        if (cmd instanceof StartDynamicWindowInNewPageCommand) {
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            asu.setParameterValue("action", "startDynamicWindowInNewPage");
            return asu;
        }



        if (cmd instanceof StopDynamicWindowCommand) {
            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            asu.setParameterValue("action", "destroyDynamicWindow");
            return asu;
        }

        if (cmd instanceof StopDynamicPageCommand) {
            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            asu.setParameterValue("action", "destroyDynamicPage");
            return asu;
        }

        if (cmd instanceof PermLinkCommand) {

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            PermLinkCommand vpCmd = (PermLinkCommand) cmd;

            try {

                asu.setParameterValue("action", "permLink");

                if (vpCmd.getPermLinkRef() != null) {
                    asu.setParameterValue("permLinkRef", URLEncoder.encode(vpCmd.getPermLinkRef(), "UTF-8"));
                }

                if (vpCmd.getTemplateInstanciationParentId() != null) {
                    asu.setParameterValue("templateInstanciationParentId", URLEncoder.encode(vpCmd.getTemplateInstanciationParentId(), "UTF-8"));
                }

                if (vpCmd.getParameters() != null) {
                    for (String paramName : vpCmd.getParameters().keySet()) {
                        asu.setParameterValue(paramName, URLEncoder.encode(vpCmd.getParameters().get(paramName), "UTF-8"));
                    }
                }

                if (vpCmd.getCmsPath() != null) {
                    asu.setParameterValue("cmsPath", URLEncoder.encode(vpCmd.getCmsPath(), "UTF-8"));
                }

                if (vpCmd.getPermLinkType() != null) {
                    asu.setParameterValue("permLinkType", URLEncoder.encode(vpCmd.getPermLinkType(), "UTF-8"));
                }

                if (vpCmd.getPortalPersistentName() != null) {
                    asu.setParameterValue("portalPersistentName", URLEncoder.encode(vpCmd.getPortalPersistentName(), "UTF-8"));
                }

                return asu;

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
        }


        // Parameterized command
        if (cmd instanceof ParameterizedCommand) {
            ParameterizedCommand parameterizedCommand = (ParameterizedCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            asu.setParameterValue(COMMAND_ACTION_PARAMETER_NAME, ParameterizedCommand.ACTION);
            try {
                // CMS path
                asu.setParameterValue(ParameterizedCommand.CMS_PATH_PARAMETER, URLEncoder.encode(parameterizedCommand.getCmsPath(), CharEncoding.UTF_8));

                // Template
                if (StringUtils.isNotEmpty(parameterizedCommand.getTemplate())) {
                    asu.setParameterValue(ParameterizedCommand.TEMPLATE_PARAMETER, URLEncoder.encode(parameterizedCommand.getTemplate(), CharEncoding.UTF_8));
                }

                // Renderset
                if (StringUtils.isNotEmpty(parameterizedCommand.getRenderset())) {
                    asu.setParameterValue(ParameterizedCommand.RENDERSET_PARAMETER, URLEncoder.encode(parameterizedCommand.getRenderset(), CharEncoding.UTF_8));
                }

                // Layout state
                if (StringUtils.isNotEmpty(parameterizedCommand.getLayoutState())) {
                    asu.setParameterValue(ParameterizedCommand.LAYOUT_STATE_PARAMETER,
                            URLEncoder.encode(parameterizedCommand.getLayoutState(), CharEncoding.UTF_8));
                }

                // Permalinks indicator
                if (parameterizedCommand.getPermalinks() != null) {
                    asu.setParameterValue(ParameterizedCommand.PERMALINKS_PARAMETER,
                            URLEncoder.encode(parameterizedCommand.getPermalinks().toString(), CharEncoding.UTF_8));
                }
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }
            return asu;
        }


        // Advanced search command
        if (cmd instanceof AdvancedSearchCommand) {
            AdvancedSearchCommand command = (AdvancedSearchCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setParameterValue(COMMAND_ACTION_PARAMETER_NAME, AdvancedSearchCommand.COMMAND_ACTION_VALUE);
            asu.setPortalRequestPath(this.path);

            // Parameters
            try {
                // Search value
                if (StringUtils.isNotBlank(command.getSearch())) {
                    asu.setParameterValue(AdvancedSearchCommand.SEARCH_PARAMETER_NAME, URLEncoder.encode(command.getSearch(), CharEncoding.UTF_8));
                }

                // Advanced search indicator
                if (command.isAdvancedSearch()) {
                    asu.setParameterValue(AdvancedSearchCommand.ADVANCED_SEARCH_PARAMETER_NAME, URLEncoder.encode(String.valueOf(true), CharEncoding.UTF_8));
                }
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }
            return asu;
        }


        /* CMS commands */


        if (cmd instanceof CMSDeleteFragmentCommand) {
            CMSDeleteFragmentCommand command = (CMSDeleteFragmentCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);


            try {
                asu.setParameterValue("action", "CMSDeleteFragment");

                asu.setParameterValue("pageId", URLEncoder.encode(command.getPageId(), "UTF-8"));
                asu.setParameterValue("pagePath", URLEncoder.encode(command.getPagePath(), "UTF-8"));
                asu.setParameterValue("refURI", URLEncoder.encode(command.getRefURI(), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }

        if (cmd instanceof CMSPutDocumentInTrashCommand) {
            CMSPutDocumentInTrashCommand command = (CMSPutDocumentInTrashCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);


            try {
                asu.setParameterValue("action", "CMSPutDocumentInTrashCommand");

                asu.setParameterValue("docId", URLEncoder.encode(command.getDocId(), "UTF-8"));
                asu.setParameterValue("docPath", URLEncoder.encode(command.getDocPath(), "UTF-8"));

                if( command.getBackCMSPageMarker() != null) {
                    asu.setParameterValue("backCMSPageMarker", URLEncoder.encode(command.getBackCMSPageMarker(), "UTF-8"));
                }


            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }

        if (cmd instanceof CMSPublishDocumentCommand) {
            CMSPublishDocumentCommand command = (CMSPublishDocumentCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);


            try {
                asu.setParameterValue("action", "CMSPublishDocument");

                asu.setParameterValue("pageId", URLEncoder.encode(command.getPageId(), "UTF-8"));
                asu.setParameterValue("pagePath", URLEncoder.encode(command.getPagePath(), "UTF-8"));
                asu.setParameterValue("actionCms", URLEncoder.encode(command.getActionCms(), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }


        if (cmd instanceof CMSDeleteDocumentCommand) {
            CMSDeleteDocumentCommand command = (CMSDeleteDocumentCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);


            try {
                asu.setParameterValue("action", "CMSDeleteDocumentCommand");

                asu.setParameterValue("pageId", URLEncoder.encode(command.getPageId(), "UTF-8"));
                asu.setParameterValue("pagePath", URLEncoder.encode(command.getPagePath(), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }


        if (cmd instanceof ChangeContributionModeCommand) {
            ChangeContributionModeCommand command = (ChangeContributionModeCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);


            try {
                asu.setParameterValue("action", "CMSChangeContributionMode");

                asu.setParameterValue("windowID", URLEncoder.encode(command.getWindowID(), "UTF-8"));
                asu.setParameterValue("contributionMode", URLEncoder.encode(command.getNewContributionMode(), "UTF-8"));
                asu.setParameterValue("docPath", URLEncoder.encode(command.getDocPath(), "UTF-8"));

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }


        if (cmd instanceof PublishContributionCommand) {
            PublishContributionCommand command = (PublishContributionCommand) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);


            try {
                asu.setParameterValue("action", "PublishContribution");

                asu.setParameterValue("windowId", URLEncoder.encode(command.getWindowId(), "UTF-8"));
                asu.setParameterValue("docPath", URLEncoder.encode(command.getDocPath(), "UTF-8"));
                asu.setParameterValue("actionCms", URLEncoder.encode(command.getActionCms(), "UTF-8"));
                if( command.getBackCMSPageMarker() != null) {
                    asu.setParameterValue("backCMSPageMarker", URLEncoder.encode(command.getBackCMSPageMarker(), "UTF-8"));
                }

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }


        if (cmd instanceof ToggleAdvancedCMSToolsCommand) {
            // Toggle advanced CMS tools command
            ToggleAdvancedCMSToolsCommand command = (ToggleAdvancedCMSToolsCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            try {
                asu.setParameterValue("action", ToggleAdvancedCMSToolsCommand.ACTION);
                asu.setParameterValue("pageId", URLEncoder.encode(command.getPageId(), CharEncoding.UTF_8));
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }

            return asu;
        }


        if (cmd instanceof SaveInheritanceConfigurationCommand) {
            // Save inheritance configuration command
            SaveInheritanceConfigurationCommand command = (SaveInheritanceConfigurationCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            try {
                asu.setParameterValue("action", SaveInheritanceConfigurationCommand.ACTION);
                asu.setParameterValue("pageId", URLEncoder.encode(command.getPageId(), CharEncoding.UTF_8));
                asu.setParameterValue("pagePath", URLEncoder.encode(command.getPagePath(), CharEncoding.UTF_8));
                asu.setParameterValue("regionName", URLEncoder.encode(command.getRegionName(), CharEncoding.UTF_8));
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }

            return asu;
        }


        if (cmd instanceof SaveRegionLayoutCommand) {
            // Save region layout command
            SaveRegionLayoutCommand command = (SaveRegionLayoutCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            try {
                asu.setParameterValue("action", SaveRegionLayoutCommand.ACTION);
                asu.setParameterValue("pageId", URLEncoder.encode(command.getPageId(), CharEncoding.UTF_8));
                asu.setParameterValue("pagePath", URLEncoder.encode(command.getPagePath(), CharEncoding.UTF_8));
                asu.setParameterValue("regionName", URLEncoder.encode(command.getRegionName(), CharEncoding.UTF_8));
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }

            return asu;
        }


//        if (cmd instanceof EcmFilesManagementCommand) {
//            EcmFilesManagementCommand command = (EcmFilesManagementCommand) cmd;
//
//            //
//            AbstractServerURL asu = new AbstractServerURL();
//            asu.setPortalRequestPath(this.path);
//
//
//            try {
//                asu.setParameterValue("action", "EcmFilesManagementCommand");
//
//                asu.setParameterValue("cmsPath", URLEncoder.encode(command.getCmsPath(), "UTF-8"));
//                asu.setParameterValue("command", URLEncoder.encode(command.getCommand().name(), "UTF-8"));
//
//            } catch (UnsupportedEncodingException e) {
//                // ignore
//            }
//            return asu;
//        }

//		if (cmd instanceof SubscriptionCommand) {
//			SubscriptionCommand command = (SubscriptionCommand) cmd;
//
//            //
//            AbstractServerURL asu = new AbstractServerURL();
//            asu.setPortalRequestPath(this.path);
//
//            try {
//                asu.setParameterValue("action", "SubscriptionCommand");
//
//                asu.setParameterValue("cmsPath", URLEncoder.encode(command.getCmsPath(), "UTF-8"));
//                asu.setParameterValue("command", URLEncoder.encode(command.getCommand().name(), "UTF-8"));
//
//            } catch (UnsupportedEncodingException e) {
//                // ignore
//            }
//            return asu;
//		}
		
		if (cmd instanceof EcmCommandDelegate) {
			EcmCommandDelegate command = (EcmCommandDelegate) cmd;

            //
            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            try {
                asu.setParameterValue("action", "EcmCommandDelegate");

                asu.setParameterValue("cmsPath", URLEncoder.encode(command.getCmsPath(), "UTF-8"));
                asu.setParameterValue("command", URLEncoder.encode(command.getCommand().getCommandName(), "UTF-8"));

                // Redirection path
                String redirectionPath = (String) controllerContext.getAttribute(Scope.SESSION_SCOPE, EcmCommand.REDIRECTION_PATH_ATTRIBUTE);
                if (StringUtils.isNotEmpty(redirectionPath)) {
                    asu.setParameterValue("cmsRedirectionPath", URLEncoder.encode(redirectionPath, "UTF-8"));
                }

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
		}


        // Update task command
        if (cmd instanceof UpdateTaskCommand) {
            UpdateTaskCommand command = (UpdateTaskCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            // Command parameters
            try {
                asu.setParameterValue(DefaultURLFactory.COMMAND_ACTION_PARAMETER_NAME, UpdateTaskCommand.ACTION);

                asu.setParameterValue(UpdateTaskCommand.UUID_PARAMETER, URLEncoder.encode(command.getUuid().toString(), CharEncoding.UTF_8));
                asu.setParameterValue(UpdateTaskCommand.ACTION_ID_PARAMETER, URLEncoder.encode(command.getActionId(), CharEncoding.UTF_8));
                if (MapUtils.isNotEmpty(command.getVariables())) {
                    List<String> variables = new ArrayList<String>(command.getVariables().size());
                    for (Entry<String, String> entry : command.getVariables().entrySet()) {
                        variables.add(StringEscapeUtils.escapeHtml(entry.getKey()) + "=" + StringEscapeUtils.escapeHtml(entry.getValue()));
                    }
                    asu.setParameterValue(UpdateTaskCommand.VARIABLES_PARAMETER, URLEncoder.encode(StringUtils.join(variables, "&"), CharEncoding.UTF_8));
                }
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }

            return asu;
        }


        // Save jQuery UI resizable component value
        if (cmd instanceof SaveResizableWidthCommand) {
            SaveResizableWidthCommand command = (SaveResizableWidthCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            asu.setParameterValue(DefaultURLFactory.COMMAND_ACTION_PARAMETER_NAME, SaveResizableWidthCommand.ACTION);

            // Parameters
            try {
                // Linked to tasks indicator
                asu.setParameterValue(SaveResizableWidthCommand.LINKED_TO_TASKS_PARAMETER,
                        URLEncoder.encode(String.valueOf(command.isLinkedToTasks()), CharEncoding.UTF_8));

                // Resizable width
                if (command.getWidth() != null) {
                    asu.setParameterValue(SaveResizableWidthCommand.WIDTH_PARAMETER, URLEncoder.encode(String.valueOf(command.getWidth()), CharEncoding.UTF_8));
                }
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }

            return asu;
        }


        // Back
        if (cmd instanceof BackCommand) {
            BackCommand command = (BackCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);
            asu.setParameterValue(DefaultURLFactory.COMMAND_ACTION_PARAMETER_NAME, BackCommand.ACTION);

            // Parameters
            try {
                // Page identifier
                String pageId = command.getPageObjectId().toString(PortalObjectPath.SAFEST_FORMAT);
                asu.setParameterValue(BackCommand.PAGE_ID_PARAMETER, URLEncoder.encode(pageId, CharEncoding.UTF_8));

                // Page marker
                String pageMarker = command.getPageMarker();
                asu.setParameterValue(BackCommand.PAGE_MARKER_PARAMETER, URLEncoder.encode(pageMarker, CharEncoding.UTF_8));

                // Refresh indicator
                if (command.isRefresh()) {
                    asu.setParameterValue(BackCommand.REFRESH_PARAMETER, URLEncoder.encode(String.valueOf(true), CharEncoding.UTF_8));
                }
            } catch (UnsupportedEncodingException e) {
                // Do nothing
            }

            return asu;
        }


        return null;
    }


    /**
     * Getter for path.
     *
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Setter for path.
     *
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

}
