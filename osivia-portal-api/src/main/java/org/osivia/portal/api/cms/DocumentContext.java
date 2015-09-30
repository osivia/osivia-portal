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

import java.util.Collection;

/**
 * This class is a root class context for all informations about a CMS document
 * @author Loïc Billon
 * @param <D> a type which represents an implementation of ECM Document
 *
 */
public interface DocumentContext<D extends EcmDocument> {

	/**
	 * Get all classes (with extentions) about permissions on the current document
	 * @return all permissions
	 */
	Collection<Permissions> getAllPermissions();
	
	/**
	 * Get permissions
	 * @param permType the type of permissions
	 * @return permissions
	 */
	<P extends Permissions>P getPermissions(Class<P> permType);
	
	/**
	 * Add a permisions definition to the collection of permissions 
	 * @param permissions permissions
	 */
	void addPermissions(Permissions permissions);
	
	
	/**
	 * Get all classes (with extentions) about publication informations on the current document
	 * @return all publication informations
	 */
	Collection<PublicationInfos> getAllPublicationInfos();
	
	/**
	 * Get publication informations
	 * @param publiInfos the type of publication informations
	 * @return publication informations
	 */	
	<P extends PublicationInfos>P getPublicationInfos(Class<P> publiInfos);
	
	/**
	 * Add a publication informations definition to the collection of publication informations 
	 * @param publication informations
	 */
	void addPublicationInfos(PublicationInfos publicationInfos);
	
	
	/**
	 * Get the document type
	 * @return the document type
	 */
	DocumentType getType();
	
	/**
	 * Set the document type
	 * @param type the document type
	 */
	void setDocumentType(DocumentType type);
	
	/**
	 * Get the document
	 * @return the document
	 */
	D getDoc();
	
	/**
	 * Set the document
	 * @param document the document
	 */
	void setDoc(D document);
	
}
