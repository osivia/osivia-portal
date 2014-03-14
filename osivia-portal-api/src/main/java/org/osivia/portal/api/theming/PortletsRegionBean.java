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
package org.osivia.portal.api.theming;

/**
 * Portlets region java bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractRegionBean
 */
public class PortletsRegionBean extends AbstractRegionBean {

    /** Header path. */
    private final String headerPath;
    /** Footer path. */
    private final String footerPath;


    /**
     * Constructor.
     *
     * @param name portlets region name
     * @param headerPath header path, may be null
     * @param footerPath footer path, may be null
     */
    public PortletsRegionBean(String name, String headerPath, String footerPath) {
        super(name);
        this.headerPath = headerPath;
        this.footerPath = footerPath;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCustomizable() {
        return true;
    }


    /**
     * Getter for headerPath.
     *
     * @return the headerPath
     */
    public String getHeaderPath() {
        return this.headerPath;
    }

    /**
     * Getter for footerPath.
     *
     * @return the footerPath
     */
    public String getFooterPath() {
        return this.footerPath;
    }

}
