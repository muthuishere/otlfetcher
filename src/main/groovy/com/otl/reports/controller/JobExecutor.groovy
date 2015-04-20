/**
 * 
 */
package com.otl.reports.controller
import java.lang.reflect.Method
import java.util.ArrayList;

import com.otl.reports.beans.JobDescription
import com.otl.reports.beans.JobInstances;
import com.otl.reports.beans.MailTemplate;
import com.otl.reports.beans.UserInfo
import com.otl.reports.helpers.Log
import com.otl.scheduler.batch.BatchReportGenerator
import com.otl.scheduler.mailer.OutlookLightMode;
/**
 * @author hutchuk
 *
 */
class JobExecutor {

	//NOw created a thread to read the jobinstance database
	//From there populate the respective LBQ based on the frequency
	//Now listen from each of these LBQ and call the implementation of each based on the frequency
	//Each of the frequency specific implementation should be calling the BatchReportGenerator
	//May be you can have a separate mailer job which again does the similar thing of populating the LBQ and sending mails
		//Should think of adding status of mailer to each of the jobInstance record life cycle
	
	//The general design for this is to implement the observer pattern. The steps would be of two fold and by using queue like data structure as a interim storage.
		//First run few background threads which constantly listens to the queue and process the data request available in the queue
		//Second populate the queue with data request
		//These two combination will allow us to handle the request asynchronously and scale horizontally
	
	DataManager dataManager
	
	JobExecutor(){
		dataManager = new DataManager()
		dataManager.init()
		//Initializing threads in background for adhoc job processing
		(1 ..Configurator.globalconfig.adhoc_worker_thread_count).each {
			Thread.start{adhoc_worker_thread(Configurator,dataManager)}
		}
		
		//Initializing threads in background for hourly job processing
		(1 ..Configurator.globalconfig.hourly_worker_thread_count).each {
			Thread.start{hourly_worker_thread(Configurator,dataManager)}
		}
		
		//Initializing threads in background for daily job processing
		(1 ..Configurator.globalconfig.daily_worker_thread_count).each {
			Thread.start{daily_worker_thread(Configurator,dataManager)}
		}
		
		
		//Initializing threads in background for weekly job processing
		(1 ..Configurator.globalconfig.weekly_worker_thread_count).each {
			Thread.start{weekly_worker_thread(Configurator,dataManager)}
		}
	}
	
	//TODO: Based on the frequency the jobs will be distributed into the queues 
	//TODO: Based on the report name, the execution function will change. This implementation should be done in each of the respective frequency processor or should be called from there
	
