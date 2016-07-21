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
 * Representation of a person which exists in the LDAP directory
 * @author Loïc Billon
 * @since 4.4
 */
public interface Person {


	/**
	 * @return the dn
	 */
	public Name getDn();

	/**
	 * @param dn the dn to set
	 */
	public void setDn(Name dn);

	/**
	 * @return the cn
	 */
	public String getCn();

	/**
	 * @param cn the cn to set
	 */
	public void setCn(String cn);

	/**
	 * @return the sn
	 */
	public String getSn();

	/**
	 * @param sn the sn to set
	 */
	public void setSn(String sn);

	/**
	 * @return the displayName
	 */
	public String getDisplayName();

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName);

	/**
	 * @return the givenName
	 */
	public String getGivenName();

	/**
	 * @param givenName the givenName to set
	 */
	public void setGivenName(String givenName);

	/**
	 * @return the mail
	 */
	public String getMail();

	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail);

	/**
	 * @return the title
	 */
	public String getTitle();

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title);

	/**
	 * @return the uid
	 */
	public String getUid();

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid);

	/**
	 * @return the portalPersonProfile
	 */
	public List<Name> getPortalPersonProfile();

	/**
	 * @param portalPersonProfile the portalPersonProfile to set
	 */
	public void setPortalPersonProfile(List<Name> portalPersonProfile);
	
	/**
	 * @return the avatar
	 */
	public Link getAvatar();
	
	/**
	 * @param avatar the avatar to set
	 */
	public void setAvatar(Link avatar);

	/**
	 * Build the DN using the ldap base and organizational units
	 * @param uid the person uid
	 */
	public Name buildDn(String uid);

}
