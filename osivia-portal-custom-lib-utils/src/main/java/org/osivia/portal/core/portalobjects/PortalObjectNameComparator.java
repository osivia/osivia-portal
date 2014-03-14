/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.portalobjects;

import java.util.Comparator;
import java.util.Locale;

import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;

/**
 * Portal object name comparator.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see PortalObject
 */
public class PortalObjectNameComparator implements Comparator<PortalObject> {

    /** Locales. */
    private Locale[] locales;


    /**
     * Default constructor.
     */
    public PortalObjectNameComparator() {
        super();
    }

    /**
     * Constructor using fields.
     *
     * @param locales locales
     */
    public PortalObjectNameComparator(Locale[] locales) {
        super();
        this.locales = locales;
    }

    /**
     * Constructor with a single locale.
     *
     * @param locale single locale
     */
    public PortalObjectNameComparator(Locale locale) {
        super();
        this.locales = new Locale[]{locale};
    }


    /**
     * {@inheritDoc}
     */
    public int compare(PortalObject po1, PortalObject po2) {
        String name1 = PortalObjectUtils.getDisplayName(po1, this.locales);
        String name2 = PortalObjectUtils.getDisplayName(po2, this.locales);

        int result = name1.compareToIgnoreCase(name2);

        if (result == 0) {
            PortalObjectId id1 = po1.getId();
            PortalObjectId id2 = po2.getId();

            result = id1.compareTo(id2);
        }

        return result;
    }

}
