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
package org.osivia.portal.core.cms;

public class CMSException extends Exception {
	
	public static int ERROR_FORBIDDEN = 1;
	public static int ERROR_UNAVAILAIBLE = 2;
	public static int ERROR_NOTFOUND = 3;	
	

	private static final long serialVersionUID = 1L;
	
	private int errorCode;
	
	public int getErrorCode() {
		return errorCode;
	}

    public CMSException(Throwable e) {
        super(e);
	}

	public CMSException(int errorCode) {
        this.errorCode = errorCode;
    }	
	
    public CMSException(String message) {
        super(message);
    }

    public CMSException(String message, Throwable e) {
        super(message, e);
    }

}
