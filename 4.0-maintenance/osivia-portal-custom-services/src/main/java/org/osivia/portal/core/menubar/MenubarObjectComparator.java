package org.osivia.portal.core.menubar;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.menubar.MenubarObject;

/**
 * Menubar object comparator.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see MenubarObject
 */
public class MenubarObjectComparator implements Comparator<MenubarObject> {

    /**
     * Constructor.
     */
    public MenubarObjectComparator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public int compare(MenubarObject o1, MenubarObject o2) {
        if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        } else if (o1.getOrder() == o2.getOrder()) {
            return StringUtils.trimToEmpty(o1.getId()).compareTo(StringUtils.trimToEmpty(o2.getId()));
        } else {
            return o1.getOrder() - o2.getOrder();
        }
    }

}
