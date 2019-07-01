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

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
            String decodedParam;
            try {
                decodedParam = URLDecoder.decode(url, CharEncoding.UTF_8);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            String[] tabParams = StringUtils.splitByWholeSeparator(decodedParam, "&&");

            properties = new HashMap<>(tabParams.length);

            for (int i = 0; i < tabParams.length; i++) {
                String[] valParams = StringUtils.splitByWholeSeparator(tabParams[i], "==");

                if (valParams.length != 1 && valParams.length != 2) {
                    throw new IllegalArgumentException("Bad parameter format");
                }

                String value;
                if (valParams.length == 2) {
                    value = valParams[1];
                } else {
                    value = StringUtils.EMPTY;
                }

                properties.put(valParams[0], value);
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
