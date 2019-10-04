/*
 * (C) Copyright 2017 OSIVIA (http://www.osivia.com) 
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
package org.osivia.portal.core.batch;

/**
 * MBean used to check if we are on the master node of the cluster
 * @author Loïc Billon
 *
 */
public interface HABatchDeployer {

	public static final String MBEAN_NAME = "osivia:service=HABatchDeployer";
	
	/**
	 * Method fired if the node is becoming master
	 */
	void startSingleton();
	
	/**
	 * Method fired if the node is becoming slave
	 */
	void stopSingleton();
	
	/**
	 * 
	 * @return master status
	 */
	boolean isMaster();
}
