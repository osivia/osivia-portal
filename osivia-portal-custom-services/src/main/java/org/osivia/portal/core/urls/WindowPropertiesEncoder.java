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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang.StringUtils;

public class WindowPropertiesEncoder {

	public static String encodeProperties(Map<String, String> properties) {
        StringBuilder url = new StringBuilder();

        for (Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = StringUtils.trimToEmpty(entry.getValue());

            if (url.length() > 0) {
                url.append("&&");
            }

            url.append(encodeValue(key));
            url.append("==");
            url.append(encodeValue(value));
        }

        try {
            return URLEncoder.encode(url.toString(), CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
	}

	public static Map<String, String> decodeProperties(String url) {
        Map<String, String> properties;
        if (StringUtils.isEmpty(url)) {
            properties = new HashMap<>(0);
        } else {
            String[] splittedUrl = StringUtils.splitByWholeSeparator(url, "&&");

            properties = new HashMap<>(splittedUrl.length);

            for (String segment : splittedUrl) {
                String[] splittedSegment = StringUtils.splitByWholeSeparator(segment, "==");

                if ((splittedSegment.length != 1) && (splittedSegment.length != 2)) {
                    throw new IllegalArgumentException("Bad parameter format");
                }

                try {
                    String key = URLDecoder.decode(splittedSegment[0], CharEncoding.UTF_8);

                    String value;
                    if (splittedSegment.length == 2) {
                        value = URLDecoder.decode(splittedSegment[1], CharEncoding.UTF_8);
                    } else {
                        value = StringUtils.EMPTY;
                    }

                    properties.put(key, value);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return properties;
	}


	private static String encodeValue(String origValue) {

		if (origValue.contains("=="))
			throw new RuntimeException("Bad parameter format");
		
		if (origValue.contains("&&"))
			throw new RuntimeException("Bad parameter format");

		return origValue;

	}

}
