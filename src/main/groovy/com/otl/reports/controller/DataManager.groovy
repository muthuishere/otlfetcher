package com.otl.reports.controller

import com.otl.reports.beans.TimeEntry
import com.otl.reports.beans.UserInfo
import com.otl.reports.model.DataStore

import java.util.TreeMap.Entry

class DataManager {

	DataStore dataStore=null
	String TIMESHEET_TABLE="timesheet"
	String USER_TABLE="users"
	private boolean initialized=false
	
	
	
		
	public void init(){
		
		if(!initialized){
			dataStore=new DataStore()
			dataStore.init(Configurator.dbname )
			initialized=true
		
		}
	}
	
	def print(){
		
		dataStore.printData();
	}
	public void addTimeEntries(ArrayList<TimeEntry> timeEntries){
	
		for(TimeEntry timeEntry:timeEntries){
			dataStore.insertTimesheet(timeEntry)
			
		}
	
	}
	public void addUserEntries(UserInfo userInfo){
	
		dataStore.insertUser(userInfo)
	}
	

	
	
	public ArrayList<UserInfo> getUserEntries(){
		
		return dataStore.getUserEntries()
		
		}
	
	
	public UserInfo findUser(String user){
	
		
		return dataStore.findUser(user)
		
	}
	
	public ArrayList<TimeEntry> getTimesheetEntries(String user,Date from,Date to){
	
		
		
		return dataStore.getTimesheetEntries(user,from,to)
		
		
	}
	
	public ArrayList<TimeEntry> getTimesheetEntries(Date from,Date to){
	
		return getTimesheetEntries(null,from,to)
	}
	public ArrayList<TimeEntry> getTimesheetEntries(String user){
	
		return getTimesheetEntries(user,null,null)
			
	}
}
