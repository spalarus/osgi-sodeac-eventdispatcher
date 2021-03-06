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
package org.sodeac.eventdispatcher.itest.components.compressor;

import java.util.HashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.sodeac.eventdispatcher.api.IQueueController;
import org.sodeac.eventdispatcher.api.EventDispatcherConstants;
import org.sodeac.eventdispatcher.api.IEventDispatcher;
import org.sodeac.eventdispatcher.api.IMetrics;
import org.sodeac.eventdispatcher.api.IOnQueuedEvent;
import org.sodeac.eventdispatcher.api.IQueue;
import org.sodeac.eventdispatcher.api.IQueueTaskContext;
import org.sodeac.eventdispatcher.api.IQueueService;
import org.sodeac.eventdispatcher.api.IQueuedEvent;

@Component
(
	immediate=true,
	service={IQueueController.class,IQueueService.class},
	property=
	{
		EventDispatcherConstants.PROPERTY_QUEUE_ID + "=" + CompressorStatics.QUEUE_ID,
		EventDispatcherConstants.PROPERTY_PERIODIC_REPETITION_INTERVAL+"="+ CompressorStatics.SERVICE_REPETITION_INTERVAL,
		EventDispatcherConstants.PROPERTY_SERVICE_ID+"=" + CompressorStatics.COMPRESSOR_SERVICE_ID,
		EventDispatcherConstants.PROPERTY_CONSUME_EVENT_TOPIC +"=" + CompressorStatics.TOPIC_START_COMPRESSOR,
		EventDispatcherConstants.PROPERTY_CONSUME_EVENT_TOPIC +"=" + CompressorStatics.TOPIC_STOP_COMPRESSOR,
		EventDispatcherConstants.PROPERTY_CONSUME_EVENT_TOPIC +"=" + CompressorStatics.TOPIC_RAW_EVENT
	}
)
public class Compressor implements IQueueService,IQueueController,IOnQueuedEvent
{
	@Reference(cardinality=ReferenceCardinality.OPTIONAL,policy=ReferencePolicy.DYNAMIC)
	protected volatile IEventDispatcher dispatcher;
	
	private boolean run = false;
	private int minCount = -1;
	private int maxCount = -1;
	private int collectedEvents = 0;
	
	@Override
	public void onQueuedEvent(IQueuedEvent event)
	{
		if(event.getEvent().getTopic().equals(CompressorStatics.TOPIC_START_COMPRESSOR))
		{
			this.run = true;
			return;
		}
		
		if(event.getEvent().getTopic().equals(CompressorStatics.TOPIC_STOP_COMPRESSOR))
		{
			this.run = false;
			return;
		}
		
		if(run)
		{
			if(event.getEvent().getTopic().equals(CompressorStatics.TOPIC_RAW_EVENT))
			{
				int count = (Integer)event.getNativeEventProperties().get(CompressorStatics.PROPERTY_COUNT);
				if((minCount < 0) || (count < minCount))
				{
					minCount = count;
				}
				if((maxCount < 0) || (count > maxCount))
				{
					maxCount = count;
				}
				collectedEvents++;
				
				this.sendEvents(event.getQueue());
			}
		}
	}
	
	@Override
	public void run(IQueueTaskContext taskContext)
	{
		if(run)
		{
			this.sendEvents(taskContext.getQueue());
		}
	}
	
	private void sendEvents(IQueue queue)
	{
		long now = System.currentTimeMillis();
		Long lastSend = queue.getMetrics().getGauge(Long.class, IMetrics.GAUGE_LAST_SEND_EVENT).getValue();
		queue.rescheduleTask(CompressorStatics.COMPRESSOR_SERVICE_ID, now + CompressorStatics.HEARTBEAT_INTERVAL, -1, -1);
		
		if(collectedEvents == 0)
		{
			if((lastSend == null) || (lastSend.longValue() <= (now - CompressorStatics.HEARTBEAT_INTERVAL)))
			{
				HashMap<String,Object> properties = new HashMap<String,Object>();
				properties.put(CompressorStatics.PROPERTY_COUNT_SIZE, 0);
				queue.sendEvent(CompressorStatics.TOPIC_COMPRESSED_EVENT, properties);
			}
		}
		else
		{
			if((lastSend == null) || (lastSend.longValue() <= (now - CompressorStatics.MINIMAL_INTERVAL)))
			{
				HashMap<String,Object> properties = new HashMap<String,Object>();
				properties.put(CompressorStatics.PROPERTY_COUNT_SIZE, collectedEvents);
				properties.put(CompressorStatics.PROPERTY_COUNT_MIN, minCount);
				properties.put(CompressorStatics.PROPERTY_COUNT_MAX, maxCount);
				queue.sendEvent(CompressorStatics.TOPIC_COMPRESSED_EVENT, properties);
				collectedEvents = 0;
				minCount = -1;
				maxCount = -1;
			}
			else
			{
				queue.rescheduleTask(CompressorStatics.COMPRESSOR_SERVICE_ID, lastSend + CompressorStatics.MINIMAL_INTERVAL, -1, -1);
			}
		}
	}


}
