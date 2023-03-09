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

import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.IDirService;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.urls.Link;

import javax.naming.Name;
import java.util.List;

/**
 * Service to request, create, update persons, aggregated between nuxeo and ldap
 *
 * @author Loïc Billon
 * @see IDirService
 * @since 4.4
 */
public interface PersonService extends IDirService {

    /**
     * Get a person with no valued fields (for search)
     *
     * @return empty object person
     */
    Person getEmptyPerson();


    /**
     * Get a person by it's full DN
     *
     * @param dn
     * @return the person
     */
    Person getPerson(Name dn);


    /**
     * Get a person by it's uid
     *
     * @param uid
     * @return the person
     */
    Person getPerson(String uid);


    /**
     * Get refreshed person by identifier.
     *
     * @param uid user identifier
     * @return person
     */
    Person refreshPerson(String uid);


    /**
     * Get a person by criteria represented by a person vith filled fields
     *
     * @param p a person
     * @return a list of person
     */
    List<Person> findByCriteria(Person p);


    /**
     * Get a link to the card person portlet
     *
     * @param portalControllerContext
     * @param p                       the person
     * @return a link to the card person portlet
     * @throws PortalException
     */
    Link getCardUrl(PortalControllerContext portalControllerContext, Person p) throws PortalException;


    /**
     * Get a link to my card
     *
     * @param portalControllerContext
     * @return a link to the card person portlet
     * @throws PortalException
     */
    Link getMyCardUrl(PortalControllerContext portalControllerContext) throws PortalException;


    /**
     * Get a document with user profile properties
     *
     * @param portalControllerContext
     * @param person
     * @return document UserProfile
     * @throws PortalException
     */
    Object getEcmProfile(PortalControllerContext portalControllerContext, Person person) throws PortalException;


    /**
     * Check if the current user is a portal administrator.
     *
     * @param portalControllerContext portal controller context
     * @return true if the current user is a portal administrator
     * @throws PortalException
     */
    boolean isPortalAdministrator(PortalControllerContext portalControllerContext) throws PortalException;

}
