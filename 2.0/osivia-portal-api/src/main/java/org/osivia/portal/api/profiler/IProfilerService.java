package org.osivia.portal.api.profiler;


public interface IProfilerService {
	
	public void logEvent( String category, String name, long time, boolean error);
	
}
