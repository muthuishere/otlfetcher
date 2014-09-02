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
		db.execute("create table if not exists userInfo (user string, password string,ip string)")

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

		}

		DataSet curUserInfo = db.dataSet("userInfo")
		//user string, projectcode string,projecttask string,tasktype string,hours integer,details string,key string

		curUserInfo.add(
				user:userInfo.user,
				password:userInfo.password,
				ip:userInfo.ip

				)

		//curUserInfo.commit();
		//insert into myTable(colname1, colname2) values(?, ?)
		//	def res=db.executeUpdate("insert into userInfo values(?,?,?)",[userInfo.user,userInfo.password,userInfo.ip]);
		//println(res)


	}
}
