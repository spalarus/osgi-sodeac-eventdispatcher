/*******************************************************************************
 * Copyright (c) 2017, 2018 Sebastian Palarus
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Palarus - initial API and implementation
 *******************************************************************************/
package org.sodeac.eventdispatcher.impl;

import java.util.concurrent.locks.ReentrantLock;

import org.sodeac.eventdispatcher.api.IJobControl;
import org.sodeac.eventdispatcher.api.IPropertyBlock;
import org.sodeac.eventdispatcher.api.IQueueJob;

public class JobControlImpl implements IJobControl
{
	private volatile boolean done = false;
	private volatile boolean inTimeOut = false;
	private volatile long executionTimeStamp = 0L;
	private volatile long timeOutValue = IQueueJob.DEFAULT_TIMEOUT;
	private volatile long heartBeatTimeOut = -1;
	
	private volatile boolean stopJobOnTimeout = false;
	private volatile boolean inRun = false;
	private volatile ExecutionTimeStampSource executionTimeStampSource = IJobControl.ExecutionTimeStampSource.SCHEDULE;
	
	
	private IPropertyBlock jobPropertyBlock = null;
	
	private ReentrantLock executionTimestampLock = null;
	
	public JobControlImpl(IPropertyBlock jobPropertyBlock)
	{
		super();
		this.executionTimeStamp = System.currentTimeMillis();
		this.jobPropertyBlock = jobPropertyBlock;
		
		this.jobPropertyBlock.setProperty(IQueueJob.PROPERTY_KEY_EXECUTION_TIMESTAMP, this.executionTimeStamp);
		this.jobPropertyBlock.setProperty(IQueueJob.PROPERTY_KEY_TIMEOUT_VALUE, this.timeOutValue);
		this.jobPropertyBlock.setProperty(IQueueJob.PROPERTY_KEY_HEARTBEAT_TIMEOUT, this.heartBeatTimeOut);
		
		this.executionTimestampLock = new ReentrantLock();
	}
	
	public void preRun()
	{
		this.inRun = true;
		this.done = true;
	}
	
	public void preRunPeriodicJob()
	{
		this.inRun = true;
	}
	
	public void postRun()
	{
		this.inRun = false;
	}
	
	
	
	@Override
	public boolean setDone()
	{
		boolean old = this.done;
		this.done = true;
		return old;
	}

	@Override
	public void timeOut()
	{
		this.inTimeOut = true;
		this.done = true;
	}
	
	public void timeOutService()
	{
		this.inTimeOut = true;
	}

	@Override
	public boolean isInTimeOut()
	{
		return inTimeOut;
	}

	@Override
	public long getExecutionTimeStamp()
	{
		return this.executionTimeStamp;
	}
	
	@Override
	public ExecutionTimeStampSource getExecutionTimeStampSource()
	{
		return this.executionTimeStampSource;
	}
	
	public void setExecutionTimeStampSource(ExecutionTimeStampSource executionTimeStampSource)
	{
		this.executionTimeStampSource = executionTimeStampSource;
	}

	public long getExecutionTimeStampIntern()
	{
		return this.executionTimeStamp;
	}
	
	@Override
	public boolean setExecutionTimeStamp(long executionTimeStamp, boolean force)
	{
		executionTimestampLock.lock();
		try
		{
			long old = this.executionTimeStamp;
			if
			( 
				(executionTimeStamp < old) || 
				force || 
				(old < System.currentTimeMillis()) || 
				(this.executionTimeStampSource == IJobControl.ExecutionTimeStampSource.SCHEDULE) ||
				(this.executionTimeStampSource == IJobControl.ExecutionTimeStampSource.WORKER) ||
				(this.executionTimeStampSource == IJobControl.ExecutionTimeStampSource.PERODIC)
			) 
			{
				this.executionTimeStamp = executionTimeStamp;
				this.executionTimeStampSource = IJobControl.ExecutionTimeStampSource.WORKER;
				this.jobPropertyBlock.setProperty(IQueueJob.PROPERTY_KEY_EXECUTION_TIMESTAMP, this.executionTimeStamp);
				
				if(inRun)
				{
					this.done = false;
				}
				
				return true;
			}
			
			return false;
		}
		finally 
		{
			executionTimestampLock.unlock();
		}
	}
	
