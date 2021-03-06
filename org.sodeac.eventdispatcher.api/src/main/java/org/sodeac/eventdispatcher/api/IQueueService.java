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
package org.sodeac.eventdispatcher.api;

import java.util.List;

/**
 * OSGi-service interface to register {@link IQueueTask} as OSGi-service / component
 * 
 * @author Sebastian Palarus
 *
 */
public interface IQueueService extends IQueueTask,IQueueComponent
{ 
	/**
	 * 
	 * @return list of configuration to configure queue service
	 */
	public default List<QueueComponentConfiguration> configureQueueService()
	{
		return null;
	}
}
