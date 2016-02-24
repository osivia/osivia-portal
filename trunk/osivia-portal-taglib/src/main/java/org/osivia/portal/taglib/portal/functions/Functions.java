package org.osivia.portal.taglib.portal.functions;

import java.util.Collection;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Tag functions.
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


    /**
     * Join all elements of a collection or array into a string.
     *
     * @param object object
     * @param separator separator
     * @return joined string
     */
    public static String join(Object object, String separator) {
        String result;
        if (object == null) {
            result = StringUtils.EMPTY;
        } else if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            result = StringUtils.join(collection, separator);
        } else if (object instanceof Object[]) {
            Object[] array = (Object[]) object;
            result = StringUtils.join(array, separator);
        } else {
            result = object.toString();
        }
        return result;
    }

}
