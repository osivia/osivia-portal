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
package org.osivia.portal.core.theming;

/**
 * Region decorator.
 *
 * @author Cédric Krommenhoek
 */
public class RegionDecorator {

    /** Header content. */
    private final String headerContent;
    /** Footer content. */
    private final String footerContent;


    /**
     * Constructor.
     * 
     * @param headerContent header content
     * @param footerContent footer content
     */
    public RegionDecorator(String headerContent, String footerContent) {
        super();
        this.headerContent = headerContent;
        this.footerContent = footerContent;
    }


    /**
     * Getter for headerContent.
     * 
     * @return the headerContent
     */
    public String getHeaderContent() {
        return this.headerContent;
    }

    /**
     * Getter for footerContent.
     * 
     * @return the footerContent
     */
    public String getFooterContent() {
        return this.footerContent;
    }

}
