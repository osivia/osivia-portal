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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.batch.AbstractBatch;
import org.osivia.portal.api.locator.Locator;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job used to control the execution of the batch
 * 
 * @author Loïc Billon
 *
 */
public class BatchJob implements Job {

	private final static Log logger = LogFactory.getLog("batch");

	private boolean running = Boolean.FALSE;

	
	public BatchJob() {
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		// Get the instance of batch
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		JobDataMap triggerDataMap = context.getTrigger().getJobDataMap();

		Object object = jobDataMap.get("instance");

		if (object instanceof AbstractBatch) {

			AbstractBatch b = (AbstractBatch) object;
			
			// Clustering, check if this batch can run in all nodes or only on master
			if (b.isRunningOnMasterOnly()) {

				HABatchDeployer haBean = Locator.findMBean(HABatchDeployer.class, HABatchDeployer.MBEAN_NAME);

				if (haBean.isMaster()) {

					logger.debug("We are on the master node, run the batch " + context.getJobDetail().getName());
					wrapExecution(b, triggerDataMap);


				} else {
					logger.debug("We are on a slave node, skip the batch " + context.getJobDetail().getName());

				}
			} else {
				wrapExecution(b, triggerDataMap);
			}
		} else {
			throw new JobExecutionException("Job is not an instance of AbstractBatch.");
		}

	}
	
	/**
	 * Mutex used to prevent parallel executions of a same batch 
	 * @param b
	 * @param triggerDataMap
	 */
	private void wrapExecution(AbstractBatch b, JobDataMap triggerDataMap){
		if(running) {
			logger.warn("Batch is currently running and will not be triggered twice. " + b.getBatchId());
		}
		else {
			
			Date startD = new Date();
			Boolean onError = false;

			try {			
				
				running = true;
				b.execute(triggerDataMap);	
				logger.warn(b.getBatchId()+" started");
				
			}
			catch(PortalException e) {
				logger.error(e);
				onError = true;
			}
			finally {
				running = false;
				Date endD = new Date();
				long duration = endD.getTime() - startD.getTime();
				
				logger.warn(b.getBatchId()+" ended (duration : "+duration+"ms), " + (onError? "with errors !" : "")  );
			}
			
		}
	}

}
