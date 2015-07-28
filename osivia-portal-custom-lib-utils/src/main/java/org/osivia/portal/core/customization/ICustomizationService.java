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
package org.osivia.portal.core.customization;

import java.util.Locale;

import org.osivia.portal.api.customization.CustomizationContext;

public interface ICustomizationService {
	
    /** MBean name. */
    String MBEAN_NAME = "osivia:service=CustomizationService";
    
	public void customize ( String customizationID, CustomizationContext ctx);
	
	 /**
     * Gets the last customization timestamp.
     *
     * @param customizationID the customization id
     * @return the last deployment timestamp
     */
    public long getFirstCustomizationTimestamp(String customizationID, Locale locale) ;
	

}
