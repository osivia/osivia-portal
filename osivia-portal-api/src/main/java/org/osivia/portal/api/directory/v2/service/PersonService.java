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
package org.osivia.portal.api.directory.v2.service;

import java.util.List;

import javax.naming.Name;

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.IDirService;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.urls.Link;

/**
 * Service to request, create, update persons, aggregated between nuxeo and ldap
 * @author Loïc Billon
 * @since 4.4
 */
public interface PersonService extends IDirService {


	/**
	 * Get a person with no valued fields (for search)
	 * @return empty object person
	 */
	public Person getEmptyPerson();
	
	/**
	 * Get a person by it's full DN
	 * @param dn
	 * @return the person
	 */
	public Person getPerson(Name dn);
	
	
	/**
	 * Get a person by it's uid
	 * @param uid
	 * @return the person
	 */
	public Person getPerson(String uid);

	/**
	 * Get a person by criteria represented by a person vith filled fields
	 * @param p a person 
	 * @return a list of person
	 */
	List<Person> findByCriteria(Person p);
	

	/**
	 * Get a link to the card person portlet
	 * @param portalControllerContext
	 * @param p the person
	 * @return a link to the card person portlet
	 * @throws PortalException
	 */
	Link getCardUrl(PortalControllerContext portalControllerContext, Person p)
			throws PortalException;

	/**
	 * Get a document with user profile properties
	 * @param portalControllerContext
	 * @param person
	 * @return document UserProfile
	 * @throws PortalException
	 */
	Object getEcmProfile(PortalControllerContext portalControllerContext,
			Person person) throws PortalException;
}
