/**
 * 
 */
package org.osivia.portal.core.mt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.portal.command.render.RenderWindowCommand;

public class MTLoggerInterceptor extends ControllerInterceptor {

	static final Log logger = LogFactory.getLog(MTLoggerInterceptor.class);

	@SuppressWarnings("unchecked")
	public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
		
	if (cmd instanceof RenderWindowCommand) {
			
			

			RenderWindowCommand rwc = (RenderWindowCommand) cmd;
			
			if( rwc.getWindow().getName().contains("proc"))	{
			
				//logger.debug("logger proc "+ rwc.getWindow().getName());
			}
		}
	
	

		ControllerResponse resp = (ControllerResponse) cmd.invokeNext();

	

		return resp;
	}

	
}
