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
 * Impl of HABatchDeployer
 * @author Loïc Billon
 *
 */
public class HABatchDeployerImpl implements HABatchDeployer {

	
	private boolean master = false;
	
	@Override
	public void startSingleton() {
		master = true;
		
	}

	@Override
	public void stopSingleton() {
		master = false;
		
	}

	@Override
	public boolean isMaster() {

		return master;
	}

}
