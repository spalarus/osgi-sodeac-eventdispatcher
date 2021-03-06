/*******************************************************************************
 * Copyright (c) 2018, 2019 Sebastian Palarus
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Palarus - initial API and implementation
 *******************************************************************************/
package org.sodeac.eventdispatcher.common.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.sodeac.eventdispatcher.api.IQueueController;
import org.sodeac.eventdispatcher.api.EventDispatcherConstants;
import org.sodeac.eventdispatcher.api.IOnQueueAttach;
import org.sodeac.eventdispatcher.api.IOnQueueDetach;
import org.sodeac.eventdispatcher.api.IQueue;

@Component
(
	name				= "QueueFactoryController"				,
	service				= IQueueController.class					,
	configurationPid	= QueueFactoryController.SERVICE_PID	, 
	configurationPolicy	= ConfigurationPolicy.REQUIRE
)

public class QueueFactoryController implements IQueueController,IOnQueueAttach, IOnQueueDetach
{
	public static final String SERVICE_PID = "org.sodeac.eventdispatcher.common.controller.queuefactory";
	
	private volatile ComponentContext context = null;
	private volatile Map<String, ?> properties = null;
	private List<IQueue> queues = new ArrayList<IQueue>();
	private ReentrantLock lock = new ReentrantLock();
	
	@ObjectClassDefinition(name=SERVICE_PID, description="",factoryPid=QueueFactoryController.SERVICE_PID)
	interface Config
	{
		@AttributeDefinition(name="dispatcher",description = "id of dispatcher (default:'default')" , defaultValue=EventDispatcherConstants.DEFAULT_DISPATCHER_ID, type=AttributeType.STRING, required=true)
		String dispatcherid();
		
		@AttributeDefinition(name="queueid",description = "queueid of attached / created queue" ,type=AttributeType.STRING, required=true)
		String queueid();
		
		@AttributeDefinition(name="queuetype",description = "value for configuration property 'type'" ,type=AttributeType.STRING, required=true)
		String queuetype();
		
		@AttributeDefinition(name="name",description = "name of factory" ,type=AttributeType.STRING)
		String jmxname();
		
		@AttributeDefinition(name="category",description = "category of factory" ,type=AttributeType.STRING)
		String jmxcategory();
	}
	
	@Activate
	public void activate(ComponentContext context, Map<String, ?> properties)
	{
		this.context = context;
		this.properties = properties;
		
		if(this.properties == null)
		{
			return;
		}
		
		List<IQueue> queueCopy = null;
		
		lock.lock();
		try
		{
			queueCopy = new ArrayList<>(this.queues);
		}
		finally 
		{
			lock.unlock();
		}
		
		for(IQueue queue : queueCopy)
		{
			for(Entry<String,?> entry : this.properties.entrySet())
			{
				queue.getConfigurationPropertyBlock().setProperty(entry.getKey(), entry.getValue());
			}
		}
	}
	
	@Deactivate
	public void deactivate(ComponentContext context)
	{
		this.context = null;
		this.properties = null;
		
		lock.lock();
		try
		{
			this.queues.clear();
		}
		finally 
		{
			lock.unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Modified 
	public void modified(Map<String, ?> properties)
	{
		this.properties = properties;
		
		if(this.properties == null)
		{
			return;
		}
		
		List<IQueue> queueCopy = null;
		
		lock.lock();
		try
		{
			queueCopy = new ArrayList<>(this.queues);
		}
		finally 
		{
			lock.unlock();
		}
		
		for(IQueue queue : queueCopy)
		{
			queue.getConfigurationPropertyBlock().setPropertyEntrySet(((Map<String,Object>)this.properties).entrySet(),true);	
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onQueueAttach(IQueue queue)
	{
		lock.lock();
		try
		{
			boolean found = false;
			for(IQueue queueItem : this.queues)
			{
				if(queueItem == queue)
				{
					found = true;
					break;
				}
			}
			if(! found)
			{
				this.queues.add(queue);
			}
		}
		finally 
		{
			lock.unlock();
		}
		
		if(this.properties == null)
		{
			return;
		}
		queue.getConfigurationPropertyBlock().setPropertyEntrySet(((Map<String,Object>)this.properties).entrySet(),true);		
	}

	@Override
	public void onQueueDetach(IQueue queue)
	{
		lock.lock();
		try
		{
			while(this.queues.remove(queue)) {}
		}
		finally 
		{
			lock.unlock();
		}		
	}
}
