package org.osivia.portal.core.page;

import org.jboss.portal.api.PortalURL;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;

public class PortalURLImpl implements PortalURL{
	
	/** . */
	private ControllerCommand command;

	/** . */
	private ControllerContext context;

	/** . */
	private Boolean wantAuthenticated;

	/** . */
	private Boolean wantSecure;

	/** . */
	private boolean relative;

	/** . */
	private String value;

	public PortalURLImpl(ControllerCommand command, ControllerContext context, Boolean wantAuthenticated,
			Boolean wantSecure) {
		this.command = command;
		this.context = context;
		this.wantAuthenticated = wantAuthenticated;
		this.wantSecure = wantSecure;
		this.relative = false;
		this.value = null;
	}

	public void setAuthenticated(Boolean wantAuthenticated) {
		this.wantAuthenticated = wantAuthenticated;
		this.value = null;
	}

	public void setSecure(Boolean wantSecure) {
		this.wantSecure = wantSecure;
		this.value = null;
	}

	public void setRelative(boolean relative) {
		this.relative = relative;
		this.value = null;
	}

	public String toString() {
		if (value == null) {
			URLContext urlContext = context.getServerInvocation().getServerContext().getURLContext();
			urlContext = urlContext.withAuthenticated(wantAuthenticated).withSecured(wantSecure);
			value = context.renderURL(command, urlContext, URLFormat.newInstance(relative, true));
		}
		return value;
	}	

}
