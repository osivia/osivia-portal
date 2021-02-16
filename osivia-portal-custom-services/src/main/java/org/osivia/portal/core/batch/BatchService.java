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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.batch.AbstractBatch;
import org.osivia.portal.api.batch.IBatchService;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Impl of the batch service
 * @author Loïc Billon
 *
 */
public class BatchService implements IBatchService {


	private final static Log logger = LogFactory.getLog("batch");
	
	private Scheduler sched;
	
	public BatchService() {
		
		// Initialize Quartz
		SchedulerFactory schedFact = new StdSchedulerFactory();
		
		try {
			sched = schedFact.getScheduler();
			
			sched.start();
			
			logger.info("BatchService started !");
			
		} catch (SchedulerException e) {
			logger.error("Unable to start BatchService !",e);
		}
		
	}

	
	@Override
	public void addBatch(AbstractBatch b) throws ParseException, PortalException {
		
		logger.info("add batch "+b.getBatchId());
				
			
		JobDetail detail = new JobDetail(b.getBatchId(), Scheduler.DEFAULT_GROUP, BatchJob.class);

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("instance", b);
		detail.setJobDataMap(jobDataMap);
		
		Trigger trigger;
		if(b.getJobScheduling() != null) {
			trigger = new CronTrigger("trigger_" + b.getBatchId(), Scheduler.DEFAULT_GROUP, b.getJobScheduling());
		}
		else {
			
			Date targetTime = new Date(); //now
			targetTime = DateUtils.addMinutes(targetTime, 1); //add minute
			trigger = new SimpleTrigger("trigger_" + b.getBatchId(), Scheduler.DEFAULT_GROUP, targetTime);
		}
		
		try {
			sched.scheduleJob(detail, trigger);
		} catch (SchedulerException e) {
			throw new PortalException(e);
		}
		
		
	}
	
	@Override
	public void startBatchImmediatly(String batchId, Map<String, Object> parameters) throws PortalException{
		
		JobDataMap data  = new JobDataMap(parameters);
		
		try {
			sched.triggerJob(batchId, Scheduler.DEFAULT_GROUP, data);
		} catch (SchedulerException e) {
			throw new PortalException(e);
		}
	}
	
	public void removeBatch(AbstractBatch b) {
		

		logger.info("remove batch "+b.getBatchId());
		
		try {
			sched.deleteJob(b.getBatchId(), Scheduler.DEFAULT_GROUP);
		} catch (SchedulerException e) {
			logger.error("Unable to remove batch.",e);
		}
		
	}

	public <B extends AbstractBatch> List<B> getBatchInstances(Class<B> clazz) {
		
		List<B> instances = new ArrayList<B>();
		
		try {
						
			List jobs = sched.getCurrentlyExecutingJobs();
			
			String[] jobGroupNames = sched.getJobGroupNames();
			for(String groupName : jobGroupNames) {
				String[] jobNames = sched.getJobNames(groupName);
				for(String jobname : jobNames) {
					
					JobDetail jobDetail = sched.getJobDetail(jobname, groupName);
					
					Object object = jobDetail.getJobDataMap().get("instance");
					
					if(clazz.isInstance(object)) {
						instances.add((B)object);
					}
				}
			}

		} catch (SchedulerException e) {
			logger.error("Unable to get batch instances for "+clazz.getName(),e);
			
			return null;
		}
		
		return instances;
		
	}
}
