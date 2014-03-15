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
package org.osivia.portal.api;



/**
 * The Class PortalException.
 */
public class PortalException extends Exception{  


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new portal exception.
     */
    public PortalException() {}  
  
    /**
     * Instantiates a new portal exception.
     *
     * @param message the message
     */
    public PortalException(String message) {  
        super(message); 
    }  
 
    /**
     * Instantiates a new portal exception.
     *
     * @param cause the cause
     */
    public PortalException(Throwable cause) {  
        super(cause); 
    }  
    
    /**
     * Instantiates a new portal exception.
     *
     * @param cause the cause
     */
    public PortalException(Exception cause) {  
        super(cause); 
    }  

    /**
     * Instantiates a new portal exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public PortalException(String message, Throwable cause) {  
        super(message, cause); 
    } 
    
    /**
     * Wrap.
     *
     * @param e the e
     * @return the portal exception
     * @throws PortalException the portal exception
     */
    public static PortalException wrap( Exception e) throws PortalException {
        if( e instanceof RuntimeException)
            throw ((RuntimeException) e);
        if( e instanceof PortalException)
            throw (PortalException) e;
         throw new PortalException(e);
   }
}
