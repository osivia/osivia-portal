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

import org.apache.commons.lang.StringUtils;

public class WindowPropertiesEncoder {

    public static String encodeProperties(Map<String, String> props) {

        try {
            String url = "";

            for (String name : props.keySet()) {
                if (props.get(name) != null) {
                    if (url.length() > 0) {
                        url += "&&";
                    }
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

            Map<String, String> params = new HashMap<>();

            if (urlParams == null || urlParams.length() == 0) {
                return params;
            }

            String decodedParam = URLDecoder.decode(urlParams, "UTF-8");

            String[] tabParams = decodedParam.split("&&");

            for (String tabParam : tabParams) {

                String name = tabParam;
                String value = StringUtils.EMPTY;

                int separatorPos = tabParam.indexOf("==");
                // #1840 on conserve tout ce qui suit le premier séparateur, même s'il y en a d'autres car il peut arriver qu'une windowProperty ait été décodée
                // (URLDecoder) plusieurs fois (notamment lors du parsing de la QueryParameterMap
                if (separatorPos != StringUtils.INDEX_NOT_FOUND) {
                    name = tabParam.substring(0, separatorPos);
                    value = tabParam.substring(separatorPos + 2);
                }

                params.put(name, value);
            }

            return params;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static String encodeValue(String origValue) {

        if (origValue.contains("==")) {
            throw new RuntimeException("Bad parameter format");
        }

        if (origValue.contains("&&")) {
            throw new RuntimeException("Bad parameter format");
        }

        return origValue;

    }

}
