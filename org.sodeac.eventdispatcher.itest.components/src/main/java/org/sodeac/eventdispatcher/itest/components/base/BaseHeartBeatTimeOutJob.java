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
package org.sodeac.eventdispatcher.itest.components.base;

import org.sodeac.eventdispatcher.api.IMetrics;
import org.sodeac.eventdispatcher.api.IQueueTask;
import org.sodeac.eventdispatcher.api.IQueueTaskContext;

public class BaseHeartBeatTimeOutJob implements IQueueTask
{
	public static final long HEARTBEATS_IN_TIME = 1000 + 2000 + 3000;
	
	public BaseHeartBeatTimeOutJob()
	{
		super();
	}

	@Override
	public void run(IQueueTaskContext taskContext)
	{
		IMetrics metrics = taskContext.getTaskMetrics();
		
		try
		{
			Thread.sleep(1000);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		metrics.setQualityValue(IMetrics.QUALITY_VALUE_LAST_HEARTBEAT, System.currentTimeMillis());
		
		try
		{
			Thread.sleep(2000);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		metrics.setQualityValue(IMetrics.QUALITY_VALUE_LAST_HEARTBEAT, System.currentTimeMillis());
		
		try
		{
			Thread.sleep(3000);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		metrics.setQualityValue(IMetrics.QUALITY_VALUE_LAST_HEARTBEAT, System.currentTimeMillis());
		
		try
		{
			Thread.sleep(4000);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		metrics.setQualityValue(IMetrics.QUALITY_VALUE_LAST_HEARTBEAT, System.currentTimeMillis());
		try
		{
			Thread.sleep(5000);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		metrics.setQualityValue(IMetrics.QUALITY_VALUE_LAST_HEARTBEAT, System.currentTimeMillis());
	}

}
