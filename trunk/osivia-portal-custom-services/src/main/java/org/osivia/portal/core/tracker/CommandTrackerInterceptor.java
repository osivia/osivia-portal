package org.osivia.portal.core.tracker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.command.PortalObjectCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.tracker.ITracker;



@SuppressWarnings("unchecked")
public class CommandTrackerInterceptor extends ControllerInterceptor{
	
	private ITracker tracker;
	
	
	public ITracker getTracker() {
		return tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}

	private Log log = LogFactory.getLog(CommandTrackerInterceptor.class);


	public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
			
		getTracker().pushState(cmd);
		

		
		
		ControllerResponse resp = (ControllerResponse) cmd.invokeNext();

		getTracker().popState();
		
		

	
		return resp;
	}
	
	
	
}
