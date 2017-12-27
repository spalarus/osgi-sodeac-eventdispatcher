package org.sodeac.eventdispatcher.common;

import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

public class CommonEventDispatcherHelper
{
	public static boolean getSwitchFromProperty(Map<String,?> properties,String key, String switchName)
	{
		if(properties == null)
		{
			return false;
		}
		Object value = properties.get(key);
		if(value == null)
		{
			return false;
		}
		String stringValue = value.toString();
		if(stringValue == null)
		{
			return false;
		}
		if(stringValue.equalsIgnoreCase(switchName))
		{
			return true;
		}
		if(stringValue.toLowerCase().startsWith(switchName.toLowerCase() + "+"))
		{
			return true;
		}
		if(stringValue.toLowerCase().endsWith("+" + switchName.toLowerCase()))
		{
			return true;
		}
		if(stringValue.toLowerCase().indexOf("+" + switchName.toLowerCase() + "+") > -1)
		{
			return true;
		}
		return false;
	}
	public static void log(ComponentContext context, LogService logService, int logServiceLevel,String logMessage, Throwable e)
	{
		
		try
		{
			if(logService != null)
			{
				logService.log(context == null ? null : context.getServiceReference(), logServiceLevel, logMessage, e);
			}
			else
			{
				if(logServiceLevel == LogService.LOG_ERROR)
				{
					System.err.println(logMessage);
				}
				if(e != null)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception ie) 
		{
			ie.printStackTrace();
		}
	}
}