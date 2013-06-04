/**
 * 
 */
package org.osivia.portal.core.toolbar;

import java.util.Comparator;

import org.jboss.portal.theme.LayoutInfo;
import org.jboss.portal.theme.PortalLayout;


/**
 * PortalLayout objects comparator.
 * 
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see PortalLayout
 */
public class PortalLayoutComparator implements Comparator<PortalLayout> {

    /**
     * Default constructor.
     */
    public PortalLayoutComparator() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(PortalLayout o1, PortalLayout o2) {
        LayoutInfo info1 = o1.getLayoutInfo();
        String name1 = info1.getName();

        LayoutInfo info2 = o2.getLayoutInfo();
        String name2 = info2.getName();

        return name1.compareTo(name2);
    }

}
