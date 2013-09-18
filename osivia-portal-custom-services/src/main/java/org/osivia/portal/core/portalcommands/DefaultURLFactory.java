package org.osivia.portal.core.portalcommands;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.core.assistantpage.CMSDeleteFragmentCommand;
import org.osivia.portal.core.assistantpage.CMSPublishDocumentCommand;
import org.osivia.portal.core.assistantpage.ChangeCMSEditionModeCommand;
import org.osivia.portal.core.assistantpage.ChangeModeCommand;
import org.osivia.portal.core.assistantpage.DeletePageCommand;
import org.osivia.portal.core.assistantpage.DeleteWindowCommand;
import org.osivia.portal.core.assistantpage.MoveWindowCommand;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.dynamic.StopDynamicPageCommand;
import org.osivia.portal.core.dynamic.StopDynamicWindowCommand;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.page.RefreshPageCommand;





public class DefaultURLFactory extends URLFactoryDelegate {

	/** . */
	private String path;

	public ServerURL doMapping(ControllerContext controllerContext,
			ServerInvocation invocation, ControllerCommand cmd) {
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

				asu.setParameterValue("windowId", URLEncoder.encode(windowId,
						"UTF-8"));

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

				asu.setParameterValue("pageId", URLEncoder.encode(pageId,
						"UTF-8"));

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

				asu.setParameterValue("pageId", URLEncoder.encode(pageId,"UTF-8"));
				asu.setParameterValue("mode", URLEncoder.encode(mode,"UTF-8"));

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

				asu.setParameterValue("pageId", URLEncoder.encode(pageId,"UTF-8"));
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

				asu.setParameterValue("pageId", URLEncoder.encode(pageId,"UTF-8"));

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

				asu.setParameterValue("windowId", URLEncoder.encode(windowId,
						"UTF-8"));
				asu.setParameterValue("moveAction", URLEncoder.encode(moveAction,
				"UTF-8"));


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

		if( cmd instanceof PermLinkCommand){

			AbstractServerURL asu = new AbstractServerURL();
			asu.setPortalRequestPath(this.path);

            PermLinkCommand vpCmd = (PermLinkCommand)cmd;

			try {

				asu.setParameterValue("action", "permLink");

				if( vpCmd.getPermLinkRef() != null) {
                    asu.setParameterValue("permLinkRef", URLEncoder.encode(vpCmd.getPermLinkRef(),
                    "UTF-8"));
                }

				if (vpCmd.getTemplateInstanciationParentId() != null) {
                    asu.setParameterValue("templateInstanciationParentId", URLEncoder.encode(vpCmd.getTemplateInstanciationParentId(),
					"UTF-8"));
                }

				if( vpCmd.getParameters() != null){
					for(String paramName :  vpCmd.getParameters().keySet())	{
						asu.setParameterValue( paramName, URLEncoder.encode(vpCmd.getParameters().get(paramName),	"UTF-8"));
					}
				}

	            if (vpCmd.getCmsPath()!= null) {
                    asu.setParameterValue("cmsPath", URLEncoder.encode(vpCmd.getCmsPath(),
                    "UTF-8"));
                }

	            if (vpCmd.getPermLinkType()!= null) {
                    asu.setParameterValue("permLinkType", URLEncoder.encode(vpCmd.getPermLinkType(),
                    "UTF-8"));
                }

	            if (vpCmd.getPortalPersistentName() != null) {
                    asu.setParameterValue("portalPersistentName", URLEncoder.encode(vpCmd.getPortalPersistentName(),
					"UTF-8"));
                }

	            return asu;

			} catch (UnsupportedEncodingException e) {
				// ignore
			}



		}

		/* CMS commands */


		if (cmd instanceof CMSDeleteFragmentCommand) {
			CMSDeleteFragmentCommand command = (CMSDeleteFragmentCommand) cmd;

			//
			AbstractServerURL asu = new AbstractServerURL();
			asu.setPortalRequestPath(this.path);


			try {
				asu.setParameterValue("action", "CMSDeleteFragment");

				asu.setParameterValue("pageId", URLEncoder.encode(command.getPageId(),"UTF-8"));
				asu.setParameterValue("pagePath", URLEncoder.encode(command.getPagePath(),"UTF-8"));
				asu.setParameterValue("refURI", URLEncoder.encode(command.getRefURI(),"UTF-8"));

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

		return null;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
