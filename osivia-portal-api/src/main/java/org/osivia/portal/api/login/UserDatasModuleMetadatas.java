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
package org.osivia.portal.api.login;


/**
 * The Class UserDatasModuleMetadatas.
 */
public class UserDatasModuleMetadatas {
	
	/** The name. */
	private String name;
	
	/** The order. */
	private int order=0;
	
	/** The module. */
	public IUserDatasModule module;
	
	/**
	 * Gets the module.
	 *
	 * @return the module
	 */
	public IUserDatasModule getModule() {
		return module;
	}
	
	/**
	 * Sets the module.
	 *
	 * @param module the new module
	 */
	public void setModule(IUserDatasModule module) {
		this.module = module;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
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
		return order;
	}
	
	/**
	 * Sets the order.
	 *
	 * @param order the new order
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	

}
