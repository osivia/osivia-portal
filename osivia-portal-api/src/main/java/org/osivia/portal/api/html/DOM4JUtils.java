package org.osivia.portal.api.html;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;

/**
 * Utility class with null-safe methods for DOM4J usage.
 *
 * @author Cédric Krommenhoek
 * @see Element
 * @see QName
 */
public final class DOM4JUtils {

    /**
     * Private constructor : prevent instantiation.
     */
    private DOM4JUtils() {
        throw new AssertionError();
    }


    /**
     * Generate DOM4J element.
     *
     * @param name element name
     * @param htmlClass HTML class, may be null
     * @param text element inner text, may be null
     * @param glyphicon glyphicon name, may be null
     * @param role accessibility role, may be null
     * @return DOM4J element, or null if element name is blank
     */
    public static Element generateElement(String name, String htmlClass, String text, String glyphicon, AccessibilityRoles role) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        Element element = new DOMElement(QName.get(name));
        addAttribute(element, HTMLConstants.CLASS, htmlClass);
        addGlyphiconText(element, glyphicon, text);

        if (role != null) {
            addAttribute(element, HTMLConstants.ROLE, role.getValue());
        }

        return element;
    }


    /**
     * Generate DOM4J element.
     *
     * @param name element name
     * @param htmlClass HTML class, may be null
     * @param text element inner text, may be null
     * @return DOM4J element, or null if element name is blank
     */
    public static Element generateElement(String name, String htmlClass, String text) {
        return generateElement(name, htmlClass, text, null, null);
    }


    /**
     * Generate HTML "div" DOM4J element.
     *
     * @param htmlClass HTML class, may be null
     * @param role accessibility role, may be null
     * @return HTML "div" DOM4J element
     */
    public static Element generateDivElement(String htmlClass, AccessibilityRoles role) {
        return generateElement(HTMLConstants.DIV, htmlClass, StringUtils.EMPTY, null, role);
    }


    /**
     * Generate HTML "div" DOM4J element.
     *
     * @param htmlClass HTML class, may be null
     * @return HTML "div" DOM4J element
     */
    public static Element generateDivElement(String htmlClass) {
        return generateDivElement(htmlClass, null);
    }


    /**
     * Generate HTML "a" link DOM4J element.
     *
     * @param href link URL
     * @param target link target, may be null
     * @param onclick link onclick action, may be null
     * @param htmlClass HTML class, may be null
     * @param text link inner text
     * @param glyphicon glyphicon name, may be null
     * @param role accessibility role, may be null
     * @return HTML "a" link DOM4J element
     */
    public static Element generateLinkElement(String href, String target, String onclick, String htmlClass, String text, String glyphicon,
            AccessibilityRoles role) {
        Element link = generateElement(HTMLConstants.A, htmlClass, text, glyphicon, role);
        addAttribute(link, HTMLConstants.TARGET, target);
        addAttribute(link, HTMLConstants.ONCLICK, onclick);

        if (href == null) {
            addAttribute(link, HTMLConstants.HREF, HTMLConstants.A_HREF_DEFAULT);
        } else {
            addAttribute(link, HTMLConstants.HREF, href);
        }

        return link;
    }


    /**
     * Generate HTML "a" link DOM4J element.
     *
     * @param href link URL
     * @param target link target, may be null
     * @param onclick link onclick action, may be null
     * @param htmlClass HTML class, may be null
     * @param text link inner text
     * @param glyphicon glyphicon name, may be null
     * @return HTML "a" link DOM4J element
     */
    public static Element generateLinkElement(String href, String target, String onclick, String htmlClass, String text, String glyphicon) {
        return generateLinkElement(href, target, onclick, htmlClass, text, glyphicon, null);
    }


    /**
     * Generate HTML "a" link DOM4J element.
     *
     * @param href link URL
     * @param target link target, may be null
     * @param onclick link onclick action, may be null
     * @param htmlClass HTML class, may be null
     * @param text link inner text
     * @return HTML "a" link DOM4J element
     */
    public static Element generateLinkElement(String href, String target, String onclick, String htmlClass, String text) {
        return generateLinkElement(href, target, onclick, htmlClass, text, null, null);
    }


    /**
     * Add attribute to an existing DOM4J element.
     *
     * @param element DOM4J element
     * @param name attribute name
     * @param value attribute value
     */
    public static void addAttribute(Element element, String name, String value) {
        if ((element == null) || StringUtils.isBlank(name)) {
            return;
        }

        if (value != null) {
            element.addAttribute(QName.get(name), value);
        }
    }


    /**
     * Add text to an existing DOM4J element.
     *
     * @param element DOM4J element
     * @param text element inner text
     */
    public static void addText(Element element, String text) {
        if (element == null) {
            return;
        }

        if (text != null) {
            element.addText(text);
        }
    }


    /**
     * Add glyphicon and text to an existing DOM4J element.
     *
     * @param element DOM4J element
     * @param glyphicon glyphicon name, may be null
     * @param text element inner text
     * @param textHTMLClass text HTML class, may be null
     */
    public static void addGlyphiconText(Element element, String glyphicon, String text) {
        if (element == null) {
            return;
        }

        if (StringUtils.isNotBlank(glyphicon)) {
            Element glyph = generateElement(HTMLConstants.I, "glyphicons " + glyphicon, StringUtils.EMPTY);
            element.add(glyph);

            if (text != null) {
                String htmlClass = null;
                String elementHTMLClass = element.attributeValue(QName.get(HTMLConstants.CLASS));
                if ((elementHTMLClass != null) && Arrays.asList(StringUtils.split(elementHTMLClass)).contains("btn")) {
                    // Button text with glyphicon is hidden for extra-small screens
                    htmlClass = "hidden-xs";
                }

                Element textSpan = generateElement(HTMLConstants.SPAN, htmlClass, text);
                element.add(textSpan);
            }
        } else {
            addText(element, text);
        }
    }

}
