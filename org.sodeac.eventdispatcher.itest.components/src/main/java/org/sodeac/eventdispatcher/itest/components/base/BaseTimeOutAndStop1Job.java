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
package org.sodeac.eventdispatcher.itest.components.base;

import java.util.List;

import org.sodeac.eventdispatcher.api.IJobControl;
import org.sodeac.eventdispatcher.api.IMetrics;
import org.sodeac.eventdispatcher.api.IPropertyBlock;
import org.sodeac.eventdispatcher.api.IQueue;
import org.sodeac.eventdispatcher.api.IQueueJob;

public class BaseTimeOutAndStop1Job implements IQueueJob
{
	private long sleepValue;

	public BaseTimeOutAndStop1Job(long sleepValue)
	{
		super();
		this.sleepValue = sleepValue;
	}
	
	@Override
	public void configure(String id, IMetrics metrics, IPropertyBlock propertyBlock, IJobControl jobControl){}

	@Override
	public void run(IQueue queue, IMetrics metrics, IPropertyBlock propertyBlock, IJobControl jobControl,List<IQueueJob> currentProcessedJobList)
	{
		try
		{
			Thread.sleep(this.sleepValue);
		}
		catch (InterruptedException e) 
		{
			queue.signal("INTERRUPT");
			
			try
			{
				Thread.sleep(this.sleepValue);
			}
			catch (Exception ie) {}
			catch(ThreadDeath td )
			{
				queue.signal("THREAD_DEATH");
				throw td;
			}
			
			return;
		}
		queue.signal("JOB_DONE_SIGNAL");
	}

}