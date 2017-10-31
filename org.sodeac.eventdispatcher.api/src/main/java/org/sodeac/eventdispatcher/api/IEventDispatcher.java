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

import java.util.List;

import org.osgi.service.event.Event;

/**
 * 
 * API for eventdispatcher service
 * 
 *  @author Sebastian Palarus
 *
 */
public interface IEventDispatcher
{
	public static final String PROPERTY_QUEUE_ID = "queueid";
	
	/**
	 * schedule an osgi event to eventdispatcher queue
	 * 
	 * @param event osgi-event to schedule to {@link IQueue} 
	 * @param queueId id of {@link IQueue} 
	 * 
	 * @return false if queue is missing, otherwise true 
	 */
	public boolean schedule(Event event, String queueId);
	
	/**
	 * factory-methode creating instance of {@link IPropertyBlock} 
	 * 
	 * @return instance of {@link IPropertyBlock} 
	 */
	public IPropertyBlock createPropertyBlock();
	
	/**
	 * getter to request for all {@link IQueue}s
	 * 
	 * @return {@link java.util.List} with queueIds
	 */
	public List<String> getQueueIdList();
	
	/**
	 * getter to request for {@link IQueue} with given id
	 * 
	 * @param queueId  id for queue
	 * @return instance of {@link IQueue} registered with {@code queueId}
	 */
	public IQueue getQueue(String queueId);
}
