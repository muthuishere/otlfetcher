/**
 * 
 */
package com.otl.reports.beans

import com.otl.reports.exceptions.ServiceException;

/**
 * @author hutchuk
 *
 */
class JobDescription {

	def jobName
	//def jobID
	def jobDesc
	def jobValidity
	Date jobStartDate
	Date jobEndDate
	def jobFrequency
	def jobRetry
	def jobReportFolder
	def jobToAddress
	def jobCCAddress
	def jobBCCAddress
	def jobSubject
	def jobMailContent
	def jobOwner
	def jobLastCreatedInstTime
	def jobTaskName
	
	String getJobID() throws ServiceException{
		
		if(!jobName || !jobOwner || !jobStartDate)
			throw new  ServiceException("Values cannot be empty");
			
		StringBuffer sb=new StringBuffer();
		sb.append(jobOwner).append("_")
		sb.append(jobFrequency).append("_")
		sb.append(jobStartDate?.format("yyyy_MM_dd_HH_mm"))
		
		
		return sb.toString()
		
	}
 
	
	@Override
	public String toString() {
		return "JobDescription [jobName=" + jobName + ", jobDesc=" + jobDesc + ", jobValidity=" + jobValidity + ", jobStartDate=" + jobStartDate + ", jobEndDate=" + jobEndDate	+ ", jobFrequency=" + jobFrequency + ", jobRetry=" + jobRetry + ", jobReportFolder=" + jobReportFolder + ", jobToAddress="	+ jobToAddress + ", jobCCAddress=" + jobCCAddress + ", jobBCCAddress=" + jobBCCAddress + ", jobSubject=" + jobSubject + ", jobMailContent=" + jobMailContent + ", jobOwner=" + jobOwner + ", jobLastCreatedInstTime="	+ jobLastCreatedInstTime + ", jobTaskName=" + jobTaskName + "]";
	}

}
