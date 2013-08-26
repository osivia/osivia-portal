package org.osivia.portal.core.portalobjects;

import java.util.Comparator;

import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.dynamic.DynamicPageBean;

/**
 * Portal object order comparator.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see PortalObject
 */
public class PortalObjectOrderComparator implements Comparator<PortalObject> {

    /** Singleton instance. */
    private static PortalObjectOrderComparator instance;

    /**
     * Private default constructor.
     */
    private PortalObjectOrderComparator() {
        super();
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static PortalObjectOrderComparator getInstance() {
        if (instance == null) {
            instance = new PortalObjectOrderComparator();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public int compare(PortalObject po1, PortalObject po2) {
        String orderPage1 = po1.getDeclaredProperty(InternalConstants.TAB_ORDER_PROPERTY_NAME);
        String orderPage2 = po2.getDeclaredProperty(InternalConstants.TAB_ORDER_PROPERTY_NAME);

        int order1 = 0;
        int order2 = 0;

        try {
            order1 = Integer.parseInt(orderPage1);
        } catch (NumberFormatException e) {
            order1 = DynamicPageBean.DYNAMIC_PAGES_FIRST_ORDER;
        }

        try {
            order2 = Integer.parseInt(orderPage2);
        } catch (NumberFormatException e) {
            order2 = DynamicPageBean.DYNAMIC_PAGES_FIRST_ORDER;
        }

        if (order1 == order2) {
            PortalObjectId idPage1 = po1.getId();
            PortalObjectId idPage2 = po2.getId();
            return idPage1.compareTo(idPage2);
        } else {
            return order1 - order2;
        }
    }

}
