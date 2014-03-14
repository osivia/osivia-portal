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
package org.osivia.portal.administration.ejb;

import java.io.Serializable;


/**
 * Page tree node data.
 *
 * @author Cédric Krommenhoek
 * @see Serializable
 */
public class PageTreeData implements Serializable {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Page identifier. */
    private final String id;
    /** Page name. */
	private final String name;


    /**
     * Constructor using fields.
     *
     * @param id page identifier
     * @param name page name
     */
    public PageTreeData(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }


    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.name;
    }

}
