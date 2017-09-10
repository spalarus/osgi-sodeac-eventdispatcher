package org.sodeac.eventdispatcher.itest.runner;

import org.ops4j.pax.exam.Option;

import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.options.ProvisionOption;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.Bundle;
import org.sodeac.eventdispatcher.api.IEventDispatcher;

import java.io.File;

public abstract class AbstractTest
{
	public static ProvisionOption<?> reactorBundle(String artifactId, String version) 
	{
		String fileName = String.format("%s/../%s/target/%s-%s.jar", PathUtils.getBaseDir(), artifactId, artifactId,version);

		if (new File(fileName).exists()) 
		{
			try
			{
				String url = "file:" + new File(fileName).getCanonicalPath();
				return bundle(url);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			fileName = String.format("%s/../%s/target/%s-%s-SNAPSHOT.jar", PathUtils.getBaseDir(), artifactId, artifactId,version);

			if (new File(fileName).exists()) 
			{
				try
				{
					String url = "file:" + new File(fileName).getCanonicalPath();
					return bundle(url);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static String getBundleStateName(int state)
	{
		switch (state) 
		{
			case Bundle.UNINSTALLED:
					
				return "UNINSTALLED";
				
			case Bundle.INSTALLED:
				
				return "INSTALLED";
	
			case Bundle.RESOLVED:
				
				return "RESOLVED";
			
			case Bundle.STARTING:
				
				return "STARTING";
			
			case Bundle.STOPPING:
				
				return "STOPPING";
				
			case Bundle.ACTIVE:
				
				return "ACTIVE";
			default:
				
				return "State " + state;
		}
	}

	public Option[] config() 
	{
		MavenArtifactUrlReference karafUrl = maven()
			.groupId("org.apache.karaf")
			.artifactId("apache-karaf")
			.version("4.1.2")
			.type("zip");

		MavenUrlReference karafStandardRepo = maven()
			.groupId("org.apache.karaf.features")
			.artifactId("standard")
			.version("4.1.2")
			.classifier("features")
			.type("xml");
		
		return new Option[] 
		{
			karafDistributionConfiguration()
				.frameworkUrl(karafUrl)
				.unpackDirectory(new File("target", "exam"))
				.useDeployFolder(false),
			keepRuntimeFolder(),
			cleanCaches( true ),
			logLevel(LogLevel.INFO),
			features(karafStandardRepo , "scr"),
			mavenBundle("io.dropwizard.metrics", "metrics-core", "3.2.3").start(),
			mavenBundle("org.apache.sling", "org.apache.sling.commons.metrics", "1.2.2").start(),
			mavenBundle("org.easymock", "easymock", "3.4").start(),
			reactorBundle("org.sodeac.eventdispatcher.api","0.9.0").start(),
			reactorBundle("org.sodeac.eventdispatcher.provider","0.9.0").start(),
			reactorBundle("org.sodeac.eventdispatcher.common","0.9.0").start(),
			reactorBundle("org.sodeac.eventdispatcher.itest.components","0.9.0").start()
		};
	}
	
	public void waitQueueIsUp(IEventDispatcher eventDispatcher, String queueId, long timeOut)
	{
		long timeOutTimestamp = System.currentTimeMillis() + timeOut;
		while(timeOutTimestamp > System.currentTimeMillis())
		{
			if(eventDispatcher.getQueue(queueId) != null)
			{
				break;
				
			}
			try {Thread.sleep(108);}catch (Exception e) {}
		}
	}
	
}
