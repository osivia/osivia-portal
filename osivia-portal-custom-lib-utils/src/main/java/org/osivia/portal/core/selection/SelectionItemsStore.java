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
package org.osivia.portal.core.selection;

import java.util.Map;
import java.util.Set;

import org.osivia.portal.api.selection.SelectionItem;

/**
 * Selection item bean.
 * 
 * @author JS Steux
 */
public class SelectionItemsStore {

    /** update timestamps. */
    private Long updateTimestamp;
    

	/** list of items  */
    private  Set<SelectionItem> items;

    public Long getUpdateTimestamp() {
		return updateTimestamp;
	}

	public Set<SelectionItem> getItems() {
		return items;
	}


    /**
     * Default contructor.
     */
    public SelectionItemsStore(  Long updateTimestamp, Set<SelectionItem> items) {
        super();
        this.items = items;
        this.updateTimestamp = updateTimestamp;
    }


    

}
