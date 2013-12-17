package org.osivia.portal.api.urls;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Window properties.
 */
public class WindowProperties {

    /**
     * Encode properties.
     *
     * @param props properties map
     * @return properties string value
     */
    public static String encodeProperties(Map<String, String> props) {
        try {
            String url = "";

            for (Entry<String, String> entry : props.entrySet()) {
                if (url.length() > 0) {
                    url += "&";
                }
                url += encodeValue(entry.getKey()) + "=" + encodeValue(entry.getValue());
            }

            return URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Decode properties.
     *
     * @param urlParams properties string value
     * @return properties map
     */
    public static Map<String, String> decodeProperties(String urlParams) {
        try {
            String decodedParam = URLDecoder.decode(urlParams, "UTF-8");

            Map<String, String> params = new HashMap<String, String>();
            String[] tabParams = decodedParam.split("&");
            for (String tabParam : tabParams) {
                String[] valParams = tabParam.split("=");

                if (valParams.length != 2) {
                    throw new IllegalArgumentException("Bad parameter format");
                }

                params.put(valParams[0], valParams[1]);
            }

            return params;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Utility method used to encode single property value.
     *
     * @param origValue original value
     * @return property string value
     */
    private static String encodeValue(String origValue) {

        if (origValue.contains("=")) {
            throw new RuntimeException("Bad parameter format");
        }
        if (origValue.contains("&")) {
            throw new RuntimeException("Bad parameter format");
        }

        return origValue;
    }

}
