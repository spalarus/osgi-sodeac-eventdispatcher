/*******************************************************************************
 * Copyright (c) 2017, 2019 Sebastian Palarus
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Palarus - initial API and implementation
 *******************************************************************************/
package org.sodeac.eventdispatcher.itest.components.metrics;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.sodeac.eventdispatcher.api.IQueueController;
import org.sodeac.eventdispatcher.api.EventDispatcherConstants;
import org.sodeac.eventdispatcher.api.IEventDispatcher;
import org.sodeac.eventdispatcher.api.ITaskControl;
import org.sodeac.eventdispatcher.api.IOnTaskDone;
import org.sodeac.eventdispatcher.api.IQueue;
import org.sodeac.eventdispatcher.api.IOnQueuedEvent;
import org.sodeac.eventdispatcher.api.IQueueTask;
import org.sodeac.eventdispatcher.api.IQueueTaskContext;
import org.sodeac.eventdispatcher.api.IQueuedEvent;

@Component
(
	immediate=true,
	service={IQueueController.class,EventHandler.class},
	property=
	{
		EventDispatcherConstants.PROPERTY_QUEUE_ID+"="+JobMetricTestController.QUEUE_ID,
		EventConstants.EVENT_TOPIC+"=" + JobMetricTestController.RUN_EVENT
	}
)
public class JobMetricTestController implements IQueueController,IOnQueuedEvent, EventHandler, IOnTaskDone
{
	public static final String EVENT_PROPERTY_LATCH 		= "LATCH"		;
	public static final String EVENT_PROPERTY_REPEAT 		= "REPEAT"		;
	public static final String EVENT_PROPERTY_SLEEP_TIME 	= "SLEEPTIME"	;
	public static final String EVENT_PROPERTY_WORK_TIME 	= "WORK"		;
	public static final String EVENT_PROPERTY_JOB_ID 		= "JOBID"		;
	
	public static final String QUEUE_ID 					= "jobmetrics"	;
	public static final String RUN_EVENT 					= "org/sodeac/eventdispatcher/itest/metrics/jobmetrics/run";
	
	@Reference(cardinality=ReferenceCardinality.OPTIONAL,policy=ReferencePolicy.DYNAMIC)
	protected volatile IEventDispatcher dispatcher;
	
	protected CountDownLatch latch = null;
	protected int workCount = 0;
	
	// EventHandler
	
	@Override
	public void handleEvent(Event event)
	{
		dispatcher.queueEvent(JobMetricTestController.QUEUE_ID, event);
	}
	
	// IOnEventScheduled
	
	@Override
	public void onQueuedEvent(IQueuedEvent event)
	{
		this.latch = (CountDownLatch)event.getNativeEventProperties().get(EVENT_PROPERTY_LATCH);
		event.getQueue().scheduleTask
		(
			(String)event.getNativeEventProperties().get(EVENT_PROPERTY_JOB_ID),
			new Job
			(
				(int)event.getNativeEventProperties().get(EVENT_PROPERTY_REPEAT),
				(int)event.getNativeEventProperties().get(EVENT_PROPERTY_SLEEP_TIME),
				(int)event.getNativeEventProperties().get(EVENT_PROPERTY_WORK_TIME)
			)
		);
	}
	
	// IOnJobDone
	
	@Override
	public void onTaskDone(IQueue queue, IQueueTask job)
	{
		this.latch.countDown();
		
		try
		{
			Thread.sleep(3000);
		}
		catch (Exception e) {}
	}
	
	// Job
	
	protected class Job implements IQueueTask
	{
		private int repeat;
		private int sleeptime;
		private int worktime;
		
		private int jobCounter = 0;
		
		public Job(int repeat, int sleeptime, int worktime)
		{
			super();
			this.repeat = repeat;
			this.sleeptime = sleeptime;
			this.worktime = worktime;
		}
		
		@Override
		public void run(IQueueTaskContext taskContext)
		{
			IQueue queue = taskContext.getQueue();
			ITaskControl taskControl = taskContext.getTaskControl();
			
			jobCounter++;
			
			if(jobCounter < repeat)
			{
				taskControl.setExecutionTimestamp(System.currentTimeMillis() + ((long)sleeptime) + ((long)worktime),true);
			}
			
			try
			{
				Thread.sleep(worktime);
			}
			catch (Exception e) {}
			
			queue.sendEvent("test/topic/send", new HashMap<>());
			queue.postEvent("test/topic/post", new HashMap<>());
			queue.signal("");
			
		}
		
	}

}