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
package org.osivia.portal.api.theming;

/**
 * Rendered region java bean.
 * 
 * @author Cédric Krommenhoek
 * @see AbstractRegionBean
 */
public class RenderedRegionBean extends AbstractRegionBean {

    /** Rendered region path. */
    private final String path;
    /** Default region indicator (default value is false). */
    private boolean defaultRegion;
    /** Customizable region indicator (default value is true). */
    private boolean customizable;


    /**
     * Constructor.
     * 
     * @param name rendered region name
     * @param path rendered region path
     */
    public RenderedRegionBean(String name, String path) {
        super(name);
        this.path = path;
        this.defaultRegion = false;
        this.customizable = true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCustomizable() {
        return this.customizable;
    }


    /**
     * Getter for path.
     * 
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Getter for defaultRegion.
     * 
     * @return the defaultRegion
     */
    public boolean isDefaultRegion() {
        return this.defaultRegion;
    }

    /**
     * Setter for defaultRegion.
     * 
     * @param defaultRegion the defaultRegion to set
     */
    public void setDefaultRegion(boolean defaultRegion) {
        this.defaultRegion = defaultRegion;
    }

    /**
     * Setter for customizable.
     * 
     * @param customizable the customizable to set
     */
    public void setCustomizable(boolean customizable) {
        this.customizable = customizable;
    }

}
