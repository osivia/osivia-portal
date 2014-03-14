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
package org.osivia.portal.core.page;

import java.util.List;
import java.util.Map;

import org.osivia.portal.api.page.PageParametersEncoder;

public class EncodedParams {
	 Map<String, String[]> publicNavigationalState;

	public Map<String, String[]> getPublicNavigationalState() {
		return publicNavigationalState;
	}

	public void setPublicNavigationalState(Map<String, String[]> publicNavigationalState) {
		this.publicNavigationalState = publicNavigationalState;
	}

	public EncodedParams(Map<String, String[]> publicNavigationalState) {
		super();
		this.publicNavigationalState = publicNavigationalState;
	}
	
	public  List<String> decode( String urlParams, String name)	{
		
		String[] value = publicNavigationalState.get(urlParams);
		if( value == null || value.length != 1)
			return null;
		
		 Map<String,List<String>> params = PageParametersEncoder.decodeProperties(value[0]);
		 if( params == null)
			 return null;
		 
		 return params.get(name);
		
	}

}
