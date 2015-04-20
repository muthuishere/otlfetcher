/**
 * 
 */

 package com.otl.scheduler.batch

 import java.text.SimpleDateFormat
import java.util.Date;

import com.otl.reports.beans.TimesheetStatusReport
import com.otl.reports.beans.UserTimeSummary
import com.otl.reports.controller.Configurator
import com.otl.reports.controller.DataManager
import com.otl.reports.controller.JobScheduler
import com.otl.reports.helpers.Log

import groovy.json.JsonSlurper
/**
 * @author hutchuk
 *
 */
class BatchReportGenerator {
	
	public static void main(String[] args) {
		println "Test Parser :"
		def configFileName
		
		configFileName=args[0]
		
		parseconfig(configFileName)
		BatchReportGenerator batchReporter = new BatchReportGenerator()
		def folder = "C:\\Users\\hutchuk\\git\\otlfetcher_balaji\\reports\\weekly\\thisweek"
		//batchReporter.teamWiseCurrentWeekReport("")
		//batchReporter.invalidUserList("")
		
		JobScheduler jc = new JobScheduler()
		
		Log.debug("Hve started new job creator.")
	}
	
	static parseconfig(def configFileName){
		
		// Read the configuration file into a map called "global".
		// This map is shared with all other threads in order to provide
		// a centralised configuration store.
		
		
		
		
		try
		{
			Configurator.globalconfig = new JsonSlurper().parse(new FileReader(configFileName))
		
			Configurator.globalconfig.configuration_file = configFileName
		
			println "Configuration: ${Configurator.globalconfig}"
		}
		catch(Exception e)
		{
			println "Error: Unable to load configuration"
			e.printStackTrace()
			System.exit(1)
		}
		
	}
	
	
	
	public String invalidUserList(String team){
		DataManager dm = new DataManager()
		String strToList = ""
		int i = 0
		dm.init();
		def userList = dm.getUserListByTeam(team)
		Log.error("Printing the user list---------------")
		userList.each{ users ->
			//Log.info(users)
			if((users.locked.toBoolean())){
				if(strToList.trim()!=""){
					strToList = strToList + ";" +users.user
				}else{
					strToList = users.user
				}
				
				Log.info(users.user)
			}
			else{
				
			}
			
			i++
		}
		strToList =  strToList.toLowerCase().replaceAll("@corpuk.net", "")
		Log.error("Total Number of users: " + i + "   " + strToList)
		return strToList
		
	}
	
	public String teamWiseCurrentWeekReport(def team, Date from, int weeks, def reportName){
		
		//Find the start and end date of the week.
		
		//Finding the start date, with the assumption that monday is first day of the week.
		
		
		Date today = new Date()
		Calendar calendar = GregorianCalendar.getInstance(); 
		if(from == null){
			
			calendar.setTime(today)
		}else {
			calendar.setTime(from)
			
		}
		
		if(weeks > 0){
			calendar.add(Calendar.DATE, -7 * weeks)
		}
		
		//Setting start day of the week as Monday.
		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			calendar.add(Calendar.DATE, -1);
		  }
		//calendar.add(Calendar.YEAR, -1)//To be deleted. Only for testing
		from = calendar.getTime()
		
		
		
		
		//Now Calculating the end date of this week
		calendar.add(Calendar.DAY_OF_WEEK, 4)
		
		//calendar.setTime(today)//To be deleted. Only for testing
		def to = calendar.getTime()
		
		Log.info("Fetching the weekly report for the following days: " + from + "---to---" + to)
		
		String reportLocation = teamWiseWeeklyReport(team, from, to,reportName)
		
		return reportLocation
	}
	
	public String teamWiseWeeklyReport(String team,Date from,Date to,def reportName){
		
		UserTimeSummary summary
		StringBuffer strReport = new StringBuffer() 
		//ArrayList<TimesheetStatusReport>  summarylist
		DataManager dm = new DataManager()
		dm.init();
		//def userList = dm.getUserListByTeam(team)
		ArrayList hashmaplist=new ArrayList();
		def userList = dm.getUserListByTeam(team)
		userList.each{curuser->

			def res=dm.getTimesheetEntriesSummary( curuser.user , from,to)
			if(null != res)
				hashmaplist.add(res)
				
				Log.debug(res)
		}
		
	
		if(hashmaplist.size() >0 ){
			hashmaplist.each {summarylist ->
				
				summarylist.each{key,val->
					
				strReport = strReport.append(val.user?.toString()?.toLowerCase().replaceAll("@corpuk.net", "") + "\t " + val.workhours?.toString() + "\t " + val.leavehours?.toString()+ "\t " )
				strReport = strReport.append(val.workdays?.toString() + "\t" + val.leavedays?.toString() + "\t " + val.defaulter?.toString()   + System.getProperty("line.separator"))
				
				}
			}
		}
		
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd_MM")
		Log.debug("Value of the Summary List : " + strReport.toString())
		Log.error("New date format : ------------------"+ DATE_FORMAT.format(from) + "-to-" + DATE_FORMAT.format(to))		
		def folderPath = Configurator.globalconfig.report_folder + "/weekly/"  + DATE_FORMAT.format(from) + "-to-" + DATE_FORMAT.format(to)
		def fileName = reportName + DATE_FORMAT.format(from) + "_to_" + DATE_FORMAT.format(to)+ ".csv"
		createFile( folderPath, fileName)
		
		//Writing the summary into report.
		def report = new File(folderPath + "\\" + fileName)
		
		if(report.isFile()){
			report.append(System.getProperty("line.separator") + "UserName \t Working Hours \t Leave Hours \t  Working Days \t  Leave Days \t IsDefaulter" + System.getProperty("line.separator"))
			report.append(strReport.toString())
			fileName = report.getAbsolutePath().toString()
		}else
		{
			Log.error("Seems the file has not been creted for the following path " + report.absoluteFile)
			fileName = null
			
		}
		//fileName = report.getAbsolutePath().toString()
		 
		return fileName 
	}
	


	public boolean createFile(def folderPath, def fileName){
		
		def fileReport
		if(createFolder(folderPath))
		{
			fileReport = new File( folderPath, fileName ).createNewFile()
		}
		
		return fileReport	
	}

	
	
		
	public boolean createFolder(def folderPath){

		def folderExists = false
		
		def parentReprotFolder = Configurator.globalconfig.report_folder
		
		Log.info("Value of the file path passed is : " + folderPath)
		
		try{

			def folderAbsName = new File( folderPath )
			
			if(!folderAbsName.exists()){
				folderAbsName.mkdirs()
			}
			
			folderExists = folderAbsName.exists()
			
			return folderExists
			
		}catch(Exception e){
		
			Log.error("Error while creating the folder : " + e.printStackTrace())
			return folderExists
			
		}
	} 

}
