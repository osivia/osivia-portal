package org.osivia.portal.api.directory.v2.service;

import java.util.List;

import javax.naming.Name;

import org.osivia.portal.api.directory.v2.IDirService;
import org.osivia.portal.api.directory.v2.model.Group;
import org.osivia.portal.api.directory.v2.model.Person;

/**
 * Group service interface.
 * 
 * @author Cédric Krommenhoek
 * @see IDirService
 * @since 4.4
 */
public interface GroupService extends IDirService {

    /**
     * Get group.
     * 
     * @param id group identifier
     * @return group
     */
    Group get(String id);


    /**
     * Get group members.
     * 
     * @param dn group DN
     * @return members
     */
    List<Person> getMembers(Name dn);

}
