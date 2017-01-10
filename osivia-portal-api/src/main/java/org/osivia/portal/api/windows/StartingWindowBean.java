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
 * The Class StartingWindowBean (allows to create a new window on processAction)
 */

public class StartingWindowBean {


    final String name;
    final String portletInstance;
    final Map<String, String> properties;
	
	public StartingWindowBean(String name, String portletInstance, Map<String, String> properties) {
		super();
		
		this.name = name;
		this.portletInstance = portletInstance;
		this.properties = properties;
	}

    
    public String getName() {
        return name;
    }

    
    public String getPortletInstance() {
        return portletInstance;
    }

    
    public Map<String, String> getProperties() {
        return properties;
    }

		



}
