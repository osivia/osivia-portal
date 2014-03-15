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
package org.osivia.portal.api.status;


/**
 * The Class UnavailableServer.
 */
public class UnavailableServer extends Exception {


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -19758871528355142L;
	
	/** The http code. */
	int httpCode = -1;
	
	/** The message. */
	String message = null;

	/**
	 * Instantiates a new unavailable server.
	 *
	 * @param httpCode the http code
	 */
	public UnavailableServer( int httpCode) {
		this.httpCode = httpCode;
	}

	/**
	 * Instantiates a new unavailable server.
	 *
	 * @param message the message
	 */
	public UnavailableServer( String message) {
		this.message = message;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#toString()
	 */
	public String toString()	{
		String res = "";
		if( httpCode != -1)
			res += "http_code : " + httpCode;
		if( message != null)
			res += "message : " + message;
		return res;
	}


}
