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

import org.osivia.portal.api.PortalException;

/**
 * Batch (facade of a quartz job).
 * @author Loïc Billon
 *
 */
public interface Batch {
	
	/**
	 * Define the cron used to run the batch periodically
	 * @return
	 */
	public String getJobScheduling();

	/**
	 * The main execution method.
	 */
	public abstract void execute(Map<String, Object> parameters) throws PortalException;

	/**
	 * Set true if this job should not run on all nodes of the cluster
	 * @return
	 */
	public boolean isRunningOnMasterOnly();
}
