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

import org.osivia.portal.api.cms.Permissions;


/**
 * Default class for permissions on a document
 * @author Loïc Billon
 *
 */
public class BasicPermissions implements Permissions{

	/** document is editable by user */
	private boolean editableByUser = false;
	
	/** document is deletable by user */
	private boolean deletableByUser = false;
	
	/** document is readable by anybody */
	private boolean anonymouslyReadable = false;
	
	/** document is manageable (all rights) by user */
	private boolean manageableByUser = false;


	/**
	 * @return the editableByUser
	 */
	public boolean isEditableByUser() {
		return editableByUser;
	}

	/**
	 * @param editableByUser the editableByUser to set
	 */
	public void setEditableByUser(boolean editableByUser) {
		this.editableByUser = editableByUser;
	}

	/**
	 * @return the deletableByUser
	 */
	public boolean isDeletableByUser() {
		return deletableByUser;
	}

	/**
	 * @param deletableByUser the deletableByUser to set
	 */
	public void setDeletableByUser(boolean deletableByUser) {
		this.deletableByUser = deletableByUser;
	}

	/**
	 * @return the anonymouslyReadable
	 */
	public boolean isAnonymouslyReadable() {
		return anonymouslyReadable;
	}

	/**
	 * @param anonymouslyReadable the anonymouslyReadable to set
	 */
	public void setAnonymouslyReadable(boolean anonymouslyReadable) {
		this.anonymouslyReadable = anonymouslyReadable;
	}

	/**
	 * @return the manageableByUser
	 */
	public boolean isManageableByUser() {
		return manageableByUser;
	}

	/**
	 * @param manageableByUser the manageableByUser to set
	 */
	public void setManageableByUser(boolean manageableByUser) {
		this.manageableByUser = manageableByUser;
	}


}
