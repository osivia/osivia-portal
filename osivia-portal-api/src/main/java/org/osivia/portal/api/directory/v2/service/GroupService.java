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
     * Get empty group.
     * 
     * @return group
     */
    Group getEmptyGroup();


    /**
     * Get group.
     * 
     * @param dn group DN
     * @return group
     */
    Group get(Name dn);


    /**
     * Get group.
     * 
     * @param cn group CN
     * @return group
     */
    Group get(String CN);


    /**
     * Get group members.
     * 
     * @param dn group DN
     * @return members
     */
    List<Person> getMembers(Name dn);


    /**
     * Search groups by criteria.
     * 
     * @param criteria search criteria
     * @return groups
     */
    List<Group> search(Group criteria);


    /**
     * Delete group.
     * 
     * @param dn group DN
     */
    void delete(Name dn);

}
