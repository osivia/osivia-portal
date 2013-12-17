/**
 * 
 */
package org.osivia.portal.core.assistantpage;

import java.util.Comparator;
import java.util.Locale;

import org.jboss.portal.core.model.instance.InstanceDefinition;


/**
 * InstanceDefinition objects comparator.
 * 
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see InstanceDefinition
 */
public class InstanceComparator implements Comparator<InstanceDefinition> {

    /** Locale used to comparate display names. */
    private Locale locale;

    /**
     * Constructor
     * 
     * @param locale locale used to comparate display names
     */
    public InstanceComparator(Locale locale) {
        this.locale = locale;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(InstanceDefinition o1, InstanceDefinition o2) {
        String n1 = o1.getDisplayName().getString(locale, true);
        if (n1 == null) {
            n1 = o1.getId();
        }
        String n2 = o2.getDisplayName().getString(locale, true);
        if (n2 == null) {
            n2 = o2.getId();
        }

        return n1.toUpperCase().compareTo(n2.toUpperCase());
    }

}
