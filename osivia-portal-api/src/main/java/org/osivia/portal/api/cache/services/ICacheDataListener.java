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
package org.osivia.portal.api.cache.services;

import java.io.Serializable;


/**
 * The listener interface for receiving ICacheData events.
 * The class that is interested in processing a ICacheData
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addICacheDataListener<code> method. When
 * the ICacheData event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ICacheDataEvent
 */
public interface ICacheDataListener extends Serializable{

	/**
	 * Removes the.
	 */
	public void remove();
	
	
}
