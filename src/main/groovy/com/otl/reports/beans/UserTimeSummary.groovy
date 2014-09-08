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
	def defaulter=true;
	
	
	def getWorkdays() throws ServiceException{
		
		
		if(workhours)
		return formatAstext(workhours/8)
		else
		return 0
	}
	
	def formatAstext(def num){
		if(num){
			def diff=num - num.round()
			if(diff > 0 || diff < 0)
				return num
			else
				return num.round()+""
					
		}
		return 0
	}
	
	def getLeavedays() throws ServiceException{
		
		
		if(leavehours)	
		return formatAstext(leavehours/8)
		else
		return 0
		
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
