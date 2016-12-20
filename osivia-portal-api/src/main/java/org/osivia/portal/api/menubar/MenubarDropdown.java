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
 */
package org.osivia.portal.api.menubar;


/**
 * Menubar dropdown menu.
 *
 * @author Cédric Krommenhoek
 * @see MenubarObject
 * @see MenubarContainer
 */
public class MenubarDropdown extends MenubarObject implements MenubarContainer {

    /** Menubar drafts dropdown menu identifier. */
    public static final String DRAFTS_DROPDOWN_MENU_ID = "DRAFTS";
    /** Menubar CMS edition dropdown menu identifier. */
    public static final String CMS_EDITION_DROPDOWN_MENU_ID = "CMS_EDITION";
    /** Menubar share dropdown menu identifier. */
    public static final String SHARE_DROPDOWN_MENU_ID = "SHARE";
    /** Menubar configuration dropdown menu identifier. */
    public static final String CONFIGURATION_DROPDOWN_MENU_ID = "CONFIGURATION";
    /** Menubar other options dropdown menu identifier. */
    public static final String OTHER_OPTIONS_DROPDOWN_MENU_ID = "OTHER_OPTIONS";


    /** Menubar dropdown menu reducible indicator. Default value: true. */
    private boolean reducible;


    /** Menubar dropdown menu parent group. */
    private final MenubarGroup group;


    /**
     * Constructor.
     *
     * @param id menubar dropdown identifier
     * @param title menubar dropdown title
     * @param glyphicon menubar dropdown glyphicon
     * @param group menubar dropdown group
     * @param order menubar dropdown order
     * @param disabled menubar dropdown disabled indicator
     * @param reducible menubar dropdown reducible indicator
     */
    public MenubarDropdown(String id, String title, String glyphicon, MenubarGroup group, int order, boolean disabled, boolean reducible) {
        super(id, title, glyphicon, order, disabled);
        this.reducible = reducible;
        this.group = group;
    }

    /**
     * Constructor.
     *
     * @param id menubar dropdown identifier
     * @param title menubar dropdown title
     * @param glyphicon menubar dropdown glyphicon
     * @param group menubar dropdown group
     * @param order menubar dropdown order
     */
    public MenubarDropdown(String id, String title, String glyphicon, MenubarGroup group, int order) {
        this(id, title, glyphicon, group, order, false, true);
    }


    /**
     * {@inheritDoc}
     */
    public MenubarGroup getGroup() {
        return this.group;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MenubarDropdown [id=");
        builder.append(this.getId());
        builder.append("]");
        return builder.toString();
    }


    /**
     * Getter for reducible.
     *
     * @return the reducible
     */
    public boolean isReducible() {
        return this.reducible;
    }

    /**
     * Setter for reducible.
     *
     * @param reducible the reducible to set
     */
    public void setReducible(boolean reducible) {
        this.reducible = reducible;
    }

}
