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
 * Document publication informations.
 * 
 * @author Loïc Billon
 * @author Cédric Krommenhoek
 */
public interface PublicationInfos {

    /**
     * Get document path.
     * 
     * @return path
     */
    String getPath();


    /**
     * Get live document identifier.
     * 
     * @return document identifier
     */
    String getLiveId();


    /**
     * Check if the document is published.
     * 
     * @return true if the document is published
     */
    boolean isPublished();


    /**
     * Check if the document is being modified.
     * 
     * @return true if the document is being modified
     */
    boolean isBeingModified();


    /**
     * Get live space indicator.
     * 
     * @return true if the document is inside a live space
     */
    boolean isLiveSpace();


    /**
     * Get space path.
     * 
     * @return path
     */
    String getSpacePath();


    /**
     * Get space display name.
     * 
     * @return display name
     */
    String getSpaceDisplayName();


    /**
     * Get space type.
     * 
     * @return type
     */
    String getSpaceType();


    /**
     * Check if the document has a draft.
     * 
     * @return true if the document has a draft
     */
    boolean hasDraft();


    /**
     * Check if the document is a draft.
     * 
     * @return true if the document is a draft
     */
    boolean isDraft();


    /**
     * Check if the document is an orphan draft.
     * 
     * @return true if the document is an orphan draft
     */
    boolean isOrphanDraft();


    /**
     * Get draft path.
     * 
     * @return path
     */
    String getDraftPath();


    /**
     * Get draft contextualization path.
     * 
     * @return path
     */
    String getDraftContextualizationPath();

}
