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


/**
 * The Class InternalWindow.
 */
public class InternalWindow  implements PortalWindow	{
	 
 	/** The internal reference. */
 	private Window internalReference;

	 /**
 	 * Instantiates a new internal window.
 	 *
 	 * @param internalReference the internal reference
 	 */
 	public InternalWindow(Window internalReference) {
		super();
		this.internalReference = internalReference;
	}
	
	/* (non-Javadoc)
	 * @see org.osivia.portal.api.windows.PortalWindow#getProperties()
	 */
	public Map<String, String> getProperties()	{
		return internalReference.getDeclaredProperties();
		 
	 }
    
    /* (non-Javadoc)
     * @see org.osivia.portal.api.windows.PortalWindow#getProperty(java.lang.String)
     */
    public String getProperty(String name)	{
   	 return internalReference.getDeclaredProperty( name);
   	 
    }
	 
 	/* (non-Javadoc)
 	 * @see org.osivia.portal.api.windows.PortalWindow#setProperty(java.lang.String, java.lang.String)
 	 */
 	public void setProperty(String name, String value)	{
		 internalReference.setDeclaredProperty( name, value);
		 
	 }
	 
     /* (non-Javadoc)
      * @see org.osivia.portal.api.windows.PortalWindow#getPageProperty(java.lang.String)
      */
     public String getPageProperty(String name)	{
      	 return internalReference.getParent().getProperty( name);
   	 
     }
  
}