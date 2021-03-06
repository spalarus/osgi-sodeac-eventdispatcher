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
 * 
 * An eventcontroller reacts to a wide variety of queue happenings, if it implements appropriate extension interfaces.
 * 
 * @author Sebastian Palarus
 *
 */
public interface IQueueController extends IQueueComponent
{
	/**
	 * 
	 * 
	 * @return list of configuration to configure queue controller
	 */
	public default List<QueueComponentConfiguration> configureQueueController()
	{
		return null;
	}
}
