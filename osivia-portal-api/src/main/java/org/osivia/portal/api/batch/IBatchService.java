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

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.osivia.portal.api.PortalException;

/**
 * Batch service, used to register and unregister batches
 * @author Loïc Billon
 *
 */
public interface IBatchService {
	
    /** MBean name. */
    static final String MBEAN_NAME = "osivia:service=BatchService";
    
    /**
     * Register a batch
     * @param b the batch
     * @throws ParseException
     * @throws PortalException
     */
	void addBatch(AbstractBatch b) throws ParseException, PortalException;
    
	/**
	 * Unregister the batch
	 * @param b
	 */
    public void removeBatch(AbstractBatch b);

    /**
     * Fire a batch immediatly with specific parameters
     * @param batchId
     * @param parameters
     * @throws PortalException
     */
	void startBatchImmediatly(String batchId, Map<String, Object> parameters)
			throws PortalException;

	/**
	 * Get current instances of a given batch type
	 * 
	 * @param clazz
	 * @return
	 */
	<B extends AbstractBatch> List<B> getBatchInstances(Class<B> clazz);
    
}
