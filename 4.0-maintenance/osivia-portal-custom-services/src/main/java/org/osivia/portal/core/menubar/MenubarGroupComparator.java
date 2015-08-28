package org.osivia.portal.core.menubar;

import java.util.Comparator;

import org.osivia.portal.api.menubar.MenubarGroup;

/**
 * Menubar group comparator.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see MenubarGroup
 */
public class MenubarGroupComparator implements Comparator<MenubarGroup> {

    /**
     * Constructor.
     */
    public MenubarGroupComparator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public int compare(MenubarGroup o1, MenubarGroup o2) {
        return o1.getOrder() - o2.getOrder();
    }

}
