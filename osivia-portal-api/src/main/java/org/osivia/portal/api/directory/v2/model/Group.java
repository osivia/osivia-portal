package org.osivia.portal.api.directory.v2.model;

import java.util.List;

import javax.naming.Name;

/**
 * LDAP group.
 * 
 * @author Cédric Krommenhoek
 */
public interface Group {

    /**
     * Get group DN.
     * 
     * @return DN
     */
    Name getDn();


    /**
     * Get group CN.
     * 
     * @return CN
     */
    String getCn();


    /**
     * Set group CN.
     * 
     * @param cn group CN
     */
    void setCn(String cn);


    /**
     * Get group members.
     * 
     * @return members
     */
    List<Name> getMembers();


    /**
     * Set group members.
     * 
     * @param members members
     */
    void setMembers(List<Name> members);

}
