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
package org.osivia.portal.administration.ejb;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.core.profils.ProfilBean;

/**
 * Profile data.
 *
 * @author Cédric Krommenhoek
 * @see Serializable
 */
public class ProfileData implements Serializable {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Profile name. */
    private String name;
    /** Profile role. */
    private String role;
    /** Profile URL. */
    private String url;
    /** Profile Nuxeo virtual user. */
    private String nuxeoVirtualUser;


    /**
     * Default constructor.
     */
    public ProfileData() {
        super();
    }

    /**
     * Constructor from profile bean.
     *
     * @param profile profile bean
     */
    public ProfileData(ProfilBean profile) {
        this.name = profile.getName();
        this.role = profile.getRoleName();
        this.url = profile.getDefaultPageName();
        this.nuxeoVirtualUser = profile.getNuxeoVirtualUser();
    }

    /**
     * Constructor from strings array.
     *
     * @param stringsArray strings array
     */
    public ProfileData(String[] stringsArray) {
        String[] resultArray = new String[4];
        Arrays.fill(resultArray, StringUtils.EMPTY);
        System.arraycopy(stringsArray, 0, resultArray, 0, Math.min(stringsArray.length, resultArray.length));
        this.name = resultArray[0];
        this.role = resultArray[1];
        this.url = resultArray[2];
        this.nuxeoVirtualUser = resultArray[3];
    }


    /**
     * Convert current profile data object into profile bean.
     *
     * @return profile bean
     */
    public ProfilBean toProfileBean() {
        ProfilBean bean = new ProfilBean(this.name, this.role, this.url, this.nuxeoVirtualUser);
        return bean;
    }


    /**
     * Convert current profile data object into string array.
     *
     * @return strings array
     */
    public String[] toStringsArray() {
        String[] stringsArray = new String[4];
        stringsArray[0] = this.name;
        stringsArray[1] = this.role;
        stringsArray[2] = this.url;
        stringsArray[3] = this.nuxeoVirtualUser;
        return stringsArray;
    }


    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for role.
     *
     * @return the role
     */
    public String getRole() {
        return this.role;
    }

    /**
     * Setter for role.
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Getter for url.
     *
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Setter for url.
     *
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter for nuxeoVirtualUser.
     *
     * @return the nuxeoVirtualUser
     */
    public String getNuxeoVirtualUser() {
        return this.nuxeoVirtualUser;
    }

    /**
     * Setter for nuxeoVirtualUser.
     *
     * @param nuxeoVirtualUser the nuxeoVirtualUser to set
     */
    public void setNuxeoVirtualUser(String nuxeoVirtualUser) {
        this.nuxeoVirtualUser = nuxeoVirtualUser;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ProfileData [name=" + this.name + ", role=" + this.role + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ProfileData other = (ProfileData) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
