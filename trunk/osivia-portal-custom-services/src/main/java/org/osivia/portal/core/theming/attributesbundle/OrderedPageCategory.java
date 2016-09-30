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
package org.osivia.portal.core.theming.attributesbundle;


/**
 * Orderable category
 *
 * @author Steux jean-sébastien
 */
public class OrderedPageCategory implements Comparable<OrderedPageCategory> {

    
    private int order;
    private String code;
    private String label;
    
    public String getLabel() {
        return this.label;
    }

    public String getCode() {
        return this.code;
    }

    public int getOrder() {
        return this.order;
    }

    public OrderedPageCategory(int order, String code, String label) {
        super();
        this.order = order;
        this.code = code;
        this.label = label;
    }

    public int compareTo(OrderedPageCategory page) {
        return this.getOrder() - page.getOrder();
    }

}