	def tasks_creator(){
		
		BatchReportGenerator batchReporter = new BatchReportGenerator()
		
		ArrayList<JobDescription> jobDefList
		int jobFetchWindow = 2 //Specified in days. This can be later taken as a configuration parameter
		MailTemplate mailTemplate = new MailTemplate()
		Date today = new Date()
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(today)
		calendar.add(Calendar.DATE, jobFetchWindow)
		Date cuttofftime = calendar.getTime()
		int loginFlag = 0
		OutlookLightMode outlook = new OutlookLightMode()
		Log.error("jobFetchWindow : " + cuttofftime.time + "  " + calendar.getTime())
		String strQuery = ""
		strQuery = strQuery + " jobinstrevisedstarttime < '${cuttofftime}'"
		ArrayList<JobInstances> jobInstanceList = dataManager.getJobInstances(strQuery)
		String strJobInstStatus = ""
		String strJobFrequency = ""
		int fetchDBWindow = 0 //Defaulting to current week
		if(jobInstanceList.size > 0)
		{
			jobInstanceList.each { jobInst ->
				
				strJobInstStatus = jobInst.jobInstResult?.trim()
				strJobFrequency = jobInst.jobInstFrequency?.trim()
				
					if(strJobInstStatus == "new")
					{						
						
						
							if(jobInst.jobInstTaskName == "fetchdb"){
							
								if(Configurator.isUpdating){
									Log.info("FetchDB job is being run in the background and hence skipping this record : " + jobInst.jobInstID )
								}else{
								
									if(Configurator.globalconfig.fetchdb_weeks != null){
										fetchDBWindow = Configurator.globalconfig.fetchdb_weeks
									}
									
									Date fromFetchDB = new Date()
									Calendar calendarFetchDB = GregorianCalendar.getInstance();
									calendarFetchDB.setTime(fromFetchDB)
									
									
									if(fetchDBWindow > 0){
										calendarFetchDB.add(Calendar.DATE, -7 * fetchDBWindow)
									}
									
									//Setting start day of the week as Monday.
									while (calendarFetchDB.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
										calendarFetchDB.add(Calendar.DATE, -1);
									  }
									//calendar.add(Calendar.YEAR, -1)//To be deleted. Only for testing
									fromFetchDB = calendarFetchDB.getTime()
									
									//Now Calculating the end date of this week
									calendarFetchDB.add(Calendar.DAY_OF_WEEK, 4 + (7 * fetchDBWindow))
																	
									def toFetchDB = calendarFetchDB.getTime()
									String userlist
									def curuserlist=dataManager.getValidUserEntries()
									
									
									int i=0;
									curuserlist.each{curuserInfo->
										if(i != 0)
											userlist= "," + userlist
				
										userlist=userlist+ curuserInfo.user
										i++
									}
									
										new DbUpdater().start(userlist,fromFetchDB ,toFetchDB)
									
										jobInst.jobInstResult = "finished"
										dataManager.updateJobInstance(jobInst)
						
								
								}
									
							}else{
									Log.debug("This new job :" + jobInst.jobInstID + " will be sent for processing again")
									//Placing the jobs in queue for asynchronous processing
									insertJobsInQueue(jobInst,strJobFrequency)
									//Updating the status of the JobInstance
									jobInst.jobInstResult = "inprogress" 
									dataManager.updateJobInstance(jobInst)
									//batchReporter.invalidUserList("")
									//Checking the frequency of the job and placing it in the queue.
									//if()
									//hourly_jobs_worker_lbq
							}
					}
					else if(strJobInstStatus == "inprogress")
					{
						//batchReporter.teamWiseCurrentWeekReport()
						Log.debug("Job request is already in the queue for the following job id: " + jobInst.jobInstID)
							
					}else if(strJobInstStatus == "mailer")
					{
						//TODO -- Change with the mail sender login - add logout logic
						//TODO: Make this login stage by stage 
						//TODO: Handle the exceptions in mailer
						//batchReporter.teamWiseCurrentWeekReport()
						try {
													
							loginFlag = loginFlag++
							Log.debug("Job request is processed, report needs to be sent in mail : " + jobInst.jobParentID)
							
							jobDefList = dataManager.getJobDescription(" jobid = '${jobInst.jobParentID}'")
							Log.error("Value of the jobDeflist is :::::::::::::::::::::: " + jobDefList.size())
							jobDefList.each{ 
							
								Log.debug("Value of the jobdescription in the mailer column is :::::::::::::::::  " + it.toString())	
									
								
							mailTemplate.toAddress =  it.jobToAddress.toString().trim() //"balaji.subramanian@three.co.uk"
							mailTemplate.ccAddress =  it.jobCCAddress.toString().trim()
							mailTemplate.bccAddress = it.jobBCCAddress.toString().trim()
							mailTemplate.mailContent= it.jobMailContent.toString().trim()
							mailTemplate.subject	= it.jobSubject.toString().trim()
							
							}
							
							mailTemplate.attachmentLocation = jobInst.jobInstReportLocation
							mailTemplate.userName = Configurator.globalconfig.outlook_username
							mailTemplate.password = Configurator.globalconfig.outlook_password
							outlook.init(Configurator.globalconfig?.proxy)
							Log.error("Value of the mailTempalte is : " + mailTemplate )
							
							if(loginFlag < 1) {
								UserInfo userInfo = new UserInfo()
								userInfo.setUser(Configurator.globalconfig.outlook_username)
								userInfo.setPassword(Configurator.globalconfig.outlook_password)
								outlook.loginMail(userInfo)
							}
							
							outlook.sendMail(mailTemplate)
							//outlook.logout(userInfo)
							jobInst.jobInstResult = "finished"
							dataManager.updateJobInstance(jobInst)
						}catch(Exception e)
						{
							jobInst.jobInstResult = "failed"
							dataManager.updateJobInstance(jobInst)
							Log.error("Exception while sending mail : " + e.printStackTrace())
							
						}
							
					}else if(strJobInstStatus == "failed")
					{
						//Check the number of retry exceeded the limit
						if(jobInst.jobInstRetry > 0){
							//Placing the jobs in queue for asynchronous processing
							insertJobsInQueue(jobInst,strJobFrequency)
							//Updating the status and decreasing the retry limit
							jobInst.jobInstResult = "new"
							jobInst.jobInstRetry = jobInst.jobInstRetry - 1 //Reducing the retry limit
							dataManager.updateJobInstance(jobInst)
						}
						Log.debug("This failed job :" + jobInst.jobInstID + " will be sent to queue for processing again")
							
					}else if(strJobInstStatus == "finished")
					{
						Log.debug("Skipping the processing of  the following job id " + jobInst.jobInstID  + " as status found : " + strJobInstStatus)
						
					}else{
					
						Log.debug("Invalid status " + strJobInstStatus + " found for the following job id : " + jobInst.jobInstID)
					
					}
				
				
				
				
			}	
		}
		else{
			Log.info("No matching job instance has been found for the query. Probaly all the jobs has been processed")
		}
		
		
	}
	
