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
package org.osivia.portal.api.customization;

import java.util.Map;


/**
 * The Class CustomizationContext.
 * 
 * Identifies inputs ant outputs of a customization point
 * 
 * @author Jean-Sébastien Steux
 */
public class CustomizationContext {
	
	/**
	 * Instantiates a new customization context.
	 *
	 * @param attributes the attributes
	 */
	public CustomizationContext(Map<String, Object> attributes) {
		super();
		this.attributes = attributes;
	}

	/** The attributes. */
	private Map<String, Object> attributes;

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	

}
