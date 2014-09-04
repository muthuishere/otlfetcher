package com.otl.reports.beans

import com.otl.reports.exceptions.ServiceException;

import groovy.transform.ToString

@ToString
class UserTimeSummary {

	def user
	def workhours
	def leavehours
	def lastupdated
	def userLocked=false
	
	def getWorkdays() throws ServiceException{
		
		
			
		return (workhours/8)
		
	}
	
	def getLeavedays() throws ServiceException{
		
		
			
		return (leavehours/8)
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserTimeSummary person = (UserTimeSummary) o;

		if (user != null ? !user.equals(person.user) : person.user != null) return false;
		

		return true;
	}
}
