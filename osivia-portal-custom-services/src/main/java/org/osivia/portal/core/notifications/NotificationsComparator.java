package org.osivia.portal.core.notifications;

import java.util.Comparator;

import org.osivia.portal.api.notifications.Notifications;

/**
 * Notifications comparator by priority and then by age.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see Notifications
 */
public class NotificationsComparator implements Comparator<Notifications> {

    /** Singleton instance. */
    private static NotificationsComparator instance;


    /**
     * Default private constructor.
     */
    private NotificationsComparator() {
        super();
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static final NotificationsComparator getInstance() {
        if (instance == null) {
            instance = new NotificationsComparator();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public int compare(Notifications o1, Notifications o2) {
        // Priority comparison
        int p1 = o1.getType().getPriority();
        int p2 = o2.getType().getPriority();
        if (p1 != p2) {
            // A higher priority gives a lower order
            return p2 - p1;
        } else {
            // Age comparison
            long a1 = o1.getCreationTime();
            long a2 = o2.getCreationTime();
            // Older notifications gives a higher order
            return Long.signum(a2 - a1);
        }
    }

}
