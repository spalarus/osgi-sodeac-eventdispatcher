/*******************************************************************************
 * Copyright (c) 2017, 2018 Sebastian Palarus
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Palarus - initial API and implementation
 *******************************************************************************/
package org.sodeac.eventdispatcher.api;

import java.util.List;

/**
 * A {@link IQueueTask} acts as processor for queued {@link IQueuedEvent}s or as service.
 * 
 * @author Sebastian Palarus
 *
 */
public interface IQueueTask
{
	
	public static final String PROPERTY_KEY_TASK_ID 				= "TASK_ID"				;
	public static final String PROPERTY_KEY_EXECUTION_TIMESTAMP 	= "EXECUTION_TIMESTAMP"	;
	public static final String PROPERTY_KEY_TIMEOUT_VALUE 			= "TIMEOUT_VALUE"		;
	public static final String PROPERTY_KEY_HEARTBEAT_TIMEOUT 		= "HEARTBEAT_TIMEOUT "	;
	public static final String PROPERTY_KEY_THROWED_EXCEPTION		= "THROWED_EXCEPTION"	;
	
	
	public static final long DEFAULT_TIMEOUT = 1080 * 1080;
	
	/**
	 * invoked onetime at initialization of this task
	 * 
	 * @param queue parent-{@link IQueue} 
	 * @param id registration-id of this task
	 * @param metrics metric-handler for this task
	 * @param propertyBlock properties for this task
	 * @param taskControl state-handler for this task
	 */
	public default void configure(IQueue queue, String id, IMetrics metrics, IPropertyBlock propertyBlock, ITaskControl taskControl) {};
	
	/**
	 * run this task, invoked by queue-worker.
	 * 
	 * @param queue parent-{@link IQueue} 
	 * @param metrics metric-handler for this task
	 * @param propertyBlock properties for this task
	 * @param taskControl state-handler for this task
	 * @param currentProcessedTaskList all tasks run in the same task phase
	 */
	public void run(IQueue queue,IMetrics metrics, IPropertyBlock propertyBlock, ITaskControl taskControl, List<IQueueTask> currentProcessedTaskList);
}