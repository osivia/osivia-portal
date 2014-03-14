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

import org.jboss.portal.core.model.portal.Window;

public class InternalWindow  implements PortalWindow	{
	 private Window internalReference;

	 public InternalWindow(Window internalReference) {
		super();
		this.internalReference = internalReference;
	}
	public Map<String, String> getProperties()	{
		return internalReference.getDeclaredProperties();
		 
	 }
    public String getProperty(String name)	{
   	 return internalReference.getDeclaredProperty( name);
   	 
    }
	 public void setProperty(String name, String value)	{
		 internalReference.setDeclaredProperty( name, value);
		 
	 }
	 
     public String getPageProperty(String name)	{
      	 return internalReference.getParent().getProperty( name);
   	 
     }
  
}