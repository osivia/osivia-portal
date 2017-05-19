package org.osivia.portal.core.cms;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.osivia.portal.api.cms.DocumentType;
import org.osivia.portal.api.internationalization.Bundle;

/**
 * CMS item type comparator.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see DocumentType
 */
public class CMSItemTypeComparator implements Comparator<DocumentType> {

    /** Internationalization bundle. */
    private final Bundle bundle;


    /**
     * Contructor.
     *
     * @param bundle internationalization bundle
     */
    public CMSItemTypeComparator(Bundle bundle) {
        super();
        this.bundle = bundle;
    }


    /**
     * {@inheritDoc}
     */
    public int compare(DocumentType o1, DocumentType o2) {
        // Display name #1
        String n1 = null;
        if ((o1 != null) && (o1.getName() != null)) {
            n1 = this.bundle.getString(StringUtils.upperCase(o1.getName()), o1.getCustomizedClassLoader());
        }

        // Display name #2
        String n2 = null;
        if ((o2 != null) && (o2.getName() != null)) {
            n2 = this.bundle.getString(StringUtils.upperCase(o2.getName()), o2.getCustomizedClassLoader());
        }

        // Comparison
        if (n1 == null) {
            return -1;
        } else if (n2 == null) {
            return 1;
        } else {
            return n1.compareToIgnoreCase(n2);
        }
    }

}
