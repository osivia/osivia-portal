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
package org.osivia.portal.core.error;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class ErrorDescriptor {
	
	public final static int NO_HTTP_ERR_CODE = -1;

	protected long errorId;
	protected Date registrationDate;
	protected int httpErrCode;
	protected Throwable exception;	
	/** Trace or message if exception is null. */
	protected String msg;
	protected String userId;
	protected Map<String, Object> properties;

	public ErrorDescriptor(int httpErrCode, Throwable exception, 
			String msg, String userId,
			Map<String, Object> properties) {
		super();
		this.registrationDate = new Date();
		this.httpErrCode = httpErrCode;
		this.exception = exception;
		this.msg = msg;
		this.userId = userId;
		this.properties = new HashMap<String, Object>();
		if( properties != null) {
			this.properties.putAll(properties);
		}
	}

	public long getErrorId() {
		return errorId;
	}	

	protected void setErrorId(long errorId) {
		this.errorId = errorId;
	}

	public Date getDate() {
		return registrationDate;
	}

	public int getHttpErrCode() {
		return httpErrCode;
	}

	public Throwable getException() {
		return exception;
	}

	public String getMessage() {
			return msg;
	}
	
	public String getStack() {
	    return Debug.throwableToString(exception);
   }


	
	public String getUserId() {
		return userId;
	}

	public Map<String, Object> getProperties() {
		return new HashMap<String, Object>(properties);
	}

	public String getProperty(String key) {
		Object value = properties.get(key);
		if( value == null) {
			return null;
		} else {
			return value.toString();
		}
	}
	
	public void addProperty(String key, Object value) {
		properties.put(key, value);		
	}

}
