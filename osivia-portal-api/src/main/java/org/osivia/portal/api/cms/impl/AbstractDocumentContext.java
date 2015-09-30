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
package org.osivia.portal.api.cms.impl;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.osivia.portal.api.cms.DocumentContext;
import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.cms.Permissions;
import org.osivia.portal.api.cms.PublicationInfos;

/**
 * 
 * Abstract implementation of a document context with basic informations (publication, read-write permissions, etc.)
 * @author Loïc Billon
 *
 * @param <D> a type which represents an implementation of ECM Document
 */
public abstract class AbstractDocumentContext<D extends EcmDocument> implements DocumentContext<D> {

	/** document */
	protected D document;
	
    /** Hide metadata indicator window property name. */
	// TODO à déplacer
    public static final String HIDE_METADATA = "osivia.cms.hideMetaDatas";
	
    /** perm collection */
	private Map<Class<? extends Permissions>,Permissions> permissionsMap = new Hashtable<Class<? extends Permissions>, Permissions>();
	
	/** publication info collection */
	private Map<Class<? extends PublicationInfos>,PublicationInfos> publicationInfosMap = new Hashtable<Class<? extends PublicationInfos>, PublicationInfos>();
	
	/** document type */
	private DocumentType type;
	
	
	/**
	 * 
	 */
	public AbstractDocumentContext() {
		permissionsMap.put(BasicPermissions.class, new BasicPermissions());
		
		publicationInfosMap.put(BasicPublicationInfos.class, new BasicPublicationInfos());
	}

	
	public Collection<Permissions> getAllPermissions() {
		return permissionsMap.values();
	}



	public void addPermissions(Permissions permissions) {
		permissionsMap.put(permissions.getClass(), permissions);

	}

	

	/* (non-Javadoc)
	 * @see org.osivia.portal.api.cms.Document#getPermissions(java.lang.Class)
	 */
	public <P extends Permissions> P getPermissions(Class<P> permType) {

		return (P) permissionsMap.get(permType);
	}


	/* (non-Javadoc)
	 * @see org.osivia.portal.api.cms.Document#getAllNavigationInfos()
	 */
	public Collection<PublicationInfos> getAllPublicationInfos() {
		return publicationInfosMap.values();
	}


	/* (non-Javadoc)
	 * @see org.osivia.portal.api.cms.Document#getNavigationInfos(java.lang.Class)
	 */
	public <P extends PublicationInfos> P getPublicationInfos(Class<P> navType) {
		return navType.cast(publicationInfosMap.get(navType));
	}


	/* (non-Javadoc)
	 * @see org.osivia.portal.api.cms.Document#addNavigationInfos(org.osivia.portal.api.cms.NavigationInfos)
	 */
	public void addPublicationInfos(PublicationInfos publicationInfos) {

		publicationInfosMap.put(publicationInfos.getClass(), publicationInfos);
		
	}


	public DocumentType getType() {
		return type;
	}


	public void setDocumentType(DocumentType type) {
		this.type = type;
		
	}

}