	boolean insertJobsInQueue(JobInstances jobInstance, String strFrequency){
		
		//response object to fill the outcome of job processing
		def taskname
		boolean queueInsertStatus = false
		
			taskname = jobInstance.jobInstTaskName
			
			if(strFrequency == "adhoc"){
				
				Configurator.adhoc_jobs_worker_lbq.put(["strFrequency" : strFrequency,  "jobInstance":jobInstance,  "taskname":taskname ])
				queueInsertStatus = true
				
				
				
			}else if(strFrequency == "hourly"){
				
				Configurator.hourly_jobs_worker_lbq.put(["strFrequency" : strFrequency,  "jobInstance":jobInstance,  "taskname":taskname ])
				queueInsertStatus = true
				
			} else if(strFrequency == "daily"){
						
				Configurator.daily_jobs_worker_lbq.put(["strFrequency" : strFrequency,  "jobInstance":jobInstance,  "taskname":taskname ])
				queueInsertStatus = true
				
			}else if(strFrequency == "weekly"){
				
				Configurator.weekly_jobs_worker_lbq.put(["strFrequency" : strFrequency,  "jobInstance":jobInstance,  "taskname":taskname ])
				queueInsertStatus = true
			
			}else{
			
				Log.error("Frequency type not found, hence skipping this job : " + jobInstance.jobInstID)
			
			}
		
		
		return queueInsertStatus
	}
	
	
	
