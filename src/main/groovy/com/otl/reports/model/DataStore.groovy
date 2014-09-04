package com.otl.reports.model

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentNavigableMap;
import java.sql.*

import org.sqlite.SQLite

import groovy.sql.DataSet
import groovy.sql.Sql

import com.otl.reports.beans.TimeEntry
import com.otl.reports.beans.UserInfo
import com.otl.reports.beans.UserTimeSummary
import com.otl.reports.helpers.Log

class DataStore {


	Sql db =null;

	void close(){
		db.close();
	}


	void init(def fileName){

		Class.forName("org.sqlite.JDBC")

		db = Sql.newInstance("jdbc:sqlite:"+fileName, "org.sqlite.JDBC")
		//create table if not exists TableName (col1 typ1, ..., colN typN)
		/*
		 public String user
		 def password
		 def ip
		 */
		
		
		db.execute("create table if not exists userInfo (user string, password string,ip string,locked string,lastupdated date)")

		/*
		 * Date entryDate
		 def user
		 def projectcode
		 def projecttask
		 def tasktype
		 def hours
		 def details
		 * 
		 */
		db.execute("create table if not exists timeentry (user string, entryDate date,projectcode string,projecttask string,tasktype string,hours integer,details string,key string)")

	}

	public ArrayList<UserInfo> getUserEntries(){


		ArrayList<UserInfo> userEntries=new ArrayList<UserInfo>()

		db.rows("select * from userInfo " ).each{

			userEntries.add(
					new UserInfo(
					user: it.user,
					password: it.password,
					ip: it.ip

					)
					);
		}

		return userEntries
	}

	public UserInfo findUser(String user){

		UserInfo userInfo=null
		String cond=" where 1=1 "
		if(null != user)
			cond=cond + " AND user like '${user}' "
		else
			return null

		

		db.rows("select * from userInfo " + cond ).each{


			userInfo=new UserInfo(
					user: it.user,
					password: it.password,
					ip: it.ip

					)
		}

		return userInfo
	}


	public ArrayList<UserTimeSummary> getuserstatusList(String user){
		ArrayList<UserTimeSummary> userstatuslist=new ArrayList<UserTimeSummary>()
			String cond=" where 1=1 "
				if(null != user)
					cond=cond + " AND user like '${user}' "
					
					db.rows("select user,locked from userInfo " + cond ).each{
						
									userstatuslist.add(
											new UserTimeSummary(											
											user: it.user,
											userLocked: Boolean.parseBoolean(it.locked)										
											)
											);
								}
					
		return userstatuslist
		
	}
	public def getTimesheetEntriesSummary(String user,Date from,Date to,def leavecodes){
		
		def timeEntries=new HashMap<String,UserTimeSummary>()
		
		//		ArrayList<UserTimeSummary> timeEntries=new ArrayList<UserTimeSummary>()
		
				String cond=" where 1=1 "
				if(null != user)
					cond=cond + " AND user like '${user}' "
		
		
				if(null != from )
					cond=cond + " AND entryDate >= "+ from.getTime()
		
				if(null != to)
					cond=cond + " AND entryDate <= "+ to.getTime()

					
					String maxdateQuery="Select user,max(entryDate) from timentries ${cond}  group by user "
					
					
					String workhrsQuery="Select user,total(hours) as totalhrs from timentries ${cond} and projectcode not in($leavecodes)  group by user "
					
					String leavehrsQuery="Select user,total(hours) as totalhrs from timentries ${cond} and projectcode  in($leavecodes)  group by user "
					
					
					
					//Select user,max(entryDate)from timentries group by user 
		
					// group by user where projectcode not in[leavecodes]
					
					//Select user,total(hours) as totalhrs from timentries group by user where projectcode in[ leavecodes]
					
					
					//Merge and Add entries
					//user, workinghours leavehours,lastupdated entry
		
				db.rows(maxdateQuery).each{
		
					timeEntries.put(it.user, new TimeEntry(
						user: it.user,
						lastupdated:new Date(it.entryDate)
						))
					
				}
		
				db.rows(workhrsQuery).each{
				
						timeEntries.get(it.user).workhours=it.hours
					
						}
					
				db.rows(leavehrsQuery).each{
					
							timeEntries.get(it.user).leavehours=it.hours
						
							}
				
				getuserstatusList( user).each{
					
							timeEntries.get(it.user).userLocked=it.userLocked
							
						
							}
				return timeEntries
			}
	
	
		//return dataStore.getUserEntries()
	//return dataStore.findUser(user)

	public ArrayList<TimeEntry> getTimesheetEntries(String user,Date from,Date to){

		ArrayList<TimeEntry> timeEntries=new ArrayList<TimeEntry>()

		String cond=" where 1=1 "
		if(null != user)
			cond=cond + " AND user like '${user}' "


		if(null != from )
			cond=cond + " AND entryDate >= "+ from.getTime()

		if(null != to)
			cond=cond + " AND entryDate <= "+ to.getTime()



		db.rows("select * from timeentry " + cond).each{

			timeEntries.add(
					new TimeEntry(
					entryDate: new Date(it.entryDate),
					hours: it.hours,
					user: it.user,
					projectcode: it.projectcode,
					projecttask: it.projecttask,
					tasktype: it.tasktype,
					details: it.details,
					)
					);
		}

		return timeEntries
	}
	void insertTimesheet(TimeEntry timeEntry){


		boolean exists =false

		def query="select key from timeentry  where key='" +timeEntry.key +"'"

		exists=(db.rows(query).size()>0)




		if( exists){
			Log.error("Key Already exists ${timeEntry.key} overWriting ");

			query="delete from timeentry  where key='${timeEntry.key}'"
			db.execute(query, []);

		}

		DataSet curtimeEntry = db.dataSet("timeentry")
		//user string, projectcode string,projecttask string,tasktype string,hours integer,details string,key string
		//	curtimeEntry.
		curtimeEntry.add(
				user:timeEntry.user,
				projectcode:timeEntry.projectcode,
				projecttask:timeEntry.projecttask,
				tasktype:timeEntry.tasktype,
				hours:timeEntry.hours,
				details:timeEntry.details,
				key:timeEntry.key,
				entryDate:timeEntry.entryDate
				)




	}

	void printData(){

		println(db.rows("select * from userInfo").size())
		db.rows("select * from userInfo").each{ println(it) }


		println(db.rows("select * from timeentry").size())

		db.rows("select * from timeentry").each{ println(it) }


	}
	
	
	
	void insertUser(UserInfo userInfo){



		boolean exists =false



		def query="select user from userInfo  where user='" + userInfo.user + "'"

		exists=(db.rows(query).size()>0)

		// boolean exists = db.execute("select user from userInfo  where user='${userInfo.user}'", null);


		if( exists){

			Log.error("Key Already exists ${userInfo.user} overWriting ");
			query="delete from userInfo  where user='${userInfo.user}'"
			db.executeUpdate(query, []);
			
			return

		}

		DataSet curUserInfo = db.dataSet("userInfo")
		//user string, projectcode string,projecttask string,tasktype string,hours integer,details string,key string

		curUserInfo.add(
				user:userInfo.user,
				password:userInfo.password,
				ip:userInfo.ip,
				locked:"false",
				lastupdated:new Date()

				)

		//curUserInfo.commit();
		//insert into myTable(colname1, colname2) values(?, ?)
		//	def res=db.executeUpdate("insert into userInfo values(?,?,?)",[userInfo.user,userInfo.password,userInfo.ip]);
		//println(res)


	}
}
