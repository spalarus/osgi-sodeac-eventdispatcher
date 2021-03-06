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
package org.sodeac.eventdispatcher.extension.api;

import org.sodeac.eventdispatcher.api.IMetrics;

public interface IExtensibleMetrics extends IMetrics
{
	public boolean isEnabled();
	public String getTaskId();
	public IExtensibleQueue getQueue();
}