	/**
	 * Function to process the Adhoc type job instances
	 * @param configurator
	 * @param dataManager
	 * @return
	 */
	def adhoc_worker_thread(def configurator,DataManager dataManager){
		
		//JobExecutor jobExec = new JobExecutor()
		//Here execute the jobs for adhoc jobs
		BatchReportGenerator batchReportGenerator = new BatchReportGenerator()
		ArrayList<JobDescription> jobDefList
		int loginFlag = 0
		OutlookLightMode outlook = new OutlookLightMode()
		String strTaskName = ""
		String batchReportName = ""
		String strJobDescQuery = ""
		MailTemplate mailTemplate = new MailTemplate()
		String strToAddressList = ""
		ClassLoader classLoader = JobExecutor.class.getClassLoader();
		String methodName = "teamWiseCurrentWeekReport"
		Method method
		Class taskClass
			try {
				taskClass = classLoader.loadClass("com.otl.scheduler.batch.BatchReportGenerator");
				Log.error("********************** " + taskClass.getName())
				def team = ""

				def newObj = taskClass.newInstance()
				
				Log.debug("Value of the new object created is :   " + newObj)
				int paramsCount = 0
				paramsCount = 2
				
				Class[] params = new Class[paramsCount]
				
				params[0] = java.lang.Object.class
				params[1] = java.lang.Object.class
				
				//method = newObj.class.getMethod(methodName,params)
				//method.invoke(newObj, "Selfcare")
				
				Object[] paramsValue = new Object[paramsCount]
				paramsValue[0] = "Selfcare"
				paramsValue[1] = "reportName"
				
				//method.invoke(newObj,paramsValue)
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		//TOT: Need to check y there is new folders created adn all the jobs are repeated again	
		while(true)
		{
			def req_msg = Configurator.adhoc_jobs_worker_lbq.take()
			def reportWeeks = Configurator.globalconfig.report_weeks
			def reply_msg = req_msg
			def start_ms = System.currentTimeMillis()
			JobInstances jobInstance
			String response = null
			//ArrayList<TimeEntry> timeEntries=new ArrayList<TimeEntry>()
			try
			{
				jobInstance = req_msg.jobInstance
				Log.debug("Job Name taken from queue is" + req_msg)
				strTaskName = req_msg.taskname
				//Code to process the request goes here, actual processing of the batch job starts here.
				if(strTaskName == "weeklyreport"){
					
					Date today = new Date()
					
					batchReportName = batchReportGenerator.teamWiseCurrentWeekReport("", today, reportWeeks, jobInstance.jobInstReportName)
					
					
					jobInstance.jobInstReportLocation = batchReportName
					jobInstance.jobInstResult = "mailer"
					dataManager.updateJobInstance(jobInstance)
					
					Log.debug("Value of the newly generated report location is : " + batchReportName)
					
				}else if(strTaskName == "uservalidity") {
				
					try{
						
						
						strToAddressList = batchReportGenerator.invalidUserList("")
						
						
						jobDefList = dataManager.getJobDescription(" jobid = '${jobInstance.jobParentID}'")
						Log.error("Value of the jobDeflist is :::::::::::::::::::::: " + jobDefList.size())
						jobDefList.each{
						
							Log.debug("Value of the jobdescription in the mailer column is :::::::::::::::::  " + it.toString())
								
						//mailTemplate.toAddress = strToAddressList //To be enabled when put in production, commented for testing.
						mailTemplate.toAddress =  it.jobToAddress.toString().trim() //"balaji.subramanian@three.co.uk"
						mailTemplate.ccAddress =  it.jobCCAddress.toString().trim()
						mailTemplate.bccAddress = it.jobBCCAddress.toString().trim()
						mailTemplate.mailContent= it.jobMailContent.toString().trim()
						mailTemplate.subject	= it.jobSubject.toString().trim()
						
						}
						//There is no mail attachement required for this .
						//mailTemplate.attachmentLocation = jobInstance.jobInstReportLocation
						mailTemplate.userName = Configurator.globalconfig.outlook_username
						mailTemplate.password = Configurator.globalconfig.outlook_password
						outlook.init(Configurator.globalconfig?.proxy)
						Log.error("Value of the mailTempalte is : " + mailTemplate )
						
						if(loginFlag < 1) {
							UserInfo userInfo = new UserInfo()
							userInfo.setUser(Configurator.globalconfig.outlook_username)
							userInfo.setPassword(Configurator.globalconfig.outlook_password)
							outlook.loginMail(userInfo)
						}
						
						if(outlook.sendMail(mailTemplate))
						{	
							//outlook.logout(userInfo)
							jobInstance.jobInstResult = "finished"
						}else{
							jobInstance.jobInstResult = "failed"
						}
						
						dataManager.updateJobInstance(jobInstance)
				}catch(Exception e)
				{
					jobInstance.jobInstResult = "failed"
					dataManager.updateJobInstance(jobInstance)
					Log.error("Exception while sending mail : " + e.printStackTrace())
					
				}
					
					
					
					Log.info("----------------------------- invalid user list : " + strToAddressList)	
				
				}else if(strTaskName == "mailer") {
				
					strJobDescQuery = " jobid = '${ req_msg.jobInstance.jobInstID}'" 
					Log.debug("((((((((((((((((((((((((((((((((((((((((((" + dataManager.getJobDescription(strJobDescQuery) )
					
				}
				else
				{
					Log.error("Unknown task type, new implemntation needs to be added : ")
				}
				

				
				def took = System.currentTimeMillis() - start_ms
				
				
			}
			catch(Exception e)
			{
				jobInstance.jobInstResult = "failed"
				dataManager.updateJobInstance(jobInstance)
				e.printStackTrace()

				reply_msg.error = "${e.getCause().toString()} (${e.getMessage()})"

			}
		
		}
	}
	
	/**
	 * Function to execute the job request related to hourly jobs
	 * @param configurator
	 * @param dataManager
	 * @return
	 */
	def hourly_worker_thread(def configurator,DataManager dataManager){
		
	}
	
	
	/**
	 * Function to execute the job request related to daily jobs
	 * @param configurator
	 * @param dataManager
	 * @return
	 */
	def daily_worker_thread(def configurator,DataManager dataManager){
		
	}
	
	
	/**
	 * Function to execute the job request related to weekly jobs
	 * @param configurator
	 * @param dataManager
	 * @return
	 */
	def weekly_worker_thread(def configurator,DataManager dataManager){
		
	}
	
	
	/**
	 * Function to execute the job request related to automated mails
	 * @param configurator
	 * @param dataManager
	 * @return
	 */
	def mailer_worker_thread(def configurator,DataManager dataManager){
		
	}
	
	
	
	
	
}
