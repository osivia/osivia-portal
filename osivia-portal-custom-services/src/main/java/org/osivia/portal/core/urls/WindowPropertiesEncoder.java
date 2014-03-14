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
package org.osivia.portal.core.urls;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WindowPropertiesEncoder {

	public static String encodeProperties(Map<String, String> props) {

		try {
			String url = "";

			for (String name : props.keySet()) {
				if (props.get(name) != null) {
					if (url.length() > 0)
						url += "&&";
					url += encodeValue(name) + "==" + encodeValue(props.get(name));
				}
			}

			return URLEncoder.encode(url, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, String> decodeProperties(String urlParams) {
		try {

			Map<String, String> params = new HashMap<String, String>();

			if (urlParams == null || urlParams.length() == 0)
				return params;

			String decodedParam = URLDecoder.decode(urlParams, "UTF-8");

			String[] tabParams = decodedParam.split("&&");

			for (int i = 0; i < tabParams.length; i++) {
				String[] valParams = tabParams[i].split("==");
				
				if (valParams.length != 1 && valParams.length != 2)
					throw new IllegalArgumentException("Bad parameter format");
			
				String value = "";
				
				if( valParams.length == 2)
					value = valParams[1];

				params.put(valParams[0], value);
			}

			return params;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static String encodeValue(String origValue) {

		if (origValue.contains("=="))
			throw new RuntimeException("Bad parameter format");
		
		if (origValue.contains("&&"))
			throw new RuntimeException("Bad parameter format");

		return origValue;

	}

}
