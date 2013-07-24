package org.osivia.portal.administration.util;

import java.util.Comparator;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.dynamic.DynamicPageBean;

/**
 * Pages comparator.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see Page
 */
public class PagesComparator implements Comparator<Page> {

    /** Page order declared property name. */
    private static final String PAGE_ORDER_PROPERTY = "order";


    /**
     * {@inheritDoc}
     */
    public int compare(Page page1, Page page2) {
        String orderPage1 = page1.getDeclaredProperty(PAGE_ORDER_PROPERTY);
        String orderPage2 = page2.getDeclaredProperty(PAGE_ORDER_PROPERTY);

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
            PortalObjectId idPage1 = page1.getId();
            PortalObjectId idPage2 = page2.getId();
            return idPage1.compareTo(idPage2);
        } else {
            return order1 - order2;
        }
    }

}
