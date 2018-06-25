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
 */
package org.osivia.portal.core.cms;

import org.apache.commons.lang.StringUtils;

/**
 * CMS exception.
 * 
 * @see Exception
 */
public class CMSException extends Exception {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Forbidden. */
    public static final int ERROR_FORBIDDEN = 1;
    /** Unavailable. */
    public static final int ERROR_UNAVAILAIBLE = 2;
    /** Not found. */
    public static final int ERROR_NOTFOUND = 3;

    /** Error code. */
    private final int errorCode;
    
    private Satellite satellite;
    private String commandId;

    /**
	 * @return the commandId
	 */
	public String getCommandId() {
		return commandId;
	}

	/**
	 * @param commandId the commandId to set
	 */
	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	/**
	 * @return the satelliteName
	 */
	public Satellite getSatellite() {
		return satellite;
	}

	/**
	 * @param satelliteName the satelliteName to set
	 */
	public void setSatellite(Satellite satelliteName) {
		this.satellite = satellite;
	}

	/**
     * Constructor.
     * @param e cause
     */
    public CMSException(Throwable e) {
        super(e);
        this.errorCode = 0;
    }

    /**
     * Constructor.
     * @param errorCode error code
     */
    public CMSException(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Constructor.
     * @param message error message
     */
    public CMSException(String message) {
        super(message);
        this.errorCode = 0;
    }

    /**
     * Constructor.
     * @param message error message
     * @param e cause
     */
    public CMSException(String message, Throwable e) {
        super(message, e);
        this.errorCode = 0;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
    	
 //       String causeMessage = super.getMessage();

         StringBuilder message = new StringBuilder();

		String nuxeoSrc = "NUXEO/"+Satellite.getAsKey(satellite);
	
		message.append("Source=" + nuxeoSrc );
		
		if( commandId != null)
			message.append(", Command=" + commandId );		
		
        if (errorCode > 0) {
            switch (errorCode) {
                case ERROR_FORBIDDEN:
                	message.append(", rc=403");
                    break;
                case ERROR_NOTFOUND:
                    message.append(", rc=404");
                    break;
            }
        }

//        if (causeMessage != null) {
//        		message.append("CAUSE     : \n");
//        }
//        message.append(StringUtils.trimToEmpty(causeMessage));

        return message.toString();
    }


    
    
    
    /**
     * Getter for errorCode.
     * 
     * @return the errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

}
