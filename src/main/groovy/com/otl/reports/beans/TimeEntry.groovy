package com.otl.reports.beans

import com.otl.reports.exceptions.ServiceException

import groovy.transform.ToString

@ToString
class TimeEntry {

	Date entryDate
	def user
	def projectcode
	def projecttask
	def tasktype
	def hours
	def details
	def isLeave
	Date fetchedDate
	def status
	def team
	ProjectInfo projectInfo;
	
	public boolean objectequals(TimeEntry timeEntry) {
		
		if(null == timeEntry)
			return false
		if(!entryDate || !user || !projectcode || !projecttask ||  !tasktype)
			return false
		
			if(!timeEntry.entryDate || !timeEntry.user || !timeEntry.projectcode || !timeEntry.projecttask ||  !timeEntry.tasktype)
			return false
			
			return (timeEntry.entryDate.getTime() == entryDate.getTime() &&  timeEntry.user == user && timeEntry.projectcode == projectcode && timeEntry.projecttask == projecttask && timeEntry.tasktype == tasktype )
			
	}
	@Override
	public boolean equals(Object o) {
//		if (this == o) return true;
	//	if (o == null || getClass() != o.getClass()) return false;

		TimeEntry timeEntry = (TimeEntry) o;

		if (user != null ? !user.equals(timeEntry.user) : timeEntry.user != null) return false;
		if (entryDate != null ? !entryDate.equals(timeEntry.entryDate) : timeEntry.entryDate != null) return false;
		
		if (projectcode != null ? !projectcode.equals(timeEntry.projectcode) : timeEntry.projectcode != null) return false;
		
		if (projecttask != null ? !projecttask.equals(timeEntry.projecttask) : timeEntry.projecttask != null) return false;
		
		if (tasktype != null ? !tasktype.equals(timeEntry.tasktype) : timeEntry.tasktype != null) return false;
		
		//if (hours != timeEntry.hours) return false;
		
		
		return true;
	}
	
	
	
	String getKey() throws ServiceException{
		
		if(!entryDate || !user || !projectcode || !projecttask ||  !tasktype)
			throw new  ServiceException("Values cannot be empty");
			
		StringBuffer sb=new StringBuffer();
		sb.append(entryDate?.format("yyyy_MM_dd")).append("_")
		sb.append(user).append("_")
		sb.append(projectcode).append("_")
		sb.append(projecttask).append("_")
		sb.append(tasktype)
		
		return sb.toString()
		
	}
}
