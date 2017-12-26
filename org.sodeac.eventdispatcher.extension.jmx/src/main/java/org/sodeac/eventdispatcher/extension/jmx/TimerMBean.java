package org.sodeac.eventdispatcher.extension.jmx;

public interface TimerMBean
{
	public String getName();
	public String getKey();
	
public long getCount();
	
	
	public double getMeanRate();
	public double getOneMinuteRate();
	public double getFiveMinuteRate();
	public double getFifteenMinuteRate();
	
	public double getValue(double quantile);
	// TODO ??? public long[] getValues();
	public int size();
	public double getMedian();
	public double get75thPercentile();
	public double get95thPercentile();
	public double get98thPercentile();
	public double get99thPercentile();
	public double get999thPercentile();
	public long getMax();
	public double getMean();
	public long getMin();
}
