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


/**
 * The Class CustomizationModuleMetadatas.
 * 
 * Describe each module that customizes the platform
 * 
 * @author Jean-Sébastien Steux
 */
public class CustomizationModuleMetadatas {

	/** The name. */
	private String name;
	
	/** The order. */
	private int order=0;
	

	

	
	/** The module. */
	public ICustomizationModule module;

	/**
	 * Gets the module.
	 *
	 * @return the module
	 */
	public ICustomizationModule getModule() {
		return this.module;
	}
	
	/**
	 * Sets the module.
	 *
	 * @param module the new module
	 */
	public void setModule(ICustomizationModule module) {
		this.module = module;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public int getOrder() {
		return this.order;
	}
	
	/**
	 * Sets the order.
	 *
	 * @param order the new order
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/** The customization i ds. */
	public List<String> customizationIDs;

	/**
	 * Gets the customization i ds.
	 *
	 * @return the customization i ds
	 */
	public List<String> getCustomizationIDs() {
		return this.customizationIDs;
	}
	
	/**
	 * Sets the customization i ds.
	 *
	 * @param customizationIDs the new customization i ds
	 */
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