	public void setExecutionTimeStampPeriodic(long executionTimeStamp)
	{
		executionTimestampLock.lock();
		try
		{
			long old = this.executionTimeStamp;
			if
			(
				(old > System.currentTimeMillis()) &&
				(this.executionTimeStampSource == IJobControl.ExecutionTimeStampSource.RESCHEDULE) &&
				(executionTimeStamp > 0)
			)
			{
				return;
			}
			
			this.executionTimeStamp = executionTimeStamp;
			this.executionTimeStampSource = IJobControl.ExecutionTimeStampSource.PERODIC;
			this.jobPropertyBlock.setProperty(IQueueJob.PROPERTY_KEY_EXECUTION_TIMESTAMP, this.executionTimeStamp);
		}
		finally 
		{
			executionTimestampLock.unlock();
		}
	}
	
	public void setExecutionTimeStampSchedule(long executionTimeStamp)
	{
		executionTimestampLock.lock();
		try
		{
			this.executionTimeStamp = executionTimeStamp;
			this.executionTimeStampSource = IJobControl.ExecutionTimeStampSource.SCHEDULE;
			this.jobPropertyBlock.setProperty(IQueueJob.PROPERTY_KEY_EXECUTION_TIMESTAMP, this.executionTimeStamp);
		}
		finally 
		{
			executionTimestampLock.unlock();
		}
		
	}
	
	public void setExecutionTimeStampReschedule(long executionTimeStamp)
	{
		executionTimestampLock.lock();
		try
		{
			long old = this.executionTimeStamp;
			if
			( 
				(executionTimeStamp < old) || 
				(old < System.currentTimeMillis()) || 
				(this.executionTimeStampSource == IJobControl.ExecutionTimeStampSource.SCHEDULE) ||
				(this.executionTimeStampSource == IJobControl.ExecutionTimeStampSource.RESCHEDULE) 
			) 
			{
				this.executionTimeStamp = executionTimeStamp;
				this.executionTimeStampSource = IJobControl.ExecutionTimeStampSource.RESCHEDULE;
				this.jobPropertyBlock.setProperty(IQueueJob.PROPERTY_KEY_EXECUTION_TIMESTAMP, this.executionTimeStamp);	
			}
		}
		finally 
		{
			executionTimestampLock.unlock();
		}
	}

	@Override
	public long getTimeOut()
	{
		return this.timeOutValue;
	}

	@Override
	public long setTimeOut(long timeOut)
	{
		long old = this.timeOutValue;
		this.timeOutValue = timeOut;
		this.jobPropertyBlock.setProperty(IQueueJob.PROPERTY_KEY_TIMEOUT_VALUE, this.timeOutValue);
		return old;
	}
	
	@Override
	public long getHeartBeatTimeOut()
	{
		return this.heartBeatTimeOut;
	}
	
	public long setHeartBeatTimeOut(long heartBeatTimeOut)
	{
		long old =  this.heartBeatTimeOut;
		this.heartBeatTimeOut = heartBeatTimeOut;
		this.jobPropertyBlock.setProperty(IQueueJob.PROPERTY_KEY_HEARTBEAT_TIMEOUT, this.heartBeatTimeOut);
		return old;
	}

	@Override
	public boolean isDone()
	{
		return this.done;
	}

	@Override
	public boolean setStopOnTimeOutFlag(boolean value)
	{
		boolean oldValue = this.stopJobOnTimeout;
		this.stopJobOnTimeout = value;
		return oldValue;
	}
	
	public boolean getStopOnTimeOutFlag()
	{
		return this.stopJobOnTimeout;
	}
}
