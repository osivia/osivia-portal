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

public class UnavailableServer extends Exception {


	private static final long serialVersionUID = -19758871528355142L;
	
	int httpCode = -1;
	
	String message = null;

	public UnavailableServer( int httpCode) {
		this.httpCode = httpCode;
	}

	public UnavailableServer( String message) {
		this.message = message;
	}
	
	public String toString()	{
		String res = "";
		if( httpCode != -1)
			res += "http_code : " + httpCode;
		if( message != null)
			res += "message : " + message;
		return res;
	}


}
