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
package org.sodeac.eventdispatcher.common.edservice.api.wiring.capability;

import org.sodeac.eventdispatcher.common.edservice.api.wiring.Capability;
import org.sodeac.eventdispatcher.common.edservice.api.wiring.CapabilityStringValue;

public class BoundedContextCapability extends Capability
{
	public static final String CAPABILITY_NAME_BOUNDED_CONTEXT = "boundedcontext";
	
	public BoundedContextCapability(String boundedContext)
	{
		super(DomainCapability.NAMESPACE, CAPABILITY_NAME_BOUNDED_CONTEXT, new CapabilityStringValue(boundedContext), false);
	}
}
