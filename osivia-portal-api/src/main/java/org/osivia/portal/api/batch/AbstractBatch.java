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
package org.osivia.portal.api.batch;

import java.util.Map;

/**
 * Abstract class used to set common behaviors on a batch
 * @author Loïc Billon
 *
 */
public abstract class AbstractBatch implements Batch {

	@Override
	public String getBatchId() {
		
		return this.getClass().getSimpleName();
	}
	
	@Override
	public boolean isRunningOnMasterOnly() {
		return true;
	}

	@Override
	public abstract String getJobScheduling();

	@Override
	public abstract void execute(Map<String, Object> parameters);

}
