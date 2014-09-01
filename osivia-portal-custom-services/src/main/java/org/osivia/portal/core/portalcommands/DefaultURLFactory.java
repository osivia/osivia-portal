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

import org.apache.commons.lang.CharEncoding;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.core.assistantpage.CMSDeleteDocumentCommand;
import org.osivia.portal.core.assistantpage.CMSDeleteFragmentCommand;
import org.osivia.portal.core.assistantpage.CMSPublishDocumentCommand;
import org.osivia.portal.core.assistantpage.ChangeCMSEditionModeCommand;
import org.osivia.portal.core.assistantpage.ChangeModeCommand;
import org.osivia.portal.core.assistantpage.DeletePageCommand;
import org.osivia.portal.core.assistantpage.DeleteWindowCommand;
import org.osivia.portal.core.assistantpage.MoveWindowCommand;
import org.osivia.portal.core.assistantpage.SaveInheritanceConfigurationCommand;
import org.osivia.portal.core.cms.CMSPutDocumentInTrashCommand;
import org.osivia.portal.core.contribution.ChangeContributionModeCommand;
import org.osivia.portal.core.contribution.PublishContributionCommand;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.dynamic.StopDynamicPageCommand;
import org.osivia.portal.core.dynamic.StopDynamicWindowCommand;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.search.AdvancedSearchCommand;


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


        // Advanced search command
        if (cmd instanceof AdvancedSearchCommand) {
            AdvancedSearchCommand advancedSearchCommand = (AdvancedSearchCommand) cmd;

            AbstractServerURL asu = new AbstractServerURL();
            asu.setPortalRequestPath(this.path);

            // Parameters
            try {
                asu.setParameterValue(COMMAND_ACTION_PARAMETER_NAME, AdvancedSearchCommand.COMMAND_ACTION_VALUE);
                asu.setParameterValue(AdvancedSearchCommand.PAGE_ID_PARAMETER_NAME, URLEncoder.encode(advancedSearchCommand.getPageId(), CharEncoding.UTF_8));
                asu.setParameterValue(AdvancedSearchCommand.SEARCH_PARAMETER_NAME, URLEncoder.encode(advancedSearchCommand.getSearch(), CharEncoding.UTF_8));
                asu.setParameterValue(AdvancedSearchCommand.ADVANCED_SEARCH_PARAMETER_NAME,
                        URLEncoder.encode(String.valueOf(advancedSearchCommand.isAdvancedSearch()), CharEncoding.UTF_8));
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

                if( command.getBackCMSPageMarker() != null)
                    asu.setParameterValue("backCMSPageMarker", URLEncoder.encode(command.getBackCMSPageMarker(), "UTF-8"));


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

            } catch (UnsupportedEncodingException e) {
                // ignore
            }
            return asu;
        }


        if (cmd instanceof SaveInheritanceConfigurationCommand) {
            // Save inheritance configuration
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
