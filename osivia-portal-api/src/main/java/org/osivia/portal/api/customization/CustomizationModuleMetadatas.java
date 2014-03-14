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
package org.osivia.portal.api.customization;

import java.util.List;

public class CustomizationModuleMetadatas {

	private String name;
	private int order=0;
	public ICustomizationModule module;

	public ICustomizationModule getModule() {
		return this.module;
	}
	public void setModule(ICustomizationModule module) {
		this.module = module;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrder() {
		return this.order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	public List<String> customizationIDs;

	public List<String> getCustomizationIDs() {
		return this.customizationIDs;
	}
	public void setCustomizationIDs(List<String> customizationIDs) {
		this.customizationIDs = customizationIDs;
	}


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CustomizationModuleMetadatas other = (CustomizationModuleMetadatas) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
