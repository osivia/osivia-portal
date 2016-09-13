/*
 * (C) Copyright 2016 OSIVIA (http://www.osivia.com)
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
package org.osivia.portal.api.directory.v2.model;

import java.util.List;

import javax.naming.Name;

import org.osivia.portal.api.urls.Link;

/**
 * Representation of a person which exists in the LDAP directory.
 * 
 * @author Loïc Billon
 * @since 4.4
 */
public interface Person {

    /**
     * Get DN.
     * 
     * @return DN
     */
    Name getDn();


    /**
     * Set DN.
     * 
     * @param dn DN
     */
    void setDn(Name dn);


    /**
     * Get CN.
     * 
     * @return CN
     */
    String getCn();


    /**
     * Set CN.
     * 
     * @param cn CN
     */
    void setCn(String cn);


    /**
     * Get SN.
     * 
     * @return SN
     */
    String getSn();


    /**
     * Set SN
     * 
     * @param sn SN
     */
    void setSn(String sn);


    /**
     * Get display name.
     * 
     * @return display name
     */
    String getDisplayName();


    /**
     * Set display name
     * 
     * @param displayName display name
     */
    void setDisplayName(String displayName);


    /**
     * Get given name.
     * 
     * @return given name
     */
    String getGivenName();


    /**
     * Set given name.
     * 
     * @param givenName given name
     */
    void setGivenName(String givenName);


    /**
     * Get email.
     * 
     * @return email
     */
    String getMail();


    /**
     * Set email.
     * 
     * @param mail email
     */
    void setMail(String mail);


    /**
     * Get title.
     * 
     * @return title
     */
    String getTitle();


    /**
     * Set title.
     * 
     * @param title title
     */
    void setTitle(String title);


    /**
     * Get UID.
     * 
     * @return UID
     */
    String getUid();


    /**
     * Set UID.
     * 
     * @param uid UID
     */
    void setUid(String uid);


    /**
     * Get profiles.
     * 
     * @return profiles
     */
    List<Name> getProfiles();


    /**
     * Set profiles.
     * 
     * @param profiles profiles
     */
    void setProfiles(List<Name> profiles);


    /**
     * Get avatar link.
     * 
     * @return avatar link
     */
    Link getAvatar();


    /**
     * Set avatar link.
     * 
     * @param avatar avatar link
     */
    void setAvatar(Link avatar);


    /**
     * Build the DN using the ldap base and organizational units.
     * 
     * @param uid UID
     */
    Name buildDn(String uid);

}
