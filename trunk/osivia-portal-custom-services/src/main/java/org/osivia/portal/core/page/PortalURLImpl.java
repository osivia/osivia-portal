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

    private boolean initState = false;


    /**
     * @return the initState
     */
    public boolean isInitState() {
        return initState;
    }


    /**
     * @param initState the initState to set
     */
    public void setInitState(boolean initState) {
        this.initState = initState;
    }

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

            if (initState) {
                value += "?init-state=true";
            }
		}
		return value;
	}	

}
