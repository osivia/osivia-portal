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
package org.osivia.portal.api.cms;

/**
 * Document permissions.
 * 
 * @author Loïc Billon
 * @author Cédric Krommenhoek
 */
public interface Permissions {

    /**
     * Check if the document is editable by the current user.
     * 
     * @return true if the document is editable by the current user
     */
    boolean isEditable();


    /**
     * Check if the document is manageable by the current user.
     * 
     * @return true if the document is manageable by the current user
     */
    boolean isManageable();


    /**
     * Check if the document can be deleted by the current user.
     * 
     * @return true if the document can be deleted by the current user
     */
    boolean isDeletable();


    /**
     * Check if the document is commentable by the current user.
     * 
     * @return true if the document is commentable by the current user
     */
    boolean isCommentable();


    /**
     * Check if the document is readable by an anonymous user.
     * 
     * @return true if the document is readable by an anonymous user
     */
    boolean isAnonymouslyReadable();

}
