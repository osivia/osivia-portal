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
package org.osivia.portal.api.windows;

import java.util.Map;

/**
 * The Interface PortalWindow.
 */
public interface PortalWindow {
	 
 	/**
 	 * Gets the properties.
 	 *
 	 * @return the properties
 	 */
 	public Map<String, String> getProperties();
     
     /**
      * Gets the property.
      *
      * @param name the name
      * @return the property
      */
     public String getProperty(String name);
	 
 	/**
 	 * Sets the property.
 	 *
 	 * @param name the name
 	 * @param value the value
 	 */
 	public void setProperty(String name, String value);
	 
 	/**
 	 * Gets the page property.
 	 *
 	 * @param name the name
 	 * @return the page property
 	 */
 	public String getPageProperty(String name);
}
