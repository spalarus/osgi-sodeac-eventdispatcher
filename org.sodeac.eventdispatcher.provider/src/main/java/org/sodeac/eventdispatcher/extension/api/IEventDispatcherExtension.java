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
package org.sodeac.eventdispatcher.extension.api;

import java.util.Map;

import org.sodeac.eventdispatcher.api.IEventController;

public interface IEventDispatcherExtension
{
	public void registerEventDispatcher(IExtensibleEventDispatcher dispatcher);
	public void unregisterEventDispatcher(IExtensibleEventDispatcher dispatcher);
	public void registerEventController(IExtensibleEventDispatcher dispatcher, IEventController eventController,Map<String, ?> properties);
	public void unregisterEventController(IExtensibleEventDispatcher dispatcher, IEventController eventController);
}
