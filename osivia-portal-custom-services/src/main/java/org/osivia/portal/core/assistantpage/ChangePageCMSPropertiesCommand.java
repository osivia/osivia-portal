package org.osivia.portal.core.assistantpage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.core.cms.CmsCommand;


public class ChangePageCMSPropertiesCommand extends AssistantCommand {

	private String pageId;
	private String cmsBasePath;
	private String scope;
	private String pageContextualizationSupport;
	private String outgoingRecontextualizationSupport;
	private String navigationScope;
	private String cmsNavigationMode;
	private String displayLiveVersion;

	public String getPageId() {
		return pageId;
	}

	public ChangePageCMSPropertiesCommand() {
	}

	public ChangePageCMSPropertiesCommand(String pageId, String cmsBasePath, String scope, String pageContextualizationSupport, String outgoingRecontextualizationSupport, String navigationScope, String displayLiveVersion) {
		this.pageId = pageId;
		this.cmsBasePath = cmsBasePath;
		this.scope=scope;
		this.pageContextualizationSupport = pageContextualizationSupport;
		this.outgoingRecontextualizationSupport = outgoingRecontextualizationSupport;
		this.navigationScope = navigationScope;
		this.cmsNavigationMode = cmsNavigationMode;
		this.displayLiveVersion = displayLiveVersion;
	
	}

	public ControllerResponse executeAssistantCommand() throws Exception {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

		if (cmsBasePath != null && cmsBasePath.length() != 0) {
			page.setDeclaredProperty("osivia.cms.basePath", cmsBasePath);
		} else {
			page.setDeclaredProperty("osivia.cms.basePath", null);
		}
		
		if (navigationScope != null && navigationScope.length() != 0) {
			page.setDeclaredProperty("osivia.cms.navigationScope", navigationScope);
		} else {
			page.setDeclaredProperty("osivia.cms.navigationScope", null);
		}
		
		
		if (scope != null && scope.length() != 0) {
			page.setDeclaredProperty("osivia.cms.scope", scope);
		} else {
			page.setDeclaredProperty("osivia.cms.scope", null);
		}
		
		
		if (displayLiveVersion != null && displayLiveVersion.length() != 0) {
			page.setDeclaredProperty("osivia.cms.displayLiveVersion", displayLiveVersion);
		} else {
			page.setDeclaredProperty("osivia.cms.displayLiveVersion", null);
		}


		
		if (pageContextualizationSupport != null && pageContextualizationSupport.length() != 0) {
			page.setDeclaredProperty("osivia.cms.pageContextualizationSupport", pageContextualizationSupport);
		} else {
			page.setDeclaredProperty("osivia.cms.pageContextualizationSupport", null);
		}
		
		if (outgoingRecontextualizationSupport != null && outgoingRecontextualizationSupport.length() != 0) {
			page.setDeclaredProperty("osivia.cms.outgoingRecontextualizationSupport", outgoingRecontextualizationSupport);
		} else {
			page.setDeclaredProperty("osivia.cms.outgoingRecontextualizationSupport", null);
		}
		

		return new UpdatePageResponse(page.getId());

	}

}
