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
package org.sodeac.eventdispatcher.common.reactiveservice.api.wiring.requirement;

import org.sodeac.eventdispatcher.common.reactiveservice.api.wiring.Requirement;
import org.sodeac.eventdispatcher.common.reactiveservice.api.wiring.capability.BoundedContextCapability;
import org.sodeac.eventdispatcher.common.reactiveservice.api.wiring.capability.DomainCapability;

public class BoundedContextRequirement extends Requirement
{
	public BoundedContextRequirement(String boundedContext)
	{
		super(DomainCapability.NAMESPACE, "(" + BoundedContextCapability.CAPABILITY_NAME_BOUNDED_CONTEXT + "=" + boundedContext + ")", false);
	}
}