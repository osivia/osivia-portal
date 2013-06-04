/**
 * 
 */
package org.osivia.portal.core.toolbar;

import java.util.Comparator;

import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeInfo;


/**
 * PortalTheme objects comparator.
 * 
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see PortalTheme
 */
public class PortalThemeComparator implements Comparator<PortalTheme> {

    /**
     * Default constructor.
     */
    public PortalThemeComparator() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(PortalTheme o1, PortalTheme o2) {
        ThemeInfo info1 = o1.getThemeInfo();
        String name1 = info1.getName();

        ThemeInfo info2 = o2.getThemeInfo();
        String name2 = info2.getName();

        return name1.compareTo(name2);
    }

}
