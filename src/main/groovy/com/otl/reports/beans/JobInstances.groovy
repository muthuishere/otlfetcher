/**
 * 
 */
package com.otl.reports.beans

import com.otl.reports.exceptions.ServiceException;

/**
 * @author hutchuk
 *
 */
class JobInstances {
	
	def jobInstID
	def jobInstDesc
	def jobParentID
	Date jobInstActualStartTime
	Date jobInstRevisedStartTime
	def jobInstResult
	def jobInstRetry
	def jobInstReportLocation
	def jobInstReportName
	def jobInstFrequency
	def jobInstTaskName

	String getJobInstID() throws ServiceException{
		
		if(!jobInstTaskName || !jobParentID || !jobInstActualStartTime)
			throw new  ServiceException("Values cannot be empty");
			
		StringBuffer sb=new StringBuffer();
		sb.append(jobParentID).append("_")
		sb.append(jobInstTaskName).append("_")
		sb.append(jobInstActualStartTime?.format("yyyy_MM_dd_HH_mm"))
		
		
		return sb.toString()
		
	}
	
}
