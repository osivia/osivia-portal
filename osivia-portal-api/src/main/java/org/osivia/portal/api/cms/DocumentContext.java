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
 * Document context.
 * 
 * @author Loïc Billon
 * @author Cédric Krommenhoek
 */
public interface DocumentContext {

    /**
     * Get CMS path.
     * 
     * @return CMS path
     * @since 4.7
     */
    String getCmsPath();


    /**
     * Get webId.
     * 
     * @return webId
     * @since 4.7
     */
    String getWebId();


    /**
     * Get document permissions.
     * 
     * @return permissions
     */
    Permissions getPermissions();


    /**
     * Get document publication informations.
     * 
     * @return publication informations
     */
    PublicationInfos getPublicationInfos();


    /**
     * Get document.
     * 
     * @return document
     */
    EcmDocument getDocument();


    /**
     * Get document type.
     * 
     * @return document type
     */
    DocumentType getDocumentType();


    /**
     * Get document state.
     * 
     * @return document state
     */
    DocumentState getDocumentState();


    /**
     * Get scope.
     * 
     * @return scope
     */
    String getScope();


    /**
     * Force the reloading of the document context.
     */
    void reload();

}
