/*
 * (C) Copyright 2014 Académie de Rennes (http://www.ac-rennes.fr/), OSIVIA (http://www.osivia.com) and others.
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

import javax.security.auth.Subject;



/**
 * The Class BinaryDelegation grants access to a BinaryServlet from portlet
 * 
 */
public class BinaryDelegation {
    
    /** The granted access. */
    private boolean grantedAccess= false;
    
    /** The is admin. */
    private boolean isAdmin= false;    
    
    /** The subject. */
    private Subject subject;
    
    
    
    
    /**
     * Gets the subject.
     *
     * @return the subject
     */
    public Subject getSubject() {
        return subject;
    }


    
    /**
     * Sets the subject.
     *
     * @param subject the new subject
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }


    /**
     * Checks if is admin.
     *
     * @return true, if is admin
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    
    /**
     * Sets the admin.
     *
     * @param isAdmin the new admin
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * Checks if is granted access.
     *
     * @return true, if is granted access
     */
    public boolean isGrantedAccess() {
        return grantedAccess;
    }
    
    /**
     * Sets the granted access.
     *
     * @param grantedAccess the new granted access
     */
    public void setGrantedAccess(boolean grantedAccess) {
        this.grantedAccess = grantedAccess;
    }


    /** The user name. */
    private String userName = null;

    
    /**
     * Gets the user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    
    /**
     * Sets the user name.
     *
     * @param userName the new user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

}
