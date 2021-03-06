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
package org.sodeac.eventdispatcher.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.osgi.service.event.Event;
import org.sodeac.multichainlist.Linker;
import org.sodeac.multichainlist.Node;
import org.sodeac.eventdispatcher.api.IPropertyBlock;
import org.sodeac.eventdispatcher.api.IQueue;
import org.sodeac.eventdispatcher.api.IQueuedEvent;
import org.sodeac.eventdispatcher.api.IQueueEventResult;

public class QueuedEventImpl implements IQueuedEvent
{
	private QueueEventResultImpl scheduleResult = null;
	private QueueImpl queue = null;
	private Event event = null;
	private String uuid = null;
	
	private volatile PropertyBlockImpl propertyBlock = null;
	private volatile Map<String,Object> nativeProperties;
	private long createTimeStamp;
	private Node<QueuedEventImpl> node = null;
	private Linker<QueuedEventImpl> linker = null;
	
	public QueuedEventImpl(Event event,QueueImpl queue)
	{
		super();
		this.event = event;
		this.queue = queue;
		this.uuid = UUID.randomUUID().toString();
		this.createTimeStamp = System.currentTimeMillis();
	}

	public Node<QueuedEventImpl> getNode()
	{
		return node;
	}
	public void setNode(Node<QueuedEventImpl> node)
	{
		this.node = node;
	}

	@Override
	public Event getEvent()
	{
		return this.event;
	}
	
	@Override
	public IQueueEventResult getScheduleResultObject()
	{
		return this.scheduleResult;
	}

	public void setScheduleResultObject(QueueEventResultImpl scheduleResult)
	{
		this.scheduleResult = scheduleResult;
	}

	@Override
	public Object setProperty(String key, Object value)
	{
		if(this.propertyBlock == null)
		{
			ReentrantLock lock = this.queue.getSharedEventLock();
			lock.lock();
			try
			{
				if(this.propertyBlock == null)
				{
					this.propertyBlock =  (PropertyBlockImpl)queue.getDispatcher().createPropertyBlock();
				}
			}
			finally 
			{
				lock.unlock();
			}
		}
		
		return this.propertyBlock.setProperty(key, value);
	}

	@Override
	public Object getProperty(String key)
	{
		if(this.propertyBlock == null)
		{
			return null;
		}
		
		return this.propertyBlock.getProperty(key);
	}

	@Override
	public String getUUID()
	{
		return uuid;
	}

	@Override
	public Set<String> getPropertyKeySet()
	{
		if(this.propertyBlock == null)
		{
			return PropertyBlockImpl.EMPTY_KEYSET;
		}
		return this.propertyBlock.getPropertyKeySet();
	}

	@Override
	public Map<String, Object> getProperties()
	{
		if(this.propertyBlock == null)
		{
			return PropertyBlockImpl.EMPTY_PROPERTIES;
		}
		return this.propertyBlock.getProperties();
	}

	@Override
	public Map<String, Object> getNativeEventProperties()
	{
		Map<String,Object> props = nativeProperties;
		if(props == null)
		{
			props = new HashMap<String,Object>();
			for(String nm : event.getPropertyNames())
			{
				props.put(nm, event.getProperty(nm));
			}
			nativeProperties = props;
		}
		return props;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapterClass)
	{
		if(adapterClass == IPropertyBlock.class)
		{
			if(this.propertyBlock == null)
			{
				ReentrantLock lock = this.queue.getSharedEventLock();
				lock.lock();
				try
				{
					if(this.propertyBlock == null)
					{
						this.propertyBlock =  (PropertyBlockImpl)queue.getDispatcher().createPropertyBlock();
					}
				}
				finally 
				{
					lock.unlock();
				}
			}
			return (T)this.propertyBlock;
		}
		return IQueuedEvent.super.getAdapter(adapterClass);
	}

	public Linker<QueuedEventImpl> getLinker()
	{
		return linker;
	}

	public void setLinker(Linker<QueuedEventImpl> linker)
	{
		this.linker = linker;
	}

	@Override
	public IQueue getQueue()
	{
		return this.queue;
	}

	@Override
	public long getCreateTimeStamp()
	{
		return this.createTimeStamp;
	}

	@Override
	public void removeFromQueue()
	{
		if(queue != null)
		{
			queue.removeEvent(this);
		}
	}
}
