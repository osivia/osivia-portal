package org.osivia.portal.core.tasks;

import java.util.Map;
import java.util.UUID;

import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;

/**
 * 
 * JBoss command from link in mails, to validate a task without actor
 * 
 * @author Loïc Billon
 *
 */
public class UpdateTaskNoActorCommand extends UpdateTaskCommand {

	public static final String ACTION_NO_ACTOR = "updateTaskNoActor";

	public UpdateTaskNoActorCommand(UUID uuid, String actionId, Map<String, String> variables, String redirectionUrl) {
		super(uuid, actionId, variables, redirectionUrl);
	}

	@Override
	protected boolean updateTask(UUID uuid, String actionId, Map<String, String> variables) throws CMSException {

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(this.context);
        
    	return cmsService.updateTaskNoActor(cmsContext, uuid, actionId, variables);
	}
	
	
	@Override
	public String getAction() {
		return ACTION_NO_ACTOR;
	}

}
