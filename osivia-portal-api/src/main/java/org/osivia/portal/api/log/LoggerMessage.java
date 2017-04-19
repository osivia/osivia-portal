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

package org.osivia.portal.api.log;




/**
 * The Class LoggerMessage.
 */
public class LoggerMessage {
    
    /** The msg. */
    private final String msg;
    
    /** The context aware. */
    private final boolean contextAware;
     
    /**
     * Instantiates a new logger message.
     *
     * @param msg the msg
     */
    public LoggerMessage(String msg) {
        this( msg, false);

    }
    
    /**
     * Instantiates a new logger message.
     *
     * @param msg the msg
     * @param contextAware the context aware
     */
    public LoggerMessage(String msg, boolean contextAware) {
        this.msg = msg;
        this.contextAware = contextAware;
    }
    
    
    /**
     * Checks if is context aware.
     *
     * @return true, if is context aware
     */
    public boolean isContextAware() {
        return contextAware;
    }

    /**
     * Gets the msg.
     *
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }
    


}
