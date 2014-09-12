package org.osivia.portal.core.tag;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Taglib functions.
 *
 * @author Cédric Krommenhoek
 */
public class Functions {

    /**
     * Escapes the characters in a String using JavaScript String rules.
     *
     * @param str String to escape values in, may be null
     * @return String with escaped values, null if null string input
     */
    public static String escapeJavaScript(String str) {
        return StringEscapeUtils.escapeJavaScript(str);
    }

}
