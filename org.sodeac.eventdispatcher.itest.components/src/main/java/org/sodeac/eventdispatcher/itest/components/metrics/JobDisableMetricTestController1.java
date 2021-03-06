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

@Component
(
	immediate=true,
	service={IQueueController.class,EventHandler.class},
	property=
	{
		EventDispatcherConstants.PROPERTY_QUEUE_ID+"="+JobDisableMetricTestController1.QUEUE_ID,
		EventConstants.EVENT_TOPIC+"=" + JobDisableMetricTestController1.RUN_EVENT,
		EventDispatcherConstants.PROPERTY_DISABLE_METRICS+"=true"
	}
)
public class JobDisableMetricTestController1 extends JobMetricTestController
{
	public static final String QUEUE_ID 					= "jobdisablemetrics1"	;
	public static final String RUN_EVENT 					= "org/sodeac/eventdispatcher/itest/metrics/jobdisablemetrics1/run";
	
	@Reference(cardinality=ReferenceCardinality.OPTIONAL,policy=ReferencePolicy.DYNAMIC)
	protected volatile IEventDispatcher dispatcher;
	
	@Override
	public void handleEvent(Event event)
	{
		this.dispatcher.queueEvent(JobDisableMetricTestController1.QUEUE_ID, event);
	}
	
}