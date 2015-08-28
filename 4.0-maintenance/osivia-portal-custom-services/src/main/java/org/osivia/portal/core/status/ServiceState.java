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
package org.osivia.portal.core.status;


public class ServiceState {
	
	private long lastCheckTimestamp = 0L;
	private boolean serviceUp = true;
	
	private String url = "";
	

    private boolean mustBeChecked = false;
    
    public boolean isMustBeChecked() {
        return mustBeChecked;
    }

    public void setMustBeChecked(boolean mustBeChecked) {
        this.mustBeChecked = mustBeChecked;
    }	
	

	public long getLastCheckTimestamp() {
		return lastCheckTimestamp;
	}

	public void setLastCheckTimestamp(long lastCheckTimestamp) {
		this.lastCheckTimestamp = lastCheckTimestamp;
	}
	
	public boolean isServiceUp() {
		return serviceUp;
	}
	


	public void setServiceUp(boolean serviceUp) {
		this.serviceUp = serviceUp;
	}

	public ServiceState( String url) {
		super();
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
