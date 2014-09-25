package com.otl.reports.controller

import com.otl.reports.beans.TimeEntry
import com.otl.reports.beans.UserInfo
import com.otl.reports.beans.UserTimeSummary;
import com.otl.reports.model.DataStore

import java.util.ArrayList;
import java.util.TreeMap.Entry

class DataManager {

	DataStore dataStore=null
	String TIMESHEET_TABLE="timesheet"
	String USER_TABLE="users"
	private boolean initialized=false
	
	
	
		
	public void init(){
		
		
			dataStore=new DataStore()
			dataStore.init(Configurator.globalconfig.userdb, Configurator.globalconfig.fetcherdb)
			
		
		
	}
	
	def print(){
		
		dataStore.printData();
	}
	public void addTimeEntries(ArrayList<TimeEntry> timeEntries){
	
		for(TimeEntry timeEntry:timeEntries){
			dataStore.insertTimesheet(timeEntry)
			
		}
	
	}
	
	public void enableUser(String user){
		
		dataStore.updateUserLock(
			new UserInfo(
				user: user, 
				locked: "false" 
				)
			)
	}
	public void disableUser(String user){
		
		dataStore.updateUserLock(
			new UserInfo(
				user: user,
				locked: "true"
				)
			)
	}
	public void addUserEntries(UserInfo userInfo){
	
		dataStore.insertUser(userInfo)
	}
	

	
	
	public ArrayList<UserInfo> getValidUserEntries(){
		
		return dataStore.getValidUserEntries()
		
		}
	
	
	public ArrayList<UserInfo> getUserEntries(){
		
		return dataStore.getUserEntries(null)
		
		}
	
	public ArrayList<UserInfo> getTeamUserEntriesasCSV(String team){
		
		return dataStore.getUserEntries(getvalidString(team))
		
		}
	
	public ArrayList<UserInfo> getUserEntries(String team){
		
		return dataStore.getUserEntries(getvalidString(team))
		
		}
	
	public ArrayList<UserTimeSummary> getAllUserStatus(){
		
		return dataStore.getuserstatusList(null)
		
		}
	
	public ArrayList<TimeEntry> getAllProjects(){
		
		return dataStore.getProjectsList(null)
		
		}
	
	public def executeSQL(def db,def sql){
		
		return dataStore.executeSQL( db, sql)
		
		}
	
	
	public UserInfo findUser(String user){
	
		
		return dataStore.findUser(user)
		
	}
	public boolean deleteUser(String user){
		
			
			return dataStore.deleteUser(user)
			
		}
	
	private String getProjectLeaveCodes(){
		String leavecode=""
		int i=0
		for(def bfr:Configurator.globalconfig.leavecodes){
			
			
			
			if(i > 0)
				leavecode=leavecode +","
			
				
				
				leavecode=leavecode +"'$bfr'"
				
			i++
			}
		return leavecode
		
	}
	
	public getvalidString(def user){
		
		if(null == user || user.trim() =="" || user.trim() =="null")
			return null
			
		return user
	
	}
	public def getTimesheetEntriesSummary(String user,Date from,Date to){
		
	
	
		
			return dataStore.getTimesheetEntriesSummary(getvalidString(user),from,to,getProjectLeaveCodes())
			
			
		}
	
	
	
	public def getProjectHoursReport(String project,Date from,Date to){
		
	
	
		
			return dataStore.getProjectHoursReport(getvalidString(project),from,to)
			
			
		}
	
	
	public def getProjectEmployeeReport(String project,Date from,Date to){
		
	
	
		
			return dataStore.getProjectEmployeeReport(getvalidString(project),from,to)
			
			
		}
	public ArrayList<TimeEntry> getTimesheetEntries(String user,Date from,Date to){
	
		
		
		return dataStore.getTimesheetEntries(getvalidString(user),from,to,getProjectLeaveCodes())
		
		
	}
	
	public ArrayList<TimeEntry> getTimesheetEntries(Date from,Date to){
	
		return getTimesheetEntries(null,from,to)
	}
	public ArrayList<TimeEntry> getTimesheetEntries(String user){
	
		return getTimesheetEntries(user,null,null)
			
	}
}
