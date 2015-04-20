/**
 * 
 */
package com.otl.scheduler.batch

/**
 * @author hutchuk
 *
 */
public interface Scheduler {
	
	void scheduleNewJobs();
	void scheduleInprogressJobs();
	void reRunFailedJobs();
	

}
