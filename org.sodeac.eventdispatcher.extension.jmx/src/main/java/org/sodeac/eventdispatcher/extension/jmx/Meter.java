/*******************************************************************************
 * Copyright (c) 2018 Sebastian Palarus
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Palarus - initial API and implementation
 *******************************************************************************/
package org.sodeac.eventdispatcher.extension.jmx;

import javax.management.ObjectName;

import org.sodeac.eventdispatcher.extension.api.IExtensibleMeter;

public class Meter implements MeterMBean
{
	protected ObjectName meterObjectName = null;
	private IExtensibleMeter meter = null;
	public Meter(ObjectName meterObjectName, IExtensibleMeter meter)
	{
		super();
		this.meterObjectName = meterObjectName;
		this.meter = meter;
	}
	
	
	@Override
	public String getName()
	{
		return this.meter.getName();
	}
	
	@Override
	public String getKey()
	{
		return this.meter.getKey();
	}


	@Override
	public long getCount()
	{
		return this.meter.getCount();
	}


	@Override
	public double getMeanRate()
	{
		return this.meter.getMeanRate();
	}


	@Override
	public double getOneMinuteRate()
	{
		return this.meter.getOneMinuteRate();
	}


	@Override
	public double getFiveMinuteRate()
	{
		return this.meter.getFiveMinuteRate();
	}


	@Override
	public double getFifteenMinuteRate()
	{
		return this.meter.getFifteenMinuteRate();
	}
}
