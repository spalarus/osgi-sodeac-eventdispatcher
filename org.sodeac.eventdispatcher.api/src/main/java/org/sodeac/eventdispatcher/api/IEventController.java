/*******************************************************************************
 * Copyright (c) 2017 Sebastian Palarus
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Palarus - initial API and implementation
 *******************************************************************************/
package org.sodeac.eventdispatcher.api;

/**
 * 
 * An eventcontroller reacts to a wide variety of queue happenings, if it implements appropriate extension interfaces.
 * 
 * @author Sebastian Palarus
 *
 */
public interface IEventController
{
	/**
	 * configuration property key to declare an {@link IEventController} as consumer of {@link org.osgi.service.event.Event} with given topics (value of property)
	 */
	public static final String PROPERTY_CONSUME_EVENT_TOPIC 	= "consume_event_topic";
	
	/**
	 * configuration property key to declare an {@link IEventController} for disabling metrics on observe {@link IQueue} ( alternative {@link IDisableMetricsOnQueueObserve})
	 */
	public static final String PROPERTY_DISABLE_METRICS			= "queue_disable_metrics";
	
	/**
	 * configuration property key to declare the name of {@link IEventController} in jmx tools
	 */
	public static final String PROPERTY_JMX_NAME				= "jmxname";
	
	/**
	 * configuration property key to declare category subfolder of {@link IEventController} in jmx tools
	 */
	public static final String PROPERTY_JMX_CATEGORY			= "jmxcategory";
	
}
